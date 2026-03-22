package com.example.product_service.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.product_service.dto.ProductDetailDTO;
import com.example.product_service.dto.ProductDetailDTO.ProductOptionDto;
import com.example.product_service.dto.ProductDetailDTO.VariantStockDto;
import com.example.product_service.dto.VariantStockMapper;
import com.example.product_service.entity.ProductDocument;
import com.example.product_service.entity.ProductOption;
import com.example.product_service.entity.ProductOptionValue;
import com.example.product_service.entity.ProductVariant;
import com.example.product_service.entity.ProductVariantOptionValue;
import com.example.product_service.entity.Products;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.repository.ProductSearchRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 상품 CUD(생성·수정·삭제) 전담 서비스.
 * <p>
 * 옵션 차원은 {@link ProductOption#optionKey} 로만 구분하며, COLOR/SIZE 등 이름은 요청 본문에서 정의합니다.
 */
@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final CacheService cacheService;
    private final ModelMapper modelMapper;

    /**
     * 등록용: DTO → 엔티티 기본 필드만 매핑.
     */
    public Products productDtoToProduct(ProductDetailDTO productDto) {
        ModelMapper mm = new ModelMapper();
        mm.typeMap(ProductDetailDTO.class, Products.class).addMappings(mapper -> {
            mapper.skip(Products::setOptions);
            mapper.skip(Products::setVariants);
        });
        return mm.map(productDto, Products.class);
    }

    /**
     * 조회/응답용: 엔티티 → 상세 DTO (옵션 정의·SKU 행은 직접 조립).
     */
    public ProductDetailDTO productToProductDetailDTO(Products product) {
        ModelMapper mm = new ModelMapper();
        mm.typeMap(Products.class, ProductDetailDTO.class).addMappings(mapper -> {
            mapper.skip(ProductDetailDTO::setProductStocks);
            mapper.skip(ProductDetailDTO::setDiscountRate);
            mapper.skip(ProductDetailDTO::setDiscountPrice);
            mapper.skip(ProductDetailDTO::setOptions);
        });
        ProductDetailDTO dto = mm.map(product, ProductDetailDTO.class);
        dto.setOptions(buildOptionDtos(product));
        dto.setProductStocks(buildVariantStockRows(product));
        return dto;
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

    private List<VariantStockDto> buildVariantStockRows(Products product) {
        if (product.getVariants() == null) {
            return List.of();
        }
        return product.getVariants().stream()
                .map(v -> VariantStockMapper.toRow(v, product))
                .collect(Collectors.toList());
    }

    public ProductDocument productToProductDocument(Products products) {
        return modelMapper.map(products, ProductDocument.class);
    }

    /**
     * 업로드된 이미지들을 순서대로 저장하고, DB에 넣을 URL 문자열을 만든다.
     *
     * @return 세미콜론(;)으로 이어 붙인 파일명(또는 경로) 조각들. 선행 {@code ';'} 포함.
     */
    String handleFiles(List<MultipartFile> images) {
        int fileSeq = 0;
        String imageUrl = "";
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                if (!file.isEmpty()) {
                    String savedUrl = saveFileAtServer(fileSeq, file);
                    fileSeq++;
                    imageUrl += ';' + savedUrl;
                }
            }
        }
        return imageUrl;
    }

    /** 로컬 디렉터리 {@code C:/upload} 에 저장 후 저장 파일명 반환. */
    String saveFileAtServer(int fileSeq, MultipartFile file) {
        String uploadDir = "C:/upload";
        String originalFilename = file.getOriginalFilename();
        String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        File destFile = new File(uploadDir + "/" + storedFilename);
        try {
            file.transferTo(destFile);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return storedFilename;
    }

    /**
     * 상품 신규 등록: 이미지 → 상품 기본 저장 → 요청의 옵션 정의별 {@link ProductOption} 생성 →
     * 재고 행마다 SKU 조합 생성. SKU 코드는 {@code P{productId}-{valueId}-...} (displayOrder 순).
     */
    @Transactional
    public ProductDetailDTO insertProduct(ProductDetailDTO productRequest, List<MultipartFile> images) {
        productRequest.setImageUrl(handleFiles(images));

        Products products = productDtoToProduct(productRequest);
        products = productRepository.save(products);

        List<ProductOptionDto> optionDefs = productRequest.getOptions() != null
                ? productRequest.getOptions()
                : List.of();
        List<ProductOptionDto> sortedDefs = optionDefs.stream()
                .sorted(Comparator.comparingInt(ProductOptionDto::getDisplayOrder))
                .collect(Collectors.toList());

        for (ProductOptionDto def : sortedDefs) {
            ProductOption opt = new ProductOption();
            opt.setProduct(products);
            opt.setOptionKey(def.getOptionKey());
            opt.setDisplayName(def.getDisplayName() != null ? def.getDisplayName() : def.getOptionKey());
            opt.setDisplayOrder(def.getDisplayOrder());
            List<String> labels = def.getValueLabels() != null ? def.getValueLabels() : List.of();
            for (int i = 0; i < labels.size(); i++) {
                ProductOptionValue v = new ProductOptionValue();
                v.setOption(opt);
                v.setValueLabel(labels.get(i));
                v.setSortOrder(i);
                opt.getValues().add(v);
            }
            products.getOptions().add(opt);
        }

        products = productRepository.save(products);

        List<ProductOption> orderedOptions = products.getOptions().stream()
                .sorted(Comparator.comparingInt(ProductOption::getDisplayOrder))
                .collect(Collectors.toList());

        List<VariantStockDto> stockRows = productRequest.getProductStocks() != null
                ? productRequest.getProductStocks()
                : List.of();

        for (VariantStockDto row : stockRows) {
            Map<String, String> selections = row.getSelections();
            ProductVariant variant = new ProductVariant();
            variant.setProduct(products);
            variant.setStockQuantity(row.getStockQuantity());
            variant.setPriceAdjustment(BigDecimal.ZERO);

            List<Integer> skuIds = new ArrayList<>();
            boolean ok = true;
            for (ProductOption opt : orderedOptions) {
                String key = opt.getOptionKey();
                String label = selections != null ? selections.get(key) : null;
                ProductOptionValue val = findValueByLabel(opt, label);
                if (val == null) {
                    ok = false;
                    break;
                }
                skuIds.add(val.getValueId());
                ProductVariantOptionValue link = new ProductVariantOptionValue();
                link.setVariant(variant);
                link.setOptionValue(val);
                variant.getOptionSelections().add(link);
            }
            if (!ok) {
                continue;
            }
            variant.setSkuCode(buildSkuCode(products.getProductId(), skuIds));
            products.getVariants().add(variant);
        }

        products = productRepository.save(products);

        ProductDocument productDocument = productToProductDocument(products);
        productSearchRepository.save(productDocument);
        cacheService.evictCachedProduct(products.getProductId());

        Products reloaded = productRepository.findProductWithDetails(products.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found after save"));
        return productToProductDetailDTO(reloaded);
    }

    private static String buildSkuCode(int productId, List<Integer> valueIdsInOptionOrder) {
        StringBuilder sb = new StringBuilder("P").append(productId);
        for (int vid : valueIdsInOptionOrder) {
            sb.append('-').append(vid);
        }
        return sb.toString();
    }

    private static ProductOptionValue findValueByLabel(ProductOption option, String label) {
        if (option.getValues() == null || label == null) {
            return null;
        }
        return option.getValues().stream()
                .filter(v -> label.equals(v.getValueLabel()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 상품 일부 필드 수정(이름·가격·설명). 옵션/SKU 구조 변경은 미구현.
     */
    public ProductDetailDTO updateProduct(int id, ProductDetailDTO productDto) {
        Products products = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        products.setName(productDto.getName());
        products.setPrice(productDto.getPrice());
        products.setDescription(productDto.getDescription());

        products = productRepository.save(products);
        cacheService.evictCachedProduct(products.getProductId());

        Products reloaded = productRepository.findProductWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productToProductDetailDTO(reloaded);
    }

    public void deleteProduct(int id) {
        Products products = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.delete(products);
        cacheService.evictCachedProduct(id);
    }
}
