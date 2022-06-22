package com.prgrms.amabnb.image.api;

import com.prgrms.amabnb.room.entity.Room;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue
    private Long id;

    private String imageName;

    @ManyToOne
    private Room roomId;

    @Builder
    public Image(Long id, String imageName, Room roomId) {
        this.id = id;
        this.imageName = imageName;
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", imageName='" + imageName +
                '}';
    }
}
