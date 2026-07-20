package com.example.spring_rest_api.comment.service.response;

import com.example.spring_rest_api.comment.entity.Comment;
import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.util.ImageFileUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long parentCommentId;

    public static CommentResponse from(Comment comment) {
        String profileImageUrl = Optional.ofNullable(comment.getUser().getProfileImage())
                .map(ImageFile::getFilePath)
                .map(ImageFileUtil::toFullUrl)
                .orElse(null);

        CommentResponse response = new CommentResponse();
        response.commentId = comment.getCommentId();
        response.userId = comment.getUser().getUserId();
        response.nickname = comment.getUser().getNickname();
        response.profileImageUrl = profileImageUrl;
        response.commentText = comment.getCommentText();
        response.createdAt = comment.getCreatedAt();
        response.updatedAt = comment.getUpdatedAt();
        response.deletedAt = comment.getDeletedAt();
        response.parentCommentId = comment.getParentComment() == null ? null : comment.getParentComment().getCommentId();
        return response;
    }

    //무한스크롤 쿼리 DTO
    public CommentResponse(Long commentId, Long userId, String nickname, String profileImagePath, String commentText, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, Long parentCommentId) {

        this.commentId = commentId;
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = Optional.ofNullable(profileImagePath)
                .map(ImageFileUtil::toFullUrl)
                .orElse(null);
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.parentCommentId = parentCommentId;
    }
}
