package com.example.order_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.order_service.dto.request.OrderListRequest;
import com.example.order_service.dto.response.OrderDetail;
import com.example.order_service.dto.response.OrderSummary;
import com.example.order_service.service.NotificationService;
import com.example.order_service.service.OrderQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/query/orders")
@RequiredArgsConstructor// Lombok을 사용하여 생성자 자동 생성
public class OrderQueryController {
    private final OrderQueryService orderQueryService;
    private final NotificationService notificationService;

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

    // SSE
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam int userId,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.createEmitter(userId, lastEventId);
    }

    // 인기 상품 조회
    @GetMapping("/top-selling-products")
    public ResponseEntity<List<Integer>> getTopSellingProducts(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @RequestParam("limit") int limit) {

        List<Integer> popularItems = orderQueryService.getTopSellingProductIds(startDate, endDate, limit);
        return ResponseEntity.ok(popularItems);
    }
}
