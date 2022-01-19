package com.crf.server.base.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("deposit_accounts")
@Getter
@Setter
@ToString
public class DepositAccount {

    @Id
    @Column("id")
    private Long       id;

    @Column("code")
    private String     code;

    @Column("customer_id")
    private Long       customerId;

    @Column("deposit_product_id")
    private Long       depositProductId;

    @Column("account_number")
    private String     accountNumber;

    @Column("amount_deposit")
    private BigDecimal depositAmount;

    @Column("interest_payout_frequency")
    private Integer    interestPayoutFrequency;

    @Column("rate_interest")
    private BigDecimal interestRate;

    @Column("term_years")
    private BigDecimal termYears;

    @Column("status")
    private int        status;

    @Column("bank_transfer_reference")
    private String     bankTransferReference;

    @Column("premature_withdrawal_min_days")
    private Integer    prematureWithdrawalMinDays;

    @Column("premature_withdrawal_rate_interest")
    private BigDecimal prematureWithdrawalInterestRate;

    @Column("fee_withdrawal")
    private BigDecimal withdrawalFee;

    @Column("amount_deposit_withdrawal")
    private BigDecimal depositWithdrawalAmount;

    @Column("amount_interest_earned")
    private BigDecimal interestEarnedAmount;

    @Column("date_open")
    private Date       dateOpen;

    @Column("date_start")
    private Date       dateStart;

    @Column("date_maturity")
    private Date       dateMaturity;

    @Column("date_last_interest_payment")
    private Date       dateLastInterestPayment;

    @Column("date_withdraw_request")
    private Date       dateWithdrawRequest;

    @Column("date_withdraw_approve")
    private Date       dateWithdrawApprove;

    @Column("date_created")
    private Date       dateCreated;
}
