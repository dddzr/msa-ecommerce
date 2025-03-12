package com.example.product_service.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ProductSizes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sizeId; // 사이즈 ID
    private String sizeName; // 사이즈 이름 (예: S, M, L, XL)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    // 생성 일시
    @CreationTimestamp
    private LocalDateTime createdAt;

    // 업데이트 일시
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}