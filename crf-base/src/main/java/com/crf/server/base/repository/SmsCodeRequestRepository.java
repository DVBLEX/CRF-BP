package com.crf.server.base.repository;

import com.crf.server.base.entity.SmsCodeRequest;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SmsCodeRequestRepository extends CrudRepository<SmsCodeRequest, Long> {

    SmsCodeRequest findSmsCodeRequestsByMsisdn(String msisdn);

    long countSmsCodeRequestByMsisdn(String msisdn);

    long countSmsCodeRequestByMsisdnAndCode(String msisdn, String code);

    @Modifying
    @Query("UPDATE sms_code_requests SET code = :code, date_verified = NULL, date_code_sent = now(), count_code_sent = count_code_sent + 1 WHERE msisdn = :msisdn")
    void updateSmsRequestCodeByMsisdn(@Param("code") String code, @Param("msisdn") String msisdn);

    @Modifying
    @Query("UPDATE sms_code_requests SET date_verified = now(), token = :token, count_verified = count_verified + 1 WHERE code = :code AND msisdn = :msisdn")
    void updateMsisdnToVerified(@Param("token") String token, @Param("code") String code, @Param("msisdn") String msisdn);

    @Modifying
    @Query("INSERT INTO sms_code_requests (msisdn, code, count_verified, count_code_sent, date_code_sent, date_created) VALUES (:msisdn, :code, 0, 1, now(), now())")
    void createSmsCodeRequest(@Param("msisdn") String msisdn, @Param("code") String code);

    @Modifying
    @Query("DELETE FROM sms_code_requests WHERE msisdn = :msisdn")
    void deleteSmsCodeRequestByMsisdn(@Param("msisdn") String msisdn);
}
