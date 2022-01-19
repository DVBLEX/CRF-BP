package com.crf.server.base.repository;

import com.crf.server.base.entity.SmsLog;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface SmsLogRepository extends CrudRepository<SmsLog, Long> {
    @Modifying
    @Query("UPDATE sms_log SET is_processed = :isProcessed, date_scheduled = :dateScheduled, retry_count = :retryCount,"
         + " date_processed = :dateProcessed, transaction_id = :transactionId, response_code = :responseCode,"
         + " response_text = :responseText WHERE id = :id")
    void updateSmsLog(@Param("isProcessed") int isProcessed, @Param("dateScheduled") Date dateScheduled, @Param("retryCount") int retryCount,
                      @Param("dateProcessed") Date dateProcessed, @Param("transactionId") long transactionId,
                      @Param("responseCode") int responseCode, @Param("responseText") String responseText, @Param("id") long id);
}
