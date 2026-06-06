package com.example.spring_rest_api.view.controller;

import com.example.spring_rest_api.common.response.ApiResponse;
import com.example.spring_rest_api.view.service.ArticleViewService;
import com.example.spring_rest_api.view.service.response.ArticleViewCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleViewController {
    private final ArticleViewService articleViewService;

    @PostMapping("/views/articles/{articleId}/users/{userId}")
    public ResponseEntity<ApiResponse<ArticleViewCountResponse>> increaseCount(@PathVariable Long articleId, @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "view_count_success",
                        articleViewService.increaseCount(articleId, userId)
                        ));
    }

    @GetMapping("/views/articles/{articleId}/count")
    public ResponseEntity<ApiResponse<ArticleViewCountResponse>> read(@PathVariable Long articleId) {
        return ResponseEntity.ok(ApiResponse.of(
                "view_count_read_success",
                articleViewService.readCount(articleId)
        ));





    }
}
