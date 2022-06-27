package com.prgrms.amabnb.image.api;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.amabnb.infra.s3.AWSS3Uploader;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final AWSS3Uploader s3Uploader;

    @PostMapping("/room-images")
    public List<String> upload(@RequestParam("images") MultipartFile[] images) throws IOException {
        return s3Uploader.upload(images, "static");
    }
}
