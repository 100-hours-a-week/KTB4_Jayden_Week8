package com.example.spring_rest_api.article.repository;

import com.example.spring_rest_api.article.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findByArticleIdAndUserId(Long articleId, Long userId);
}
