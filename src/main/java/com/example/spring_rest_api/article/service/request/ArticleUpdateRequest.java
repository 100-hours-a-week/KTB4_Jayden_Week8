package com.example.spring_rest_api.article.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticleUpdateRequest {
    @NotBlank
    @Size(min = 1, max = 26)
    private String title;
    private String content;
    private Long userId;
    private List<String> contentImages;
}
