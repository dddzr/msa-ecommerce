package com.example.user_service.dto;
import lombok.Data;

@Data
public class UserProfileRequest {
    private Integer id;
    private String username;
    private String nickname;
    private String email;
    private String password;
}