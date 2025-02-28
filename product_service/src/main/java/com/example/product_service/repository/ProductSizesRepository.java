package com.example.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.product_service.entity.ProductSizes;

import jakarta.transaction.Transactional;

public interface ProductSizesRepository extends JpaRepository<ProductSizes, Integer> {

    // 기존 상품 사이즈 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductSizes ps WHERE ps.product.id = :productId")
    void deleteExistingSizes(@Param("productId") Integer productId);
}
