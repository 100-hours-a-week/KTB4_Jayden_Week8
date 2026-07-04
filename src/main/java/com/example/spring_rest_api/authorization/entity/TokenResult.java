package com.example.spring_rest_api.authorization.entity;

import com.example.spring_rest_api.authorization.service.response.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResult {
    private TokenInfo token;
    private String newRefreshToken;
}
