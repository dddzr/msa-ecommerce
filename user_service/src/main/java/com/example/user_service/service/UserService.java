package com.example.user_service.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user_service.dto.UserProfileRequest;
import com.example.user_service.dto.UserProfileResponse;
import com.example.user_service.dto.UserSignUpRequest;
import com.example.user_service.entity.User;
import com.example.user_service.exception.InvalidCredentialsException;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheService cacheService;

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    public Map<String, String> login(String username, String password) {
        // 사용자 정보 검증
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!checkPassword(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // 토큰 생성
        Map<String, String> tokens = jwtTokenProvider.generateTokens(user);

        // Refresh Token을 Redis 또는 DB에 저장 (보안상 필요)
        cacheService.saveRefreshToken(username, tokens.get("refreshToken"));
        return tokens;
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid Refresh Token");
        }

        // 저장된 Refresh Token과 비교하여 검증
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        String savedRefreshToken = cacheService.getRefreshToken(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!refreshToken.equals(savedRefreshToken)) {
            throw new InvalidCredentialsException("Refresh Token mismatch");
        }

        // 토큰 생성
        Map<String, String> tokens = jwtTokenProvider.generateTokens(user);
        // refresh Token을 캐시에 저장
        cacheService.saveRefreshToken(username, tokens.get("refreshToken"));
        return tokens;
    }

    public boolean signUp(UserSignUpRequest signUpRequest) {
        // 1. 이미 존재하는 사용자명이나 이메일이 있는지 확인
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = encodePassword(signUpRequest.getPassword());

        // 3. User 엔티티 생성
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setNickname(signUpRequest.getNickname());
        user.setPassword(encodedPassword);  // 암호화된 비밀번호 저장
        user.setRoleId(2);  // 기본적인 역할 설정 (추가 설정 필요시 수정)

        // 4. 사용자 정보 저장
        try {
            // 사용자 정보 저장 (업데이트 또는 신규 저장)
            userRepository.save(user);
            return true;  // 성공적으로 저장된 경우
        } catch (Exception e) {
            // 예외 발생 시 실패 처리
            return false;
        }
    }

    public void assignRole(String username, String role) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'assignRole'");
    }

    public UserProfileResponse getUserProfile(Integer id) {
        Optional<User> user = userRepository.findByUserIdCustom(id);
        UserProfileResponse response = new UserProfileResponse();
        user.ifPresent(u -> {
            response.setId(u.getUserId());
            response.setUsername(u.getUsername());
            response.setEmail(u.getEmail());
        });
        return response;
    }

    public boolean UpdateUserProfile(UserProfileRequest userProfileRequest) {
        String encodedPassword = encodePassword(userProfileRequest.getPassword());

        User user = new User();
        user.setUserId(userProfileRequest.getId());
        user.setUsername(userProfileRequest.getUsername());
        user.setEmail(userProfileRequest.getEmail());
        user.setNickname(userProfileRequest.getNickname());
        user.setPassword(encodedPassword);  // 암호화된 비밀번호 저장
        user.setRoleId(2);  // 기본적인 역할 설정 (추가 설정 필요시 수정)
        userRepository.save(user);

        try {
            // 사용자 정보 저장 (업데이트 또는 신규 저장)
            userRepository.save(user);
            return true;  // 성공적으로 저장된 경우
        } catch (Exception e) {
            // 예외 발생 시 실패 처리
            return false;
        }
    }

    public Optional<User> getUserByUserName(String username) {
        return userRepository.findByUsername(username);
    }
}
