package com.prgrms.amabnb.common.vo;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 10_000_000;

    private int value;

    public Money(int value) {
        validateMoney(value);
        this.value = value;
    }

    private void validateMoney(int value) {
        if (value <= MIN_VALUE || value >= MAX_VALUE) {
            throw new IllegalArgumentException();
        }
    }

    public Money multiply(int period) {
        return new Money(this.value * period);
    }

    public Money add(Money payment) {
        return new Money(this.value + payment.value);
    }
}
