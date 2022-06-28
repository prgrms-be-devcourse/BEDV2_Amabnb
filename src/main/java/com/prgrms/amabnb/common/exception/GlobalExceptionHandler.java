package com.prgrms.amabnb.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_LOG_MESSAGE = "[ERROR] {} : {}";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("서버에서 에러가 발생했습니다."));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("접근권한이 없습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("잘못된 값입니다.", e.getBindingResult());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e) {
        log.error(ERROR_LOG_MESSAGE, e.getClass().getSimpleName(), e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of("잘못된 타입입니다.", e);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

}
