package com.example.order_service.dto.request;

import lombok.Data;

@Data
public class OrderListRequest {
    private Integer userId; // 특정 사용자의 주문 목록 (선택적)
    private Integer productId; // 특정 상품의 주문 목록 (선택적)
    private Long startDate; // 조회 시작 날짜 (선택적)
    private Long endDate; // 조회 종료 날짜 (선택적)
    private int page = 1; // 기본값 1 페이지
    private int pageSize = 10; // 기본 페이지 크기 10
    // private String sortBy = "createdAt"; // 정렬 기준 (기본값: createdAt)
}
