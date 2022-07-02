package com.prgrms.amabnb.reservation.dto.response;

import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;

public record RoomInfoResponse(
    long roomId,
    String name,
    RoomAddress roomAddress
) {
    public static RoomInfoResponse from(Room room) {
        return new RoomInfoResponse(
            room.getId(),
            room.getName(),
            room.getAddress());
    }
    
}
