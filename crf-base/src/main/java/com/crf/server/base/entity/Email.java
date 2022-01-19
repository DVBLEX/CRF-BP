package com.crf.server.base.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Email implements Serializable {

    private static final long serialVersionUID = 1L;

    private long              id;
    private int               isProcessed;
    private long              type;
    private long              configId;
    private long              customerId;
    private long              templateId;
    private int               priority;
    private String            emailTo;
    private String            emailReplyTo;
    private String            emailBcc;
    private String            subject;
    private String            message;
    private int               channel;
    private String            attachmentPath;
    private Date              dateCreated;
    private Date              dateScheduled;
    private int               retryCount;
    private Date              dateProcessed;
    private int               responseCode;
    private String            responseText;
}
