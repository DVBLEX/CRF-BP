package com.crf.server.base.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CRFValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    private int               responseCode;
    private String            responseText;
    private String            responseSource;

    public CRFValidationException() {
        super();
    }

    public CRFValidationException(int responseCode, String responseText, String responseSource) {
        super();
        this.responseCode = responseCode;
        this.responseText = responseText;
        this.responseSource = responseSource;
    }
}
