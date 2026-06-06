package com.example.spring_rest_api.like.service;

import com.example.spring_rest_api.common.exception.BadRequestException;
import com.example.spring_rest_api.like.entity.ArticleLike;
import com.example.spring_rest_api.like.repository.ArticleLikeCountMemoryRepository;
import com.example.spring_rest_api.like.repository.ArticleLikeMemoryRepository;
import com.example.spring_rest_api.like.service.response.ArticleLikeCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeMemoryRepository articleLikeMemoryRepository;
    private final ArticleLikeCountMemoryRepository articleLikeCountMemoryRepository;
    private Long sequence = 0L;

    public ArticleLikeCountResponse like(Long articleId, Long userId) {
        if (articleLikeMemoryRepository.findByArticleIdAndUserId(articleId, userId) == null) {
            articleLikeMemoryRepository.save(ArticleLike.create(
                    sequence++,
                    articleId,
                    userId
            ));
            articleLikeCountMemoryRepository.increase(articleId);
        } else {
            throw new BadRequestException("LIKE_BAD_REQUEST");
        }

        return ArticleLikeCountResponse.from(
                articleId,
                articleLikeCountMemoryRepository.read(articleId)
        );
    }

    public ArticleLikeCountResponse unlike(Long articleId, Long userId) {
        ArticleLike findArticleLike = articleLikeMemoryRepository.findByArticleIdAndUserId(articleId, userId);
        if (findArticleLike != null) {
            articleLikeMemoryRepository.delete(findArticleLike);
            articleLikeCountMemoryRepository.decrease(articleId);
        } else {
            throw new BadRequestException("UNLIKE_BAD_REQUEST");
        }
        return ArticleLikeCountResponse.from(
                articleId,
                articleLikeCountMemoryRepository.read(articleId)
        );
    }

    public ArticleLikeCountResponse readCount(Long articleId) {
        return ArticleLikeCountResponse.from(
                articleId,
                articleLikeCountMemoryRepository.read(articleId)
        );
    }
}
