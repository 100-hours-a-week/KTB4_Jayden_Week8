package com.example.spring_rest_api.comment.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    private Long commentId;
    private Long articleId;
    private Long userId;
    private String profileImage;
    private String commentText;
    private boolean isUserDeleted;
    private boolean isCommentDeleted;
    private boolean isCommentUpdated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentCommentId;

    public static Comment create(Long commentId, Long articleId, Long userId, String profileImage, String commentText, Long parentCommentId) {
        Comment comment = new  Comment();
        comment.commentId = commentId;
        comment.articleId = articleId;
        comment.userId = userId;
        comment.profileImage = profileImage;
        comment.commentText = commentText;
        comment.isUserDeleted = false;
        comment.isCommentDeleted = false;
        comment.isCommentUpdated = false;
        comment.createdAt = LocalDateTime.now();
        comment.updatedAt = comment.createdAt;
        comment.parentCommentId = parentCommentId;
        return comment;
    }

    public Comment update(String commentText) {
        this.commentText = commentText;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public Comment delete() {
        this.isCommentDeleted = true;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
}
