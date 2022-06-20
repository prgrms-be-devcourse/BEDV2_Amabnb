package com.prgrms.amabnb.room.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.prgrms.amabnb.common.model.BaseEntity;
import com.prgrms.amabnb.common.model.Money;
import com.prgrms.amabnb.review.entity.Review;
import com.prgrms.amabnb.room.entity.vo.RoomAddress;
import com.prgrms.amabnb.room.entity.vo.RoomOption;
import com.prgrms.amabnb.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Money price;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int maxGuestNum;

    @Embedded
    private RoomAddress address;

    @Embedded
    private RoomOption roomOption;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomScope roomScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User host;

    @OneToMany
    @JoinColumn(name = "review_id")
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Room(Long id, Money price, String description, int maxGuestNum, RoomAddress address, RoomOption roomOption,
                RoomType roomType, RoomScope roomScope) {
        validateRoom(maxGuestNum, description);
        this.id = id;
        this.price = price;
        this.description = description;
        this.maxGuestNum = maxGuestNum;
        this.address = address;
        this.roomOption = roomOption;
        this.roomType = roomType;
        this.roomScope = roomScope;
    }

    private void validateRoom(int maxGuestNum, String description) {
        validateMaxGuestNum(maxGuestNum);
        validateDescription(description);
    }

    private void validateMaxGuestNum(int maxGuestNum) {
        if (maxGuestNum < 1) {
            throw new IllegalArgumentException("최대 인원 수 입력값이 잘못됐습니다.");
        }
    }

    private void validateDescription(String description) {
        if (description.isBlank()) {
            throw new IllegalArgumentException();
        }
    }
}
