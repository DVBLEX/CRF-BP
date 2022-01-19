package com.crf.server.base.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.EmailScheduler;
import com.crf.server.base.repository.EmailSchedulerRepository;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.EmailService;
import com.crf.server.base.service.SystemService;

import lombok.extern.apachecommons.CommonsLog;

@Component
@CommonsLog
public class EmailTimerTask {

    private long                     dateLastRunMillis = (System.currentTimeMillis() - ServerConstants.NINE_MINUTES_MILLIS);

    private EmailSchedulerRepository emailSchedulerRepository;
    private CustomerService          customerService;
    private EmailService             emailService;
    private SystemService            systemService;
    private TaskExecutor             emailTaskExecutor;

    @Autowired
    public void setEmailSchedulerRepository(EmailSchedulerRepository emailSchedulerRepository) {
        this.emailSchedulerRepository = emailSchedulerRepository;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Autowired
    public void setEmailTaskExecutor(TaskExecutor emailTaskExecutor) {
        this.emailTaskExecutor = emailTaskExecutor;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 10000)
    public void run() {

        try {
            // log.info("run#");
            if ((System.currentTimeMillis() - dateLastRunMillis) >= ServerConstants.NINE_MINUTES_MILLIS) {
                dateLastRunMillis = System.currentTimeMillis();
                systemService.updateSystemTimerTaskDateLastRun(ServerConstants.SYSTEM_TIMER_TASK_EMAIL_ID, new Date(dateLastRunMillis));
            }

            List<EmailScheduler> emailSchedulerList = emailSchedulerRepository.findByIsProcessedTillNow(ServerConstants.PROCESS_NOTPROCESSED);
            emailSchedulerList.forEach(this::processRow);

        } catch (Exception e) {
            log.error("run###Exception: ", e);
            emailService.sendSystemEmail("Email TimerTask Error", EmailService.EMAIL_TYPE_EXCEPTION, null, null, "EmailTimerTask#run###Exception:<br />" + e.getMessage());
        }
    }

    private void processRow(EmailScheduler emailScheduler) {
        if (ServerConstants.SCHEDULER_ID == emailScheduler.getId()) {
            emailSchedulerRepository.updateRetryCount(emailScheduler.getId());
            return;
        }

        emailSchedulerRepository.updateIsProcessed(emailScheduler.getId(), ServerConstants.PROCESS_PROGRESS);

        try {
            Email email = new Email();

            email.setId(emailScheduler.getId());
            email.setType(emailScheduler.getType());
            email.setConfigId(emailScheduler.getConfigId());
            email.setCustomerId(emailScheduler.getCustomerId());
            email.setTemplateId(emailScheduler.getTemplateId());
            email.setEmailTo(emailScheduler.getEmailTo());
            email.setEmailReplyTo(emailScheduler.getEmailReplyTo());
            email.setEmailBcc(emailScheduler.getEmailBcc());
            email.setSubject(emailScheduler.getSubject());
            email.setMessage(emailScheduler.getMessage());
            email.setAttachmentPath(emailScheduler.getAttachmentPath());
            email.setDateScheduled(emailScheduler.getDateScheduled());
            email.setRetryCount(emailScheduler.getRetryCount());
            email.setIsProcessed(ServerConstants.PROCESS_PROGRESS);

            emailTaskExecutor.execute(new EmailTaskExecutor(email, emailService, customerService));

        } catch (TaskRejectedException tre) {
            log.error("run#EmailId=" + emailScheduler.getId() + "###TaskRejectedException: ", tre);

            emailSchedulerRepository.updateIsProcessed(emailScheduler.getId(), ServerConstants.PROCESS_NOTPROCESSED);
        }
    }
}
