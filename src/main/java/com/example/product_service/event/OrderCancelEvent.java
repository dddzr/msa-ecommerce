package com.example.product_service.event;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderCancelEvent {
    private int orderId;
    private int userId;
    private String reason; //"USER_REQUEST", "OUT_OF_STOCK", "PAYMENT_FAIL"
    private LocalDateTime cancelledAt;
    // private List<Integer> cancelledProductIds; // 부분 취소 기능 추가 시 상품ID 필요

    // zero-payload방식 고민 필요
    //상품시스템에서 필요
    private int productId;
    private int sizeId;
    private int colorId;
    private int quantity;

    //결제시스템에서 필요
    private int price;
}
