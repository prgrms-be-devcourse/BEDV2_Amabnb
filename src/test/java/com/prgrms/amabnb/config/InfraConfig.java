package com.prgrms.amabnb.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.amabnb.image.service.ImageUploader;

@TestConfiguration
public class InfraConfig {

    @Bean
    public ImageUploader imageUploader() {
        return new MockImageUploader();
    }

    static class MockImageUploader implements ImageUploader {

        @Override
        public List<String> uploadImage(List<MultipartFile> images) throws IOException {

            List<String> mock = new ArrayList<>();

            for (int i = 1; i < images.size(); i++) {
                mock.add(UUID.randomUUID().toString());
            }

            return mock;
        }
    }

}
