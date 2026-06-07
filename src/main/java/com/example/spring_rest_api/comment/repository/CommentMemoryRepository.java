package com.example.spring_rest_api.comment.repository;

import com.example.spring_rest_api.comment.entity.Comment;
import com.example.spring_rest_api.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.util.Comparator.*;

@Repository
@RequiredArgsConstructor
public class CommentMemoryRepository {
    private final Map<Long, Comment> commentStorage = new ConcurrentSkipListMap<>();

    public Comment save(Comment comment) {
        commentStorage.put(comment.getCommentId(), comment);
        return commentStorage.get(comment.getCommentId());
    }

    public Comment update(Comment comment) {
        commentStorage.replace(comment.getCommentId(), comment);
        return findById(comment.getCommentId());
    }

    public Comment delete(Comment comment) {
        Comment deleted = findById(comment.getCommentId());
        commentStorage.replace(comment.getCommentId(), deleted);
        return findById(comment.getCommentId());
    }

    public Comment findById(Long commentId) {
        return Optional.ofNullable(commentStorage.get(commentId))
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));
    }

    public List<Comment> findAllInfiniteScroll(Long articleId, Long pageSize) {
        return commentStorage.values().stream()
                .filter(comment -> comment.getArticleId().equals(articleId))
                .sorted(comparing(Comment::getParentCommentId, nullsFirst(naturalOrder()))
                        .thenComparing(Comment::getCommentId))
                .limit(pageSize)
                .toList();
    }

    public List<Comment> findAllInfiniteScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId) {
        return commentStorage.values().stream()
                .filter(comment -> comment.getArticleId().equals(articleId))
                .sorted(comparing(Comment::getParentCommentId, nullsFirst(naturalOrder()))
                        .thenComparing(Comment::getCommentId))
                .filter(comment -> (comment.getParentCommentId() > lastParentCommentId) ||
                        (comment.getParentCommentId().equals(lastParentCommentId) && comment.getCommentId() >= lastCommentId))
                .limit(pageSize)
                .toList();
    }
}
