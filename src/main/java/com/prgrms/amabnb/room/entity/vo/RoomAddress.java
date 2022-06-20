package com.prgrms.amabnb.room.entity.vo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomAddress {

    @Column(nullable = false)
    private int zipcode;

    @Column(nullable = false)
    private String address;

    private String detailAddress;

    public RoomAddress(int zipcode, String address, String detailAddress) {
        validateRoomAddress(zipcode, address);
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
    }

    private void validateRoomAddress(int zipcode, String address) {
        validateZipcode(zipcode);
        validateAddress(address);
    }

    private void validateZipcode(int zipcode) {
        if (zipcode < 0) {
            throw new IllegalArgumentException("우편번호 입력값이 잘못됐습니다");
        }
    }

    private void validateAddress(String address) {
        if (address.isBlank()) {
            throw new IllegalArgumentException("주소 입력값이 잘못됐습니다");
        }
    }
}
