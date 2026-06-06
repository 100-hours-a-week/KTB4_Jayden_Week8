package com.example.spring_rest_api.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountMemoryRepository {
    // <articleId, articleViewCount>
    private final Map<Long, Long> articleViewCountStorage = new ConcurrentHashMap<>();

    public Long read(Long articleId) {
        return articleViewCountStorage.getOrDefault(articleId, 0L);
    }

    public void increase(Long articleId) {
        if (!articleViewCountStorage.containsKey(articleId)) {
            articleViewCountStorage.put(articleId, 1L);
        } else {
            articleViewCountStorage.put(articleId, articleViewCountStorage.get(articleId) + 1);
        }
    }
}
