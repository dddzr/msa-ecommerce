package com.example.product_service.service;

import com.example.product_service.dto.Criteria;
import com.example.product_service.dto.ProductDTO;
import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.ProductDetailDTO.ProductOptionDto;
import com.example.product_service.dto.ProductDetailDTO.VariantStockDto;
import com.example.product_service.dto.ProductSearchDTO;
import com.example.product_service.dto.VariantStockMapper;
import com.example.product_service.dto.cache.CachedProduct;
import com.example.product_service.dto.response.ProductResponse;
import com.example.product_service.entity.ProductOption;
import com.example.product_service.entity.ProductOptionValue;
import com.example.product_service.entity.Products;
import com.example.product_service.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;

import com.example.product_service.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
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
    private final CacheService cacheService;

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

        productDTO.setOptions(buildOptionDtos(product));
        List<VariantStockDto> stockDTOs = product.getVariants() == null
                ? List.of()
                : product.getVariants().stream()
                        .map(v -> VariantStockMapper.toRow(v, product))
                        .collect(Collectors.toList());
        productDTO.setProductStocks(stockDTOs);
        return productDTO;
    }

    private List<ProductOptionDto> buildOptionDtos(Products product) {
        if (product.getOptions() == null) {
            return List.of();
        }
        return product.getOptions().stream()
                .sorted(Comparator.comparingInt(ProductOption::getDisplayOrder))
                .map(o -> new ProductOptionDto(
                        o.getOptionKey(),
                        o.getDisplayName(),
                        o.getDisplayOrder(),
                        o.getValues() == null
                                ? List.of()
                                : o.getValues().stream()
                                        .sorted(Comparator.comparingInt(ProductOptionValue::getSortOrder))
                                        .map(ProductOptionValue::getValueLabel)
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private ProductResponse toResponse(CachedProduct cached) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(cached.getProductId());
        productResponse.setProductName(cached.getProductName());
        productResponse.setPrice(cached.getPrice());
        productResponse.setAvailableOptions(cached.getAvailableOptions());
        return productResponse;
    }

    public ProductDetailDTO getProductDetail(int id) {
        Products product = productRepository.findProductWithDetails(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        return productToProductDto(product);
    }

    public ProductResponse getProduct(int id) {
        CachedProduct cacheHit = cacheService.getCachedProduct(id);
        if (cacheHit != null) {
            return toResponse(cacheHit);
        }

        var results = productRepository.findProduct(id);

        if (results.isEmpty()) {
            return null;
        }

        CachedProduct cacheValue = null;
        Map<String, Map<Integer, String>> optionBuckets = CachedProduct.emptyOptionMaps();

        for (var row : results) {
            if (cacheValue == null) {
                cacheValue = new CachedProduct(row.productId(), row.name(), row.price(), optionBuckets);
            }
            mergeOptionKeyIntoCache(cacheValue, row.valueId(), row.valueLabel(), row.optionKey());
        }

        cacheService.saveCachedProduct(id, cacheValue);

        return toResponse(cacheValue);
    }

    public List<ProductResponse> getProductList(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, CachedProduct> cacheHits = cacheService.getCachedProducts(ids);
        List<Integer> missingIds = ids.stream()
                .filter(id -> id != null && !cacheHits.containsKey(id))
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, CachedProduct> dbHits = new HashMap<>();
        if (!missingIds.isEmpty()) {
            var results = productRepository.findProductList(missingIds);

            Map<Integer, CachedProduct> cacheValueList = new LinkedHashMap<>();
            for (var row : results) {
                CachedProduct cacheValue = cacheValueList.get(row.productId());
                if (cacheValue == null) {
                    cacheValue = new CachedProduct(row.productId(), row.name(), row.price(),
                            CachedProduct.emptyOptionMaps());
                    cacheValueList.put(row.productId(), cacheValue);
                }
                mergeOptionKeyIntoCache(cacheValue, row.valueId(), row.valueLabel(), row.optionKey());
            }

            dbHits.putAll(cacheValueList);
            cacheService.saveCachedProducts(cacheValueList, 200);
        }

        List<ProductResponse> ordered = new ArrayList<>(ids.size());
        for (Integer id : ids) {
            if (id == null) {
                continue;
            }
            CachedProduct cacheValue = cacheHits.get(id);
            if (cacheValue == null) {
                cacheValue = dbHits.get(id);
            }
            if (cacheValue != null) {
                ordered.add(toResponse(cacheValue));
            }
        }

        return ordered;
    }

    public List<ProductDTO> getProductsByName(String name) {
        return productSearchRepository.findByName(name);
    }

    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<ProductSearchDTO> getFilteredProducts(Criteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPage() - 1, criteria.getPageSize());
        return productSearchRepository.findByNameOrAll(criteria.getKeyword(), pageable);
    }

    private void mergeOptionKeyIntoCache(CachedProduct cache, Integer valueId, String valueLabel, String optionKey) {
        if (valueId == null || optionKey == null) {
            return;
        }
        cache.getAvailableOptions()
                .computeIfAbsent(optionKey, k -> new HashMap<>())
                .put(valueId, valueLabel != null ? valueLabel : "");
    }
}
