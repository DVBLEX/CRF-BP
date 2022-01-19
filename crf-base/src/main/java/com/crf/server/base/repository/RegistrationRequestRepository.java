package com.crf.server.base.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.crf.server.base.entity.RegistrationRequest;

public interface RegistrationRequestRepository extends CrudRepository<RegistrationRequest, Long> {

    List<RegistrationRequest> findByEmailOrderByIdDesc(String email);
}
