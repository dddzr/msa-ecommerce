package com.example.product_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "stockId")
public class ProductStocks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stockId; // 재고 ID

    private int stockQuantity; // 재고 수량

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Products product; // 연관된 상품

    @ManyToOne
    @JoinColumn(name = "color_id")
    private ProductColors color; // 연관된 색상

    @ManyToOne
    @JoinColumn(name = "size_id")
    private ProductSizes size; // 연관된 사이즈

    // 생성 일시
    @CreationTimestamp
    private LocalDateTime createdAt;

    // 업데이트 일시
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}