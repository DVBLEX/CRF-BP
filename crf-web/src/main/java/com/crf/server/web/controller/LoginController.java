package com.crf.server.web.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.repository.CustomerRepository;
import com.crf.server.base.repository.OperatorRepository;
import com.crf.server.base.service.AdminRegistrationService;
import com.crf.server.base.service.OperatorService;
import com.crf.server.base.service.RegistrationService;
import com.crf.server.base.service.SystemService;

import lombok.extern.apachecommons.CommonsLog;

@Controller
@CommonsLog
public class LoginController {

    private static final ZoneId currentZone = ZoneId.systemDefault();

    private CustomerRepository  customerRepository;
    private OperatorRepository operatorRepository;
    private OperatorService     operatorService;
    private RegistrationService registrationService;
    private SystemService       systemService;
    private AdminRegistrationService adminRegistrationService;

    @Value("${tc.google.recaptcha.key.site}")
    private String              recaptchKeySite;

    @Value("${tc.login.failure}")
    private String              loginFailure;

    @Value("${tc.login.logout}")
    private String              loginLogout;

    @Value("${tc.login.denied}")
    private String              loginDenied;

    @Value("${tc.login.deleted}")
    private String              loginDeleted;

    @Value("${tc.login.locked}")
    private String              loginLocked;

    @Value("${tc.login.credentialsUpdated}")
    private String              loginCredentialsUpdated;

    @Value("${tc.system.environment}")
    private String              systemEnvironment;

    @Value("${tc.system.name}")
    private String              systemName;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setOperatorService(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Autowired
    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Autowired
    public void setAdminRegistrationService(AdminRegistrationService adminRegistrationService) {
        this.adminRegistrationService = adminRegistrationService;
    }

    @RequestMapping(value = "/login.html", method = RequestMethod.GET)
    public String login(Model model, @RequestParam(value = "failure", required = false) String error, @RequestParam(value = "logout", required = false) String logout,
        @RequestParam(value = "denied", required = false) String denied, @RequestParam(value = "locked", required = false) String locked,
                        @RequestParam(value = "deleted", required = false) String deleted,
        @RequestParam(value = "credentialsUpdated", required = false) String credentialsUpdated) {

        if (error != null) {
            model.addAttribute("failure", loginFailure);
        } else if (logout != null) {
            model.addAttribute("logout", loginLogout);
        } else if (denied != null) {
            model.addAttribute("denied", loginDenied);
        } else if (deleted != null) {
            model.addAttribute("deleted", loginDeleted);
        } else if (locked != null) {
            model.addAttribute("locked", loginLocked);
        } else if (credentialsUpdated != null) {
            model.addAttribute("credentialsUpdated", loginCredentialsUpdated);
        }

        model.addAttribute("environment", systemEnvironment);
        model.addAttribute("appName", systemName);

        return "login";
    }

    @RequestMapping(value = "/registration.html", method = RequestMethod.GET)
    public String register(Model model, @RequestParam(value = "u") String userName, @RequestParam(value = "ti") String title, @RequestParam(value = "fn") String firstName,
        @RequestParam(value = "ln") String lastName, @RequestParam(value = "t") String token1, @RequestParam(value = "t2") String token2) {

        if (!userName.matches(ServerConstants.REGEXP_EMAIL) || !token1.matches(ServerConstants.REGEX_SHA256) || !token2.matches(ServerConstants.REGEX_SHA256))
            // The details are invalid. It might be a hacking attempt.
            return "redirect:login.html?denied";

        try {
            Customer customer = customerRepository.findByEmail(userName);
            if (customer != null && !customer.getIsDeleted())
                // the customer has registered and tried to open the registration page again. In that case redirect to the login page
                return "redirect:login.html";

            if (registrationService.isRegistrationRequestAllowed(userName, token1, token2)) {

                model.addAttribute("recaptchaKey", "'" + recaptchKeySite + "'");
                model.addAttribute("email", userName);
                model.addAttribute("title", title);
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);

                if (systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_LOCAL) || systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_DEV)) {
                    model.addAttribute("isTestEnvironment", true);
                } else {
                    model.addAttribute("isTestEnvironment", false);
                }
                model.addAttribute("appName", systemName);

                return "registration";
            }

        } catch (Exception e) {

            log.error("LoginController#register###Exception: " + e.getMessage());
        }

        return "errorPage";
    }

    @RequestMapping(value = "/passwordForgot.html", method = RequestMethod.GET)
    public String forgotPassword(Model model) {

        if (systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_LOCAL) || systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_DEV)) {
            model.addAttribute("isTestEnvironment", true);
        } else {
            model.addAttribute("isTestEnvironment", false);
        }
        model.addAttribute("recaptchaKey", "'" + recaptchKeySite + "'");
        model.addAttribute("appName", systemName);

        return "passwordForgot";
    }

    @RequestMapping(value = "/passwordForgotChange.html", method = RequestMethod.GET)
    public String passwordForgotChange(Model model, @RequestParam(value = "u") String email, @RequestParam(value = "t") String token1, @RequestParam(value = "t2") String token2) {

        if (!email.matches(ServerConstants.REGEXP_EMAIL) || !token1.matches(ServerConstants.REGEX_SHA256) || !token2.matches(ServerConstants.REGEX_SHA256))
            // The details are invalid. It might be a hacking attempt.
            return "redirect:login.html?denied";

        try {
            Operator operator = operatorService.getActiveOperatorByUsername(email);

            if (operator != null) {

                LocalDateTime dateLastPasswordForgotRequest = LocalDateTime.ofInstant(operator.getDateLastPasswdForgotRequest().toInstant(), currentZone);
                LocalDateTime datePasswordForgotRequestPlusValidMinutes = dateLastPasswordForgotRequest
                    .plusMinutes(systemService.getSystemParameter().getPasswordForgotUrlValidMinutes());

                LocalDateTime now = LocalDateTime.ofInstant(new Date().toInstant(), currentZone);

                if (now.isBefore(datePasswordForgotRequestPlusValidMinutes)) {

                    String tokenCheck1 = SecurityUtil.generateDateBasedToken1(email, operator.getDateLastPasswdForgotRequest());
                    String tokenCheck2 = SecurityUtil.generateDateBasedToken2(email, operator.getDateLastPasswdForgotRequest());

                    if (token1.equals(tokenCheck1) && token2.equals(tokenCheck2)) {

                        model.addAttribute("appName", systemName);
                        model.addAttribute("recaptchaKey", "'" + recaptchKeySite + "'");
                        model.addAttribute("userName", "'" + email + "'");
                        model.addAttribute("key", "'" + token1 + "'");
                        model.addAttribute("key2", "'" + token2 + "'");

                        if (systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_LOCAL) || systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_DEV)) {
                            model.addAttribute("isTestEnvironment", true);
                        } else {
                            model.addAttribute("isTestEnvironment", false);
                        }

                        return "passwordForgotChange";
                    }
                }
            }

        } catch (Exception e) {

            log.error("LoginController#passwordForgotChange###Exception: " + e.getMessage());
        }

        return "errorPage";
    }

    @RequestMapping(value = "adminregistration/registrationUser.html", method = RequestMethod.GET)
    public String register(Model model, @RequestParam(value = "u") String userName, @RequestParam(value = "fn") String firstName,
                           @RequestParam(value = "ln") String lastName, @RequestParam(value = "t") String token1, @RequestParam(value = "t2") String token2) {

        if (!userName.matches(ServerConstants.REGEXP_EMAIL) || !token1.matches(ServerConstants.REGEX_SHA256) || !token2.matches(ServerConstants.REGEX_SHA256))
            // The details are invalid. It might be a hacking attempt.
            return "redirect:login.html?denied";

        try {
            Operator admin = operatorRepository.findByEmail(userName);
            if (admin != null && !admin.getIsDeleted())
                // the customer has registered and tried to open the registration page again. In that case redirect to the login page
                return "redirect:login.html";

           if (adminRegistrationService.isAdminRegistrationRequestAllowed(userName, token1, token2)) {

                model.addAttribute("email", userName);
                model.addAttribute("firstName", firstName);
                model.addAttribute("lastName", lastName);
                model.addAttribute("token", token1);

                if (systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_LOCAL) || systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_DEV)) {
                    model.addAttribute("isTestEnvironment", true);
                } else {
                    model.addAttribute("isTestEnvironment", false);
                }
                model.addAttribute("appName", systemName);

                return "registrationUser";
           }

        } catch (Exception e) {

            log.error("LoginController#registrationUser###Exception: " + e.getMessage());
        }

        return "errorPage";
    }

    @RequestMapping(value = "/credentialsExpired.html", method = RequestMethod.GET)
    public String credentialsExpired(Model model, @RequestParam(value = "u") String email, @RequestParam(value = "t") String token1, @RequestParam(value = "t2") String token2) {

        if (!email.matches(ServerConstants.REGEXP_EMAIL) || !token1.matches(ServerConstants.REGEX_SHA256) || !token2.matches(ServerConstants.REGEX_SHA256))
            // The details are invalid. It might be a hacking attempt.
            return "redirect:login.html?denied";

        try {
            Operator operator = operatorService.getActiveOperatorByUsername(email);

            if (operator != null) {

                String tokenCheck1 = SecurityUtil.generateDateBasedToken1(email, operator.getDateLastPassword());
                String tokenCheck2 = SecurityUtil.generateDateBasedToken2(email, operator.getDateLastPassword());

                if (token1.equals(tokenCheck1) && token2.equals(tokenCheck2)) {

                    model.addAttribute("appName", systemName);
                    model.addAttribute("recaptchaKey", "'" + recaptchKeySite + "'");
                    model.addAttribute("userName", "'" + email + "'");
                    model.addAttribute("key", "'" + token1 + "'");
                    model.addAttribute("key2", "'" + token2 + "'");

                    if (systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_LOCAL) || systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_DEV)) {
                        model.addAttribute("isTestEnvironment", true);
                    } else {
                        model.addAttribute("isTestEnvironment", false);
                    }

                    return "credentialsExpired";
                }
            }

        } catch (Exception e) {

            log.error("LoginController#credentialsExpired###Exception: " + e.getMessage());
        }

        return "errorPage";
    }
}
