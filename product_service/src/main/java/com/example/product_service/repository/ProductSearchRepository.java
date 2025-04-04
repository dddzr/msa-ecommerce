package com.example.product_service.repository;

import com.example.product_service.dto.ProductDTO;
import com.example.product_service.entity.ProductDocument;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Integer> {
    // 커스텀 쿼리 작성 가능
    List<ProductDTO> findByName(String name);

    @Query("{\"match_all\": {}}")
    List<ProductDTO> getAllProducts();

    Optional<ProductDocument> findById(int id);

    @Query("{\"bool\": {\"should\": [{\"match\": {\"name\": \"?0\"}}, {\"match_all\": {}}]}}")
    Page<ProductDTO> findByNameOrAll(String keyword, Pageable pageable);
    
}
