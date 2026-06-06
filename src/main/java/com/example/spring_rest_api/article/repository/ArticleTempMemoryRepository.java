package com.example.spring_rest_api.article.repository;

import com.example.spring_rest_api.article.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ArticleTempMemoryRepository {
    // <userId, Article>
    private final Map<Long, Article> articleTempStorage = new ConcurrentHashMap<>();

    public Article save(Article article) {
        return articleTempStorage.put(article.getUserId(), article);
    }

    public Article read(Long userId) {
        return articleTempStorage.get(userId);
    }
}
