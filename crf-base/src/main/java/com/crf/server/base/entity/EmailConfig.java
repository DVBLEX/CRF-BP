package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("email_config")
@Getter
@Setter
@ToString
public class EmailConfig {

    @Id
    @Column("id")
    private Long   id;

    @Column("smtp_host")
    private String smtpHost;

    @Column("smtp_auth")
    private String smtpAuth;

    @Column("smtp_port")
    private String smtpPort;

    @Column("smtp_starttls_enable")
    private String smtpStarttlsEnable;

    @Column("smtp_ssl_protocols")
    private String smtpSslProtocols;

    @Column("operator_id")
    private long   operatorId;

    @Column("date_created")
    private Date   dateCreated;

    @Column("date_edited")
    private Date   dateEdited;

}
