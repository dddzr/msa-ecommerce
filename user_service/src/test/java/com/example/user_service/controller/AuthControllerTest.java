package com.example.user_service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;


import com.example.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class) // 컨트롤러 단위 테스트
@AutoConfigureMockMvc(addFilters = false) // Spring Security 비활성화
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService; // 서비스는 Mock 처리

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환용

    @Test
    void login_Success() throws Exception {
        // Given
        String username = "testUser";
        String password = "testPassword";
        Map<String, String> tokens = Map.of("accessToken", "testAccessToken", "refreshToken", "testRefreshToken");

        given(userService.login(username, password)).willReturn(tokens);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", username, "password", password))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.accessToken").value("testAccessToken"));
    }

    @Test
    void login_Fail_UserNotFound() throws Exception {
        // Given
        String username = "unknownUser";
        String password = "testPassword";

        given(userService.login(username, password)).willThrow(new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", username, "password", password))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }

    @Test
    void login_Fail_WrongPassword() throws Exception {
        // Given
        String username = "testUser";
        String password = "wrongPassword";

        given(userService.login(username, password)).willThrow(new BadCredentialsException("비밀번호가 틀렸습니다."));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", username, "password", password))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("비밀번호가 틀렸습니다."));
    }

    @Test
    void login_Fail_ServerError() throws Exception {
        // Given
        String username = "testUser";
        String password = "testPassword";

        given(userService.login(username, password)).willThrow(new RuntimeException("서버 오류 발생"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("username", username, "password", password))))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("서버 오류 발생"));
    }
}
