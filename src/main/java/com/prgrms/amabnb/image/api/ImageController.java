package com.prgrms.amabnb.image.api;

import static org.springframework.http.HttpHeaders.*;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.amabnb.image.entity.Image;
import com.prgrms.amabnb.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    String dirPath = "/Users/willkim/IdeaProjects/BEDV2_Amabnb/src/main/resources/images";

    @PostMapping("/room-images")
    public ResponseEntity<Void> uploadImage(
        @RequestHeader(AUTHORIZATION) String userId,
        @RequestParam("images") MultipartFile[] files
    ) {
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
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.created(URI.create("/room-images/")).build();
    }
}
