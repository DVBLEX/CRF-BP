package com.crf.server.rest.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.entity.DepositAccountDocument;
import com.crf.server.base.entity.DepositProduct;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFNotFoundException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.DepositAccountJson;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.DepositAccountDocumentService;
import com.crf.server.base.service.DepositAccountService;
import com.crf.server.base.service.DepositProductService;
import com.crf.server.base.service.OperatorService;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@RestController
@RequestMapping("/depositaccount")
public class DepositAccountController {

    private OperatorService               operatorService;
    private CustomerService               customerService;
    private DepositAccountService         depositAccountService;
    private DepositAccountDocumentService depositAccountDocumentService;
    private DepositProductService         depositProductService;

    @Autowired
    public void setOperatorService(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setDepositAccountService(DepositAccountService depositAccountService) {
        this.depositAccountService = depositAccountService;
    }

    @Autowired
    public void setDepositAccountDocumentService(DepositAccountDocumentService depositAccountDocumentService) {
        this.depositAccountDocumentService = depositAccountDocumentService;
    }

    @Autowired
    public void setDepositProductService(DepositProductService depositProductService) {
        this.depositProductService = depositProductService;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity list(HttpServletResponse response, Pageable pageable) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedInvestor = operatorService.getLoggedInvestor();

        PageList<DepositAccountJson> depositAccounts = depositAccountService.getDepositAccountsByCustomerId(pageable, loggedInvestor.getCustomerId());

        apiResponse.setDataList(depositAccounts.getDataList());
        apiResponse.setPage(depositAccounts.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity save(HttpServletResponse response, @RequestBody DepositAccountJson depositAccountJson) throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedInvestor = operatorService.getLoggedInvestor();

        validateDepositAccountJson(depositAccountJson);

        // check if there already exists a deposit account with such bank transfer reference. If so, then throw exception
        DepositAccount depositAccount = depositAccountService.getDepositAccountByBankTransferReference(depositAccountJson);

        if (depositAccount != null)
            throw new CRFValidationException(ServerResponseConstants.INVALID_BANK_TRANSFER_REF_CODE, ServerResponseConstants.INVALID_BANK_TRANSFER_REF_TEXT, "#2");

        Customer customer = customerService.getCustomerById(loggedInvestor.getCustomerId());
        if (customer == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#customer is null");

        DepositProduct depositProduct = depositProductService.getDepositProductByCode(depositAccountJson.getDepositProductJson().getCode());

        if (depositProduct == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositProduct is null");

        validateDepositProductConditions(depositAccountJson, depositProduct);

        depositAccountService.saveDeposit(loggedInvestor.getId(), depositAccountJson, depositProduct, customer);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PostMapping(value = "/calc/withdrawal/stats", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity calcWithdrawalStats(HttpServletResponse response, @RequestBody DepositAccountJson depositAccountJson) throws CRFException, CRFValidationException,
        Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        operatorService.getLoggedInvestor();

        if (StringUtils.isBlank(depositAccountJson.getCode()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

        DepositAccount depositAccount = depositAccountService.getDepositAccountByCode(depositAccountJson.getCode());

        validateDepositAccountStatus(depositAccount);

        setDepositAccountData(depositAccount, depositAccountJson);

        apiResponse.setData(setDepositAccountData(depositAccount, depositAccountJson));

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @ResponseBody
    @PostMapping(value = "/doc/download", headers = "Accept=*/*")
    public ResponseEntity<InputStreamResource> downloadDepositAccountDocument(HttpServletRequest request, @RequestParam(value = "code") String code) throws CRFException,
        CRFNotFoundException {

        File file;
        InputStreamResource inputStreamResource;

        operatorService.getLoggedInvestor();

        if (StringUtils.isBlank(code))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "code is null or empty");

        String path = getDepositAccountDocumentPath(code);

        try {

            file = new File(path);

            inputStreamResource = new InputStreamResource(new FileInputStream(file));

        } catch (FileNotFoundException fnfe) {

            log.error(fnfe.getMessage());

            throw new CRFNotFoundException(ServerResponseConstants.FILE_NOT_FOUND_CODE, ServerResponseConstants.FILE_NOT_FOUND_TEXT, "depositAccountDocument");
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
        responseHeaders.setContentDispositionFormData(file.getName(), file.getName());

        return new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.OK);
    }

    private String getDepositAccountDocumentPath(String code) throws CRFException {
        DepositAccountDocument depositAccountDocument;
        try {

            depositAccountDocument = depositAccountDocumentService.getDepositAccountDocumentByCode(code);

        } catch (Exception e) {

            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, e.getMessage());
        }

        if (depositAccountDocument.getPath() == null)

            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "depositAccountDocument is null");

        return depositAccountDocument.getPath();
    }

    private void validateDepositAccountStatus(DepositAccount depositAccount) throws CRFValidationException, CRFException {

        if (depositAccount == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositAccount is null");

        if (depositAccount.getStatus() != ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE && depositAccount.getStatus() != ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED)
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#2");
    }

    private DepositAccountJson setDepositAccountData(DepositAccount depositAccount, DepositAccountJson depositAccountJson) {

        BigDecimal accruedInterest = BigDecimal.ZERO; // any interest not paid out yet.
        BigDecimal totalInterest = BigDecimal.ZERO; // interest already paid out + accrued (not paid out yet). the total interest earned

        if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED) {
            totalInterest = depositAccount.getInterestEarnedAmount();

        } else {
            totalInterest = depositAccountService.calculateDepositInterestEarnedSoFar(depositAccount, LocalDate.now(), true);

            accruedInterest = totalInterest.subtract(depositAccount.getInterestEarnedAmount());
        }

        BigDecimal withdrawalAmount = depositAccount.getDepositAmount().add(accruedInterest); // the withdrawal amount (deposit + any accrued interest)
        withdrawalAmount = withdrawalAmount.subtract(depositAccount.getWithdrawalFee()); // apply the withdrawal fee

        depositAccountJson.setAccruedInterest(accruedInterest.toString());
        depositAccountJson.setTotalInterest(totalInterest.toString());
        depositAccountJson.setDepositPlusInterestAmount(withdrawalAmount.toString());

        return depositAccountJson;
    }

    private void validateDepositAccountJson(DepositAccountJson depositAccountJson) throws CRFValidationException {

        if (StringUtils.isBlank(depositAccountJson.getDepositProductJson().getCode()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

        if (StringUtils.isBlank(depositAccountJson.getDepositAmount()))
            throw new CRFValidationException(ServerResponseConstants.MISSING_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.MISSING_DEPOSIT_AMOUNT_TEXT, "");

        if (depositAccountJson.getInterestPayoutFrequency() == null)
            throw new CRFValidationException(ServerResponseConstants.MISSING_DEPOSIT_INTEREST_PAYOUT_FREQUENCY_CODE,
                ServerResponseConstants.MISSING_DEPOSIT_INTEREST_PAYOUT_FREQUENCY_TEXT, "");

        if (depositAccountJson.getInterestPayoutFrequency() != ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY
            && depositAccountJson.getInterestPayoutFrequency() != ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY
            && depositAccountJson.getInterestPayoutFrequency() != ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY)
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_INTEREST_PAYOUT_FREQUENCY_CODE,
                ServerResponseConstants.INVALID_DEPOSIT_INTEREST_PAYOUT_FREQUENCY_TEXT, "");

        if (StringUtils.isBlank(depositAccountJson.getInterestRate()))
            throw new CRFValidationException(ServerResponseConstants.MISSING_INTEREST_RATE_CODE, ServerResponseConstants.MISSING_INTEREST_RATE_TEXT, "");

        if (StringUtils.isBlank(depositAccountJson.getTermYears()))
            throw new CRFValidationException(ServerResponseConstants.MISSING_TERM_YEARS_CODE, ServerResponseConstants.MISSING_TERM_YEARS_TEXT, "");

        try {
            int termYears = Integer.parseInt(depositAccountJson.getTermYears());

            if (termYears <= 0 || termYears > 100)

                throw new CRFValidationException(ServerResponseConstants.INVALID_TERM_YEARS_CODE, ServerResponseConstants.INVALID_TERM_YEARS_TEXT, "#1");

        } catch (Exception e) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_TERM_YEARS_CODE, ServerResponseConstants.INVALID_TERM_YEARS_TEXT, "#2");
        }

        if (StringUtils.isBlank(depositAccountJson.getBankTransferReference()))
            throw new CRFValidationException(ServerResponseConstants.MISSING_BANK_TRANSFER_REF_CODE, ServerResponseConstants.MISSING_BANK_TRANSFER_REF_TEXT, "");

        if (!depositAccountJson.getBankTransferReference().matches(ServerConstants.REGEX_BANK_TRANSFER_REF))
            throw new CRFValidationException(ServerResponseConstants.INVALID_BANK_TRANSFER_REF_CODE, ServerResponseConstants.INVALID_BANK_TRANSFER_REF_TEXT, "#1");
    }

    private void validateDepositProductConditions(DepositAccountJson depositAccountJson, DepositProduct depositProduct) throws CRFValidationException {

        // double check the request data. It should be matching with the selected product on record otherwise it could be a hacking attempt
        if (depositAccountJson.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY && !depositAccountJson.getInterestRate().equals(
            depositProduct.getQuarterlyInterestRate().toString()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#3");

        if (depositAccountJson.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY && !depositAccountJson.getInterestRate().equals(
            depositProduct.getYearlyInterestRate().toString()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#4");

        if (depositAccountJson.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY
            && !depositAccountJson.getInterestRate().equals(depositProduct.getTwiceYearlyInterestRate().toString()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#5");

        if (!depositAccountJson.getTermYears().equals(depositProduct.getTermYears().toString()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#6");

        BigDecimal depositAmount = new BigDecimal(depositAccountJson.getDepositAmount());

        if (depositAmount.compareTo(depositProduct.getDepositMinAmount()) < 0)
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_TEXT, "#1");

        if (depositAmount.compareTo(depositProduct.getDepositMaxAmount()) > 0)
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_TEXT, "#2");
    }
}
