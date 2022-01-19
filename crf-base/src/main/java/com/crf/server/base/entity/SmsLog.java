package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table("sms_log")
@Getter
@Setter
@ToString
public class SmsLog {
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

    @Column("msisdn")
    private String msisdn;

    @Column("source_addr")
    private String sourceAddr;

    @Column("message")
    private String message;

    @Column("channel")
    private int    channel;

    @Column("date_created")
    private Date   dateCreated;

    @Column("date_scheduled")
    private Date   dateScheduled;

    @Column("retry_count")
    private int    retryCount;

    @Column("date_processed")
    private Date   dateProcessed;

    @Column("transaction_id")
    private long   transactionId;

    @Column("response_code")
    private int    responseCode;

    @Column("response_text")
    private String responseText;
}
