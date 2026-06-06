package com.example.spring_rest_api.view.repository;

import com.example.spring_rest_api.view.entity.ArticleView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ArticleViewMemoryRepository {
    // <articleViewId, ArticleView>
    private final Map<Long, ArticleView> articleViewStorage = new ConcurrentHashMap<>();

    public ArticleView findByArticleIdAndUserId(Long articleId, Long userId) {
        return articleViewStorage.entrySet().stream()
                .filter(
                        entry ->
                                entry.getValue().getArticleId().equals(articleId) &&
                                        entry.getValue().getUserId().equals(userId)
                )
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public void save(ArticleView articleView) {
        articleViewStorage.put(articleView.getArticleViewId(), articleView);
    }

    public void update(ArticleView articleView) {
        articleViewStorage.put(articleView.getArticleViewId(), articleView);
    }
}
