package com.prgrms.amabnb.room.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomScrollResponse {
    private Long id;
    private String name;
    private int price;
    private List<String> imagePaths;
}
