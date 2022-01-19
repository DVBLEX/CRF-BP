package com.crf.server.base.service;

import java.util.Date;
import java.util.HashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.entity.Customer;
import com.crf.server.base.entity.Email;
import com.crf.server.base.entity.MobileUploadRequest;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.repository.CustomerRepository;
import com.crf.server.base.repository.MobileUploadRequestRepository;

import lombok.extern.apachecommons.CommonsLog;

@Service
@CommonsLog
public class MobileUploadService {

    private CustomerRepository            customerRepository;
    private MobileUploadRequestRepository mobileUploadRequestRepository;
    private EmailService                  emailService;

    @Value("${tc.system.url}")
    private String                        sustemURL;

    @Value("${tc.email.mobileupload.passport.title}")
    private String                        emailMobileUploadPassportTitle;

    @Value("${tc.email.mobileupload.idcard.title}")
    private String                        emailMobileUploadIDCardTitle;

    @Value("${tc.email.mobileupload.drivinglicense.title}")
    private String                        emailMobileUploadDrivingLicenseTitle;

    @Value("${tc.email.mobileupload.photo.title}")
    private String                        emailMobileUploadPhotoTitle;

    @Value("${tc.email.mobileupload.passport.text}")
    private String                        emailMobileUploadPassportText;

    @Value("${tc.email.mobileupload.idcard.text}")
    private String                        emailMobileUploadIDCardText;

    @Value("${tc.email.mobileupload.drivinglicense.text}")
    private String                        emailMobileUploadDrivingLicenseText;

    @Value("${tc.email.mobileupload.photo.text}")
    private String                        emailMobileUploadPhotoText;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setMobileUploadRequestRepository(MobileUploadRequestRepository mobileUploadRequestRepository) {
        this.mobileUploadRequestRepository = mobileUploadRequestRepository;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Transactional
    public void createMobileUploadRequestForCustomer(String email, int fileRole) throws Exception {

        Customer customer = customerRepository.findByEmail(email);

        if (customer != null && !customer.getIsDeleted()) {

            MobileUploadRequest mobileUploadRequest = mobileUploadRequestRepository.findByCustomerIdAndFileRole(customer.getId(), fileRole);

            boolean exists = false;

            if (mobileUploadRequest != null) {
                exists = true;
            }

            if (!exists) {

                mobileUploadRequest = new MobileUploadRequest();
                mobileUploadRequest.setCustomerId(customer.getId());
                mobileUploadRequest.setDateCreated(new Date());
                mobileUploadRequest.setCountRequest(0);
                mobileUploadRequest.setFileRole(fileRole);
            }

            regenerateRequest(mobileUploadRequest);

            mobileUploadRequestRepository.save(mobileUploadRequest);

            sendMobileUploadLinkOut(mobileUploadRequest, email, customer);

        } else
            throw new Exception();
    }

    public MobileUploadRequest getMobileUploadRequestForCustomer(long customerId, int fileRole) throws Exception {

        return mobileUploadRequestRepository.findByCustomerIdAndFileRole(customerId, fileRole);
    }

    public MobileUploadRequest getMobileUploadRequestWithCustomer(String email, String token1, String token2) throws Exception {

        Customer customer = customerRepository.findByEmail(email);

        if (customer == null) {
            log.error("FileServiceImpl#getMobileUploadRequestWithCustomer###Exception: Customer not found by email: " + email);
            throw new Exception("Customer not found. email: " + email);
        }

        return mobileUploadRequestRepository.findByCustomerIdAndToken1AndToken2(customer.getId(), token1, token2);
    }

    @Transactional
    public void setMobileUploadRequestCompleted(long id) throws Exception {

        MobileUploadRequest mobileUploadRequest = mobileUploadRequestRepository.findById(id).orElse(null);
        mobileUploadRequest.setIsCompleted(true);
        mobileUploadRequest.setDateCompleted(new Date());

        mobileUploadRequestRepository.save(mobileUploadRequest);
    }

    public void cancelMobileUploadRequestByCustomer(long customerId, int fileRole) throws Exception {

        mobileUploadRequestRepository.cancelMobileUploadRequestByCustomer(customerId, fileRole);
    }

    public boolean isMobileUploadRequestWithinMinutes(String email, String token1, String token2, int minutes) throws Exception {

        return mobileUploadRequestRepository.countMobileUploadRequestsWithinMinutes(email, token1, token2, minutes) > 0L;
    }

    public boolean isMobileUploadCompleted(String email, int fileRole) throws Exception {

        return mobileUploadRequestRepository.countMobileUploadRequestsCompleted(email, fileRole) > 0L;
    }

    public int getMobileUploadByCustomerAndFileRole(String email, String token1, String token2) throws Exception {

        return mobileUploadRequestRepository.findFileRoleByEmailAndTokens(email, token1, token2);
    }

    private void regenerateRequest(MobileUploadRequest mobileUploadRequest) throws Exception {

        StringBuilder token1SB = new StringBuilder();
        StringBuilder token2SB = new StringBuilder();

        token1SB.append(ServerConstants.SYSTEM_TOKEN_PREFIX).append(".").append(".");
        token1SB.append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));
        token1SB.append(".").append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));

        token2SB.append(ServerConstants.SYSTEM_TOKEN_PREFIX).append(".").append(".");
        token2SB.append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));
        token2SB.append(".").append(RandomStringUtils.randomAlphanumeric(12)).append(".").append(RandomStringUtils.randomAlphanumeric(12));

        String token1 = DigestUtils.sha256Hex(token1SB.toString());
        String token2 = DigestUtils.sha256Hex(token2SB.toString());

        mobileUploadRequest.setDateLastRequest(new Date());
        mobileUploadRequest.setToken1(token1);
        mobileUploadRequest.setToken2(token2);
        mobileUploadRequest.setCountRequest(mobileUploadRequest.getCountRequest() + 1);
        mobileUploadRequest.setIsValid(true);
        mobileUploadRequest.setIsCompleted(false);
        mobileUploadRequest.setDateCompleted(null);
    }

    private void sendMobileUploadLinkOut(MobileUploadRequest mobileUploadRequest, String emailTo, Customer customer) throws CRFException {

        StringBuilder urlSB = new StringBuilder();
        urlSB.append(sustemURL).append("mobileUpload.html").append("?u=").append(emailTo).append("&t=");
        urlSB.append(mobileUploadRequest.getToken1()).append("&t2=").append(mobileUploadRequest.getToken2());

        HashMap<String, Object> params = new HashMap<>();
        params.put("uploadLink", urlSB.toString());
        params.put("firstName", customer.getFirstName());

        int fileRole = mobileUploadRequest.getFileRole();

        if (fileRole == ServerConstants.FILE_ROLE_ID_DOCUMENT) {

            if (customer.getId1Type() == ServerConstants.ID_TYPE_PASSPORT) {
                params.put("uploadType", emailMobileUploadPassportTitle);
                params.put("uploadDescription", emailMobileUploadPassportText);

            } else if (customer.getId1Type() == ServerConstants.ID_TYPE_NATIONAL_ID_CARD) {
                params.put("uploadType", emailMobileUploadIDCardTitle);
                params.put("uploadDescription", emailMobileUploadIDCardText);
            } else if (customer.getId1Type() == ServerConstants.ID_TYPE_DRIVING_LICENSE) {
                params.put("uploadType", emailMobileUploadDrivingLicenseTitle);
                params.put("uploadDescription", emailMobileUploadDrivingLicenseText);
            }

        } else if (fileRole == ServerConstants.FILE_ROLE_PHOTOGRAPH) {
            params.put("uploadType", emailMobileUploadPhotoTitle);
            params.put("uploadDescription", emailMobileUploadPhotoText);
        }

        Email scheduledEmail = new Email();
        scheduledEmail.setEmailTo(emailTo);
        scheduledEmail.setCustomerId(customer.getId());

        emailService.scheduleEmailByType(scheduledEmail, ServerConstants.EMAIL_MOBILE_UPLOAD_LINK_TEMPLATE_ID, params);
    }
}
