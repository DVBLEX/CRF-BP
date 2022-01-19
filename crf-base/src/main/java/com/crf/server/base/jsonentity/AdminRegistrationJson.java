package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class AdminRegistrationJson {
    private String  email;
    private String  firstName;
    private String  lastName;

    @ToString.Exclude
    private String  password;
    private String  token;
}


