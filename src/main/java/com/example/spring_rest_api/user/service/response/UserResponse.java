package com.example.spring_rest_api.user.service.response;

import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.util.ImageFileUtil;
import com.example.spring_rest_api.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class UserResponse {
    private String email;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime deletedAt;


    public static UserResponse of(String email, String nickname, String profileImageUrl, LocalDateTime deletedAt) {
        UserResponse userResponse = new UserResponse();
        userResponse.email = email;
        userResponse.nickname = nickname;
        userResponse.profileImageUrl = profileImageUrl;
        userResponse.deletedAt = deletedAt;
        return userResponse;
    }

    public static UserResponse from(User user) {
        String fullProfileUrl = Optional.ofNullable(user.getProfileImage())
                .map(ImageFile::getFilePath)
                .map(ImageFileUtil::toFullUrl)
                .orElse(null);

        return of(
                user.getEmail(),
                user.getNickname(),
                fullProfileUrl,
                user.getDeletedAt()
        );
    }
}
