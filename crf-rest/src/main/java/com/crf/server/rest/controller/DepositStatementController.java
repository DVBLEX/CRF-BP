package com.crf.server.rest.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.entity.PageList;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.jsonentity.DepositStatementJson;
import com.crf.server.base.service.DepositStatementService;
import com.crf.server.base.service.OperatorService;

@RestController
@RequestMapping("/depositstatement")
public class DepositStatementController {

    private DepositStatementService depositStatementService;
    private OperatorService         operatorService;

    @Autowired
    public void setDepositStatementService(DepositStatementService depositStatementService) {
        this.depositStatementService = depositStatementService;
    }

    @Autowired
    public void setOperatorService(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseJsonEntity listDepositStatements(HttpServletResponse response, Pageable pageable) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        Operator loggedInvestor = operatorService.getLoggedInvestor();

        PageList<DepositStatementJson> depositStatementsList = depositStatementService.getDepositStatementsByCustomerId(pageable, loggedInvestor.getCustomerId());

        apiResponse.setDataList(depositStatementsList.getDataList());
        apiResponse.setPage(depositStatementsList.getPage());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        apiResponse.setResponseDate(new Date());

        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }
}
