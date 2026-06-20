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

    public static ArticleStat create(Article article) {
        ArticleStat articleStat = new ArticleStat();
        articleStat.article = article;
        articleStat.commentCount = 0L;
        articleStat.articleLikeCount = 0L;
        articleStat.articleViewCount = 0L;
        articleStat.articleReportCount = 0L;
        return articleStat;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount--;
    }

    public void increaseArticleLikeCount() {
        this.articleLikeCount++;
    }

    public void decreaseArticleLikeCount() {
        this.articleLikeCount--;
    }

    public void incrementArticleViewCount() {
        this.articleViewCount++;
    }

    public void incrementArticleReportCount() {
        this.articleReportCount++;

        if (this.getArticleReportCount() >= 5) {
            article.hideArticle();
        }
    }
}
