package com.example.spring_rest_api.view.service.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleViewCountResponse {
    private Long articleId;
    private Long viewCount;

    public static ArticleViewCountResponse from(Long articleId, Long viewCount) {
        ArticleViewCountResponse response = new ArticleViewCountResponse();
        response.articleId = articleId;
        response.viewCount = viewCount;
        return response;
    }
}
