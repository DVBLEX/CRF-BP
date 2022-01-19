package com.crf.server.base.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import com.crf.server.base.entity.SmsLog;
import com.crf.server.base.repository.SmsLogRepository;
import com.crf.server.base.repository.SmsSchedulerRepository;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.MessageFormatterUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.Sms;
import com.crf.server.base.entity.SmsConfig;
import com.crf.server.base.entity.SmsTemplate;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.repository.SmsConfigRepository;
import com.crf.server.base.repository.SmsTemplateRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class SMSService {

    private final ConcurrentMap<Long, SmsConfig>     smsConfigMap               = new ConcurrentHashMap<>();
    private boolean                                  isLive                     = false;
    private List<SmsConfig>                          smsConfigList              = new CopyOnWriteArrayList<>();
    private List<SmsTemplate>                        smsTemplateList            = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<Long, SmsTemplate>   smsTemplateMap             = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, SmsTemplate> smsTypeLanguageTemplateMap = new ConcurrentHashMap<>();

    private SmsConfigRepository                      smsConfigRepository;
    private SmsLogRepository                         smsLogRepository;
    private SmsSchedulerRepository                   smsSchedulerRepository;
    private SmsTemplateRepository                    smsTemplateRepository;

    @Value("${tc.system.environment}")
    private String                                   systemEnvironment;

    @Autowired
    public void setSmsConfigRepository(SmsConfigRepository smsConfigRepository) {
        this.smsConfigRepository = smsConfigRepository;
    }

    @Autowired
    public void setSmsLogRepository(SmsLogRepository smsLogRepository) {
        this.smsLogRepository = smsLogRepository;
    }

    @Autowired
    public void setSmsSchedulerRepository(SmsSchedulerRepository smsSchedulerRepository) {
        this.smsSchedulerRepository = smsSchedulerRepository;
    }

    @Autowired
    public void setSmsTemplateRepository(SmsTemplateRepository smsTemplateRepository) {
        this.smsTemplateRepository = smsTemplateRepository;
    }

    @PostConstruct
    public void init() {

        isLive = ServerConstants.SYSTEM_ENVIRONMENT_PROD.equals(systemEnvironment);

        smsConfigList = (List<SmsConfig>) smsConfigRepository.findAll();
        if (smsConfigList != null && !smsConfigList.isEmpty()) {
            for (SmsConfig smsConfig : smsConfigList) {

                log.info("init###" + smsConfig.toString());
                smsConfigMap.put(smsConfig.getId(), smsConfig);
            }
        }

        smsTemplateList = (List<SmsTemplate>) smsTemplateRepository.findAll();
        if (smsTemplateList != null && !smsTemplateList.isEmpty()) {
            for (SmsTemplate smsTemplate : smsTemplateList) {

                log.info("init###" + smsTemplate.toString());
                smsTemplateMap.put(smsTemplate.getId(), smsTemplate);

                String key = smsTemplate.getType() + "";
                smsTypeLanguageTemplateMap.put(key, smsTemplate);
            }
        }
    }

    @Transactional
    public void sendBulkSms(Sms sms) throws CRFException {

        try {
            sms.setTransactionId(ServerConstants.DEFAULT_LONG);
            sms.setResponseCode(ServerConstants.DEFAULT_INT);
            SmsConfig smsConfig = smsConfigMap.get(sms.getConfigId());

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("username", smsConfig.getUsername()));
            nameValuePairs.add(new BasicNameValuePair("password", smsConfig.getPassword()));
            nameValuePairs.add(new BasicNameValuePair("destinationAddress", sms.getMsisdn()));
            nameValuePairs.add(new BasicNameValuePair("header", sms.getSourceAddr()));
            nameValuePairs.add(new BasicNameValuePair("message", sms.getMessage()));

            String responseText = "";
            responseText = callAllPointsServlet(sms.getId(), smsConfig.getUrl(), nameValuePairs);

            log.info("sendBulkSms#smsId=" + sms.getId() + "#Response: [" + responseText + "]");

            if (responseText == null)
                throw new CRFException(ServerResponseConstants.EXTERNAL_API_CONNECTION_FAILURE_CODE, ServerResponseConstants.EXTERNAL_API_CONNECTION_FAILURE_TEXT, "sendBulkSms");
            else {
                String responseParameters[] = responseText.split(",");
                sms.setTransactionId(Long.parseLong(responseParameters[0].substring("requestId=".length())));
                sms.setResponseCode(Integer.parseInt(responseParameters[1].substring(" responseCode=".length())));
                sms.setResponseText(responseParameters[2].substring(" responseText=".length()));

                if (sms.getResponseCode() != ServerResponseConstants.SUCCESS_CODE)
                    throw new CRFException(sms.getResponseCode(), sms.getResponseText(), "sendBulkSms");
            }
        } catch (CRFException crfe) {
            throw crfe;
        } catch (Exception e) {
            log.error("sendBulkSms###Exception: ", e);
            throw new CRFException(ServerResponseConstants.EXTERNAL_API_CONNECTION_FAILURE_CODE, e.getClass() + "###" + e.getMessage(), "sendBulkSms###Exception");
        }
    }

    @Transactional
    public void scheduleSmsById(Sms sms, long templateId, HashMap<String, Object> params) throws CRFException {

        SmsTemplate template = smsTemplateMap.get(templateId);

        sms.setType(template.getType());
        sms.setConfigId(template.getConfigId());
        sms.setTemplateId(template.getId());
        sms.setPriority(template.getPriority());

        if (isLive) {
            sms.setSourceAddr(template.getSourceAddr());
        } else {
            String sourceAddr = systemEnvironment + "-" + template.getSourceAddr();
            sms.setSourceAddr(sourceAddr.length() > 11 ? sourceAddr.substring(0, 11) : sourceAddr);
        }

        sms.setMessage(MessageFormatterUtil.formatText(template.getMessage(), params));
        sms.setChannel(ServerConstants.CHANNEL_SYSTEM);
        sms.setDateScheduled(sms.getDateScheduled() == null ? new Date() : sms.getDateScheduled());

        scheduleSms(sms);
    }

    @Transactional
    public void scheduleSmsByType(Sms sms, long templateType, HashMap<String, Object> params) throws CRFException {

        SmsTemplate template = smsTypeLanguageTemplateMap.get(templateType + "");

        sms.setType(template.getType());
        sms.setConfigId(template.getConfigId());
        sms.setTemplateId(template.getId());
        sms.setPriority(template.getPriority());

        if (isLive) {
            sms.setSourceAddr(template.getSourceAddr());
        } else {
            String sourceAddr = systemEnvironment + "-" + template.getSourceAddr();
            sms.setSourceAddr(sourceAddr.length() > 11 ? sourceAddr.substring(0, 11) : sourceAddr);
        }

        HashMap<String, Object> templateBody = new HashMap<>();
        templateBody.put("templateBody", MessageFormatterUtil.formatText(template.getMessage(), params));

        sms.setMessage(MessageFormatterUtil.formatText(template.getMessage(), params));
        sms.setChannel(ServerConstants.CHANNEL_SYSTEM);
        sms.setDateScheduled(new Date());

        scheduleSms(sms);
    }

    public void updateScheduledSms(Sms sms) {

        smsSchedulerRepository.updateSmsScheduler(sms.getIsProcessed(), sms.getDateScheduled(), sms.getRetryCount(),
                new Date(), sms.getTransactionId(), sms.getResponseCode(),
                ServerUtil.restrictLength(sms.getResponseText(), 256), sms.getId());
    }

    public void deleteScheduledSms(long smsId) {

        smsSchedulerRepository.deleteById(smsId);
    }

    public void updateSms(Sms sms) {

        smsLogRepository.updateSmsLog(sms.getIsProcessed(), sms.getDateScheduled(), sms.getRetryCount(), new Date(),
                sms.getTransactionId(), sms.getResponseCode(), ServerUtil.restrictLength(sms.getResponseText(), 256), sms.getId());
    }

    private void scheduleSms(Sms sms) {

        sms.setIsProcessed(ServerConstants.PROCESS_NOTPROCESSED);
        sms.setDateCreated(new Date());
        sms.setRetryCount(0);
        sms.setTransactionId(ServerConstants.DEFAULT_LONG);
        sms.setResponseCode(ServerConstants.DEFAULT_INT);

        sms.setId(createSmsLog(sms));

        smsSchedulerRepository.scheduleSms(sms.getId());
    }

    private long createSmsLog(Sms sms) {

        SmsLog smsLog = new SmsLog();
        smsLog.setIsProcessed(sms.getIsProcessed());
        smsLog.setType(sms.getType());
        smsLog.setConfigId(sms.getConfigId());
        smsLog.setCustomerId(sms.getCustomerId());
        smsLog.setTemplateId(sms.getTemplateId());
        smsLog.setPriority(sms.getPriority());
        smsLog.setMsisdn(sms.getMsisdn());
        smsLog.setSourceAddr(sms.getSourceAddr());
        smsLog.setMessage(sms.getMessage());
        smsLog.setChannel(sms.getChannel());
        smsLog.setDateCreated(sms.getDateCreated());
        smsLog.setDateScheduled(sms.getDateScheduled());
        smsLog.setRetryCount(sms.getRetryCount());
        smsLog.setTransactionId(sms.getTransactionId());
        smsLog.setResponseCode(smsLog.getResponseCode());

        smsLog = smsLogRepository.save(smsLog);

        return smsLog.getId();
    }

    private String callAllPointsServlet(long smsId, String url, List<NameValuePair> nameValuePairs) throws CRFException {

        String responseSource = "callAllPointsServlet#";

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String responseText = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            httpClient = HttpClients.createDefault();

            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).setConnectionRequestTimeout(30000).build();
            httpPost.setConfig(requestConfig);

            httpResponse = httpClient.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity httpResponseEntity = httpResponse.getEntity();
                if (httpResponseEntity != null) {
                    ByteArrayOutputStream httpResponseByteArrayOutputStream = new ByteArrayOutputStream();
                    httpResponseEntity.writeTo(httpResponseByteArrayOutputStream);
                    responseText = httpResponseByteArrayOutputStream.toString();
                }
            } else {

                log.info(responseSource + "smsId=" + smsId + "#Response: [StatusCode=" + httpResponse.getStatusLine().getStatusCode() + ", ReasonPhrase="
                    + httpResponse.getStatusLine().getReasonPhrase() + "]");
                throw new CRFException(ServerResponseConstants.EXTERNAL_API_CONNECTION_FAILURE_CODE, responseSource + "Response: [StatusCode="
                    + httpResponse.getStatusLine().getStatusCode() + ", ReasonPhrase=" + httpResponse.getStatusLine().getReasonPhrase() + "]", responseSource);
            }

        } catch (CRFException rbse) {
            throw rbse;

        } catch (Exception e) {
            log.error(responseSource + "smsId=" + smsId + "###Exception: ", e);
            throw new CRFException(ServerResponseConstants.EXTERNAL_API_CONNECTION_FAILURE_CODE, e.getClass() + "###" + e.getMessage(), responseSource + "##Exception");

        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }

                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (Exception e) {
                log.error(responseSource + "smsId=" + smsId + "###finally#Exception: ", e);
            }
        }

        return responseText;
    }
}
