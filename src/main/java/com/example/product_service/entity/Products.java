package com.example.product_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "productId")
public class Products {    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId; // 상품 ID
    // private int sellerId; // 판매자 ID

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String brand;
    private String imageUrl;
    private boolean available;

    /** 동적 옵션 정의 (색상, 사이즈, 소재 …) */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductOption> options = new ArrayList<>();

    /** SKU(재고·옵션 조합) */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductVariant> variants = new ArrayList<>();

    private int reviewCount;
    private BigDecimal discountRate;
    private BigDecimal discountPrice;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
