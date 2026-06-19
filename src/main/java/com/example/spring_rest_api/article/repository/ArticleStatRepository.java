package com.example.spring_rest_api.article.repository;

import com.example.spring_rest_api.article.entity.ArticleStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleStatRepository extends JpaRepository<ArticleStat, Long> {
}
