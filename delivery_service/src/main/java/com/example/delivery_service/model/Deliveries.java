package com.example.delivery_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
public class Deliveries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private int deliveryId; // 배송 ID

    @Column(name = "order_id", nullable = false)
    private int orderId; // 주문 ID

    private String deliveryStatus; // 배송 상태
    private LocalDateTime shippedAt; // 배송 시작 시간
    private LocalDateTime deliveredAt; // 배송 완료 시간
    private String trackingNumber; // 배송 추적 번호
    private String carrier; // 배송 업체
    private String shippingAddress; // 배송 주소

    // Getters and Setters
    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public String toString() {
        return "Deliveries{" +
                "deliveryId=" + deliveryId +
                ", orderId=" + orderId +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", shippedAt=" + shippedAt +
                ", deliveredAt=" + deliveredAt +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", carrier='" + carrier + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }
}
