package com.example.order_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.order_service.dto.request.OrderListRequest;
import com.example.order_service.dto.response.OrderDetail;
import com.example.order_service.dto.response.OrderItemDetail;
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
    private ObjectMapper objectMapper;

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
        Integer userId = request.getUserId();
        List<OrderSummary> orderSummaries = new ArrayList<>(); // 응답 dto

        // 1.주문 db에서 유저 주문 목록 조회
        List<Orders> orders = orderRepository.findByUserId(userId); 
        for (Orders order : orders) {
            OrderSummary orderSummary = new OrderSummary();
            orderSummary = objectMapper.convertValue(order, OrderSummary.class);
            BeanUtils.copyProperties(order, orderSummary); // TODO: 변환 방법 이거랑 비교해보기

            // 2.주문 상품 별 상품 정보 세팅
            List<OrderItemDetail> orderItemDetails = new ArrayList<>();
            // for(OrderItems orderItem: order.getOrderItems()){
            //     OrderItemDetail orderItemDetail = objectMapper.convertValue(orderItem, OrderItemDetail.class);
            //     // 2-1. 캐시에서 상품 정보 조회
            //     OrderItemDetail.OrderedProductInfo orderedProductInfo = cacheService.getOrderedProductInfo(orderItemDetail.getProductId());
            //     // 2-2. 없으면 Product 서비스 API에 요청 (product 서비스에서 캐시에 저장.)
            //     if(orderedProductInfo == null) {
            //         // Product product = productRepository.findByOrderId(order.getId()); // product 서비스 API Call
            //         // orderedProductInfo = objectMapper.convertValue(product, OrderItemDetail.OrderedProductInfo.class);
            //     }

            //     orderItemDetail.setOrderedProductInfo(orderedProductInfo);
            //     orderItemDetails.add(orderItemDetail);
            // }
            orderSummary.setOrderItemDetails(orderItemDetails);
            orderSummaries.add(orderSummary);
        }        
        return orderSummaries;
    }

    // 판매자 주문 목록 조회
    public List<OrderSummary> getSellerOrders(OrderListRequest request) {
        // List<Orders> orders = orderRepository.findAll();
        List<OrderSummary> orderSummaries = new ArrayList<>();        
        
        return orderSummaries;
    }
}
