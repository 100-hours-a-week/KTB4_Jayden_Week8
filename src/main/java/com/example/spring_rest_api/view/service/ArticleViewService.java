package com.example.spring_rest_api.view.service;

import com.example.spring_rest_api.view.repository.ArticleViewCountMemoryRepository;
import com.example.spring_rest_api.view.service.response.ArticleViewCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
    private final ArticleViewCountMemoryRepository articleViewCountMemoryRepository;

    public ArticleViewCountResponse increaseCount(Long articleId, Long userId) {
        articleViewCountMemoryRepository.increase(articleId, userId);
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
