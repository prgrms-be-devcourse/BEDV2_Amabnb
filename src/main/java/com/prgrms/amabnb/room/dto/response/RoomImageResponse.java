package com.prgrms.amabnb.room.dto.response;

import java.util.List;

import com.prgrms.amabnb.room.entity.RoomImage;

import lombok.Getter;

@Getter
public class RoomImageResponse {

    private String imagePath;

    public RoomImageResponse(String imagePath) {
        this.imagePath = imagePath;
    }

    public static List<RoomImageResponse> from(List<RoomImage> roomImages) {
        return roomImages.stream().map(RoomImage::getImagePath).map(RoomImageResponse::new).toList();
    }

}
