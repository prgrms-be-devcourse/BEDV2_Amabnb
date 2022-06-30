package com.prgrms.amabnb.room.dto.response;

import java.util.List;

import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;

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
    private RoomType roomType;
    private RoomScope roomScope;
    private List<RoomImageResponse> imagePaths;

    public static RoomResponse from(Room room) {

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
            .roomType(room.getRoomType())
            .roomScope(room.getRoomScope())
            .imagePaths(RoomImageResponse.from(room.getRoomImages()))
            .build();

    }

}
