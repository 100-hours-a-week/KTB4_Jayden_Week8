package com.example.spring_rest_api.article.service.response;

import com.example.spring_rest_api.article.entity.Article;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class ArticleResponse {
    private Long articleId;
    private String title;
    private String content;
    private Long userId;
    private List<String> contentImages;
    private LocalDateTime createdAt;
    private boolean isArticleDeleted;
    private boolean isArticleEdited;
    private boolean isArticleHidden;
    private Long reportCount;
    private Long articleLikeCount;
    private Long articleViewCount;
    private Long commentCount;

    public static ArticleResponse from(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.articleId = article.getArticleId();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.userId = article.getUserId();
        response.contentImages = article.getContentImages();
        response.createdAt = article.getCreatedAt();
        response.isArticleDeleted = article.isArticleDeleted();
        response.isArticleEdited = article.isArticleEdited();
        response.isArticleHidden = article.isArticleHidden();
        response.reportCount = article.getReportCount();
        response.articleLikeCount = article.getArticleLikeCount();
        response.articleViewCount = article.getArticleViewCount();
        response.commentCount = article.getCommentCount();
        return response;
    }
}
