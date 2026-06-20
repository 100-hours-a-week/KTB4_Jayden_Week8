package com.example.spring_rest_api.article.controller;

import com.example.spring_rest_api.article.service.ArticleService;
import com.example.spring_rest_api.article.service.request.ArticleCreateRequest;
import com.example.spring_rest_api.article.service.request.ArticleUpdateRequest;
import com.example.spring_rest_api.article.service.response.ArticleReadResponse;
import com.example.spring_rest_api.article.service.response.ArticleResponse;
import com.example.spring_rest_api.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/articles")
    public ResponseEntity<ApiResponse<ArticleResponse>> create(@Valid @RequestBody ArticleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "article_create_success",
                        articleService.create(request)
                ));
    }

    @PutMapping("/articles/{articleId}")
    public ResponseEntity<ApiResponse<ArticleResponse>> update(@PathVariable Long articleId, @Valid @RequestBody ArticleUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(
                "article_update_success",
                articleService.update(articleId, request)
        ));
    }

    @DeleteMapping("/articles/{articleId}")
    public ResponseEntity<ApiResponse<ArticleResponse>> delete(@PathVariable Long articleId) {
        return ResponseEntity.ok(ApiResponse.of(
                "article_delete_success",
                articleService.delete(articleId)
        ));
    }

    @GetMapping("/articles/{articleId}")
    public ResponseEntity<ApiResponse<ArticleReadResponse>> read(@PathVariable Long articleId) {
        return ResponseEntity.ok(ApiResponse.of(
                "article_load_success",
                articleService.read(articleId)
        ));
    }

    @GetMapping("/articles")
    public ResponseEntity<ApiResponse<List<ArticleReadResponse>>> readInfiniteScroll(
            @RequestParam("pageSize") Long pageSize,
            @RequestParam(value = "lastArticleId", required = false) Long lastArticleId
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                "articles_load_success",
                articleService.readInfiniteScroll(pageSize, lastArticleId)
        ));
    }
}
