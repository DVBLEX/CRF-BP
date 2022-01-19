package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DepositAccountDocumentJson {

    private String  code;
    private Integer type;
    private String  accountNumber;
    private String  dateCreatedString;
}
