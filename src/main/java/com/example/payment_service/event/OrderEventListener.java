package com.example.payment_service.event;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.payment_service.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderEventListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public OrderEventListener(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    public <T> T parseEvent(String message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (Exception e) {
            System.out.println("Parsing error: " + e.getMessage());
            return null;  // 또는 적절한 예외 처리
        }
    }

    /**
     * Listener for the "order-created-topic"
     * @param record the Kafka record containing the order creation event.
     */
    @KafkaListener(topics = "order-created-topic", groupId = "payment-service-group")
    public void handleOrderCreated(ConsumerRecord<String, String> record) {
        String orderEvent = record.value();        
        OrderCreatedEvent event = parseEvent(orderEvent, OrderCreatedEvent.class);
        paymentService.createPaymentUrl(event);
    }
}
