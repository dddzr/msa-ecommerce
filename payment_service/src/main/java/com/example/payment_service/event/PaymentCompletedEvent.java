package com.example.payment_service.event;

import lombok.Data;

@Data
public class PaymentCompletedEvent {
    private int orderId;
    private double amount;  //결제 금액
    private String transactionId; // Optional: ID from the payment gateway
    private String paymentMethod; // Optional: e.g., Credit Card, PayPal
    private String timestamp; // ISO 8601 format

    public PaymentCompletedEvent(int orderId, double amount, String transactionId, String paymentMethod, String timestamp) {
        this.orderId = orderId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
    }

    public PaymentCompletedEvent(int orderId, double amount) {
        this(orderId, amount, null, null, null); // 선택적 필드는 모두 null로 설정
    }

    @Override
    public String toString() {
        return "PaymentCompletedEvent{" +
                "orderId='" + orderId + '\'' +
                ", quantity=" + amount +
                '}';
    }
}