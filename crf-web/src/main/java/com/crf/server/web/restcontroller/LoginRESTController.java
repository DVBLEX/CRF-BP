package com.crf.server.web.restcontroller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.CaptchaService;
import com.crf.server.base.service.OperatorService;
import com.crf.server.base.service.SystemService;

@RestController
@RequestMapping("/login")
public class LoginRESTController {

    private PasswordEncoder    passwordEncoder;
    private OperatorRepository operatorRepository;
    private CaptchaService     captchaService;
    private OperatorService    operatorService;
    private SystemService      systemService;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
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
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @RequestMapping(value = "/password/forgot/send")
    public ApiResponseJsonEntity forgotPasswordSend(HttpServletResponse response, @RequestParam(value = "input1") String email,
        @RequestParam(value = "recaptchaResponse") String recaptchaResponse) throws CRFException, CRFValidationException, Exception {

        captchaService.processResponse(recaptchaResponse);

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        if (!email.matches(ServerConstants.REGEXP_EMAIL) || !systemService.isEmailRegisteredAlready(email)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFValidationException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "RegexFormatCheck#Email");

        } else if (!systemService.isCountEmailForgotPasswordUnderLimit(email, systemService.getSystemParameter().getPasswordForgotEmailLimit())) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFValidationException(ServerResponseConstants.LIMIT_EXCEEDED_EMAIL_FORGOT_PASSWORD_CODE, ServerResponseConstants.LIMIT_EXCEEDED_EMAIL_FORGOT_PASSWORD_TEXT,
                "LimitExceeded#ForgotPassword");

        } else {
            operatorService.sendPasswdForgotEmail(email);

            apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
            apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

            response.setStatus(HttpServletResponse.SC_OK);
        }

        return apiResponse;
    }

    @RequestMapping(value = "/password/forgot/change")
    public ApiResponseJsonEntity forgotPasswordChange(HttpServletResponse response, @RequestParam(value = "input1") String email,
        @RequestParam(value = "input2") String newPassword, @RequestParam(value = "input3") String confirmPassword, @RequestParam(value = "input4") String key,
        @RequestParam(value = "input5") String key2, @RequestParam(value = "recaptchaResponse") String recaptchaResponse) throws CRFException, CRFValidationException, Exception {

        captchaService.processResponse(recaptchaResponse);

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        if (!email.matches(ServerConstants.REGEXP_EMAIL)) {

            // The email invalid. It might be a hacking attempt.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFValidationException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "RegexFormatCheck#Email");

        } else if (!newPassword.matches(ServerConstants.REGEX_PASSWORD)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFValidationException(ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_CODE, ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_TEXT,
                "RegexFormatCheck#Password");

        } else if (!newPassword.equals(confirmPassword)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.MISMATCH_PASSWORD_CODE, ServerResponseConstants.MISMATCH_PASSWORD_TEXT, "");

        } else if (!key.matches(ServerConstants.REGEX_SHA256) || !key2.matches(ServerConstants.REGEX_SHA256)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Token");

        } else {
            Operator operator = operatorService.getActiveOperatorByUsername(email);

            String token1 = SecurityUtil.generateDateBasedToken1(email, operator.getDateLastPasswdForgotRequest());
            String token2 = SecurityUtil.generateDateBasedToken2(email, operator.getDateLastPasswdForgotRequest());

            if (!token1.equals(key) || !token2.equals(key2)) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Conflict#TokenValidation");

            } else {
                operator.setPassword(passwordEncoder.encode(newPassword));
                operator.setIsCredentialsExpired(false);
                operator.setDateLastPassword(new Date());
                operator.setLoginFailureCount(0);
                operator.setIsLocked(false);
                operator.setCountPasswdForgotRequests(0);

                operatorRepository.save(operator);

                response.setStatus(HttpServletResponse.SC_OK);

                apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
                apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
            }
        }

        return apiResponse;
    }

    @RequestMapping(value = "/password/expired/update")
    public ApiResponseJsonEntity expiredPasswordUpdate(HttpServletResponse response, @RequestParam(value = "input1") String email,
        @RequestParam(value = "input2") String oldPassword, @RequestParam(value = "input3") String newPassword, @RequestParam(value = "input4") String confirmPassword,
        @RequestParam(value = "input5") String key, @RequestParam(value = "input6") String key2, @RequestParam(value = "recaptchaResponse") String recaptchaResponse)
        throws CRFException, Exception {

        captchaService.processResponse(recaptchaResponse);

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        if (!email.matches(ServerConstants.REGEXP_EMAIL)) {

            // The email invalid. It might be a hacking attempt.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFValidationException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "RegexFormatCheck#Email");

        } else if (!newPassword.matches(ServerConstants.REGEX_PASSWORD)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFValidationException(ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_CODE, ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_TEXT,
                "RegexFormatCheck#Password");

        } else if (!newPassword.equals(confirmPassword)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.MISMATCH_PASSWORD_CODE, ServerResponseConstants.MISMATCH_PASSWORD_TEXT, "");

        } else if (!key.matches(ServerConstants.REGEX_SHA256) || !key2.matches(ServerConstants.REGEX_SHA256)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Token");

        } else {
            Operator operator = operatorService.getActiveOperatorByUsername(email);

            String token1 = SecurityUtil.generateDateBasedToken1(email, operator.getDateLastPassword());
            String token2 = SecurityUtil.generateDateBasedToken2(email, operator.getDateLastPassword());

            if (!token1.equals(key) || !token2.equals(key2)) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Conflict#TokenValidation");

            } else {
                if (!operator.getIsCredentialsExpired())
                    // The credentials are not expired. It might be a hacking attempt.
                    throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Conflict#CredentialsAreNotExpired");

                if (!passwordEncoder.matches(oldPassword, operator.getPassword()))
                    throw new CRFValidationException(ServerResponseConstants.INVALID_OLD_PASSWORD_CODE, ServerResponseConstants.INVALID_OLD_PASSWORD_TEXT, "InvalidOldPassword");

                else if (passwordEncoder.matches(newPassword, operator.getPassword()))
                    throw new CRFValidationException(ServerResponseConstants.INVALID_NEW_PASSWORD_CODE, ServerResponseConstants.INVALID_NEW_PASSWORD_TEXT, "InvalidNewPassword");

                else {
                    operator.setPassword(passwordEncoder.encode(newPassword));
                    operator.setIsCredentialsExpired(false);
                    operator.setDateLastPassword(new Date());
                    operator.setLoginFailureCount(0);
                    operator.setIsLocked(false);
                    operator.setCountPasswdForgotRequests(0);

                    operatorRepository.save(operator);

                    response.setStatus(HttpServletResponse.SC_OK);

                    apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
                    apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
                }
            }
        }

        return apiResponse;
    }
}
