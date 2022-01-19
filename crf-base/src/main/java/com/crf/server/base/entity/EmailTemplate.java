package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("email_templates")
@Getter
@Setter
@ToString
public class EmailTemplate {

    @Id
    @Column("id")
    private long   id;

    @Column("type")
    private long   type;

    @Column("name")
    private String name;

    @Column("config_id")
    private long   configId;

    @Column("email_from")
    private String emailFrom;

    @Column("email_from_password")
    @ToString.Exclude
    private String emailFromPassword;

    @Column("email_bcc")
    private String emailBcc;

    @Column("subject")
    private String subject;

    @Column("template")
    private String template;

    @Column("message")
    private String message;

    @Column("variables")
    private String variables;

    @Column("priority")
    private int    priority;

    @Column("operator_id")
    private long   operatorId;

    @Column("date_created")
    private Date   dateCreated;

    @Column("date_edited")
    private Date   dateEdited;
}
