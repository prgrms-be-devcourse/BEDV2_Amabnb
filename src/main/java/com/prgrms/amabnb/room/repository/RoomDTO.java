package com.prgrms.amabnb.room.repository;

import java.util.List;

import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;

import lombok.Builder;

public class RoomDTO {

    private Long id;
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
    private List<String> imagePaths;

    @Builder
    public RoomDTO(Long id, String name, int price, String description, int maxGuestNum, String zipcode,
        String address, String detailAddress, int bedCnt, int bedRoomCnt, int bathRoomCnt,
        RoomType roomType, RoomScope roomScope, List<String> imagePaths) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.maxGuestNum = maxGuestNum;
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.bedCnt = bedCnt;
        this.bedRoomCnt = bedRoomCnt;
        this.bathRoomCnt = bathRoomCnt;
        this.roomType = roomType;
        this.roomScope = roomScope;
        this.imagePaths = imagePaths;
    }
}
