package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.TempArticle;
import com.example.spring_rest_api.article.repository.ArticleTempSaveRepository;
import com.example.spring_rest_api.article.service.request.ArticleTempSaveRequest;
import com.example.spring_rest_api.article.service.response.ArticleTempSaveResponse;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleTempSaveService {
    private final ArticleTempSaveRepository tempSaveRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveTempArticle(Long userId, ArticleTempSaveRequest request) {
        tempSaveRepository.save(TempArticle.create(
                userRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                request.getTitle(),
                request.getContent(),
                request.getContentImages()
        ));
    }

    public ArticleTempSaveResponse readTempArticle(Long userId) {
        TempArticle article = tempSaveRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("TEMPSAVE_NOT_FOUND"));
        return ArticleTempSaveResponse.from(article);
    }
}
