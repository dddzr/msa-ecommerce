package com.example.product_service.repository;
import com.example.product_service.entity.Products;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Products, Integer> {
    
    Optional<Products> findById(int id);

    @Query("SELECT p FROM Products p LEFT JOIN FETCH p.stocks s LEFT JOIN FETCH s.color c LEFT JOIN FETCH s.size sz WHERE p.productId = :id")
    Optional<Products> findProductWithDetails(@Param("id") int id);

    @Query("SELECT p.productId, p.name, p.price, " +
           "c.colorId, c.colorName, s.sizeId, s.sizeName, ps.stockQuantity " +
           "FROM Products p " +
           "LEFT JOIN p.colors c " +
           "LEFT JOIN p.sizes s " +
           "LEFT JOIN p.stocks ps ON ps.color.colorId = c.colorId AND ps.size.sizeId = s.sizeId " +
           "WHERE p.productId = :id")
    List<Object[]> findProductForCache(@Param("id") int id);


    /* pageable은 
        SELECT * FROM product
        WHERE name LIKE '%keyword%'
        ORDER BY id
        LIMIT 10 OFFSET 10; -- limit: 개수(pageSize), offset: 시작 번호 (페이지*개수) 자동계산됨.
     */
    Page<Products> findByNameContaining(String keyword, Pageable pageable);
}