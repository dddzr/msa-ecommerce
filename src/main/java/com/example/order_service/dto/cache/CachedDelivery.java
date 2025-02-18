package com.example.order_service.dto.cache;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CachedDelivery { // TODO: 개별 배송일 때
    private int deliveryId;
    private String deliveryStatus;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String trackingNumber;
    private String carrier;
    private String shippingAddress;
}
