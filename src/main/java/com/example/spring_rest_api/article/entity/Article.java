package com.example.spring_rest_api.article.entity;

import com.example.spring_rest_api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long articleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String content;
    private List<String> contentImages;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private boolean isArticleHidden;

    public static Article create(User user, String title, String content, List<String> contentImages) {
        Article article = new Article();
        article.user = user;
        article.title = title;
        article.content = content;
        article.contentImages = contentImages;
        article.createdAt = LocalDateTime.now();
        article.deletedAt = null;
        article.isArticleHidden = false;
        return article;
    }

    public Article update(String title, String content, List<String> contentImages) {
        this.title = title;
        this.content = content;
        this.contentImages = contentImages;
        return this;
    }

    public Article delete() {
        this.deletedAt = LocalDateTime.now();
        return this;
    }

    public void hideArticle() {
        this.isArticleHidden = true;
    }
}
