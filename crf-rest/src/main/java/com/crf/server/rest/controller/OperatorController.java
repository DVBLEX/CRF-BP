package com.crf.server.rest.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.OperatorJson;
import com.crf.server.base.service.EmailService;
import com.crf.server.base.service.OperatorService;
import com.crf.server.base.service.SystemService;

@RestController
@RequestMapping(value = "/operator", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperatorController {

    private PasswordEncoder passwordEncoder;
    private EmailService    emailService;
    private OperatorService operatorService;
    private SystemService   systemService;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setOperatorService(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @PostMapping(value = "/password/change", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity changePassword(HttpServletResponse response, @RequestBody OperatorJson operatorJson) throws CRFException, CRFValidationException {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorService.getActiveOperatorByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "OperatorController#changePassword#loggedOperatorIsNull");
        else if (!passwordEncoder.matches(operatorJson.getCurrentPassword(), loggedOperator.getPassword()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_CURRENT_PASSWORD_CODE, ServerResponseConstants.INVALID_CURRENT_PASSWORD_TEXT,
                "Mismatch#CurrentPassword");
        else if (!operatorJson.getPassword().matches(ServerConstants.REGEX_PASSWORD))
            throw new CRFValidationException(ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_CODE, ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_TEXT,
                "RegexFormatCheck#Password");
        else if (!operatorJson.getPassword().equals(operatorJson.getConfirmPassword()))
            throw new CRFValidationException(ServerResponseConstants.MISMATCH_PASSWORD_CODE, ServerResponseConstants.MISMATCH_PASSWORD_TEXT, "Mismatch#Password");
        else {
            operatorService.changePassword(loggedOperator, operatorJson.getPassword());

            apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
            apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

            response.setStatus(HttpServletResponse.SC_OK);

            return apiResponse;
        }
    }

    @GetMapping(value = "/contact/form/submit")
    public ApiResponseJsonEntity submitContactForm(HttpServletResponse response, @RequestParam(value = "queryType") String queryType,
        @RequestParam(value = "queryDetails") String queryDetails) throws CRFException {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorService.getActiveOperatorByUsername(SecurityUtil.getSystemUsername());
        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "OperatorController#submitContactForm#loggedOperatorIsNull");

        Email email = new Email();
        email.setEmailTo(systemService.getSystemParameter().getContactEmail());
        email.setEmailReplyTo(loggedOperator.getEmail());
        email.setCustomerId(loggedOperator.getCustomerId());

        HashMap<String, Object> params = new HashMap<>();
        params.put("queryDetails", queryDetails);

        if (queryType.equalsIgnoreCase("Other")) {
            params.put("queryType", "Support Query");
        } else {
            params.put("queryType", queryType);
        }

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_CUSTOMER_SUPPORT_QUERY_ID, params);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
