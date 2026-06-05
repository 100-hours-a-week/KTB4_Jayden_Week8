package com.example.spring_rest_api.view.repository;

import com.example.spring_rest_api.view.entity.ArticleViewCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountMemoryRepository {
    // <articleId, ArticleViewCount>
    private final Map<Long, ArticleViewCount> viewCountStorage = new ConcurrentHashMap<>();

    public Long read(Long articleId) {
        return viewCountStorage.containsKey(articleId) ?
                viewCountStorage.get(articleId).getViewCount() :
                0L;
    }

    public void increase(Long articleId, Long userId) {
        // 유저 24시간
        // 키 없을 때 초기화.

        if (!viewCountStorage.containsKey(articleId)) {
            viewCountStorage.put(articleId, ArticleViewCount.init(articleId, userId, 1L));
        }

        if (viewCountStorage.get(articleId).getUserId().equals(userId) &&
                viewCountStorage.get(articleId).getUpdatedAt().plusDays(1)
                .compareTo(LocalDateTime.now()) < 0) {
            viewCountStorage.put(articleId, viewCountStorage.get(articleId).increase());
        }
    }
}
