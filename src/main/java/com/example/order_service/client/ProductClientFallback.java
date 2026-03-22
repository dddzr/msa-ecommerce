package com.example.order_service.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.order_service.dto.response.ProductResponse;

@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public ProductResponse getProduct(int id) { // 404일 때 기본 데이터 반환
        ProductResponse fallback = new ProductResponse();
        fallback.setProductId(id);
        fallback.setProductName("상품 정보를 찾을 수 없습니다.");
        fallback.setPrice(BigDecimal.ZERO);
        fallback.setAvailableOptions(new HashMap<>());
        fallback.setFallback(true); // fallback 여부 표시
        return fallback;
    }

    @Override
    public List<ProductResponse> getProductList(List<Integer> ids) {
        if (ids == null) {
            return List.of();
        }
        return ids.stream()
                .distinct()
                .map(id -> id == null ? null : getProduct(id.intValue()))
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }
}
