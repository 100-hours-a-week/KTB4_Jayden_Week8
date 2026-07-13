package com.example.spring_rest_api.article.entity;

import com.example.spring_rest_api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "temp_articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempArticle {
    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private List<String> contentImages = new ArrayList<>();

    private LocalDateTime savedAt;

    public static TempArticle create(User user, String title, String content, List<String> contentImages) {
        TempArticle article = new TempArticle();
        article.user = user;
        article.title = title;
        article.content = content;
        article.contentImages = contentImages == null ? new ArrayList<>() : new ArrayList<>(contentImages);
        article.savedAt = LocalDateTime.now();
        return article;
    }

    public TempArticle update(String title, String content, List<String> contentImages) {
        this.title = title;
        this.content = content;
        this.contentImages = contentImages == null ? new ArrayList<>() : new ArrayList<>(contentImages);
        this.savedAt = LocalDateTime.now();
        return this;
    }
}
