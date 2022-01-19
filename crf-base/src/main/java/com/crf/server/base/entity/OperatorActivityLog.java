package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table("operator_activity_log")
@Getter
@Setter
@ToString
public class OperatorActivityLog {

    @Id
    @Column("id")
    private Long   id;

    @Column("operator_id")
    private Long   operatorId;

    @Column("activity_id")
    private Long   activityId;

    @Column("activity_name")
    private String activityName;

    @Column("json")
    private String json;

    @Column("date_created")
    private Date   dateCreated;
}
