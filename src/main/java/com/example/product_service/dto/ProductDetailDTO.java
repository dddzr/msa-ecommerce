package com.example.product_service.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 클라이언트에 필요한 필드만 포함
@Data
public class ProductDetailDTO {
    private Integer productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category; // 상품 카테고리
    private String brand; // 브랜드명
    private String imageUrl; // 상품 이미지 URL
    private boolean available; // 상품 구매 가능 여부
    
    private List<String> sizes; // sizeName 상품 사이즈 옵션 (예: S, M, L, XL 등)
    private List<String> colors; // colorName 상품 색상 옵션 (예: 빨강, 파랑, 검정 등)

    private double averageRating; // 상품 평점 (고객 리뷰 기반)
    private int reviewCount; // 상품 리뷰 수

    private BigDecimal discountRate; // 할인율 (0~100)
    private BigDecimal discountPrice; // 할인 금액

    private List<ProductStockDto> productStocks; // size, color 조합에 따른 수량 정보
    
    @Data
    @AllArgsConstructor //모든 필드를 초기화하는 생성자
    @NoArgsConstructor //JPA나 JSON 직렬화 등을 위해 기본 생성자
    public static class ProductStockDto {   
        private int colorId;
        private String colorName; // 색상명 (예: 빨강, 파랑 등)
        private int sizeId;
        private String sizeName; // 사이즈명 (예: S, M, L 등)
        private int stockQuantity; // 해당 사이즈, 색상 조합의 수량
    }
}