package com.example.order_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.order_service.dto.request.OrderListRequest;
import com.example.order_service.dto.request.OrderRequest;
import com.example.order_service.dto.response.OrderDetail;
import com.example.order_service.dto.response.OrderSummary;
import com.example.order_service.entity.OrderStatus;
import com.example.order_service.entity.Orders;
import com.example.order_service.service.NotificationService;
import com.example.order_service.service.OrderCommandService;
import com.example.order_service.service.OrderQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor// Lombok을 사용하여 생성자 자동 생성
public class OrderController {
    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final NotificationService notificationService;

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

    // 주문 정보 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetail> getOrderDetail(@PathVariable int orderId) {
        OrderDetail orderDetail = orderQueryService.getOrderDetail(orderId);
        return ResponseEntity.ok(orderDetail);
    }

    // 주문 목록 (구매자용)
    @PostMapping("/orderList")
    public ResponseEntity<List<OrderSummary>> getCustomerOrders(@RequestBody OrderListRequest request) {
        List<OrderSummary> orders = orderQueryService.getCustomerOrders(request);
        return ResponseEntity.ok(orders);
    }

    // 주문 목록 (판매자용) // 현재 관리자 = 판매자
    @PostMapping("/admin/orderList")
    public ResponseEntity<List<OrderSummary>> getSellerOrders(@RequestBody OrderListRequest request) {
        List<OrderSummary> orders = orderQueryService.getSellerOrders(request);
        return ResponseEntity.ok(orders);
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

    // SSE
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam int userId,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.createEmitter(userId, lastEventId);
    }
}
