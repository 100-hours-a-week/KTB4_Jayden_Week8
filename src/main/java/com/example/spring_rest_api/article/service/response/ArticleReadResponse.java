package com.example.spring_rest_api.article.service.response;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.util.ImageFileUtil;
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
    private List<String> contentImageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long commentCount;
    private Long articleLikeCount;
    private Long articleViewCount;

    public static ArticleReadResponse from(Article article) {
        List<String> fullPicturesPaths = article.getContentImages().stream()
                .map(ImageFile::getFilePath)
                .map(ImageFileUtil::toFullUrl)
                .toList();

        ArticleReadResponse response = new ArticleReadResponse();
        response.articleId = article.getArticleId();
        response.userId = article.getUser().getUserId();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.contentImageUrls = fullPicturesPaths;
        response.createdAt = article.getCreatedAt();
        response.updatedAt = article.getUpdatedAt();
        response.commentCount = article.getArticleStat().getCommentCount();
        response.articleLikeCount = article.getArticleStat().getArticleLikeCount();
        response.articleViewCount = article.getArticleStat().getArticleViewCount();
        return response;
    }
}
