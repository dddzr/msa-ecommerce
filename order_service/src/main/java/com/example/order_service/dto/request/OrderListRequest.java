package com.example.order_service.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OrderListRequest {
    private Integer userId = null; // 특정 사용자의 주문 목록 (선택적)
    private Integer productId = null; // 특정 상품의 주문 목록 (선택적)
    private String status = ""; // 배송 상태 (선택적)
    private LocalDate startDate = null; // 조회 시작 날짜 (선택적)
    private LocalDate endDate = null; // 조회 종료 날짜 (선택적)
    // private int page = 1; // 기본값 1 페이지
    // private int pageSize = 5; // 기본 페이지 크기 10
}
