package com.crf.server.web.restcontroller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.entity.DepositProduct;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.AdminStatsJson;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.BankDetailsJson;
import com.crf.server.base.jsonentity.DepositAccountJson;
import com.crf.server.base.jsonentity.DepositProductJson;
import com.crf.server.base.jsonentity.InvestorStatsJson;
import com.crf.server.base.repository.CustomerRepository;
import com.crf.server.base.repository.DepositAccountRepository;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.DepositAccountService;
import com.crf.server.base.service.DepositProductService;

@RestController
@RequestMapping("/depositproduct")
public class DepositProductRESTController {

    private CustomerRepository       customerRepository;
    private DepositAccountRepository depositAccountRepository;
    private OperatorRepository       operatorRepository;
    private DepositAccountService    depositAccountService;
    private DepositProductService    depositProductService;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setDepositAccountRepository(DepositAccountRepository depositAccountRepository) {
        this.depositAccountRepository = depositAccountRepository;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setDepositAccountService(DepositAccountService depositAccountService) {
        this.depositAccountService = depositAccountService;
    }

    @Autowired
    public void setDepositProductService(DepositProductService depositProductService) {
        this.depositProductService = depositProductService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity list(HttpServletResponse response) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        AdminStatsJson adminStatsJson = new AdminStatsJson();
        InvestorStatsJson investorStatsJson = new InvestorStatsJson();

        LocalDate localDateToday = LocalDate.now();

        Operator loggedOperator = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() == ServerConstants.OPERATOR_ROLE_ADMIN) {

            adminStatsJson.setInvestorCount(customerRepository.countActiveCustomersByType(ServerConstants.CUSTOMER_TYPE_INVESTOR));

            List<DepositAccount> depositAccountList = depositAccountRepository.findAllOrderByDateCreated();

            for (DepositAccount depositAccount : depositAccountList) {

                switch (depositAccount.getStatus()) {

                    case ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE:
                        adminStatsJson.setActiveDepositCount(adminStatsJson.getActiveDepositCount() + 1);
                        adminStatsJson.setTotalActiveDepositAmount(adminStatsJson.getTotalActiveDepositAmount().add(depositAccount.getDepositAmount()));
                        break;

                    default:
                        break;
                }

                adminStatsJson.setTotalInterestPaidAmount(adminStatsJson.getTotalInterestPaidAmount().add(depositAccount.getInterestEarnedAmount()));
                adminStatsJson.setTotalInterestPaidAmountString(adminStatsJson.getTotalInterestPaidAmount().toString());
                adminStatsJson.setTotalActiveDepositAmountString(adminStatsJson.getTotalActiveDepositAmount().toString());
            }

        } else if (loggedOperator.getRoleId() == ServerConstants.OPERATOR_ROLE_INVESTOR) {

            List<DepositAccount> depositAccountList = depositAccountRepository.findAllByCustomerIdOrderByIdDesc(loggedOperator.getCustomerId());

            // calculate the logged in investor stats
            for (DepositAccount depositAccount : depositAccountList) {

                switch (depositAccount.getStatus()) {

                    case ServerConstants.DEPOSIT_ACCOUNT_STATUS_INITIATED:
                        investorStatsJson.setInitiatedDepositCount(investorStatsJson.getInitiatedDepositCount() + 1);
                        break;

                    case ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE:
                        investorStatsJson.setActiveDepositCount(investorStatsJson.getActiveDepositCount() + 1);
                        investorStatsJson.setTotalActiveDepositAmount(investorStatsJson.getTotalActiveDepositAmount().add(depositAccount.getDepositAmount()));

                        investorStatsJson.setTotalAccruedInterestAmount(investorStatsJson.getTotalAccruedInterestAmount()
                            .add(depositAccountService.calculateDepositInterestEarnedSoFar(depositAccount, localDateToday, false)));
                        investorStatsJson.setTotalAccruedInterestAmount(investorStatsJson.getTotalAccruedInterestAmount().subtract(depositAccount.getInterestEarnedAmount()));

                        investorStatsJson.setTotalInterestEarnedAmount(investorStatsJson.getTotalInterestEarnedAmount().add(depositAccount.getInterestEarnedAmount()));
                        break;

                    case ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED:
                    case ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWAL_REQUESTED:
                    case ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWN:
                        investorStatsJson.setTotalInterestEarnedAmount(investorStatsJson.getTotalInterestEarnedAmount().add(depositAccount.getInterestEarnedAmount()));
                        break;

                    default:
                        break;
                }

                investorStatsJson.setTotalActiveDepositAmountString(investorStatsJson.getTotalActiveDepositAmount().toString());
                investorStatsJson.setTotalInterestEarnedAmountString(investorStatsJson.getTotalInterestEarnedAmount().toString());
                investorStatsJson.setTotalAccruedInterestAmountString(investorStatsJson.getTotalAccruedInterestAmount().toString());
            }
        }

        List<DepositProductJson> depositProducts = depositProductService.getDepositProducts();

        if (loggedOperator.getRoleId() == ServerConstants.OPERATOR_ROLE_ADMIN) {
            apiResponse.setSingleData(adminStatsJson);

        } else if (loggedOperator.getRoleId() == ServerConstants.OPERATOR_ROLE_INVESTOR) {
            apiResponse.setSingleData(investorStatsJson);
        }
        apiResponse.setDataList(depositProducts);
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/calc/interest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity calcInterest(HttpServletResponse response, @RequestBody DepositAccountJson depositAccountJson)
        throws CRFException, CRFValidationException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        if (StringUtils.isBlank(depositAccountJson.getDepositAmount()))
            throw new CRFValidationException(ServerResponseConstants.MISSING_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.MISSING_DEPOSIT_AMOUNT_TEXT, "");

        if (StringUtils.isBlank(depositAccountJson.getInterestRate()))
            throw new CRFValidationException(ServerResponseConstants.MISSING_INTEREST_RATE_CODE, ServerResponseConstants.MISSING_INTEREST_RATE_TEXT, "");

        int term = 0;
        try {
            term = Integer.parseInt(depositAccountJson.getTermYears());

            if (term <= 0 || term > 100)
                throw new CRFValidationException(ServerResponseConstants.INVALID_TERM_YEARS_CODE, ServerResponseConstants.INVALID_TERM_YEARS_TEXT, "#1");

        } catch (Exception e) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_TERM_YEARS_CODE, ServerResponseConstants.INVALID_TERM_YEARS_TEXT, "#2");
        }

        DepositProduct depositProduct = depositProductService.getDepositProductByCode(depositAccountJson.getDepositProductJson().getCode());
        if (depositProduct == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositProduct is null");

        Date dateOpen = new Date();
        Date dateMaturity = null;

        LocalDate localDateOpen = dateOpen.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        dateMaturity = Date.from(localDateOpen.plusYears(term).atStartOfDay(ZoneId.systemDefault()).toInstant());

        BigDecimal depositAmount = new BigDecimal(depositAccountJson.getDepositAmount());
        BigDecimal interestRate = new BigDecimal(depositAccountJson.getInterestRate());
        BigDecimal termYears = new BigDecimal(depositAccountJson.getTermYears());

        if (depositAmount.compareTo(depositProduct.getDepositMinAmount()) < 0)
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_TEXT, "#1");

        if (depositAmount.compareTo(depositProduct.getDepositMaxAmount()) > 0)
            throw new CRFValidationException(ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_CODE, ServerResponseConstants.INVALID_DEPOSIT_AMOUNT_TEXT, "#2");

        depositAccountJson.setBankTransferReference(depositAccountService.generateBankTransferReference());
        depositAccountJson.setTotalInterest(depositProductService.calculateDepositTotalInterest(depositAmount, interestRate, termYears).toString());
        depositAccountJson.setDateMaturityString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, dateMaturity));

        BankDetailsJson bankDetailsJson = new BankDetailsJson();
        bankDetailsJson.setBankAccountName(ServerConstants.BANK_ACCOUNT_NAME);
        bankDetailsJson.setIban(ServerConstants.BANK_IBAN);
        bankDetailsJson.setBic(ServerConstants.BANK_BIC);
        bankDetailsJson.setName(ServerConstants.BANK_NAME);
        bankDetailsJson.setAddress(ServerConstants.BANK_ADDRESS);

        depositAccountJson.setBankDetailsJson(bankDetailsJson);

        apiResponse.setData(depositAccountJson);
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
