package com.prgrms.amabnb.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchRoomResponse {

    private Long roomId;
    private int price;
    private String address;
    private int bedCnt;
}
