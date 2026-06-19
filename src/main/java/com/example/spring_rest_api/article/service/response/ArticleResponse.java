package com.example.spring_rest_api.article.service.response;

import com.example.spring_rest_api.article.entity.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleResponse {
    private Long articleId;
    private Long userId;
    private String title;
    private String content;
    private List<String> contentImages;
    private LocalDateTime createdAt;
//    private Long reportCount;
//    private Long articleLikeCount;
//    private Long articleViewCount;
//    private Long commentCount;

    public static ArticleResponse from(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.articleId = article.getArticleId();
        response.userId = article.getUser().getUserId();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.contentImages = article.getContentImages();
        response.createdAt = article.getCreatedAt();
        return response;
    }
}
