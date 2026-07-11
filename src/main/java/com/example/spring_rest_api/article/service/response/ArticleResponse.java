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
public class ArticleResponse {
    private Long articleId;
    private Long userId;
    private String title;
    private String content;
    private List<String> contentImageUrls;
    private LocalDateTime createdAt;

    public static ArticleResponse from(Article article) {
        List<String> fullPicturesPaths = article.getContentImages().stream()
                .map(ImageFile::getFilePath)
                .map(ImageFileUtil::toFullUrl)
                .toList();

        ArticleResponse response = new ArticleResponse();
        response.articleId = article.getArticleId();
        response.userId = article.getUser().getUserId();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.contentImageUrls = fullPicturesPaths;
        response.createdAt = article.getCreatedAt();
        return response;
    }
}
