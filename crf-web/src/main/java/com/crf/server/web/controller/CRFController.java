package com.crf.server.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.crf.server.base.common.SecurityUtil;
import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.SystemService;
import com.crf.server.web.security.MyUserDetails;

import lombok.extern.apachecommons.CommonsLog;

@Controller
@CommonsLog
public class CRFController {

    private CustomerService customerService;
    private SystemService   systemService;

    @Value("${tc.system.name}")
    private String          systemName;

    @Value("${tc.system.environment}")
    private String          systemEnvironment;

    @Value("${tc.google.recaptcha.key.site}")
    private String          recaptchaKeySite;

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @RequestMapping(value = "/crf.html", method = RequestMethod.GET)
    public String index(HttpServletRequest request, Model model, Device device) {

        String responseSource = "crfIndex#";
        responseSource = responseSource + request.getRemoteAddr() + "#username=" + SecurityUtil.getSystemUsername();

        StringBuilder builder = new StringBuilder();
        builder.append(responseSource);
        builder.append("#Request: ");
        builder.append("[]");
        log.info(builder.toString());

        MyUserDetails authUser = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("appName", systemName);
        model.addAttribute("environment", systemEnvironment);

        if (systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_LOCAL) || systemEnvironment.equals(ServerConstants.SYSTEM_ENVIRONMENT_DEV)) {
            model.addAttribute("isTestEnvironment", true);
        } else {
            model.addAttribute("isTestEnvironment", false);
        }
        
        model.addAttribute("username", authUser.getUsername());
        model.addAttribute("firstName", authUser.getFirstname());
        model.addAttribute("lastName", authUser.getLastname());
        model.addAttribute("isInvestorOperator", authUser.getRole() == ServerConstants.OPERATOR_ROLE_INVESTOR);
        model.addAttribute("isBorrowerOperator", authUser.getRole() == ServerConstants.OPERATOR_ROLE_BORROWER);
        model.addAttribute("isAdmin", authUser.getRole() == ServerConstants.OPERATOR_ROLE_ADMIN);
        model.addAttribute("deviceType", "'" + ServerUtil.getDeviceTypeString(device) + "'");

        if (authUser.getRole() == ServerConstants.OPERATOR_ROLE_ADMIN) {

        } else {

            try {
                Customer customer = customerService.getCustomerById(authUser.getCustomerId());

                model.addAttribute("customerCategory", customer.getCategory());
                model.addAttribute("isCustomerAccountVerified", customer.getIsPassportScanVerified());

                if (!customer.getIsPassportScanUploaded() || !customer.getIsPhotoUploaded() || customer.getIsPassportScanDenied()) {

                    model.addAttribute("isPassportScanUploaded", customer.getIsPassportScanUploaded());
                    model.addAttribute("isPassportScanDenied", customer.getIsPassportScanDenied());
                    model.addAttribute("isPhotoUploaded", customer.getIsPhotoUploaded());
                    model.addAttribute("userEmail", authUser.getUsername());
                    model.addAttribute("idType", "'" + customer.getId1Type() + "'");
                    model.addAttribute("idNumber", "'" + customer.getId1Number() + "'");
                    model.addAttribute("idExpiryDateString", "'" + ServerUtil.formatDate(ServerConstants.dateFormatMMddyyyy, customer.getDateID1Expiry()) + "'");
                    model.addAttribute("customerType", "'" + customer.getType() + "'");

                    if (!customer.getIsPassportScanDenied()) {

                        // account-setup (no re-submission)
                        model.addAttribute("recaptchaKey", "'" + recaptchaKeySite + "'");
                        model.addAttribute("userMobileNumber", "'" + customer.getMsisdn() + "'");
                        model.addAttribute("isPassportScanVerified", customer.getIsPassportScanVerified());

                    } else {

                        // re-submission
                        model.addAttribute("verificationDenialList", systemService.listCustomerVerificationDenials(customer.getId()));
                    }

                    return "accountsetup";
                }

            } catch (Exception e) {
                return "errorPage";
            }
        }

        return "crf";
    }

}
