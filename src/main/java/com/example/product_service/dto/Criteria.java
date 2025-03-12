package com.example.product_service.dto;

import lombok.Data;

@Data
public class Criteria {
    private int page = 1; // 현재 페이지
    private int pageSize = 3; // 한 페이지당 개수
    private String keyword = ""; // 검색어

    // public int getOffset() { //startIndex
    //     return (page - 1) * pageSize;
    // }
}
