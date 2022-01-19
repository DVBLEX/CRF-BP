package com.crf.server.rest.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import com.crf.server.rest.exception.AuthenticateException;
import com.crf.server.rest.exception.BadRequestException;
import com.crf.server.rest.exception.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (BadRequestException e) {
            setErrorResponse(HttpStatus.BAD_REQUEST, httpServletResponse, e, httpServletRequest.getRequestURI());
        } catch (UnsupportedMediaTypeStatusException e) {
            setErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, httpServletResponse, e, httpServletRequest.getRequestURI());
        } catch (MethodNotAllowedException e) {
            setErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, httpServletResponse, e, httpServletRequest.getRequestURI());
        } catch (AuthenticateException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, httpServletResponse, e, httpServletRequest.getRequestURI());
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex, String path) {
        response.setStatus(status.value());
        response.setContentType("application/json");

        ErrorResponse apiError = new ErrorResponse(status.value(), ex.getMessage(), Collections.singletonList(status.getReasonPhrase()), new Date(), path);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            response.getWriter().write(mapper.writeValueAsString(apiError));
        } catch (IOException e) {
            log.warn("Exception has been caught", e);
        }
    }
}
