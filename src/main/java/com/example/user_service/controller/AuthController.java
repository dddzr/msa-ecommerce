package com.example.user_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, String> tokens = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
    
            saveRefreshToken(tokens.get("refreshToken"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("accessToken", tokens.get("accessToken")); // accessToken, refreshToken 포함
    
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException | BadCredentialsException e) { //AuthenticationException
            // 로그인 실패 (아이디 없음 or 비밀번호 틀림)
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
    
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            // 기타 서버 오류
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "서버 오류 발생");
    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    

    /*
     * JWT의 원래 의도는 Stateless 인증을 유지하는 것. 
     * 클라이언트에 JWT를 저장하고, 요청 시 Authorization 헤더로 전달하는 방식을 사용합니다.
     * 그러나 보안이 우선인 경우, Redis나 세션을 통해 관리할 수도 있습니다.
     */

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        Map<String, String> tokens = userService.refreshAccessToken(refreshToken);

        saveRefreshToken(tokens.get("refreshToken"));
        
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", tokens.get("accessToken")); // accessToken, refreshToken 포함
        return ResponseEntity.ok(response);
    }

    HttpHeaders saveRefreshToken(String refreshToken) {
        //refreshToken은 HttpOnly쿠키에 저장
        String cookieHeader = "refreshToken=" + refreshToken + "; Path=/; HttpOnly; Secure";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", cookieHeader);
        return headers;
    }

}
