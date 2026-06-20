package com.example.spring_rest_api.comment.service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCountResponse {
    private Long articleId;
    private Long articleCommentCount;

    public static CommentCountResponse from(Long articleId, Long articleCommentCount) {
        CommentCountResponse response = new CommentCountResponse();
        response.articleId = articleId;
        response.articleCommentCount = articleCommentCount;
        return response;
    }
}
