package com.example.spring_rest_api.comment.controller;

import com.example.spring_rest_api.comment.service.CommentService;
import com.example.spring_rest_api.comment.service.request.CommentCreateRequest;
import com.example.spring_rest_api.comment.service.request.CommentUpdateRequest;
import com.example.spring_rest_api.comment.service.response.CommentCountResponse;
import com.example.spring_rest_api.comment.service.response.CommentResponse;
import com.example.spring_rest_api.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/articles/{articleId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> create(@PathVariable Long articleId, @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "comment_create_success",
                        commentService.create(articleId, request)
                ));
    }

    @PutMapping("/articles/{articleId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> update(@PathVariable Long articleId, @PathVariable Long commentId, @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(
                "comment_update_success",
                commentService.update(articleId, commentId, request)
        ));
    }

    @DeleteMapping("/articles/{articleId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> delete(@PathVariable Long articleId, @PathVariable Long commentId) {
        return ResponseEntity.ok(ApiResponse.of(
                "comment_delete_success",
                commentService.delete(articleId, commentId)
        ));
    }

    @GetMapping("/articles/{articleId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> read(@PathVariable Long articleId, @PathVariable Long commentId) {
        return ResponseEntity.ok(ApiResponse.of(
                "comment_load_success",
                commentService.read(articleId, commentId)
        ));
    }

    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> readAllInfiniteScroll(
            @PathVariable Long articleId,
            @RequestParam Long pageSize,
            @RequestParam(required = false) Long lastParentCommentId,
            @RequestParam(required = false) Long lastCommentId
    ) {
        return ResponseEntity.ok(ApiResponse.of(
                "comments_load_success",
                commentService.readAllInfiniteScroll(articleId, pageSize, lastParentCommentId, lastCommentId)
        ));
    }

    @GetMapping("/articles/{articleId}/comments/count")
    public ResponseEntity<ApiResponse<CommentCountResponse>> readCount(@PathVariable Long articleId) {
        return ResponseEntity.ok(ApiResponse.of(
                "comment_count_read_success",
                commentService.readCount(articleId)
        ));
    }
}
