package com.example.order_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.dto.cache.CachedProduct;
import com.example.order_service.dto.request.OrderListRequest;
import com.example.order_service.dto.response.OrderDetail;
import com.example.order_service.dto.response.OrderItemDetail;
import com.example.order_service.dto.response.OrderItemDetail.OrderedProductInfo;
import com.example.order_service.dto.response.OrderSummary;
import com.example.order_service.entity.OrderItems;
import com.example.order_service.entity.Orders;
import com.example.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final CacheService cacheService;
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
        List<OrderSummary> orderSummaries = new ArrayList<>(); // 응답 dto

        // 1.주문 db에서 유저 주문 목록 조회
        List<Orders> orders = orderRepository.findOrdersByRequest(request); 
        for (Orders order : orders) {
            OrderSummary orderSummary = new OrderSummary();
            orderSummary = objectMapper.convertValue(order, OrderSummary.class); // TODO: 변환 방법 비교해보기
            // System.out.println("ObjectMapper: " + orderSummary);
            // BeanUtils.copyProperties(order, orderSummary);
            // System.out.println("BeanUtils: " + orderSummary);

            // 2.주문 상품 별 상품 정보 세팅
            List<OrderItemDetail> orderItemDetails = new ArrayList<>();
            for(OrderItems orderItem: order.getOrderItems()){
                OrderItemDetail orderItemDetail = objectMapper.convertValue(orderItem, OrderItemDetail.class);

                CachedProduct cachedProduct = getCachedProduct(orderItem.getProductId());
                OrderItemDetail.OrderedProductInfo orderedProductInfo = findOrderdProductInfo(cachedProduct, orderItem.getColorId(), orderItem.getSizeId());
                orderedProductInfo.setPrice(orderItem.getPrice()); // 가격은 결제 당시 가격 표기
                orderItemDetail.setOrderedProductInfo(orderedProductInfo);
                orderItemDetails.add(orderItemDetail);
            }
            orderSummary.setOrderItemDetails(orderItemDetails);
            orderSummaries.add(orderSummary);
        }
        return orderSummaries;
    }

    // 상품 서비스 API 호출 (FeignClient 사용)
    private CachedProduct getCachedProduct(int productId) {
        CachedProduct cachedProduct= new CachedProduct();
        // 1. 캐시에서 상품 정보 조회
        // cachedProduct = cacheService.getOrderedProductInfo(productId);
        // 2. 없으면 Product 서비스 API에 요청 (product 서비스에서 캐시에 저장.)
        // if(cachedProduct == null) {
            cachedProduct = productClient.getProductForCache(productId);
        // }
        return cachedProduct;
    }
    
    // 캐시 데이터를 OrderedProductInfo로 변환
    private OrderedProductInfo findOrderdProductInfo(CachedProduct cachedProduct, int colorId, int sizeId) {
        // 색상 & 사이즈 정보 찾기
        String colorName = cachedProduct.getAvailableColors().get(colorId);
        String sizeName = cachedProduct.getAvailableSizes().get(sizeId);

        // OrderedProductInfo 생성 및 반환
        OrderedProductInfo orderedProductInfo = new OrderedProductInfo();
        orderedProductInfo.setProductId(cachedProduct.getProductId());
        orderedProductInfo.setProductName(cachedProduct.getProductName());
        orderedProductInfo.setColorId(colorId);
        orderedProductInfo.setColorName(colorName);
        orderedProductInfo.setSizeId(sizeId);
        orderedProductInfo.setSizeName(sizeName);
        // orderedProductInfo.setPrice(cachedProduct.getPrice());

        return orderedProductInfo;
    }


    // 판매자 주문 목록 조회
    public List<OrderSummary> getSellerOrders(OrderListRequest request) {
        // List<Orders> orders = orderRepository.findAll();
        List<OrderSummary> orderSummaries = new ArrayList<>();        
        
        return orderSummaries;
    }
}
