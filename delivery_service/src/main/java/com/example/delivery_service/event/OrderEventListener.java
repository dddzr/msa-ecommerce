package com.example.delivery_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.delivery_service.service.DeliveryService;

@Service
public class OrderEventListener {

    private final DeliveryService deliveryService;

    public OrderEventListener(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @KafkaListener(topics = "payment_completed", groupId = "delivery-service-group")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        deliveryService.startDelivery(event);
    }

}
