package com.crf.server.base.service;

import java.net.URI;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.GoogleResponse;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class CaptchaService {

    private HttpServletRequest      request;
    private ReCaptchaAttemptService reCaptchaAttemptService;
    private RestTemplate            restTemplate;

    @Value("${tc.google.recaptcha.key.site}")
    private String                  site;

    @Value("${tc.google.recaptcha.key.secret}")
    private String                  secret;

    @Value("${tc.google.recaptcha.verify.url}")
    private String                  verify_url;

    private static final Pattern    response_pattern = Pattern.compile("[A-Za-z0-9_-]+");

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    public void setReCaptchaAttemptService(ReCaptchaAttemptService reCaptchaAttemptService) {
        this.reCaptchaAttemptService = reCaptchaAttemptService;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void processResponse(String response) throws CRFException {

        if (!responseSanityCheck(response))
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Captcha response contains invalid characters.");

        if (reCaptchaAttemptService.isBlocked(getClientIP()))
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT,
                "Account exceeded the maximum number of captcha failed attempts.");

        final URI verifyUri = URI.create(String.format(verify_url, getReCaptchaSecret(), response, getClientIP()));
        try {
            final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            log.info("Google's response: {} " + googleResponse.toString());

            if (!googleResponse.getSuccess()) {
                if (googleResponse.hasClientError()) {
                    reCaptchaAttemptService.reCaptchaFailed(getClientIP());
                }
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "Captcha was not successfully validated.");
            }
        } catch (RestClientException rce) {
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "ReCaptcha service unavailable.");
        }
        reCaptchaAttemptService.reCaptchaSucceeded(getClientIP());
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.isNotBlank(response) && response_pattern.matcher(response).matches();
    }

    public String getReCaptchaSite() {
        return site;
    }

    public String getReCaptchaSecret() {
        return secret;
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null)
            return request.getRemoteAddr();
        return xfHeader.split(",")[0];
    }
}
