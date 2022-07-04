package com.prgrms.amabnb.room.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrms.amabnb.room.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long>, QueryRoomRepository {

    @Query("SELECT r FROM Room r "
        + "JOIN FETCH r.host "
        + "WHERE r.id = :roomId")
    Optional<Room> findRoomWithHostById(@Param("roomId") Long roomId);

}
