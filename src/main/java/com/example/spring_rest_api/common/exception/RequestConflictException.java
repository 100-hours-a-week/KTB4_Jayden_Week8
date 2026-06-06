package com.example.spring_rest_api.common.exception;

import org.springframework.http.HttpStatus;

public class RequestConflictException extends BusinessException {

    public RequestConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
