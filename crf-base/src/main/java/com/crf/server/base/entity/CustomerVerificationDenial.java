package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("customer_verification_denials")
@Getter
@Setter
@ToString
public class CustomerVerificationDenial {

    @Id
    @Column("id")
    private long   id;

    @Column("customer_id")
    private long   customerId;

    @Column("reason_id")
    private long   verificationDenialReasonId;

    @Column("additional_description")
    private String additionalDescription;

    @Column("date_created")
    private Date   dateCreated;
}
