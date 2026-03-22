package com.example.order_service.dto.response;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class OrderItemDetail {
    private int orderId;
    private int orderItemId;
    private int quantity;
    private int productId;
    private Integer variantId;
    private Map<String, String> optionLabelsSnapshot = new LinkedHashMap<>();
    private OrderedProductInfo orderedProductInfo;

    @Data
    public static class OrderedProductInfo {
        private int productId;
        private String productName;
        private int variantId;
        /** 주문 시점 스냅샷 (표시용) */
        private Map<String, String> optionLabels = new LinkedHashMap<>();
        private BigDecimal price;
    }
}
