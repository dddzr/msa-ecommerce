package com.example.product_service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 상세 API. 옵션 차원은 고정 색/사이즈가 아니라 {@link ProductOptionDto#optionKey} 로 식별합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO {
    private Integer productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String brand;
    private String imageUrl;
    private boolean available;

    /** 등록/조회 공통: 옵션 정의 + 값 라벨 목록 */
    private List<ProductOptionDto> options;

    private double averageRating;
    private int reviewCount;

    private BigDecimal discountRate;
    private BigDecimal discountPrice;

    /** SKU별 재고. {@link VariantStockDto#selections} 은 등록 시 optionKey → 라벨, 조회 시 라벨 + {@link VariantStockDto#valueIds} 포함 */
    private List<VariantStockDto> productStocks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductOptionDto {
        private String optionKey;
        private String displayName;
        private int displayOrder;
        private List<String> valueLabels;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VariantStockDto {
        /** DB PK. 주문·재고 차감 시 이 값을 쓰면 옵션 맵 조합보다 단순 */
        private int variantId;
        /** optionKey → product_option_values.value_id (조회 응답에 채워짐, 등록 요청은 비워도 됨) */
        private Map<String, Integer> valueIds;
        /** optionKey → 표시 라벨 (등록 요청 필수) */
        private Map<String, String> selections;
        private int stockQuantity;
    }
}
