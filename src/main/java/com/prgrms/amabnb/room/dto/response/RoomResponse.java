package com.prgrms.amabnb.room.dto.response;

import java.util.List;

import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomImage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResponse {

    private String name;
    private int price;
    private String description;
    private int maxGuestNum;
    private String zipcode;
    private String address;
    private String detailAddress;
    private int bedCnt;
    private int bedRoomCnt;
    private int bathRoomCnt;
    private String roomType;
    private String roomScope;
    private List<String> imagePaths;

    public static RoomResponse from(Room room) {
        List<String> roomImagePaths = room.getRoomImages().stream()
            .map(RoomImage::getImagePath)
            .toList();

        return RoomResponse.builder()
            .name(room.getName())
            .price(room.getPrice().getValue())
            .description(room.getDescription())
            .maxGuestNum(room.getMaxGuestNum())
            .zipcode(room.getAddress().getZipcode())
            .address(room.getAddress().getAddress())
            .detailAddress(room.getAddress().getDetailAddress())
            .bedCnt(room.getRoomOption().getBedCnt())
            .bedRoomCnt(room.getRoomOption().getBedRoomCnt())
            .bathRoomCnt(room.getRoomOption().getBathRoomCnt())
            .roomType(room.getRoomType().toString())
            .roomScope(room.getRoomScope().toString())
            .imagePaths(roomImagePaths)
            .build();
    }

}
