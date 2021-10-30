package com.accenture.pay.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnprocessableStateException extends RuntimeException {
    public UnprocessableStateException(String message) {
        super(message);
    }
}
