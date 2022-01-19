package com.crf.server.base.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.Email;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.EmailService;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class EmailTaskExecutor implements Runnable {

    private CustomerService customerService;

    private EmailService    emailService;

    private Email           email;

    public EmailTaskExecutor(Email email, EmailService emailService, CustomerService customerService) {
        this.email = email;
        this.emailService = emailService;
        this.customerService = customerService;
    }

    @Override
    public void run() {

        try {
            email.setEmailTo(email.getEmailTo().replaceAll(" ", ""));
            if (emailService.getIsLive() || email.getCustomerId() == ServerConstants.DEFAULT_LONG) {
                emailService.sendEmail(email);
            } else {
                Customer customer = customerService.getCustomerById(email.getCustomerId());

                String[] emailsTo = email.getEmailTo().split(",");
                List<String> emailsSend = new ArrayList<>();
                List<String> emailsNotSend = new ArrayList<>();

                for (String emailTo : emailsTo) {
                    if (customer != null) {
                        emailsSend.add(emailTo);
                    } else {
                        emailsNotSend.add(emailTo);
                    }
                }

                if (!emailsSend.isEmpty()) {
                    email.setEmailTo(String.join(",", emailsSend));
                    emailService.sendEmail(email);

                } else if (!emailsNotSend.isEmpty()) {
                    email.setDateProcessed(new Date());
                    email.setResponseCode(ServerResponseConstants.SUCCESS_TEST_CODE);
                    email.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
                }

            }

            email.setIsProcessed(ServerConstants.PROCESS_PROCESSED);
            emailService.updateEmail(email);
            emailService.deleteScheduledEmail(email.getId());

        } catch (CRFException crfe) {

            email.setRetryCount(email.getRetryCount() + 1);
            email.setResponseCode(crfe.getResponseCode());
            email.setResponseText(crfe.getResponseText());

            if (ServerResponseConstants.INVALID_EMAIL_CODE == email.getResponseCode()) {
                emailService.updateEmail(email);
                emailService.deleteScheduledEmail(email.getId());
            } else {
                if (email.getRetryCount() == 1) {
                    email.setIsProcessed(ServerConstants.PROCESS_NOTPROCESSED);
                    email.setDateScheduled(new Date(email.getDateScheduled().getTime() + 60l * 1000l));
                    emailService.updateScheduledEmail(email);
                } else if (email.getRetryCount() < 7) {
                    email.setIsProcessed(ServerConstants.PROCESS_NOTPROCESSED);
                    email.setDateScheduled(new Date(email.getDateScheduled().getTime() + 10l * 60l * 1000l));
                    emailService.updateScheduledEmail(email);
                } else {
                    emailService.updateScheduledEmail(email);
                    // emailService.updateEmail(email);
                    // emailService.deleteScheduledEmail(email.getId());
                }
            }

        } catch (Exception e) {

            log.error("EmailTaskExecutor##Exception: ", e);
        }
    }
}
