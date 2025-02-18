package com.example.order_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.order_service.entity.OrderStatus;

import lombok.Data;

@Data
public class OrderSummary { // db에서 조회 + orderItems캐시에서 합성
    private int orderId;
    private int userId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDetail> orderItemDetails;
}
