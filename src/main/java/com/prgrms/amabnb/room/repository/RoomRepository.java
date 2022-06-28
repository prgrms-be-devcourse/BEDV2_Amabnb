package com.prgrms.amabnb.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrms.amabnb.room.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long>, QueryRoomRepository {
}
