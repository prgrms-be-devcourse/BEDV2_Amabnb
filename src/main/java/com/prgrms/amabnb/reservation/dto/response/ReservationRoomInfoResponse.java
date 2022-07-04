package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationRoomInfoResponse {

    private long roomId;
    private String name;
    private RoomAddress roomAddress;

    public ReservationRoomInfoResponse(long roomId, String name, RoomAddress roomAddress) {
        this.roomId = roomId;
        this.name = name;
        this.roomAddress = roomAddress;
    }

    public static ReservationRoomInfoResponse from(Room room) {
        return new ReservationRoomInfoResponse(
            room.getId(),
            room.getName(),
            room.getAddress());
    }

}
