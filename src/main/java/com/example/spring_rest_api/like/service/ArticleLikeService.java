package com.example.spring_rest_api.like.service;

import com.example.spring_rest_api.article.entity.ArticleStat;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.repository.ArticleStatRepository;
import com.example.spring_rest_api.common.exception.BadRequestException;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.like.entity.ArticleLike;
import com.example.spring_rest_api.like.repository.ArticleLikeRepository;
import com.example.spring_rest_api.like.service.response.ArticleLikeCountResponse;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleStatRepository articleStatRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleLikeCountResponse like(Long articleId, Long userId) {
        if (articleLikeRepository.findByArticle_ArticleIdAndUser_UserId(articleId, userId).isPresent()) {
            throw new BadRequestException("ALREADY_LIKED");
        }

        articleLikeRepository.save(ArticleLike.create(
                articleRepository.findById(articleId)
                        .filter(a -> a.getDeletedAt() == null && !a.isArticleHidden())
                        .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND")),
                userRepository.findById(userId)
                        .filter(u -> u.getDeletedAt() == null)
                        .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"))
        ));

        ArticleStat stat = articleStatRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        stat.increaseArticleLikeCount();

        return ArticleLikeCountResponse.from(
                articleId,
                stat.getArticleLikeCount()
        );
    }

    @Transactional
    public ArticleLikeCountResponse unlike(Long articleId, Long userId) {
        articleRepository.findById(articleId)
                .filter(a -> a.getDeletedAt() == null && !a.isArticleHidden())
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
        userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        ArticleLike like = articleLikeRepository
                .findByArticle_ArticleIdAndUser_UserId(articleId, userId)
                .orElseThrow(() -> new NotFoundException("LIKE_NOT_FOUND"));
        articleLikeRepository.delete(like);

        ArticleStat stat = articleStatRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        stat.decreaseArticleLikeCount();

        return ArticleLikeCountResponse.from(
                articleId,
                stat.getArticleLikeCount()
        );
    }

    public ArticleLikeCountResponse readCount(Long articleId) {
        articleRepository.findById(articleId)
                .filter(a -> a.getDeletedAt() == null && !a.isArticleHidden())
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));

        ArticleStat stat = articleStatRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        return ArticleLikeCountResponse.from(
                articleId,
                stat.getArticleLikeCount()
        );
    }
}
