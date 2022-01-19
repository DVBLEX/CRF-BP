package com.crf.server.base.repository;

import com.crf.server.base.entity.EmailScheduler;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface EmailSchedulerRepository extends CrudRepository<EmailScheduler, Long> {
    @Query("SELECT es.id, es.type, es.config_id, es.customer_id, es.template_id, es.email_to, es.email_bcc, es.subject, es.message, es.attachment_path, es.date_scheduled, es.retry_count "
        + "FROM email_scheduler es WHERE  es.date_scheduled <= now() AND es.is_processed = :isProcessed ORDER BY es.priority, es.id LIMIT 3")
    List<EmailScheduler> findByIsProcessedTillNow(@Param("isProcessed") int isProcessed);

    @Modifying
    @Query("INSERT INTO email_scheduler(id, is_processed, type, config_id, customer_id, template_id, priority, email_to, email_reply_to, email_bcc, subject, message, channel, attachment_path, date_created, "
        + "date_scheduled, retry_count, date_processed, response_code, response_text) "
        + "SELECT id, is_processed, type, config_id, customer_id, template_id, priority, email_to, email_reply_to, email_bcc, subject, :message, channel, attachment_path, date_created, "
        + "date_scheduled, retry_count, date_processed, response_code, response_text FROM email_log WHERE id = :emailId")
    void scheduleEmail(@Param("message") String message, @Param("emailId") long emailId);

    @Modifying
    @Query("UPDATE email_scheduler SET is_processed = :isProcessed WHERE id = :emailId")
    void updateIsProcessed(@Param("emailId") long emailId, @Param("isProcessed") int isProcessed);

    @Modifying
    @Query("UPDATE email_scheduler SET date_scheduled = now(), retry_count = retry_count + 1 WHERE id = :emailId")
    void updateRetryCount(@Param("emailId") long emailId);
    
    @Modifying
    @Query("UPDATE email_scheduler SET is_processed = :isProcessed, date_scheduled = :dateScheduled, retry_count = :retryCount, date_processed = :dateProcessed,"
         + " response_code = :responseCode, response_text = :responseText WHERE id = :id")
    void updateEmailScheduler(@Param("isProcessed") int isProcessed, @Param("dateScheduled") Date dateScheduled, @Param("retryCount") int retryCount,
        @Param("dateProcessed") Date dateProcessed, @Param("responseCode") int responseCode, @Param("responseText") String responseText, @Param("id")  long id);
}
