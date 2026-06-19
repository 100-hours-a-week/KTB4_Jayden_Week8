package com.example.spring_rest_api.article.entity;

import com.example.spring_rest_api.article.entity.id.TempArticleId;
import com.example.spring_rest_api.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TempArticle {
    @EmbeddedId
    private TempArticleId tempArticleId;

    @MapsId("userId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String content;
    private List<String> contentImages;
    private LocalDateTime savedAt;

    public static TempArticle create(User user, String title, String content, List<String> contentImages) {
        TempArticle article = new TempArticle();
        article.user = user;
        article.title = title;
        article.content = content;
        article.contentImages = contentImages;
        article.savedAt = LocalDateTime.now();
        return article;
    }
}
