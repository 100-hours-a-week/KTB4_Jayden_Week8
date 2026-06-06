package com.example.spring_rest_api.comment.service.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CommentCreateRequest {
    private Long userId;
    private String commentText;
    private Long parentCommentId;
}
