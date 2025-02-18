package com.example.user_service.dto;

import lombok.Data;

@Data
public class UserSignUpRequest {
    private String username;
    private String email;
    private String nickname;
    private String password;

    // Getters and setters
}
