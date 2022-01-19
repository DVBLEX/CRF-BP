package com.crf.server.base.repository;

import com.crf.server.base.entity.EmailLog;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface EmailLogRepository extends CrudRepository<EmailLog, Long> {
    @Modifying
    @Query("UPDATE email_log SET is_processed = :isProcessed, date_scheduled = :dateScheduled, retry_count = :retryCount,"
         + "date_processed = :dateProcessed, response_code = :responseCode, response_text = :responseText WHERE id = :id")
    void updateEmailLog(@Param("isProcessed") int isProcessed, @Param("dateScheduled") Date dateScheduled, @Param("retryCount") int retryCount,
        @Param("dateProcessed") Date dateProcessed, @Param("responseCode") int responseCode, @Param("responseText") String responseText, @Param("id") long id);
}
