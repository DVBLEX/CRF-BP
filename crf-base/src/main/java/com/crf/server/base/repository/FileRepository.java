package com.crf.server.base.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.crf.server.base.entity.FileEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends CrudRepository<FileEntity, Long> {

    FileEntity findByCode(String code);

    FileEntity findByCustomerIdAndRole(long customerId, int role);

    List<FileEntity> findListByCustomerIdAndRole(long customerId, int role);

    FileEntity findByIdAndRole(long id, int role);

    @Modifying
    @Query("DELETE FROM files WHERE customer_id = :customerId AND role = :role AND entity_number = :entityNumber")
    void deleteAllByCustomerIdAndRoleAndEntityNumber(@Param("customerId") long customerId, @Param("role") int role, @Param("entityNumber") int entityNumber);
}
