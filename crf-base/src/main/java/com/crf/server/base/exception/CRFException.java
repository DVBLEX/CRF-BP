package com.crf.server.base.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CRFException extends Exception {

    private static final long serialVersionUID = 1L;

    private int               responseCode;
    private String            responseText;
    private String            responseSource;

    public CRFException() {
        super();
    }

    public CRFException(int responseCode, String responseText, String responseSource) {
        super(responseText);
        this.responseCode = responseCode;
        this.responseText = responseText;
        this.responseSource = responseSource;
    }
}
