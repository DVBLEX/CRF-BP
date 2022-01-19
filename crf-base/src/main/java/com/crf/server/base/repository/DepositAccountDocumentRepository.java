package com.crf.server.base.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.crf.server.base.entity.DepositAccountDocument;

public interface DepositAccountDocumentRepository extends PagingAndSortingRepository<DepositAccountDocument, Long> {

    DepositAccountDocument findByCode(String code);

    Page<DepositAccountDocument> findAllByCustomerId(long customerId, Pageable pageable);
}
