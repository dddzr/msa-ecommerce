package com.example.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
    
    // 기본 생성자
    public OrderNotFoundException(int orderId) {
        super("Order not found with id: " + orderId);
    }
    
    // 다른 생성자 (예: 메시지를 커스터마이징할 수 있는 경우)
    public OrderNotFoundException(String message) {
        super(message);
    }
}
