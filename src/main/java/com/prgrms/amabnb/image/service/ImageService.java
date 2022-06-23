package com.prgrms.amabnb.image.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.amabnb.image.entity.Image;
import com.prgrms.amabnb.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Long save(Image image) {
        return imageRepository.save(image).getId();
    }

    @Transactional
    public void saveAll(List<Image> images) {
        imageRepository.saveAll(images);
    }

    @Transactional
    public List<Image> findAll() {
        return imageRepository.findAll();
    }
}
