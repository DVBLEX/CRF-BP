package com.crf.server.base.exception;

import lombok.ToString;

@ToString
public class CryptoException extends Exception {

    private static final long serialVersionUID = 1L;

    public CryptoException() {
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
