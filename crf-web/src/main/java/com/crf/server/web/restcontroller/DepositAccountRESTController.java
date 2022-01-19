package com.crf.server.web.restcontroller;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.BankAccount;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.entity.DepositAccountDocument;
import com.crf.server.base.entity.DepositProduct;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.DepositAccountDocumentJson;
import com.crf.server.base.jsonentity.DepositAccountJson;
import com.crf.server.base.jsonentity.DepositStatementJson;
import com.crf.server.base.repository.BankAccountRepository;
import com.crf.server.base.repository.DepositAccountRepository;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.DepositAccountDocumentService;
import com.crf.server.base.service.DepositAccountService;
import com.crf.server.base.service.DepositProductService;
import com.crf.server.base.service.DepositStatementService;

@RestController
@RequestMapping("/depositaccount")
public class DepositAccountRESTController {

    private DepositAccountRepository      depositAccountRepository;
    private BankAccountRepository         bankAccountRepository;
    private OperatorRepository            operatorRepository;
    private CustomerService               customerService;
    private DepositAccountService         depositAccountService;
    private DepositAccountDocumentService depositAccountDocumentService;
    private DepositProductService         depositProductService;
    private DepositStatementService       depositStatementService;

    @Autowired
    public void setDepositAccountRepository(DepositAccountRepository depositAccountRepository) {
        this.depositAccountRepository = depositAccountRepository;
    }

    @Autowired
    public void setBankAccountRepository(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
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

    @Autowired
    public void setDepositStatementService(DepositStatementService depositStatementService) {
        this.depositStatementService = depositStatementService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity list(HttpServletResponse response, @RequestParam(value = "page") int page, @RequestParam(value = "size") int size) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_INVESTOR)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not investor");

        PageList<DepositAccountJson> depositAccounts = depositAccountService.getDepositAccountsByCustomerId(ServerUtil.createDefaultPageRequest(page, size),
            loggedOperator.getCustomerId());

        apiResponse.setDataList(depositAccounts.getDataList());
        apiResponse.setPage(depositAccounts.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity save(HttpServletResponse response, @RequestBody DepositAccountJson depositAccountJson) throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_INVESTOR)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not investor");

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

        // check if there already exists a deposit account with such bank transfer reference. If so, then throw exception
        DepositAccount depositAccount = depositAccountRepository.findByBankTransferReference(depositAccountJson.getBankTransferReference());
        if (depositAccount != null)
            throw new CRFValidationException(ServerResponseConstants.INVALID_BANK_TRANSFER_REF_CODE, ServerResponseConstants.INVALID_BANK_TRANSFER_REF_TEXT, "#2");

        Customer customer = customerService.getCustomerById(loggedOperator.getCustomerId());
        if (customer == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#customer is null");

        DepositProduct depositProduct = depositProductService.getDepositProductByCode(depositAccountJson.getDepositProductJson().getCode());
        if (depositProduct == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositProduct is null");

        // double check the request data. It should be matching with the selected product on record otherwise it could be a hacking attempt
        if (depositAccountJson.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY
            && !depositAccountJson.getInterestRate().equals(depositProduct.getQuarterlyInterestRate().toString()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#3");

        if (depositAccountJson.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY
            && !depositAccountJson.getInterestRate().equals(depositProduct.getYearlyInterestRate().toString()))
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

        depositAccountService.saveDeposit(loggedOperator.getId(), depositAccountJson, depositProduct, customer);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/calc/withdrawal/stats", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity calcWithdrawalStats(HttpServletResponse response, @RequestBody DepositAccountJson depositAccountJson)
        throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_INVESTOR)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not investor");

        if (StringUtils.isBlank(depositAccountJson.getCode()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

        DepositAccount depositAccount = depositAccountService.getDepositAccountByCode(depositAccountJson.getCode());
        if (depositAccount == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositAccount is null");

        if (depositAccount.getStatus() != ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE && depositAccount.getStatus() != ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED)
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#2");

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

        apiResponse.setData(depositAccountJson);
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/request/withdrawal", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity requestWithdrawal(HttpServletResponse response, @RequestBody DepositAccountJson depositAccountJson)
        throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_INVESTOR)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not investor");

        if (StringUtils.isBlank(depositAccountJson.getCode()))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

        DepositAccount depositAccount = depositAccountService.getDepositAccountByCode(depositAccountJson.getCode());
        if (depositAccount == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositAccount is null");

        if (depositAccount.getStatus() != ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE && depositAccount.getStatus() != ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED)
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#2");

        BankAccount bankAccount = bankAccountRepository.findByCustomerId(depositAccount.getCustomerId());
        if (bankAccount == null)
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#3");

        depositAccountService.requestWithdrawal(loggedOperator.getId(), depositAccount, LocalDate.now());

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/doc/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity documentsList(HttpServletResponse response, @RequestParam(value = "page") int page, @RequestParam(value = "size") int size)
        throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_INVESTOR)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not investor");

        PageList<DepositAccountDocumentJson> depositAccountDocuments = depositAccountDocumentService
            .getDepositAccountDocumentsByCustomerId(ServerUtil.createDefaultPageRequest(page, size), loggedOperator.getCustomerId());

        apiResponse.setDataList(depositAccountDocuments.getDataList());
        apiResponse.setPage(depositAccountDocuments.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/doc/download", headers = "Accept=*/*", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<InputStreamResource> downloadDepositAccountDocument(HttpServletRequest request, @RequestParam(value = "code") String code)
        throws CRFException, Exception {

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_INVESTOR)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not investor");

        if (StringUtils.isBlank(code))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "code is null or empty");

        DepositAccountDocument depositAccountDocument = depositAccountDocumentService.getDepositAccountDocumentByCode(code);

        if (depositAccountDocument == null)
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "depositAccountDocument is null");

        File file = new File(depositAccountDocument.getPath());

        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(file.length());
        responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
        responseHeaders.setContentDispositionFormData(file.getName(), file.getName());

        return new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/statement/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity listDepositStatements(HttpServletResponse response, @RequestParam(value = "page") int page, @RequestParam(value = "size") int size)
        throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_INVESTOR)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not investor");

        PageList<DepositStatementJson> depositStatementsList = depositStatementService.getDepositStatementsByCustomerId(ServerUtil.createDefaultPageRequest(page, size),
            loggedOperator.getCustomerId());

        apiResponse.setDataList(depositStatementsList.getDataList());
        apiResponse.setPage(depositStatementsList.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
