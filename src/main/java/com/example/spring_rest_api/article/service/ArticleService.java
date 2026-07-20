package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.Article;
import com.example.spring_rest_api.article.entity.ArticleStat;
import com.example.spring_rest_api.article.entity.ArticleUpdateHistory;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.repository.ArticleUpdateHistoryRepository;
import com.example.spring_rest_api.article.service.request.ArticleCreateRequest;
import com.example.spring_rest_api.article.service.request.ArticleUpdateRequest;
import com.example.spring_rest_api.article.service.response.ArticleReadResponse;
import com.example.spring_rest_api.article.service.response.ArticleReadScrollResponse;
import com.example.spring_rest_api.article.service.response.ArticleResponse;
import com.example.spring_rest_api.common.exception.*;
import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.entity.ImageStatus;
import com.example.spring_rest_api.image.repository.ImageFileRepository;
import com.example.spring_rest_api.image.util.ImageFileUtil;
import com.example.spring_rest_api.like.repository.ArticleLikeRepository;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ArticleUpdateHistoryRepository historyRepository;
    private final ImageFileRepository imageFileRepository;


    @Transactional
    public ArticleResponse create(Long userId, ArticleCreateRequest request) {
        throwIfTooManyRequests(userId);

        Article article = articleRepository.save(Article.create(
                userRepository.findById(userId)
                        .filter(u -> u.getDeletedAt() == null)
                        .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                request.getTitle(),
                request.getContent(),
                request.getContentImageUrls()
        ));

        ArticleStat.create(article);

        List<ImageFile> contentImages = resolveArticleImages(request.getContentImageUrls());
        if (!contentImages.isEmpty()) {
            contentImages.forEach(ImageFile::changeAttachedStatus);
        }

        return ArticleResponse.from(article);
    }

    private void throwIfTooManyRequests(Long userId) {
        if (articleRepository.countWithinOneMinute(userId, LocalDateTime.now().minusMinutes(1)) >= 3) {
            throw new TooManyRequestsException("1분 내 글 3개까지 작성할 수 있습니다.");
        }
    }

    @Transactional
    public ArticleResponse update(Long userId, Long articleId, ArticleUpdateRequest request) {
        throwIfAccessNotValid(userId, articleId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));

        historyRepository.save(ArticleUpdateHistory.create(
                article,
                article.getTitle(),
                article.getContent(),
                article.getContentImages()
        ));
        List<String> previousImages = article.getContentImages();
        List<String> updatedImages = request.getContentImageUrls();
        if (!previousImages.isEmpty() && !isSameImages(previousImages, updatedImages)) {
            resolveArticleImages(article.getContentImages())
                    .forEach(ImageFile::changeTempStatus);
        }
        List<ImageFile> updatedImageFiles = getImageFiles(updatedImages);
        if  (!updatedImageFiles.isEmpty()) {
            updatedImageFiles.stream()
                    .filter(imageFile -> imageFile.getImageStatus() == ImageStatus.TEMP)
                    .forEach(ImageFile::changeAttachedStatus);
        }

        return ArticleResponse.from(
                article.update(
                        request.getTitle(),
                        request.getContent(),
                        request.getContentImageUrls()
                )
        );
    }

    private boolean isSameImages(List<String> previousImages, List<String> updatedImages) {
        if (previousImages == updatedImages) {
            return true;
        }
        if (previousImages == null || updatedImages == null) {
            return false;
        }
        return Objects.equals(previousImages, updatedImages);
    }

    @Transactional
    public ArticleResponse delete(Long userId, Long articleId) {
        throwIfAccessNotValid(userId, articleId);

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));

        if (article.getDeletedAt() != null) {
            throw new RequestConflictException("이미 삭제된 게시글입니다.");
        }

        return ArticleResponse.from(article.delete());
    }

    private void throwIfAccessNotValid(Long userId, Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
        if (article.getDeletedAt() != null || article.isArticleHidden()) {
            throw new BadRequestException("ARTICLE_UNAVAILABLE");
        }
        if (!userId.equals(article.getUser().getUserId()) || article.getUser().getDeletedAt() != null) {
            throw new ForbiddenException("ACCESS_NOT_VALID");
        }
    }

    public ArticleReadResponse read(Long articleId, Long userId) {
        throwIfArticleIsAbsent(articleId);

        return ArticleReadResponse.from(
                articleRepository.findById(articleId)
                        .filter(a -> a.getDeletedAt() == null)
                        .orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND")),
                likeRepository.findByArticle_ArticleIdAndUser_UserId(articleId, userId)
                        .isPresent()
        );
    }

    private void throwIfArticleIsAbsent(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND"));
        if (article.getDeletedAt() != null || article.isArticleHidden()) {
            throw new BadRequestException("ARTICLE_UNAVAILABLE");
        }
    }

    public List<ArticleReadScrollResponse> readInfiniteScroll(Long pageSize, Long lastArticleId) {
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(pageSize) :
                articleRepository.findAllInfiniteScroll(pageSize, lastArticleId);
        return articles.stream()
                .map(ArticleReadScrollResponse::from)
                .toList();
    }




    private List<ImageFile> resolveArticleImages(List<String> imageUrls) {
        if (imageUrls.isEmpty()) {
            return new ArrayList<>();
        }
        return imageUrls.stream()
                .map(this::resolveArticleImage)
                .toList();
    }

    private ImageFile resolveArticleImage(String articleImageUrl) {
        if (articleImageUrl == null || articleImageUrl.isBlank()) {
            return null;
        }

        String relativePath = extractPathFromUrl(articleImageUrl);
        return imageFileRepository.findByFilePath(relativePath)
                .orElseThrow(() -> new NotFoundException("ARTICLE_IMAGE_NOT_FOUND"));
    }

    private String extractPathFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            return path != null ? path : url;

        } catch (URISyntaxException e) {
            return url;
        }
    }

    private List<ImageFile> getImageFiles(List<String> imageUrls) {
        if (imageUrls.isEmpty()) {
            return new ArrayList<>();
        }
        return imageUrls.stream()
                .map(this::getImageFile)
                .toList();
    }

    private ImageFile getImageFile(String requestedPath) {
        if (requestedPath == null) {
            return null;
        }
        String relativePath = ImageFileUtil.extractPathFromUrl(requestedPath);
        return imageFileRepository.findByFilePath(relativePath)
                .orElseThrow(() -> new NotFoundException("ARTICLE_IMAGE_NOT_FOUND"));
    }
}
