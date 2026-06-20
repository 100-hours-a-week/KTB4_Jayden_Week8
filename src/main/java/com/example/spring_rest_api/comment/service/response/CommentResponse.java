package com.example.spring_rest_api.comment.service.response;

import com.example.spring_rest_api.comment.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String profileImage;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long parentCommentId;

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.commentId = comment.getCommentId();
        response.userId = comment.getUser().getUserId();
        response.profileImage = comment.getUser().getProfileImage();
        response.commentText = comment.getCommentText();
        response.createdAt = comment.getCreatedAt();
        response.updatedAt = comment.getUpdatedAt();
        response.deletedAt = comment.getDeletedAt();
        response.parentCommentId = comment.getParentCommentId();
        return response;
    }
}
