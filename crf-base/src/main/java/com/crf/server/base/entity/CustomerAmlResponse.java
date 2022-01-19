package com.crf.server.base.entity;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("customer_aml_responses")
@Getter
@Setter
@ToString
public class CustomerAmlResponse {

    @Id
    @Column("id")
    private long   id;

    @Column("customer_id")
    private long   customerId;

    @Column("aml_scan_response")
    private String AmlScanResponse;

    @Column("number_of_matches")
    private int numberOfMatches;

    @Column("date_created")
    private Date   dateCreated;

}
