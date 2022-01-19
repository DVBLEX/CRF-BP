package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("files")
@Getter
@Setter
@ToString
public class FileEntity {

    @Id
    @Column("id")
    private long     id;

    @Column("code")
    private String   code;

    @Column("customer_id")
    private long     customerId;

    @Column("data")
    private byte[]   data;

    @Column("role")
    private int      role;

    @Column("type")
    private FileType type;

    @Column("mime_type")
    private String   mimeType;

    @Column("entity_type")
    private int      entityType;

    @Column("entity_number")
    private int      entityNumber;

    @Column("operator_id")
    private long     operatorId;

    @Column("date_created")
    private Date     dateCreated;

    @Column("date_edited")
    private Date     dateEdited;

    public enum FileType {

        PDF, JPG, JPEG, JPE, JIF, JFIF, JFI, PNG
    }

}
