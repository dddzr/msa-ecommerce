package com.example.user_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String username;
    private String email;
    private String nickname;
    private String password;
    private String profile_picture;
    private String createdAt;
    private String updatedAt;
    private String status;
    private String lastLogin;
    private Integer roleId;
    //private Set<Role> roles; // User와 관련된 역할

    // Getters and setters
}
