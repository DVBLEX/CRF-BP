package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table("system_timer_task")
@Getter
@Setter
@ToString
public class SystemTimerTask {

    @Id
    @Column("id")
    private Long   id;

    @Column("name")
    private String name;

    @Column("date_last_run")
    private Date   dateLastRun;

    @Column("type")
    private String type;

    @Column("period")
    private String period;

    @Column("application")
    private String application;
}
