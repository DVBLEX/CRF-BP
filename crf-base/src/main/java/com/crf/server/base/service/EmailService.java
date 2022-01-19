package com.crf.server.base.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.MessageFormatterUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.EmailConfig;
import com.crf.server.base.entity.EmailLog;
import com.crf.server.base.entity.EmailTemplate;
import com.crf.server.base.entity.NameValuePair;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.repository.EmailConfigRepository;
import com.crf.server.base.repository.EmailLogRepository;
import com.crf.server.base.repository.EmailSchedulerRepository;
import com.crf.server.base.repository.EmailTemplateRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class EmailService {

    public static final String                         EMAIL_TYPE_BLANK             = "";
    public static final String                         EMAIL_TYPE_ALERT             = "ALERT";
    public static final String                         EMAIL_TYPE_SUCCESS           = "SUCCESS";
    public static final String                         EMAIL_TYPE_FAILURE           = "FAILURE";
    public static final String                         EMAIL_TYPE_EXCEPTION         = "EXCEPTION";

    private final ConcurrentMap<Long, Properties>      emailConfigPropertiesMap     = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, EmailConfig>     emailConfigMap               = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, EmailTemplate>   emailTemplateMap             = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, EmailTemplate> emailTypeLanguageTemplateMap = new ConcurrentHashMap<>();

    private boolean                                    isLive                       = false;

    private List<EmailConfig>                          emailConfigList              = new CopyOnWriteArrayList<>();

    private List<EmailTemplate>                        emailTemplateList            = new CopyOnWriteArrayList<>();

    private EmailConfigRepository                      emailConfigRepository;
    private EmailLogRepository                         emailLogRepository;
    private EmailSchedulerRepository                   emailSchedulerRepository;
    private EmailTemplateRepository                    emailTemplateRepository;
    private SystemService                              systemService;

    @Value("${tc.system.environment}")
    private String                                     systemEnvironment;

    @Value("${tc.system.shortname}")
    private String                                     systemShortName;

    @Autowired
    public void setEmailConfigRepository(EmailConfigRepository emailConfigRepository) {
        this.emailConfigRepository = emailConfigRepository;
    }

    @Autowired
    public void setEmailLogRepository(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    @Autowired
    public void setEmailSchedulerRepository(EmailSchedulerRepository emailSchedulerRepository) {
        this.emailSchedulerRepository = emailSchedulerRepository;
    }

    @Autowired
    public void setEmailTemplateRepository(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @PostConstruct
    public void init() {

        isLive = ServerConstants.SYSTEM_ENVIRONMENT_PROD.equals(systemEnvironment);

        emailConfigList = (List<EmailConfig>) emailConfigRepository.findAll();
        if (emailConfigList != null && !emailConfigList.isEmpty()) {
            for (EmailConfig emailConfig : emailConfigList) {
                emailConfigPropertiesMap.put(emailConfig.getId(), createProperties(emailConfig));
                emailConfigMap.put(emailConfig.getId(), emailConfig);
            }
        }

        emailTemplateList = (List<EmailTemplate>) emailTemplateRepository.findAll();
        if (emailTemplateList != null && !emailTemplateList.isEmpty()) {
            for (EmailTemplate emailTemplate : emailTemplateList) {
                emailTemplateMap.put(emailTemplate.getId(), emailTemplate);

                String key = emailTemplate.getType() + "";
                emailTypeLanguageTemplateMap.put(key, emailTemplate);
            }
        }
    }

    private Properties createProperties(EmailConfig emailConfig) {

        Properties emailProps = new Properties();
        emailProps.put("mail.smtp.host", emailConfig.getSmtpHost());
        emailProps.put("mail.smtp.auth", emailConfig.getSmtpAuth());
        emailProps.put("mail.smtp.port", emailConfig.getSmtpPort());

        if (emailConfig.getSmtpStarttlsEnable() != null) {
            emailProps.put("mail.smtp.starttls.enable", emailConfig.getSmtpStarttlsEnable());
            emailProps.put("mail.smtp.ssl.protocols", emailConfig.getSmtpSslProtocols());
        }

        return emailProps;
    }

    public void sendSystemEmail(String subject, String emailType, String headerContent, List<NameValuePair> nameValuePairs, String footerContent) {

        try {
            subject = systemEnvironment + " " + systemShortName + " " + subject;

            String content = getContent(emailType, headerContent, nameValuePairs, footerContent);
            InternetAddress[] addressTo = new InternetAddress[1];
            addressTo[0] = new InternetAddress(systemService.getSystemParameter().getErrorsToEmail());

            sendEmail(emailConfigPropertiesMap.get(ServerConstants.DEFAULT_LONG), systemService.getSystemParameter().getErrorsFromEmail(),
                systemService.getSystemParameter().getErrorsFromEmailPassword(), addressTo, null, null, null, subject, content, null);

        } catch (Exception e) {
            log.error("sendSystemEmail###Exception: ", e);
        }
    }

    public String getContent(String emailType, String headerContent, List<NameValuePair> nameValuePairs, String footerContent) {

        StringBuilder content = new StringBuilder("<b>").append(emailType).append("</b>");

        if (headerContent != null) {
            content.append("<p>").append(headerContent).append("</p>");
        }

        content.append("<table>");
        content.append("<tr><td>Date</td><td>").append(new Date()).append("</td></tr>");
        if (nameValuePairs != null) {
            for (NameValuePair nameValuePair : nameValuePairs) {
                content.append("<tr><td>").append(nameValuePair.getName()).append("</td><td>").append(nameValuePair.getValue()).append("</td></tr>");
            }
        }
        content.append("</table>");

        if (footerContent != null) {
            content.append("<p>").append(footerContent).append("</p>");
        }

        return content.toString();
    }

    public void sendEmail(Email email) throws CRFException {

        InternetAddress[] addressTo = null;
        InternetAddress[] addressReplyTo = null;
        InternetAddress[] addressBcc = null;
        try {
            addressTo = InternetAddress.parse(email.getEmailTo());
            for (InternetAddress address : addressTo) {
                address.validate();
            }

            if (StringUtils.isNotBlank(email.getEmailBcc())) {
                addressBcc = InternetAddress.parse(email.getEmailBcc());
                for (InternetAddress address : addressBcc) {
                    address.validate();
                }
            }

            if (StringUtils.isNotBlank(email.getEmailReplyTo())) {
                addressReplyTo = InternetAddress.parse(email.getEmailReplyTo());
                for (InternetAddress address : addressReplyTo) {
                    address.validate();
                }
            }

        } catch (Exception e) {
            throw new CRFException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT + " To. " + e.getClass() + "###" + e.getMessage(),
                "sendEmail#addressTo###Exception");
        }

        EmailTemplate emailTemplate = emailTemplateMap.get(email.getTemplateId());

        File attachment = null;
        if (StringUtils.isNotBlank(email.getAttachmentPath())) {
            attachment = new File(email.getAttachmentPath());
        }

        sendEmail(emailConfigPropertiesMap.get(email.getConfigId()), emailTemplate.getEmailFrom(), emailTemplate.getEmailFromPassword(), addressTo, addressReplyTo, null,
            addressBcc, email.getSubject(), email.getMessage(), attachment);
    }

    public void updateScheduledEmail(Email email) {

        emailSchedulerRepository.updateEmailScheduler(email.getIsProcessed(), email.getDateScheduled(), email.getRetryCount(), new Date(), email.getResponseCode(),
            email.getResponseText(), email.getId());
    }

    public void deleteScheduledEmail(long emailId) {

        emailSchedulerRepository.deleteById(emailId);
    }

    public void updateEmail(Email email) {

        emailLogRepository.updateEmailLog(email.getIsProcessed(), email.getDateScheduled(), email.getRetryCount(), new Date(), email.getResponseCode(), email.getResponseText(),
            email.getId());
    }

    public boolean getIsLive() {

        return this.isLive;
    }

    @Transactional
    public void scheduleEmailByType(Email email, long templateType, HashMap<String, Object> params) throws CRFException {

        updateEmailWithTemplateData(email, templateType, params);
        email.setChannel(ServerConstants.CHANNEL_SYSTEM);
        email.setAttachmentPath(email.getAttachmentPath() == null ? "" : email.getAttachmentPath());
        email.setDateScheduled(email.getDateScheduled() == null ? new Date() : email.getDateScheduled());

        scheduleEmail(email);
    }

    private void sendEmail(Properties props, final String fromEmail, final String fromEmailPassword, InternetAddress[] addressTo, InternetAddress[] addressReplyTo,
        InternetAddress[] addressCc, InternetAddress[] addressBcc, String subject, String content, File attachment) throws CRFException {

        try {
            Session session = Session.getInstance(props, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, fromEmailPassword);
                }
            });

            Message msg = new MimeMessage(session);

            InternetAddress addressFrom = new InternetAddress(fromEmail);
            addressFrom.setPersonal(systemShortName);

            msg.setFrom(addressFrom);
            msg.setRecipients(Message.RecipientType.TO, addressTo);

            if (addressReplyTo != null) {
                msg.setReplyTo(addressReplyTo);
            }
            if (addressCc != null) {
                msg.setRecipients(Message.RecipientType.CC, addressCc);
            }
            if (addressBcc != null) {
                msg.setRecipients(Message.RecipientType.BCC, addressBcc);
            }

            msg.setSubject(subject);

            if (attachment == null) {
                msg.setContent(content, "text/html; charset=utf-8");
            } else {
                BodyPart msgContent = new MimeBodyPart();
                msgContent.setContent(content, "text/html; charset=utf-8");

                BodyPart msgAttachment = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                msgAttachment.setDataHandler(new DataHandler(source));
                msgAttachment.setFileName(attachment.getName());
                Multipart multipart = new MimeMultipart();

                multipart.addBodyPart(msgContent);
                multipart.addBodyPart(msgAttachment);

                msg.setContent(multipart);
            }

            Transport.send(msg);

        } catch (Exception e) {
            log.error("sendEmail###Exception: ", e);
            throw new CRFException(ServerResponseConstants.EXTERNAL_API_CONNECTION_FAILURE_CODE, e.getClass() + "###" + e.getMessage(), "sendEmail###Exception");
        }
    }

    private void updateEmailWithTemplateData(Email email, long templateType, HashMap<String, Object> params) throws CRFException {

        EmailTemplate template = emailTypeLanguageTemplateMap.get(templateType + "");

        email.setType(template.getType());
        email.setConfigId(template.getConfigId());
        email.setTemplateId(template.getId());
        email.setPriority(template.getPriority());
        email.setEmailBcc(template.getEmailBcc());

        if (isLive) {
            email.setSubject(MessageFormatterUtil.formatText(template.getSubject(), params));
        } else {
            email.setSubject(systemEnvironment + " " + MessageFormatterUtil.formatText(template.getSubject(), params));
        }

        HashMap<String, Object> templateBody = new HashMap<>();
        templateBody.put("templateBody", MessageFormatterUtil.formatText(template.getMessage(), params));

        email.setMessage(MessageFormatterUtil.formatText(template.getTemplate(), templateBody));
    }

    private void scheduleEmail(Email email) {

        email.setIsProcessed(ServerConstants.PROCESS_NOTPROCESSED);
        email.setDateCreated(new Date());
        email.setRetryCount(0);
        email.setResponseCode(ServerConstants.DEFAULT_INT);
        email.setResponseText("");

        email.setId(createEmailLog(email));

        emailSchedulerRepository.scheduleEmail(email.getMessage(), email.getId());
    }

    private Long createEmailLog(Email email) {
        EmailLog emailLog = new EmailLog();
        emailLog.setIsProcessed(email.getIsProcessed());
        emailLog.setType(email.getType());
        emailLog.setConfigId(email.getConfigId());
        emailLog.setCustomerId(email.getCustomerId());
        emailLog.setTemplateId(email.getTemplateId());
        emailLog.setPriority(email.getPriority());
        emailLog.setEmailTo(email.getEmailTo());
        emailLog.setEmailReplyTo(email.getEmailReplyTo());
        emailLog.setEmailBcc(email.getEmailBcc());
        emailLog.setSubject(email.getSubject());
        emailLog.setChannel(email.getChannel());
        emailLog.setAttachmentPath(email.getAttachmentPath());
        emailLog.setDateCreated(email.getDateCreated());
        emailLog.setDateScheduled(email.getDateScheduled());
        emailLog.setRetryCount(email.getRetryCount());
        emailLog.setResponseCode(email.getResponseCode());
        emailLog.setResponseText(email.getResponseText());

        emailLog = emailLogRepository.save(emailLog);

        return emailLog.getId();
    }

}
