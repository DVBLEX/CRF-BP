package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class BankAccountJson {

    private String code;
    private String bankName;
    private String bankAccountName;
    private String bankAddress;
    private String iban;
    private String bic;
}
