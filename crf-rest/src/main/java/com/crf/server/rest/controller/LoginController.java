package com.crf.server.rest.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.LoginJson;
import com.crf.server.base.service.CaptchaService;
import com.crf.server.base.service.LoginService;
import com.crf.server.base.service.OperatorService;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@RestController
@RequestMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {

    private PasswordEncoder passwordEncoder;
    private CaptchaService  captchaService;
    private OperatorService operatorService;
    private LoginService    loginService;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setCaptchaService(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @Autowired
    public void setOperatorService(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/password/forgot/send")
    public ApiResponseJsonEntity forgotPasswordSend(HttpServletResponse response, @RequestBody LoginJson login) throws CRFException, CRFValidationException, Exception {

        captchaService.processResponse(login.getRecaptchaResponse());

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        loginService.validateLoginEmailRequest(login.getEmail());

        operatorService.sendPasswdForgotEmail(login.getEmail());

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PutMapping("/password/forgot/change")
    public ApiResponseJsonEntity forgotPasswordChange(HttpServletResponse response, @RequestBody LoginJson login) throws CRFException, CRFValidationException, Exception {

        captchaService.processResponse(login.getRecaptchaResponse());

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        loginService.passwordEmailCheck(login.getEmail());

        loginService.passwordCheck(login.getNewPassword(), login.getConfirmPassword());

        if (!login.getKey().matches(ServerConstants.REGEX_SHA256) || !login.getSecondKey().matches(ServerConstants.REGEX_SHA256)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Token");

        } else {
            Operator operator = operatorService.getActiveOperatorByUsername(login.getEmail());

            String token1 = SecurityUtil.generateDateBasedToken1(login.getEmail(), operator.getDateLastPasswdForgotRequest());
            String token2 = SecurityUtil.generateDateBasedToken2(login.getEmail(), operator.getDateLastPasswdForgotRequest());

            if (!token1.equals(login.getKey()) || !token2.equals(login.getSecondKey())) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Conflict#TokenValidation");

            } else {
                operator.setPassword(passwordEncoder.encode(login.getNewPassword()));
                operator.setIsCredentialsExpired(false);
                operator.setDateLastPassword(new Date());
                operator.setLoginFailureCount(0);
                operator.setIsLocked(false);
                operator.setCountPasswdForgotRequests(0);

                loginService.operatorSave(operator);

                response.setStatus(HttpServletResponse.SC_OK);

                apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
                apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
            }
        }
        return apiResponse;
    }

    @PutMapping("/password/expired/update")
    public ApiResponseJsonEntity expiredPasswordUpdate(HttpServletResponse response, @RequestBody LoginJson login) throws CRFException, Exception {

        captchaService.processResponse(login.getRecaptchaResponse());

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        loginService.passwordEmailCheck(login.getEmail());

        loginService.passwordCheck(login.getNewPassword(), login.getConfirmPassword());

        if (!login.getKey().matches(ServerConstants.REGEX_SHA256) || !login.getSecondKey().matches(ServerConstants.REGEX_SHA256)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Token");

        } else {
            Operator operator = operatorService.getActiveOperatorByUsername(login.getEmail());

            String token1 = SecurityUtil.generateDateBasedToken1(login.getEmail(), operator.getDateLastPassword());
            String token2 = SecurityUtil.generateDateBasedToken2(login.getEmail(), operator.getDateLastPassword());

            if (!token1.equals(login.getKey()) || !token2.equals(login.getSecondKey())) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Conflict#TokenValidation");

            } else {
                if (!operator.getIsCredentialsExpired())
                    // The credentials are not expired. It might be a hacking attempt.
                    throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Conflict#CredentialsAreNotExpired");

                if (!passwordEncoder.matches(login.getOldPassword(), operator.getPassword()))
                    throw new CRFValidationException(ServerResponseConstants.INVALID_OLD_PASSWORD_CODE, ServerResponseConstants.INVALID_OLD_PASSWORD_TEXT, "InvalidOldPassword");

                else if (passwordEncoder.matches(login.getNewPassword(), operator.getPassword()))
                    throw new CRFValidationException(ServerResponseConstants.INVALID_NEW_PASSWORD_CODE, ServerResponseConstants.INVALID_NEW_PASSWORD_TEXT, "InvalidNewPassword");

                else {
                    operator.setPassword(passwordEncoder.encode(login.getNewPassword()));
                    operator.setIsCredentialsExpired(false);
                    operator.setDateLastPassword(new Date());
                    operator.setLoginFailureCount(0);
                    operator.setIsLocked(false);
                    operator.setCountPasswdForgotRequests(0);

                    loginService.operatorSave(operator);

                    response.setStatus(HttpServletResponse.SC_OK);

                    apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
                    apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
                }
            }
        }

        return apiResponse;
    }
}
