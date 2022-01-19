package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("bank_accounts")
@Getter
@Setter
@ToString
public class BankAccount {

    @Id
    @Column("id")
    private Long   id;

    @Column("code")
    private String code;

    @Column("customer_id")
    private Long   customerId;

    @Column("bank_name")
    private String bankName;

    @Column("bank_account_name")
    private String bankAccountName;

    @Column("bank_address")
    private String bankAddress;

    @Column("iban")
    private String iban;

    @Column("bic")
    private String bic;

    @Column("date_created")
    private Date   dateCreated;

    @Column("date_edited")
    private Date   dateEdited;
}
