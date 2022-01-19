package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DepositProductJson {

    private String  code;
    private String  name;
    private String  description;
    private String  yearlyInterestRate;
    private String  twiceYearlyInterestRate;
    private String  quarterlyInterestRate;
    private String  termYears;
    private String  depositMinAmount;
    private String  depositMaxAmount;
    private Integer prematureWithdrawalMinDays;
    private String prematureWithdrawalInterestRate;
    private String withdrawalFee;
}
