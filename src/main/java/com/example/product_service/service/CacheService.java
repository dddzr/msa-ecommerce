package com.example.product_service.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.product_service.dto.cache.CachedProduct ;
import com.example.product_service.entity.Products;

@Service
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public CachedProduct getProduct(int productId) {
        String key = "product-service:product:" + productId;
        return (CachedProduct) redisTemplate.opsForValue().get(key);
    }

    public void saveProduct(int productId, Products product) {
        String key = "product-service:product:" + productId;
        redisTemplate.opsForValue().set(key, product, Duration.ofMinutes(10));
    }

    public void evictProduct(int productId) {
        String key = "product-service:product:" + productId;
        redisTemplate.delete(key);
    }
}
