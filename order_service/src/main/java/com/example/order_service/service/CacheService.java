package com.example.order_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.order_service.dto.cache.CachedDelivery;
import com.example.order_service.dto.cache.CachedPayment;
import com.example.order_service.dto.cache.CachedProduct;
import com.example.order_service.dto.response.OrderDetail;
import com.example.order_service.dto.response.OrderItemDetail;
import com.example.order_service.dto.response.OrderSummary;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CacheService {

    // @Autowired
    // private RedisTemplate<String, Object> redisTemplate;
    // @Autowired
    // private ObjectMapper objectMapper;

    // // 캐시에서 데이터 조회
    // public Object getFromCache(String key) {
    //     return redisTemplate.opsForValue().get(key);
    // }

    // // 캐시에 데이터 저장
    // public void saveToCache(String key, Object value) {
    //     redisTemplate.opsForValue().set(key, value);
    // }

    // // 캐시 삭제
    // public void deleteFromCache(String key) {
    //     redisTemplate.delete(key);
    // }

    // // 주문 상세 정보 - 데이터 합성하는거 서비스로 옯기고, getDelivery 이런 함수 다 따로 만들 것.
    // public OrderDetail getOrderDetail(int orderId) { // Hash-Hash 데이터 모델 (Parent-Child 관계)
    //     String orderKey = "order:" + orderId;
    //     String deliveryKey = "delivery:" + orderId;
    //     String paymentKey = "payment:" + orderId;

    //     OrderDetail orderDetail = (OrderDetail) redisTemplate.opsForValue().get(orderKey);
    //     if (orderDetail == null) {
    //         // 캐시가 없으면 DB에서 조회
    //         // 주문 데이터 조회 및 캐시 저장
    //     }
    //     for(OrderItemDetail itemDetail: orderDetail.getOrderItemDetails()){
    //         // getOrderItemDetail(itemDetail.getOrderItemId());
    //     }

    //     CachedDelivery deliveryDetail = (CachedDelivery) redisTemplate.opsForValue().get(deliveryKey);
    //     CachedPayment paymentDetail = (CachedPayment) redisTemplate.opsForValue().get(paymentKey);
    //     if (deliveryDetail == null) {
    //     }

    //     if (paymentDetail == null) {
    //     }

    //     // OrderDetail 합성
    //     orderDetail.setDelivery(deliveryDetail);
    //     orderDetail.setPayment(paymentDetail);

    //     return orderDetail;
    // }

    // public OrderItemDetail.OrderedProductInfo getOrderedProductInfo(int productId) {
    //     String productKey = "product:" + productId;

    //     OrderItemDetail.OrderedProductInfo orderedProductInfo = new OrderItemDetail.OrderedProductInfo();
    //     CachedProduct product = (CachedProduct) redisTemplate.opsForValue().get(productKey);
    //     if (product != null) {
    //         orderedProductInfo = objectMapper.convertValue(product, OrderItemDetail.OrderedProductInfo.class);
    //     }else {
    //         // 캐시가 없으면 DB에서 조회
    //         // 주문 데이터 조회 및 캐시 저장
    //     }

    //     return orderedProductInfo;
    // }

    // // 목록 하나씩 추가 (주문이 생성될 때 추가, 배송/결제 시스템에 의해 수정된다.)
    // public void addOrderToCache(OrderSummary orderSummary) {
    //     int orderId = orderSummary.getOrderId();
    //     int userId = orderSummary.getUserId();
    //     long timestamp = System.currentTimeMillis();

    //     // String orderKey = "orderDetail:" + orderId;
    //     // redisTemplate.opsForList().rightPush(orderKey, orderDetail);

    //     // 주문 목록 저장용 OrderSummary 생성
    //     // OrderSummary orderSummary = new OrderSummary();
    //     // BeanUtils.copyProperties(orderDetail, orderSummary); // 필드 복사
        
    //     // 상품별 주문 목록 필요 시
    //     // String productKey = "orderList:" + productId;
    //     // redisTemplate.opsForZSet().add(productKey, orderSummary, timestamp);
    // }
}
