package com.crf.server.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.repository.OperatorRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class LoginService {

    private OperatorRepository operatorRepository;
    private SystemService      systemService;

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public void validateLoginEmailRequest(String email) throws CRFValidationException, CRFException, Exception {

        if (!email.matches(ServerConstants.REGEXP_EMAIL) || !systemService.isEmailRegisteredAlready(email)) {

            throw new CRFValidationException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "RegexFormatCheck#Email");

        } else if (!systemService.isCountEmailForgotPasswordUnderLimit(email, systemService.getSystemParameter().getPasswordForgotEmailLimit())) {

            throw new CRFValidationException(ServerResponseConstants.LIMIT_EXCEEDED_EMAIL_FORGOT_PASSWORD_CODE, ServerResponseConstants.LIMIT_EXCEEDED_EMAIL_FORGOT_PASSWORD_TEXT,
                "LimitExceeded#ForgotPassword");
        }

    }

    public void passwordEmailCheck(String email) throws CRFValidationException, CRFException, Exception {
        if (!email.matches(ServerConstants.REGEXP_EMAIL)) {

            // The email invalid. It might be a hacking attempt.
            throw new CRFValidationException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "RegexFormatCheck#Email");

        }

    }

    public void passwordCheck(String newPassword, String confirmPassword) throws CRFValidationException, CRFException, Exception {
        if (!newPassword.matches(ServerConstants.REGEX_PASSWORD)) {

            throw new CRFValidationException(ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_CODE, ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_TEXT,
                "RegexFormatCheck#Password");

        }
        if (!newPassword.equals(confirmPassword)) {

            throw new CRFException(ServerResponseConstants.MISMATCH_PASSWORD_CODE, ServerResponseConstants.MISMATCH_PASSWORD_TEXT, "");

        }
    }

   /* public void forgotPasswordChangeKeyCheck(String key,String key2) throws CRFValidationException, CRFException, Exception {
        if (!key.matches(ServerConstants.REGEX_SHA256) || !key2.matches(ServerConstants.REGEX_SHA256)) {

            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Token");

        } else {
            Operator operator = operatorService.getActiveOperatorByUsername(email);

           String token1 = SecurityUtil.generateDateBasedToken1(email, operator.getDateLastPasswdForgotRequest());
            String token2 = SecurityUtil.generateDateBasedToken2(email, operator.getDateLastPasswdForgotRequest());

            if (!token1.equals(key) || !token2.equals(key2)) {

                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Conflict#TokenValidation");

            }

        }
    }*/


    public void operatorSave(Operator operator) {
        operatorRepository.save(operator);

    }

}
