package com.example.user_service.dto;

import lombok.Data;

@Data
public class RoleAssignmentRequest {
    private String username;
    private String role;

    // Getters and setters
}
