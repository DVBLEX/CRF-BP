package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("registration_requests")
@Getter
@Setter
@ToString
public class RegistrationRequest {

    @Id
    @Column("id")
    private long   id;

    // customer type
    @Column("type")
    private int    type;

    // customer category
    @Column("category")
    private int    category;

    @Column("title")
    private String title;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("email")
    private String email;

    @Column("token1")
    private String token1;

    @Column("token2")
    private String token2;

    @Column("date_created")
    private Date   dateCreated;

}
