package com.example.product_service.event;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrderCancelEvent {
    private int orderId;
    private int userId;
    private String reason;
    private LocalDateTime cancelledAt;

    private int productId;
    private int variantId;
    private int quantity;

    private int price;
}
