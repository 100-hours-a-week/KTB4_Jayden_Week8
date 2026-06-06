package com.example.spring_rest_api.view.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleView {
    private Long articleViewId;
    private Long articleId;
    private Long userId;
    private LocalDateTime updatedAt;

    public static ArticleView init(Long articleViewId, Long articleId, Long userId) {
        ArticleView articleView = new ArticleView();
        articleView.articleViewId = articleViewId;
        articleView.articleId = articleId;
        articleView.userId = userId;
        articleView.updatedAt = LocalDateTime.now();
        return articleView;
    }

    public ArticleView update() {
        this.updatedAt = LocalDateTime.now();
        return this;
    }
}
