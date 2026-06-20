package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.article.entity.ArticleStat;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.repository.ArticleStatRepository;
import com.example.spring_rest_api.article.service.request.ArticleCreateRequest;
import com.example.spring_rest_api.article.service.request.ArticleUpdateRequest;
import com.example.spring_rest_api.article.service.response.ArticleReadResponse;
import com.example.spring_rest_api.article.service.response.ArticleResponse;
import com.example.spring_rest_api.common.exception.*;
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

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        throwIfTooManyRequests(request);

        Article article = articleRepository.save(Article.create(
                userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                request.getTitle(),
                request.getContent(),
                request.getContentImages()
        ));
        ArticleStat.create(article);
        return ArticleResponse.from(article);
    }

    private void throwIfTooManyRequests(ArticleCreateRequest request) {
        if (articleRepository.countWithinOneMinute(request.getUserId(), LocalDateTime.now().minusDays(1)) >= 3) {
            throw new TooManyRequestsException("1분 내 글 3개까지 작성할 수 있습니다.");
        }
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        throwIfNotValidAccess(articleId, request);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"))
                .update(
                        request.getTitle(),
                        request.getContent(),
                        request.getContentImages()
                );
        //TODO - 수정 이력 저장 추가하기
        return ArticleResponse.from(article);
    }

    private void throwIfNotValidAccess(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
        if (!request.getUserId().equals(article.getUser().getUserId())) {
            throw new ForbiddenException("ACCESS_NOT_VALID");
        }
    }

    @Transactional
    public ArticleResponse delete(Long articleId) {
        //TODO - 사용자 인가 validation 넣기
        Article deleted = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"))
                .delete();
        return ArticleResponse.from(deleted);
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
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(pageSize) :
                articleRepository.findAllInfiniteScroll(pageSize, lastArticleId);
        //TODO - 게시글 무한스크롤 수정하기
        return articles.stream()
                .map(ArticleReadResponse::from)
                .toList();
    }
}
