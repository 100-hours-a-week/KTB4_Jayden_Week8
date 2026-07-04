package com.example.spring_rest_api.article.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleCreateRequest {
    @NotBlank
    @Size(min = 1, max = 26)
    private String title;
    @NotNull
    private String content;

    private List<String> contentImages;
}
