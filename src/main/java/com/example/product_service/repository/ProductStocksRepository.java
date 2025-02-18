package com.example.product_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.product_service.model.ProductStocks;

import jakarta.transaction.Transactional;

public interface ProductStocksRepository extends JpaRepository<ProductStocks, Integer> {

    @Query("SELECT ps FROM ProductStocks ps " +
       "JOIN ps.product p " +
       "JOIN ps.size s " +
       "JOIN ps.color c " +
       "WHERE p.productId = :productId " +
       "AND s.sizeId = :sizeId " +
       "AND c.colorId = :colorId")
    Optional<ProductStocks> findStockQuantity(@Param("productId") int productId,
                                         @Param("colorId") int colorId,
                                         @Param("sizeId") int sizeId);

    // 기존 상품 재고 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductStocks ps WHERE ps.product.id = :productId")
    void deleteExistingStock(@Param("productId") Integer productId);
}
