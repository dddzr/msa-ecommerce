package com.example.product_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/**
 * SKU(ProductVariant)가 선택한 옵션 값 조합 (한 옵션 타입당 값 1개 — 애플리케이션에서 검증).
 */
@Entity
@Table(
        name = "product_variant_option_values",
        uniqueConstraints = @UniqueConstraint(columnNames = {"variant_id", "option_value_id"})
                   )
@Data
public class ProductVariantOptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    @JsonBackReference
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_value_id", nullable = false)
    private ProductOptionValue optionValue;
}
