package com.example.product_service.dto.cache;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Redis 조회 캐시. 노출 가능한 옵션 값은 optionKey 기준으로 그룹화 (valueId → 라벨).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CachedProduct {
    private int productId;
    private String productName;
    private BigDecimal price;
    /** optionKey → (valueId → 표시 라벨) */
    private Map<String, Map<Integer, String>> availableOptions;

    public static Map<String, Map<Integer, String>> emptyOptionMaps() {
        return new HashMap<>();
    }
}
