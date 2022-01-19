package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class BankDetailsJson {

    private String bankAccountName;
    private String iban;
    private String bic;
    private String name;
    private String address;

    private long   daysSinceLastUpdate;
}
