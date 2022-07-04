package com.prgrms.amabnb.room.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.entity.Room;

public interface QueryRoomRepository {

    List<Room> findRoomsByFilterCondition(SearchRoomFilterCondition searchRoomFilterCondition, Pageable pageable);

    List<Room> findRoomsByHostId(Long userId);

    Optional<Room> findRoomByIdAndHostId(Long roomId, Long hostId);

    Optional<Room> findRoomById(Long roomId);
}
