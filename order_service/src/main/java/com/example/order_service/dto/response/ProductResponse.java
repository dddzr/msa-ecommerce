package com.example.order_service.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ProductResponse {
    private int productId;
    private String productName;
    private BigDecimal price;
    private Map<Integer, String> availableColors; // colorId -> colorName
    private Map<Integer, String> availableSizes; // sizeId -> sizeName
    // private List<ProductStockInfo> stockInfo;

    // @Data
    // public static class ProductStockInfo {
    //     private int colorId;
    //     private int sizeId;
    //     private int stockQuantity;
    // }
    
}