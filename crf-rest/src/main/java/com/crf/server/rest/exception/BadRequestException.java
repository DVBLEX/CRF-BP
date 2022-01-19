package com.crf.server.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String errorString, Throwable throwable) {
        super("Bad request data:\n" + errorString, throwable);
    }
}
