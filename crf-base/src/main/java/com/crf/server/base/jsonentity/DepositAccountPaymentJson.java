package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DepositAccountPaymentJson {

    private String          code;
    private String          customerName;
    private String          accountNumber;
    private Integer         interestPayoutFrequency;
    private String          interestPaymentAmount;
    private String          interestPeriodString;
    private BankDetailsJson bankDetailsJson;
    private Boolean         isProcessed;
    private String          dateProcessedString;
    private String          dateCreatedString;
}
