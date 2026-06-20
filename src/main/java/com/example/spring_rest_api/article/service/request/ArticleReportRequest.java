package com.example.spring_rest_api.article.service.request;

import com.example.spring_rest_api.article.entity.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArticleReportRequest {
    @NotBlank
    private ReportType reportType;
    @NotBlank
    @Size(min = 1, max = 500)
    private String reason;
}
