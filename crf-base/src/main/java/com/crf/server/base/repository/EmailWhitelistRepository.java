package com.crf.server.base.repository;

import com.crf.server.base.entity.EmailWhitelist;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailWhitelistRepository extends CrudRepository<EmailWhitelist, Long> {

    @Query("SELECT count(1) FROM email_whitelist WHERE email = :email")
    long countWhitelistByEmail(@Param("email") String email);

    @Modifying
    @Query("INSERT INTO email_whitelist(email, date_created) VALUES (:email, now())")
    void whitelistEmail(@Param("email") String email);
}
