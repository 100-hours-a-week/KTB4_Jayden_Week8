package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.article.repository.ArticleMemoryRepository;
import com.example.spring_rest_api.article.repository.ArticleReportMemoryRepository;
import com.example.spring_rest_api.article.repository.ArticleTempMemoryRepository;
import com.example.spring_rest_api.article.service.request.ArticleCreateRequest;
import com.example.spring_rest_api.article.service.request.ArticleUpdateRequest;
import com.example.spring_rest_api.article.service.response.ArticleResponse;
import com.example.spring_rest_api.comment.repository.CommentCountMemoryRepository;
import com.example.spring_rest_api.common.exception.BadRequestException;
import com.example.spring_rest_api.common.exception.ForbiddenException;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.common.exception.RequestConflictException;
import com.example.spring_rest_api.like.repository.ArticleLikeCountMemoryRepository;
import com.example.spring_rest_api.user.repository.UserMemoryRepository;
import com.example.spring_rest_api.view.repository.ArticleViewCountMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleMemoryRepository articleMemoryRepository;
    private final ArticleTempMemoryRepository articleTempMemoryRepository;
    private final ArticleReportMemoryRepository articleReportMemoryRepository;
    private final UserMemoryRepository userMemoryRepository;
    private final CommentCountMemoryRepository commentCountMemoryRepository;
    private final ArticleLikeCountMemoryRepository articleLikeCountMemoryRepository;
    private final ArticleViewCountMemoryRepository articleViewCountMemoryRepository;
    private Long sequence = 0L;

    public ArticleResponse create(ArticleCreateRequest request) {
        throwIfUserNotFound(request);

        return ArticleResponse.from(articleMemoryRepository.save(Article.create(
                sequence++,
                request.getTitle(),
                request.getContent(),
                request.getUserId(),
                request.getContentImages()
        )));
    }

    private void throwIfUserNotFound(ArticleCreateRequest request) {
        if (userMemoryRepository.findById(request.getUserId()) == null) {
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }
    private void throwIfUserNotFound(ArticleUpdateRequest request) {
        if (userMemoryRepository.findById(request.getUserId()) == null) {
            throw new NotFoundException("USER_NOT_FOUND");
        }
    }

    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        throwIfNotAllowed(articleId, request);

        return ArticleResponse.from(articleMemoryRepository.update(
                        articleId,
                        articleMemoryRepository.findById(articleId).update(
                                request.getTitle(),
                                request.getContent(),
                                request.getContentImages()
                        )
        ));
    }

    private void throwIfNotAllowed(Long articleId, ArticleUpdateRequest request) {
                if (!articleMemoryRepository.findById(articleId).getUserId().equals(request.getUserId())) {
            throw new ForbiddenException("NOT_ALLOWED");
        }
    }

    public void saveTempArticle(Long userId, ArticleUpdateRequest request) {
        throwIfUserNotFound(request);

        articleTempMemoryRepository.save(Article.create(
                userId,
                request.getTitle(),
                request.getContent(),
                userId,
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
        throwIfArticleIsAbsent(articleId);

        return ArticleResponse.from(
                articleMemoryRepository.findById(articleId)
                        .updateCount(
                                articleLikeCountMemoryRepository.read(articleId),
                                articleViewCountMemoryRepository.read(articleId),
                                commentCountMemoryRepository.read(articleId)
                        )
        );
    }

    public List<ArticleResponse> readInfiniteScroll(Long pageSize, Long lastArticleId) {
        return articleMemoryRepository.findAllInfiniteScroll(pageSize, lastArticleId).stream()
                .map(a -> a.updateCount(
                        articleLikeCountMemoryRepository.read(a.getArticleId()),
                        articleViewCountMemoryRepository.read(a.getArticleId()),
                        commentCountMemoryRepository.read(a.getArticleId())
                ))
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

    private void throwIfArticleIsAbsent(Long articleId) {
        Article findArticle = articleMemoryRepository.findById(articleId);
        if (findArticle.isArticleDeleted() || findArticle.isArticleHidden()) {
            throw new BadRequestException("ARTICLE_UNAVAILABLE");
        }
    }
}
