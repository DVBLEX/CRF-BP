package com.crf.server.web.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.crf.server.base.entity.Operator;
import com.crf.server.base.repository.OperatorRepository;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private OperatorRepository operatorRepository;

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication)
        throws IOException, ServletException {

        MyUserDetails user = (MyUserDetails) authentication.getPrincipal();

        String redirect = "";

        try {
            Operator operator = operatorRepository.findByUsername(user.getUsername());
            operator.setLoginFailureCount(0);
            operator.setDateLastLogin(new Date());
            operator.setDateLastAttempt(new Date());
            operator.setIsLocked(false);
            operator.setIsCredentialsExpired(false);

            operatorRepository.save(operator);

            log.info("onAuthenticationSuccess#username=" + user.getUsername());

            redirect = "crf.html";

        } catch (Exception e) {

            log.error("onAuthenticationSuccess#username=" + user.getUsername() + "###Exception: " + e.getMessage());
            redirect = "errorPage.html";
        }

        httpServletResponse.sendRedirect(redirect);
    }

}
