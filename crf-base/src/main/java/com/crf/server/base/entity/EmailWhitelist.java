package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table("email_whitelist")
@Getter
@Setter
@ToString
public class EmailWhitelist {

    @Id
    @Column("id")
    private Long   id;

    @Column("email")
    private String email;

    @Column("date_created")
    private Date   dateCreated;
}
