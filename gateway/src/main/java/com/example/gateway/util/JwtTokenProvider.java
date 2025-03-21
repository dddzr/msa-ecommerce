package com.example.gateway.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private final String SECRET_KEY = readSecretKey(); // 환경 변수로 관리 권장
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간

    // JWT 생성
    @SuppressWarnings("deprecation")
    public String createToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                   .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                   .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(SECRET_KEY.getBytes())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();  // 전체 claims 반환
    }    

    // JWT에서 사용자 이름(Subject) 추출
    @SuppressWarnings("deprecation")
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(SECRET_KEY.getBytes())
                   .build()
                   .parseClaimsJws(token)
                   .getBody()  // 전체 claims 반환
                   .getSubject();
    }

    // JWT에서 사용자 이름 추출
    @SuppressWarnings("deprecation")
    public List<String> getRolesFromToken(String token) {
        Claims claims = extractClaims(token); // JWT에서 claims를 추출하는 메서드
        return claims.get("roles", List.class); // Role 정보 추출
    }

    // JWT 유효성 검증
    @SuppressWarnings("deprecation")
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token); // JWT의 형식(Header.Payload.Signature 형태인지) & 서명 & 만료 검증       

            return true;
        // } catch (ExpiredJwtException e) {
        //     System.out.println("❌ 만료된 토큰입니다!");
        // } catch (MalformedJwtException e) {
        //     System.out.println("❌ 잘못된 형식의 토큰입니다!");
        // } catch (SignatureException e) {
        //     System.out.println("❌ 서명이 올바르지 않은 토큰입니다!");
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
