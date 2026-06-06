package com.example.spring_rest_api.article.repository;

import com.example.spring_rest_api.article.entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class ArticleReportMemoryRepository {
    private final Map<Long, Report> articleReportStorage = new ConcurrentHashMap<>();
    private Long sequence = 0L;

    public Report findByArticleIdAndReportingUserId(Long articleId, Long reportingUserId) {
        return articleReportStorage.entrySet().stream()
                .filter(entry ->
                        entry.getValue().getArticleId().equals(articleId) &&
                                entry.getValue().getReportingUserId().equals(reportingUserId)
                )
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public void report(Long articleId, Long reportingUserId) {
        Report report = Report.create(
                sequence++,
                articleId,
                reportingUserId
        );
        articleReportStorage.put(report.getReportId(), report);
    }
}







