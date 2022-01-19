package com.crf.server.base.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.NameValuePair;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.OperatorActivityLog;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.repository.OperatorActivityLogRepository;
import com.crf.server.base.repository.OperatorRepository;

@Service
public class OperatorService {

    // admin activities
    public static final long                         ADMIN_ACTIVITY_ID_VERIFY_CUSTOMER                  = 100;
    public static final long                         ADMIN_ACTIVITY_ID_DENY_CUSTOMER                    = 110;
    public static final long                         ADMIN_ACTIVITY_ID_WHITELIST_EMAIL                  = 120;
    public static final long                         ADMIN_ACTIVITY_ID_SEND_EMAIL_REGISTRATION_URL      = 130;
    public static final long                         ADMIN_ACTIVITY_ID_APPROVE_DEPOSIT                  = 140;
    public static final long                         ADMIN_ACTIVITY_ID_APPROVE_DEPOSIT_WITHDRAWAL       = 150;
    public static final long                         ADMIN_ACTIVITY_ID_PROCESS_DEPOSIT_INTEREST_PAYMENT = 160;

    // investor activities
    public static final long                         INVESTOR_ACTIVITY_ID_INITIATE_DEPOSIT              = 500;
    public static final long                         INVESTOR_ACTIVITY_ID_REQUEST_DEPOSIT_WITHDRAWAL    = 510;
    public static final long                         INVESTOR_ACTIVITY_ID_SAVE_BANK_ACCOUNT             = 520;

    private final ConcurrentMap<Long, NameValuePair> activityLogMap                                     = new ConcurrentHashMap<>();

    private PasswordEncoder                          passwordEncoder;
    private OperatorActivityLogRepository            operatorActivityLogRepository;
    private OperatorRepository                       operatorRepository;
    private EmailService                             emailService;
    private SystemService                            systemService;

    @Value("${tc.system.url}")
    private String                                   systemUrl;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setOperatorActivityLogRepository(OperatorActivityLogRepository operatorActivityLogRepository) {
        this.operatorActivityLogRepository = operatorActivityLogRepository;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @PostConstruct
    public void init() {

        activityLogMap.put(ADMIN_ACTIVITY_ID_VERIFY_CUSTOMER, new NameValuePair("Admin - Verify Customer", ADMIN_ACTIVITY_ID_VERIFY_CUSTOMER));
        activityLogMap.put(ADMIN_ACTIVITY_ID_DENY_CUSTOMER, new NameValuePair("Admin - Deny Customer", ADMIN_ACTIVITY_ID_DENY_CUSTOMER));
        activityLogMap.put(ADMIN_ACTIVITY_ID_WHITELIST_EMAIL, new NameValuePair("Admin - Whitelist Email Address", ADMIN_ACTIVITY_ID_WHITELIST_EMAIL));
        activityLogMap.put(ADMIN_ACTIVITY_ID_SEND_EMAIL_REGISTRATION_URL, new NameValuePair("Admin - Send Email Registration URL", ADMIN_ACTIVITY_ID_SEND_EMAIL_REGISTRATION_URL));
        activityLogMap.put(ADMIN_ACTIVITY_ID_APPROVE_DEPOSIT, new NameValuePair("Admin - Approve Deposit", ADMIN_ACTIVITY_ID_APPROVE_DEPOSIT));
        activityLogMap.put(ADMIN_ACTIVITY_ID_APPROVE_DEPOSIT_WITHDRAWAL, new NameValuePair("Admin - Approve Deposit Withdrawal", ADMIN_ACTIVITY_ID_APPROVE_DEPOSIT_WITHDRAWAL));
        activityLogMap.put(ADMIN_ACTIVITY_ID_PROCESS_DEPOSIT_INTEREST_PAYMENT, new NameValuePair("Admin - Process Deposit Interest Payment",
            ADMIN_ACTIVITY_ID_PROCESS_DEPOSIT_INTEREST_PAYMENT));

        activityLogMap.put(INVESTOR_ACTIVITY_ID_INITIATE_DEPOSIT, new NameValuePair("Investor - Initiate Deposit", INVESTOR_ACTIVITY_ID_INITIATE_DEPOSIT));
        activityLogMap.put(INVESTOR_ACTIVITY_ID_REQUEST_DEPOSIT_WITHDRAWAL, new NameValuePair("Investor - Request Deposit Withdrawal",
            INVESTOR_ACTIVITY_ID_REQUEST_DEPOSIT_WITHDRAWAL));
        activityLogMap.put(INVESTOR_ACTIVITY_ID_SAVE_BANK_ACCOUNT, new NameValuePair("Investor - Save Bank Account Details", INVESTOR_ACTIVITY_ID_SAVE_BANK_ACCOUNT));
    }

    public String getActivityNameById(long activityId) {

        return activityLogMap.get(activityId).getName();
    }

    @Transactional
    public void sendPasswdForgotEmail(String emailTo) throws CRFException, Exception {

        Operator operator = operatorRepository.findByUsername(emailTo);

        operator.setCountPasswdForgotRequests(operator.getCountPasswdForgotRequests() + 1);

        Date dateForgotPasswdRequest = new Date();

        String token1 = SecurityUtil.generateDateBasedToken1(emailTo, dateForgotPasswdRequest);
        String token2 = SecurityUtil.generateDateBasedToken2(emailTo, dateForgotPasswdRequest);

        Email email = new Email();
        email.setCustomerId(operator.getCustomerId());
        email.setEmailTo(emailTo);

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", operator.getFirstName());
        params.put("resetPasswordLink", systemUrl + "passwordForgotChange.html?u=" + URLEncoder.encode(emailTo, StandardCharsets.UTF_8.name()) + "&t=" + token1 + "&t2=" + token2);
        params.put("validMinutes", systemService.getSystemParameter().getPasswordForgotUrlValidMinutes());

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_PASSWORD_FORGOT_TEMPLATE_ID, params);

        operator.setDateLastPasswdForgotRequest(dateForgotPasswdRequest);

        operatorRepository.save(operator);
    }

    public Operator getActiveOperatorByUsername(String username) {

        Operator operator = operatorRepository.findByUsername(username);

        if (operator == null || operator.getIsDeleted())
            return null;

        return operator;
    }

    @Transactional
    public void logOperatorActivity(long operatorId, long activityId, String json) throws Exception {

        String activityName = getActivityNameById(activityId);

        OperatorActivityLog operatorActivityLog = new OperatorActivityLog();
        operatorActivityLog.setOperatorId(operatorId);
        operatorActivityLog.setActivityId(activityId);
        operatorActivityLog.setActivityName(activityName);
        operatorActivityLog.setJson(json);
        operatorActivityLog.setDateCreated(new Date());

        operatorActivityLogRepository.save(operatorActivityLog);
    }

    @Transactional
    public void changePassword(Operator loggedOperator, String newPassword) {

        loggedOperator.setPassword(passwordEncoder.encode(newPassword));
        loggedOperator.setIsCredentialsExpired(false);
        loggedOperator.setDateLastPassword(new Date());
        loggedOperator.setLoginFailureCount(0);
        loggedOperator.setCountPasswdForgotRequests(0);
        loggedOperator.setOperatorId(loggedOperator.getId());
        loggedOperator.setDateEdited(new Date());

        operatorRepository.save(loggedOperator);
    }


    public Operator getLoggedInvestor() throws CRFException {

        Operator loggedOperator = operatorRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (loggedOperator == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedOperator.getRoleId() != ServerConstants.OPERATOR_ROLE_INVESTOR)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not investor");

        return loggedOperator;
    }
}
