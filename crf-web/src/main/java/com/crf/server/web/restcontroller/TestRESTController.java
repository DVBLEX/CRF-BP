package com.crf.server.web.restcontroller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.apachecommons.CommonsLog;

@RestController
@CommonsLog
public class TestRESTController {

    @Value("${tc.system.name}")
    String systemName;

    @Value("${tc.system.environment}")
    String systemEnvironment;

    @RequestMapping("/test")
    public String index() {

        // this is a test API

        log.trace("trace###TestRESTController.index call");
        log.debug("debug###TestRESTController.index call");
        log.info("info###TestRESTController.index call");
        log.warn("warn###TestRESTController.index call");
        log.error("error###TestRESTController.index call");
        log.fatal("fatal###TestRESTController.index call");

        return "Greetings from Spring Boot! We are running " + systemName + " in " + systemEnvironment + " mode!";
    }
}
