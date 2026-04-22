package com.jel.spys.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RequestStateValidationException extends RuntimeException {
    public RequestStateValidationException(String message) {
        super(message);
    }
}
