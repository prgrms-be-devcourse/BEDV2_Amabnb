package com.prgrms.amabnb.room.entity.vo;

import com.prgrms.amabnb.room.exception.RoomInvalidValueException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomAddress {

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String address;

    private String detailAddress;

    public RoomAddress(String zipcode, String address, String detailAddress) {
        validateRoomAddress(zipcode, address);
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
    }

    private void validateRoomAddress(String zipcode, String address) {
        validateZipcode(zipcode);
        validateAddress(address);
    }

    private void validateZipcode(String zipcode) {
        final String ZIPCODE_REGEX = "^\\d{5}$";

        if (Objects.isNull(zipcode) || zipcode.isBlank() || !zipcode.matches(ZIPCODE_REGEX)) {
            throw new RoomInvalidValueException("우편번호 입력값이 잘못됐습니다");
        }
    }

    private void validateAddress(String address) {
        if (Objects.isNull(address) || address.isBlank()) {
            throw new RoomInvalidValueException("주소 입력값이 잘못됐습니다");
        }
    }
}
