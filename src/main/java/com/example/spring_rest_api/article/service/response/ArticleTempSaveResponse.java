package com.example.spring_rest_api.article.service.response;

import com.example.spring_rest_api.article.entity.TempArticle;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleTempSaveResponse {
    private String title;
    private String content;
    private List<String> contentImages;

    public static ArticleTempSaveResponse from(TempArticle article) {
        ArticleTempSaveResponse response = new ArticleTempSaveResponse();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.contentImages = article.getContentImages();
        return response;
    }
}
