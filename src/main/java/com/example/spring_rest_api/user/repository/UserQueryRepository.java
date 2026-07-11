package com.example.spring_rest_api.user.repository;

import com.example.spring_rest_api.user.entity.User;

import java.util.Optional;

public interface UserQueryRepository {
    Optional<User> findByEmailWithProfileImage(String email);
    Optional<User> findByIdWithProfileImage(Long id);
}
