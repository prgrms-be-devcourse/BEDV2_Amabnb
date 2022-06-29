package com.prgrms.amabnb.image.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
    List<String> uploadImage(List<MultipartFile> images, String dirName) throws IOException;
}
