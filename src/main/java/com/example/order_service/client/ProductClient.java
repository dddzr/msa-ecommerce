package com.example.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.order_service.dto.response.ProductResponse;
import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8080/product", fallback = ProductClientFallback.class) // 상품 시스템 URL
public interface ProductClient {

    @GetMapping("/api/query/products/product/{id}") 
    ProductResponse getProduct(@PathVariable("id") int id);

    @GetMapping("/api/query/products/productList")
    List<ProductResponse> getProductList(@RequestParam("ids") List<Integer> ids);
}
