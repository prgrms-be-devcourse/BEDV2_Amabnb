package com.prgrms.amabnb.room.entity.dto.request;

import com.prgrms.amabnb.room.entity.Room;
import com.prgrms.amabnb.room.entity.RoomScope;
import com.prgrms.amabnb.room.entity.RoomType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class CreateRoomRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;
    private CreateRoomRequest createRoomRequest;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        createRoomRequest = CreateRoomRequest.builder()
                .price(1)
                .description("방설명")
                .maxGuestNum(1)
                .zipcode("00000")
                .address("창원")
                .detailAddress("의창구")
                .bedCnt(2)
                .bedRoomCnt(1)
                .bathRoomCnt(1)
                .roomType(RoomType.APARTMENT)
                .roomScope(RoomScope.PRIVATE)
                .build();
    }

    @DisplayName("get 테스트")
    @Test
    void getTest() {
        //then
        assertThat(createRoomRequest.getPrice()).isEqualTo(1);
        assertThat(createRoomRequest.getDescription()).isEqualTo("방설명");
        assertThat(createRoomRequest.getMaxGuestNum()).isEqualTo(1);
        assertThat(createRoomRequest.getZipcode()).isEqualTo("00000");
        assertThat(createRoomRequest.getAddress()).isEqualTo("창원");
        assertThat(createRoomRequest.getDetailAddress()).isEqualTo("의창구");
        assertThat(createRoomRequest.getBedCnt()).isEqualTo(2);
        assertThat(createRoomRequest.getBedRoomCnt()).isEqualTo(1);
        assertThat(createRoomRequest.getBathRoomCnt()).isEqualTo(1);
        assertThat(createRoomRequest.getRoomType()).isEqualTo(RoomType.APARTMENT);
        assertThat(createRoomRequest.getRoomScope()).isEqualTo(RoomScope.PRIVATE);
    }

    @DisplayName("가격은 음수 혹은 10000000초과되면 안된다")
    @ParameterizedTest
    @ValueSource(
            ints = {-1, 10000001, -1000, 742389423}
    )
    void isPositiveOrZeroPrice(int price) {
        //given
        createRoomRequest.setPrice(price);

        //when
        Set<ConstraintViolation<CreateRoomRequest>> validate = validator.validate(createRoomRequest);

        //then
        assertThat(validate.size()).isEqualTo(1);
    }

    @DisplayName("방설명은 빈 칸이나 빈값이 오면 안된다")
    @ParameterizedTest
    @NullAndEmptySource
    void notBlankDescription(String description) {
        //given
        createRoomRequest.setDescription(description);
        //when
        Set<ConstraintViolation<CreateRoomRequest>> validate = validator.validate(createRoomRequest);
        //then
        assertThat(validate.size()).isEqualTo(1);
    }

    @DisplayName("게스트 수는 0이하는 안된다")
    @ParameterizedTest
    @ValueSource(
            ints = {-1, 0, -2, -432}
    )
    void isPositiveGuestNum(int maxGuestNum) {
        //given
        createRoomRequest.setMaxGuestNum(maxGuestNum);

        //when
        Set<ConstraintViolation<CreateRoomRequest>> validate = validator.validate(createRoomRequest);

        //then
        assertThat(validate.size()).isEqualTo(1);
    }

    @DisplayName("zipcode는 5자리고 숫자여야한다")
    @ParameterizedTest
    @ValueSource(
            strings = {"4444","!@#$%","666666","a1234","ab123","abc12","abcd1","abcde"}
    )
    void zipcodeTest(String zipcode) {
        //given
        createRoomRequest.setZipcode(zipcode);

        //when
        Set<ConstraintViolation<CreateRoomRequest>> validate = validator.validate(createRoomRequest);

        //then
        assertThat(validate.size()).isEqualTo(1);
    }

    @DisplayName("roomType은 null값이 들어오면 안된다.")
    @ParameterizedTest
    @NullSource
    void roomTypeTest(RoomType roomType) {
        //given
        createRoomRequest.setRoomType(roomType);

        //when
        Set<ConstraintViolation<CreateRoomRequest>> validate = validator.validate(createRoomRequest);

        //then
        assertThat(validate.size()).isEqualTo(1);
    }

    @DisplayName("bedCnt는 음수가 되면 안된다.")
    @Test
    void bedCntTest() {
        //given
        createRoomRequest.setBedCnt(-1);

        //when
        Set<ConstraintViolation<CreateRoomRequest>> validate = validator.validate(createRoomRequest);

        //then
        assertThat(validate.size()).isEqualTo(1);
    }

    @DisplayName("bedRoomCnt는 음수가 되면 안된다.")
    @Test
    void bedRoomCntTest() {
        //given
        createRoomRequest.setBedRoomCnt(-1);

        //when
        Set<ConstraintViolation<CreateRoomRequest>> validate = validator.validate(createRoomRequest);

        //then
        assertThat(validate.size()).isEqualTo(1);
    }
    @DisplayName("bathRoomCnt는 음수가 되면 안된다.")
    @Test
    void bathRoomCntTest() {
        //given
        createRoomRequest.setBathRoomCnt(-1);

        //when
        Set<ConstraintViolation<CreateRoomRequest>> validate = validator.validate(createRoomRequest);

        //then
        assertThat(validate.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("toRoom 테스트")
    void toRoomTest() {
        //then
        assertThat(CreateRoomRequest.toRoom(createRoomRequest)).isInstanceOf(Room.class);
    }
}