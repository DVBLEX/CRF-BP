package com.crf.server.base.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.crf.server.base.entity.CustomerAmlResponse;

@Repository
public interface AMLScanCustomerRepository extends CrudRepository<CustomerAmlResponse, Long> {
    CustomerAmlResponse findByCustomerId(Long customerId);
}
