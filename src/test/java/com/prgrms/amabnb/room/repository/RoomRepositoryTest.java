package com.prgrms.amabnb.room.repository;

import static com.prgrms.amabnb.config.util.Fixture.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.config.RepositoryTest;
import com.prgrms.amabnb.room.dto.request.SearchRoomFilterCondition;
import com.prgrms.amabnb.room.dto.response.RoomSearchResponse;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomImage;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.user.entity.User;
import com.prgrms.amabnb.user.entity.UserRole;
import com.prgrms.amabnb.user.repository.UserRepository;

class RoomRepositoryTest extends RepositoryTest {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomImageRepository roomImageRepository;

    @AfterEach
    void cleanUp(){
        roomRepository.deleteAll();
        userRepository.deleteAll();
        roomImageRepository.deleteAll();
    }

    @Test
    @DisplayName("숙소정보를 db에 저장할 수 있다")
    void roomJpaSave() {
        //given
        User host = userRepository.save(createUser("fdas"));
        Room room = createRoom(host);

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
        User host = userRepository.save(createUser("fdsa"));
        SearchRoomFilterCondition filter = createFullFilter();

        List<Room> createRooms = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            createRooms.add(createRoom(host));
        }

        roomRepository.saveAll(createRooms);

        Room room2 = createRoom(host);
        room2.changePrice(new Money(50000));
        roomRepository.save(room2);

        List<Room> all = roomRepository.findAll();
        assertThat(all.size()).isEqualTo(31);
        //when
        List<RoomSearchResponse> rooms = roomRepository.findRoomsByFilterCondition(filter, PageRequest.of(0, 10));

        //then
        assertThat(rooms.size()).isEqualTo(10);
    }

    @Test
    @DisplayName("필터 조건이 없어으면 모든 숙소정보를 가져온다.")
    void noFilterTest() {
        //given
        User host = userRepository.save(createUser("fdas"));
        SearchRoomFilterCondition nullFilter = createNullFilter();
        Room room1 = createRoom(host);
        Room room2 = createRoom(host);
        Room room3 = createRoom(host);
        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);
        //when
        List<RoomSearchResponse> rooms = roomRepository.findRoomsByFilterCondition(nullFilter, PageRequest.of(0, 10));

        //then
        assertThat(rooms.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("호스트가 등록한 숙소들을 가져온다.")
    void findRoomByHostTest() {
        //given
        User host = userRepository.save(createUser("fdas"));
        User host2 = userRepository.save(User.builder()
            .oauthId("testOauthId")
            .provider("testProvider")
            .userRole(UserRole.GUEST)
            .name("testUser")
            .email(new Email("asaaaa@gmail.com"))
            .profileImgUrl("urlurlrurlrurlurlurl")
            .build());

        Room room1 = createRoom(host);
        Room room2 = createRoom(host);
        Room room3 = createRoom(host2);

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
        User host = userRepository.save(createUser("fads"));

        Room room = createRoom(host);

        roomRepository.save(room);

        //when
        Room foundRoom = roomRepository.findRoomByIdAndHostId(room.getId(), host.getId()).get();

        //then
        assertThat(foundRoom).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class)
            .isEqualTo(room);

    }

    @Test
    @DisplayName("호스트는 숙소를 삭제할 수 있다.")
    void deleteHostRoom() {
        //given
        User host = userRepository.save(createUser("fads"));
        Room room = roomRepository.save(createRoom(host));
        //when
        roomRepository.deleteById(room.getId());
        //then
        assertThat(roomRepository.findById(room.getId()).orElse(null)).isNull();
        assertThat(roomImageRepository.findRoomImagesByRoom(room).size()).isEqualTo(0);
    }

    private SearchRoomFilterCondition createFullFilter() {
        return SearchRoomFilterCondition.builder()
            .minBeds(1)
            .minBedrooms(1)
            .minBathrooms(1)
            .minPrice(2000)
            .maxPrice(30000)
            .roomTypes(List.of(RoomType.APARTMENT))
            .roomScopes(List.of(RoomScope.PRIVATE))
            .build();
    }

    private SearchRoomFilterCondition createNullFilter() {
        return SearchRoomFilterCondition.builder().build();
    }

}
