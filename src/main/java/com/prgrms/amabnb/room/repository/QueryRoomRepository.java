package com.prgrms.amabnb.room.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.entity.Room;

public interface QueryRoomRepository {

    List<Room> findRoomsByFilterCondition(SearchRoomFilterCondition searchRoomFilterCondition, Pageable pageable);

    List<Room> findRoomsByUserIdForHost(Long userId);

}
