package com.example.spring_rest_api.view.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleViewCount {
    private Long articleId;
    private Long userId;
    private LocalDateTime updatedAt;
    private Long viewCount;

    public static ArticleViewCount init(Long articleId, Long userId, Long viewCount) {
        ArticleViewCount articleViewCount = new ArticleViewCount();
        articleViewCount.articleId = articleId;
        articleViewCount.userId = userId;
        articleViewCount.updatedAt = LocalDateTime.now();
        articleViewCount.viewCount = viewCount;
        return articleViewCount;
    }

    public ArticleViewCount increase() {
        this.viewCount++;
        return this;
    }
}
