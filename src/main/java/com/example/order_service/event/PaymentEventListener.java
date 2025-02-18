package com.example.order_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.order_service.entity.OrderStatus;
import com.example.order_service.service.OrderCommandService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymentEventListener {

    private final OrderCommandService orderCommandService;
    private final ObjectMapper objectMapper;

    public PaymentEventListener(OrderCommandService orderCommandService, ObjectMapper objectMapper) {
        this.orderCommandService = orderCommandService;
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

    @KafkaListener(topics = "payment_url_created", groupId = "order-service-group")
    public void handlePaymentUrlCreated(String message) {        
        PaymentCreatedEvent event = parseEvent(message, PaymentCreatedEvent.class);
        orderCommandService.handlePaymentUrl(event.getOrderId(), event.getPaymentUrl());
    }

    // 메타 데이터 필요할 시 ConsumerRecord 이용 (메시지의 key, partition, offset 등)
    // @KafkaListener(topics = "payment_url_created", groupId = "order-service-group")
    // public void handlePaymentUrlCreated(ConsumerRecord<String, String> record) {
    //     // 메시지 본문 추출
    //     String eventMessage = record.value();
    //     PaymentCreatedEvent event = objectMapper.readValue(message, PaymentCreatedEvent.class);
    //     orderCommandService.handlePaymentUrl(event.getOrderId(), event.getPaymentUrl());
        
    //     // 메타데이터 처리 예시 (옵션)
    //     System.out.println("Received message from partition " + record.partition() + " with offset " + record.offset());
    // }

    // 결제 완료 이벤트 수신
    @KafkaListener(topics = "payment_completed", groupId = "order-service-group")
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        // 결제 완료 후 주문 상태 변경
        orderCommandService.updateOrderStatus(event.getOrderId(), OrderStatus.PAYMENT_COMPLETED);
    }

    // 결제 실패 이벤트 수신
    @KafkaListener(topics = "payment_failed", groupId = "order-service-group")
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {
        // 결제 실패 후 주문 상태 변경
        orderCommandService.updateOrderStatus(event.getOrderId(), OrderStatus.PAYMENT_FAILED);
    }
}
