package com.crf.server.base.repository;

import com.crf.server.base.entity.OperatorActivityLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatorActivityLogRepository extends CrudRepository<OperatorActivityLog, Long> {

}
