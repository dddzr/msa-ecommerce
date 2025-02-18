package com.example.order_service.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class OrderRequest {
    private int userId;
    private String shippingAddress;
    private String paymentMethod;
    private List<OrderItemRequest> items; // 주문 상품 목록
}