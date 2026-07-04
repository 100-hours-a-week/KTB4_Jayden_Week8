package com.example.spring_rest_api.comment.service.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {
    @NotBlank
    private String commentText;
    private Long parentCommentId;
}
