package com.crf.server.web.aspect;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;

/**
 * CustomRestControllerAdvice
 *
 * To convert CRFException into Generic API Failure with BAD_REQUEST HTTP response code.
 * Convert CRFValidationException into Generic API Failure with custom response code & text and BAD_REQUEST HTTP response code.
 *
 * Set CRFValidationException when response code/text needs to be sent back to front-end, otherwise use CRFException that will log
 * response on server but only send generic response to front-end.
 */

@RestControllerAdvice
public class CustomRestControllerAdvice {

    @ExceptionHandler({ CRFException.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponseJsonEntity handleCRFException(CRFException e) {

        ApiResponseJsonEntity result = new ApiResponseJsonEntity();
        result.setResponseCode(ServerResponseConstants.API_FAILURE_CODE);
        result.setResponseText(ServerResponseConstants.API_FAILURE_TEXT);
        result.setResponseDate(new Date());

        return result;
    }

    @ExceptionHandler({ CRFValidationException.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponseJsonEntity handleCRFValidationException(HttpServletRequest request, CRFValidationException padve) {

        ApiResponseJsonEntity result = new ApiResponseJsonEntity();
        result.setResponseCode(padve.getResponseCode());
        result.setResponseText(padve.getResponseText());
        result.setResponseDate(new Date());

        return result;
    }

    @ExceptionHandler({ Exception.class })
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ApiResponseJsonEntity handleGenericException(Exception e) {

        ApiResponseJsonEntity result = new ApiResponseJsonEntity();
        result.setResponseCode(ServerResponseConstants.API_FAILURE_CODE);
        result.setResponseText(ServerResponseConstants.API_FAILURE_TEXT);
        result.setResponseDate(new Date());

        return result;
    }
}
