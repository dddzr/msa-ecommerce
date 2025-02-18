package com.example.order_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.order_service.entity.OrderStatus;
import com.example.order_service.service.OrderCommandService;

@Service
public class DeliveryEventListener {

    private final OrderCommandService orderService;

    public DeliveryEventListener(OrderCommandService orderService) {
        this.orderService = orderService;
    }

    // 배송 시작 이벤트 수신
    @KafkaListener(topics = "delivery_started", groupId = "order-service-group")
    public void handleDeliveryStartedEvent(DeliveryStartedEvent event) {
        // 배송 시작 후 주문 상태 변경
        orderService.updateOrderStatus(event.getOrderId(), OrderStatus.DELIVERY_STARTED);
    }

    // 배송 완료 이벤트 수신
    @KafkaListener(topics = "delivery_completed", groupId = "order-service-group")
    public void handleDeliveryCompletedEvent(DeliveryCompletedEvent event) {
        // 배송 완료 후 주문 상태 변경
        orderService.updateOrderStatus(event.getOrderId(), OrderStatus.DELIVERY_COMPLETED);
    }

    // 상품 재고 부족 이벤트 수신
    // @KafkaListener(topics = "stock_out", groupId = "order-service-group")
    // public void handleStockOutEvent(StockOutEvent event) {
    //     // 재고 부족시 주문을 취소하거나 다른 처리
    //     orderService.cancelOrder(event.getOrderId());
    // }
}
