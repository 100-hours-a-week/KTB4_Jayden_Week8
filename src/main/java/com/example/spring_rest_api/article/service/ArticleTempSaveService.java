package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.TempArticle;
import com.example.spring_rest_api.article.repository.ArticleTempSaveRepository;
import com.example.spring_rest_api.article.service.request.ArticleTempSaveRequest;
import com.example.spring_rest_api.article.service.response.ArticleTempSaveResponse;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.image.entity.ImageFile;
import com.example.spring_rest_api.image.repository.ImageFileRepository;
import com.example.spring_rest_api.image.util.ImageFileUtil;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleTempSaveService {
    private final ArticleTempSaveRepository tempSaveRepository;
    private final UserRepository userRepository;
    private final ImageFileRepository imageFileRepository;

    @Transactional
    public void saveTempArticle(Long userId, ArticleTempSaveRequest request) {
        TempArticle tempArticle = tempSaveRepository.findByUserId(userId).orElse(null);
        if (tempArticle == null) {
            tempSaveRepository.save(TempArticle.create(
                    userRepository.findById(userId).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                    request.getTitle(),
                    request.getContent(),
                    request.getContentImageUrls()
            ));
        } else {
            tempArticle.update(
                    request.getTitle(),
                    request.getContent(),
                    request.getContentImageUrls()
            );
        }
    }

    public ArticleTempSaveResponse readTempArticle(Long userId) {
        TempArticle article = tempSaveRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("TEMPSAVE_NOT_FOUND"));
        return ArticleTempSaveResponse.from(article);
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
