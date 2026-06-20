package com.example.spring_rest_api.user.service;

import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.user.entity.User;
import com.example.spring_rest_api.user.repository.UserRepository;
import com.example.spring_rest_api.user.service.request.UserCreateRequest;
import com.example.spring_rest_api.user.service.request.UserUpdateRequest;
import com.example.spring_rest_api.user.service.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        return UserResponse.from(userRepository.save(User.create(
                request.getEmail(),
                request.getPassword(),
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
    public UserResponse updateInformation(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return UserResponse.from(user.updateInformation(
                request.getNickname(),
                request.getProfileImage()
        ));
    }

    @Transactional
    public UserResponse updatePassword(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return UserResponse.from(user.updatePassword(
                request.getPassword()
        ));
    }

    @Transactional
    public UserResponse delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        userRepository.delete(user);
        return UserResponse.from(user);
    }
}
