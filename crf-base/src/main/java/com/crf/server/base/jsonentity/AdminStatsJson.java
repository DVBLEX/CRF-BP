package com.crf.server.base.jsonentity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AdminStatsJson {

    private int        activeDepositCount;
    private int        investorCount;

    private BigDecimal totalActiveDepositAmount;
    private BigDecimal totalInterestPaidAmount;

    private String     totalActiveDepositAmountString;
    private String     totalInterestPaidAmountString;

    public AdminStatsJson() {
        this.activeDepositCount = 0;
        this.investorCount = 0;

        this.totalActiveDepositAmount = BigDecimal.ZERO;
        this.totalInterestPaidAmount = BigDecimal.ZERO;

        this.totalActiveDepositAmountString = this.totalActiveDepositAmount.toString();
        this.totalInterestPaidAmountString = this.totalInterestPaidAmount.toString();
    }
}
