package com.crf.server.base.repository;

import com.crf.server.base.entity.SmsScheduler;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SmsSchedulerRepository extends CrudRepository<SmsScheduler, Long> {
    @Query("SELECT ss.id, ss.type, ss.config_id, ss.customer_id, ss.template_id, ss.msisdn, ss.source_addr, ss.message,"
         + " ss.date_scheduled, ss.retry_count FROM sms_scheduler ss WHERE ss.date_scheduled <= now()"
         + " AND ss.is_processed = :isProcessed ORDER BY ss.priority, ss.id LIMIT 3")
    List<SmsScheduler> findByIsProcessedTillNow(@Param("isProcessed") int isProcessed);

    @Modifying
    @Query("UPDATE sms_scheduler SET is_processed = :isProcessed WHERE id = :smsId")
    void updateIsProcessed(@Param("smsId") long smsId, @Param("isProcessed") int isProcessed);

    @Modifying
    @Query("UPDATE sms_scheduler SET date_scheduled = now(), retry_count = retry_count + 1 WHERE id = :smsId")
    void updateRetryCount(@Param("smsId") long smsId);
    
    @Modifying
    @Query("UPDATE sms_scheduler SET is_processed = :isProcessed, date_scheduled = :dateScheduled, retry_count = :retryCount,"
         + " date_processed = :dateProcessed, transaction_id = :transactionId, response_code = :responseCode, response_text = :responseText WHERE id = :id")
    void updateSmsScheduler(@Param("isProcessed") int isProcessed, @Param("dateScheduled") Date dateScheduled, @Param("retryCount") int retryCount,
        @Param("dateProcessed") Date dateProcessed, @Param("transactionId") long transactionId,
        @Param("responseCode") int responseCode, @Param("responseText") String responseText, @Param("id")  long id);

    @Modifying
    @Query("INSERT INTO sms_scheduler(id, is_processed, type, config_id, customer_id, template_id, priority, msisdn, source_addr, message, channel, "
            + "date_created, date_scheduled, retry_count, date_processed, transaction_id, response_code, response_text) SELECT id, is_processed, type, config_id, customer_id, template_id, priority, "
            + "msisdn, source_addr, message, channel, date_created, date_scheduled, retry_count, date_processed, transaction_id, response_code, response_text FROM sms_log WHERE id = :smsId")
    void scheduleSms(@Param("smsId") long smsId);
}
