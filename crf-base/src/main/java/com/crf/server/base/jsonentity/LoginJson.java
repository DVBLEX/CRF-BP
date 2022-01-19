package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class LoginJson {


    private String email;

    @ToString.Exclude
    private String oldPassword;
    @ToString.Exclude
    private String newPassword;
    @ToString.Exclude
    private String confirmPassword;

    @ToString.Exclude
    private String key;
    @ToString.Exclude
    private String secondKey;

    private String recaptchaResponse;

}
