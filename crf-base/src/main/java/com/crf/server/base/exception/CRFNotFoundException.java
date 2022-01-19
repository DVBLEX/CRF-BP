package com.crf.server.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;

@Data
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CRFNotFoundException extends RuntimeException {
    private int code;
    private String message;
    private String source;

    public CRFNotFoundException(int code, String message, String source) {
        this.code = code;
        this.message = message;
        this.source = source;
    }
}
