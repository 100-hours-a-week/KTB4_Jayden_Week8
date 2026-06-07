package com.example.spring_rest_api.like.repository;

import com.example.spring_rest_api.like.entity.ArticleLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ArticleLikeMemoryRepository {
    // <articleLikeId, ArticleLike>
    private final Map<Long, ArticleLike> articleLikeStorage =  new ConcurrentHashMap<>();

    public ArticleLike findByArticleIdAndUserId(Long articleId, Long userId) {
        return articleLikeStorage.values().stream()
                .filter(entry ->
                        entry.getArticleId().equals(articleId) &&
                                entry.getUserId().equals(userId)
                )
                .findFirst()
                .orElse(null);
    }

    public ArticleLike save(ArticleLike articleLike) {
        articleLikeStorage.put(articleLike.getArticleLikeId(), articleLike);
        return articleLikeStorage.get(articleLike.getArticleLikeId());
    }

    public void delete(ArticleLike articleLike) {
        articleLikeStorage.remove(articleLike.getArticleLikeId());
    }
}