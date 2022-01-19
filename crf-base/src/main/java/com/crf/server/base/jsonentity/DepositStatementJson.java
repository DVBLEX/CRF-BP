package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DepositStatementJson {

    private String  code;
    private Integer type;
    private String  description;
    private String  accountNumber;
    private String  amountTransaction;
    private String  amountBalance;
    private String  dateCreatedString;
}
