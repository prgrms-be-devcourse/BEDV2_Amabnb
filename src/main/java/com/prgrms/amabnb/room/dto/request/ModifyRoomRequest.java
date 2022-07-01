package com.prgrms.amabnb.room.dto.request;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifyRoomRequest {

    @NotBlank
    private String name;

    @Max(value = 10000000)
    @PositiveOrZero
    private int price;

    @NotBlank
    private String description;

    @Positive
    private int maxGuestNum;

    @PositiveOrZero
    private int bedCnt;

    @PositiveOrZero
    private int bedRoomCnt;

    @PositiveOrZero
    private int bathRoomCnt;

    @Builder
    public ModifyRoomRequest(String name, int price, String description, int maxGuestNum, int bedCnt, int bedRoomCnt,
        int bathRoomCnt, List<String> imagePaths) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.maxGuestNum = maxGuestNum;
        this.bedCnt = bedCnt;
        this.bedRoomCnt = bedRoomCnt;
        this.bathRoomCnt = bathRoomCnt;
    }

}
