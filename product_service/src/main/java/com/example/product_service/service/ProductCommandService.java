package com.example.product_service.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.ProductDetailDTO.ProductStockDto;
import com.example.product_service.model.ProductColors;
// import com.example.product_service.model.ProductDocument;
import com.example.product_service.model.ProductSizes;
import com.example.product_service.model.ProductStocks;
import com.example.product_service.model.Products;
import com.example.product_service.repository.ProductColorsRepository;
import com.example.product_service.repository.ProductRepository;
// import com.example.product_service.repository.ProductSearchRepository;
import com.example.product_service.repository.ProductSizesRepository;
import com.example.product_service.repository.ProductStocksRepository;

import lombok.RequiredArgsConstructor;


/* 삽삭갱 명령 */
@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final ProductSizesRepository productSizesRepository;
    private final ProductColorsRepository productColorsRepository;
    private final ProductStocksRepository productStocksRepository;
    // private final ProductSearchRepository productSearchRepository;
    private final ModelMapper modelMapper;

    public Products productDtoToProduct(ProductDetailDTO productDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(ProductDetailDTO.class, Products.class).addMappings(mapper -> {
            mapper.skip(Products::setStocks);  // productStocks 필드 제외
            mapper.skip(Products::setSizes);          // sizes 필드 제외
            mapper.skip(Products::setColors);         // colors 필드 제외
        });
        return modelMapper.map(productDto, Products.class);
    }
    // public ProductDocument productToProductDocument(Products products) {
    //     return modelMapper.map(products, ProductDocument.class);
    // }

    public void insertProduct(ProductDetailDTO productRequest) {
        Products products = productDtoToProduct(productRequest);
        products = productRepository.save(products);

        productSizesRepository.deleteExistingSizes(products.getProductId());

        List<ProductSizes> savedSizes = new ArrayList<>();
        for (String size : productRequest.getSizes()) {
            ProductSizes sizeEntity = new ProductSizes();
            sizeEntity.setProduct(products);
            sizeEntity.setSizeName(size);
            sizeEntity = productSizesRepository.save(sizeEntity);
            savedSizes.add(sizeEntity);
        }

        productColorsRepository.deleteExistingColors(products.getProductId());
        // 새 색상 저장
        List<ProductColors> savedColors = new ArrayList<>();
        for (String color : productRequest.getColors()) {
            ProductColors colorEntity = new ProductColors();
            colorEntity.setProduct(products);
            colorEntity.setColorName(color);
            colorEntity = productColorsRepository.save(colorEntity);
            savedColors.add(colorEntity);
        }

        // 새로 재고 정보 저장
        for (ProductSizes size : savedSizes) {
            for (ProductColors color : savedColors) {
                // 새 ProductStocks 객체 생성
                ProductStocks productStocks = new ProductStocks();
                productStocks.setProduct(products);
                productStocks.setSize(size);
                productStocks.setColor(color);

                int stockQuantity = 0;
                for (ProductStockDto stockDto : productRequest.getProductStocks()) {
                    if (stockDto.getSizeName().equals(size.getSizeName()) && stockDto.getColorName().equals(color.getColorName())) {
                        stockQuantity = stockDto.getStockQuantity(); // 해당 수량을 가져옴
                        break; // 찾으면 더 이상 반복할 필요 없음
                    }
                } 
                productStocks.setStockQuantity(stockQuantity);

                // ProductStocks 저장
                productStocksRepository.save(productStocks);
            }
        }

        // ProductDocument productDocument = productToProductDocument(products);
        // productSearchRepository.save(productDocument);

        // TODO: Redis 캐싱
        // String productKey = "product:" + event.getProductId();
        // redisTemplate.opsForValue().set(productKey, products, Duration.ofDays(30));
    }

    public void updateProduct(int id, ProductDetailDTO productDto) {
        // 기존 상품 조회
        Products products = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ProductDto -> Product Model로 업데이트
        products.setName(productDto.getName());
        products.setPrice(productDto.getPrice());
        products.setDescription(productDto.getDescription());

        // 상품 수정 로직
        products = productRepository.save(products);
        // productSearchRepository.save(productToProductDocument(products));

        // TODO: Redis 캐싱
        // String productKey = "product:" + event.getProductId();
        // redisTemplate.opsForValue().set(productKey, products, Duration.ofDays(30));
    }

    public void deleteProduct(int id) {
        // 상품 삭제 로직
        Products products = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        productRepository.delete(products);
        // productSearchRepository.delete(productToProductDocument(products));
    }
}
