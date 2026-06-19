package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.article.repository.ArticleReportMemoryRepository;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.repository.ArticleStatRepository;
import com.example.spring_rest_api.article.repository.ArticleTempMemoryRepository;
import com.example.spring_rest_api.article.service.request.ArticleCreateRequest;
import com.example.spring_rest_api.article.service.request.ArticleUpdateRequest;
import com.example.spring_rest_api.article.service.response.ArticleResponse;
import com.example.spring_rest_api.comment.repository.CommentCountMemoryRepository;
import com.example.spring_rest_api.common.exception.*;
import com.example.spring_rest_api.like.repository.ArticleLikeCountMemoryRepository;
import com.example.spring_rest_api.user.repository.UserMemoryRepository;
import com.example.spring_rest_api.view.repository.ArticleViewCountMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleStatRepository articleStatRepository;
//    private final ArticleTempRepository articleTempRepository;
//    private final ArticleReportMemoryRepository articleReportMemoryRepository;
//    private final UserMemoryRepository userMemoryRepository;
//    private final CommentCountMemoryRepository commentCountMemoryRepository;
//    private final ArticleLikeCountMemoryRepository articleLikeCountMemoryRepository;
//    private final ArticleViewCountMemoryRepository articleViewCountMemoryRepository;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        throwIfUserNotFound(request);
        throwIfTooManyRequests(request);

        Article article = articleRepository.save(Article.create(
                request.getUserId(),
                request.getTitle(),
                request.getContent(),
                request.getContentImages()
        ));
        return ArticleResponse.from(article);
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

    private void throwIfTooManyRequests(ArticleCreateRequest request) {
        if (articleRepository.countWithinOneMinute(request.getUserId()) >= 3) {
            throw new TooManyRequestsException("1분 내 글 3개까지 작성할 수 있습니다.");
        }
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        throwIfNotAllowed(articleId, request);

        return ArticleResponse.from(articleRepository.update(
                        articleId,
                        articleRepository.findById(articleId).update(
                                request.getTitle(),
                                request.getContent(),
                                request.getContentImages()
                        )
        ));
    }

    private void throwIfNotAllowed(Long articleId, ArticleUpdateRequest request) {
                if (!articleRepository.findById(articleId).getUserId().equals(request.getUserId())) {
            throw new ForbiddenException("NOT_ALLOWED");
        }
    }

    @Transactional
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

    @Transactional
    public ArticleResponse delete(Long articleId) {
        return ArticleResponse.from(
                articleRepository.delete(articleId)
        );
    }

    public ArticleResponse read(Long articleId) {
        throwIfArticleIsAbsent(articleId);

        return ArticleResponse.from(
                articleRepository.findById(articleId)
                        .updateCount(
                                articleLikeCountMemoryRepository.read(articleId),
                                articleViewCountMemoryRepository.read(articleId),
                                commentCountMemoryRepository.read(articleId)
                        )
        );
    }

    public List<ArticleResponse> readInfiniteScroll(Long pageSize, Long lastArticleId) {
        return lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(pageSize) :
                articleRepository.findAllInfiniteScroll(pageSize, lastArticleId).stream()
                .map(a -> a.updateCount(
                        articleLikeCountMemoryRepository.read(a.getArticleId()),
                        articleViewCountMemoryRepository.read(a.getArticleId()),
                        commentCountMemoryRepository.read(a.getArticleId())
                ))
                .map(ArticleResponse::from)
                .toList();
    }

    @Transactional
    public void report(Long articleId, Long reportingUserId) {
        if (articleReportMemoryRepository.findByArticleIdAndReportingUserId(articleId, reportingUserId) == null) {
            articleRepository.report(articleId);
            articleReportMemoryRepository.report(articleId, reportingUserId);
        } else {
            throw new RequestConflictException("ALREADY_REPORTED");
        }
    }

    private void throwIfArticleIsAbsent(Long articleId) {
        Article findArticle = articleRepository.findById(articleId);
        if (findArticle.isArticleDeleted() || findArticle.isArticleHidden()) {
            throw new BadRequestException("ARTICLE_UNAVAILABLE");
        }
    }
}
