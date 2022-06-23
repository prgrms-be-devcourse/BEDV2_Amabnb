package com.prgrms.amabnb.image.api;

import com.prgrms.amabnb.room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    String dirPath = "/Users/willkim/IdeaProjects/BEDV2_Amabnb/src/main/resources/images";

    @PostMapping("/upload")
    public Long uploadImage(
            @RequestHeader(AUTHORIZATION) String userId,
            @RequestParam("images") MultipartFile[] files
    ) throws IOException {

        int cnt = 1;
        List<Image> imageList = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String randomUUID = UUID.randomUUID().toString();
                String imageName = userId + "-" + randomUUID + "-" + file.getOriginalFilename() + "-" + cnt;
                String realPath = dirPath + File.separator + imageName;
                File destination = new File(realPath);
                file.transferTo(destination);

                Image image = Image.builder()
                        .imageName(realPath)
                        .build();

                imageList.add(image);
                cnt++;
            }

            imageService.saveAll(imageList);

        } catch (Exception e) {
            System.out.println("this is exception : " + e);
            return -1L;
        }

        return 1L;
    }
}
