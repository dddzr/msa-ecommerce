package com.example.payment_service.service;

import java.net.URI;

import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.payment_service.dto.PaymentApprovalRequest;
import com.example.payment_service.event.OrderCreatedEvent;
import com.example.payment_service.event.PaymentCompletedEvent;
import com.example.payment_service.event.PaymentCreatedEvent;
import com.example.payment_service.event.PaymentFailedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void createPaymentUrl(OrderCreatedEvent event) {
        //외부 결제 API 호출
        // 결제 예약 api를 호출하는 것이고 redirect Url을 지정하고 해당 api를 구현해야한다.

        // 결제 URL 생성
        String paymentUrl = "https://payment-system.com/checkout?orderId=" + event.getOrderId();

        // 필요시 결제 정보 DB에 저장
        // paymentRepository.save(order);

        // 결제 URL 생성 후 주문 서비스에 발행 (주문 상태 업데이트)
        System.out.println("payment_url_created 이벤트 발행!!");
        kafkaTemplate.send("payment_url_created", new PaymentCreatedEvent(event.getOrderId(), paymentUrl));
    }        
    public void approvePayment(String reserveId, int orderId, double amount) {
        // 네이버 페이 결제 승인 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl("naverPayApiUrl")
                .pathSegment("partnerId", "payments", "v2", "apply", "payment")
                .toUriString();

        // 결제 승인 요청 파라미터 설정
        PaymentApprovalRequest request = new PaymentApprovalRequest(reserveId, orderId, amount);

        // 네이버 페이 API로 결제 승인 요청 httpClient 방법으로로
        ResponseEntity<String> response = new ResponseEntity<String>(null);
        //ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        // 결제 승인 응답 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            kafkaTemplate.send("payment_completed", new PaymentCompletedEvent(orderId, amount));
        } else {
            kafkaTemplate.send("payment_failed", new PaymentFailedEvent(orderId, (String) response.getBody()));
        }
    }

    public void getPayment(String paymentId) {
        // 결제 내역 조회
    }

    public void cancelPayment(String paymentId) {
        // 결제 취소
    }
    
}
