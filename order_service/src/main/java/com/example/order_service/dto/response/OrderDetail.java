package com.example.order_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.order_service.dto.cache.CachedDelivery;
import com.example.order_service.dto.cache.CachedPayment;
import com.example.order_service.entity.OrderStatus;

import lombok.Data;

@Data
public class OrderDetail { // 조회할 때 합성하여 반환
    // order 테이블에 있는 정보보
    private int orderId;
    private int userId;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private String paymentMethod;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 캐시 합침
    private List<OrderItemDetail> orderItemDetails; // 주문 상품 목록 (include ProductDetail)
    private CachedDelivery delivery; // 배송 정보
    private CachedPayment payment; // 결제 정보
}
