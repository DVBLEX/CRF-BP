package com.crf.server.base.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.crf.server.base.entity.SystemParameter;

@Repository
public interface SystemParameterRepository extends CrudRepository<SystemParameter, Long> {

}
