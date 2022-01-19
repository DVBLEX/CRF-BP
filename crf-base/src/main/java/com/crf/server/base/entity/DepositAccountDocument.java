package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("deposit_account_documents")
@Getter
@Setter
@ToString
public class DepositAccountDocument {

    @Id
    @Column("id")
    private Long    id;

    @Column("code")
    private String  code;

    @Column("type")
    private Integer type;

    @Column("customer_id")
    private Long    customerId;

    @Column("deposit_account_id")
    private Long    depositAccountId;

    @Column("deposit_product_id")
    private Long    depositProductId;

    @Column("deposit_account_payment_id")
    private Long    depositAccountPaymentId;

    @Column("account_number")
    private String  accountNumber;

    @Column("path")
    private String  path;

    @Column("date_created")
    private Date    dateCreated;

}
