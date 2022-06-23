package com.prgrms.amabnb.image;

import com.prgrms.amabnb.image.api.Image;
import com.prgrms.amabnb.image.api.ImageService;
import com.prgrms.amabnb.room.entity.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest(includeFilters = {@ComponentScan.Filter(Service.class)})
public class ImageServiceTest {

    @Autowired
    ImageService imageService;

    @Test
    void test_save_image() {
        Room room = Room.builder()
                .id(1L)
                .build();

        Image img = Image.builder()
                .imageName("thisisImageName.png")
                .roomId(room)
                .build();

        Long id = imageService.save(img);

        System.out.println(">>>" + id);

//        List<Image> selectedImageList = imageService.findAll();
//
//        for (Image m : selectedImageList) {
//            System.out.println(">>>" + m.toString());
//        }

    }

    @Test
    void test_save_all_image() {
        Room room = Room.builder()
                .id(1L)
                .build();

        List<Image> imageList = new ArrayList<>();

        Image img = Image.builder()
                .imageName("thisisImageName.png")
                .roomId(room)
                .build();

        Image img2 = Image.builder()
                .imageName("thisisImageName.png")
                .roomId(room)
                .build();

        Image img3 = Image.builder()
                .imageName("thisisImageName.png")
                .roomId(room)
                .build();

        imageList.add(img);
        imageList.add(img2);
        imageList.add(img3);

        imageService.saveAll(imageList);

        List<Image> seletedImageList = imageService.findAll();

        for (Image m : seletedImageList) {
            System.out.println(">>>" + m.toString());
        }
    }
}
