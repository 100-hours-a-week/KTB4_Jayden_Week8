package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.article.entity.ArticleStat;
import com.example.spring_rest_api.article.entity.ArticleUpdateHistory;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.repository.ArticleStatRepository;
import com.example.spring_rest_api.article.repository.ArticleUpdateHistoryRepository;
import com.example.spring_rest_api.article.service.request.ArticleCreateRequest;
import com.example.spring_rest_api.article.service.request.ArticleDeleteRequest;
import com.example.spring_rest_api.article.service.request.ArticleUpdateRequest;
import com.example.spring_rest_api.article.service.response.ArticleReadResponse;
import com.example.spring_rest_api.article.service.response.ArticleResponse;
import com.example.spring_rest_api.common.exception.BadRequestException;
import com.example.spring_rest_api.common.exception.ForbiddenException;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.common.exception.TooManyRequestsException;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleStatRepository statRepository;
    private final UserRepository userRepository;
    private final ArticleUpdateHistoryRepository historyRepository;

    @Transactional
    public ArticleResponse create(Long userId, ArticleCreateRequest request) {
        throwIfTooManyRequests(userId);

        Article article = articleRepository.save(Article.create(
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                request.getTitle(),
                request.getContent(),
                request.getContentImages()
        ));
        ArticleStat.create(article);
        return ArticleResponse.from(article);
    }

    private void throwIfTooManyRequests(Long userId) {
        if (articleRepository.countWithinOneMinute(userId, LocalDateTime.now().minusMinutes(1)) >= 3) {
            throw new TooManyRequestsException("1분 내 글 3개까지 작성할 수 있습니다.");
        }
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        throwIfAccessNotValid(articleId, request.getUserId());

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));

        historyRepository.save(ArticleUpdateHistory.create(
                article,
                article.getTitle(),
                article.getContent(),
                article.getContentImages()
        ));

        return ArticleResponse.from(
                article.update(
                        request.getTitle(),
                        request.getContent(),
                        request.getContentImages()
                )
        );
    }

    private void throwIfAccessNotValid(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
        if (!userId.equals(article.getUser().getUserId()) || article.getUser().getDeletedAt() != null) {
            throw new ForbiddenException("ACCESS_NOT_VALID");
        }
    }

    @Transactional
    public ArticleResponse delete(Long articleId, ArticleDeleteRequest request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
        throwIfAccessNotValid(articleId, request.getUserId());

        return ArticleResponse.from(article.delete());
    }

    public ArticleReadResponse read(Long articleId) {
        throwIfArticleIsAbsent(articleId);

        return ArticleReadResponse.from(
                articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND")),
                statRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"))
        );
    }

    private void throwIfArticleIsAbsent(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
        if (article.getDeletedAt() != null || article.isArticleHidden()) {
            throw new BadRequestException("ARTICLE_UNAVAILABLE");
        }
    }

    public List<ArticleReadResponse> readInfiniteScroll(Long pageSize, Long lastArticleId) {
        return lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(pageSize) :
                articleRepository.findAllInfiniteScroll(pageSize, lastArticleId);
    }
}
