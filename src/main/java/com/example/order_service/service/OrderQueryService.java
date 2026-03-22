package com.example.order_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.request.OrderListRequest;
import com.example.order_service.dto.response.OrderDetail;
import com.example.order_service.dto.response.OrderItemDetail;
import com.example.order_service.dto.response.OrderItemDetail.OrderedProductInfo;
import com.example.order_service.dto.response.OrderSummary;
import com.example.order_service.dto.response.ProductResponse;
import com.example.order_service.entity.OrderItems;
import com.example.order_service.entity.Orders;
import com.example.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderQueryService {
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final ProductClient productClient;

    // 주문 상세 조회 (주문 + 상품 + 배송 + 결제 정보)
    public OrderDetail getOrderDetail(int orderId) {
        OrderDetail orderDetail = new OrderDetail();
        // 캐시에서 조회
        // orderDetail = cacheService.getOrderDetail(orderId);
        if (orderDetail != null) {
            return orderDetail;  // 캐시된 데이터 반환
        }
        // 캐시에 없으면 DB에서 조회 (각 서비스 api로 요청)
        Orders orders = orderRepository.findByOrderId(orderId).orElse(null);
        //     Delivery delivery = deliveryRepository.findByOrderId(orderId);
        //     Payment payment = paymentRepository.findByOrderId(orderId);
        //     Product product = productRepository.findByOrderId(orderId);
        //     orderDetails.add(new OrderDetail(order, payment, product, delivery));

        return orderDetail;
    }

    // 고객 주문 목록 조회 (주문 + 상품정보)
    public List<OrderSummary> getCustomerOrders(OrderListRequest request) {
        long startTime = System.currentTimeMillis();

        List<OrderSummary> orderSummaries = new ArrayList<>(); // 응답 dto

        // 1) 주문 페이징 조회
        int page = Math.max(request.getPage(), 1);
        int pageSize = Math.min(Math.max(request.getPageSize(), 1), 100); // 안전장치: 1~100
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Orders> orderPage = orderRepository.findOrdersByRequest(request, pageable);
        List<Orders> orders = orderPage.getContent();

        // 2) 주문 페이지 내 필요한 productId 수집 (중복 제거 + 순서 유지)
        Set<Integer> productIds = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItems::getProductId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // 3) 배치 호출을 chunkSize로 끊어서 수행 (URL 길이/부하 제한)
        final int productBatchSize = 100;
        Map<Integer, ProductResponse> reqProductCache = new HashMap<>(); // 요청 단위(in-memory) 캐시
        int batchCalls = 0;
        int missingProductCount = 0; // 누락 건수

        if (!productIds.isEmpty()) {
            List<Integer> productIdList = new ArrayList<>(productIds);
            for (int i = 0; i < productIdList.size(); i += productBatchSize) {
                int end = Math.min(i + productBatchSize, productIdList.size());
                List<Integer> chunk = productIdList.subList(i, end);
                List<ProductResponse> products = productClient.getProductList(chunk);
                for (ProductResponse p : products) {
                    if (p != null) {
                        reqProductCache.put(p.getProductId(), p);
                    }
                }
                batchCalls++;
            }
        }

        // 4) 주문 상품 별 상품 정보 세팅
        for (Orders order : orders) {
            OrderSummary orderSummary = new OrderSummary();
            orderSummary = objectMapper.convertValue(order, OrderSummary.class); // TODO: 변환 방법 비교해보기
            // System.out.println("ObjectMapper: " + orderSummary);
            // BeanUtils.copyProperties(order, orderSummary);
            // System.out.println("BeanUtils: " + orderSummary);
            
            List<OrderItemDetail> orderItemDetails = new ArrayList<>();
            for (OrderItems orderItem : order.getOrderItems()) {
                OrderItemDetail orderItemDetail = objectMapper.convertValue(orderItem, OrderItemDetail.class);
                orderItemDetail.setOrderId(order.getOrderId());

                int productId = orderItem.getProductId();
                ProductResponse productResponse = reqProductCache.get(productId);
                if (productResponse == null) {
                    // 배치 응답에 없는 상품은 "없는 상품" fallback 객체로 처리 (단건 재호출 생략)
                    productResponse = createMissingProductResponse(productId);
                    reqProductCache.put(productId, productResponse);
                    missingProductCount++;
                }

                OrderItemDetail.OrderedProductInfo orderedProductInfo =
                        mapToOrderedProductInfo(productResponse, orderItem);
                orderedProductInfo.setPrice(orderItem.getPrice()); // 가격은 결제 당시 가격 표기
                orderItemDetail.setOrderedProductInfo(orderedProductInfo);
                orderItemDetails.add(orderItemDetail);
            }
            orderSummary.setOrderItemDetails(orderItemDetails);
            orderSummaries.add(orderSummary);
        }

        long endTime = System.currentTimeMillis();
        long elapsedMs = endTime - startTime;

        log.info(
                "[getCustomerOrders] userId={}, page={}/{}, ordersCount={}, distinctProductCount={}, batchCalls={}, repairCalls={}, elapsedMs={}",
                request.getUserId(),
                page,
                orderPage.getTotalPages(),
                orders.size(),
                reqProductCache.size(),
                batchCalls,
                missingProductCount,
                elapsedMs
        );

        return orderSummaries;
    }

    // 단건 조회: 상품 서비스 API 호출 (FeignClient, fallback) - 미사용.
    private ProductResponse getProduct(int productId) {
        ProductResponse productResponse = new ProductResponse();
        // product-service에서 생성한 캐시는 order-service에서 직접 조회x. 무조건 API 요청.
        productResponse = productClient.getProduct(productId);
        return productResponse;
    }

    private ProductResponse createMissingProductResponse(int productId) {
        ProductResponse fallback = new ProductResponse();
        fallback.setProductId(productId);
        fallback.setProductName("상품 정보를 찾을 수 없습니다.");
        fallback.setAvailableOptions(Collections.emptyMap());
        fallback.setFallback(true);
        return fallback;
    }
    
    private OrderedProductInfo mapToOrderedProductInfo(
            ProductResponse productResponse,
            OrderItems orderItem) {
        OrderedProductInfo orderedProductInfo = new OrderedProductInfo();
        orderedProductInfo.setProductId(productResponse.getProductId());
        orderedProductInfo.setProductName(productResponse.getProductName());
        orderedProductInfo.setVariantId(orderItem.getVariantId() != null ? orderItem.getVariantId() : 0);
        Map<String, String> labels = new LinkedHashMap<>();
        if (orderItem.getOptionLabelsSnapshot() != null) {
            labels.putAll(orderItem.getOptionLabelsSnapshot());
        }
        orderedProductInfo.setOptionLabels(labels);
        return orderedProductInfo;
    }

    // 판매자 주문 목록 조회
    public List<OrderSummary> getSellerOrders(OrderListRequest request) {
        // List<Orders> orders = orderRepository.findAll();
        List<OrderSummary> orderSummaries = new ArrayList<>();
        
        return orderSummaries;
    }

    // 인기 상품 조회
    public List<Integer> getTopSellingProductIds(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Integer> popularItems = orderRepository.findTopSellingProducts(startDate, endDate, pageable);
        
        return popularItems;
    }
}
