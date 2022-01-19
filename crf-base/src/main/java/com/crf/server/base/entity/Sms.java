package com.crf.server.base.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Sms implements Serializable {

    private static final long serialVersionUID = 1L;

    private long              id;
    private int               isProcessed;
    private long              type;
    private long              configId;
    private long              customerId;
    private long              templateId;
    private int               priority;
    private String            msisdn;
    private String            sourceAddr;
    private String            message;
    private int               channel;
    private Date              dateCreated;
    private Date              dateScheduled;
    private int               retryCount;
    private Date              dateProcessed;
    private long              transactionId;
    private int               responseCode;
    private String            responseText;
}
