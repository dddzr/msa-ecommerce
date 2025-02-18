package com.example.delivery_service.event;

import lombok.Data;

@Data
public class PaymentFailedEvent {
    private int orderId;
    private String reason;

    public PaymentFailedEvent(int orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "PaymentFailureEvent{" +
                "orderId='" + orderId + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}