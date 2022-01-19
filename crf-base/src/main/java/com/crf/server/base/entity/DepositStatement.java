package com.crf.server.base.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("deposit_statements")
@Getter
@Setter
@ToString
public class DepositStatement {

    @Id
    @Column("id")
    private Long       id;

    @Column("code")
    private String     code;

    @Column("type")
    private Integer    type;

    @Column("description")
    private String     description;

    @Column("deposit_account_id")
    private Long       depositAccountId;

    @Column("account_number")
    private String     accountNumber;

    @Column("customer_id")
    private Long       customerId;

    @Column("amount_transaction")
    private BigDecimal amountTransaction;

    @Column("amount_balance")
    private BigDecimal amountBalance;

    @Column("date_created")
    private Date       dateCreated;
}
