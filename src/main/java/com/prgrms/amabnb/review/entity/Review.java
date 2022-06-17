package com.prgrms.amabnb.review.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.prgrms.amabnb.common.model.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String content;

    private int score;

    @Builder
    public Review(Long id, String content, int score) {
        this.id = id;
        this.content = content;
        this.score = score;
    }

}
