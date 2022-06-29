package com.prgrms.amabnb.room.dto.request;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomImage;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRoomRequest {

    @NotBlank
    private String name;

    @Max(value = 10000000)
    @PositiveOrZero
    private int price;

    @NotBlank
    private String description;

    @Positive
    private int maxGuestNum;

    @Pattern(regexp = "^\\d{5}$")
    private String zipcode;

    @NotBlank
    private String address;

    private String detailAddress;

    @PositiveOrZero
    private int bedCnt;

    @PositiveOrZero
    private int bedRoomCnt;

    @PositiveOrZero
    private int bathRoomCnt;

    @NotNull
    private RoomType roomType;

    @NotNull
    private RoomScope roomScope;

    @NotNull
    private List<String> imagePaths;

    @Builder
    public CreateRoomRequest(String name, int price, String description, int maxGuestNum,
        String zipcode, String address, String detailAddress, int bedCnt, int bedRoomCnt, int bathRoomCnt,
        RoomType roomType, RoomScope roomScope, List<String> imagePaths) {
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

    public Room toRoom() {
        return Room.builder()
            .name(name)
            .price(new Money(price))
            .description(description)
            .maxGuestNum(maxGuestNum)
            .address(new RoomAddress(zipcode, address, detailAddress))
            .roomOption(new RoomOption(bedCnt, bedRoomCnt, bathRoomCnt))
            .roomType(roomType)
            .roomScope(roomScope)
            .build();
    }

    public List<RoomImage> toRoomImages() {

        return imagePaths.stream().map(RoomImage::new).toList();

    }

}
