package com.example.spring_rest_api.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime informationUpdatedAt;
    private LocalDateTime passwordUpdatedAt;

    public static User create(String email, String password, String nickname, String profileImage) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.nickname = nickname;
        user.profileImage = profileImage;
        user.createdAt = LocalDateTime.now();
        user.deletedAt = null;
        user.informationUpdatedAt = user.createdAt;
        user.passwordUpdatedAt = user.createdAt;
        return user;
    }

    public User updateInformation(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.informationUpdatedAt = LocalDateTime.now();
        return this;
    }

    public User updatePassword(String password) {
        this.password = password;
        this.passwordUpdatedAt = LocalDateTime.now();
        return this;
    }

    public User delete() {
        this.deletedAt = LocalDateTime.now();
        return this;
    }
}
