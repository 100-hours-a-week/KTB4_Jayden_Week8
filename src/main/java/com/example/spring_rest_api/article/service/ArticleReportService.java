package com.example.spring_rest_api.article.service;

import com.example.spring_rest_api.article.entity.ArticleStat;
import com.example.spring_rest_api.article.entity.Report;
import com.example.spring_rest_api.article.entity.ReportType;
import com.example.spring_rest_api.article.repository.ArticleReportRepository;
import com.example.spring_rest_api.article.repository.ArticleRepository;
import com.example.spring_rest_api.article.repository.ArticleStatRepository;
import com.example.spring_rest_api.article.service.request.ArticleReportRequest;
import com.example.spring_rest_api.common.exception.BadRequestException;
import com.example.spring_rest_api.common.exception.NotFoundException;
import com.example.spring_rest_api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleReportService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleReportRepository reportRepository;
    private final ArticleStatRepository articleStatRepository;

    @Transactional
    public void report(Long articleId, Long reportingUserId, ArticleReportRequest request) {
        if (reportRepository.findByArticle_ArticleIdAndUser_UserId(articleId, reportingUserId).isPresent()) {
            throw new BadRequestException("ALREADY_REPORTED");
        }
        ArticleStat stat = articleStatRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_STAT_NOT_FOUND"));
        stat.incrementArticleReportCount();

        reportRepository.save(Report.create(
                articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("ARTICLE_NOT_FOUND")),
                userRepository.findById(reportingUserId).orElseThrow(() -> new NotFoundException("USER_NOT_FOUND")),
                ReportType.valueOf(request.getReportType()),
                request.getReason()
        ));
    }

}
