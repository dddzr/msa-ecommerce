package com.example.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.UserProfileRequest;
import com.example.user_service.service.UserService;

@RestController
@RequestMapping("/api/users/authenticated")
public class AuthenticatedUserController {

    private final UserService userService;

    public AuthenticatedUserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 프로필 업데이트 API (Authenticated)

    // 사용자 프로필 조회 API
    @PostMapping("/update/profile")
    public ResponseEntity<String> UpdateUserProfile(@RequestBody UserProfileRequest userProfileRequest,
            @RequestHeader("X-User-Name") String loggedInUserName) {
        
        // 요청된 userProfileRequest의 ID와 로그인한 유저의 ID 비교
        if (!userProfileRequest.getUsername().equals(loggedInUserName)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("You do not have permission to update this profile.");
        }

        boolean result = userService.UpdateUserProfile(userProfileRequest);
        if (result) {
            return ResponseEntity.status(HttpStatus.OK).body("User profile updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to update user profile. Please try again.");
        }
    }
}