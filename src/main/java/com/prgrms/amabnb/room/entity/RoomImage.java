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

    private String filePath;

    public RoomImage(String filePath) {
        this(null, filePath);
    }

    public RoomImage(Long id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

}
