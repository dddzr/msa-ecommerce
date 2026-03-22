package com.example.product_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.product_service.event.OrderCancelEvent;
import com.example.product_service.event.OrderCreatedEvent;
import com.example.product_service.entity.ProductVariant;
import com.example.product_service.repository.ProductRepository;
import com.example.product_service.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockManagementService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public void deductStock(OrderCreatedEvent orderEvent) {
        updateStock(orderEvent.getProductId(), orderEvent.getVariantId(),
                orderEvent.getQuantity(), "DECREASE");
    }

    public void restoreStock(OrderCancelEvent orderEvent) {
        updateStock(orderEvent.getProductId(), orderEvent.getVariantId(),
                orderEvent.getQuantity(), "INCREASE");
    }

    public void updateStock(int productId, int variantId, int quantity, String stockOperation) {
        if (variantId <= 0) {
            return;
        }
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<ProductVariant> optionalVariant = productVariantRepository.findById(variantId);
        if (optionalVariant.isEmpty()) {
            return;
        }
        ProductVariant variant = optionalVariant.get();
        if (variant.getProduct() == null || variant.getProduct().getProductId() != productId) {
            throw new RuntimeException("Variant does not belong to product");
        }

        int availableQuantity = variant.getStockQuantity();
        int updatedStock = availableQuantity;
        if ("INCREASE".equals(stockOperation)) {
            updatedStock += quantity;
        } else if ("DECREASE".equals(stockOperation)) {
            updatedStock -= quantity;
        }
        variant.setStockQuantity(updatedStock);
        productVariantRepository.save(variant);
    }

    public void publishOutOfStockEvent(int orderId, int userId, String reason) {
        OrderCancelEvent event = new OrderCancelEvent();
        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setReason(reason);
    }
}
