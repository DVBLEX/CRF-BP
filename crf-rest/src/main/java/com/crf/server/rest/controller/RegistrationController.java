package com.crf.server.rest.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.RegistrationRequest;
import com.crf.server.base.entity.SmsCodeRequest;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.RegistrationJson;
import com.crf.server.base.service.CaptchaService;
import com.crf.server.base.service.RegistrationService;
import com.crf.server.base.service.SystemService;

@RestController
@RequestMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {

    private static final ZoneId currentZone = ZoneId.systemDefault();
    private static final int    ALLOWED_AGE = 18;
    private CaptchaService      captchaService;
    private RegistrationService registrationService;
    private SystemService       systemService;

    @Autowired
    public void setCaptchaService(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @Autowired
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @PostMapping("/sendsmscode")
    public ApiResponseJsonEntity sendSmsCode(HttpServletResponse response, @RequestBody RegistrationJson registrationJson) throws CRFException, CRFValidationException {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();
        String msisdn = ServerUtil.getValidNumber(registrationJson.getMobileNumber(), "sendSmsCode");

        validateRegistrationJSON(registrationJson);

        validateMsisdn(msisdn);

        registrationService.sendRegistrationCodeSMS(msisdn);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

        return apiResponse;

    }

    @PostMapping("/verifysmscode")
    public ApiResponseJsonEntity verifySmsCode(HttpServletResponse response, @RequestBody RegistrationJson registrationJson) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        String msisdn = getValidatedMsisdn(registrationJson.getMobileNumber());

        if (!registrationJson.getCode().matches(ServerConstants.REGEX_REGISTRATION_CODE))
            throw new CRFValidationException(ServerResponseConstants.INVALID_VERIFICATION_CODE_CODE, ServerResponseConstants.INVALID_VERIFICATION_CODE_TEXT,
                "RegexFormatCheck#SmsVerificationCode");

        SmsCodeRequest smsCodeRequest = registrationService.getSmsCodeRequest(msisdn);

        if (smsCodeRequest != null && smsCodeRequest.getCountVerified() >= systemService.getSystemParameter().getRegSMSVerificationLimit())
            throw new CRFValidationException(ServerResponseConstants.LIMIT_VERIFICATION_EXCEEDED_CODE, ServerResponseConstants.LIMIT_VERIFICATION_EXCEEDED_TEXT,
                "LimitExceeded#SmsVerification");

        else if (smsCodeRequest != null) {

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime codeSentDate = LocalDateTime.ofInstant(smsCodeRequest.getDateCodeSent().toInstant(), currentZone);

            if (codeSentDate.isBefore(now.minusMinutes(systemService.getSystemParameter().getRegEmailCodeValidMinutes())))
                throw new CRFValidationException(ServerResponseConstants.EXPIRED_VERIFICATION_CODE_CODE, ServerResponseConstants.EXPIRED_VERIFICATION_CODE_TEXT,
                    "Expired#SmsVerificationCode");
        }

        else if (registrationService.verifyRegistrationCodeSMS(msisdn, registrationJson.getCode())) {

            StringBuilder tokenSB = new StringBuilder();
            tokenSB.append(ServerConstants.SYSTEM_TOKEN_PREFIX).append(".").append(msisdn).append(".").append(registrationJson.getCode()).append(".");
            tokenSB.append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));
            String token = DigestUtils.sha256Hex(tokenSB.toString());

            registrationService.setMsisdnToVerified(msisdn, registrationJson.getCode(), token);

            apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
            apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
            apiResponse.setData(token);

        } else
            throw new CRFValidationException(ServerResponseConstants.INVALID_VERIFICATION_CODE_CODE, ServerResponseConstants.INVALID_VERIFICATION_CODE_TEXT,
                "Exception#SmsVerificationCode");

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    private String getValidatedMsisdn(String mobileNumber) throws CRFValidationException, CRFException {

        if (StringUtils.isBlank(mobileNumber))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "MobileNumber");

        String msisdn = ServerUtil.getValidNumber(mobileNumber, "sendSmsCode");

        if (msisdn.length() > ServerConstants.DEFAULT_VALIDATION_LENGTH_16)
            throw new CRFValidationException(ServerResponseConstants.INVALID_MSISDN_CODE, ServerResponseConstants.INVALID_MSISDN_TEXT, "");

        if (systemService.isMsisdnRegisteredAlready(msisdn))
            throw new CRFValidationException(ServerResponseConstants.CUSTOMER_MSISDN_ALREADY_REGISTERED_CODE, ServerResponseConstants.CUSTOMER_MSISDN_ALREADY_REGISTERED_TEXT,
                "Conflict#AlreadyRegisteredMsisdn");
        return msisdn;
    }

    private void validateRegistrationJSON(RegistrationJson registrationJson) throws CRFException {

        if (StringUtils.isBlank(registrationJson.getFirstName()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "FirstName");

        if (StringUtils.isBlank(registrationJson.getLastName()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "LastName");

        if (StringUtils.isBlank(registrationJson.getMobileNumber()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "MobileNumber");

    }

    private void validateMsisdn(String msisdn) throws CRFValidationException {
        if (msisdn.length() > ServerConstants.DEFAULT_VALIDATION_LENGTH_16)
            throw new CRFValidationException(ServerResponseConstants.INVALID_MSISDN_CODE, ServerResponseConstants.INVALID_MSISDN_TEXT, "");

        else if (!registrationService.isCountSmsCodeSentUnderLimit(msisdn, systemService.getSystemParameter().getRegSMSCodeSendLimit()))
            throw new CRFValidationException(ServerResponseConstants.LIMIT_EXCEEDED_VERIFICATION_CODE_SENT_CODE, ServerResponseConstants.LIMIT_EXCEEDED_VERIFICATION_CODE_SENT_TEXT,
                "LimitExceeded#sendSmsCode");

        else if (systemService.isMsisdnRegisteredAlready(msisdn))
            throw new CRFValidationException(ServerResponseConstants.CUSTOMER_MSISDN_ALREADY_REGISTERED_CODE, ServerResponseConstants.CUSTOMER_MSISDN_ALREADY_REGISTERED_TEXT,
                "Conflict#AlreadyRegisteredMsisdn");

    }

    @PostMapping("/processregistration")
    public ApiResponseJsonEntity processRegistration(HttpServletResponse response, @RequestBody RegistrationJson registrationJson) throws CRFException, CRFValidationException,
        Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        RegistrationRequest registrationRequest = registrationService.getRegistrationRequest(registrationJson.getEmail());

        if (registrationRequest == null)
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "registrationRequest is null");

        else if (!registrationJson.getPassword().matches(ServerConstants.REGEX_PASSWORD))
            throw new CRFValidationException(ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_CODE, ServerResponseConstants.INVALID_TOO_WEAK_PASSWORD_TEXT,
                "RegexFormatCheck#Password");

        else if (StringUtils.isBlank(registrationJson.getMobileNumber()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "MobileNumber");

        String msisdn = ServerUtil.getValidNumber(registrationJson.getMobileNumber(), "processRegistration");

        registrationJson.setMobileNumber(msisdn);

        validateDateOfBirth(registrationJson.getDobString());

        try {
            registrationJson.setDateOfBirth(ServerUtil.parseDate(ServerConstants.dateFormatddMMyyyy, registrationJson.getDobString()));

        } catch (ParseException pe) {
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "DobString#2");
        }

        validateNationalIdNumber(registrationJson.getNationalIdNumber());

        validateNationalityCountryISO(registrationJson.getNationalityCountryISO());

        validateResidenceCountryISO(registrationJson.getResidenceCountryISO());

        if (!registrationService.isMsisdnVerifiedWithinHours(registrationJson.getMobileNumber(), registrationJson.getToken(),
            systemService.getSystemParameter().getRegSMSVerificationValidHours()))
            throw new CRFValidationException(ServerResponseConstants.VERIFICATION_EXPIRED_CODE, ServerResponseConstants.VERIFICATION_EXPIRED_TEXT,
                "Expired#MobileNumberVerificationForRegistration");

        validateAddresses(registrationJson.getAddress1(), registrationJson.getAddress2(), registrationJson.getAddress3(), registrationJson.getAddress4());

        if (StringUtils.isBlank(registrationJson.getPostCode()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "PostCode");

        else if (!registrationJson.getToken().matches(ServerConstants.REGEX_SHA256))
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Token");

        else {
            registrationService.processRegistration(registrationRequest, registrationJson);

            apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
            apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        }

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    private void validateDateOfBirth(String dateOfBirthString) throws CRFException, CRFValidationException {

        if (StringUtils.isBlank(dateOfBirthString))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "DobString#1");

        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthString, DateTimeFormatter.ofPattern(ServerConstants.dateFormatddMMyyyy));

        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

        if (age < ALLOWED_AGE) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_AGE_CODE, ServerResponseConstants.INVALID_AGE_TEXT, "validate Date of Birth");
        }

    }

    private void validateNationalIdNumber(String nationalIdNumber) throws CRFException {
        if (StringUtils.isBlank(nationalIdNumber))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "NationalIdNumber#1");

        else if (nationalIdNumber.length() < 4 || nationalIdNumber.length() > 32)
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "NationalIdNumber#2");

        else if (!nationalIdNumber.matches(ServerConstants.REGEXP_BASIC_ID_NUMBER))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "NationalIdNumber#3");

    }

    private void validateAddresses(String address1, String address2, String address3, String address4) throws CRFException {

        if (StringUtils.isBlank(address1) || address1.length() > 64)
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "Address1");

        else if (StringUtils.isBlank(address2) || address2.length() > 64)
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "Address2");

        else if (StringUtils.isNotBlank(address3) && address3.length() > 64)
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "Address3");

        else if (StringUtils.isNotBlank(address4) && address4.length() > 64)
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "Address4");

    }

    private void validateNationalityCountryISO(String nationalityCountryISO) throws CRFException {
        if (StringUtils.isBlank(nationalityCountryISO))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "Nationality#1");

        else if (!nationalityCountryISO.matches(ServerConstants.REGEX_UNIVERSAL_COUNTRY_CODE))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "Nationality#2");

    }

    private void validateResidenceCountryISO(String residenceCountryISO) throws CRFException {
        if (StringUtils.isBlank(residenceCountryISO))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "ResidenceCountry#1");

        else if (!residenceCountryISO.matches(ServerConstants.REGEX_UNIVERSAL_COUNTRY_CODE))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "ResidenceCountry#2");

    }
}
