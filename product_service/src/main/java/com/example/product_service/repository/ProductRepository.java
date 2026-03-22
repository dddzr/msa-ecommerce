package com.example.product_service.repository;
import com.example.product_service.dto.ProductDTO;
import com.example.product_service.dto.cache.ProductCacheOptionRow;
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

    @Query("SELECT DISTINCT p FROM Products p "
            + "LEFT JOIN FETCH p.options o "
            + "LEFT JOIN FETCH o.values "
            + "LEFT JOIN FETCH p.variants v "
            + "LEFT JOIN FETCH v.optionSelections sel "
            + "LEFT JOIN FETCH sel.optionValue ov "
            + "LEFT JOIN FETCH ov.option "
            + "WHERE p.productId = :id")
    Optional<Products> findProductWithDetails(@Param("id") int id);

    /**
     * 캐시/목록용: 상품별 등장하는 옵션 값 행 (variant → 옵션 값 → optionKey).
     * variant만으로는 valueLabel·optionKey(COLOR/SIZE)를 알 수 없어 조인 깊이는 동일.
     */
    @Query("SELECT new com.example.product_service.dto.cache.ProductCacheOptionRow("
           + "p.productId, p.name, p.price, ov.valueId, ov.valueLabel, o.optionKey) "
           + "FROM Products p "
           + "LEFT JOIN p.variants v "
           + "LEFT JOIN v.optionSelections sel "
           + "LEFT JOIN sel.optionValue ov "
           + "LEFT JOIN ov.option o "
           + "WHERE p.productId = :id")
    List<ProductCacheOptionRow> findProduct(@Param("id") int id);

    @Query("SELECT new com.example.product_service.dto.cache.ProductCacheOptionRow("
           + "p.productId, p.name, p.price, ov.valueId, ov.valueLabel, o.optionKey) "
           + "FROM Products p "
           + "LEFT JOIN p.variants v "
           + "LEFT JOIN v.optionSelections sel "
           + "LEFT JOIN sel.optionValue ov "
           + "LEFT JOIN ov.option o "
           + "WHERE p.productId IN :ids")
    List<ProductCacheOptionRow> findProductList(@Param("ids") List<Integer> ids);

    Page<ProductDTO> findByNameContaining(String keyword, Pageable pageable);
}
