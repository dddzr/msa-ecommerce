package com.example.order_service.event;

import lombok.Data;

@Data
public class PaymentFailedEvent {
    private int orderId;
    private String reason;

    public PaymentFailedEvent(int orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }
}