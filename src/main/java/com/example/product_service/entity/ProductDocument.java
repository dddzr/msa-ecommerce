// package com.example.product_service.entity;

// import java.math.BigDecimal;
// import java.util.List;

// import org.springframework.data.annotation.Id;
// import org.springframework.data.elasticsearch.annotations.Document;
// import org.springframework.data.elasticsearch.annotations.Field;
// import org.springframework.data.elasticsearch.annotations.FieldType;

// import lombok.Data;

// // Elasticsearch에 저장될 필드만 포함
// @Document(indexName = "products")
// @Data
// public class ProductDocument {
    
//     @Id
//     private int productId; // 상품 ID (주로 검색에서 사용)

//     @Field(type = FieldType.Text)
//     private String name; // 상품명

//     @Field(type = FieldType.Double)
//     private BigDecimal price; // 상품 가격

//     @Field(type = FieldType.Keyword)
//     private String category; // 상품 카테고리

//     @Field(type = FieldType.Keyword)
//     private String brand; // 브랜드명

//     @Field(type = FieldType.Boolean)
//     private boolean available; // 상품 구매 가능 여부

//     @Field(type = FieldType.Keyword)
//     private List<String> colors; // 상품 색상 옵션

//     @Field(type = FieldType.Double)
//     private double averageRating; // 상품 평점

//     @Field(type = FieldType.Integer)
//     private int reviewCount; // 상품 리뷰 수

//     @Field(type = FieldType.Double)
//     private BigDecimal discountRate; // 할인율

//     @Field(type = FieldType.Double)
//     private BigDecimal discountPrice; // 할인 금액
// }
