package com.crf.server.rest.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.AdminRegistrationRequest;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.AdminRegistrationJson;
import com.crf.server.base.jsonentity.AdminRegistrationRequestJson;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.service.AdminRegistrationService;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@RestController
@RequestMapping(value = "/adminregistration", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRegistrationController {

    private AdminRegistrationService adminRegistrationService;

    @Autowired
    public void setAdminRegistrationService(AdminRegistrationService adminRegistrationService) {
        this.adminRegistrationService = adminRegistrationService;
    }

    @PostMapping("/process")
    public ApiResponseJsonEntity processAdminRegistration(HttpServletResponse response, @RequestBody AdminRegistrationJson adminRegistrationJson) throws CRFException,
        CRFValidationException, Exception {
        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        AdminRegistrationRequest adminRegistrationRequest = adminRegistrationService.getAdminRegistrationRequest(adminRegistrationJson.getEmail());

        adminRegistrationService.validateAdminRegistrationRequest(adminRegistrationRequest, adminRegistrationJson);

        adminRegistrationService.processAdminRegistration(adminRegistrationRequest, adminRegistrationJson);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PostMapping("/emailRegURL")
    public ApiResponseJsonEntity emailRegURL(HttpServletResponse response, @RequestBody AdminRegistrationRequestJson adminRegistrationRequestJson) throws CRFException,
        CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        adminRegistrationService.validateAdminRegistrationRequestJson(adminRegistrationRequestJson);

        adminRegistrationService.createAdminRegistrationRequest(adminRegistrationRequestJson);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
