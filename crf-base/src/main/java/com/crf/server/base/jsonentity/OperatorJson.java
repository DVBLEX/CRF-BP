package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class OperatorJson {

    private String  code;
    private String  firstName;
    private String  lastName;
    private String  email;
    private String  msisdn;
    private String  username;
    private Integer roleId;
    private Boolean isActive;
    private Boolean isDeleted;
    private Boolean isLocked;
    private Integer loginFailureCount;

    @ToString.Exclude
    private String  currentPassword;
    @ToString.Exclude
    private String  password;
    @ToString.Exclude
    private String  confirmPassword;

    private String  recaptchaResponse;
}
