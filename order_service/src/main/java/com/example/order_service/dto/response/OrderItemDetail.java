package com.example.order_service.dto.response;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDetail {
    private int orderId;
    private int orderItemId;
    private int quantity;
    private int productId;
    private int colorId;
    private int sizeId;
    private OrderedProductInfo orderedProductInfo;

    @Data
    public static class OrderedProductInfo{
        private int productId;
        private String productName;
        private int colorId;
        private String colorName;
        private int sizeId;
        private String sizeName;
        private BigDecimal price;
    }
}