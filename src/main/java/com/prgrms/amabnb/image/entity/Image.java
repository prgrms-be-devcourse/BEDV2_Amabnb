package com.prgrms.amabnb.image.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue
    private Long id;

    private String imageName;

    @Builder
    public Image(Long id, String imageName) {
        this.id = id;
        this.imageName = imageName;
    }
}