package com.crf.server.base.entity;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

@Table("email_code_requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmailCodeRequest {

    private long   id;
    private String email;
    private String code;
    private String token;
    private int    countCodeSent;
    private int    countVerified;
    private Date   dateCodeSent;
    private Date   dateVerified;
    private Date   dateCreated;

    public EmailCodeRequest(long id, String email, String code, String token, int countCodeSent, int countVerified, Date dateCodeSent, Date dateVerified) {
        this.id = id;
        this.email = email;
        this.code = code;
        this.token = token;
        this.countCodeSent = countCodeSent;
        this.countVerified = countVerified;
        this.dateCodeSent = dateCodeSent;
        this.dateVerified = dateVerified;
    }
}
