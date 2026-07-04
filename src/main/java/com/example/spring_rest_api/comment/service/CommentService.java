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
import com.example.spring_rest_api.common.exception.BadRequestException;
import com.example.spring_rest_api.common.exception.ForbiddenException;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.common.exception.RequestConflictException;
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
    public CommentResponse create(Long userId, Long articleId, CommentCreateRequest request) {
        Comment comment = Comment.create(
                articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND")),
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                request.getParentCommentId() == null ? null : commentRepository.findById(request.getParentCommentId()).orElseThrow(() -> new NotFoundException("PARENT_COMMENT_NOT_FOUND")),
                request.getCommentText()
        );

        throwIfNotInArticle(articleId, comment);

        ArticleStat stat = articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        stat.increaseCommentCount();

        return CommentResponse.from(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse update(Long userId, Long articleId, Long commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        throwIfForbidden(userId, comment);
        throwIfNotInArticle(articleId, comment);

        articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));

        return CommentResponse.from(
                comment.update(request.getCommentText())
        );
    }

    @Transactional
    public CommentResponse delete(Long userId, Long articleId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));
        if (comment.getDeletedAt() != null) {
            throw new RequestConflictException("이미 삭제된 댓글입니다.");
        }

        throwIfForbidden(userId, comment);
        throwIfNotInArticle(articleId, comment);

        ArticleStat stat = articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        stat.decreaseCommentCount();
        return CommentResponse.from(
                comment.delete()
        );
    }

    public CommentResponse read(Long userId, Long articleId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        throwIfForbidden(userId, comment);
        throwIfNotInArticle(articleId, comment);

        articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        if (comment.getDeletedAt() != null) {
            throw new BadRequestException("COMMENT_ALREADY_DELETED");
        }

        return CommentResponse.from(comment);
    }

    private void throwIfForbidden(Long userId, Comment comment) {
        if (!userId.equals(comment.getUser().getUserId())) {
            throw new ForbiddenException("ACCESS_DENIED");
        }
    }

    private void throwIfNotInArticle(Long articleId, Comment comment) {
        if (!comment.getArticle().getArticleId().equals(articleId)) {
            throw new BadRequestException("다른 게시글의 댓글입니다.");
        }
    }

    public List<CommentResponse> readAllInfiniteScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId) {
        return (lastCommentId == null && lastParentCommentId == null) ?
                commentRepository.findAllInfiniteScroll(articleId, pageSize) :
                (lastParentCommentId == null) ?
                        commentRepository.findAllInfiniteScroll(articleId, pageSize, lastCommentId) :
                        commentRepository.findAllInfiniteScroll(articleId, pageSize, lastParentCommentId, lastCommentId);
    }

    public CommentCountResponse readCount(Long articleId) {
        ArticleStat stat = articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        return CommentCountResponse.from(
                articleId,
                stat.getCommentCount()
        );
    }
}
