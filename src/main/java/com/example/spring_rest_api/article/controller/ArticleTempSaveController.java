package com.example.spring_rest_api.article.controller;

import com.example.spring_rest_api.article.service.ArticleTempSaveService;
import com.example.spring_rest_api.article.service.request.ArticleTempSaveRequest;
import com.example.spring_rest_api.article.service.response.ArticleTempSaveResponse;
import com.example.spring_rest_api.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArticleTempSaveController {
    private final ArticleTempSaveService articleTempSaveService;

    @PutMapping("/articles/temp-save")
    public ResponseEntity<?> saveTempArticle(@AuthenticationPrincipal Long userId, @Valid @RequestBody ArticleTempSaveRequest request) {
        articleTempSaveService.saveTempArticle(userId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/articles/temp-save")
    public ResponseEntity<ApiResponse<ArticleTempSaveResponse>> readTempArticle(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.of(
                "temp_save_load_success",
                articleTempSaveService.readTempArticle(userId)
        ));
    }
}
