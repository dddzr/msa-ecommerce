package com.example.order_service.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    /**
     * 상품 서비스의 {@code product_variants.variant_id}. 재고 차감·조회의 단일 키.
     */
    private Integer variantId;

    /**
     * 주문 시점 옵션 라벨 스냅샷 (optionKey → 표시명). 목록/상세에서 상품 API 없이 표시용.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "option_labels_snapshot", columnDefinition = "json")
    private Map<String, String> optionLabelsSnapshot = new HashMap<>();

    private int quantity;
    private BigDecimal price;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Orders order;
}
