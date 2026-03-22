package com.example.product_service.dto;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.product_service.dto.ProductDetailDTO.VariantStockDto;
import com.example.product_service.entity.ProductOption;
import com.example.product_service.entity.ProductOptionValue;
import com.example.product_service.entity.ProductVariant;
import com.example.product_service.entity.ProductVariantOptionValue;
import com.example.product_service.entity.Products;

/**
 * SKU(ProductVariant) ↔ API용 재고 행 변환. 옵션 차원은 {@code optionKey} 로 구분하며 표시 순서는 상품의 {@code displayOrder} 를 따름.
 */
public final class VariantStockMapper {

    private VariantStockMapper() {
    }

    public static VariantStockDto toRow(ProductVariant variant, Products product) {
        if (variant == null) {
            return new VariantStockDto(0, Map.of(), Map.of(), 0);
        }
        Map<String, Integer> valueIds = new LinkedHashMap<>();
        Map<String, String> labels = new LinkedHashMap<>();
        if (product != null && product.getOptions() != null && variant.getOptionSelections() != null) {
            List<ProductOption> ordered = product.getOptions().stream()
                    .sorted(Comparator.comparingInt(ProductOption::getDisplayOrder))
                    .toList();
            for (ProductOption opt : ordered) {
                String key = opt.getOptionKey();
                variant.getOptionSelections().stream()
                        .map(ProductVariantOptionValue::getOptionValue)
                        .filter(ov -> matchesOption(ov, key))
                        .findFirst()
                        .ifPresent(ov -> {
                            valueIds.put(key, ov.getValueId());
                            labels.put(key, ov.getValueLabel());
                        });
            }
        }
        return new VariantStockDto(variant.getVariantId(), valueIds, labels, variant.getStockQuantity());
    }

    private static boolean matchesOption(ProductOptionValue ov, String optionKey) {
        return ov != null
                && ov.getOption() != null
                && optionKey.equals(ov.getOption().getOptionKey());
    }
}
