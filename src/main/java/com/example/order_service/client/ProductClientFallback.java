package com.example.order_service.client;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.example.order_service.dto.response.ProductResponse;

@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public ProductResponse getProduct(int id) { // 404일 때 기본 데이터 반환
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(id);
        productResponse.setProductName("상품 정보를 찾을 수 없습니다.");
        productResponse.setPrice(BigDecimal.ZERO);
        productResponse.setAvailableColors(new HashMap<>());
        productResponse.setAvailableSizes(new HashMap<>());
        return productResponse;
    }
}
