package com.example.user_service.dto;
import lombok.Data;

@Data
public class UserProfileResponse {
    private Integer id;
    private String username;
    private String nickname;
    private String email;
}