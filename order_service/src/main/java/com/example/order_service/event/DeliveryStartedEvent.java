package com.example.order_service.event;

public class DeliveryStartedEvent {
    private int orderId;

    public DeliveryStartedEvent(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
