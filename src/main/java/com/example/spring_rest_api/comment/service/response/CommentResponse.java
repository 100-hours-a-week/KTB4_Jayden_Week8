package com.example.spring_rest_api.comment.service.response;

import com.example.spring_rest_api.comment.entity.Comment;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String profileImage;
    private String commentText;
    private boolean isUserDeleted;
    private boolean isCommentDeleted;
    private LocalDateTime createdAt;
    private Long parentCommentId;

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.commentId = comment.getCommentId();
        response.userId = comment.getUserId();
        response.profileImage = comment.getProfileImage();
        response.commentText = comment.getCommentText();
        response.isUserDeleted = comment.isUserDeleted();
        response.isCommentDeleted = comment.isCommentDeleted();
        response.createdAt = comment.getCreatedAt();
        response.parentCommentId = comment.getParentCommentId();
        return response;
    }
}
