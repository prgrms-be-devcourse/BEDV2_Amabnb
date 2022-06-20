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
public class RoomOption {

    @Column(nullable = false)
    private int bedCount;

    @Column(nullable = false)
    private int bedRoomCnt;

    @Column(nullable = false)
    private int bathRoomCnt;

    public RoomOption(int bedCount, int bedRoomCnt, int bathRoomCnt) {
        validateRoomOption(bedCount,bedRoomCnt,bathRoomCnt);
        this.bedCount = bedCount;
        this.bedRoomCnt = bedRoomCnt;
        this.bathRoomCnt = bathRoomCnt;
    }

    private void validateRoomOption(int bedCount, int bedRoomCnt, int bathRoomCnt) {
        validateBedCnt(bedCount);
        validateBedRoomCnt(bedRoomCnt);
        validateBathRoomCnt(bathRoomCnt);
    }

    private void validateBedCnt(int bedCount) {
        if (bedCount < 0) {
            throw new IllegalArgumentException("침대 수 입렵값이 잘못됐습니다");
        }
    }
    private void validateBedRoomCnt(int bedRoomCnt) {
        if (bedRoomCnt < 0) {
            throw new IllegalArgumentException("침실 수 입렵값이 잘못됐습니다");
        }
    }
    private void validateBathRoomCnt(int bathRoomCnt) {
        if (bathRoomCnt < 0) {
            throw new IllegalArgumentException("욕실 수 입렵값이 잘못됐습니다");
        }
    }
}
