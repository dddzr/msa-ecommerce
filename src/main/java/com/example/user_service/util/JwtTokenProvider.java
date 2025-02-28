package com.example.user_service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.user_service.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
    private final String SECRET_KEY = readSecretKey(); // 환경 변수로 관리 권장
    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15분
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7일
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간

    // Access & Refresh Token 발급 (Refresh 1회 사용 후 재발급)
    public Map<String, String> generateTokens(User user) {
        String username = user.getUsername();

        // 테스트 코드
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");
        roles.add("USER");

        String accessToken = createToken(username, roles, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = createToken(username, roles, REFRESH_TOKEN_EXPIRATION);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    // JWT 생성
    @SuppressWarnings("deprecation")
    public String createToken(String username, List<String> roles, Long expiration) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);//user.getRoles()

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expiration))
                   .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                   .compact();
    }

    // JWT에서 사용자 이름 추출
    @SuppressWarnings("deprecation")
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                   .setSigningKey(SECRET_KEY.getBytes())
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    // JWT 유효성 검증
    @SuppressWarnings("deprecation")
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String readSecretKey() {
        String secretKey = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(JwtTokenProvider.class.getResourceAsStream("/private_20241127.key")))) {
            secretKey = reader.readLine(); // 파일에서 첫 번째 줄을 읽음
        } catch (IOException e) {
            System.err.println("Error reading secret key file: " + e.getMessage());
        }

        return secretKey;
    }
}
