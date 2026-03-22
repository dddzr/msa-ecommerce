package com.example.product_service.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.product_service.entity.Products;
import com.example.product_service.dto.cache.CachedProduct;

@Service
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);
    private static final String SERVICE_PREFIX = "product-service";
    // private static final String CACHE_VERSION = "v1";

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String keyCachedProduct(int productId) {
        // {service}:[{version}]:{domain}[:{type}]:{id}
        // ex) product-service:v2:product:cached:123
        return SERVICE_PREFIX + ":product:" + productId;
    }

    public CachedProduct getCachedProduct(int productId) {
        return (CachedProduct) redisTemplate.opsForValue().get(keyCachedProduct(productId));
    }

    public Map<Integer, CachedProduct> getCachedProducts(List<Integer> productIds) {
        Map<Integer, CachedProduct> result = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) {
            return result;
        }
        for (Integer id : productIds) {
            if (id == null) continue;
            CachedProduct cached = getCachedProduct(id);
            if (cached != null) {
                result.put(id, cached);
            }
        }
        return result;
    }

    public void saveCachedProduct(int productId, CachedProduct cachedProduct) {
        redisTemplate.opsForValue().set(keyCachedProduct(productId), cachedProduct, DEFAULT_TTL);
    }

    /**
     * 배치 캐시 저장 (파이프라이닝)
     */
    public void saveCachedProducts(Map<Integer, CachedProduct> productsById) {
        saveCachedProducts(productsById, 200);
    }

    /**
     * 배치 캐시 저장 (파이프라이닝) - maxBatchSize 만큼 끊어서 실행. 네트워크 RTT를 줄이기 위함.
     * @param maxBatchSize 한 번의 파이프라인에 태울 최대 개수 (권장: 100~500)
     */
    public void saveCachedProducts(Map<Integer, CachedProduct> productsById, int maxBatchSize) {
        if (productsById == null || productsById.isEmpty()) {
            return;
        }
        if (maxBatchSize <= 0) {
            throw new IllegalArgumentException("maxBatchSize must be > 0");
        }

        @SuppressWarnings("unchecked")
        RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        @SuppressWarnings("unchecked")
        RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        int count = 0;
        Map<Integer, CachedProduct> batch = new HashMap<>(Math.min(maxBatchSize, productsById.size()));

        for (Map.Entry<Integer, CachedProduct> entry : productsById.entrySet()) {
            Integer productId = entry.getKey();
            CachedProduct cachedProduct = entry.getValue();
            if (productId == null || cachedProduct == null) continue;

            batch.put(productId, cachedProduct);
            count++;

            if (count >= maxBatchSize) {
                executePipelineBatch(batch, keySerializer, valueSerializer);
                batch.clear();
                count = 0;
            }
        }

        if (!batch.isEmpty()) {
            executePipelineBatch(batch, keySerializer, valueSerializer);
        }
    }

    private void executePipelineBatch(
            Map<Integer, CachedProduct> batch,
            RedisSerializer<String> keySerializer,
            RedisSerializer<Object> valueSerializer
    ) {
        redisTemplate.executePipelined((RedisConnection connection) -> {
            for (Map.Entry<Integer, CachedProduct> entry : batch.entrySet()) {
                Integer productId = entry.getKey();
                CachedProduct cachedProduct = entry.getValue();

                byte[] keyBytes = keySerializer.serialize(keyCachedProduct(productId));
                byte[] valueBytes = valueSerializer.serialize(cachedProduct);

                // SET + EXPIRE (TTL) 를 파이프라인으로 처리
                connection.stringCommands().set(keyBytes, valueBytes);
                connection.keyCommands().expire(keyBytes, DEFAULT_TTL.getSeconds());
            }
            return null;
        });
    }

    /**
     * Command 쪽에서 상품 변경 시, 조회 캐시를 무효화하는 용도.
     */
    public void evictCachedProduct(int productId) {
        redisTemplate.delete(keyCachedProduct(productId));
    }

    /**
     * (기존 코드 호환) 상품 변경 시 캐시 무효화로 대체 권장.
     * 현재는 조회 응답 캐시와 타입이 달라 stale/deserialize 이슈를 만들 수 있어 저장은 하지 않습니다.
     */
    @Deprecated
    public void saveProduct(int productId, Products product) {
        evictCachedProduct(productId);
    }

    @Deprecated
    public void evictProduct(int productId) {
        evictCachedProduct(productId);
    }
}
