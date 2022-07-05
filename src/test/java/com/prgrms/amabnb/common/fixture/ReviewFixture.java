package com.prgrms.amabnb.common.fixture;

import java.time.LocalDate;
import java.util.UUID;

import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.security.oauth.UserProfile;
import com.prgrms.amabnb.user.entity.User;

public class ReviewFixture {
    public static Reservation createReservation(User guest, Room room) {
        var reservation = Reservation.builder()
            .id(1L)
            .reservationDate(new ReservationDate(LocalDate.now(), LocalDate.now().plusDays(3L)))
            .totalGuest(1)
            .totalPrice(new Money(1000))
            .room(room)
            .guest(guest)
            .build();
        return reservation;
    }

    public static Room createRoom(User user) {
        var room = Room.builder()
            .id(1L)
            .name("방이름")
            .price(new Money(1000))
            .description("방설명")
            .maxGuestNum(10)
            .address(new RoomAddress("12345", "address", "detailAddress"))
            .roomOption(new RoomOption(1, 1, 1))
            .roomType(RoomType.HOUSE)
            .roomScope(RoomScope.PUBLIC)
            .host(user)
            .build();
        return room;
    }

    public static UserProfile createUserProfile(String name) {
        return UserProfile.builder()
            .oauthId(UUID.randomUUID().toString())
            .provider("kakao")
            .name(name)
            .email(UUID.randomUUID() + "@gmail.com")
            .profileImgUrl("url")
            .build();
    }

}
