package com.example.spring_rest_api.authorization.service;

import com.example.spring_rest_api.authorization.entity.LoginResult;
import com.example.spring_rest_api.authorization.entity.RefreshToken;
import com.example.spring_rest_api.authorization.entity.TokenResult;
import com.example.spring_rest_api.authorization.repository.RefreshTokenRepository;
import com.example.spring_rest_api.authorization.service.request.LoginRequest;
import com.example.spring_rest_api.authorization.service.response.LoginResponse;
import com.example.spring_rest_api.authorization.service.response.TokenInfo;
import com.example.spring_rest_api.common.exception.UnauthorizedException;
import com.example.spring_rest_api.user.entity.User;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResult login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("INVALID_CREDENTIALS");
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getUserId(),
                user.getEmail(),
                user.getNickname()
        );

        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());
        refreshTokenRepository.deleteByUserId(user.getUserId());
        refreshTokenRepository.save(RefreshToken.create(
                refreshToken,
                user.getUserId(),
                LocalDateTime.now().plusDays(14)
        ));

        return new LoginResult(
                LoginResponse.of(user, accessToken, jwtProvider.getAccessTokenValidityInMilliseconds()),
                refreshToken
        );
    }

    public TokenResult refreshAccessToken(String refreshToken) {
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("UNAUTHORIZED"));

        if (saved.isExpired()) {
            refreshTokenRepository.delete(saved);
            throw new UnauthorizedException("UNAUTHORIZED");
        }

        User user = userRepository.findById(saved.getUserId())
                .orElseThrow(() -> new UnauthorizedException("UNAUTHORIZED"));

        String newAccessToken = jwtProvider.createAccessToken(
                user.getUserId(),
                user.getEmail(),
                user.getNickname()
        );

        String newRefreshToken = jwtProvider.createRefreshToken(user.getUserId());
        refreshTokenRepository.delete(saved);
        refreshTokenRepository.save(RefreshToken.create(
                newRefreshToken,
                user.getUserId(),
                LocalDateTime.now().plusDays(14)
        ));

        return new TokenResult(
                new TokenInfo(newAccessToken, 3600),
                newRefreshToken
        );
    }

    public void logout(Long userId) {
        // 리프레시 토큰 DB에서 삭제하기
        refreshTokenRepository.deleteByUserId(userId);


        // access token blacklist
    }
}
