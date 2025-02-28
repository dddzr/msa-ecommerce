package com.example.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order_service.dto.request.OrderItemRequest;
import com.example.order_service.dto.request.OrderRequest;
import com.example.order_service.dto.response.OrderDetail;
import com.example.order_service.dto.response.OrderItemDetail;
import com.example.order_service.dto.response.OrderSummary;
import com.example.order_service.entity.OrderItems;
import com.example.order_service.entity.OrderStatus;
import com.example.order_service.entity.Orders;
import com.example.order_service.event.DeliveryStartedEvent;
import com.example.order_service.event.OrderCreatedEvent;
import com.example.order_service.event.OrderEvent;
import com.example.order_service.exception.OrderNotFoundException;
import com.example.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderCommandService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    // private final WebSocketService webSocketService;
    private final NotificationService notificationService;

    public OrderItems dtoToOrderItems(OrderItemRequest itemRequest, Orders order) {        
        OrderItems orderItem = new OrderItems();
        orderItem.setProductId(itemRequest.getProductId());
        orderItem.setSizeId(itemRequest.getSizeId());
        orderItem.setColorId(itemRequest.getColorId());
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setPrice(itemRequest.getPrice());
        orderItem.setOrder(order); // 주문 객체와 연관 설정
        return orderItem;
    }

    public Orders dtoToOrder(OrderRequest request, OrderStatus status) {   
        Orders order = new Orders();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.CREATED);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        return order;
    }

    @Transactional
    public Orders createOrder(OrderRequest request) {
        // 주문 객체 생성
        Orders order = dtoToOrder(request, OrderStatus.CREATED);

        // 주문 상품 리스트
        List<OrderItems> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItems orderItem = dtoToOrderItems(itemRequest, order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        // 총 금액 계산
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        // 주문 저장
        Orders savedOrder = orderRepository.save(order);

        // 이벤트 발행
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getOrderId());
        event.setProductId(savedOrder.getOrderItems().get(0).getProductId());
        event.setColorId(savedOrder.getOrderItems().get(0).getColorId());
        event.setSizeId(savedOrder.getOrderItems().get(0).getSizeId());
        event.setQuantity(savedOrder.getOrderItems().get(0).getQuantity());
        event.setUserId(savedOrder.getUserId());

        kafkaTemplate.send("order-created-topic", event);
        return savedOrder;
    }

    public void handlePaymentUrl(int orderId, String paymentUrl) {
        Orders order = orderRepository.findByOrderId(orderId).orElse(null);

        if (order == null) {
            System.out.println("결제 URL을 찾을 수 없습니다. orderId: " + orderId);
            notificationService.notifyUser(orderId, "PAYMENT_FAILED");
            return;
        }
        
        // 주문에 결제 URL 업데이트
        // order.setPaymentUrl(paymentUrl);
        // order.setStatus(OrderStatus.PAYMENT_PENDING);
        // orderRepository.save(order);
        
        // 사용자 알림
        // webSocketService.sendPaymentUrlToUser(order.getUserId(), paymentUrl); //WebSocket - 양방향
        notificationService.notifyUser(order.getUserId(), paymentUrl); //SSE - 단방향
    }

    // 결제 완료 후 주문 상태 변경
    public Orders updateOrderStatus(int orderId, OrderStatus status) {
        Orders order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(status);
        Orders savedOrder = orderRepository.save(order);

        // 결제 완료 후 배송 서비스로 이벤트 발행
        if (status == OrderStatus.PAYMENT_COMPLETED) {
            kafkaTemplate.send("delivery_started", new DeliveryStartedEvent(orderId));
        } else if(status == OrderStatus.PAYMENT_FAILED){
            notificationService.notifyUser(order.getUserId(), "PAYMENT_FAILED"); // 필요하다면!! 외부 결제api가 알아서 실패 전달할 것 같긴함.
        }
        return savedOrder;
    }

    // 주문 취소
    public boolean cancelOrder(int orderId) {
        Orders order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(OrderStatus.CANCELLED);
        try {
            orderRepository.save(order);

            // 주문 취소 후 결제 취소와 배송 취소를 요청하는 이벤트 발행
            kafkaTemplate.send("payment_cancelled", new OrderEvent(orderId));
            kafkaTemplate.send("delivery_cancelled", new OrderEvent(orderId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
