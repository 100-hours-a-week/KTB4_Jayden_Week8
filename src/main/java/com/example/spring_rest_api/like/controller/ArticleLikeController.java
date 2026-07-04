package com.example.spring_rest_api.like.controller;

import com.example.spring_rest_api.common.response.ApiResponse;
import com.example.spring_rest_api.like.service.ArticleLikeService;
import com.example.spring_rest_api.like.service.response.ArticleLikeCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArticleLikeController {
    private final ArticleLikeService likeService;

    @PostMapping("/likes/articles/{articleId}")
    public ResponseEntity<ApiResponse<ArticleLikeCountResponse>> like(@PathVariable Long articleId, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "like_success",
                        likeService.like(articleId, userId)
                ));
    }

    @DeleteMapping("/likes/articles/{articleId}")
    public ResponseEntity<ApiResponse<ArticleLikeCountResponse>> unlike(@PathVariable Long articleId, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.of(
                "unlike_success",
                likeService.unlike(articleId, userId)
        ));
    }

    @GetMapping("/likes/articles/{articleId}/count")
    public ResponseEntity<ApiResponse<ArticleLikeCountResponse>> countLike(@PathVariable Long articleId) {
        return ResponseEntity.ok(ApiResponse.of(
                "success",
                likeService.readCount(articleId)
        ));
    }
}
