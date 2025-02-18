package com.example.payment_service.event;

import lombok.Data;

@Data
public class PaymentCreatedEvent {
    private int orderId;
    private String paymentUrl;

    public PaymentCreatedEvent(int orderId, String paymentUrl) {
        this.orderId = orderId;
        this.paymentUrl = paymentUrl;
    }
}