package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table("blocked_email_domains")
@Getter
@Setter
@ToString
public class BlockedEmailDomain {

    @Id
    @Column("id")
    private Long   id;

    @Column("name")
    private String name;

    @Column("date_created")
    private Date   dateCreated;
}
