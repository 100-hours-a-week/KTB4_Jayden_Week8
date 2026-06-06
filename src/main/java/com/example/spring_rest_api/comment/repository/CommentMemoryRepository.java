package com.example.spring_rest_api.comment.repository;

import com.example.spring_rest_api.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.util.Comparator.*;

@Repository
@RequiredArgsConstructor
public class CommentMemoryRepository {
    private final Map<Long, Comment> commentStorage = new ConcurrentSkipListMap<>();

    public Comment save(Comment comment) {
        return commentStorage.put(comment.getCommentId(), comment);
    }

    public Comment update(Comment comment) {
        return commentStorage.replace(comment.getCommentId(), comment);
    }

    public Comment delete(Comment comment) {
        Comment deleted = findById(comment.getCommentId()).delete();
        return commentStorage.replace(comment.getCommentId(), deleted);
    }

    public Comment findById(Long commentId) {
        return commentStorage.get(commentId);
    }

    public List<Comment> findAllInfiniteScroll(Long articleId, Long pageSize) {
        return commentStorage.values().stream()
                .filter(comment -> comment.getArticleId().equals(articleId))
                .sorted(comparing(Comment::getParentCommentId)
                        .thenComparing(Comment::getCommentId))
                .limit(pageSize)
                .toList();
    }

    public List<Comment> findAllInfiniteScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId) {
        return commentStorage.values().stream()
                .filter(comment -> comment.getArticleId().equals(articleId))
                .sorted(comparing(Comment::getParentCommentId)
                        .thenComparing(Comment::getCommentId))
                .filter(comment -> (comment.getParentCommentId() > lastParentCommentId) ||
                        (comment.getParentCommentId().equals(lastParentCommentId) && comment.getCommentId() >= lastCommentId))
                .limit(pageSize)
                .toList();
    }
}
