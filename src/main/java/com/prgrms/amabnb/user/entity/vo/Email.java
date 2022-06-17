package com.prgrms.amabnb.user.entity.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    private static final String EMAIL_REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
        + "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String value;

    public Email(String value) {
        validateEmail(value);
        this.value = value;
    }

    private void validateEmail(String value) {
        validateBlank(value);
        validateFormat(value);
    }

    private void validateBlank(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    private void validateFormat(String value) {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException();
        }
    }

}
