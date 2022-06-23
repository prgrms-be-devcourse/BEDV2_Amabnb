package com.prgrms.amabnb.image.api;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
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

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", imageName='" + imageName +
                '}';
    }
}
