package com.example.spring_rest_api.user.controller;

import com.example.spring_rest_api.common.response.ApiResponse;
import com.example.spring_rest_api.user.service.UserService;
import com.example.spring_rest_api.user.service.request.UserCreateRequest;
import com.example.spring_rest_api.user.service.request.UserUpdateInfoRequest;
import com.example.spring_rest_api.user.service.request.UserUpdatePasswordRequest;
import com.example.spring_rest_api.user.service.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "register_success",
                        userService.create(request)
                ));
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> read(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.of(
                "get_user_info_success",
                userService.read(userId)
        ));
    }

    @PatchMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateInformation(@AuthenticationPrincipal Long userId, @Valid @RequestBody UserUpdateInfoRequest request) {
        return ResponseEntity.ok(ApiResponse.of(
                "user_info_update_success",
                userService.updateInformation(userId, request)
        ));
    }

    @PatchMapping("/users/me/password")
    public ResponseEntity<ApiResponse<UserResponse>> updatePassword(@AuthenticationPrincipal Long userId, @Valid @RequestBody UserUpdatePasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.of(
                "user_password_update_success",
                userService.updatePassword(userId, request)
        ));
    }

    @DeleteMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> delete(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.of(
                "user_delete_success",
                userService.delete(userId)
        ));
    }
}
