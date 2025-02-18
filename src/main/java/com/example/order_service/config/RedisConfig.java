package com.example.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.order_service.dto.response.OrderDetail;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, OrderDetail> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, OrderDetail> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // 키와 값의 직렬화 설정
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // // 값은 Object로 직렬화
        // template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(OrderDetail.class));
        
        return template;
    }
}
