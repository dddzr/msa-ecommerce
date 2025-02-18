package com.example.product_service.service;

import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.ProductDetailDTO.ProductStockDto;
import com.example.product_service.model.ProductStocks;
import com.example.product_service.model.Products;
// import com.example.product_service.model.ProductDocument;
// import com.example.product_service.repository.ProductSearchRepository;

import lombok.RequiredArgsConstructor;

import com.example.product_service.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/* 조회 쿼리 */
@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;
    // private final ProductSearchRepository productSearchRepository;
    //private final ModelMapper modelMapper;
    
    public ProductDetailDTO productToProductDto(Products product) {
        ProductDetailDTO productDTO = new ProductDetailDTO();
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCategory(product.getCategory());
        productDTO.setBrand(product.getBrand());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setAvailable(product.isAvailable());

        List<ProductDetailDTO.ProductStockDto> stockDTOs = product.getStocks().stream()
                .map(stock -> new ProductStockDto(
                        stock.getSize().getSizeName(),
                        stock.getColor().getColorName(),
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

    /* 검색 기능(List 출력) => elasticSearch */
    // public List<ProductRequest> getProductsByName(String name) {
    //     return productSearchRepository.findByName(name);
    // }

    public List<Products> getAllProducts() {
        return productRepository.findAll();
        //return productSearchRepository.getAllProducts();
    }
}
