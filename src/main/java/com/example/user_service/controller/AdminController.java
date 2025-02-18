package com.example.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.RoleAssignmentRequest;
import com.example.user_service.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 권한 부여 API
    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRoleToUser(@RequestBody RoleAssignmentRequest roleAssignmentRequest) {
        userService.assignRole(roleAssignmentRequest.getUsername(), roleAssignmentRequest.getRole());
        return ResponseEntity.ok("Role assigned successfully");
    }
}
