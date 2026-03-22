package com.example.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.product_service.entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
}
