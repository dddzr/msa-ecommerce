package com.example.product_service.event;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    private int orderId;
    private int productId;
    private int sizeId;
    private int colorId;
    private int quantity;
    private int userId;
    private int price;
}
// 장바구니 기능 추가 시 여러 상품 동시에 주문
// 아래 처럼 수정하거나 Zero-Payload 방식으로 key만 이벤트로 넘기고 api call하여 필요 정보 조회.
// public class OrderCreatedEvent {
//     private int orderId; // 주문 ID
//     private int userId;  // 사용자 ID
//     private List<OrderItem> items; // 주문 상품 리스트

//     @Data
//     public static class OrderItem {
//         private int productId; // 상품 ID
//         private int colorId;   // 색상 ID
//         private int sizeId;    // 사이즈 ID
//         private int quantity;  // 수량
//     }
// }
