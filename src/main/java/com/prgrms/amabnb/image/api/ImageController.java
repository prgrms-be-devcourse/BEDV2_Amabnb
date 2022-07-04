package com.prgrms.amabnb.image.api;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.amabnb.common.model.ApiResponse;
import com.prgrms.amabnb.image.service.ImageUploader;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageUploader imageUploader;

    @PostMapping("/room-images")
    public ResponseEntity<ApiResponse<List<String>>> upload(@RequestParam List<MultipartFile> images) throws
        IOException {
        return ResponseEntity.ok(new ApiResponse<>(imageUploader.uploadImage(images)));
    }
}
