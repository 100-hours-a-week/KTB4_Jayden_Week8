package com.example.spring_rest_api.comment.service;

import com.example.spring_rest_api.comment.entity.Comment;
import com.example.spring_rest_api.comment.repository.CommentCountMemoryRepository;
import com.example.spring_rest_api.comment.repository.CommentMemoryRepository;
import com.example.spring_rest_api.comment.service.request.CommentCreateRequest;
import com.example.spring_rest_api.comment.service.request.CommentUpdateRequest;
import com.example.spring_rest_api.comment.service.response.CommentCountResponse;
import com.example.spring_rest_api.comment.service.response.CommentResponse;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.user.repository.UserMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMemoryRepository commentMemoryRepository;
    private final CommentCountMemoryRepository commentCountMemoryRepository;
    private final UserMemoryRepository userMemoryRepository;
    private final ArticleMemoryRepository articleMemoryRepository;
    private Long sequence = 0L;

    public CommentResponse create(Long articleId, CommentCreateRequest request) {
        throwIfArticleNotFound(articleId);

        commentCountMemoryRepository.increase(articleId);
        return CommentResponse.from(commentMemoryRepository.save(Comment.create(
                sequence++,
                articleId,
                request.getUserId(),
                userMemoryRepository.findById(request.getUserId()).getProfileImage(),
                request.getCommentText(),
                request.getParentCommentId()
        )));
    }

    public CommentResponse update(Long articleId, Long commentId, CommentUpdateRequest request) {
        throwIfArticleNotFound(articleId);

        Comment updated = commentMemoryRepository.findById(commentId).update(request.getCommentText());
        return CommentResponse.from(
                commentMemoryRepository.update(updated)
        );
    }

    public CommentResponse delete(Long articleId, Long commentId) {
        throwIfArticleNotFound(articleId);

        Comment deleted = commentMemoryRepository.findById(commentId).delete();
        commentCountMemoryRepository.decrease(articleId);
        return CommentResponse.from(
                commentMemoryRepository.delete(deleted)
        );
    }

    public CommentResponse read(Long articleId, Long commentId) {
        throwIfArticleNotFound(articleId);

        return CommentResponse.from(
                commentMemoryRepository.findById(commentId)
        );
    }

    public List<CommentResponse> readAllInfiniteScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId) {
        return (lastParentCommentId == null && lastCommentId == null) ?
                commentMemoryRepository.findAllInfiniteScroll(articleId, pageSize).stream()
                .map(CommentResponse::from)
                .toList() :
                commentMemoryRepository.findAllInfiniteScroll(articleId, pageSize, lastParentCommentId, lastCommentId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    public CommentCountResponse readCount(Long articleId) {
        return CommentCountResponse.from(
                articleId,
                commentCountMemoryRepository.read(articleId)
        );
    }


    private void throwIfArticleNotFound(Long articleId) {
        if (articleMemoryRepository.findById(articleId) == null) {
            throw new NotFoundException("ARTICLE_NOT_FOUND");
        }
    }
}
