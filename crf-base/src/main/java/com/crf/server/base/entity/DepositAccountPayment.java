package com.crf.server.base.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("deposit_account_payments")
@Getter
@Setter
@ToString
public class DepositAccountPayment {

    @Id
    @Column("id")
    private Long       id;

    @Column("code")
    private String     code;

    @Column("deposit_account_id")
    private Long       depositAccountId;

    @Column("account_number")
    private String     accountNumber;

    @Column("interest_payout_frequency")
    private Integer    interestPayoutFrequency;

    @Column("customer_id")
    private Long       customerId;

    @Column("customer_name")
    private String     customerName;

    @Column("amount")
    private BigDecimal amount;

    @Column("operator_id")
    private Long       operatorId;

    @Column("is_processed")
    private Boolean    isProcessed;

    @Column("date_processed")
    private Date       dateProcessed;

    @Column("date_period_from")
    private Date       datePeriodFrom;

    @Column("date_period_to")
    private Date       datePeriodTo;

    @Column("date_created")
    private Date       dateCreated;
}
