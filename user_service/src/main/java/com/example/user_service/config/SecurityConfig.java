package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 기본 로그인 폼 비활성화
        http.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .formLogin(form -> form.disable()) // 기본 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()); // HTTP Basic 인증 비활성화

        // 인증/인가 기능 없이 모든 요청을 허용
        http.authorizeHttpRequests((requests) -> requests
            .anyRequest().permitAll()
        ); // 모든 요청을 허용

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
