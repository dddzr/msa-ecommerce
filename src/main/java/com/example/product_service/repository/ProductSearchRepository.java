// package com.example.product_service.repository;

// import com.example.product_service.dto.ProductRequest;
// import com.example.product_service.entity.ProductDocument;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.data.elasticsearch.annotations.Query;
// import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

// public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Integer> {
//     // 커스텀 쿼리 작성 가능
//     List<ProductRequest> findByName(String name);

//     @Query("{\"match_all\": {}}")
//     List<ProductRequest> getAllProducts();

//     Optional<ProductDocument> findById(int id);
    
// }
