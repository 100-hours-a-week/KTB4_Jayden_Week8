package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.article.repository.ArticleMemoryRepository;
import com.example.spring_rest_api.article.repository.ArticleReportMemoryRepository;
import com.example.spring_rest_api.article.repository.ArticleTempMemoryRepository;
import com.example.spring_rest_api.article.service.request.ArticleCreateRequest;
import com.example.spring_rest_api.article.service.request.ArticleUpdateRequest;
import com.example.spring_rest_api.article.service.response.ArticleResponse;
import com.example.spring_rest_api.common.exception.RequestConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleMemoryRepository articleMemoryRepository;
    private final ArticleTempMemoryRepository articleTempMemoryRepository;
    private final ArticleReportMemoryRepository articleReportMemoryRepository;
    private Long sequence = 0L;

    public ArticleResponse create(ArticleCreateRequest request) {
        return ArticleResponse.from(articleMemoryRepository.save(Article.create(
                sequence++,
                request.getTitle(),
                request.getContent(),
                request.getUserId(),
                request.getContentImages()
        )));
    }

    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        return ArticleResponse.from(articleMemoryRepository.update(
                        articleId,
                        articleMemoryRepository.findById(articleId).update(
                                request.getTitle(),
                                request.getContent(),
                                request.getContentImages()
                        )
        ));
    }

    public void saveTempArticle(Long userId, ArticleUpdateRequest request) {
        articleTempMemoryRepository.save(Article.create(
                userId,
                request.getTitle(),
                request.getContent(),
                request.getUserId(),
                request.getContentImages()
        ));
    }

    public ArticleResponse readTempArticle(Long userId) {
        return ArticleResponse.from(
                articleTempMemoryRepository.read(userId)
        );
    }

    public ArticleResponse delete(Long articleId) {
        return ArticleResponse.from(
                articleMemoryRepository.delete(articleId)
        );
    }

    public ArticleResponse read(Long articleId) {
        return ArticleResponse.from(
                articleMemoryRepository.findById(articleId)
        );
    }

    public List<ArticleResponse> readInfiniteScroll(Long pageSize, Long lastArticleId) {
        return articleMemoryRepository.findAllInfiniteScroll(pageSize, lastArticleId).stream()
                .map(ArticleResponse::from)
                .toList();
    }

    public void report(Long articleId, Long reportingUserId) {
        if (articleReportMemoryRepository.findByArticleIdAndReportingUserId(articleId, reportingUserId) == null) {
            articleMemoryRepository.report(articleId);
            articleReportMemoryRepository.report(articleId, reportingUserId);
        } else {
            throw new RequestConflictException("ALREADY_REPORTED");
        }
    }
}
