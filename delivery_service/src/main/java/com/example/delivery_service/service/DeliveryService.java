package com.example.delivery_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.delivery_service.event.PaymentCompletedEvent;
import com.example.delivery_service.model.Deliveries;
import com.example.delivery_service.event.DeliveryCompletedEvent;
import com.example.delivery_service.event.DeliveryStartedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void startDelivery(PaymentCompletedEvent event) {
        kafkaTemplate.send("payment_cancelled", new DeliveryStartedEvent(event.getOrderId()));
        //판매자에게 알림
        //배송상태에 따른 구매자에게 알림
    }

    public void completeDelivery(PaymentCompletedEvent event) {
        kafkaTemplate.send("payment_cancelled", new DeliveryCompletedEvent(event.getOrderId(), ""));
    }

    public void updateDeliveryStatus(Deliveries deliveries) {
        //
    }

    public void getDeliveryByOrderId(int orderId) {
        //
    }

    public Deliveries getDeliveryByDeliveryId(int deliveryId) {
        Deliveries delivery = new Deliveries();
        return delivery;
    }
}
