package com.example.order_service.dto.request;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class OrderItemRequest {
    private int productId;
    /** {@code product_variants.variant_id} */
    private int variantId;
    /** 주문 내역 표시용 (optionKey → 라벨). 클라이언트가 선택한 SKU 행의 selections 등 */
    private Map<String, String> optionLabels = new LinkedHashMap<>();
    private int quantity;
    private BigDecimal price;
}
