package com.example.order_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 결제 URL을 클라이언트에 전송
    public void sendPaymentUrlToUser(int userId, String paymentUrl) {
        // /topic/payment/{userId}로 메시지를 전송
        messagingTemplate.convertAndSend("/topic/payment/" + userId, paymentUrl);
    }
}
