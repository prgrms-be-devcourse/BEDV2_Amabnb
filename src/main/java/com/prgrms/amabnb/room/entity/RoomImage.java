package com.prgrms.amabnb.room.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomImage {

    @Id
    @GeneratedValue
    private Long id;

    private String imagePath;

    public RoomImage(String imagePath) {
        this(null, imagePath);
    }

    public RoomImage(Long id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }

}
