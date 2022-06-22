package com.prgrms.amabnb.room.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
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
import com.prgrms.amabnb.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Builder
    public Room(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private Money price;

    @Lob
    private String description;

    private int maxGuestNum;
    
    private String address;

    private int bedCount;

    private int bedRoomCnt;

    private int bathRoomCnt;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    private RoomScope roomScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User host;

    @OneToMany
    @JoinColumn(name = "review_id")
    private List<Review> reviews = new ArrayList<>();

}
