package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("operators")
@Getter
@Setter
@ToString
public class Operator {

    @Id
    @Column("id")
    private Long    id;

    @Column("code")
    private String  code;

    @Column("customer_id")
    private long    customerId;

    @Column("first_name")
    private String  firstName;

    @Column("last_name")
    private String  lastName;

    @Column("email")
    private String  email;

    @Column("msisdn")
    private String  msisdn;

    @Column("username")
    private String  username;

    @Column("password")
    @ToString.Exclude
    private String  password;

    @Column("role_id")
    private int     roleId;

    @Column("is_active")
    private Boolean isActive;

    @Column("is_deleted")
    private Boolean isDeleted;

    @Column("is_locked")
    private Boolean isLocked;

    @Column("login_failure_count")
    private int     loginFailureCount;

    @Column("date_locked")
    private Date    dateLocked;

    @Column("date_last_login")
    private Date    dateLastLogin;

    @Column("date_last_attempt")
    private Date    dateLastAttempt;

    @Column("count_passwd_forgot_requests")
    private int     countPasswdForgotRequests;

    @Column("date_last_passwd_forgot_request")
    private Date    dateLastPasswdForgotRequest;

    @Column("date_password_forgot_reported")
    private Date    datePasswordForgotReported;

    @Column("date_last_password")
    private Date    dateLastPassword;

    @Column("date_last_passwd_set_up")
    private Date    dateLastPasswdSetUp;

    @Column("operator_id")
    private long    operatorId;

    @Column("is_credentials_expired")
    private Boolean isCredentialsExpired;

    @Column("date_created")
    private Date    dateCreated;

    @Column("date_edited")
    private Date    dateEdited;

}
