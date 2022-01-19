package com.crf.server.base.jsonentity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class RegistrationJson {

    private String  code;
    private String  email;
    private String  firstName;
    private String  lastName;

    @ToString.Exclude
    private String  password;

    private String  mobileNumber;
    private String  dobString;
    private String  nationalIdNumber;
    private String  address1;
    private String  address2;
    private String  address3;
    private String  address4;
    private String  postCode;
    private String  nationalityCountryISO;
    private String  residenceCountryISO;

    private String  token;
    private String  recaptchaResponse;

    private Date    dateOfBirth;
}
