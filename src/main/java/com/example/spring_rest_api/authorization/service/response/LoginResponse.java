package com.example.spring_rest_api.authorization.service.response;

import com.example.spring_rest_api.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private User user;
    private TokenInfo token;

    public static LoginResponse of(User user, String accessToken, long expiresIn) {
        return new LoginResponse(
                user,
                new TokenInfo(accessToken, expiresIn)
        );
    }
}
