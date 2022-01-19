package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("verification_denial_reasons")
@Getter
@Setter
@ToString
public class VerificationDenialReason {

    @Id
    @Column("id")
    private long   id;

    @Column("description")
    private String description;

    @Column("question")
    private String question;

    @Column("is_poid_related")
    private Boolean isPOIDRelated;

    @Column("is_poa_related")
    private Boolean isPOARelated;

    @Column("is_photo_related")
    private Boolean isPhotoRelated;

    @Column("date_created")
    private Date   dateCreated;

}
