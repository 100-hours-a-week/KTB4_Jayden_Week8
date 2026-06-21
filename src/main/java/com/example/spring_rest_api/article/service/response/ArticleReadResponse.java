package com.example.spring_rest_api.article.service.response;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.article.entity.ArticleStat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleReadResponse {
    private Long articleId;
    private Long userId;
    private String title;
    private String content;
    private List<String> contentImages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long commentCount;
    private Long articleLikeCount;
    private Long articleViewCount;

    public static ArticleReadResponse from(Article article, ArticleStat articleStat) {
        ArticleReadResponse response = new ArticleReadResponse();
        response.articleId = article.getArticleId();
        response.userId = article.getUser().getUserId();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.contentImages = article.getContentImages();
        response.createdAt = article.getCreatedAt();
        response.updatedAt = article.getUpdatedAt();
        response.commentCount = articleStat.getCommentCount();
        response.articleLikeCount = articleStat.getArticleLikeCount();
        response.articleViewCount = articleStat.getArticleViewCount();
        return response;
    }

    //무한스크롤 쿼리 DTO
    public ArticleReadResponse(Long articleId, Long userId, String title, String content, List<String> contentImages, LocalDateTime createdAt, LocalDateTime updatedAt, Long commentCount, Long articleLikeCount, Long articleViewCount) {
        this.articleId = articleId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.contentImages = contentImages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentCount = commentCount;
        this.articleLikeCount = articleLikeCount;
        this.articleViewCount = articleViewCount;
    }
}
