package com.prgrms.amabnb.room.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.common.vo.PhoneNumber;
import com.prgrms.amabnb.config.RepositoryTest;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.repository.UserRepository;

class RoomRepositoryTest extends RepositoryTest {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("숙소정보를 db에 저장할 수 있다")
    void roomJpaSave() {
        //given
        Room room = createRoom();

        //when
        roomRepository.save(room);

        //then
        Room foundRoom = roomRepository.findById(room.getId()).get();
        assertThat(foundRoom).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(room);
    }

    @Test
    @DisplayName("필터로 걸러진 숙소들을 찾는다")
    void findRoomsByFilter() {
        //given
        SearchRoomFilterCondition filter = createFullFilter();
        Room room1 = createRoom();
        Room room2 = createRoom();
        room2.changePrice(new Money(2000));
        Room room3 = createRoom();
        room3.changePrice(new Money(60000));
        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);
        //when
        List<Room> rooms = roomRepository.findRoomsByFilterCondition(filter, PageRequest.of(0, 10));

        //then
        assertThat(rooms.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("필터 조건이 없어으면 모든 숙소정보를 가져온다.")
    void name() {
        //given
        SearchRoomFilterCondition nullFilter = createNullFilter();
        Room room1 = createRoom();
        Room room2 = createRoom();
        Room room3 = createRoom();
        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);
        //when
        List<Room> rooms = roomRepository.findRoomsByFilterCondition(nullFilter, PageRequest.of(0, 10));

        //then
        assertThat(rooms.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("호스트가 등록한 숙소들을 가져온다.")
    void findRoomByHostTest() {
        //given
        User host = createUser();
        userRepository.save(host);

        Room room1 = createRoom();
        room1.setHost(host);
        Room room2 = createRoom();
        room2.setHost(host);
        Room room3 = createRoom();
        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        Long hostId = host.getId();
        //when
        List<Room> rooms = roomRepository.findRoomsByUserIdForHost(hostId);
        //then
        assertThat(rooms.size()).isEqualTo(2);
    }

    private SearchRoomFilterCondition createFullFilter() {
        return new SearchRoomFilterCondition(1, 1, 1, 2000, 50000, List.of(RoomType.APARTMENT),
            List.of(RoomScope.PRIVATE));
    }

    private SearchRoomFilterCondition createNullFilter() {
        return new SearchRoomFilterCondition(
            null, null, null, null, null, null, null
        );
    }

    private User createUser() {
        return User.builder()
            .oauthId("testOauthId")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .email(new Email("asdsadsad@gmail.com"))
            .phoneNumber(new PhoneNumber("010-2312-1231"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build();
    }

    private Room createRoom() {
        RoomAddress roomAddress = new RoomAddress("00000", "창원", "의창구");
        Money price = new Money(1000);
        RoomOption roomOption = new RoomOption(1, 1, 1);

        return Room.builder()
            .name("방 이름")
            .maxGuestNum(1)
            .description("방 설명 입니다")
            .address(roomAddress)
            .price(price)
            .roomOption(roomOption)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .build();
    }
}
