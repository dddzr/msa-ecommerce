package com.example.order_service.entity;

public enum OrderStatus {
    CREATED,           // 주문 생성
    PAYMENT_COMPLETED, // 결제 완료
    PAYMENT_FAILED,    // 결제 실패
    DELIVERY_STARTED,  // 배송 시작
    DELIVERY_COMPLETED,// 배송 완료
    CANCELLED;         // 주문 취소
}
