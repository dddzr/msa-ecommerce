package com.example.delivery_service.event;

import lombok.Data;

@Data
public class DeliveryCompletedEvent {
    private int orderId;             // 주문 ID
    private String deliveryAddress;  // 배송 주소
    private String deliveryStatus;   // 배송 상태 (예: "완료", "배송 중")
    private String deliveryTimestamp; // 배송 완료 시간 (ISO 8601 포맷, 예: "2025-01-16T12:00:00Z")
    
    // 필수 필드만 사용한 생성자
    public DeliveryCompletedEvent(int orderId, String deliveryAddress, String deliveryStatus, String deliveryTimestamp) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.deliveryStatus = deliveryStatus;
        this.deliveryTimestamp = deliveryTimestamp;
    }
    
    // 필수 필드만 사용한 생성자 (배송 완료 상태 및 시간을 필수로 제공하지 않는 경우)
    public DeliveryCompletedEvent(int orderId, String deliveryAddress) {
        this(orderId, deliveryAddress, "배송 완료", null); // 기본 배송 상태는 "배송 완료", 시간은 null로 설정
    }
}
