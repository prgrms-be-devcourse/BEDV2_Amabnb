package com.prgrms.amabnb.room.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public RoomImage(String imagePath) {
        this(null, imagePath);
    }

    public RoomImage(Long id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
