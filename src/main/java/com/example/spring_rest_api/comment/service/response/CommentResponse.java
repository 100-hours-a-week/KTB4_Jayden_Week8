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
        response.profileImage = comment.getUser().getProfileImage().getFilePath();
        response.commentText = comment.getCommentText();
        response.createdAt = comment.getCreatedAt();
        response.updatedAt = comment.getUpdatedAt();
        response.deletedAt = comment.getDeletedAt();
        response.parentCommentId = comment.getParentComment() == null ? null : comment.getParentComment().getCommentId();
        return response;
    }

    //무한스크롤 쿼리 DTO
    public CommentResponse(Long commentId, Long userId, String profileImage, String commentText, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, Long parentCommentId) {
        this.commentId = commentId;
        this.userId = userId;
        this.profileImage = profileImage;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.parentCommentId = parentCommentId;
    }
}
