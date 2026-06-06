package com.example.spring_rest_api.user.controller;

import com.example.spring_rest_api.common.response.ApiResponse;
import com.example.spring_rest_api.user.service.UserService;
import com.example.spring_rest_api.user.service.request.UserCreateRequest;
import com.example.spring_rest_api.user.service.request.UserUpdateRequest;
import com.example.spring_rest_api.user.service.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> read(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.of(
                "get_user_info_success",
                userService.read(userId)
        ));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateInformation(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(
                "user_info_update_success",
                userService.updateInformation(userId, request)
        ));
    }

    @PatchMapping("/users/{userId}/password")
    public ResponseEntity<ApiResponse<UserResponse>> updatePassword(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(
                "user_password_update_success",
                userService.updatePassword(userId, request)
        ));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> delete(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.of(
                "user_delete_success",
                userService.delete(userId)
        ));
    }
}
