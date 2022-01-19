package com.crf.server.web.restcontroller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.crf.server.base.entity.Customer;
import com.crf.server.base.jsonentity.*;
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
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerRESTController {

    private OperatorRepository operatorRepository;
    private CustomerService    customerService;

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(value = "/get/details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity getDetails(HttpServletResponse response) throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        apiResponse.setSingleData(customerService.getCustomerDetails(loggedOperator.getCustomerId()));
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        return apiResponse;
    }

    @RequestMapping(value = "/submitbankdetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity submitBankDetails(HttpServletResponse response, @RequestBody BankAccountJson bankAccountJson)
        throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (StringUtils.isBlank(bankAccountJson.getBankName()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "bankName#1");

        if (StringUtils.isBlank(bankAccountJson.getBankAccountName()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "bankAccountName#1");

        if (StringUtils.isBlank(bankAccountJson.getBankAddress()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "bankAddress#1");

        if (StringUtils.isBlank(bankAccountJson.getIban()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "iban#1");

        if (StringUtils.isBlank(bankAccountJson.getBic()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "bic#1");

        if (bankAccountJson.getBankName().length() < 2 || bankAccountJson.getBankName().length() > 64
            || !bankAccountJson.getBankName().matches(ServerConstants.REGEXP_BASIC_PERMISSIVE_SAFE_TEXT))
            throw new CRFValidationException(ServerResponseConstants.INVALID_BANK_NAME_CODE, ServerResponseConstants.INVALID_BANK_NAME_TEXT, "");

        if (bankAccountJson.getBankAccountName().length() < 2 || bankAccountJson.getBankAccountName().length() > 64
            || !bankAccountJson.getBankAccountName().matches(ServerConstants.REGEXP_BASIC_PERMISSIVE_SAFE_TEXT))
            throw new CRFValidationException(ServerResponseConstants.INVALID_BANK_ACCOUNT_NAME_CODE, ServerResponseConstants.INVALID_BANK_ACCOUNT_NAME_TEXT, "");

        if (bankAccountJson.getBankAddress().length() < 2 || bankAccountJson.getBankAddress().length() > 256
            || !bankAccountJson.getBankAddress().matches(ServerConstants.REGEXP_BASIC_PERMISSIVE_SAFE_TEXT))
            throw new CRFValidationException(ServerResponseConstants.INVALID_BANK_ADDRESS_CODE, ServerResponseConstants.INVALID_BANK_ADDRESS_TEXT, "");

        if (bankAccountJson.getIban().length() < 15 || bankAccountJson.getIban().length() > 39 || !bankAccountJson.getIban().matches(ServerConstants.REGEX_BANK_ACCOUNT_IBAN))
            throw new CRFValidationException(ServerResponseConstants.INVALID_IBAN_CODE, ServerResponseConstants.INVALID_IBAN_TEXT, "");

        if (bankAccountJson.getBic().length() < 8 || bankAccountJson.getBic().length() > 11 || !bankAccountJson.getBic().matches(ServerConstants.REGEXP_BASIC_PERMISSIVE_SAFE_TEXT))
            throw new CRFValidationException(ServerResponseConstants.INVALID_BIC_CODE, ServerResponseConstants.INVALID_BIC_TEXT, "");

        customerService.saveBankDetails(bankAccountJson, loggedOperator.getId(), loggedOperator.getCustomerId());

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        return apiResponse;
    }

    @RequestMapping(value = "/getforresubmission", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity getCustomerForReSubmission(HttpServletResponse response)
            throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());
        Customer customer = customerService.getCustomerById(loggedOperator.getCustomerId());
        VerifyCustomerJson verifyCustomerJson = customerService.getCustomerForVerification(customer.getCode());

        apiResponse.setSingleData(verifyCustomerJson);
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/getflags", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity getCustomerFlags(HttpServletResponse response)
            throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());
        Customer customer = customerService.getCustomerById(loggedOperator.getCustomerId());

        CustomerFlagsJson customerFlagsJson = new CustomerFlagsJson(customer.getIsPassportScanUploaded(),
                customer.getIsPassportScanVerified(),
                customer.getIsPassportScanDenied(),
                customer.getIsPhotoUploaded());

        apiResponse.setSingleData(customerFlagsJson);
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
