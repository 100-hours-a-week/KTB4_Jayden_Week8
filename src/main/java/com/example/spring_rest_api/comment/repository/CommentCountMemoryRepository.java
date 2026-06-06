package com.example.spring_rest_api.comment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class CommentCountMemoryRepository {
    // <articleId, commentCount>
    private final Map<Long, Long> commentCountStorage = new ConcurrentHashMap<>();

    public Long read(Long articleId) {
        return  commentCountStorage.getOrDefault(articleId, 0L);
    }

    public void increase(Long articleId) {
        if (commentCountStorage.containsKey(articleId)) {
            commentCountStorage.put(articleId, commentCountStorage.get(articleId) + 1);
        } else {
            commentCountStorage.put(articleId, 1L);
        }
    }

    public void decrease(Long articleId) {
        if (commentCountStorage.containsKey(articleId) && commentCountStorage.get(articleId) > 0L) {
            commentCountStorage.put(articleId, commentCountStorage.get(articleId) - 1L);
        } else {
            commentCountStorage.put(articleId, 0L);
        }
    }
}
