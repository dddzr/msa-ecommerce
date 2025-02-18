package com.example.order_service.event;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    private int orderId;
    private int productId;
    private int userId;
    private int colorId;
    private int sizeId;
    private int quantity;
    private int price;
}
