package com.crf.server.rest.controller;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.DepositProductJson;
import com.crf.server.base.service.AdminService;
import com.crf.server.base.service.DepositProductService;

@RestController
@RequestMapping(value = "/depositproductadmin", produces = MediaType.APPLICATION_JSON_VALUE)
public class DepositProductAdminController {

    private static final int        MAX_DEPOSIT_PRODUCT_NAME_LENGTH        = 32;
    private static final int        MAX_DEPOSIT_PRODUCT_DESCRIPTION_LENGTH = 256;
    private static final int        MIN_PREMATURE_WITHDRAWAL_DAYS          = 0;
    private static final int        MAX_PREMATURE_WITHDRAWAL_DAYS          = 80;
    private static final BigDecimal MIN_DEPOSIT_AMOUNT                     = BigDecimal.valueOf(5000.00);
    private static final BigDecimal MIN_INTEREST_RATE                      = BigDecimal.valueOf(0.01);
    private static final BigDecimal MAX_INTEREST_RATE                      = BigDecimal.valueOf(10.00);
    private static final BigDecimal MIN_WITHDRAWAL_FEE                     = BigDecimal.valueOf(0.00);
    private static final BigDecimal MAX_WITHDRAWAL_FEE                     = BigDecimal.valueOf(1000.00);

    private DepositProductService   depositProductService;
    private AdminService            adminService;

    @Autowired
    public void setDepositProductService(DepositProductService depositProductService) {
        this.depositProductService = depositProductService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/list")
    public ApiResponseJsonEntity getAllDepositProduct(HttpServletResponse response, Pageable pageable) throws CRFException, Exception {
        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        adminService.validateLoggedAdmin();

        PageList<DepositProductJson> depositProductJsonPageList = depositProductService.getAllDepositProductList(pageable);

        apiResponse.setDataList(depositProductJsonPageList.getDataList());
        apiResponse.setPage(depositProductJsonPageList.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PutMapping("/edit")
    public ApiResponseJsonEntity editDepositProduct(HttpServletResponse response, @RequestBody DepositProductJson depositProductJson) throws CRFException, CRFValidationException {
        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        validateDepositProductJson(depositProductJson);

        try {
            depositProductService.editDepositProduct(depositProductJson);
        } catch (IOException e) {
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, e.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_OK);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

        return apiResponse;
    }

    private void validateDepositProductJson(DepositProductJson depositProductJson) throws CRFValidationException, CRFException {
        //BigDecimal fields that need validation
        BigDecimal minDepositAmount;
        BigDecimal maxDepositAmount;
        BigDecimal quarterlyInterestRate;
        BigDecimal yearlyInterestRate;
        BigDecimal twiceYearlyInterestRate;
        BigDecimal prematureWithdrawalInterestRate;
        BigDecimal withdrawalFee;

        adminService.validateLoggedAdmin();

        try {

            minDepositAmount = new BigDecimal(depositProductJson.getDepositMinAmount());
            maxDepositAmount = new BigDecimal(depositProductJson.getDepositMaxAmount());
            quarterlyInterestRate = new BigDecimal(depositProductJson.getQuarterlyInterestRate());
            yearlyInterestRate = new BigDecimal(depositProductJson.getYearlyInterestRate());
            twiceYearlyInterestRate = new BigDecimal(depositProductJson.getTwiceYearlyInterestRate());
            prematureWithdrawalInterestRate = new BigDecimal(depositProductJson.getPrematureWithdrawalInterestRate());
            withdrawalFee = new BigDecimal(depositProductJson.getWithdrawalFee());

        } catch (NumberFormatException e) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_BIG_DECIMAL_NUMBER_FORMAT_CODE, ServerResponseConstants.INVALID_BIG_DECIMAL_NUMBER_FORMAT_TEXT, "#1");
        }

        validateName(depositProductJson.getName());

        validateDescription(depositProductJson.getDescription());

        validateDepositAmount(minDepositAmount, maxDepositAmount);

        validatePeriodInterestRates(quarterlyInterestRate, yearlyInterestRate, twiceYearlyInterestRate);

        try {
            int termYear = Integer.parseInt(depositProductJson.getTermYears());

            if (termYear < 1 || termYear > 99)
                throw new CRFValidationException(ServerResponseConstants.INVALID_TERM_YEARS_CODE, ServerResponseConstants.INVALID_TERM_YEARS_TEXT, " Min value = 1");

        } catch (Exception e) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_TERM_YEARS_CODE, ServerResponseConstants.INVALID_TERM_YEARS_TEXT, "#2");
        }

        validateWithdrawalOptions(depositProductJson.getPrematureWithdrawalMinDays(), prematureWithdrawalInterestRate, withdrawalFee);

    }

    private void validateName(String name) throws CRFValidationException {
        if (StringUtils.isBlank(name)) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

        } else if (name.length() > MAX_DEPOSIT_PRODUCT_NAME_LENGTH) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_PRODUCT_NAME_CODE, ServerResponseConstants.INVALID_DEPOSIT_PRODUCT_NAME_TEXT, "");
        }
    }

    private void validateDescription(String description) throws CRFValidationException {
        if (StringUtils.isBlank(description)) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "");

        } else if (description.length() > MAX_DEPOSIT_PRODUCT_DESCRIPTION_LENGTH) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_PRODUCT_DESCRIPTION_CODE, ServerResponseConstants.INVALID_DEPOSIT_PRODUCT_DESCRIPTION_TEXT,
                "");
        }
    }

    private void validateDepositAmount(BigDecimal min, BigDecimal max) throws CRFValidationException {
        if (min.compareTo(MIN_DEPOSIT_AMOUNT) < 0)
            throw new CRFValidationException(ServerResponseConstants.INVALID_MIN_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_MIN_DEPOSIT_AMOUNT_TEXT, "#1");

        if (max.compareTo(MIN_DEPOSIT_AMOUNT) < 0) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_MAX_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_MAX_DEPOSIT_AMOUNT_TEXT, "#1");
        }

        if (max.compareTo(min) < 0) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_MAX_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_MAX_DEPOSIT_AMOUNT_TEXT, "#2");
        }
    }

    private void validatePeriodInterestRates(BigDecimal quarterly, BigDecimal yearly, BigDecimal twiceYearly) throws CRFValidationException {

        //quarterly Interest rate validation
        if (quarterly.compareTo(MIN_INTEREST_RATE) < 0 || quarterly.compareTo(MAX_INTEREST_RATE) > 0) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_QUARTERLY_INTEREST_RATE_CODE,
                ServerResponseConstants.INVALID_DEPOSIT_QUARTERLY_INTEREST_RATE_TEXT, "#1");
        }

        //yearly Interest rate validation
        if (yearly.compareTo(MIN_INTEREST_RATE) < 0 || yearly.compareTo(MAX_INTEREST_RATE) > 0) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_YEARLY_INTEREST_RATE_CODE, ServerResponseConstants.INVALID_DEPOSIT_YEARLY_INTEREST_RATE_TEXT,
                "#1");
        }

        //twice yearly interest rate validation
        if (twiceYearly.compareTo(MIN_INTEREST_RATE) < 0 || twiceYearly.compareTo(MAX_INTEREST_RATE) > 0) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_TWICE_YEARLY_INTEREST_RATE_CODE,
                ServerResponseConstants.INVALID_DEPOSIT_TWICE_YEARLY_INTEREST_RATE_TEXT, "#1");
        }
    }

    private void validateWithdrawalOptions(Integer prematureWithdrawalMinDays, BigDecimal prematureWithdrawalInterestRate, BigDecimal withdrawalFee) throws CRFValidationException {

        //premature withdrawal min days validation
        if (prematureWithdrawalMinDays < MIN_PREMATURE_WITHDRAWAL_DAYS || prematureWithdrawalMinDays > MAX_PREMATURE_WITHDRAWAL_DAYS) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_PREMATURE_WITHDRAWAL_MIN_DAYS_CODE, ServerResponseConstants.INVALID_PREMATURE_WITHDRAWAL_MIN_DAYS_TEXT,
                "");
        }

        //premature withdrawal interest rate
        if (prematureWithdrawalInterestRate.compareTo(MIN_INTEREST_RATE) < 0 || prematureWithdrawalInterestRate.compareTo(MAX_INTEREST_RATE) > 0) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_PREMATURE_WITHDRAWAL_INTEREST_RATE_CODE,
                ServerResponseConstants.INVALID_PREMATURE_WITHDRAWAL_INTEREST_RATE_TEXT, "");
        }

        //withdrawal fee validation
        if (withdrawalFee.compareTo(MIN_WITHDRAWAL_FEE) < 0 || withdrawalFee.compareTo(MAX_WITHDRAWAL_FEE) > 0) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_WITHDRAWAL_FEE_VALUE_CODE, ServerResponseConstants.INVALID_WITHDRAWAL_FEE_VALUE_TEXT, "");
        }
    }
}
