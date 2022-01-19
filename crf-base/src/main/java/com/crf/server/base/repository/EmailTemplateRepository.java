package com.crf.server.base.repository;

import org.springframework.data.repository.CrudRepository;

import com.crf.server.base.entity.EmailTemplate;

public interface EmailTemplateRepository extends CrudRepository<EmailTemplate, Long> {

}
