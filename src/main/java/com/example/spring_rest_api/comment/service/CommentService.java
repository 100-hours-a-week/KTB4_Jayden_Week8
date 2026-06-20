package com.example.spring_rest_api.comment.service;

import com.example.spring_rest_api.article.entity.ArticleStat;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.repository.ArticleStatRepository;
import com.example.spring_rest_api.comment.entity.Comment;
import com.example.spring_rest_api.comment.repository.CommentRepository;
import com.example.spring_rest_api.comment.service.request.CommentCreateRequest;
import com.example.spring_rest_api.comment.service.request.CommentUpdateRequest;
import com.example.spring_rest_api.comment.service.response.CommentCountResponse;
import com.example.spring_rest_api.comment.service.response.CommentResponse;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleStatRepository articleStatRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public CommentResponse create(Long articleId, CommentCreateRequest request) {
        ArticleStat stat = articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        stat.increaseCommentCount();

        Comment comment = Comment.create(
                articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND")),
                userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                request.getParentCommentId() == null ? null : request.getParentCommentId(),
                request.getCommentText()
        );
        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse update(Long articleId, Long commentId, CommentUpdateRequest request) {
        articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));
        return CommentResponse.from(
                comment.update(request.getCommentText())
        );
    }

    @Transactional
    public CommentResponse delete(Long articleId, Long commentId) {
        ArticleStat stat = articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        stat.decreaseCommentCount();

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));
        return CommentResponse.from(
                comment.delete()
        );
    }

    public CommentResponse read(Long articleId, Long commentId) {
        articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        return CommentResponse.from(comment);
    }

    public List<CommentResponse> readAllInfiniteScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId) {
        List<Comment> comments = lastCommentId == null && lastParentCommentId == null ?
                commentRepository.findAllInfiniteScroll(articleId, pageSize) :
                commentRepository.findAllInfiniteScroll(articleId, pageSize, lastParentCommentId, lastCommentId);
        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }

    public CommentCountResponse readCount(Long articleId) {
        ArticleStat stat = articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        return CommentCountResponse.from(
                articleId,
                stat.getCommentCount()
        );
    }
}
