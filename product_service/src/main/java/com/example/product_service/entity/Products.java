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

    private String name; // 상품명
    private String description; // 상품 설명
    private BigDecimal price; // 상품 가격
    private String category; // 상품 카테고리
    private String brand; // 브랜드명
    private String imageUrl; // 상품 대표 이미지 URL
    private boolean available; // 상품 구매 가능 여부

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference 
    private List<ProductStocks> stocks; // 상품별 색상, 사이즈, 재고 관리
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference 
    private List<ProductColors> colors = new ArrayList<>(); 
 
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference 
    private List<ProductSizes> sizes = new ArrayList<>();

    private int reviewCount; // 상품 리뷰 수

    private BigDecimal discountRate; // 할인율 (0~100)
    private BigDecimal discountPrice; // 할인 금액

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성 일시    

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 업데이트 일시

    // Getters and Setters
}