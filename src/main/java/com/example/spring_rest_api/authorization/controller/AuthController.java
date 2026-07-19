package com.example.spring_rest_api.authorization.controller;

import com.example.spring_rest_api.authorization.entity.LoginResult;
import com.example.spring_rest_api.authorization.entity.TokenResult;
import com.example.spring_rest_api.authorization.service.AuthService;
import com.example.spring_rest_api.authorization.service.request.LoginRequest;
import com.example.spring_rest_api.authorization.service.response.LoginResponse;
import com.example.spring_rest_api.authorization.service.response.TokenInfo;
import com.example.spring_rest_api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpResponse) {
        LoginResult result = authService.login(loginRequest);

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(ApiResponse.of(
                "login_success",
                result.getResponse()
        ));
    }

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<ApiResponse<TokenInfo>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        TokenResult result = authService.refreshAccessToken(refreshToken);

        if (result.getNewRefreshToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getNewRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(14 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity.ok(ApiResponse.of(
                "Token_refresh_success",
                result.getToken()
        ));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        authService.logout(refreshToken);

        if (refreshToken != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(60)
                    .sameSite("Strict")
                    .build();
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity.ok(ApiResponse.of(
                "logout_success", null
        ));
    }
}
