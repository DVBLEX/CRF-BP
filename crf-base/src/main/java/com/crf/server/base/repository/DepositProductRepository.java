package com.crf.server.base.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.crf.server.base.entity.DepositProduct;

public interface DepositProductRepository extends PagingAndSortingRepository<DepositProduct, Long> {

    DepositProduct findByCode(String code);
}
