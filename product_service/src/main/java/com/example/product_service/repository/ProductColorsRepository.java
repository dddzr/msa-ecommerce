package com.example.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.product_service.model.ProductColors;

import jakarta.transaction.Transactional;

public interface ProductColorsRepository extends JpaRepository<ProductColors, Integer> {

    // 기존 상품 색상 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductColors pc WHERE pc.product.id = :productId")
    void deleteExistingColors(@Param("productId") Integer productId);
}
