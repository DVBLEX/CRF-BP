package com.crf.server.base.service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.BankAccount;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.entity.DepositAccountPayment;
import com.crf.server.base.entity.DepositProduct;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.BankDetailsJson;
import com.crf.server.base.jsonentity.DepositAccountPaymentJson;
import com.crf.server.base.jsonentity.PageInfo;
import com.crf.server.base.repository.BankAccountRepository;
import com.crf.server.base.repository.DepositAccountPaymentRepository;
import com.crf.server.base.repository.DepositAccountRepository;

@Service
public class DepositAccountPaymentService {

    private BankAccountRepository           bankAccountRepository;
    private DepositAccountRepository        depositAccountRepository;
    private DepositAccountPaymentRepository depositAccountPaymentRepository;

    private CustomerService                 customerService;
    private DepositAccountService           depositAccountService;
    private DepositAccountDocumentService   depositAccountDocumentService;
    private DepositProductService           depositProductService;
    private DepositStatementService         depositStatementService;
    private EmailService                    emailService;
    private OperatorService                 operatorService;
    private PdfService                      pdfService;

    @Value("${tc.system.url}")
    private String                          systemUrl;

    @Autowired
    public void setBankAccountRepository(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Autowired
    public void setDepositAccountRepository(DepositAccountRepository depositAccountRepository) {
        this.depositAccountRepository = depositAccountRepository;
    }

    @Autowired
    public void setDepositAccountPaymentRepository(DepositAccountPaymentRepository depositAccountPaymentRepository) {
        this.depositAccountPaymentRepository = depositAccountPaymentRepository;
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

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setOperatorService(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Autowired
    public void setPdfService(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    public DepositAccountPayment getDepositAccountPaymentByCode(String code) throws Exception {

        return depositAccountPaymentRepository.findByCode(code);
    }

    @Transactional
    public void saveDepositAccountPayment(DepositAccount depositAccount, LocalDate localDateToday) throws CRFException, Exception {

        BigDecimal interestPaymentAmount = BigDecimal.ZERO;

        Customer customer = customerService.getCustomerById(depositAccount.getCustomerId());
        if (customer == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#customer is null");

        DepositAccountPayment depositAccountPayment = new DepositAccountPayment();
        depositAccountPayment.setCode(SecurityUtil.generateUniqueCode());
        depositAccountPayment.setDepositAccountId(depositAccount.getId());
        depositAccountPayment.setAccountNumber(depositAccount.getAccountNumber());
        depositAccountPayment.setInterestPayoutFrequency(depositAccount.getInterestPayoutFrequency());
        depositAccountPayment.setCustomerId(depositAccount.getCustomerId());
        depositAccountPayment.setCustomerName(customer.getFirstName() + " " + customer.getLastName());

        // calculate how much interest needs to be paid out based on how many days the deposit has been active for in the last quarter / year or half year
        interestPaymentAmount = depositAccountService.calculateDepositInterestEarnedSoFar(depositAccount, localDateToday, false);
        interestPaymentAmount = interestPaymentAmount.subtract(depositAccount.getInterestEarnedAmount());

        depositAccountPayment.setAmount(interestPaymentAmount);
        depositAccountPayment.setOperatorId(ServerConstants.DEFAULT_LONG);
        depositAccountPayment.setIsProcessed(false);
        depositAccountPayment.setDateCreated(new Date());

        LocalDate localDateYesterday = localDateToday.minusDays(1l);

        YearMonth yearMonthObject = null;
        int daysInCurrentYear = 0;

        if (ServerUtil.isBeginningOfTheYear(localDateToday)) {
            // we are at the beginning of the new year so get the number of days in the previous year
            yearMonthObject = YearMonth.of(localDateYesterday.getYear(), localDateYesterday.getMonthValue());
            daysInCurrentYear = yearMonthObject.lengthOfYear();

        } else {
            yearMonthObject = YearMonth.of(localDateToday.getYear(), localDateToday.getMonthValue());
            daysInCurrentYear = yearMonthObject.lengthOfYear();
        }

        switch (depositAccount.getInterestPayoutFrequency()) {

            case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY:

                int daysInCurrentQuarter = ServerUtil.getNumberOfDaysInTheQuarter(localDateYesterday.get(IsoFields.QUARTER_OF_YEAR), yearMonthObject.isLeapYear());

                depositAccountPayment.setDatePeriodFrom(Date.from(localDateToday.minusDays(daysInCurrentQuarter).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                depositAccountPayment.setDatePeriodTo(Date.from(localDateYesterday.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                break;

            case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY:

                depositAccountPayment.setDatePeriodFrom(Date.from(localDateToday.minusDays(daysInCurrentYear).atStartOfDay(ZoneId.systemDefault()).toInstant()));

                if (ServerUtil.isBeginningOfTheYear(localDateToday)) {
                    depositAccountPayment.setDatePeriodTo(Date.from(localDateYesterday.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                } else {
                    depositAccountPayment.setDatePeriodTo(Date.from(localDateToday.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }

                break;

            case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY:

                LocalDate localDateDeposit6MonthPeriodEnd;

                if (depositAccount.getDateLastInterestPayment() == null) {
                    localDateDeposit6MonthPeriodEnd = depositAccount.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(6l);
                    depositAccountPayment.setDatePeriodFrom(depositAccount.getDateStart());

                } else {
                    localDateDeposit6MonthPeriodEnd = depositAccount.getDateLastInterestPayment().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(6l);
                    depositAccountPayment.setDatePeriodFrom(depositAccount.getDateLastInterestPayment());
                }
                depositAccountPayment.setDatePeriodTo(Date.from(localDateDeposit6MonthPeriodEnd.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                break;

            default:
                break;
        }

        depositAccountPaymentRepository.save(depositAccountPayment);

        depositAccount.setInterestEarnedAmount(depositAccount.getInterestEarnedAmount().add(interestPaymentAmount));
        depositAccount.setDateLastInterestPayment(new Date());

        depositAccountRepository.save(depositAccount);

        depositStatementService.saveDepositStatement(depositAccount, ServerConstants.DEPOSIT_STATEMENT_TYPE_INTEREST_EARNED, interestPaymentAmount);
    }

    public PageList<DepositAccountPaymentJson> getDepositAccountPayments(Pageable pageable) throws CRFException, Exception {

        List<DepositAccountPaymentJson> resultList = new ArrayList<>();

        Page<DepositAccountPayment> depositAccountPaymentPage = depositAccountPaymentRepository.findAll(pageable);

        for (DepositAccountPayment depositAccountPayment : depositAccountPaymentPage) {

            DepositAccountPaymentJson depositAccountPaymentJson = new DepositAccountPaymentJson();

            depositAccountPaymentJson.setCode(depositAccountPayment.getCode());
            depositAccountPaymentJson.setCustomerName(depositAccountPayment.getCustomerName());
            depositAccountPaymentJson.setAccountNumber(depositAccountPayment.getAccountNumber());
            depositAccountPaymentJson.setInterestPayoutFrequency(depositAccountPayment.getInterestPayoutFrequency());
            depositAccountPaymentJson.setInterestPaymentAmount(depositAccountPayment.getAmount().toString());
            depositAccountPaymentJson.setInterestPeriodString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccountPayment.getDatePeriodFrom()) + " - "
                + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccountPayment.getDatePeriodTo()));
            depositAccountPaymentJson.setIsProcessed(depositAccountPayment.getIsProcessed());
            depositAccountPaymentJson.setDateProcessedString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccountPayment.getDateProcessed()));
            depositAccountPaymentJson.setDateCreatedString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccountPayment.getDateCreated()));

            BankAccount bankAccount = bankAccountRepository.findByCustomerId(depositAccountPayment.getCustomerId());
            if (bankAccount != null) {

                BankDetailsJson bankDetailsJson = new BankDetailsJson();
                bankDetailsJson.setBankAccountName(bankAccount.getBankAccountName());
                bankDetailsJson.setIban(bankAccount.getIban());
                bankDetailsJson.setBic(bankAccount.getBic());
                bankDetailsJson.setName(bankAccount.getBankName());
                bankDetailsJson.setAddress(bankAccount.getBankAddress());

                depositAccountPaymentJson.setBankDetailsJson(bankDetailsJson);
            }

            resultList.add(depositAccountPaymentJson);
        }

        return new PageList<>(resultList, new PageInfo(depositAccountPaymentPage.getTotalPages(), depositAccountPaymentPage.getTotalElements()));
    }

    @Transactional
    public void processDepositInterestPayment(long operatorId, DepositAccountPayment depositAccountPayment) throws CRFException, Exception {

        depositAccountPayment.setOperatorId(operatorId);
        depositAccountPayment.setIsProcessed(true);
        depositAccountPayment.setDateProcessed(new Date());

        depositAccountPaymentRepository.save(depositAccountPayment);

        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountPayment.getDepositAccountId()).orElse(null);
        if (depositAccount == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositAccount is null");

        DepositProduct depositProduct = depositProductService.getDepositProductById(depositAccount.getDepositProductId());
        if (depositProduct == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositProduct is null");

        Customer customer = customerService.getCustomerById(depositAccount.getCustomerId());
        if (customer == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#customer is null");

        File pdfFile = pdfService.generatePdfReport(ServerConstants.PDF_DOC_TYPE_DEPOSIT_INTEREST_PAYMENT, depositProduct, depositAccount, depositAccountPayment);

        if (pdfFile != null) {
            depositAccountDocumentService.saveDepositAccountDocument(depositAccount, ServerConstants.PDF_DOC_TYPE_DEPOSIT_INTEREST_PAYMENT, depositAccountPayment.getId(),
                pdfFile.getAbsolutePath());
        }

        depositStatementService.saveDepositStatement(depositAccount, ServerConstants.DEPOSIT_STATEMENT_TYPE_INTEREST_PAYMENT, depositAccountPayment.getAmount());

        Email email = new Email();
        email.setEmailTo(customer.getEmail());
        email.setCustomerId(customer.getId());

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", customer.getFirstName());
        params.put("loginPageUrl", systemUrl);

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_CUSTOMER_INVESTMENT_PRODUCT_INTEREST_PAID_TEMPLATE_ID, params);

        operatorService.logOperatorActivity(operatorId, OperatorService.ADMIN_ACTIVITY_ID_PROCESS_DEPOSIT_INTEREST_PAYMENT, ServerUtil.toJson(depositAccountPayment));
    }
}
