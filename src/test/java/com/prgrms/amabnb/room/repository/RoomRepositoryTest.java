package com.prgrms.amabnb.room.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;

@DataJpaTest
@Import(RoomTestConfig.class)
class RoomRepositoryTest {

    private final RoomAddress roomAddress = new RoomAddress("00000", "창원", "의창구");
    private final Money price = new Money(20000);
    private final RoomOption roomOption = new RoomOption(1, 1, 1);

    @Autowired
    RoomRepository roomRepository;

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
        roomRepository.save(room);

        //then
        Room foundRoom = roomRepository.findById(room.getId()).get();
        assertThat(foundRoom).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(room);
    }
}
