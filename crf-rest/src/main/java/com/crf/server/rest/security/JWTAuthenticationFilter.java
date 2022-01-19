package com.crf.server.rest.security;

import static com.crf.server.rest.security.SecurityConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.jsonentity.OperatorJson;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.SystemService;
import com.crf.server.rest.exception.AuthenticateException;
import com.crf.server.rest.exception.BadRequestException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final OperatorRepository    operatorRepository;
    private final SystemService         systemService;

    @Value("${tc.login.failure}")
    private String              loginFailure;

    @Value("${tc.login.logout}")
    private String              loginLogout;

    @Value("${tc.login.denied}")
    private String              loginDenied;

    @Value("${tc.login.deleted}")
    private String              loginDeleted;

    @Value("${tc.login.locked}")
    private String              loginLocked;

    @Value("${tc.login.credentialsUpdated}")
    private String              loginCredentialsUpdated;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext ctx) {
        this.authenticationManager = authenticationManager;
        this.operatorRepository = ctx.getBean(OperatorRepository.class);
        this.systemService = ctx.getBean(SystemService.class);
        this.setFilterProcessesUrl(LOGIN_URL);
    }

    @Override
    public final Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        if (!req.getMethod().equals("POST")) {
            throw new MethodNotAllowedException(req.getMethod(), Collections.singleton(HttpMethod.POST));
        }
        req.getHeaderNames();
        String contentType = req.getHeader("Content-Type");
        if (!contentType.contains("application/json")) {
            throw new UnsupportedMediaTypeStatusException("Not supported media type");
        }
        try {
            OperatorJson credentials = new ObjectMapper().readValue(req.getInputStream(), OperatorJson.class);
            req.setAttribute("username", credentials.getUsername());
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword(), new ArrayList<>()));
        } catch (JsonMappingException | JsonParseException e) {
            throw new BadRequestException(e.getMessage(), e);
        } catch (IOException e) {
            throw new AuthenticateException(e);
        }
    }

    @Override
    protected final void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
        String username = ((MyUserDetails) auth.getPrincipal()).getUsername();
        String token = Jwts.builder().setSubject(username).setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).signWith(SignatureAlgorithm.HS512,
            SECRET).compact();
        res.addHeader("Access-Control-Expose-Headers", "Authorization");
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);

        saveLoginData(auth, username);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String username = (String) request.getAttribute("username");
        StringBuilder builder = new StringBuilder();
        builder.append("LoginFailure#username=").append(username);

        try {
            Operator operator = operatorRepository.findByUsername(username);

            if (operator != null) {

                if (failed instanceof CredentialsExpiredException) {
                    operator.setIsCredentialsExpired(true);

                    String token1 = SecurityUtil.generateDateBasedToken1(username, operator.getDateLastPassword());
                    String token2 = SecurityUtil.generateDateBasedToken2(username, operator.getDateLastPassword());

                    builder.append("[credentials expired]");

                } else if (failed instanceof DisabledException) {
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);

                    builder.append("[disabled]");

                } else if (operator.getIsDeleted() && !operator.getIsActive() && operator.getIsLocked()) {
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);

                    builder.append("[deleted]");

                } else if (operator.getIsLocked()) {
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);

                    builder.append("[locked]");

                } else if (operator.getLoginFailureCount() >= systemService.getSystemParameter().getLoginLockCountFailed()) {
                    operator.setIsLocked(true);
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);
                    operator.setDateLocked(new Date());

                    builder.append("[lock]");

                } else {
                    operator.setLoginFailureCount(operator.getLoginFailureCount() + 1);

                    builder.append("[bad credentials]");
                }

                operatorRepository.save(operator);
            }

            log.warn(builder.toString());

        } catch (Exception e) {

            log.error("onAuthenticationFailure#username=" + username + "###Exception: " + e.getMessage());
        }
        throw new AuthenticateException(builder.toString());
    }

    private void saveLoginData(Authentication auth, String username) {
        try {
            Operator operator = operatorRepository.findByUsername(((MyUserDetails) auth.getPrincipal()).getUsername());
            operator.setLoginFailureCount(0);
            operator.setDateLastLogin(new Date());
            operator.setDateLastAttempt(new Date());
            operator.setIsLocked(false);
            operator.setIsCredentialsExpired(false);

            operatorRepository.save(operator);

            log.info("onAuthenticationSuccess#username=" + username);

        } catch (Exception e) {
            log.error("onAuthenticationSuccess#username=" + username + "###Exception: " + e.getMessage());
        }
    }
}
