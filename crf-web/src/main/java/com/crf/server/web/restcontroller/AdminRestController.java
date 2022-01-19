package com.crf.server.web.restcontroller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.AdminJson;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminRestController {
    private AdminService adminService;

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping("/list")
    public ApiResponseJsonEntity getAdminList(HttpServletResponse response, @RequestParam(value = "page") int page, @RequestParam(value = "size") int size)
        throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        PageList<AdminJson> adminList = adminService.getAdminList(ServerUtil.createDefaultPageRequest(page, size));

        apiResponse.setDataList(adminList.getDataList());
        apiResponse.setPage(adminList.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) public ApiResponseJsonEntity edit(
        HttpServletResponse response, @RequestBody AdminJson adminJson) throws Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        adminService.editAdmin(adminJson);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST) public ApiResponseJsonEntity delete(HttpServletResponse response, @RequestParam("code") String code)
        throws Exception {
        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        adminService.delete(code);

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
