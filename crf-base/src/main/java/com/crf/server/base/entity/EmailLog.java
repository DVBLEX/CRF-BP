package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table("email_log")
@Getter
@Setter
@ToString
public class EmailLog {

    @Id
    @Column("id")
    private long   id;

    @Column("is_processed")
    private int    isProcessed;

    @Column("type")
    private long   type;

    @Column("config_id")
    private long   configId;

    @Column("customer_id")
    private long   customerId;

    @Column("template_id")
    private long   templateId;

    @Column("priority")
    private int    priority;

    @Column("email_to")
    private String emailTo;

    @Column("email_reply_to")
    private String emailReplyTo;

    @Column("email_bcc")
    private String emailBcc;

    @Column("subject")
    private String subject;

    @Column("channel")
    private int    channel;

    @Column("attachment_path")
    private String attachmentPath;

    @Column("date_created")
    private Date   dateCreated;

    @Column("date_scheduled")
    private Date   dateScheduled;

    @Column("retry_count")
    private int    retryCount;

    @Column("date_processed")
    private Date   dateProcessed;

    @Column("response_code")
    private int    responseCode;

    @Column("response_text")
    private String responseText;
}
