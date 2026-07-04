package com.example.spring_rest_api.user.service;

import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.common.exception.RequestConflictException;
import com.example.spring_rest_api.user.entity.User;
import com.example.spring_rest_api.user.repository.UserRepository;
import com.example.spring_rest_api.user.service.request.UserCreateRequest;
import com.example.spring_rest_api.user.service.request.UserUpdateInfoRequest;
import com.example.spring_rest_api.user.service.request.UserUpdatePasswordRequest;
import com.example.spring_rest_api.user.service.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RequestConflictException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new RequestConflictException("이미 존재하는 닉네임입니다.");
        }

        return UserResponse.from(userRepository.save(User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                request.getProfileImage()
        )));
    }

    public UserResponse read(Long userId) {
        return UserResponse.from(
                userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"))
        );
    }

    @Transactional
    public UserResponse updateInformation(Long userId, UserUpdateInfoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        User userByNickname = userRepository.findByNickname(request.getNickname())
                .orElse(null);

        if (userByNickname != null && !userByNickname.getUserId().equals(userId)) {
            throw new RequestConflictException("이미 존재하는 닉네임입니다.");
        }

        return UserResponse.from(user.updateInformation(
                request.getNickname(),
                request.getProfileImage()
        ));
    }

    @Transactional
    public UserResponse updatePassword(Long userId, UserUpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return UserResponse.from(user.updatePassword(
                passwordEncoder.encode(request.getPassword())
        ));
    }

    @Transactional
    public UserResponse delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        user.delete();
        return UserResponse.from(user);
    }
}
