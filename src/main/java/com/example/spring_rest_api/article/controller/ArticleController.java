package com.example.spring_rest_api.article.controller;

import com.example.spring_rest_api.article.service.ArticleService;
import com.example.spring_rest_api.article.service.request.ArticleCreateRequest;
import com.example.spring_rest_api.article.service.request.ArticleUpdateRequest;
import com.example.spring_rest_api.article.service.response.ArticleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/articles")
    public ArticleResponse create(@Valid @RequestBody ArticleCreateRequest request) {
        return articleService.create(request);
    }

    @PutMapping("/articles/{articleId}")
    public ArticleResponse update(@PathVariable Long articleId, @Valid @RequestBody ArticleUpdateRequest request) {
        return articleService.update(articleId, request);
    }

    @PutMapping("/articles/temp-save/{userId}")
    public ArticleResponse saveTempArticle(@PathVariable Long userId, @Valid @RequestBody ArticleUpdateRequest request) {
        return articleService.saveTempArticle(userId, request);
    }

    @GetMapping("/articles/temp-save/{userId}")
    public ArticleResponse readTempArticle(@PathVariable Long userId) {
        return articleService.readTempArticle(userId);
    }

    @DeleteMapping("/articles/{articleId}")
    public ArticleResponse delete(@PathVariable Long articleId) {
        return articleService.delete(articleId);
    }

    @GetMapping("/articles/{articleId}")
    public ArticleResponse read(@PathVariable Long articleId) {
        return articleService.read(articleId);
    }

    @GetMapping("/articles")
    public List<ArticleResponse> readInfiniteScroll(
            @RequestParam("pageSize") Long pageSize,
            @RequestParam(value = "lastArticleId", required = false) Long lastArticleId
    ) {
        return articleService.readInfiniteScroll(pageSize, lastArticleId);
    }

    @PostMapping("/articles/{articleId}/users/{userId}/report")
    public void report(@PathVariable Long articleId, @PathVariable Long userId) {
        articleService.report(articleId, userId);
    }
}
