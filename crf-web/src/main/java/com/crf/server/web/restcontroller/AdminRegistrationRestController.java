package com.crf.server.web.restcontroller;

import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.AdminRegistrationRequest;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.AdminRegistrationJson;
import com.crf.server.base.jsonentity.AdminRegistrationRequestJson;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.AdminRegistrationService;
import com.crf.server.base.service.SystemService;

import lombok.extern.apachecommons.CommonsLog;
@CommonsLog
@RestController
@RequestMapping("/adminregistration")
public class AdminRegistrationRestController {
    private AdminRegistrationService adminRegistrationService;
    private OperatorRepository operatorRepository;
    private SystemService systemService;

    @Autowired
    public void setAdminRegistrationService(AdminRegistrationService adminRegistrationService) {
        this.adminRegistrationService = adminRegistrationService;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @RequestMapping(value = "/process", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity processAdminRegistration(HttpServletResponse response, @RequestBody AdminRegistrationJson adminRegistrationJson)
            throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        AdminRegistrationRequest adminRegistrationRequest = adminRegistrationService.getAdminRegistrationRequest(adminRegistrationJson.getEmail());

        if (adminRegistrationRequest == null) {
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "adminRegistrationRequest is null");
        }
        else if (!adminRegistrationJson.getPassword().matches(ServerConstants.REGEX_PASSWORD))
            throw new CRFValidationException(ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_CODE, ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_TEXT,
                    "RegexFormatCheck#Password");

        else if (!adminRegistrationJson.getToken().matches(ServerConstants.REGEX_SHA256))
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Token");

        else if  (!adminRegistrationJson.getToken().equals(adminRegistrationRequest.getToken1()))
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "AdminRegistrationRestController#process#InvalidToken");
        else {
            adminRegistrationService.processAdminRegistration(adminRegistrationRequest, adminRegistrationJson);

            apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
            apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        }

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    // used for sending unique registration URL to new admin
    @RequestMapping(value = "/emailRegURL", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity emailRegURL(HttpServletResponse response, @RequestBody AdminRegistrationRequestJson adminRegistrationRequestJson)
            throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

            Operator loggedAdmin = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());
            Set<String> registeredEmailSet = operatorRepository.getRegisteredEmailSet();

        if (loggedAdmin == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "logged admin is null");

        if (loggedAdmin.getRoleId() != ServerConstants.OPERATOR_ROLE_ADMIN)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "logged user is not admin");

        if (registeredEmailSet.contains(adminRegistrationRequestJson.getEmail())) {

            log.error("emailRegURL###Exception: user already exists with this email: " + adminRegistrationRequestJson.getEmail());
            throw new CRFValidationException(ServerResponseConstants.CUSTOMER_EMAIL_ALREADY_REGISTERED_CODE,
                    ServerResponseConstants.CUSTOMER_EMAIL_ALREADY_REGISTERED_TEXT + "email : " + adminRegistrationRequestJson.getEmail(), "");
        }

        if (adminRegistrationRequestJson.getEmail().length() > ServerConstants.DEFAULT_VALIDATION_LENGTH_64 || !adminRegistrationRequestJson.getEmail().matches(ServerConstants.REGEXP_EMAIL))
            throw new CRFValidationException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "RegexFormatCheck#Email");

        else if (systemService.isEmailBlockedDomain(adminRegistrationRequestJson.getEmail()))
            throw new CRFValidationException(ServerResponseConstants.CUSTOMER_EMAIL_BLOCKED_DOMAIN_CODE, "Email is not a company email. Please whitelist the email first.",
                    "Conflict#isEmailBlockedDomain");

        else if (StringUtils.isBlank(adminRegistrationRequestJson.getFirstName()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "FirstName");

        else if (StringUtils.isBlank(adminRegistrationRequestJson.getLastName()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "LastName");

        else {
            adminRegistrationService.createAdminRegistrationRequest(adminRegistrationRequestJson);
        }

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
