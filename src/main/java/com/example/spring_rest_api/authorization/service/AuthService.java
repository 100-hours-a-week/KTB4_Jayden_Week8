package com.example.spring_rest_api.authorization.service;

import com.example.spring_rest_api.authorization.service.request.AuthRequest;
import com.example.spring_rest_api.authorization.service.response.AuthResponse;
import com.example.spring_rest_api.common.exception.ForbiddenException;
import com.example.spring_rest_api.common.exception.UnauthorizedException;
import com.example.spring_rest_api.user.entity.User;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public AuthResponse login(AuthRequest request) {
        log.info("Login request: {}", request.getEmail());

        String email = request.getEmail();
        String password = request.getPassword();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("INVALID_EMAIL"));

        if (user.getPassword().equals(password)) {
            return AuthResponse.from("abc1234567890");
        } else {
            throw new UnauthorizedException("UNAUTHORIZED");
        }
    }

    public void logout(AuthRequest request) {
        log.info("Logout request: {}", request.getToken());

        if (!request.getToken().equals("abc1234567890")) {
            throw new ForbiddenException("TOKEN_NOT_VALID");
        }
        log.info("logout processing...");
    }
}
