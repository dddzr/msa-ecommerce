package com.example.product_service.dto.response;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class ProductResponse {
    private int productId;
    private String productName;
    private BigDecimal price;
    /** optionKey → (valueId → 표시 라벨). 목록 캐시용 */
    private Map<String, Map<Integer, String>> availableOptions;
}
