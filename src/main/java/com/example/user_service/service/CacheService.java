package com.example.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /*
        refresh_token:{userId}	
            - 조회가 쉬움 (userId로 바로 찾음)
            - 하나의 Refresh Token만 유지        
            - 다중 로그인 불가능
            - 보안 위험 (userId로 예측 가능)
        refresh_token:{refreshToken}	
            - 다중 로그인 지원
            - Refresh Token 개별 관리 가능
            - 보안성 강화 (토큰이 없으면 유저 못 찾음)	
            - 조회할 때 Refresh Token이 필요
     */
    public void saveRefreshToken(String username, String refreshToken){
        String key = "refreshToken:" + username;
        redisTemplate.opsForValue().set(key, refreshToken);
    }

    public String getRefreshToken(String username) {
        String key = "refreshToken:" + username;
        return redisTemplate.opsForValue().get(key);
    }
}
