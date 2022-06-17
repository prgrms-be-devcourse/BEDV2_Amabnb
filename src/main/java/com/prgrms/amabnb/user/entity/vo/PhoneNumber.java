package com.prgrms.amabnb.user.entity.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneNumber {

    private static final String PHONE_NUMBER_REGEX = "\\d{3}-\\d{3,4}-\\d{4}";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String number;

    public PhoneNumber(String number) {
        validatePhoneNumber(number);
        this.number = number;
    }

    private void validatePhoneNumber(String number) {
        validateBlank(number);
        validateFormat(number);
    }

    private void validateFormat(String number) {
        if (!PHONE_NUMBER_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException();
        }
    }

    private void validateBlank(String number) {
        if (Objects.isNull(number) || number.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

}
