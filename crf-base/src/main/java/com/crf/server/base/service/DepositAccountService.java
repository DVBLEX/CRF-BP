package com.crf.server.base.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
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
import com.crf.server.base.jsonentity.DepositAccountJson;
import com.crf.server.base.jsonentity.DepositProductJson;
import com.crf.server.base.jsonentity.PageInfo;
import com.crf.server.base.repository.BankAccountRepository;
import com.crf.server.base.repository.DepositAccountPaymentRepository;
import com.crf.server.base.repository.DepositAccountRepository;
import com.crf.server.base.repository.OperatorRepository;

@Service
public class DepositAccountService {

    private BankAccountRepository           bankAccountRepository;
    private DepositAccountRepository        depositAccountRepository;
    private DepositAccountPaymentRepository depositAccountPaymentRepository;
    private OperatorRepository              operatorRepository;

    private CustomerService                 customerService;
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
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
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

    public PageList<DepositAccountJson> getDepositAccountsByCustomerId(Pageable pageable, long customerId) throws CRFException, Exception {

        List<DepositAccountJson> resultList = new ArrayList<>();

        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null)
            throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#customer is null");

        Page<DepositAccount> depositAccountPage = depositAccountRepository.findAllByCustomerId(customerId, pageable);

        for (DepositAccount depositAccount : depositAccountPage) {

            DepositAccountJson depositAccountJson = new DepositAccountJson();

            BeanUtils.copyProperties(depositAccount, depositAccountJson);

            depositAccountJson.setCustomerCode(customer.getCode());
            depositAccountJson.setDepositAmount(depositAccount.getDepositAmount().toString());
            depositAccountJson.setDepositWithdrawalAmount(depositAccount.getDepositWithdrawalAmount().toString());
            depositAccountJson.setInterestEarnedAmount(depositAccount.getInterestEarnedAmount().toString());
            depositAccountJson.setTermYears(depositAccount.getTermYears().toString());
            depositAccountJson.setPrematureWithdrawalInterestRate(depositAccount.getPrematureWithdrawalInterestRate().toString());
            depositAccountJson.setWithdrawalFee(depositAccount.getWithdrawalFee().toString());
            depositAccountJson.setInterestRate(depositAccount.getInterestRate().toString());
            depositAccountJson.setDateOpenString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateOpen()));
            depositAccountJson.setDateStartString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateStart()));
            depositAccountJson.setDateWithdrawalString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateWithdrawApprove()));
            depositAccountJson.setDateCreatedString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateCreated()));

            DepositProduct depositProduct = depositProductService.getDepositProductById(depositAccount.getDepositProductId());
            if (depositProduct == null)
                throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositProduct is null");

            DepositProductJson depositProductJson = new DepositProductJson();

            BeanUtils.copyProperties(depositProduct, depositProductJson);

            depositAccountJson.setDepositProductJson(depositProductJson);

            if (depositAccount.getDateMaturity() == null) {
                depositAccountJson.setDateMaturityString("");
            } else {
                depositAccountJson.setDateMaturityString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateMaturity()));
            }

            resultList.add(depositAccountJson);
        }

        return new PageList<>(resultList, new PageInfo(depositAccountPage.getTotalPages(), depositAccountPage.getTotalElements()));
    }

    public PageList<DepositAccountJson> getDepositAccounts(Pageable pageable, int status, String bankTransferRef) throws CRFException, Exception {

        List<DepositAccountJson> resultList = new ArrayList<>();

        Page<DepositAccount> depositAccountPage = depositAccountRepository.findAllByStatusAndBankTransferReferenceContainsOrderByDateCreatedDesc(status, bankTransferRef, pageable);

        for (DepositAccount depositAccount : depositAccountPage) {

            DepositAccountJson depositAccountJson = new DepositAccountJson();

            BeanUtils.copyProperties(depositAccount, depositAccountJson);

            Customer customer = customerService.getCustomerById(depositAccount.getCustomerId());
            if (customer == null)
                throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#customer is null");

            depositAccountJson.setCustomerCode(customer.getCode());
            depositAccountJson.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
            depositAccountJson.setDepositAmount(depositAccount.getDepositAmount().toString());
            depositAccountJson.setDepositWithdrawalAmount(depositAccount.getDepositWithdrawalAmount().toString());
            depositAccountJson.setInterestEarnedAmount(depositAccount.getInterestEarnedAmount().toString());
            depositAccountJson.setTermYears(depositAccount.getTermYears().toString());
            depositAccountJson.setPrematureWithdrawalInterestRate(depositAccount.getPrematureWithdrawalInterestRate().toString());
            depositAccountJson.setWithdrawalFee(depositAccount.getWithdrawalFee().toString());
            depositAccountJson.setInterestRate(depositAccount.getInterestRate().toString());
            depositAccountJson.setDateOpenString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateOpen()));
            depositAccountJson.setDateStartString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateStart()));
            depositAccountJson.setDateWithdrawalString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateWithdrawApprove()));
            depositAccountJson.setDateCreatedString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateCreated()));

            DepositProduct depositProduct = depositProductService.getDepositProductById(depositAccount.getDepositProductId());
            if (depositProduct == null)
                throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#depositProduct is null");

            DepositProductJson depositProductJson = new DepositProductJson();

            BeanUtils.copyProperties(depositProduct, depositProductJson);

            depositProductJson.setDepositMinAmount(depositProduct.getDepositMinAmount().toString());
            depositProductJson.setDepositMaxAmount(depositProduct.getDepositMaxAmount().toString());

            depositAccountJson.setDepositProductJson(depositProductJson);

            if (depositAccount.getDateMaturity() == null) {
                depositAccountJson.setDateMaturityString("");
            } else {
                depositAccountJson.setDateMaturityString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateMaturity()));
            }

            if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWAL_REQUESTED) {
                // set the bank account details for the current deposit so the admin can verify them during the withdrawal approval step

                BankAccount bankAccount = bankAccountRepository.findByCustomerId(depositAccount.getCustomerId());
                if (bankAccount != null) {

                    BankDetailsJson bankDetailsJson = new BankDetailsJson();
                    bankDetailsJson.setBankAccountName(bankAccount.getBankAccountName());
                    bankDetailsJson.setIban(bankAccount.getIban());
                    bankDetailsJson.setBic(bankAccount.getBic());
                    bankDetailsJson.setName(bankAccount.getBankName());
                    bankDetailsJson.setAddress(bankAccount.getBankAddress());

                    LocalDate localDateEdited = bankAccount.getDateEdited().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    bankDetailsJson.setDaysSinceLastUpdate(ChronoUnit.DAYS.between(localDateEdited, LocalDate.now()));

                    depositAccountJson.setBankDetailsJson(bankDetailsJson);
                }
            }

            resultList.add(depositAccountJson);
        }

        return new PageList<>(resultList, new PageInfo(depositAccountPage.getTotalPages(), depositAccountPage.getTotalElements()));
    }

    public DepositAccount getDepositAccountByCode(String code) throws Exception {

        return depositAccountRepository.findByCode(code);
    }

    @Transactional
    public void saveDeposit(long operatorId, DepositAccountJson depositAccountJson, DepositProduct depositProduct, Customer customer) throws CRFException, Exception {

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setCode(SecurityUtil.generateUniqueCode());
        depositAccount.setCustomerId(customer.getId());
        depositAccount.setDepositProductId(depositProduct.getId());
        depositAccount.setDepositAmount(new BigDecimal(depositAccountJson.getDepositAmount()));
        depositAccount.setAccountNumber(generate8DigitAccountNumber());
        depositAccount.setInterestPayoutFrequency(depositAccountJson.getInterestPayoutFrequency());
        depositAccount.setInterestRate(new BigDecimal(depositAccountJson.getInterestRate()));
        depositAccount.setTermYears(new BigDecimal(depositAccountJson.getTermYears()));
        depositAccount.setStatus(ServerConstants.DEPOSIT_ACCOUNT_STATUS_INITIATED);
        depositAccount.setBankTransferReference(depositAccountJson.getBankTransferReference());
        depositAccount.setPrematureWithdrawalMinDays(depositProduct.getPrematureWithdrawalMinDays());
        depositAccount.setPrematureWithdrawalInterestRate(depositProduct.getPrematureWithdrawalInterestRate());
        depositAccount.setWithdrawalFee(depositProduct.getWithdrawalFee());
        depositAccount.setDepositWithdrawalAmount(BigDecimal.ZERO);
        depositAccount.setInterestEarnedAmount(BigDecimal.ZERO);
        depositAccount.setDateOpen(new Date());
        depositAccount.setDateCreated(depositAccount.getDateOpen());

        depositAccountRepository.save(depositAccount);

        File pdfFile = pdfService.generatePdfReport(ServerConstants.PDF_DOC_TYPE_DEPOSIT_INITIATED, depositProduct, depositAccount, null);

        if (pdfFile != null) {
            depositAccountDocumentService.saveDepositAccountDocument(depositAccount, ServerConstants.PDF_DOC_TYPE_DEPOSIT_INITIATED, ServerConstants.DEFAULT_LONG,
                pdfFile.getAbsolutePath());
        }

        Email email = new Email();
        email.setEmailTo(customer.getEmail());
        email.setCustomerId(customer.getId());

        LocalDate localDateOpen = depositAccount.getDateOpen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Date potentialDateMaturity = Date.from(localDateOpen.plusYears(depositAccount.getTermYears().intValue()).atStartOfDay(ZoneId.systemDefault()).toInstant());

        StringBuilder productDetailsSB = new StringBuilder();
        productDetailsSB.append("<table  style=\"font-family:Helvetica;font-size:16px;color:#545457;line-height:22px;margin-bottom:1.3em\">");
        productDetailsSB.append("<tr><td>Product</td><td>" + depositProduct.getName() + "</td></tr>");
        productDetailsSB.append("<tr><td>Interest rate</td><td>" + depositProduct.getYearlyInterestRate() + "%</td></tr>");
        productDetailsSB.append("<tr><td>Term (years)</td><td>" + depositAccount.getTermYears() + "</td></tr>");
        productDetailsSB.append("<tr><td>Start Date*</td><td>" + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateOpen()) + "</td></tr>");
        productDetailsSB.append("<tr><td>Maturity Date**</td><td>" + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, potentialDateMaturity) + "</td></tr>");
        productDetailsSB.append("</table>");

        StringBuilder bankDetailsSB = new StringBuilder();
        bankDetailsSB.append("<table  style=\"font-family:Helvetica;font-size:16px;color:#545457;line-height:22px;margin-bottom:1.3em\">");
        bankDetailsSB.append("<tr><td>Bank Transfer Reference</td><td>" + depositAccount.getBankTransferReference() + "</td></tr>");
        bankDetailsSB.append("<tr><td>Account</td><td>" + ServerConstants.BANK_ACCOUNT_NAME + "</td></tr>");
        bankDetailsSB.append("<tr><td>IBAN</td><td>" + ServerConstants.BANK_IBAN + "</td></tr>");
        bankDetailsSB.append("<tr><td>BIC</td><td>" + ServerConstants.BANK_BIC + "</td></tr>");
        bankDetailsSB.append("<tr><td>Bank Name</td><td>" + ServerConstants.BANK_NAME + "</td></tr>");
        bankDetailsSB.append("<tr><td>Bank Address</td><td>" + ServerConstants.BANK_ADDRESS + "</td></tr>");
        bankDetailsSB.append("</table>");

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", customer.getFirstName());
        params.put("productDetailsTable", productDetailsSB.toString());
        params.put("bankDetailsTable", bankDetailsSB.toString());
        params.put("loginPageUrl", systemUrl);

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_CUSTOMER_INVESTMENT_PRODUCT_SELECTED_TEMPLATE_ID, params);

        operatorService.logOperatorActivity(operatorId, OperatorService.INVESTOR_ACTIVITY_ID_INITIATE_DEPOSIT, ServerUtil.toJson(depositAccount));
    }

    @Transactional
    public void approveDeposit(long operatorId, DepositAccount depositAccount, Customer customer) throws CRFException, Exception {

        LocalDate localDateStart = depositAccount.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date dateMaturity = Date.from(localDateStart.plusYears(depositAccount.getTermYears().longValue()).atStartOfDay(ZoneId.systemDefault()).toInstant());

        depositAccount.setStatus(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE);
        depositAccount.setDateMaturity(dateMaturity);

        depositAccountRepository.save(depositAccount);

        depositStatementService.saveDepositStatement(depositAccount, ServerConstants.DEPOSIT_STATEMENT_TYPE_DEPOSIT_APPROVED, BigDecimal.ZERO);

        Email email = new Email();
        email.setEmailTo(customer.getEmail());
        email.setCustomerId(customer.getId());

        StringBuilder productDetailsSB = new StringBuilder();
        productDetailsSB.append("<table  style=\"font-family:Helvetica;font-size:16px;color:#545457;line-height:22px;margin-bottom:1.3em\">");
        productDetailsSB.append("<tr><td>Deposit Number</td><td>" + depositAccount.getAccountNumber() + "</td></tr>");
        productDetailsSB.append("<tr><td>Interest rate</td><td>" + depositAccount.getInterestRate() + "%</td></tr>");
        productDetailsSB.append("<tr><td>Term (years)</td><td>" + depositAccount.getTermYears() + "</td></tr>");
        productDetailsSB.append("<tr><td>Start Date</td><td>" + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateStart()) + "</td></tr>");
        productDetailsSB.append("<tr><td>Maturity Date</td><td>" + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateMaturity()) + "</td></tr>");
        productDetailsSB.append("</table>");

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", customer.getFirstName());
        params.put("productDetailsTable", productDetailsSB.toString());
        params.put("loginPageUrl", systemUrl);

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_CUSTOMER_INVESTMENT_PRODUCT_APPROVED_TEMPLATE_ID, params);

        operatorService.logOperatorActivity(operatorId, OperatorService.ADMIN_ACTIVITY_ID_APPROVE_DEPOSIT, ServerUtil.toJson(depositAccount));
    }

    public BigDecimal calculateDepositInterestEarnedSoFar(DepositAccount depositAccount, LocalDate localDateToday, boolean isWithdrawalRequest) {

        BigDecimal accruedInterestEarned = BigDecimal.ZERO;
        BigDecimal totalInterestEarned = BigDecimal.ZERO;
        BigDecimal termYearFraction = BigDecimal.ZERO;
        BigDecimal daysInCurrentPeriodAsPercentageOfCurrentYear = BigDecimal.ZERO;

        BigDecimal daysInCurrentPeriod = BigDecimal.ZERO;
        LocalDate localDateYesterday = localDateToday.minusDays(1l);
        LocalDate localDateStart = depositAccount.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDateMaturity = depositAccount.getDateMaturity().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        long totalTermDays = ChronoUnit.DAYS.between(localDateStart, localDateMaturity);
        long elapsedTermDays = ChronoUnit.DAYS.between(localDateStart, localDateToday);
        long daysSinceLastInterestPayment = 0l;

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

        if (elapsedTermDays <= depositAccount.getPrematureWithdrawalMinDays()
            && (isWithdrawalRequest || depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWAL_REQUESTED))
            return BigDecimal.ZERO;

        else if (elapsedTermDays <= totalTermDays) {

            if (elapsedTermDays < totalTermDays && (isWithdrawalRequest || depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWAL_REQUESTED)) {
                // if investor requests (or already requested) a deposit withdrawal before the maturity date, apply a lower interest rate on the elapsed days of the term
                termYearFraction = new BigDecimal(elapsedTermDays + ".0000").divide(new BigDecimal(daysInCurrentYear + ".0000"), RoundingMode.HALF_UP).setScale(4,
                    RoundingMode.HALF_UP);

                totalInterestEarned = depositProductService.calculateDepositTotalInterest(depositAccount.getDepositAmount(), depositAccount.getPrematureWithdrawalInterestRate(),
                    termYearFraction);

            } else {
                // apply any additional interest for the elapsed days in the current year / half year or quarter
                // find the last interest payment date, if any
                if (depositAccount.getDateLastInterestPayment() == null) {
                    daysSinceLastInterestPayment = elapsedTermDays;

                } else {
                    LocalDate localDateLastInterestPayment = depositAccount.getDateLastInterestPayment().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    daysSinceLastInterestPayment = ChronoUnit.DAYS.between(localDateLastInterestPayment, localDateToday);
                }

                if (daysSinceLastInterestPayment == 0) {
                    // no accrued interest to worry about in that case
                    totalInterestEarned = depositAccount.getInterestEarnedAmount();

                } else {

                    switch (depositAccount.getInterestPayoutFrequency()) {

                        case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY:

                            int daysInCurrentQuarter = ServerUtil.getNumberOfDaysInTheQuarter(localDateToday.get(IsoFields.QUARTER_OF_YEAR), yearMonthObject.isLeapYear());

                            if (ServerUtil.isBeginningOfTheQuarter(localDateToday)) {
                                // we are at the beginning of the new quarter so get the number of days in the previous quarter
                                daysInCurrentQuarter = ServerUtil.getNumberOfDaysInTheQuarter(localDateYesterday.get(IsoFields.QUARTER_OF_YEAR), yearMonthObject.isLeapYear());
                            }

                            daysInCurrentPeriod = new BigDecimal(daysInCurrentQuarter + ".0000");

                            daysInCurrentPeriodAsPercentageOfCurrentYear = daysInCurrentPeriod.divide(new BigDecimal(daysInCurrentYear + ".0000"), RoundingMode.HALF_UP).setScale(4,
                                RoundingMode.HALF_UP);

                            termYearFraction = (new BigDecimal(daysSinceLastInterestPayment + ".0000").divide(daysInCurrentPeriod, RoundingMode.HALF_UP))
                                .multiply(daysInCurrentPeriodAsPercentageOfCurrentYear).setScale(4, RoundingMode.HALF_UP);

                            break;

                        case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY:

                            daysInCurrentPeriod = new BigDecimal(daysInCurrentYear + ".0000");

                            termYearFraction = new BigDecimal(daysSinceLastInterestPayment + ".0000").divide(daysInCurrentPeriod, RoundingMode.HALF_UP).setScale(4,
                                RoundingMode.HALF_UP);

                            break;

                        case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY:

                            long daysInCurrentHalfYearPeriod;

                            if (depositAccount.getDateLastInterestPayment() == null) {

                                daysInCurrentHalfYearPeriod = ChronoUnit.DAYS.between(localDateStart, localDateStart.plusMonths(6l));

                            } else {
                                LocalDate localDateLastInterestPayment = depositAccount.getDateLastInterestPayment().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                                daysInCurrentHalfYearPeriod = ChronoUnit.DAYS.between(localDateLastInterestPayment, localDateLastInterestPayment.plusMonths(6l));
                            }

                            daysInCurrentPeriod = new BigDecimal(daysInCurrentHalfYearPeriod + ".0000");

                            daysInCurrentPeriodAsPercentageOfCurrentYear = daysInCurrentPeriod.divide(new BigDecimal(daysInCurrentYear + ".0000"), RoundingMode.HALF_UP).setScale(4,
                                RoundingMode.HALF_UP);

                            termYearFraction = (new BigDecimal(daysSinceLastInterestPayment + ".0000").divide(daysInCurrentPeriod, RoundingMode.HALF_UP))
                                .multiply(daysInCurrentPeriodAsPercentageOfCurrentYear).setScale(4, RoundingMode.HALF_UP);

                            break;

                        default:
                            return BigDecimal.ZERO;
                    }

                    accruedInterestEarned = depositProductService.calculateDepositTotalInterest(depositAccount.getDepositAmount(), depositAccount.getInterestRate(),
                        termYearFraction);

                    totalInterestEarned = depositAccount.getInterestEarnedAmount();
                    totalInterestEarned = totalInterestEarned.add(accruedInterestEarned);
                }
            }

        } else {
            // the deposit has matured. no accrued interest to worry about in that case
            totalInterestEarned = depositAccount.getInterestEarnedAmount();
        }

        return totalInterestEarned;
    }

    @Transactional
    public void requestWithdrawal(long operatorId, DepositAccount depositAccount, LocalDate localDateRequest) throws CRFException, Exception {

        BigDecimal totalInterest = BigDecimal.ZERO;

        if (depositAccount.getStatus() == ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED) {
            totalInterest = depositAccount.getInterestEarnedAmount();

        } else {
            // premature withdrawal, so calculate the total interest earned again applying lower interest rate
            totalInterest = calculateDepositInterestEarnedSoFar(depositAccount, localDateRequest, true);

            if (totalInterest.compareTo(BigDecimal.ZERO) > 0) {

                depositStatementService.saveDepositStatement(depositAccount, ServerConstants.DEPOSIT_STATEMENT_TYPE_INTEREST_OFFSET_DUE_TO_EARLY_WITHDRAWAL,
                    depositAccount.getInterestEarnedAmount());

                depositStatementService.saveDepositStatement(depositAccount, ServerConstants.DEPOSIT_STATEMENT_TYPE_INTEREST_EARNED_DUE_TO_EARLY_WITHDRAWAL, totalInterest);
            }
        }

        BigDecimal accruedInterest = totalInterest.subtract(depositAccount.getInterestEarnedAmount());

        BigDecimal withdrawalAmount = depositAccount.getDepositAmount().add(accruedInterest);
        withdrawalAmount = withdrawalAmount.subtract(depositAccount.getWithdrawalFee()); // apply the withdrawal fee

        if (depositAccount.getWithdrawalFee().compareTo(BigDecimal.ZERO) > 0) {
            depositStatementService.saveDepositStatement(depositAccount, ServerConstants.DEPOSIT_STATEMENT_TYPE_WITHDRAWAL_FEE, BigDecimal.ZERO);
        }

        depositAccount.setStatus(ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWAL_REQUESTED);
        depositAccount.setDepositWithdrawalAmount(withdrawalAmount);
        depositAccount.setInterestEarnedAmount(totalInterest);
        depositAccount.setDateWithdrawRequest(new Date());

        depositAccountRepository.save(depositAccount);

        operatorService.logOperatorActivity(operatorId, OperatorService.INVESTOR_ACTIVITY_ID_REQUEST_DEPOSIT_WITHDRAWAL, ServerUtil.toJson(depositAccount));
    }

    @Transactional
    public void approveDepositWithdrawal(long operatorId, DepositAccount depositAccount, Customer customer) throws CRFException, Exception {

        depositAccount.setStatus(ServerConstants.DEPOSIT_ACCOUNT_STATUS_WITHDRAWN);
        depositAccount.setDateWithdrawApprove(new Date());

        depositAccountRepository.save(depositAccount);

        depositStatementService.saveDepositStatement(depositAccount, ServerConstants.DEPOSIT_STATEMENT_TYPE_WITHDRAWAL, BigDecimal.ZERO);

        Email email = new Email();
        email.setEmailTo(customer.getEmail());
        email.setCustomerId(customer.getId());

        StringBuilder productDetailsSB = new StringBuilder();
        productDetailsSB.append("<table  style=\"font-family:Helvetica;font-size:16px;color:#545457;line-height:22px;margin-bottom:1.3em\">");
        productDetailsSB.append("<tr><td>Deposit Number</td><td>" + depositAccount.getAccountNumber() + "</td></tr>");
        productDetailsSB.append("<tr><td>Interest rate</td><td>" + depositAccount.getInterestRate() + "%</td></tr>");
        productDetailsSB.append("<tr><td>Term (years)</td><td>" + depositAccount.getTermYears() + "</td></tr>");
        productDetailsSB.append("<tr><td>Start Date</td><td>" + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateStart()) + "</td></tr>");
        productDetailsSB.append(
            "<tr><td>Withdrawal Requested Date</td><td>" + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateWithdrawRequest()) + "</td></tr>");
        productDetailsSB.append(
            "<tr><td>Withdrawal Approved Date</td><td>" + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateWithdrawApprove()) + "</td></tr>");
        productDetailsSB.append("<tr><td>Maturity Date</td><td>" + ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, depositAccount.getDateMaturity()) + "</td></tr>");
        productDetailsSB.append("</table>");

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", customer.getFirstName());
        params.put("productDetailsTable", productDetailsSB.toString());
        params.put("loginPageUrl", systemUrl);

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_CUSTOMER_INVESTMENT_PRODUCT_WITHDRAWAL_APPROVED_TEMPLATE_ID, params);

        operatorService.logOperatorActivity(operatorId, OperatorService.ADMIN_ACTIVITY_ID_APPROVE_DEPOSIT_WITHDRAWAL, ServerUtil.toJson(depositAccount));
    }

    public void updateDepositStatus(DepositAccount depositAccount, int status) throws CRFException, Exception {

        depositAccount.setStatus(status);

        if (status == ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED) {

            // in case interest is paid on a quarterly basis, apply any additional interest for the days the deposit has been active for in the current quarter
            if (depositAccount.getInterestPayoutFrequency() == ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY) {

                BigDecimal totalInterest = BigDecimal.ZERO; // interest already paid out + accrued (not paid out yet). the total interest earned
                totalInterest = calculateDepositInterestEarnedSoFar(depositAccount, LocalDate.now(), false);

                if (depositAccount.getInterestEarnedAmount().compareTo(totalInterest) < 0) {

                    Customer customer = customerService.getCustomerById(depositAccount.getCustomerId());
                    if (customer == null)
                        throw new CRFException(ServerResponseConstants.FAILURE_CODE, ServerResponseConstants.FAILURE_TEXT, "#customer is null");

                    // schedule interest payment
                    DepositAccountPayment depositAccountPayment = new DepositAccountPayment();
                    depositAccountPayment.setCode(SecurityUtil.generateUniqueCode());
                    depositAccountPayment.setDepositAccountId(depositAccount.getId());
                    depositAccountPayment.setAccountNumber(depositAccount.getAccountNumber());
                    depositAccountPayment.setInterestPayoutFrequency(depositAccount.getInterestPayoutFrequency());
                    depositAccountPayment.setCustomerId(depositAccount.getCustomerId());
                    depositAccountPayment.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
                    depositAccountPayment.setAmount(totalInterest.subtract(depositAccount.getInterestEarnedAmount()));
                    depositAccountPayment.setOperatorId(ServerConstants.DEFAULT_LONG);
                    depositAccountPayment.setIsProcessed(false);
                    depositAccountPayment.setDateCreated(new Date());

                    depositAccountPaymentRepository.save(depositAccountPayment);

                    depositAccount.setInterestEarnedAmount(totalInterest);
                    depositAccount.setDateLastInterestPayment(new Date());
                }
            }

            // notify the investor that the deposit has matured and a withdrawal can be requested
            emailInvestorAfterDepositStatusUpdate(depositAccount.getCustomerId(), ServerConstants.EMAIL_CUSTOMER_INVESTMENT_PRODUCT_MATURED_TEMPLATE_ID);

        } else if (status == ServerConstants.DEPOSIT_ACCOUNT_STATUS_INITIATED_EXPIRED) {
            // notify the investor that the deposit they have initiated has expired. It is expired if it is not activated within x number of days
            emailInvestorAfterDepositStatusUpdate(depositAccount.getCustomerId(), ServerConstants.EMAIL_CUSTOMER_INVESTMENT_PRODUCT_EXPIRED_TEMPLATE_ID);
        }

        depositAccountRepository.save(depositAccount);
    }

    private void emailInvestorAfterDepositStatusUpdate(long customerId, long emailTemplateId) throws Exception {

        Customer customer = customerService.getCustomerById(customerId);

        Email email = new Email();
        email.setEmailTo(customer.getEmail());
        email.setCustomerId(customer.getId());

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", customer.getFirstName());
        params.put("loginPageUrl", systemUrl);

        emailService.scheduleEmailByType(email, emailTemplateId, params);

    }

    public String generateBankTransferReference() throws Exception {

        String bankTransferReference = "";
        DepositAccount depositAccount = null;

        do {
            bankTransferReference = SecurityUtil.generateSecureRandom16CharReference();

            depositAccount = depositAccountRepository.findByBankTransferReference(bankTransferReference);

        } while (depositAccount != null);

        return bankTransferReference;
    }

    private String generate8DigitAccountNumber() throws Exception {

        String accountNumber = "";
        DepositAccount depositAccount = null;

        do {
            String numbers = RandomStringUtils.randomNumeric(8);
            List<Character> refChars = numbers.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
            Collections.shuffle(refChars);

            accountNumber = refChars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();

            depositAccount = depositAccountRepository.findByAccountNumber(accountNumber);

        } while (depositAccount != null);

        return accountNumber;
    }

    public DepositAccount getDepositAccountByBankTransferReference(DepositAccountJson depositAccountJson) {
        return depositAccountRepository.findByBankTransferReference(depositAccountJson.getBankTransferReference());
    }
}
