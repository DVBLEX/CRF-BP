package com.crf.server.api.restcontrollers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crf.server.base.common.ServerConstants;

@RestController
public class ApiRESTController {

    @RequestMapping("/api")
    public String apiIndex() {
        return "Greetings from Spring Boot API! We are running in " + ServerConstants.SYSTEM_ENVIRONMENT_LOCAL + " mode!";
    }
}
