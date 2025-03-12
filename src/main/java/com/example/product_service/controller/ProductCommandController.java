package com.example.product_service.controller;

import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.service.ProductCommandService;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/command/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    @PostMapping
    public ResponseEntity<ProductDetailDTO> insertProduct(
            @RequestBody ProductDetailDTO productRequest,
            @RequestHeader(value = "X-User-Name", required = false) String username) {

        ProductDetailDTO createdProduct = productCommandService.insertProduct(productRequest);

        URI location = URI.create("/api/query/products/" + createdProduct.getProductId()); // 생성된 리소스 URI
        return ResponseEntity.created(location).body(createdProduct); // 201 Created + 생성된 상품 반환
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> updateProduct(
            @PathVariable int id,
            @RequestBody ProductDetailDTO productRequest) {
    
        ProductDetailDTO updatedProduct = productCommandService.updateProduct(id, productRequest);
        
        return ResponseEntity.ok(updatedProduct); // 200 OK + 수정된 상품 정보 반환
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        productCommandService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204 No Content (본문 없음)
    }
}
