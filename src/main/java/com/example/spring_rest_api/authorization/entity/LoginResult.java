package com.example.spring_rest_api.authorization.entity;

import com.example.spring_rest_api.authorization.service.response.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {
    private LoginResponse response;
    private String refreshToken;
}
