package com.example.product_service.dto.cache;

import java.math.BigDecimal;

/**
 * 캐시 적재용: SKU( variant )에 실제로 붙은 옵션 값 1건에 대한 플랫 행.
 * JPQL constructor expression 과 인자 순서·타입을 맞출 것.
 */
public record ProductCacheOptionRow(
        int productId,
        String name,
        BigDecimal price,
        Integer valueId,
        String valueLabel,
        String optionKey
) {
}
