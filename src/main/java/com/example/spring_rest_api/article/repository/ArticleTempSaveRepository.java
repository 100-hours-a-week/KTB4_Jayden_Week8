package com.example.spring_rest_api.article.repository;

import com.example.spring_rest_api.article.entity.TempArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleTempSaveRepository extends JpaRepository<TempArticle, Long> {

    Optional<TempArticle> findByUserId(Long userId);
}
