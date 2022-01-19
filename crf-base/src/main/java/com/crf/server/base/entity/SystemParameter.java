package com.crf.server.base.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("system_parameters")
@Getter
@Setter
@ToString
public class SystemParameter {

    @Id
    @Column("id")
    private long    id;

    @Column("errors_from_email")
    private String  errorsFromEmail;

    @Column("errors_from_email_password")
    @ToString.Exclude
    private String  errorsFromEmailPassword;

    @Column("errors_to_email")
    private String  errorsToEmail;

    @Column("contact_email")
    private String  contactEmail;

    @Column("password_forgot_email_limit")
    private int     passwordForgotEmailLimit;

    @Column("reg_email_code_send_limit")
    private int     regEmailCodeSendLimit;

    @Column("reg_email_verification_limit")
    private int     regEmailVerificationLimit;

    @Column("reg_email_code_valid_minutes")
    private int     regEmailCodeValidMinutes;

    @Column("reg_email_verification_valid_hours")
    private int     regEmailVerificationValidHours;

    @Column("reg_sms_code_send_limit")
    private int     regSMSCodeSendLimit;

    @Column("reg_sms_verification_limit")
    private int     regSMSVerificationLimit;

    @Column("reg_sms_code_valid_minutes")
    private int     regSMSCodeValidMinutes;

    @Column("reg_sms_verification_valid_hours")
    private int     regSMSVerificationValidHours;

    @Column("reg_link_valid_hours")
    private int     regLinkValidHours;

    @Column("login_lock_count_failed")
    private int     loginLockCountFailed;

    @Column("login_lock_period")
    private int     loginLockPeriod;

    @Column("login_password_valid_period")
    private int     loginPasswordValidPeriod;

    @Column("email_mobile_fileupload_link_limit")
    private int     emailMobileFileuploadLinkLimit;

    @Column("email_mobile_fileupload_link_valid_minutes")
    private int     emailMobileFileuploadLinkValidMinutes;

    @Column("password_forgot_url_valid_minutes")
    private int     passwordForgotUrlValidMinutes;

    @Column("initiated_deposit_expiry_days")
    private Integer initiatedDepositExpiryDays;
}
