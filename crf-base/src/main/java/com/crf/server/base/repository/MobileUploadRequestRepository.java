package com.crf.server.base.repository;

import com.crf.server.base.entity.MobileUploadRequest;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MobileUploadRequestRepository extends CrudRepository<MobileUploadRequest, Long> {

    MobileUploadRequest findByCustomerIdAndFileRole(long customerId, int fileRole);

    MobileUploadRequest findByCustomerIdAndToken1AndToken2(long customerId, String token1, String token2);

    @Modifying
    @Query("UPDATE mobile_upload_requests SET is_valid = 0 WHERE customer_id = :customerId AND file_role = :fileRole")
    void cancelMobileUploadRequestByCustomer(@Param("customerId") long customerId, @Param("fileRole") int fileRole);

    @Query("SELECT COUNT(1) FROM mobile_upload_requests request INNER JOIN customers ON customers.id = request.customer_id"
        + " WHERE request.is_completed = 0 AND request.is_valid = 1 AND customers.email = :email AND request.token1 = :token1 AND request.token2 = :token2"
        + " AND request.date_last_request > SUBDATE( CURRENT_TIMESTAMP, INTERVAL :minutes MINUTE )")
    long countMobileUploadRequestsWithinMinutes(@Param("email") String email, @Param("token1") String token1, @Param("token2") String token2, @Param("minutes") int minutes);

    @Query("SELECT COUNT(1) FROM mobile_upload_requests request INNER JOIN customers ON customers.id = request.customer_id"
        + " WHERE request.is_completed = 1 AND customers.email = :email AND request.file_role = :fileRole ")
    long countMobileUploadRequestsCompleted(@Param("email") String email, @Param("fileRole") int fileRole);

    @Query("SELECT request.file_role FROM mobile_upload_requests request INNER JOIN customers ON customers.id = request.customer_id"
        + " WHERE customers.email = :email AND request.token1 = :token1 AND request.token2 = :token2 ")
    int findFileRoleByEmailAndTokens(@Param("email") String email, @Param("token1") String token1, @Param("token2") String token2);
}
