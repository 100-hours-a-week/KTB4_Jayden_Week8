package com.example.spring_rest_api.article.controller;

import com.example.spring_rest_api.article.service.ArticleReportService;
import com.example.spring_rest_api.article.service.request.ArticleReportRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleReportController {
    private final ArticleReportService articleReportService;

    @PostMapping("/articles/{articleId}/report")
    public ResponseEntity<?> report(@PathVariable Long articleId, @AuthenticationPrincipal Long userId, @Valid @RequestBody ArticleReportRequest request) {
        articleReportService.report(articleId, userId, request);
        return ResponseEntity.noContent().build();
    }
}
