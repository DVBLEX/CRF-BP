package com.crf.server.base.repository;

import com.crf.server.base.entity.BlockedEmailDomain;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedEmailDomainRepository extends CrudRepository<BlockedEmailDomain, Long> {

    @Query("SELECT COUNT(1) FROM blocked_email_domains WHERE :email REGEXP CONCAT('^.+\\\\@', CONCAT(name, '\\\\..+$'))")
    long countEmailBlockedDomain(@Param("email") String email);
}
