package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.Report;
import com.example.spring_rest_api.article.repository.ArticleReportRepository;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.service.request.ArticleReportRequest;
import com.example.spring_rest_api.common.exception.BadRequestException;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleReportService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleReportRepository reportRepository;

    @Transactional
    public void report(Long articleId, Long reportingUserId, ArticleReportRequest request) {
        if (reportRepository.findByArticleIdAndUserId(articleId, reportingUserId).isPresent()) {
            throw new BadRequestException("ALREADY_REPORTED");
        }
        reportRepository.save(Report.create(
                articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND")),
                userRepository.findById(reportingUserId).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                request.getReportType(),
                request.getReason()
        ));
    }

}
