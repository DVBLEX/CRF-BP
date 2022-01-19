package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DepositAccountJson {

    private String             code;
    private String             customerCode;
    private String             customerName;
    private String             accountNumber;
    private String             depositAmount;
    private String             withdrawalFee;
    private String             depositWithdrawalAmount;
    private Integer            interestPayoutFrequency;
    private String             interestEarnedAmount;
    private String             interestRate;
    private String             prematureWithdrawalInterestRate;
    private String             termYears;
    private Integer            status;
    private String             bankTransferReference;
    private String             dateOpenString;
    private String             dateStartString;
    private String             dateWithdrawalString;
    private String             dateCreatedString;

    // before deposit approval, maturityDate = dateOpen + termYears
    // after deposit approval, maturityDate = dateStart + termYears
    private String             dateMaturityString;

    // any interest that has accumulated and hasn't been paid out yet
    private String             accruedInterest;

    // the field below is calculated based on the selected depositAmount, interestRate & termYears
    private String             totalInterest;

    // the selected initial depositAmount + total interest earned for the deposit
    private String             depositPlusInterestAmount;

    private BankDetailsJson    bankDetailsJson;

    private DepositProductJson depositProductJson;
}
