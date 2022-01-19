package com.crf.server.base.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crf.server.base.entity.Operator;

@Repository
public interface OperatorRepository extends CrudRepository<Operator, Long> {

    Operator findByEmail(String userName);

    Operator findByUsername(String username);

    Operator findByCustomerId(long customerId);

    Operator findByCode(String code);

    @Query("SELECT COUNT(1) FROM operators WHERE email = :email AND count_passwd_forgot_requests >= :passwordForgotEmailLimit AND is_deleted = 0")
    long countEmailForgotPasswordUnderLimit(@Param("email") String email, @Param("passwordForgotEmailLimit") int passwordForgotEmailLimit);

    @Query("SELECT COUNT(1) FROM operators WHERE email = :email AND is_deleted = 0")
    long countEmailRegisteredAlready(@Param("email") String email);

    @Query("SELECT COUNT(1) FROM operators WHERE msisdn = :msisdn AND is_deleted = 0")
    long countMsisdnRegisteredAlready(@Param("msisdn") String msisdn);

    @Query("SELECT email FROM operators")
    Set<String> getRegisteredEmailSet();

    Page<Operator> findAllByRoleIdAndIsDeletedIsFalse(int role, Pageable pageable);
}
