package com.crf.server.base.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.crf.server.base.entity.DepositAccountPayment;

public interface DepositAccountPaymentRepository extends PagingAndSortingRepository<DepositAccountPayment, Long> {

    @Query("SELECT * FROM deposit_account_payments depositAccountsPayments ORDER BY depositAccountsPayments.id DESC")
    List<DepositAccountPayment> findAllOrderByIdDesc();

    DepositAccountPayment findByCode(String code);

    Page<DepositAccountPayment> findAll(Pageable pageable);
}
