package com.example.product_service.dto.cache;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor  // 모든 필드를 받는 생성자 자동 생성
@NoArgsConstructor   // 기본 생성자 추가 (필요할 수도 있음)
public class CachedProduct {
    private int productId;
    private String productName;
    private BigDecimal price;
    private Map<Integer, String> availableColors; // colorId -> colorName
    private Map<Integer, String> availableSizes; // sizeId -> sizeName
    private List<ProductStockInfo> stockInfo;

    @Data
    public static class ProductStockInfo {
        private int colorId;
        private int sizeId;
        private int stockQuantity;
    }
    
}