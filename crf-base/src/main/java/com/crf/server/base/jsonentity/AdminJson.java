package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class AdminJson {

    private String code;
    private String firstName;
    private String lastName;
    private String email;
    private String dateCreatedString;
    private String username;
}
