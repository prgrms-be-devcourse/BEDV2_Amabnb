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
        Room room = createRoom(null);

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
        Room room1 = createRoom(null);
        Room room2 = createRoom(null);
        room2.changePrice(new Money(2000));
        Room room3 = createRoom(null);
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
    void noFilterTest() {
        //given
        SearchRoomFilterCondition nullFilter = createNullFilter();
        Room room1 = createRoom(null);
        Room room2 = createRoom(null);
        Room room3 = createRoom(null);
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
        User host = userRepository.save(createUser());

        Room room1 = createRoom(host);
        Room room2 = createRoom(host);
        Room room3 = createRoom(host);

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        Long hostId = host.getId();
        //when
        List<Room> rooms = roomRepository.findRoomsByHostId(hostId);
        //then
        assertThat(rooms.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("호스트가 등록한 특정 숙소를 가져온다.")
    void findByIdAndHostId() {
        //given
        User host = userRepository.save(createUser());

        Room room = createRoom(host);

        roomRepository.save(room);

        //when
        Room foundRoom = roomRepository.findRoomByIdAndHostId(room.getId(), host.getId()).get();

        //then
        assertThat(foundRoom).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(room);

    }

    private SearchRoomFilterCondition createFullFilter() {
        return SearchRoomFilterCondition.builder()
            .minBeds(1)
            .minBedrooms(1)
            .minBathrooms(1)
            .minPrice(2000)
            .maxPrice(5000)
            .roomTypes(List.of(RoomType.APARTMENT))
            .roomScopes(List.of(RoomScope.PRIVATE))
            .build();
    }

    private SearchRoomFilterCondition createNullFilter() {
        return SearchRoomFilterCondition.builder().build();
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

    private Room createRoom(User host) {
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
            .host(host)
            .build();
    }
}
