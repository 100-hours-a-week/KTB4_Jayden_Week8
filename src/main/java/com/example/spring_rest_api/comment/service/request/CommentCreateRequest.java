package com.example.spring_rest_api.comment.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String commentText;
    private Long parentCommentId;
}
