package com.example.spring_rest_api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InvalidFileException extends BusinessException {

    private final Map<String, String> errors;

    public InvalidFileException(String fieldName, String message) {
        super("INVALID_INPUT", HttpStatus.UNPROCESSABLE_ENTITY);

        this.errors = new HashMap<>();
        this.errors.put(fieldName, message);
    }
}
