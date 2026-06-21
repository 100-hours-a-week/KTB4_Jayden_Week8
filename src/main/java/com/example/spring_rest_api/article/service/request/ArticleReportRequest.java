package com.example.spring_rest_api.article.service.request;

import com.example.spring_rest_api.article.entity.ReportType;
import com.example.spring_rest_api.common.validation.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ArticleReportRequest {
    @NotNull
    @ValidEnum(enumClass =  ReportType.class, message = "타입을 선정해주세요.")
    private String reportType;
    @NotBlank
    @Size(min = 1, max = 500)
    private String reason;
}
