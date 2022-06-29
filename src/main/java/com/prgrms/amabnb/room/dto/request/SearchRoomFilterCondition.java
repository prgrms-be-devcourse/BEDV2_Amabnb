package com.prgrms.amabnb.room.dto.request;

import java.util.List;

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
    private List<String> roomTypes;
    private List<String> roomScopes;

}

