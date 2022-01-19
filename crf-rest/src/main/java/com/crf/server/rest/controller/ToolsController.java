package com.crf.server.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/tools", produces = MediaType.APPLICATION_JSON_VALUE)
public class ToolsController {

    private static final String OK_STATUS   = "ok";
    private static final String FAIL_STATUS = "fail";

    @GetMapping("/status")
    public StatusResponse getDBStatus() {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setApplication(OK_STATUS);

        return statusResponse;
    }
}
