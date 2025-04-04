package com.example.product_service.controller;

import com.example.product_service.dto.Criteria;
import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.ProductDTO;
import com.example.product_service.dto.cache.CachedProduct;
import com.example.product_service.entity.Products;
import com.example.product_service.service.ProductQueryService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/query/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final ProductQueryService productQueryService;

    // 조회 (상품 목록, 상세 조회 등)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable("id") int id) {
        ProductDetailDTO product = productQueryService.getProductDetail(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    // 조회 (캐시 DTO)
    @GetMapping("/productForCache/{id}")
    public ResponseEntity<CachedProduct> getProductForCache(@PathVariable("id") int id) {
        CachedProduct product = productQueryService.getProductForCache(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    // @GetMapping("/search/{name}")
    // public List<ProductRequest> getProductsByName(@PathVariable String name) {
    //     return productQueryService.getProductsByName(name);
    // }

    // 전체 상품 조회
    // @GetMapping("/all")
    // public ResponseEntity<List<Products>> getAllProducts() {
    //     List<Products> products = productQueryService.getAllProducts();
    //     return ResponseEntity.ok(products);
    //     //return products.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(products);
    // }

    // 필터링된 상품 조회 (페이징 적용)
    @PostMapping("/filteredProducts")
    public ResponseEntity<Page<ProductDTO>> getFilteredProducts(@RequestBody Criteria criteria) {
        Page<ProductDTO> products = productQueryService.getFilteredProducts(criteria);
        return ResponseEntity.ok(products);
        //return products.hasContent() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build();
    }

}
