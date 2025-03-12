package com.example.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.order_service.dto.cache.CachedProduct;

@FeignClient(name = "product-service", url = "http://localhost:8080/product", fallback = ProductClientFallback.class) // 상품 시스템 URL
public interface ProductClient {

    @GetMapping("/api/query/products/productForCache/{id}") 
    CachedProduct getProductForCache(@PathVariable("id") int id);
}
