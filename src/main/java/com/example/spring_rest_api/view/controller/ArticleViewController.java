package com.example.spring_rest_api.view.controller;

import com.example.spring_rest_api.view.service.ArticleViewService;
import com.example.spring_rest_api.view.service.response.ArticleViewCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleViewController {
    private final ArticleViewService articleViewService;

    @PostMapping("/views/articles/{articleId}/users/{userId}")
    public ArticleViewCountResponse increaseCount(@PathVariable Long articleId, @PathVariable Long userId) {
        return articleViewService.increaseCount(articleId, userId);
    }

    @GetMapping("/views/articles/{articleId}/count")
    public ArticleViewCountResponse read(@PathVariable Long articleId) {
        return articleViewService.readCount(articleId);
    }
}
