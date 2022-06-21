package com.prgrms.amabnb.user.entity.vo;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.prgrms.amabnb.user.exception.UserInvalidValueException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    private static final int EMAIL_MAX_LENGTH = 100;
    private static final String EMAIL_REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
        + "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Column(name = "email", length = EMAIL_MAX_LENGTH, nullable = false, unique = true)
    private String value;

    public Email(String value) {
        validateEmail(value);
        this.value = value;
    }

    private void validateEmail(String value) {
        validateBlank(value);
        validateFormat(value);
        validateLength(value);
    }

    private void validateBlank(String value) {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new UserInvalidValueException("이메일은 비어있을 수 없습니다.");
        }
    }

    private void validateFormat(String value) {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new UserInvalidValueException("이메일 포맷을 만족해야합니다. 현재 이메일 : %s".formatted(value));
        }
    }

    private void validateLength(String value) {
        if (value.length() > EMAIL_MAX_LENGTH) {
            throw new UserInvalidValueException(
                "이메일은 %d자 이하여야 합니다. 현재 이메일 길이 :%d".formatted(EMAIL_MAX_LENGTH, value.length())
            );
        }
    }

}
