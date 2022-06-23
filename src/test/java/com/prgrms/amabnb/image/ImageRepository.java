package com.prgrms.amabnb.image;

import com.prgrms.amabnb.image.api.Image;
import com.prgrms.amabnb.image.api.ImageService;
import com.prgrms.amabnb.room.entity.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ImageRepository {

    @Autowired
    private com.prgrms.amabnb.image.api.ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;


    @Test
    void 이미지_리파지토리_테스트() {

        Room room = Room.builder()
                .id(1L)
                .build();

        Image img = Image.builder()
                .imageName("thisisImageName.png")
                .roomId(room)
                .build();

        Image savedImage = imageRepository.save(img);
        System.out.println(">>> " + savedImage.toString());

    }

}
