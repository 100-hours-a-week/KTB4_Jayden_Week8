package com.example.spring_rest_api.article.entity;

import com.example.spring_rest_api.article.entity.id.ArticleStatId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity(name = "article_stats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleStat {
    @EmbeddedId
    private ArticleStatId articleStatId;

    @MapsId("articleId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    private Long commentCount;
    private Long articleLikeCount;
    private Long articleViewCount;
    private Long articleReportCount;

    public ArticleStat create(Article article, Long commentCount, Long articleLikeCount, Long articleViewCount, Long articleReportCount) {
        ArticleStat articleStat = new ArticleStat();
        articleStat.article = article;
        articleStat.commentCount = commentCount;
        articleStat.articleLikeCount = articleLikeCount;
        articleStat.articleViewCount = articleViewCount;
        articleStat.articleReportCount = articleReportCount;
        return articleStat;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void incrementArticleLikeCount() {
        this.articleLikeCount++;
    }

    public void incrementArticleViewCount() {
        this.articleViewCount++;
    }

    public void incrementArticleReportCount() {
        this.articleReportCount++;
    }
}
