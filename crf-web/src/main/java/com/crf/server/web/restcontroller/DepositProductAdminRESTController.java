package com.crf.server.web.restcontroller;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.DepositProductJson;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.DepositProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@RestController
@RequestMapping("/depositproductadmin")
public class DepositProductAdminRESTController {
    private static final int MAX_DEPOSIT_PRODUCT_NAME_LENGTH = 32;
    private static final int MAX_DEPOSIT_PRODUCT_DESCRIPTION_LENGTH = 256;
    private static final int MIN_PREMATURE_WITHDRAWAL_DAYS = 0;
    private static final int MAX_PREMATURE_WITHDRAWAL_DAYS = 80;
    private static final BigDecimal MIN_DEPOSIT_AMOUNT = BigDecimal.valueOf(5000.00);
    private static final BigDecimal MIN_INTEREST_RATE = BigDecimal.valueOf(0.01);
    private static final BigDecimal MAX_INTEREST_RATE = BigDecimal.valueOf(10.00);
    private static final BigDecimal MIN_WITHDRAWAL_FEE = BigDecimal.valueOf(0.00);
    private static final BigDecimal MAX_WITHDRAWAL_FEE = BigDecimal.valueOf(1000.00);

    private OperatorRepository operatorRepository;
    private DepositProductService depositProductService;

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setDepositProductService(DepositProductService depositProductService) {
        this.depositProductService = depositProductService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity getAllDepositProduct(HttpServletResponse response, @RequestParam(value = "page") int page, @RequestParam(value = "size") int size) throws CRFException, Exception {
        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();


        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_ADMIN) {
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not Admin");
        }

        PageList<DepositProductJson> depositProductJsonPageList = depositProductService.getAllDepositProductList(ServerUtil.createDefaultPageRequest(page, size));

        apiResponse.setDataList(depositProductJsonPageList.getDataList());
        apiResponse.setPage(depositProductJsonPageList.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity editDepositProduct(HttpServletResponse response, @RequestBody DepositProductJson depositProductJson)
            throws CRFException, CRFValidationException, Exception {
        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        //BigDecimal fields need validation
        BigDecimal minDepositAmount;
        BigDecimal maxDepositAmount;
        BigDecimal quarterlyInterestRate;
        BigDecimal yearlyInterestRate;
        BigDecimal twiceYearlyInterestRate;
        BigDecimal prematureWithdrawalInterestRate;
        BigDecimal withdrawalFee;

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null) {
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");
        }
        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_ADMIN) {
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, "Access denied", "loggedOperator is not Admin");
        }

        try {

            minDepositAmount = new BigDecimal(depositProductJson.getDepositMinAmount());
            maxDepositAmount = new BigDecimal(depositProductJson.getDepositMaxAmount());
            quarterlyInterestRate = new BigDecimal(depositProductJson.getQuarterlyInterestRate());
            yearlyInterestRate = new BigDecimal(depositProductJson.getYearlyInterestRate());
            twiceYearlyInterestRate = new BigDecimal(depositProductJson.getTwiceYearlyInterestRate());
            prematureWithdrawalInterestRate = new BigDecimal(depositProductJson.getPrematureWithdrawalInterestRate());
            withdrawalFee = new BigDecimal(depositProductJson.getWithdrawalFee());

        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
            throw new CRFValidationException(ServerResponseConstants.INVALID_BIG_DECIMAL_NUMBER_FORMAT_CODE, ServerResponseConstants.INVALID_BIG_DECIMAL_NUMBER_FORMAT_TEXT, "#1");
        }

            //Name Validation
            if (StringUtils.isBlank(depositProductJson.getName())) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "#1");

            } else if (depositProductJson.getName().length() > MAX_DEPOSIT_PRODUCT_NAME_LENGTH) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_PRODUCT_NAME_CODE, ServerResponseConstants.INVALID_DEPOSIT_PRODUCT_NAME_TEXT, "");
            }

            //Description validation
            if (StringUtils.isBlank(depositProductJson.getDescription())) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "");

            } else if (depositProductJson.getDescription().length() > MAX_DEPOSIT_PRODUCT_DESCRIPTION_LENGTH) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_PRODUCT_DESCRIPTION_CODE, ServerResponseConstants.INVALID_DEPOSIT_PRODUCT_DESCRIPTION_TEXT, "");
            }

            //min deposit amount validation
            if (minDepositAmount.compareTo(MIN_DEPOSIT_AMOUNT) < 0)
                throw new CRFValidationException(ServerResponseConstants.INVALID_MIN_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_MIN_DEPOSIT_AMOUNT_TEXT, "#1");

            //max deposit amount validation
            if (maxDepositAmount.compareTo(MIN_DEPOSIT_AMOUNT) < 0) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_MAX_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_MAX_DEPOSIT_AMOUNT_TEXT, "#1");
            }

            if (maxDepositAmount.compareTo(minDepositAmount) < 0) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_MAX_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_MAX_DEPOSIT_AMOUNT_TEXT, "#2");
            }

            //quarterly Interest rate validation
            if (quarterlyInterestRate.compareTo(MIN_INTEREST_RATE) < 0 | quarterlyInterestRate.compareTo(MAX_INTEREST_RATE) > 0) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_QUARTERLY_INTEREST_RATE_CODE, ServerResponseConstants.INVALID_DEPOSIT_QUARTERLY_INTEREST_RATE_TEXT, "#1");
            }

            //quarterly Interest rate validation
            if (yearlyInterestRate.compareTo(MIN_INTEREST_RATE) < 0 | yearlyInterestRate.compareTo(MAX_INTEREST_RATE) > 0) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_YEARLY_INTEREST_RATE_CODE, ServerResponseConstants.INVALID_DEPOSIT_YEARLY_INTEREST_RATE_TEXT, "#1");
            }

            //twice yearly interest rate validation
            if (twiceYearlyInterestRate.compareTo(MIN_INTEREST_RATE) < 0 | twiceYearlyInterestRate.compareTo(MAX_INTEREST_RATE) > 0) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_TWICE_YEARLY_INTEREST_RATE_CODE, ServerResponseConstants.INVALID_DEPOSIT_TWICE_YEARLY_INTEREST_RATE_TEXT, "#1");
            }

            try {
                int termYear = Integer.parseInt(depositProductJson.getTermYears());

                if (termYear < 1 | termYear > 99)
                    throw new CRFValidationException(ServerResponseConstants.INVALID_TERM_YEARS_CODE, ServerResponseConstants.INVALID_TERM_YEARS_TEXT, " Min value = 1");

            } catch (Exception e) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_TERM_YEARS_CODE, ServerResponseConstants.INVALID_TERM_YEARS_TEXT, "#2");
            }

            //premature withdrawal min days validation
            if (depositProductJson.getPrematureWithdrawalMinDays() < MIN_PREMATURE_WITHDRAWAL_DAYS | depositProductJson.getPrematureWithdrawalMinDays() > MAX_PREMATURE_WITHDRAWAL_DAYS) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_PREMATURE_WITHDRAWAL_MIN_DAYS_CODE, ServerResponseConstants.INVALID_PREMATURE_WITHDRAWAL_MIN_DAYS_TEXT, "");
            }

            //premature withdrawal interest rate
            if (prematureWithdrawalInterestRate.compareTo(MIN_INTEREST_RATE) < 0 | prematureWithdrawalInterestRate.compareTo(MAX_INTEREST_RATE) > 0) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_PREMATURE_WITHDRAWAL_INTEREST_RATE_CODE, ServerResponseConstants.INVALID_PREMATURE_WITHDRAWAL_INTEREST_RATE_TEXT, "");
            }

            //withdrawal fee validation
            if (withdrawalFee.compareTo(MIN_WITHDRAWAL_FEE) < 0 | withdrawalFee.compareTo(MAX_WITHDRAWAL_FEE) > 0) {
                throw new CRFValidationException(ServerResponseConstants.INVALID_WITHDRAWAL_FEE_VALUE_CODE, ServerResponseConstants.INVALID_WITHDRAWAL_FEE_VALUE_TEXT, "");
            }

            StringUtils.isNumeric(withdrawalFee.toString());


        depositProductService.editDepositProduct(depositProductJson);

        response.setStatus(HttpServletResponse.SC_OK);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

        return apiResponse;
    }
}
