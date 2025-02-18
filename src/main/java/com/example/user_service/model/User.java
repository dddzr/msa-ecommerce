package com.example.user_service.model;

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
    private Integer user_id;
    private String username;
    private String email;
    private String nickname;
    private String password;
    private String profile_picture;
    private String created_at;
    private String updated_at;
    private String status;
    private String last_login;
    private Integer role_id;
    //private Set<Role> roles; // User와 관련된 역할

    // Getters and setters
}
