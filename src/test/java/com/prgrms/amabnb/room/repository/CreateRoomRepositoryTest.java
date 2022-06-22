package com.prgrms.amabnb.room.repository;

import com.prgrms.amabnb.common.model.Money;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CreateRoomRepositoryTest {

    private final RoomAddress roomAddress = new RoomAddress("00000", "창원", "의창구");
    private final Money price = new Money(20000);
    private final RoomOption roomOption = new RoomOption(1, 1, 1);

    @Autowired
    CreateRoomRepository createRoomRepository;

    @Test
    @DisplayName("숙소정보를 db에 저장할 수 있다")
    void roomJpaSave() {
        //given
        Room room = Room.builder()
                .id(1L)
                .maxGuestNum(1)
                .description("description")
                .address(roomAddress)
                .price(price)
                .roomOption(roomOption)
                .roomType(RoomType.APARTMENT)
                .roomScope(RoomScope.PRIVATE)
                .build();

        //when
        createRoomRepository.save(room);

        //then
        Room foundRoom = createRoomRepository.findById(room.getId()).get();
        assertThat(foundRoom).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(room);
    }
}