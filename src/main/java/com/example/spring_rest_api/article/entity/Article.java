package com.example.spring_rest_api.article.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    private Long articleId;
    private String title;
    private String content;
    private String previousContent;
    private Long userId;
    private List<String> contentImages;
    private List<String> previousContentImages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isArticleDeleted;
    private boolean isArticleEdited;
    private boolean isArticleHidden;
    private Long reportCount;
    private Long articleLikeCount;
    private Long articleViewCount;
    private Long commentCount;

    public static Article create(Long articleId, String title, String content, Long userId, List<String> contentImages) {
        Article article = new Article();
        article.articleId = articleId;
        article.title = title;
        article.content = content;
        article.previousContent = "";
        article.userId = userId;
        article.contentImages = contentImages;
        article.previousContentImages = new ArrayList<>();
        article.createdAt = LocalDateTime.now();
        article.updatedAt = article.createdAt;
        article.deletedAt = null;
        article.isArticleDeleted = false;
        article.isArticleEdited = false;
        article.isArticleHidden = false;
        article.reportCount = 0L;
        article.articleLikeCount = 0L;
        article.articleViewCount = 0L;
        article.commentCount = 0L;
        return article;
    }

    public Article update(String title, String content, List<String> contentImages) {
        this.title = title;
        this.previousContent = "%s \n %s \n %s"
                .formatted(this.previousContent, LocalDateTime.now(), this.content);
        this.content = content;
        this.previousContentImages.addAll(this.contentImages);
        this.contentImages = contentImages;
        this.updatedAt = LocalDateTime.now();
        this.isArticleEdited = true;
        return this;
    }

    public Article updateCount(Long articleLikeCount, Long articleViewCount, Long commentCount) {
        this.articleLikeCount = articleLikeCount;
        this.articleViewCount = articleViewCount;
        this.commentCount = commentCount;
        return this;
    }

    public Article delete() {
        this.isArticleDeleted = true;
        this.deletedAt = LocalDateTime.now();
        return this;
    }

    public Article increaseReportCount() {
        this.reportCount = this.reportCount + 1;
        return this;
    }

    public void hideArticle() {
        this.isArticleHidden = true;
    }
}
