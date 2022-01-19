package com.crf.server.base.entity;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

@Table("sms_code_requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SmsCodeRequest {

    private long   id;
    private String msisdn;
    private String code;
    private String token;
    private int    countCodeSent;
    private int    countVerified;
    private Date   dateCodeSent;
    private Date   dateVerified;
    private Date   dateCreated;

    public SmsCodeRequest(long id, String msisdn, String code, String token, int countCodeSent, int countVerified, Date dateCodeSent, Date dateVerified) {
        this.id = id;
        this.msisdn = msisdn;
        this.code = code;
        this.token = token;
        this.countCodeSent = countCodeSent;
        this.countVerified = countVerified;
        this.dateCodeSent = dateCodeSent;
        this.dateVerified = dateVerified;
    }
}
