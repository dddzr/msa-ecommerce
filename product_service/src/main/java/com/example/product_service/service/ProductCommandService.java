package com.example.product_service.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.ProductDetailDTO.ProductStockDto;
import com.example.product_service.entity.ProductColors;
// import com.example.product_service.entity.ProductDocument;
import com.example.product_service.entity.ProductSizes;
import com.example.product_service.entity.ProductStocks;
import com.example.product_service.entity.Products;
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

    public ProductDetailDTO productToProductDetailDTO(Products product) {
        ModelMapper modelMapper = new ModelMapper();
        
        // 기본 필드 매핑
        ProductDetailDTO productDto = modelMapper.map(product, ProductDetailDTO.class);

        // 추가 매핑 (사이즈, 색상, 재고)
        productDto.setSizes(
            product.getSizes().stream()
                .map(ProductSizes::getSizeName)
                .collect(Collectors.toList())
        );

        productDto.setColors(
            product.getColors().stream()
                .map(ProductColors::getColorName)
                .collect(Collectors.toList())
        );

        productDto.setProductStocks(
            product.getStocks().stream()
                .map(stock -> new ProductDetailDTO.ProductStockDto(
                    stock.getSize().getSizeId(), stock.getSize().getSizeName(), stock.getColor().getColorId(), stock.getColor().getColorName(), stock.getStockQuantity()
                    ))
                .collect(Collectors.toList())
        );

        return productDto;
    }

    // public ProductDocument productToProductDocument(Products products) {
    //     return modelMapper.map(products, ProductDocument.class);
    // }


    public ProductDetailDTO insertProduct(ProductDetailDTO productRequest) {
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
        List<ProductColors> savedColors = new ArrayList<>();
        for (String color : productRequest.getColors()) {
            ProductColors colorEntity = new ProductColors();
            colorEntity.setProduct(products);
            colorEntity.setColorName(color);
            colorEntity = productColorsRepository.save(colorEntity);
            savedColors.add(colorEntity);
        }

        // 재고 정보 저장
        List<ProductStocks> savedStocks = new ArrayList<>();
        for (ProductSizes size : savedSizes) {
            for (ProductColors color : savedColors) {
                ProductStocks stock = new ProductStocks();
                stock.setProduct(products);
                stock.setSize(size);
                stock.setColor(color);

                int stockQuantity = productRequest.getProductStocks().stream()
                .filter(stockDto -> stockDto.getSizeName().equals(size.getSizeName()) &&
                                    stockDto.getColorName().equals(color.getColorName()))
                .map(ProductStockDto::getStockQuantity)
                .findFirst()
                .orElse(0);

                stock.setStockQuantity(stockQuantity);
                savedStocks.add(productStocksRepository.save(stock));
            }
        }

        // ProductDocument productDocument = productToProductDocument(products);
        // productSearchRepository.save(productDocument);

        // TODO: Redis 캐싱
        // String productKey = "product:" + event.getProductId();
        // redisTemplate.opsForValue().set(productKey, products, Duration.ofDays(30));

        
        products.setSizes(savedSizes);
        products.setColors(savedColors);
        products.setStocks(savedStocks);
        return productToProductDetailDTO(products);
    }

    public ProductDetailDTO updateProduct(int id, ProductDetailDTO productDto) {
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

        return productToProductDetailDTO(products);
    }

    public void deleteProduct(int id) {
        // 상품 삭제 로직
        Products products = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        productRepository.delete(products);
        // productSearchRepository.delete(productToProductDocument(products));
    }
}
