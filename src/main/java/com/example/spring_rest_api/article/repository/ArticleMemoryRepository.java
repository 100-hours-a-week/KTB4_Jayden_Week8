package com.example.spring_rest_api.article.repository;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
@RequiredArgsConstructor
public class ArticleMemoryRepository {
    private final Map<Long, Article> articleStorage = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

    public Article save(Article article) {
        articleStorage.put(article.getArticleId(), article);
        return findById(article.getArticleId());
    }

    public Article update(Long articleId, Article article) {
        articleStorage.replace(articleId, article);
        return findById(articleId);
    }

    public Article delete(Long articleId) {
        Article deleted = findById(articleId).delete();
        articleStorage.replace(articleId, deleted);
        return findById(articleId);
    }

    public Article findById(Long articleId) {
        return Optional.ofNullable(articleStorage.get(articleId))
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
    }

    public List<Article> findAllInfiniteScroll(Long pageSize, Long lastArticleId) {
        return lastArticleId == null ?
                articleStorage.entrySet().stream()
                .limit(pageSize)
                .map(Map.Entry::getValue)
                .toList() :
                articleStorage.entrySet().stream()
                .filter(entry -> entry.getKey() < lastArticleId)
                .limit(pageSize)
                .map(Map.Entry::getValue)
                .toList();
    }

    public void report(Long articleId) {
        Article findArticle = findById(articleId);
        Article replaced = articleStorage.replace(articleId, findArticle.increaseReportCount());
        if (replaced.getReportCount() >= 5) {
            replaced.hideArticle();
        }
    }
}
