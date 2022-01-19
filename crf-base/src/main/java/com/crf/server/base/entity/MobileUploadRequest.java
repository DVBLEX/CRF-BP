package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("mobile_upload_requests")
@Getter
@Setter
@ToString
public class MobileUploadRequest {

    @Id
    @Column("id")
    private long    id;

    @Column("customer_id")
    private long    customerId;

    @Column("token1")
    private String  token1;

    @Column("token2")
    private String  token2;

    @Column("file_role")
    private int     fileRole;

    @Column("count_request")
    private int     countRequest;

    @Column("is_valid")
    private Boolean isValid;

    @Column("is_completed")
    private Boolean isCompleted;

    @Column("date_completed")
    private Date    dateCompleted;

    @Column("date_last_request")
    private Date    dateLastRequest;

    @Column("date_created")
    private Date    dateCreated;

}
