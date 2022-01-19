package com.crf.server.base.task;

import java.util.Date;
import java.util.List;

import com.crf.server.base.entity.SmsScheduler;
import com.crf.server.base.repository.SmsSchedulerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.entity.Sms;
import com.crf.server.base.service.EmailService;
import com.crf.server.base.service.SMSService;
import com.crf.server.base.service.SystemService;

import lombok.extern.apachecommons.CommonsLog;

@Component
@CommonsLog
public class SmsTimerTask {

    private long                   dateLastRunMillis = (System.currentTimeMillis() - ServerConstants.NINE_MINUTES_MILLIS);

    private SmsSchedulerRepository smsSchedulerRepository;
    private EmailService           emailService;
    private SMSService             smsService;
    private SystemService          systemService;
    private TaskExecutor           smsTaskExecutor;

    @Autowired
    public void setSmsSchedulerRepository(SmsSchedulerRepository smsSchedulerRepository) {
        this.smsSchedulerRepository = smsSchedulerRepository;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setSmsService(SMSService smsService) {
        this.smsService = smsService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Autowired
    public void setSmsTaskExecutor(TaskExecutor smsTaskExecutor) {
        this.smsTaskExecutor = smsTaskExecutor;
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 10000)
    public void run() {

        try {
            // log.info("run#");
            if ((System.currentTimeMillis() - dateLastRunMillis) >= ServerConstants.NINE_MINUTES_MILLIS) {
                dateLastRunMillis = System.currentTimeMillis();
                systemService.updateSystemTimerTaskDateLastRun(ServerConstants.SYSTEM_TIMER_TASK_SMS_ID, new Date(dateLastRunMillis));
            }

            List<SmsScheduler> smsSchedulerList = smsSchedulerRepository.findByIsProcessedTillNow(ServerConstants.PROCESS_NOTPROCESSED);
            smsSchedulerList.forEach(this::processRow);
        } catch (Exception e) {
            log.error("run###Exception: ", e);
            emailService.sendSystemEmail("Sms TimerTask Error", EmailService.EMAIL_TYPE_EXCEPTION, null, null, "SmsTimerTask#run###Exception:<br />" + e.getMessage());
        }
    }

    private void processRow(SmsScheduler smsScheduler) {
        if (ServerConstants.SCHEDULER_ID == smsScheduler.getId()) {
            smsSchedulerRepository.updateRetryCount(smsScheduler.getId());
            return;
        }

        smsSchedulerRepository.updateIsProcessed(smsScheduler.getId(), ServerConstants.PROCESS_PROGRESS);

        try {

            Sms sms = new Sms();
            sms.setId(smsScheduler.getId());
            sms.setType(smsScheduler.getType());
            sms.setConfigId(smsScheduler.getConfigId());
            sms.setCustomerId(smsScheduler.getCustomerId());
            sms.setTemplateId(smsScheduler.getTemplateId());
            sms.setMsisdn(smsScheduler.getMsisdn());
            sms.setSourceAddr(smsScheduler.getSourceAddr());
            sms.setMessage(smsScheduler.getMessage());
            sms.setDateScheduled(smsScheduler.getDateScheduled());
            sms.setRetryCount(smsScheduler.getRetryCount());
            sms.setIsProcessed(ServerConstants.PROCESS_PROGRESS);

            smsTaskExecutor.execute(new SmsTaskExecutor(sms, smsService));

        } catch (TaskRejectedException tre) {
            log.error("run#smsId=" + smsScheduler.getId() + "###TaskRejectedException: ", tre);

            smsSchedulerRepository.updateIsProcessed(smsScheduler.getId(), ServerConstants.PROCESS_NOTPROCESSED);
        }
    }
}
