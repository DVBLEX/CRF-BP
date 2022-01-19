package com.crf.server.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.service.MobileUploadService;
import com.crf.server.base.service.SystemService;

import lombok.extern.apachecommons.CommonsLog;

@Controller
@CommonsLog
public class MobileUploadController {

    private SystemService       systemService;
    private MobileUploadService mobileUploadService;

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @Autowired
    public void setMobileUploadService(MobileUploadService mobileUploadService) {
        this.mobileUploadService = mobileUploadService;
    }

    @RequestMapping(value = "/mobileUpload.html", method = RequestMethod.GET)
    public String mobileUpload(Model model, Device device, @RequestParam(value = "u") String userName, @RequestParam(value = "t") String token1,
        @RequestParam(value = "t2") String token2) {

        if (!userName.matches(ServerConstants.REGEXP_EMAIL) || !token1.matches(ServerConstants.REGEX_SHA256) || !token2.matches(ServerConstants.REGEX_SHA256))
            // The details are invalid. It might be a hacking attempt.
            return "redirect:login.html?denied";

        try {
            if (mobileUploadService.isMobileUploadRequestWithinMinutes(userName, token1, token2, systemService.getSystemParameter().getEmailMobileFileuploadLinkValidMinutes())) {

                model.addAttribute("deviceType", "'" + ServerUtil.getDeviceTypeString(device) + "'");
                model.addAttribute("fileRole", "'" + mobileUploadService.getMobileUploadByCustomerAndFileRole(userName, token1, token2) + "'");
                model.addAttribute("userName", "'" + userName + "'");
                model.addAttribute("key", "'" + token1 + "'");
                model.addAttribute("key2", "'" + token2 + "'");

                return "mobileUpload";
            }

        } catch (Exception e) {

            log.error("MobileUploadController#mobileUpload###Exception: " + e.getMessage());
        }

        return "errorPage";
    }
}
