package com.example.spring_rest_api.view.service;

import com.example.spring_rest_api.view.entity.ArticleView;
import com.example.spring_rest_api.view.repository.ArticleViewCountMemoryRepository;
import com.example.spring_rest_api.view.repository.ArticleViewMemoryRepository;
import com.example.spring_rest_api.view.service.response.ArticleViewCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
    private final ArticleViewMemoryRepository articleViewMemoryRepository;
    private final ArticleViewCountMemoryRepository articleViewCountMemoryRepository;
    private Long sequence = 0L;

    public ArticleViewCountResponse increaseCount(Long articleId, Long userId) {
        ArticleView findArticleView = articleViewMemoryRepository.findByArticleIdAndUserId(articleId, userId);
        if (findArticleView == null) {
            articleViewMemoryRepository.save(ArticleView.init(
                    sequence++,
                    articleId,
                    userId
            ));
            articleViewCountMemoryRepository.increase(articleId);
        } else {
            if (findArticleView.getUpdatedAt().plusDays(1).compareTo(LocalDateTime.now()) < 0) {
                articleViewMemoryRepository.update(findArticleView);
                articleViewCountMemoryRepository.increase(articleId);
            }
        }

        return ArticleViewCountResponse.from(
                articleId,
                articleViewCountMemoryRepository.read(articleId)
        );
    }

    public ArticleViewCountResponse readCount(Long articleId) {
        return ArticleViewCountResponse.from(
                articleId,
                articleViewCountMemoryRepository.read(articleId)
        );
    }
}
