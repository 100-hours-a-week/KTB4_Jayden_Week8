package com.example.spring_rest_api.authorization.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String email;
    private String nickname;
    private TokenInfo token;

    public static LoginResponse of(Long userId, String email, String nickname, String accessToken, long expiresIn) {
        return new LoginResponse(
                userId,
                email,
                nickname,
                new TokenInfo(accessToken, expiresIn)
        );
    }
}
