package com.crf.server.base.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.crf.server.base.entity.DepositStatement;

public interface DepositStatementRepository extends PagingAndSortingRepository<DepositStatement, Long> {

    @Query("SELECT amount_balance FROM deposit_statements WHERE deposit_account_id = :depositAccountId ORDER BY id DESC LIMIT 1")
    BigDecimal getAmountBalanceLast(@Param("depositAccountId") long depositAccountId);

    Page<DepositStatement> findAllByCustomerId(long customerId, Pageable pageable);

}
