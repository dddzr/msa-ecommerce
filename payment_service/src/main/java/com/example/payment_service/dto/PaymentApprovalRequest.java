package com.example.payment_service.dto;

import lombok.Data;

@Data
public class PaymentApprovalRequest {

    private String reserveId;  // 결제 예약 ID
    private int orderId;    // 주문 ID
    private double amount;     // 결제 금액

    public PaymentApprovalRequest(String reserveId, int orderId, double amount) {
        this.reserveId = reserveId;
        this.orderId = orderId;
        this.amount = amount;
    }

    // Getters and Setters
}
