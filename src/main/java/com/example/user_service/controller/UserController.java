package com.example.user_service.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service.dto.UserProfileResponse;
import com.example.user_service.dto.UserSignUpRequest;
import com.example.user_service.entity.User;
import com.example.user_service.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원 가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    // 사용자 프로필 조회 API
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@RequestParam("id") Integer id) {
        UserProfileResponse profile = userService.getUserProfile(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/myInfo")
    public ResponseEntity<?> getMyInfo(@RequestHeader(value = "X-User-Name") String username) {
        System.out.println("X-User-Name: " + username);
        Optional<User> user = userService.getUserByUserName(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User not exist.");
        }
    
        return ResponseEntity.ok(user);
    }
}
