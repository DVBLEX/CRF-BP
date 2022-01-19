package com.crf.server.rest.exception;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private int          responseCode;
    //General error message about nature of error
    private String       message;
    //Specific errors in API request processing
    private List<String> details;
    private Date         responseDate;
    private String       path;
}
