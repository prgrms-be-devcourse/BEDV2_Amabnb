package com.prgrms.amabnb.image.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Long save(Image image) {
        return imageRepository.save(image).getId();
    }
//
//    @Transactional
//    public Long saveAll(Image image) {
//        return imageRepository.saveAll().getId();
//    }
}
