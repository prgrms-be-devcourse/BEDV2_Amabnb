package com.prgrms.amabnb.config.util;

import static com.prgrms.amabnb.reservation.entity.ReservationStatus.*;
import static com.prgrms.amabnb.user.entity.UserRole.*;
import static java.time.LocalDate.*;

import java.util.List;

import com.prgrms.amabnb.common.vo.Email;
import com.prgrms.amabnb.common.vo.Money;
import com.prgrms.amabnb.reservation.dto.request.CreateReservationRequest;
import com.prgrms.amabnb.reservation.entity.Reservation;
import com.prgrms.amabnb.reservation.entity.vo.ReservationDate;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.room.dto.request.CreateRoomRequest;
import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomImage;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.security.oauth.UserProfile;
import com.prgrms.amabnb.user.entity.User;

public class Fixture {

    public static CreateReservationRequest makeCreateReservationRequest(Room room) {
        return CreateReservationRequest.builder()
            .checkIn(now())
            .checkOut(now().plusDays(3L))
            .totalGuest(1)
            .totalPrice(room.getPrice().getValue() * 3)
            .roomId(room.getId())
            .build();
    }

    public static UserProfile createUserProfile(String name) {
        return UserProfile.builder()
            .oauthId(name)
            .provider("kakao")
            .name(name)
            .email(name + "@gmail.com")
            .profileImgUrl("url")
            .build();
    }

    public static User createUser(String name) {
        return User.builder()
            .oauthId(name)
            .provider("kakao")
            .name(name)
            .email(new Email(name + "@gmail.com"))
            .userRole(GUEST)
            .profileImgUrl("url")
            .build();
    }

    public static User createUserWithId(String name) {
        return User.builder()
            .id(1L)
            .oauthId(name)
            .provider("kakao")
            .name(name)
            .email(new Email(name + "@gmail.com"))
            .userRole(GUEST)
            .profileImgUrl("url")
            .build();
    }

    public static Room createRoom(User host) {
        return Room.builder()
            .name("?????? ????????? ???")
            .maxGuestNum(10)
            .host(host)
            .description("??? ?????? ?????????")
            .address(new RoomAddress("00000", "??????", "?????????"))
            .price(new Money(10_000))
            .roomOption(new RoomOption(1, 1, 1))
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .roomImages(List.of(new RoomImage("image")))
            .build();
    }

    public static Reservation createReservation(Room room, User guest) {
        return reservationBuilder(room, guest).build();
    }

    public static Reservation createReservationWithId(Room room, User guest) {
        return reservationBuilder(room, guest).id(1L).build();
    }

    private static Reservation.ReservationBuilder reservationBuilder(Room room, User guest) {
        return Reservation.builder()
            .room(room)
            .guest(guest)
            .totalPrice(room.getPrice())
            .totalGuest(1)
            .reservationDate(new ReservationDate(now(), now().plusDays(1L)))
            .reservationStatus(PENDING);
    }

    public static CreateRoomRequest createRoomRequest() {
        return CreateRoomRequest.builder()
            .name("?????????")
            .price(100_000)
            .description("?????????")
            .maxGuestNum(10)
            .zipcode("00000")
            .address("??????")
            .detailAddress("?????????")
            .bedCnt(2)
            .bedRoomCnt(1)
            .bathRoomCnt(1)
            .roomType(RoomType.APARTMENT)
            .roomScope(RoomScope.PRIVATE)
            .imagePaths(List.of("test"))
            .build();
    }

    public static Review createReviewWithId(Reservation reservation) {
        return new Review(1L, "content", 2, reservation);
    }
}
