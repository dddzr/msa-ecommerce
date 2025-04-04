package com.example.product_service.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private int productId; // 상품 ID
    private String name; // 상품명
    private BigDecimal price; // 상품 가격
    // private BigDecimal discountPrice; // 할인 금액
    private String category; // 상품 카테고리
    private String brand; // 브랜드명
    private String imageUrl; // 상품 대표 이미지 URL
}
