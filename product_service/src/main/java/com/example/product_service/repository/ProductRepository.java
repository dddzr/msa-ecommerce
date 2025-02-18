package com.example.product_service.repository;
import com.example.product_service.model.Products;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Products, Integer> {
    
    Optional<Products> findById(int id);

    @Query("SELECT p FROM Products p LEFT JOIN FETCH p.stocks s LEFT JOIN FETCH s.color c LEFT JOIN FETCH s.size sz WHERE p.productId = :id")
    Optional<Products> findProductWithDetails(@Param("id") int id);
}