package com.example.order_service.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrderItems {    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;
    private int productId;
    private int colorId;
    private int sizeId;
    private int quantity;
    private BigDecimal price; // 구매 당시 가격
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference // 역방향 참조를 무시
    private Orders order; // 주문 ID (Orders 테이블과의 외래 키)
}