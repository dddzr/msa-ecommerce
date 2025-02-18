package com.example.order_service.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemRequest {
    private int productId;
    private int colorId;
    private int sizeId;
    private int quantity;
    private BigDecimal price;
}