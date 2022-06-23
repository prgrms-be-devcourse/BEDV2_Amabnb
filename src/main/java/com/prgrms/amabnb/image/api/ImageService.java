package com.prgrms.amabnb.image.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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
