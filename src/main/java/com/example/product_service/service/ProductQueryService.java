package com.example.product_service.service;

import com.example.product_service.dto.Criteria;
import com.example.product_service.dto.ProductDTO;
import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.ProductDetailDTO.ProductStockDto;
import com.example.product_service.dto.cache.CachedProduct;
import com.example.product_service.entity.Products;
import com.example.product_service.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;

import com.example.product_service.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/* 조회 쿼리 */
@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    //private final ModelMapper modelMapper;
    
    public ProductDetailDTO productToProductDto(Products product) {
        ProductDetailDTO productDTO = new ProductDetailDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategory(product.getCategory());
        productDTO.setBrand(product.getBrand());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setAvailable(product.isAvailable());

        List<ProductDetailDTO.ProductStockDto> stockDTOs = product.getStocks().stream()
                .map(stock -> new ProductStockDto(
                    stock.getColor().getColorId(),
                    stock.getColor().getColorName(),
                    stock.getSize().getSizeId(),
                    stock.getSize().getSizeName(),
                    stock.getStockQuantity()))
                .collect(Collectors.toList());

        productDTO.setProductStocks(stockDTOs);
        return productDTO;
        // return modelMapper.map(products, ProductDetailDTO.class);
    }

    // public ProductRequest productDocmentToProductDto(ProductDocument productDocment) {
    //     return modelMapper.map(productDocment, ProductRequest.class);
    // }

    /* 상세정보 => mariaDB */
    public ProductDetailDTO getProductDetail(int id) {
        Products product = productRepository.findProductWithDetails(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        ProductDetailDTO productDTO = productToProductDto(product);
        return productDTO;
    }

    public CachedProduct getProductForCache(int id) {
        List<Object[]> results = productRepository.findProductForCache(id);
        
        if (results.isEmpty()) {
            return null;
        }

        CachedProduct cachedProduct = null;
        Map<Integer, String> availableColors = new HashMap<>();
        Map<Integer, String> availableSizes = new HashMap<>();
        List<CachedProduct.ProductStockInfo> stockInfoList = new ArrayList<>();

        for (Object[] row : results) {
            int productId = (Integer) row[0];
            String name = (String) row[1];
            BigDecimal price = (BigDecimal) row[2];
            int colorId = (Integer) row[3];
            String colorName = (String) row[4];
            int sizeId = (Integer) row[5];
            String sizeName = (String) row[6];
            int stockQuantity = (row[7] != null) ? (Integer) row[7] : 0;

            if (cachedProduct == null) {
                cachedProduct = new CachedProduct(productId, name, price, availableColors, availableSizes, stockInfoList);
            }

            availableColors.put(colorId, colorName);
            availableSizes.put(sizeId, sizeName);

            CachedProduct.ProductStockInfo stockInfo = new CachedProduct.ProductStockInfo();
            stockInfo.setColorId(colorId);
            stockInfo.setSizeId(sizeId);
            stockInfo.setStockQuantity(stockQuantity);
            stockInfoList.add(stockInfo);
        }
        //cacheService.saveOrderedProductInfo(id, cachedProduct); TODO: 캐시 저장
        return cachedProduct;
    }
    
    /* 검색 기능(List 출력) => elasticSearch */
    public List<ProductDTO> getProductsByName(String name) {
        return productSearchRepository.findByName(name);
    }

    public List<Products> getAllProducts() {
        return productRepository.findAll();
        //return productSearchRepository.getAllProducts();
    }

    public Page<ProductDTO> getFilteredProducts(Criteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPage() - 1, criteria.getPageSize());
        // return productRepository.findByNameContaining(criteria.getKeyword(), pageable);
        return productSearchRepository.findByName(criteria.getKeyword(), pageable);
    }
}
