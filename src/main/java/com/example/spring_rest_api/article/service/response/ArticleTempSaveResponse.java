package com.example.spring_rest_api.article.service.response;

import com.example.spring_rest_api.article.entity.TempArticle;
import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.util.ImageFileUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleTempSaveResponse {
    private String title;
    private String content;
    private List<String> contentImageUrls;

    public static ArticleTempSaveResponse from(TempArticle article) {
        List<String> fullPicturesPaths = article.getContentImages().stream()
                .map(ImageFile::getFilePath)
                .map(ImageFileUtil::toFullUrl)
                .toList();

        ArticleTempSaveResponse response = new ArticleTempSaveResponse();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.contentImageUrls = fullPicturesPaths;
        return response;
    }
}
