package com.example.product_service.controller;

import com.example.product_service.dto.Criteria;
import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.cache.CachedProduct;
import com.example.product_service.entity.Products;
import com.example.product_service.service.ProductQueryService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/query/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final ProductQueryService productQueryService;

    // 조회 (상품 목록, 상세 조회 등)
    @GetMapping("/{id}")
    public ProductDetailDTO getProductDetail(@PathVariable("id") int id) {
        return productQueryService.getProductDetail(id);
    }

    // 조회 ( 캐시 dto )
    @GetMapping("/productForCache/{id}")
    public CachedProduct getProductForCache(@PathVariable("id") int id) {
        return productQueryService.getProductForCache(id);
    }

    // @GetMapping("/search/{name}")
    // public List<ProductRequest> getProductsByName(@PathVariable String name) {
    //     return productQueryService.getProductsByName(name);
    // }

    @GetMapping("/all")
    public List<Products> getAllProducts() {
        return productQueryService.getAllProducts();
    }

    @PostMapping("/filteredProducts")
    public Page<Products> getFilteredProducts(@RequestBody Criteria criteria) {
        return productQueryService.getFilteredProducts(criteria);
    }

}
