package com.crf.server.rest.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.CustomerFlagsJson;
import com.crf.server.base.jsonentity.SystemEnvironmentJson;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.SystemService;
import com.crf.server.rest.security.MyUserDetails;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@RestController
@RequestMapping(value = "/system", produces = MediaType.APPLICATION_JSON_VALUE)
public class SystemController {

    private CustomerService customerService;
    private SystemService   systemService;

    @Value("${tc.system.name}")
    private String          systemName;

    @Value("${tc.system.environment}")
    private String          systemEnvironment;

    @Value("${tc.google.recaptcha.key.site}")
    private String          recaptchaKeySite;

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping
    public ApiResponseJsonEntity getEnvironmentData(HttpServletRequest request, HttpServletResponse response, Device device) throws CRFException {
        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();
        String responseSource = "crfIndex#";
        responseSource = responseSource + request.getRemoteAddr() + "#username=" + SecurityUtil.getSystemUsername();

        log.info(responseSource + "#Request: " + "[]");
        MyUserDetails authUser = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SystemEnvironmentJson systemEnvironmentJson = new SystemEnvironmentJson();

        systemEnvironmentJson.setAppName(systemName);
        systemEnvironmentJson.setEnvironment(systemEnvironment);
        systemEnvironmentJson.setTestEnvironment(systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_LOCAL) || systemEnvironment.equals(
            ServerConstants.SYSTEM_ENVIRONMENT_DEV));
        systemEnvironmentJson.setUsername(authUser.getUsername());
        systemEnvironmentJson.setFirstName(authUser.getFirstname());
        systemEnvironmentJson.setLastName(authUser.getLastname());
        systemEnvironmentJson.setInvestorOperator(authUser.getRole() == ServerConstants.OPERATOR_ROLE_INVESTOR);
        systemEnvironmentJson.setBorrowerOperator(authUser.getRole() == ServerConstants.OPERATOR_ROLE_BORROWER);
        systemEnvironmentJson.setAdmin(authUser.getRole() == ServerConstants.OPERATOR_ROLE_ADMIN);
        systemEnvironmentJson.setDeviceType(ServerUtil.getDeviceTypeString(device));

        if (authUser.getRole() != ServerConstants.OPERATOR_ROLE_ADMIN) {

            try {
                Customer customer = customerService.getCustomerById(authUser.getCustomerId());
                CustomerFlagsJson customerFlagsJson = new CustomerFlagsJson();
                customerFlagsJson.setIsPassportScanUploaded(customer.getIsPassportScanUploaded());
                customerFlagsJson.setIsPassportScanVerified(customer.getIsPassportScanVerified());
                customerFlagsJson.setIsPassportScanDenied(customer.getIsPassportScanDenied());
                customerFlagsJson.setIsPhotoUploaded(customer.getIsPhotoUploaded());

                systemEnvironmentJson.setCustomerCategory(customer.getCategory());
                systemEnvironmentJson.setCustomerFlags(customerFlagsJson);

                if (!customer.getIsPassportScanUploaded() || !customer.getIsPhotoUploaded() || customer.getIsPassportScanDenied()) {

                    systemEnvironmentJson.setUserEmail(authUser.getUsername());
                    systemEnvironmentJson.setCustomerType(customer.getType());

                    if (customer.getIsPassportScanDenied()) {
                        // re-submission
                        systemEnvironmentJson.setVerificationDenialList(systemService.listCustomerVerificationDenials(customer.getId()));
                    } else {

                        // account-setup (no re-submission)
                        systemEnvironmentJson.setRecaptchaKey(recaptchaKeySite);
                        systemEnvironmentJson.setUserMobileNumber(customer.getMsisdn());
                    }
                }

            } catch (Exception e) {
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "SystemController#getEnvironmentData#userDetailsIsNull");
            }
        }
        apiResponse.setSingleData(systemEnvironmentJson);
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);
        return apiResponse;
    }

    @GetMapping("/recaptchaKey")
    public String getRecaptchaKey()  {

        return recaptchaKeySite;
    }
}
