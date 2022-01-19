package com.crf.server.rest.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.AdminJson;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.service.AdminService;

@RestController
@RequestMapping(value = "admin")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity getAdminList(HttpServletResponse response, Pageable pageable) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        PageList<AdminJson> adminList = adminService.getAdminList(pageable);

        apiResponse.setDataList(adminList.getDataList());
        apiResponse.setPage(adminList.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @PutMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity edit(HttpServletResponse response, @RequestBody AdminJson adminJson) throws Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        adminService.editAdmin(adminJson);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @DeleteMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity delete(HttpServletResponse response, @RequestParam("code") String code) throws Exception {
        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        adminService.delete(code);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
