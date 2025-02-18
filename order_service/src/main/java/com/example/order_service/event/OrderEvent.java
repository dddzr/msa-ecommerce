package com.example.order_service.event;

public class OrderEvent {
    private int orderId;
    
    public OrderEvent(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
