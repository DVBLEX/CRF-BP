package com.crf.server.base.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.AdminRegistrationRequest;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.AdminRegistrationJson;
import com.crf.server.base.jsonentity.AdminRegistrationRequestJson;
import com.crf.server.base.repository.AdminRegistrationRequestRepository;
import com.crf.server.base.repository.EmailCodeRequestRepository;
import com.crf.server.base.repository.OperatorRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class AdminRegistrationService {

    private EmailCodeRequestRepository         emailCodeRequestRepository;
    private OperatorRepository                 operatorRepository;
    private AdminRegistrationRequestRepository adminRegistrationRequestRepository;
    private EmailService                       emailService;
    private OperatorService                    operatorService;
    private SystemService                      systemService;
    private PasswordEncoder                    passwordEncoder;

    @Value("${tc.system.url}")
    private String                             systemUrl;

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setAdminRegistrationRequestRepository(AdminRegistrationRequestRepository adminRegistrationRequestRepository) {
        this.adminRegistrationRequestRepository = adminRegistrationRequestRepository;
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
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setEmailCodeRequestRepository(EmailCodeRequestRepository emailCodeRequestRepository) {
        this.emailCodeRequestRepository = emailCodeRequestRepository;
    }

    @Transactional
    public void processAdminRegistration(AdminRegistrationRequest adminRegistrationRequest, AdminRegistrationJson adminRegistrationJson) throws CRFException, Exception {

        try {
            //We use table 'operators' to persist admins
            Operator admin = new Operator();

            admin.setCode(SecurityUtil.generateUniqueCode());
            admin.setCustomerId(ServerConstants.DEFAULT_INT);
            admin.setFirstName(adminRegistrationRequest.getFirstName());
            admin.setLastName(adminRegistrationRequest.getLastName());
            admin.setEmail(adminRegistrationRequest.getEmail());
            admin.setMsisdn(ServerConstants.DEFAULT_STRING);
            admin.setUsername(adminRegistrationJson.getEmail());
            admin.setPassword(passwordEncoder.encode(adminRegistrationJson.getPassword()));
            admin.setRoleId(ServerConstants.OPERATOR_ROLE_ADMIN);

            admin.setIsActive(true);
            admin.setIsDeleted(false);
            admin.setIsLocked(false);
            admin.setLoginFailureCount(0);
            admin.setDateLocked(null);
            admin.setDateLastLogin(null);
            admin.setDateLastAttempt(null);
            admin.setOperatorId(ServerConstants.DEFAULT_LONG);
            admin.setCountPasswdForgotRequests(0);
            admin.setDateLastPasswdForgotRequest(null);
            admin.setDatePasswordForgotReported(null);
            admin.setDateLastPassword(new Date());
            admin.setDateLastPasswdSetUp(null);
            admin.setIsCredentialsExpired(false);
            admin.setDateCreated(new Date());
            admin.setDateEdited(admin.getDateCreated());

            operatorRepository.save(admin);

        } catch (Exception e) {
            log.error("processRegistration##AdminRegistrationService: ", e);
        }
    }

    @Transactional
    public void createAdminRegistrationRequest(AdminRegistrationRequestJson adminRegistrationRequestJson) throws CRFException, CRFValidationException, Exception {

        Operator logged = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());
        Operator operator = operatorRepository.findByUsername(adminRegistrationRequestJson.getEmail());

        if (operator == null) {
            createAdminRegRequest(adminRegistrationRequestJson);

        } else {

            if (Boolean.TRUE.equals(operator.getIsDeleted())) {
                createAdminRegRequest(adminRegistrationRequestJson);

            } else {
                log.error("createRegistrationRequest###Exception: user already exists with this email. email: " + adminRegistrationRequestJson.getEmail());
                throw new CRFValidationException(ServerResponseConstants.CUSTOMER_EMAIL_ALREADY_REGISTERED_CODE, ServerResponseConstants.CUSTOMER_EMAIL_ALREADY_REGISTERED_TEXT,
                    "");
            }
        }

        operatorService.logOperatorActivity(logged.getId(), OperatorService.ADMIN_ACTIVITY_ID_SEND_EMAIL_REGISTRATION_URL, ServerUtil.toJson(adminRegistrationRequestJson));
    }

    public AdminRegistrationRequest getAdminRegistrationRequest(String email) throws Exception {

        AdminRegistrationRequest adminRegistrationRequest = null;

        List<AdminRegistrationRequest> adminRegistrationRequestList = adminRegistrationRequestRepository.getAdminRequestListByEmail(email);

        if (adminRegistrationRequestList != null && !adminRegistrationRequestList.isEmpty()) {
            adminRegistrationRequest = adminRegistrationRequestList.get(0);
        }

        return adminRegistrationRequest;
    }

    private void createAdminRegRequest(AdminRegistrationRequestJson adminRegistrationRequestJson) throws CRFException, Exception {

        StringBuilder token1SB = new StringBuilder();
        StringBuilder token2SB = new StringBuilder();

        token1SB.append(ServerConstants.SYSTEM_TOKEN_PREFIX).append(".").append(adminRegistrationRequestJson.getEmail()).append(".");
        token1SB.append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));
        token1SB.append(".").append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));

        token2SB.append(ServerConstants.SYSTEM_TOKEN_PREFIX).append(".").append(adminRegistrationRequestJson.getEmail()).append(".");
        token2SB.append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));
        token2SB.append(".").append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));

        String token1 = DigestUtils.sha256Hex(token1SB.toString());
        String token2 = DigestUtils.sha256Hex(token2SB.toString());

        AdminRegistrationRequest adminRegistrationRequest = null;

        try {
            adminRegistrationRequest = getAdminRegistrationRequest(adminRegistrationRequestJson.getEmail());

        } catch (Exception e) {
            log.warn("Exception has been thrown", e);
        }

        boolean exists = adminRegistrationRequest != null;

        if (!exists) {

            adminRegistrationRequest = new AdminRegistrationRequest();
            adminRegistrationRequest.setEmail(adminRegistrationRequest.getEmail());
        }

        adminRegistrationRequest.setFirstName(adminRegistrationRequestJson.getFirstName());
        adminRegistrationRequest.setLastName(adminRegistrationRequestJson.getLastName());
        adminRegistrationRequest.setEmail(adminRegistrationRequestJson.getEmail());
        adminRegistrationRequest.setToken1(token1);
        adminRegistrationRequest.setToken2(token2);
        adminRegistrationRequest.setDateCreated(new Date());

        adminRegistrationRequestRepository.save(adminRegistrationRequest);

        StringBuilder adminUrlSB = new StringBuilder();
        adminUrlSB.append(systemUrl).append("adminregistration/registrationUser.html").append("?u=")
                .append(URLEncoder.encode(adminRegistrationRequestJson.getEmail(), StandardCharsets.UTF_8.name()))
                .append("&fn=").append(adminRegistrationRequest.getFirstName())
                .append("&ln=").append(adminRegistrationRequest.getLastName())
                .append("&t=").append(token1)
                .append("&t2=").append(token2);

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", adminRegistrationRequest.getFirstName());
        params.put("registrationLink", adminUrlSB.toString());
        params.put("regUrlValidHours", systemService.getSystemParameter().getRegLinkValidHours());

        Email email = new Email();
        email.setCustomerId(ServerConstants.DEFAULT_LONG);
        email.setEmailTo(adminRegistrationRequestJson.getEmail());

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_ADMIN_REGISTRATION_LINK_TEMPLATE_ID, params);
    }

    public boolean isAdminRegistrationRequestAllowed(String email, String token1, String token2) throws Exception {
        return emailCodeRequestRepository.countAdminRequestByEmailAndTokens(email, token1, token2, systemService.getSystemParameter().getRegLinkValidHours()) > 0L;
    }

    public void validateAdminRegistrationRequestJson(AdminRegistrationRequestJson adminRegistrationRequestJson) throws CRFValidationException, CRFException, Exception {

        validateLoggedAdmin();
        validateEmail(adminRegistrationRequestJson.getEmail());
        validateFirstLastNameIsNotBlank(adminRegistrationRequestJson.getFirstName(), adminRegistrationRequestJson.getLastName());

    }

    void validateLoggedAdmin() throws CRFException {

        Operator loggedAdmin = operatorRepository.findByUsername(SecurityUtil.getSystemUsername());

        if (Objects.isNull(loggedAdmin)) {
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "logged admin is null");
        }

        if (loggedAdmin.getRoleId() != ServerConstants.OPERATOR_ROLE_ADMIN) {
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "logged user is not admin");
        }

    }

    private void validateFirstLastNameIsNotBlank(String name, String lastName) throws CRFValidationException {

        if (StringUtils.isBlank(name))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "FirstName");

        if (StringUtils.isBlank(lastName))
            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "LastName");

    }

    private void validateEmail(String email) throws CRFValidationException, Exception {
        Set<String> registeredEmailSet = operatorRepository.getRegisteredEmailSet();

        if (registeredEmailSet.contains(email)) {

            log.error("emailRegURL###Exception: user already exists with this email: " + email);
            throw new CRFValidationException(ServerResponseConstants.CUSTOMER_EMAIL_ALREADY_REGISTERED_CODE, ServerResponseConstants.CUSTOMER_EMAIL_ALREADY_REGISTERED_TEXT, "");
        }

        if (email.length() > ServerConstants.DEFAULT_VALIDATION_LENGTH_64 || !email.matches(ServerConstants.REGEXP_EMAIL))
            throw new CRFValidationException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "RegexFormatCheck#Email");

        if (systemService.isEmailBlockedDomain(email))
            throw new CRFValidationException(ServerResponseConstants.CUSTOMER_EMAIL_BLOCKED_DOMAIN_CODE, "Email is not a company email. Please whitelist the email first.",
                "Conflict#isEmailBlockedDomain");
    }

    public void validateAdminRegistrationRequest(AdminRegistrationRequest adminRegistrationRequest, AdminRegistrationJson adminRegistrationJson) throws CRFException,
        CRFValidationException {
        if (Objects.isNull(adminRegistrationJson)) {
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "adminRegistrationJSON is null");
        } else if (!adminRegistrationJson.getPassword().matches(ServerConstants.REGEX_PASSWORD))
            throw new CRFValidationException(ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_CODE, ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_TEXT,
                "RegexFormatCheck#Password");

        if (!adminRegistrationJson.getToken().matches(ServerConstants.REGEX_SHA256))
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Token");

        if (!adminRegistrationJson.getToken().equals(adminRegistrationRequest.getToken1()))
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "CheckTokenFromJson#Token");
    }

}
