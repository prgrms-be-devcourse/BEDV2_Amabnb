package com.prgrms.amabnb.room.dto.request;

import java.util.List;

import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;

import lombok.Getter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRoomFilterCondition {

    private Integer minBeds;
    private Integer minBedrooms;
    private Integer minBathrooms;
    private Integer minPrice;
    private Integer maxPrice;
    private List<RoomType> roomTypes;
    private List<RoomScope> roomScopes;

    public SearchRoomFilterCondition(Integer minBeds, Integer minBedrooms, Integer minBathrooms, Integer minPrice,
        Integer maxPrice, List<RoomType> roomTypes, List<RoomScope> roomScopes) {
        this.minBeds = minBeds;
        this.minBedrooms = minBedrooms;
        this.minBathrooms = minBathrooms;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.roomTypes = roomTypes;
        this.roomScopes = roomScopes;
    }
}

