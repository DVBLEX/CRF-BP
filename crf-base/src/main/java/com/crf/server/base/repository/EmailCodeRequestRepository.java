package com.crf.server.base.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.crf.server.base.entity.EmailCodeRequest;

public interface EmailCodeRequestRepository extends CrudRepository<EmailCodeRequest, Long> {
    EmailCodeRequest findEmailCodeRequestsByEmail(String email);

    long countEmailCodeRequestByEmail(String email);

    @Modifying
    @Query("DELETE FROM email_code_requests WHERE email = :email")
    void deleteEmailCodeRequestByEmail(@Param("email") String email);

    @Modifying
    @Query("INSERT INTO email_code_requests (email, code, count_verified, count_code_sent, date_code_sent, date_created) VALUES (:emailTo, :code, 0, 1, now(), now())")
    void createEmailCodeRequest(@Param("emailTo") String emailTo, @Param("code") String code);

    @Query("SELECT COUNT(1) FROM registration_requests request WHERE request.email = :email AND request.token1 = :token1 AND request.token2 = :token2 AND request.date_created IS NOT NULL"
        + " AND request.date_created > SUBDATE( CURRENT_TIMESTAMP, INTERVAL :hours HOUR )")
    long countByEmailAndTokens(@Param("email") String email, @Param("token1") String token1, @Param("token2") String token2, @Param("hours") int hours);

    @Query("SELECT COUNT(1) FROM email_code_requests WHERE email = :email AND code = code AND date_verified IS NULL")
    long countNotVerifiedByEmailAndCode(@Param("email") String email, @Param("code") String code);

    @Query("SELECT COUNT(1) FROM email_code_requests WHERE email = :email AND token = :token AND date_verified IS NOT NULL AND date_verified > SUBDATE( CURRENT_TIMESTAMP, INTERVAL :hours HOUR )")
    long countEmailVerifiedWithinHours(@Param("email") String email, @Param("token") String token, @Param("hours") int hours);

    @Query("SELECT COUNT(1) FROM email_code_requests WHERE email = :email AND count_code_sent >= :codeSentLimit ")
    long countEmailCodeSentUnderLimit(@Param("email") String email, @Param("codeSentLimit") int codeSentLimit);

    @Query("SELECT COUNT(1) FROM email_code_requests WHERE email = :email AND count_verified >= :verifiedLimit ")
    long countEmailVerifiedUnderLimit(@Param("email") String email, @Param("verifiedLimit") int verifiedLimit);

    @Query("SELECT COUNT(1) FROM sms_code_requests WHERE msisdn = :msisdn AND code = :code AND date_verified IS NULL")
    long countVerifyRegistrationCodeSMS(@Param("msisdn") String msisdn, @Param("code") String code);

    @Query("SELECT COUNT(1) FROM sms_code_requests WHERE msisdn = :msisdn AND token = :token AND date_verified IS NOT NULL AND date_verified > SUBDATE( CURRENT_TIMESTAMP, INTERVAL :hours HOUR )")
    long countMsisdnVerifiedWithinHours(@Param("msisdn") String msisdn, @Param("token") String token, @Param("hours") int hours);

    @Query("SELECT COUNT(1) FROM sms_code_requests WHERE msisdn = :msisdn AND count_code_sent >= :codeSentLimit ")
    long countSmsCodeSentUnderLimit(@Param("msisdn") String msisdn, @Param("codeSentLimit") int codeSentLimit);

    @Query("SELECT COUNT(1) FROM sms_code_requests WHERE msisdn = :msisdn AND count_verified >= :verifiedLimit ")
    long countMsisdnVerifiedUnderLimit(@Param("msisdn") String msisdn, @Param("verifiedLimit") int verifiedLimit);

    @Modifying
    @Query("UPDATE email_code_requests SET code = :code, date_verified = NULL, date_code_sent = now(), count_code_sent = count_code_sent + 1 WHERE email = :email")
    void updateEmailRequestCodeByEmail(@Param("code") String code, @Param("email") String email);

    @Modifying
    @Query("UPDATE email_code_requests SET date_verified = now(), token = :token, count_verified = count_verified + 1 WHERE code = :code AND email = :email")
    void updateEmailCodeRequestSetEmailToVerified(@Param("token") String token, @Param("code") String code, @Param("email") String email);

    @Query("SELECT COUNT(1) FROM admin_registration_requests request WHERE request.email = :email AND request.token1 = :token1 AND request.token2 = :token2 AND request.date_created IS NOT NULL"
            + " AND request.date_created > SUBDATE( CURRENT_TIMESTAMP, INTERVAL :hours HOUR )")
    long countAdminRequestByEmailAndTokens(@Param("email") String email, @Param("token1") String token1, @Param("token2") String token2, @Param("hours") int hours);
}
