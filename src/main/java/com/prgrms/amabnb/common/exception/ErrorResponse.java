package com.prgrms.amabnb.common.exception;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldError> fieldErrors;

    private ErrorResponse(String message, List<FieldError> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public ErrorResponse(String message) {
        this(message, null);
    }

    public static ErrorResponse of(String message, MethodArgumentTypeMismatchException e) {
        return new ErrorResponse(message, FieldError.of(e.getName(), getValue(e.getValue()), e.getErrorCode()));
    }

    public static ErrorResponse of(String message, BindingResult bindingResult) {
        return new ErrorResponse(message, FieldError.from(bindingResult));
    }

    private static String getValue(Object errorValue) {
        if (errorValue == null) {
            return "";
        }
        return errorValue.toString();
    }

    @Getter
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(String field, String value, String reason) {
            return List.of(new FieldError(field, value, reason));
        }

        public static List<FieldError> from(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                .map(error -> new FieldError(
                    error.getField(),
                    ErrorResponse.getValue(error.getRejectedValue()),
                    error.getDefaultMessage()
                ))
                .toList();
        }
    }

}
