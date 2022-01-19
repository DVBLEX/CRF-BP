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
public class InvestorStatsJson {

    private int        activeDepositCount;
    private int        initiatedDepositCount;

    private BigDecimal totalActiveDepositAmount;
    private BigDecimal totalInterestEarnedAmount;
    private BigDecimal totalAccruedInterestAmount;

    private String     totalActiveDepositAmountString;
    private String     totalInterestEarnedAmountString;
    private String     totalAccruedInterestAmountString;

    public InvestorStatsJson() {
        this.activeDepositCount = 0;
        this.initiatedDepositCount = 0;

        this.totalActiveDepositAmount = BigDecimal.ZERO;
        this.totalInterestEarnedAmount = BigDecimal.ZERO;
        this.totalAccruedInterestAmount = BigDecimal.ZERO;

        this.totalActiveDepositAmountString = this.totalActiveDepositAmount.toString();
        this.totalInterestEarnedAmountString = this.totalInterestEarnedAmount.toString();
        this.totalAccruedInterestAmountString = this.totalAccruedInterestAmount.toString();
    }
}
