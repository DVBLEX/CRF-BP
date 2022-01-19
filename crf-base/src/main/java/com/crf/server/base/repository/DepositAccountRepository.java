package com.crf.server.base.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.crf.server.base.entity.DepositAccount;

public interface DepositAccountRepository extends PagingAndSortingRepository<DepositAccount, Long> {

    List<DepositAccount> findAllByStatusOrderById(int status);

    List<DepositAccount> findAllByCustomerIdOrderByIdDesc(long customerId);

    Page<DepositAccount> findAllByStatusAndBankTransferReferenceContainsOrderByDateCreatedDesc(@Param("status") int status, @Param("bankTransferRef") String bankTransferRef,
        Pageable pageable);

    @Query("SELECT * FROM deposit_accounts depositAccounts WHERE depositAccounts.status = :status AND depositAccounts.date_maturity <= :dateToday")
    List<DepositAccount> findActiveDepositsThatHaveMatured(@Param("status") int status, @Param("dateToday") Date dateToday);

    @Query("SELECT * FROM deposit_accounts depositAccounts WHERE depositAccounts.status = :status AND depositAccounts.date_open < :dateDepositExpiry")
    List<DepositAccount> findInitiatedDepositsThatHaveExpired(@Param("status") int status, @Param("dateDepositExpiry") Date dateDepositExpiry);

    DepositAccount findByBankTransferReference(String bankTransferReference);

    DepositAccount findByAccountNumber(String accountNumber);

    DepositAccount findByCode(String code);

    @Query("SELECT * FROM deposit_accounts depositAccounts ORDER BY depositAccounts.date_created ASC")
    List<DepositAccount> findAllOrderByDateCreated();

    Page<DepositAccount> findAllByCustomerId(long customerId, Pageable pageable);
}
