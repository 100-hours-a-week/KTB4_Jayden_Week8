package com.example.spring_rest_api.comment.entity;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long commentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Long parentCommentId;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static Comment create(Article article, User user, Long parentCommentId, String commentText) {
        Comment comment = new  Comment();
        comment.article = article;
        comment.user = user;
        comment.parentCommentId = parentCommentId;
        comment.commentText = commentText;
        comment.createdAt = LocalDateTime.now();
        comment.updatedAt = null;
        comment.deletedAt = null;
        return comment;
    }

    public Comment update(String commentText) {
        this.commentText = commentText;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public Comment delete() {
        this.updatedAt = LocalDateTime.now();
        return this;
    }
}
