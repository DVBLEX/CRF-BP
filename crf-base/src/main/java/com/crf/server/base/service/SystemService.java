package com.crf.server.base.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crf.server.base.entity.CustomerVerificationDenial;
import com.crf.server.base.entity.SystemParameter;
import com.crf.server.base.entity.VerificationDenialReason;
import com.crf.server.base.jsonentity.CustomerVerificationDenialJson;
import com.crf.server.base.jsonentity.VerificationDenialReasonJson;
import com.crf.server.base.repository.BlockedEmailDomainRepository;
import com.crf.server.base.repository.CustomerVerificationDenialRepository;
import com.crf.server.base.repository.EmailWhitelistRepository;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.repository.SystemParameterRepository;
import com.crf.server.base.repository.SystemTimerTaskRepository;
import com.crf.server.base.repository.VerificationDenialReasonRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class SystemService {

    private BlockedEmailDomainRepository         blockedEmailDomainRepository;
    private CustomerVerificationDenialRepository customerVerificationDenialRepository;
    private EmailWhitelistRepository             emailWhitelistRepository;
    private SystemParameterRepository            systemParameterRepository;
    private OperatorRepository                   operatorRepository;
    private SystemTimerTaskRepository            systemTimerTaskRepository;
    private VerificationDenialReasonRepository   verificationDenialReasonRepository;

    private SystemParameter                      systemParameter;

    @Value("${tc.system.shortname}")
    private String                               systemShortname;

    @Value("${tc.system.environment}")
    private String                               systemEnvironment;

    @Autowired
    public void setBlockedEmailDomainRepository(BlockedEmailDomainRepository blockedEmailDomainRepository) {
        this.blockedEmailDomainRepository = blockedEmailDomainRepository;
    }

    @Autowired
    public void setCustomerVerificationDenialRepository(CustomerVerificationDenialRepository customerVerificationDenialRepository) {
        this.customerVerificationDenialRepository = customerVerificationDenialRepository;
    }

    @Autowired
    public void setEmailWhitelistRepository(EmailWhitelistRepository emailWhitelistRepository) {
        this.emailWhitelistRepository = emailWhitelistRepository;
    }

    @Autowired
    public void setSystemParameterRepository(SystemParameterRepository systemParameterRepository) {
        this.systemParameterRepository = systemParameterRepository;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setSystemTimerTaskRepository(SystemTimerTaskRepository systemTimerTaskRepository) {
        this.systemTimerTaskRepository = systemTimerTaskRepository;
    }

    @Autowired
    public void setVerificationDenialReasonRepository(VerificationDenialReasonRepository verificationDenialReasonRepository) {
        this.verificationDenialReasonRepository = verificationDenialReasonRepository;
    }

    @PostConstruct
    public void init() {

        log.info(systemShortname + "###SYSTEM_ENVIRONMENT=" + systemEnvironment);

        systemParameter = systemParameterRepository.findById(1l).orElse(null);

        log.info("init###" + systemParameter.toString());
    }

    public SystemParameter getSystemParameter() {
        return systemParameter;
    }

    public boolean isEmailBlockedDomain(String email) throws Exception {
        if (isEmailWhitelisted(email))
            return false;

        return blockedEmailDomainRepository.countEmailBlockedDomain(email) > 0L;
    }

    public boolean isEmailWhitelisted(String email) {

        return emailWhitelistRepository.countWhitelistByEmail(email) > 0L;
    }

    public void whitelistEmail(String email) throws Exception {

        emailWhitelistRepository.whitelistEmail(email);
    }

    public boolean isCountEmailForgotPasswordUnderLimit(String email, int passwordForgotEmailLimit) throws Exception {
        return operatorRepository.countEmailForgotPasswordUnderLimit(email, passwordForgotEmailLimit) == 0L;
    }

    public boolean isEmailRegisteredAlready(String email) throws Exception {
        return operatorRepository.countEmailRegisteredAlready(email) > 0L;
    }

    public boolean isMsisdnRegisteredAlready(String msisdn) {
        return operatorRepository.countMsisdnRegisteredAlready(msisdn) > 0L;
    }

    public void updateSystemTimerTaskDateLastRun(long timerTaskId, Date dateLastRun) {

        systemTimerTaskRepository.updateDateLastRun(dateLastRun, timerTaskId);
    }

    public List<VerificationDenialReasonJson> listVerificationDenialReasons() throws Exception {

        List<VerificationDenialReason> denialReasonsList = (List<VerificationDenialReason>) verificationDenialReasonRepository.findAll();

        List<VerificationDenialReasonJson> resultList = new ArrayList<>();

        for (VerificationDenialReason verificationDenialReason : denialReasonsList) {

            VerificationDenialReasonJson verificationDenialReasonJson = new VerificationDenialReasonJson();

            BeanUtils.copyProperties(verificationDenialReason, verificationDenialReasonJson);

            resultList.add(verificationDenialReasonJson);
        }

        return resultList;
    }

    public List<CustomerVerificationDenialJson> listCustomerVerificationDenials(long customerId) throws Exception {

        List<CustomerVerificationDenial> customerVerificationDenialsList = customerVerificationDenialRepository.findAllByCustomerId(customerId);

        List<CustomerVerificationDenialJson> resultList = new ArrayList<>();

        for (CustomerVerificationDenial customerVerificationDenial : customerVerificationDenialsList) {

            CustomerVerificationDenialJson customerVerificationDenialJson = new CustomerVerificationDenialJson();

            customerVerificationDenialJson.setId(customerVerificationDenial.getId());
            customerVerificationDenialJson.setAdditionalDescription(customerVerificationDenial.getAdditionalDescription());

            VerificationDenialReason verificationDenialReason = verificationDenialReasonRepository.findById(customerVerificationDenial.getVerificationDenialReasonId())
                .orElse(null);

            customerVerificationDenialJson.setDenialReason(verificationDenialReason.getDescription());

            resultList.add(customerVerificationDenialJson);
        }

        return resultList;
    }

}
