package com.example.order_service.client;

import org.springframework.stereotype.Component;

import com.example.order_service.dto.cache.CachedProduct;

@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public CachedProduct getProductForCache(int id) {
        return null; // 404일 때 null 반환 (혹은 기본 데이터)
    }
}
