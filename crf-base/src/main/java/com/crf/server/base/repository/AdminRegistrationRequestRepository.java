package com.crf.server.base.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crf.server.base.entity.AdminRegistrationRequest;

@Repository
public interface AdminRegistrationRequestRepository extends CrudRepository <AdminRegistrationRequest, Long> {

    @Query("SELECT * FROM admin_registration_requests WHERE email = :email ORDER BY id DESC")
    List<AdminRegistrationRequest> getAdminRequestListByEmail(@Param("email") String email);

}
