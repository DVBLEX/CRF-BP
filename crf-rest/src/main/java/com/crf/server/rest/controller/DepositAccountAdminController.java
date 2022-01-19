package com.crf.server.rest.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.entity.DepositAccountPayment;
import com.crf.server.base.entity.DepositProduct;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.DepositAccountJson;
import com.crf.server.base.jsonentity.DepositAccountPaymentJson;
import com.crf.server.base.service.AdminService;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.DepositAccountPaymentService;
import com.crf.server.base.service.DepositAccountService;
import com.crf.server.base.service.DepositProductService;

@RestController
@RequestMapping(value = "/depositaccountadmin", produces = MediaType.APPLICATION_JSON_VALUE)
public class DepositAccountAdminController {

    private AdminService                 adminService;
    private CustomerService              customerService;
    private DepositAccountService        depositAccountService;
    private DepositAccountPaymentService depositAccountPaymentService;
    private DepositProductService        depositProductService;

    @Autowired
    public void setDepositAccountService(DepositAccountService depositAccountService) {
        this.depositAccountService = depositAccountService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setDepositAccountPaymentService(DepositAccountPaymentService depositAccountPaymentService) {
        this.depositAccountPaymentService = depositAccountPaymentService;
    }

    @Autowired
    public void setDepositProductService(DepositProductService depositProductService) {
        this.depositProductService = depositProductService;
    }

    @GetMapping("/list")
    public ApiResponseJsonEntity list(HttpServletResponse response, Pageable pageable, @RequestParam(value = "status") int status,
        @RequestParam(value = "bankTransferRef") String bankTransferRef) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        adminService.validateLoggedAdmin();

        PageList<DepositAccountJson> depositAccounts = depositAccountService.getDepositAccounts(pageable, status, bankTransferRef);

        apiResponse.setDataList(depositAccounts.getDataList());
        apiResponse.setPage(depositAccounts.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PostMapping(value = "/approve", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity approve(HttpServletResponse response, @RequestBody DepositAccountJson depositAccountJson) throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedAdmin = adminService.getLoggedAdmin();

        validateDepositAccountJsonFields(depositAccountJson);

        DepositAccount depositAccount = depositAccountService.getDepositAccountByCode(depositAccountJson.getCode());

        validateDepositAccount(depositAccount);

        if (!depositAccount.getBankTransferReference().equalsIgnoreCase(depositAccountJson.getBankTransferReference()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE,
                "The entered bank transfer reference does not match with the actual bank transfer reference associated with this deposit", "#6");

        Customer customer = customerService.getCustomerById(depositAccount.getCustomerId());

        validateCustomer(customer);

        DepositProduct depositProduct = depositProductService.getDepositProductById(depositAccount.getDepositProductId());

        BigDecimal depositAmount = new BigDecimal(depositAccountJson.getDepositAmount());

        validateDepositAmount(depositProduct, depositAmount);

        Date dateStart = null;
        try {
            dateStart = ServerUtil.parseDate(ServerConstants.dateFormatddMMyyyy, depositAccountJson.getDateStartString());

        } catch (ParseException pe) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "invalid dateStart format");
        }

        LocalDate localDateOpen = depositAccount.getDateOpen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date dateOpen = Date.from(localDateOpen.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (dateStart.compareTo(dateOpen) < 0)
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, "The selected date is before the date the deposit was opened", "#7");

        depositAccount.setDepositAmount(depositAmount);
        depositAccount.setDateStart(dateStart);

        depositAccountService.approveDeposit(loggedAdmin.getId(), depositAccount, customer);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @GetMapping("/interestpayment/list")
    public ApiResponseJsonEntity listInterestPayments(HttpServletResponse response, Pageable pageable) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        adminService.validateLoggedAdmin();

        PageList<DepositAccountPaymentJson> depositAccountsPayments = depositAccountPaymentService.getDepositAccountPayments(pageable);

        apiResponse.setDataList(depositAccountsPayments.getDataList());
        apiResponse.setPage(depositAccountsPayments.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PostMapping(value = "/process/interestpayment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity processDepositInterestPayment(HttpServletResponse response, @RequestBody DepositAccountPaymentJson depositAccountPaymentJson) throws CRFException,
        CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedAdmin = adminService.getLoggedAdmin();

        if (StringUtils.isBlank(depositAccountPaymentJson.getCode()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

        DepositAccountPayment depositAccountPayment = depositAccountPaymentService.getDepositAccountPaymentByCode(depositAccountPaymentJson.getCode());

        if (depositAccountPayment == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositAccountPayment is null");

        if (depositAccountPayment.getIsProcessed())
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#2");

        depositAccountPaymentService.processDepositInterestPayment(loggedAdmin.getId(), depositAccountPayment);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PostMapping(value = "/approve/withdrawal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity approveWithdrawal(HttpServletResponse response, @RequestBody DepositAccountJson depositAccountJson) throws CRFException, CRFValidationException,
        Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = adminService.getLoggedAdmin();

        if (StringUtils.isBlank(depositAccountJson.getCode()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

        DepositAccount depositAccount = depositAccountService.getDepositAccountByCode(depositAccountJson.getCode());

        if (depositAccount == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositAccount is null");

        if (depositAccount.getStatus() != ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWAL_REQUESTED)
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#2");

        Customer customer = customerService.getCustomerById(depositAccount.getCustomerId());

        validateCustomer(customer);

        if (customer.getType() != ServerConstants.CUSTOMER_TYPE_INVESTOR && customer.getType() != ServerConstants.CUSTOMER_TYPE_INVESTOR_AND_BORROWER)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "customer type is unexpected");

        depositAccountService.approveDepositWithdrawal(loggedOperator.getId(), depositAccount, customer);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    private void validateDepositAccountJsonFields(DepositAccountJson depositAccountJson) throws CRFValidationException {
        if (StringUtils.isBlank(depositAccountJson.getCode()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

        if (StringUtils.isBlank(depositAccountJson.getBankTransferReference()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#2");

        if (StringUtils.isBlank(depositAccountJson.getDepositAmount()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#3");

        if (StringUtils.isBlank(depositAccountJson.getDateStartString()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#4");
    }

    private void validateDepositAccount(DepositAccount depositAccount) throws CRFValidationException {
        if (depositAccount == null)
            throw new CRFValidationException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "depositAccount is null");

        if (depositAccount.getStatus() != ServerConstants.DEPOSIT_ACCOUNT_STATUS_INITIATED)
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#5");

    }

    private void validateCustomer(Customer customer) throws CRFException {
        if (customer == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "customer is null");

        if (customer.getIsDeleted() || !customer.getIsPassportScanVerified())
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "customer is not active");
    }

    private void validateDepositAmount(DepositProduct depositProduct, BigDecimal depositAmount) throws CRFValidationException {
        if (depositAmount.compareTo(depositProduct.getDepositMinAmount()) < 0)
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_TEXT, "#1");

        if (depositAmount.compareTo(depositProduct.getDepositMaxAmount()) > 0)
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_TEXT, "#2");
    }
}
