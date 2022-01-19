package com.crf.server.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin("http://localhost:4200")
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home(Model model) {
        return "forward:/index.html";
    }

    @GetMapping("/login-page")
    public String goToLoginPage() {
        return "forward:/index.html";
    }
}
