package com.example.spring_rest_api.common.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class BusinessException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
