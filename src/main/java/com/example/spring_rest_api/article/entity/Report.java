package com.example.spring_rest_api.article.entity;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class Report {
    private Long reportId;
    private Long articleId;
    private Long reportingUserId;
    private LocalDateTime createdAt;

    public static Report create(Long reportId, Long articleId, Long reportingUserId) {
        Report report = new Report();
        report.reportId = reportId;
        report.articleId = articleId;
        report.reportingUserId = reportingUserId;
        report.createdAt = LocalDateTime.now();
        return report;
    }
}
