package com.example.order_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.dto.request.OrderRequest;
import com.example.order_service.entity.OrderStatus;
import com.example.order_service.entity.Orders;
import com.example.order_service.service.OrderCommandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/command/orders")
@RequiredArgsConstructor// Lombok을 사용하여 생성자 자동 생성
public class OrderCommandController {
    private final OrderCommandService orderCommandService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<Orders> createOrder(@RequestBody OrderRequest request) {
        Orders createdOrder = orderCommandService.createOrder(request);
        return ResponseEntity.ok(createdOrder);
    }

    // 주문 상태 변경
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<Orders> updateOrderStatus(@PathVariable int orderId, @RequestBody OrderStatus status) {
        Orders updatedOrder = orderCommandService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable int orderId) {
        boolean isCancelled = orderCommandService.cancelOrder(orderId);
        if (isCancelled) {
            return ResponseEntity.ok("Order successfully cancelled");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to cancel the order");
        }
    }
}
