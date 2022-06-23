package com.prgrms.amabnb.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prgrms.amabnb.image.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
