package com.crf.server.base.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.AdminJson;
import com.crf.server.base.jsonentity.PageInfo;
import com.crf.server.base.repository.OperatorRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class AdminService {

    private OperatorRepository operatorRepository;

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    public PageList<AdminJson> getAdminList(Pageable pageable) throws Exception {

        Page<Operator> adminPage = operatorRepository.findAllByRoleIdAndIsDeletedIsFalse(ServerConstants.OPERATOR_ROLE_ADMIN, pageable);

        List<AdminJson> resultList = new ArrayList<>();

        for (Operator admin : adminPage) {

            AdminJson adminJson = new AdminJson();

            BeanUtils.copyProperties(admin, adminJson);

            try {
                adminJson.setDateCreatedString(ServerUtil.formatDate(ServerConstants.dateFormatddMMyyyy, admin.getDateCreated()));
            } catch (ParseException e) {
                adminJson.setDateCreatedString("");
            }
            resultList.add(adminJson);

        }
        return new PageList<>(resultList, new PageInfo(adminPage.getTotalPages(), adminPage.getTotalElements()));
    }

    @Transactional public void editAdmin(AdminJson adminJson) throws CRFException, CRFValidationException {

        validateLoggedAdmin();

        String code = adminJson.getCode();

        if (StringUtils.isBlank(code)) {

            log.error("AdminService###code is null or blank#edit");

            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "code is null or blank");

        }
        Operator editedAdmin = operatorRepository.findByCode(code);

        if (StringUtils.isBlank(adminJson.getFirstName()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "FirstName");

        if (StringUtils.isBlank(adminJson.getLastName()))
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "LastName");

        editedAdmin.setFirstName(adminJson.getFirstName());

        editedAdmin.setLastName(adminJson.getLastName());

        editedAdmin.setDateEdited(new Date());

        try {
            operatorRepository.save(editedAdmin);

        } catch (Exception e) {

            log.error("AdminService###There is an error in editAdmin method", e);

            throw new CRFException();
        }

        log.info("The information about admin: " + editedAdmin.getUsername() + " was updated");
    }

    @Transactional public void delete(String code) throws CRFException, Exception {

        Operator loggedAdmin = getLoggedAdmin();

        if (StringUtils.isBlank(code)) {

            log.error("AdminService###code is null or blank#delete");

            throw new CRFValidationException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "code is null or blank");

        }

        if (code.equals(loggedAdmin.getCode())) {

            log.error("AdminService###admin trying to remove their own account");

            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "admin trying to remove their own account");
        }

        Operator admin = operatorRepository.findByCode(code);

        try {
                admin.setIsDeleted(true);
                admin.setIsActive(false);
                admin.setIsLocked(true);
                admin.setDateLocked(new Date());

                operatorRepository.save(admin);

            } catch (Exception e) {
                log.error("AdminService###There is an error during delete admin " + admin.getUsername(), e);

                log.info("Admin: " + admin.getUsername() + " was deleted");
            }

    }

    public Operator getLoggedAdmin() throws CRFException {
        Operator loggedAdmin = operatorRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if (loggedAdmin == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedAdmin.getRoleId() != ServerConstants.OPERATOR_ROLE_ADMIN)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not admin");

        return loggedAdmin;
    }

    public void validateLoggedAdmin() throws CRFException {
        Operator loggedAdmin = operatorRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (loggedAdmin == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is null");

        if (loggedAdmin.getRoleId() != ServerConstants.OPERATOR_ROLE_ADMIN)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "loggedOperator is not admin");

    }
}
