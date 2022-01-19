package com.crf.server.base.repository;

import com.crf.server.base.entity.SystemTimerTask;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SystemTimerTaskRepository extends CrudRepository<SystemTimerTask, Long> {

    @Modifying
    @Query("UPDATE system_timer_tasks SET date_last_run = :dateLastRun WHERE id = :timerTaskId ")
    long updateDateLastRun(@Param("dateLastRun") Date dateLastRun, @Param("timerTaskId") long timerTaskId);
}
