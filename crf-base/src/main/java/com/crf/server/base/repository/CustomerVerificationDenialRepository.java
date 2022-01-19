package com.crf.server.base.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.crf.server.base.entity.CustomerVerificationDenial;
import org.springframework.data.repository.query.Param;

public interface CustomerVerificationDenialRepository extends CrudRepository<CustomerVerificationDenial, Long> {

    List<CustomerVerificationDenial> findAllByCustomerId(long customerId);

    @Modifying
    @Query("DELETE FROM customer_verification_denials WHERE customer_id = :customerId")
    void deleteByCustomerId(@Param("customerId") long customerId);

}
