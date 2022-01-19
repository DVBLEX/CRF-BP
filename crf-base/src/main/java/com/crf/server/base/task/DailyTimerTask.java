package com.crf.server.base.task;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.DepositAccount;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.repository.DepositAccountRepository;
import com.crf.server.base.service.DepositAccountPaymentService;
import com.crf.server.base.service.DepositAccountService;
import com.crf.server.base.service.EmailService;
import com.crf.server.base.service.SystemService;

import lombok.extern.apachecommons.CommonsLog;

@Component
@CommonsLog
public class DailyTimerTask {

    private DepositAccountRepository     depositAccountRepository;
    private DepositAccountService        depositAccountService;
    private DepositAccountPaymentService depositAccountPaymentService;
    private EmailService                 emailService;
    private SystemService                systemService;

    @Autowired
    public void setDepositAccountRepository(DepositAccountRepository depositAccountRepository) {
        this.depositAccountRepository = depositAccountRepository;
    }

    @Autowired
    public void setDepositAccountService(DepositAccountService depositAccountService) {
        this.depositAccountService = depositAccountService;
    }

    @Autowired
    public void setDepositAccountPaymentService(DepositAccountPaymentService depositAccountPaymentService) {
        this.depositAccountPaymentService = depositAccountPaymentService;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Scheduled(cron = "0 10 0 * * *")
    public void run() {

        try {
            log.info("run###");

            systemService.updateSystemTimerTaskDateLastRun(ServerConstants.SYSTEM_TIMER_TASK_DAILY_ID, new Date());

            LocalDate localDateToday = LocalDate.now();
            LocalDate localDateDepositStarted = null;
            LocalDate localDateDeposit6MonthPeriodEnd = null;

            Date dateToday = Date.from(localDateToday.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Date dateDepositExpiry = Date
                .from(localDateToday.minusDays(systemService.getSystemParameter().getInitiatedDepositExpiryDays().longValue()).atStartOfDay(ZoneId.systemDefault()).toInstant());

            // On the first day of every new quarter, check for active deposits with quarterly interest payout frequency and schedule the interest payments for processing
            // Also, do the same for active deposits at their yearly anniversary with yearly interest payout frequency
            List<DepositAccount> activeDepositAccountList = depositAccountRepository.findAllByStatusOrderById(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE);

            if (activeDepositAccountList != null && !activeDepositAccountList.isEmpty()) {

                for (DepositAccount depositAccount : activeDepositAccountList) {

                    switch (depositAccount.getInterestPayoutFrequency()) {

                        case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY:

                            if (ServerUtil.isBeginningOfTheQuarter(localDateToday)) {
                                // schedule interest payment
                                try {
                                    depositAccountPaymentService.saveDepositAccountPayment(depositAccount, localDateToday);

                                } catch (CRFException crfe) {
                                    log.error("depositAccountPaymentService.saveDepositAccountPayment###CRFException###" + crfe);
                                } catch (Exception e) {
                                    log.error("depositAccountPaymentService.saveDepositAccountPayment###Exception###" + e);
                                }
                            }
                            break;

                        case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY:

                            localDateDepositStarted = depositAccount.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                            if (localDateToday.getMonthValue() == localDateDepositStarted.getMonthValue()
                                && localDateToday.getDayOfMonth() == localDateDepositStarted.getDayOfMonth()) {
                                // schedule interest payment
                                try {
                                    depositAccountPaymentService.saveDepositAccountPayment(depositAccount, localDateToday);

                                } catch (CRFException crfe) {
                                    log.error("depositAccountPaymentService.saveDepositAccountPayment###CRFException###" + crfe);
                                } catch (Exception e) {
                                    log.error("depositAccountPaymentService.saveDepositAccountPayment###Exception###" + e);
                                }
                            }
                            break;

                        case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY:

                            if (depositAccount.getDateLastInterestPayment() == null) {
                                localDateDeposit6MonthPeriodEnd = depositAccount.getDateStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(6l);
                            } else {
                                localDateDeposit6MonthPeriodEnd = depositAccount.getDateLastInterestPayment().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                    .plusMonths(6l);
                            }

                            if (localDateToday.getMonthValue() == localDateDeposit6MonthPeriodEnd.getMonthValue()
                                && localDateToday.getDayOfMonth() == localDateDeposit6MonthPeriodEnd.getDayOfMonth()) {
                                // schedule interest payment
                                try {
                                    depositAccountPaymentService.saveDepositAccountPayment(depositAccount, localDateToday);

                                } catch (CRFException crfe) {
                                    log.error("depositAccountPaymentService.saveDepositAccountPayment###CRFException###" + crfe);
                                } catch (Exception e) {
                                    log.error("depositAccountPaymentService.saveDepositAccountPayment###Exception###" + e);
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }
            }

            // check for any matured deposits and mark their status as matured
            List<DepositAccount> maturedDepositAccountList = depositAccountRepository.findActiveDepositsThatHaveMatured(ServerConstants.DEPOSIT_ACCOUNT_STATUS_ACTIVE, dateToday);

            if (maturedDepositAccountList != null && !maturedDepositAccountList.isEmpty()) {

                final StringBuilder maturedDepositIds = new StringBuilder("DailyTimerTask#MaturedDepositIds=[");

                for (DepositAccount depositAccount : maturedDepositAccountList) {

                    try {
                        depositAccountService.updateDepositStatus(depositAccount, ServerConstants.DEPOSIT_ACCOUNT_STATUS_MATURED);

                    } catch (CRFException crfe) {
                        log.error("depositAccountService.updateDepositStatus###CRFException###" + crfe);
                    } catch (Exception e) {
                        log.error("depositAccountService.updateDepositStatus###Exception###" + e);
                    }

                    maturedDepositIds.append(depositAccount.getId() + ", ");
                }

                maturedDepositIds.append("]");

                log.info(maturedDepositIds.toString());
            }

            // check for any expired deposits and mark their status as initiated - expired
            List<DepositAccount> expiredDepositAccountList = depositAccountRepository.findInitiatedDepositsThatHaveExpired(ServerConstants.DEPOSIT_ACCOUNT_STATUS_INITIATED,
                dateDepositExpiry);

            if (expiredDepositAccountList != null && !expiredDepositAccountList.isEmpty()) {

                final StringBuilder expiredDepositIds = new StringBuilder("DailyTimerTask#ExpiredDepositIds=[");

                for (DepositAccount depositAccount : expiredDepositAccountList) {

                    try {
                        depositAccountService.updateDepositStatus(depositAccount, ServerConstants.DEPOSIT_ACCOUNT_STATUS_INITIATED_EXPIRED);

                    } catch (CRFException crfe) {
                        log.error("depositAccountService.updateDepositStatus###CRFException###" + crfe);
                    } catch (Exception e) {
                        log.error("depositAccountService.updateDepositStatus###Exception###" + e);
                    }

                    expiredDepositIds.append(depositAccount.getId() + ", ");
                }

                expiredDepositIds.append("]");

                log.info(expiredDepositIds.toString());
            }

        } catch (DataAccessException dae) {

            log.error("DailyTimerTask##DataAccessException: ", dae);

            emailService.sendSystemEmail("DailyTimerTask DataAccessException", EmailService.EMAIL_TYPE_EXCEPTION, null, null,
                "DailyTimerTask#run###DataAccessException:<br />" + dae.getMessage());

        } catch (Exception e) {

            log.error("DailyTimerTask##Exception: ", e);

            emailService.sendSystemEmail("DailyTimerTask Exception", EmailService.EMAIL_TYPE_EXCEPTION, null, null, "DailyTimerTask#run###Exception:<br />" + e.getMessage());
        }
    }
}
