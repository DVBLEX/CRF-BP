package com.crf.server.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticateException extends RuntimeException {

    public AuthenticateException(String message) {
        super(message);
    }

    public AuthenticateException(Throwable cause) {
        super(cause);
    }
}
