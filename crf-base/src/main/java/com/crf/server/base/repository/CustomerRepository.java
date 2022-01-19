package com.crf.server.base.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crf.server.base.entity.Customer;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {

    Customer findByEmail(String email);

    Customer findByCode(String code);

    Page<Customer> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT COUNT(1) FROM customers customers WHERE customers.type = :type AND customers.is_passport_scan_verified = 1 AND customers.is_deleted = 0")
    int countActiveCustomersByType(@Param("type") int type);
}
