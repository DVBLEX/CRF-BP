package com.crf.server.base.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("deposit_products")
@Getter
@Setter
@ToString
public class DepositProduct {

    @Id
    @Column("id")
    private Long       id;

    @Column("code")
    private String     code;

    @Column("name")
    private String     name;

    @Column("description")
    private String     description;

    @Column("yearly_rate_interest")
    private BigDecimal yearlyInterestRate;

    @Column("twice_yearly_rate_interest")
    private BigDecimal twiceYearlyInterestRate;

    @Column("quarterly_rate_interest")
    private BigDecimal quarterlyInterestRate;

    @Column("term_years")
    private BigDecimal termYears;

    @Column("amount_deposit_min")
    private BigDecimal depositMinAmount;

    @Column("amount_deposit_max")
    private BigDecimal depositMaxAmount;

    @Column("premature_withdrawal_min_days")
    private Integer    prematureWithdrawalMinDays;

    @Column("premature_withdrawal_rate_interest")
    private BigDecimal prematureWithdrawalInterestRate;

    @Column("fee_withdrawal")
    private BigDecimal withdrawalFee;
}
