package com.example.product_service.controller;

import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.model.Products;
import com.example.product_service.service.ProductQueryService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/query/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final ProductQueryService productQueryService;

    // 조회 (상품 목록, 상세 조회 등)
    @GetMapping("/{id}")
    public ProductDetailDTO getProductDetail(@PathVariable int id) {
        return productQueryService.getProductDetail(id);
    }

    // @GetMapping("/search/{name}")
    // public List<ProductRequest> getProductsByName(@PathVariable String name) {
    //     return productQueryService.getProductsByName(name);
    // }

    @GetMapping("/all")
    public List<Products> getAllProducts() {
        return productQueryService.getAllProducts();
    }
}
