package com.example.product_service.controller;

import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.service.ProductCommandService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/command/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    @PostMapping
    public void insertProduct(@RequestBody ProductDetailDTO productRequest, @RequestHeader(value = "X-User-Name", required = false) String username) {
        productCommandService.insertProduct(productRequest);
    }

    @PatchMapping("/{id}")
    public void updateProduct(@PathVariable int id, @RequestBody ProductDetailDTO productRequest) {
        productCommandService.updateProduct(id, productRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productCommandService.deleteProduct(id);
    }
}
