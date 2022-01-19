package com.crf.server.base.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.crf.server.base.entity.BankAccount;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

    BankAccount findByCustomerId(Long customerId);
}
