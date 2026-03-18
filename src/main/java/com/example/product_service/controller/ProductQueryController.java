package com.example.product_service.controller;

import com.example.product_service.dto.Criteria;
import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.ProductDTO;
import com.example.product_service.dto.ProductSearchDTO;
import com.example.product_service.dto.response.ProductResponse;
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

    // 단건 조회 (캐시 DTO)
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") int id) {
        ProductResponse product = productQueryService.getProduct(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    // 배치 조회 - 아직 안 씀
    @GetMapping("/productList/{ids}")
    public ResponseEntity<List<ProductResponse>> getProductList(@RequestParam("ids") List<Integer> ids) {
        // List<CachedProduct> products = ids.stream()
        //         .map(productQueryService::getProductForCache)
        //         .filter(p -> p != null) // 없는 상품 제외
        //         .collect(Collectors.toList());
        List<ProductResponse> products = productQueryService.getProductList(ids);
        return ResponseEntity.ok(products);
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
    public ResponseEntity<Page<ProductSearchDTO>> getFilteredProducts(@RequestBody Criteria criteria) {
        Page<ProductSearchDTO> products = productQueryService.getFilteredProducts(criteria);
        return ResponseEntity.ok(products);
        //return products.hasContent() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build();
    }

}
