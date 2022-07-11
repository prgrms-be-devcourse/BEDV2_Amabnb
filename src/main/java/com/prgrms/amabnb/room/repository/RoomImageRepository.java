package com.prgrms.amabnb.room.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomImage;

public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {

    List<RoomImage> findRoomImagesByRoom(Room room);

}
