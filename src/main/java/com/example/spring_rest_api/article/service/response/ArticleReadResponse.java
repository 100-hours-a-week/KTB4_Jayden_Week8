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
        response.commentCount = articleStat.getCommentCount();
        response.articleLikeCount = articleStat.getArticleLikeCount();
        response.articleViewCount = articleStat.getArticleViewCount();
        return response;
    }
}
