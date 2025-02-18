package com.example.product_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.product_service.service.StockManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderEventListener {
    // private static final Logger logger = LogManager.getLogger(OrderEventListener.class);

    private final ObjectMapper objectMapper;
    private final StockManagementService stockCommandService;

    // 재고 관리
    @KafkaListener(topics = "order-created-topic", groupId = "products-service-group")
    public void handleOrderCreatedEvent(String message) {
        try {
            OrderCreatedEvent orderEvent = objectMapper.readValue(message, OrderCreatedEvent.class);
            stockCommandService.deductStock(orderEvent);
        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "order-cancel-topic", groupId = "products-service-group")
    public void handleOrderCancelEvent(String message) {
        try {
            OrderCancelEvent orderEvent = objectMapper.readValue(message, OrderCancelEvent.class);
            String reason = orderEvent.getReason();
            if(reason.equals("OUT_OF_STOCK")){ // 본인이 발행함.
                return;
            }
            stockCommandService.restoreStock(orderEvent); // 재고 복구
        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
        }
    }
}
