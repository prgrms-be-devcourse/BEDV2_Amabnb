package com.prgrms.amabnb.room.dto.request;

import java.util.List;
import java.util.Objects;

import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static SearchRoomFilterCondition from(String minBeds, String minBedrooms, String minBathrooms,
        String minPrice, String maxPrice, List<RoomType> roomTypes, List<RoomScope> roomScopes) {

        return SearchRoomFilterCondition.builder()
            .minBeds(Objects.isNull(minBeds) ? null : Integer.valueOf(minBeds))
            .minBedrooms(Objects.isNull(minBedrooms) ? null : Integer.valueOf(minBedrooms))
            .minBathrooms(Objects.isNull(minBathrooms) ? null : Integer.valueOf(minBathrooms))
            .minPrice(Objects.isNull(minPrice) ? null : Integer.valueOf(minPrice))
            .maxPrice(Objects.isNull(maxPrice) ? null : Integer.valueOf(maxPrice))
            .roomTypes(Objects.isNull(roomTypes) ? null : roomTypes)
            .roomScopes(Objects.isNull(roomScopes) ? null : roomScopes)
            .build();
    }

}

