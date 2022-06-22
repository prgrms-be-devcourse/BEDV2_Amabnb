package com.prgrms.amabnb.room.repository;

import com.prgrms.amabnb.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreateRoomRepository extends JpaRepository<Room, Long> {
}
