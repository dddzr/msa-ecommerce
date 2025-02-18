package com.example.payment_service.controller;

import org.springframework.web.bind.annotation.*;

import com.example.payment_service.dto.PaymentApprovalRequest;
import com.example.payment_service.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 결제 승인을 처리하는 REST API 엔드포인트
     */
    @PostMapping("/approve")
    public void approvePayment(@RequestParam PaymentApprovalRequest request) {
        paymentService.approvePayment(request.getReserveId(), request.getOrderId(), request.getAmount());
    }
}
