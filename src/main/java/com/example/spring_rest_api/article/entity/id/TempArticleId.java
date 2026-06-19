package com.example.spring_rest_api.article.entity.id;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TempArticleId implements Serializable {
    private Long userId;
    private Long tempArticleId;
}
