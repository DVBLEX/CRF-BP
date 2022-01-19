package com.crf.server.web.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.SystemService;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private OperatorRepository operatorRepository;
    private SystemService      systemService;

    @Value("${tc.system.url}")
    private String             systemUrl;

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException authenticationException)
        throws IOException, ServletException {

        String redirect = "login.html?failure";
        String userName = httpServletRequest.getParameter("input1");
        StringBuilder builder = new StringBuilder();
        builder.append("LoginFailure#username=").append(userName);

        try {
            Operator operator = operatorRepository.findByUsername(userName);

            if (operator != null) {

                if (authenticationException instanceof CredentialsExpiredException) {
                    operator.setIsCredentialsExpired(true);

                    String token1 = SecurityUtil.generateDateBasedToken1(userName, operator.getDateLastPassword());
                    String token2 = SecurityUtil.generateDateBasedToken2(userName, operator.getDateLastPassword());

                    redirect = systemUrl + "credentialsExpired.html?u=" + URLEncoder.encode(userName, StandardCharsets.UTF_8.name()) + "&t=" + token1 + "&t2=" + token2;

                    builder.append("[credentials expired]");

                } else if (authenticationException instanceof DisabledException) {
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);

                    redirect = "login.html?disabled";

                    builder.append("[disabled]");

                } else if (operator.getIsDeleted()&& !operator.getIsActive() && operator.getIsLocked()) {
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);

                    redirect = "login.html?deleted";

                    builder.append("[deleted]");

                } else if (operator.getIsLocked()) {
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);

                    redirect = "login.html?locked";

                    builder.append("[locked]");

                } else if (operator.getLoginFailureCount() >= systemService.getSystemParameter().getLoginLockCountFailed()) {
                    operator.setIsLocked(true);
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);
                    operator.setDateLocked(new Date());

                    redirect = "login.html?locked";

                    builder.append("[lock]");

                } else {
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);

                    builder.append("[bad credentials]");
                }

                operatorRepository.save(operator);
            }

            log.warn(builder.toString());

        } catch (Exception e) {

            log.error("onAuthenticationFailure#username=" + userName + "###Exception: " + e.getMessage());
        }

        httpServletResponse.sendRedirect(redirect);
    }
}
