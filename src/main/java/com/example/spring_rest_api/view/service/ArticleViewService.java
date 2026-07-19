package com.example.spring_rest_api.view.service;

import com.example.spring_rest_api.article.entity.ArticleStat;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.repository.ArticleStatRepository;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.user.repository.UserRepository;
import com.example.spring_rest_api.view.entity.ArticleView;
import com.example.spring_rest_api.view.repository.ArticleViewRepository;
import com.example.spring_rest_api.view.service.response.ArticleViewCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleViewService {
    private final ArticleViewRepository articleViewRepository;
    private final ArticleStatRepository articleStatRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ArticleViewCountResponse increaseCount(Long articleId, Long userId) {
        ArticleView articleView = articleViewRepository.findByArticle_ArticleIdAndUser_UserId(articleId, userId)
                .orElse(null);
        ArticleStat stat = articleStatRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));

        return articleView == null ?
                getViewCountIfNotSaved(articleId, userId, stat) :
                getViewCountIfSaved(articleId, userId, articleView, stat);
    }

    private ArticleViewCountResponse getViewCountIfNotSaved(Long articleId, Long userId, ArticleStat stat) {
        articleViewRepository.save(ArticleView.init(
                articleRepository.findById(articleId)
                        .filter(a -> a.getDeletedAt() == null && !a.isArticleHidden())
                        .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND")),
                userRepository.findById(userId)
                        .filter(u -> u.getDeletedAt() == null)
                        .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"))));

        stat.incrementArticleViewCount();

        return ArticleViewCountResponse.from(
                articleId,
                stat.getArticleViewCount()
        );
    }

    private ArticleViewCountResponse getViewCountIfSaved(Long articleId, Long userId, ArticleView articleView, ArticleStat stat) {
        articleRepository.findById(articleId)
                .filter(a -> a.getDeletedAt() == null && !a.isArticleHidden())
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
        userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (articleView.getUpdatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
            articleView.update();
            stat.incrementArticleViewCount();

            return ArticleViewCountResponse.from(
                    articleId,
                    stat.getArticleViewCount());

        } else {
            return ArticleViewCountResponse.from(
                    articleId,
                    null
            );
        }
    }

    public ArticleViewCountResponse readCount(Long articleId) {
        return ArticleViewCountResponse.from(
                articleId,
                articleStatRepository.findById(articleId)
                        .orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"))
                        .getArticleViewCount()
        );
    }

}
