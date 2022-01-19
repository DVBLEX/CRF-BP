package com.crf.server.rest.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.FileData;
import com.crf.server.base.entity.FileEntity;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.CustomerJson;
import com.crf.server.base.jsonentity.DenyCustomerJson;
import com.crf.server.base.jsonentity.RegistrationRequestJson;
import com.crf.server.base.jsonentity.VerifyCustomerJson;
import com.crf.server.base.service.AdminService;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.FileService;
import com.crf.server.base.service.RegistrationService;
import com.crf.server.base.service.SystemService;
import com.crf.server.rest.security.MyUserDetails;

@RestController
@RequestMapping(value = "/customeradmin", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerAdminController {

    private CustomerService     customerService;
    private FileService         fileService;
    private RegistrationService registrationService;
    private SystemService       systemService;
    private AdminService        adminService;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    // used for sending unique registration URL to customer
    @PostMapping(value = "/emailRegURL", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity emailRegURL(HttpServletResponse response, @RequestBody RegistrationRequestJson registrationRequestJson) throws CRFException,
        CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedAdmin = adminService.getLoggedAdmin();

        validateRegistrationRequestJson(registrationRequestJson);

        registrationService.createRegistrationRequest(loggedAdmin.getId(), registrationRequestJson);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @GetMapping("/list")
    public ApiResponseJsonEntity listRegisteredCustomers(HttpServletResponse response, Pageable pageable) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        PageList<CustomerJson> customerList = customerService.listRegisteredCustomers(pageable);

        apiResponse.setDataList(customerList.getDataList());
        apiResponse.setPage(customerList.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @GetMapping("/getforverification")
    public ApiResponseJsonEntity getCustomerForVerification(HttpServletResponse response, @RequestParam(value = "customerCode") String customerCode) throws Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        VerifyCustomerJson verifyCustomerJson = customerService.getCustomerForVerification(customerCode);

        apiResponse.setSingleData(verifyCustomerJson);
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @GetMapping("/showPDFPassportScanFile")
    public ResponseEntity<byte[]> showPDFPassportScanFile(HttpServletResponse response, @RequestParam(value = "customerCode") String customerCode) throws CRFException, Exception {

        FileData fileData;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));

        try {

            Customer customer = customerService.getCustomerByCode(customerCode);

            // if we cannot find the customer or the customer is already verified so we do not allow to access the files
            if (customer == null || customer.getIsPassportScanVerified())
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "#1");

            fileData = fileService.getCustomerFileData(ServerConstants.FILE_ROLE_ID_DOCUMENT, customer.getId());

            if (!fileData.getType().equalsIgnoreCase(FileEntity.FileType.PDF.toString()))
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "#2");

            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(fileData.getData(), headers, HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(new byte[0], headers, HttpStatus.OK);
        }
    }

    @GetMapping("/verification/denial/reason/list")
    public ApiResponseJsonEntity listVerificationDenialReasons(HttpServletResponse response) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();
        apiResponse.setDataList(systemService.listVerificationDenialReasons());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @GetMapping("/verify")
    public ApiResponseJsonEntity verifyCustomer(HttpServletResponse response, @RequestParam(value = "customerCode") String customerCode) throws CRFException, Exception {

        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "CustomerAdminController#verify#userDetailsIsNull");

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        customerService.verifyCustomer(userDetails.getId(), customerCode);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PostMapping(value = "/verification/deny", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity denyCustomerVerification(HttpServletResponse response, @RequestBody DenyCustomerJson denyCustomerJson) throws CRFException, Exception {

        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT,
                "CustomerAdminController#verification/deny#userDetailsIsNull");

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        customerService.denyCustomerVerification(userDetails.getId(), denyCustomerJson);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    private void validateRegistrationRequestJson(RegistrationRequestJson registrationRequestJson) throws CRFValidationException {
        if (registrationRequestJson.getEmail().length() > ServerConstants.DEFAULT_VALIDATION_LENGTH_64 || !registrationRequestJson.getEmail().matches(ServerConstants.REGEXP_EMAIL))
            throw new CRFValidationException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "RegexFormatCheck#Email");

        else
            try {
                if (systemService.isEmailBlockedDomain(registrationRequestJson.getEmail()))
                    throw new CRFValidationException(ServerResponseConstants.CUSTOMER_EMAIL_BLOCKED_DOMAIN_CODE, "Email is not a company email. Please whitelist the email first.",
                        "Conflict#isEmailBlockedDomain");
            } catch (Exception e) {
                throw new CRFValidationException(ServerResponseConstants.CUSTOMER_EMAIL_BLOCKED_DOMAIN_CODE, e.getMessage(), "Conflict#isEmailBlockedDomain#2");
            }

        if (registrationRequestJson.getType() == null || (registrationRequestJson.getType() != ServerConstants.CUSTOMER_TYPE_BORROWER
            && registrationRequestJson.getType() != ServerConstants.CUSTOMER_TYPE_INVESTOR))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "CustomerType");

        if (registrationRequestJson.getCategory() == null || (registrationRequestJson.getCategory() != ServerConstants.CUSTOMER_CATEGORY_INDIVIDUAL
            && registrationRequestJson.getCategory() != ServerConstants.CUSTOMER_CATEGORY_COMPANY))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "CustomerCategory");

        if (StringUtils.isBlank(registrationRequestJson.getTitle()) || registrationRequestJson.getTitle().length() < 2 || registrationRequestJson.getTitle().length() > 16)
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "Title");

        if (StringUtils.isBlank(registrationRequestJson.getFirstName()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "FirstName");

        if (StringUtils.isBlank(registrationRequestJson.getLastName()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "LastName");

    }
}
