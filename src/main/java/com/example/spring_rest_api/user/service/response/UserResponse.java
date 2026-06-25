package com.example.spring_rest_api.user.service.response;

import com.example.spring_rest_api.user.entity.User;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class UserResponse {
    private String email;
    private String nickname;
    private String profileImage;
    private LocalDateTime deletedAt;


    public static UserResponse from(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.email = user.getEmail();
        userResponse.nickname = user.getNickname();
        userResponse.profileImage = user.getProfileImage();
        userResponse.deletedAt = user.getDeletedAt();
        return userResponse;
    }
}
