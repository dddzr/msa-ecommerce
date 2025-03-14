package com.example.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.util.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CacheService cacheService;

    @Test
    void login_Success() {
        // Given (준비)
        String username = "testUser";
        String password = "testPassword";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword"); // 암호화된 비밀번호 가정

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateTokens(mockUser)).thenReturn(Map.of("accessToken", "mockAccessToken"));

        // When (실행)
        Map<String, String> result = userService.login(username, password);

        // Then (검증)
        assertNotNull(result);
        assertEquals("mockAccessToken", result.get("accessToken"));
    }

    @Test
    void login_UserNotFound() {
        // Given
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.login(username, "password"));
    }

    @Test
    void login_WrongPassword() {
        // Given
        String username = "testUser";
        String password = "wrongPassword";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> userService.login(username, password));
    }
}
