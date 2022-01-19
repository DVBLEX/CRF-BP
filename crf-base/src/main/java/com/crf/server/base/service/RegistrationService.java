package com.crf.server.base.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.CustomerAmlResponse;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.EmailCodeRequest;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.RegistrationRequest;
import com.crf.server.base.entity.Sms;
import com.crf.server.base.entity.SmsCodeRequest;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.RegistrationJson;
import com.crf.server.base.jsonentity.RegistrationRequestJson;
import com.crf.server.base.repository.CustomerRepository;
import com.crf.server.base.repository.EmailCodeRequestRepository;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.repository.RegistrationRequestRepository;
import com.crf.server.base.repository.SmsCodeRequestRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class RegistrationService {

    private CustomerRepository            customerRepository;
    private EmailCodeRequestRepository    emailCodeRequestRepository;
    private OperatorRepository            operatorRepository;
    private RegistrationRequestRepository registrationRequestRepository;
    private SmsCodeRequestRepository      smsCodeRequestRepository;

    private AMLScanService                amlScanService;
    private EmailService                  emailService;
    private OperatorService               operatorService;
    private SMSService                    smsService;
    private SystemService                 systemService;

    private PasswordEncoder               passwordEncoder;

    @Value("${tc.system.url}")
    private String                        systemUrl;

    @Value("${tc.system.environment}")
    private String                        systemEnvironment;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setEmailCodeRequestRepository(EmailCodeRequestRepository emailCodeRequestRepository) {
        this.emailCodeRequestRepository = emailCodeRequestRepository;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setRegistrationRequestRepository(RegistrationRequestRepository registrationRequestRepository) {
        this.registrationRequestRepository = registrationRequestRepository;
    }

    @Autowired
    public void setSmsCodeRequestRepository(SmsCodeRequestRepository smsCodeRequestRepository) {
        this.smsCodeRequestRepository = smsCodeRequestRepository;
    }

    @Autowired
    public void setAmlScanService(AMLScanService amlScanService) {
        this.amlScanService = amlScanService;
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
    public void setSmsService(SMSService smsService) {
        this.smsService = smsService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(rollbackFor = CRFException.class)
    public void sendRegistrationCodeEmail(String emailTo, String firstName, String lastName) throws CRFException {

        String code = RandomStringUtils.randomNumeric(ServerConstants.SIZE_VERIFICATION_CODE);
        long emailCount = emailCodeRequestRepository.countEmailCodeRequestByEmail(emailTo);

        if (emailCount > 0L) {
            emailCodeRequestRepository.updateEmailRequestCodeByEmail(code, emailTo);
        } else {
            emailCodeRequestRepository.createEmailCodeRequest(emailTo, code);
        }

        Email email = new Email();
        email.setEmailTo(emailTo);
        email.setCustomerId(ServerConstants.DEFAULT_LONG);

        HashMap<String, Object> params = new HashMap<>();
        params.put("emailCodeValidityPeriod", systemService.getSystemParameter().getRegEmailCodeValidMinutes());
        params.put("verificationCode", code);
        params.put("firstName", firstName);

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_VERIFICATION_CODE_TEMPLATE_TYPE, params);
    }

    public boolean verifyRegistrationCodeEmail(String email, String code) {

        return emailCodeRequestRepository.countNotVerifiedByEmailAndCode(email, code) > 0L;
    }

    public void setEmailToVerified(String email, String code, String token) {

        emailCodeRequestRepository.updateEmailCodeRequestSetEmailToVerified(token, code, email);
    }

    public boolean isEmailVerifiedWithinHours(String email, String token, int hours) {

        return emailCodeRequestRepository.countEmailVerifiedWithinHours(email, token, hours) > 0L;
    }

    public boolean isCountEmailCodeSentUnderLimit(String email, int codeSentLimit) {

        return emailCodeRequestRepository.countEmailCodeSentUnderLimit(email, codeSentLimit) == 0L;
    }

    public boolean isCountEmailVerifiedUnderLimit(String email, int verifiedLimit) {

        return emailCodeRequestRepository.countEmailVerifiedUnderLimit(email, verifiedLimit) == 0L;
    }

    public EmailCodeRequest getEmailCodeRequest(String email) {

        return emailCodeRequestRepository.findEmailCodeRequestsByEmail(email);
    }

    public void deleteEmailCodeRequest(String email) {

        emailCodeRequestRepository.deleteEmailCodeRequestByEmail(email);
    }

    @Transactional(rollbackFor = CRFException.class)
    public void sendRegistrationCodeSMS(String msisdnTo) throws CRFException {

        String code = RandomStringUtils.randomNumeric(ServerConstants.SIZE_VERIFICATION_CODE);
        long smsCount = smsCodeRequestRepository.countSmsCodeRequestByMsisdn(msisdnTo);

        if (smsCount > 0L) {
            smsCodeRequestRepository.updateSmsRequestCodeByMsisdn(code, msisdnTo);
        } else {
            smsCodeRequestRepository.createSmsCodeRequest(msisdnTo, code);
        }

        Sms scheduleSms = new Sms();
        scheduleSms.setCustomerId(ServerConstants.DEFAULT_LONG);
        scheduleSms.setMsisdn(msisdnTo);

        HashMap<String, Object> params = new HashMap<>();
        params.put("smsCodeValidityPeriod", systemService.getSystemParameter().getRegSMSCodeValidMinutes());
        params.put("verificationCode", code);

        smsService.scheduleSmsByType(scheduleSms, ServerConstants.SMS_VERIFICATION_CODE_TEMPLATE_TYPE, params);
    }

    public boolean verifyRegistrationCodeSMS(String msisdn, String code) {

        return emailCodeRequestRepository.countVerifyRegistrationCodeSMS(msisdn, code) > 0L;
    }

    public boolean verifyRegistrationCodeSMSForPasswordReset(String msisdn, String code) {

        return smsCodeRequestRepository.countSmsCodeRequestByMsisdnAndCode(msisdn, code) > 0L;
    }

    public void setMsisdnToVerified(String msisdn, String code, String token) {

        smsCodeRequestRepository.updateMsisdnToVerified(token, code, msisdn);
    }

    public boolean isMsisdnVerifiedWithinHours(String msisdn, String token, int hours) {

        return emailCodeRequestRepository.countMsisdnVerifiedWithinHours(msisdn, token, hours) > 0L;
    }

    public boolean isCountSmsCodeSentUnderLimit(String msisdn, int codeSentLimit) {

        return emailCodeRequestRepository.countSmsCodeSentUnderLimit(msisdn, codeSentLimit) == 0L;
    }

    public boolean isCountMsidnVerifiedUnderLimit(String msisdn, int verifiedLimit) {

        return emailCodeRequestRepository.countMsisdnVerifiedUnderLimit(msisdn, verifiedLimit) == 0L;
    }

    public SmsCodeRequest getSmsCodeRequest(String msisdn) {

        return smsCodeRequestRepository.findSmsCodeRequestsByMsisdn(msisdn);
    }

    public void deleteSmsCodeRequest(String msisdn) {

        smsCodeRequestRepository.deleteSmsCodeRequestByMsisdn(msisdn);
    }

    @Transactional
    public void createRegistrationRequest(long operatorId, RegistrationRequestJson registrationRequestJson) throws CRFException, CRFValidationException, Exception {

        Customer customer = customerRepository.findByEmail(registrationRequestJson.getEmail());

        if (customer == null) {
            createRegRequest(registrationRequestJson);

        } else {

            if (Boolean.TRUE.equals(customer.getIsDeleted())) {
                createRegRequest(registrationRequestJson);

            } else {
                log.error("createRegistrationRequest###Exception: customer already exists with this email. email: " + registrationRequestJson.getEmail());
                throw new CRFValidationException(ServerResponseConstants.CUSTOMER_EMAIL_ALREADY_REGISTERED_CODE, ServerResponseConstants.CUSTOMER_EMAIL_ALREADY_REGISTERED_TEXT,
                    "");
            }
        }

        operatorService.logOperatorActivity(operatorId, OperatorService.ADMIN_ACTIVITY_ID_SEND_EMAIL_REGISTRATION_URL, ServerUtil.toJson(registrationRequestJson));
    }

    public RegistrationRequest getRegistrationRequest(String email) throws Exception {

        RegistrationRequest registrationRequest = null;

        List<RegistrationRequest> registrationRequestList = registrationRequestRepository.findByEmailOrderByIdDesc(email);

        if (registrationRequestList != null && !registrationRequestList.isEmpty()) {
            registrationRequest = registrationRequestList.get(0);
        }

        return registrationRequest;
    }

    public boolean isRegistrationRequestAllowed(String email, String token1, String token2) throws Exception {
        return emailCodeRequestRepository.countByEmailAndTokens(email, token1, token2, systemService.getSystemParameter().getRegLinkValidHours()) > 0L;
    }

    @Transactional
    public void processRegistration(RegistrationRequest registrationRequest, RegistrationJson registrationJson) throws CRFException, Exception {

        try {
            Customer customer = new Customer();
            customer.setCode(SecurityUtil.generateUniqueCode());
            customer.setType(registrationRequest.getType());
            customer.setCategory(registrationRequest.getCategory());
            customer.setTitle(registrationRequest.getTitle());
            customer.setFirstName(registrationRequest.getFirstName());
            customer.setLastName(registrationRequest.getLastName());
            customer.setDateOfBirth(registrationJson.getDateOfBirth());
            customer.setEmail(registrationRequest.getEmail());
            customer.setMsisdn(registrationJson.getMobileNumber());
            customer.setNationalIdNumber(registrationJson.getNationalIdNumber());
            customer.setNationality(registrationJson.getNationalityCountryISO());
            customer.setResidnenceCountry(registrationJson.getResidenceCountryISO());
            customer.setAddress1(StringUtils.isBlank(registrationJson.getAddress1()) ? ServerConstants.DEFAULT_STRING : registrationJson.getAddress1());
            customer.setAddress2(StringUtils.isBlank(registrationJson.getAddress2()) ? ServerConstants.DEFAULT_STRING : registrationJson.getAddress2());
            customer.setAddress3(StringUtils.isBlank(registrationJson.getAddress3()) ? ServerConstants.DEFAULT_STRING : registrationJson.getAddress3());
            customer.setAddress4(StringUtils.isBlank(registrationJson.getAddress4()) ? ServerConstants.DEFAULT_STRING : registrationJson.getAddress4());
            customer.setPostCode(StringUtils.isBlank(registrationJson.getPostCode()) ? ServerConstants.DEFAULT_STRING : registrationJson.getPostCode());
            customer.setKycOption(ServerConstants.DEFAULT_INT);
            customer.setId1Type(ServerConstants.DEFAULT_INT);
            customer.setId1Number(ServerConstants.DEFAULT_STRING);
            customer.setId2Type(ServerConstants.DEFAULT_INT);
            customer.setId2Number(ServerConstants.DEFAULT_STRING);
            customer.setDateID1Expiry(null);
            customer.setDateID2Expiry(null);
            customer.setPoa1Type(ServerConstants.DEFAULT_INT);
            customer.setPoa2Type(ServerConstants.DEFAULT_INT);
            customer.setIsPassportScanUploaded(false);
            customer.setIsPassportScanVerified(false);
            customer.setIsPassportScanDenied(false);
            customer.setIsPhotoUploaded(false);
            customer.setIsBankAccountSetup(false);
            customer.setIsDeleted(false);
            customer.setDateCreated(new Date());
            customer.setDateEdited(customer.getDateCreated());

            if (systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_LOCAL) || systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_DEV)) {
                CustomerAmlResponse customerAmlResponse = amlScanService.sendPostStub(customer);
                customer.setIsAmlVerified(customerAmlResponse.getNumberOfMatches() == 0);
                customerRepository.save(customer);
                amlScanService.setCustomerIdAndSave(customer, customerAmlResponse);
            } else {
                CustomerAmlResponse customerAmlResponse = amlScanService.sendPost(customer);
                customer.setIsAmlVerified(customerAmlResponse.getNumberOfMatches() == 0);
                customerRepository.save(customer);
                amlScanService.setCustomerIdAndSave(customer, customerAmlResponse);
            }

            Operator operator = new Operator();
            operator.setCode(SecurityUtil.generateUniqueCode());
            operator.setCustomerId(customer.getId());
            operator.setFirstName(customer.getFirstName());
            operator.setLastName(customer.getLastName());
            operator.setEmail(customer.getEmail());
            operator.setMsisdn(customer.getMsisdn());
            operator.setUsername(customer.getEmail());
            operator.setPassword(passwordEncoder.encode(registrationJson.getPassword()));

            if (customer.getType() == ServerConstants.CUSTOMER_TYPE_INVESTOR) {
                operator.setRoleId(ServerConstants.OPERATOR_ROLE_INVESTOR);
            } else if (customer.getType() == ServerConstants.CUSTOMER_TYPE_BORROWER) {
                operator.setRoleId(ServerConstants.OPERATOR_ROLE_BORROWER);
            } else if (customer.getType() == ServerConstants.CUSTOMER_TYPE_INVESTOR_AND_BORROWER) {
                operator.setRoleId(ServerConstants.OPERATOR_ROLE_INVESTOR_AND_BORROWER);
            }

            operator.setIsActive(true);
            operator.setIsDeleted(false);
            operator.setIsLocked(false);
            operator.setLoginFailureCount(0);
            operator.setDateLocked(null);
            operator.setDateLastLogin(null);
            operator.setDateLastAttempt(null);
            operator.setOperatorId(ServerConstants.DEFAULT_LONG);
            operator.setCountPasswdForgotRequests(0);
            operator.setDateLastPasswdForgotRequest(null);
            operator.setDatePasswordForgotReported(null);
            operator.setDateLastPassword(new Date());
            operator.setDateLastPasswdSetUp(null);
            operator.setIsCredentialsExpired(false);
            operator.setDateCreated(new Date());
            operator.setDateEdited(operator.getDateCreated());

            operatorRepository.save(operator);

            Email registrationSuccessEmail = new Email();
            registrationSuccessEmail.setEmailTo(customer.getEmail());
            registrationSuccessEmail.setCustomerId(customer.getId());

            HashMap<String, Object> params = new HashMap<>();
            params.put("firstName", customer.getFirstName());
            params.put("loginPageUrl", systemUrl);

            emailService.scheduleEmailByType(registrationSuccessEmail, ServerConstants.EMAIL_CUSTOMER_REGISTRATION_SUCCESSFUL_TEMPLATE_ID, params);

        } catch (CRFException crfe) {
            log.error("processRegistration##CRFException: ", crfe);
            emailService.sendSystemEmail("Process Registration Error", EmailService.EMAIL_TYPE_EXCEPTION, null, null,
                "processRegistration###CRFException:<br />" + crfe.getMessage() + "<br /> <br /> Source: " + crfe.getResponseSource());
            throw crfe;
        }

        catch (IllegalArgumentException iae) {
            log.error("processRegistration##IllegalArgumentException: ", iae);
            emailService.sendSystemEmail("Process Registration Error", EmailService.EMAIL_TYPE_EXCEPTION, null, null,
                "processRegistration###IllegalArgumentException:<br />" + iae.getMessage());

            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "IllegalArgumentException");

        } catch (Exception e) {
            log.error("processRegistration##Exception: ", e);
            emailService.sendSystemEmail("Process Registration Error", EmailService.EMAIL_TYPE_EXCEPTION, null, null, "processRegistration###Exception:<br />" + e.getMessage());

            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Exception");
        }

    }

    private void createRegRequest(RegistrationRequestJson registrationRequestJson) throws CRFException, Exception {

        StringBuilder token1SB = new StringBuilder();
        StringBuilder token2SB = new StringBuilder();

        token1SB.append(ServerConstants.SYSTEM_TOKEN_PREFIX).append(".").append(registrationRequestJson.getEmail()).append(".");
        token1SB.append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));
        token1SB.append(".").append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));

        token2SB.append(ServerConstants.SYSTEM_TOKEN_PREFIX).append(".").append(registrationRequestJson.getEmail()).append(".");
        token2SB.append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));
        token2SB.append(".").append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));

        DigestUtils.sha256Hex(token1SB.toString());
        DigestUtils.sha256Hex(token2SB.toString());

        String token1 = DigestUtils.sha256Hex(token1SB.toString());
        String token2 = DigestUtils.sha256Hex(token2SB.toString());

        RegistrationRequest registrationRequest = null;

        try {
            registrationRequest = getRegistrationRequest(registrationRequestJson.getEmail());

        } catch (Exception e) {
            log.warn("Exception has been thrown", e);
        }

        boolean exists = registrationRequest != null;

        if (!exists) {

            registrationRequest = new RegistrationRequest();
            registrationRequest.setEmail(registrationRequestJson.getEmail());
        }

        registrationRequest.setType(registrationRequestJson.getType());
        registrationRequest.setCategory(registrationRequestJson.getCategory());
        registrationRequest.setTitle(registrationRequestJson.getTitle());
        registrationRequest.setFirstName(registrationRequestJson.getFirstName());
        registrationRequest.setLastName(registrationRequestJson.getLastName());
        registrationRequest.setToken1(token1);
        registrationRequest.setToken2(token2);
        registrationRequest.setDateCreated(new Date());

        registrationRequestRepository.save(registrationRequest);

        StringBuilder urlSB = new StringBuilder();
        urlSB.append(systemUrl).append("registration.html").append("?u=").append(URLEncoder.encode(registrationRequestJson.getEmail(), StandardCharsets.UTF_8.name()));
        urlSB.append("&ti=").append(registrationRequest.getTitle()).append("&fn=");
        urlSB.append(registrationRequest.getFirstName()).append("&ln=");
        urlSB.append(registrationRequest.getLastName()).append("&t=");
        urlSB.append(token1).append("&t2=").append(token2);

        HashMap<String, Object> params = new HashMap<>();
        params.put("firstName", registrationRequest.getFirstName());
        params.put("registrationLink", urlSB.toString());
        params.put("regUrlValidHours", systemService.getSystemParameter().getRegLinkValidHours());

        Email email = new Email();
        email.setCustomerId(ServerConstants.DEFAULT_LONG);
        email.setEmailTo(registrationRequestJson.getEmail());

        emailService.scheduleEmailByType(email, ServerConstants.EMAIL_CUSTOMER_REGISTRATION_LINK_TEMPLATE_ID, params);
    }

}
