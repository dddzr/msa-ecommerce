package com.example.payment_service.event;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    private int orderId;
    private int productId;
    private int colorId;
    private int sizeId;
    private int quantity;
    private int userId;
}
