package com.crf.server.web.restcontroller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crf.server.base.entity.FileData;
import com.crf.server.base.entity.FileEntity;
import com.crf.server.base.entity.KycDetails;
import com.crf.server.base.service.FileService;
import com.domenicseccareccia.jpegautorotate.JpegAutorotate;
import com.domenicseccareccia.jpegautorotate.JpegAutorotateException;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.MobileUploadRequest;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.service.CustomerService;
import com.crf.server.base.service.MobileUploadService;
import com.crf.server.base.service.SystemService;
import com.crf.server.web.security.MyUserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/accountsetup")
public class AccountSetUpRESTController {

    private CustomerService     customerService;
    private FileService         fileService;
    private MobileUploadService mobileUploadService;
    private SystemService       systemService;

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setMobileUploadService(MobileUploadService mobileUploadService) {
        this.mobileUploadService = mobileUploadService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    @RequestMapping(value = "/submitKYCDetails", method = RequestMethod.POST)
    public ApiResponseJsonEntity submitKYCDetails(HttpServletResponse response, KycDetails kycDetails) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null)
            throw new CRFException();

        boolean isResubmission = Boolean.parseBoolean(kycDetails.getIsResubmission());

        if (kycDetails.getKycOption() == null) {
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "kycOption#1");
        } else if (kycDetails.getKycOption() == ServerConstants.KYC_OPTION_COPY) {
            validatePOI(kycDetails.getId1Type(), kycDetails.getId1Number());
            validatePOI(kycDetails.getId2Type(), kycDetails.getId2Number());
            validatePOA(kycDetails.getPoa1Type());
            validatePOA(kycDetails.getPoa2Type());
            if (!isResubmission) {
                validateFiles(kycDetails.getId1Files());
                validateFiles(kycDetails.getId2Files());
                validateFiles(kycDetails.getPoa1Files());
                validateFiles(kycDetails.getPoa2Files());
                validateFiles(kycDetails.getPhotoFiles());
            }
            kycDetails.setDateId1Expiry(getValidateExpiryDate(kycDetails.getDateId1ExpiryString()));
            kycDetails.setDateId2Expiry(getValidateExpiryDate(kycDetails.getDateId2ExpiryString()));
        } else if (kycDetails.getKycOption() == ServerConstants.KYC_OPTION_ORIGINAL_OR_CERTIFIED) {
            validatePOI(kycDetails.getId1Type(), kycDetails.getId1Number());
            validatePOA(kycDetails.getPoa1Type());
            if (!isResubmission) {
                validateFiles(kycDetails.getId1Files());
                validateFiles(kycDetails.getPoa1Files());
                validateFiles(kycDetails.getPhotoFiles());
            }
            kycDetails.setDateId1Expiry(getValidateExpiryDate(kycDetails.getDateId1ExpiryString()));
        } else {
            throw new CRFException(ServerResponseConstants.INVALID_REQUEST_FORMAT_CODE, ServerResponseConstants.INVALID_REQUEST_FORMAT_TEXT, "kycOption#2");
        }

        long customerId = userDetails.getCustomerId();
        customerService.saveKYCDetails(customerId, kycDetails);
        if (kycDetails.getKycOption() == ServerConstants.KYC_OPTION_COPY) {
            if (kycDetails.getId1Files() != null) {
                fileService.saveMultipleCustomerFiles(convertMultipartToFileData(kycDetails.getId1Files(), kycDetails.getId1Type(), 1),
                        ServerConstants.FILE_ROLE_ID_DOCUMENT, 1, customerId);
            }
            if (kycDetails.getId2Files() != null) {
                fileService.saveMultipleCustomerFiles(convertMultipartToFileData(kycDetails.getId2Files(), kycDetails.getId2Type(), 2),
                        ServerConstants.FILE_ROLE_ID_DOCUMENT, 2, customerId);
            }
            if (kycDetails.getPoa1Files() != null) {
                fileService.saveMultipleCustomerFiles(convertMultipartToFileData(kycDetails.getPoa1Files(), kycDetails.getPoa1Type(), 1),
                        ServerConstants.FILE_ROLE_POA_DOCUMENT, 1, customerId);
            }
            if (kycDetails.getPoa2Files() != null) {
                fileService.saveMultipleCustomerFiles(convertMultipartToFileData(kycDetails.getPoa2Files(), kycDetails.getPoa2Type(), 2),
                        ServerConstants.FILE_ROLE_POA_DOCUMENT, 2, customerId);
            }
            if (kycDetails.getPhotoFiles() != null) {
                fileService.saveMultipleCustomerFiles(convertMultipartToFileData(kycDetails.getPhotoFiles(), ServerConstants.DEFAULT_INT, ServerConstants.DEFAULT_INT),
                        ServerConstants.FILE_ROLE_PHOTOGRAPH, ServerConstants.DEFAULT_INT, customerId);
            }
        } else if (kycDetails.getKycOption() == ServerConstants.KYC_OPTION_ORIGINAL_OR_CERTIFIED) {
            if (kycDetails.getId1Files() != null) {
                fileService.saveMultipleCustomerFiles(convertMultipartToFileData(kycDetails.getId1Files(), kycDetails.getId1Type(), 1),
                        ServerConstants.FILE_ROLE_ID_DOCUMENT, 1, customerId);
            }
            if (kycDetails.getPoa1Files() != null) {
                fileService.saveMultipleCustomerFiles(convertMultipartToFileData(kycDetails.getPoa1Files(), kycDetails.getPoa1Type(), 1),
                        ServerConstants.FILE_ROLE_POA_DOCUMENT, 1, customerId);
            }
            if (kycDetails.getPhotoFiles() != null) {
                fileService.saveMultipleCustomerFiles(convertMultipartToFileData(kycDetails.getPhotoFiles(), ServerConstants.DEFAULT_INT, ServerConstants.DEFAULT_INT),
                    ServerConstants.FILE_ROLE_PHOTOGRAPH, ServerConstants.DEFAULT_INT, customerId);
            }
        }

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);

        response.setStatus(HttpServletResponse.SC_OK);

        apiResponse.setResponseDate(new Date());
        return apiResponse;
    }

    private List<FileData> convertMultipartToFileData(MultipartFile[] files, int entityType, int entityNumber) throws IOException {
        List<FileData> fileDataList = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String fileName = (multipartFile).getOriginalFilename();
            if (fileName != null && !fileName.isEmpty()) {
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                FileData fileData = new FileData();
                if ("image/jpeg".equals(multipartFile.getContentType())) {
                    try {
                        fileData.setData(JpegAutorotate.rotate(multipartFile.getInputStream()));
                    } catch (JpegAutorotateException | IOException ignored) {
                        fileData.setData(multipartFile.getBytes());
                    }
                } else {
                    fileData.setData(multipartFile.getBytes());
                }
                fileData.setType(extension.toUpperCase());
                fileData.setMimeType(multipartFile.getContentType());
                fileData.setEntityType(entityType);
                fileData.setEntityNumber(entityNumber);
                fileDataList.add(fileData);
            }
        }
        return fileDataList;
    }

    private void validatePOI(Integer idType, String idNumber) throws CRFValidationException {
        if (idType == null || (idType != ServerConstants.ID_TYPE_PASSPORT
                && idType != ServerConstants.ID_TYPE_NATIONAL_ID_CARD
                && idType != ServerConstants.ID_TYPE_DRIVING_LICENSE)) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_ID_TYPE_CODE, ServerResponseConstants.INVALID_ID_TYPE_TEXT, "");
        }
        if (idNumber == null || idNumber.isEmpty() || !idNumber.matches(ServerConstants.REGEXP_BASIC_ID_NUMBER)) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_ID_NUMBER_CODE, ServerResponseConstants.INVALID_ID_NUMBER_TEXT, "");
        }
    }

    private void validatePOA(Integer poaType) throws CRFValidationException {
        if (poaType == null || (poaType != ServerConstants.POA_TYPE_UTILITY_BILL
                && poaType != ServerConstants.POA_TYPE_BANK_STATEMENT
                && poaType != ServerConstants.POA_TYPE_TAX_NOTICE
                && poaType != ServerConstants.POA_TYPE_SOCIAL_WELFARE
                && poaType != ServerConstants.POA_TYPE_MOTOR_TAX
                && poaType != ServerConstants.POA_TYPE_HOME_OR_MOTOR_INSURANCE_CERT)) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_POA_TYPE_CODE, ServerResponseConstants.INVALID_POA_TYPE_TEXT, "");
        }
    }

    private Date getValidateExpiryDate(String expiryDate) throws CRFException {
        Date dateIdExpiry;
        if (StringUtils.isBlank(expiryDate))
            throw new CRFException(ServerResponseConstants.INVALID_EXPIRY_DATE_CODE, ServerResponseConstants.INVALID_EXPIRY_DATE_TEXT, "idExpiryDateString#1");
        else {
            try {
                dateIdExpiry = ServerUtil.parseDate(ServerConstants.dateFormatddMMyyyy, expiryDate);
            } catch (ParseException pe) {
                throw new CRFException(ServerResponseConstants.INVALID_EXPIRY_DATE_CODE, ServerResponseConstants.INVALID_EXPIRY_DATE_TEXT, "idExpiryDateString#2");
            }
        }
        return dateIdExpiry;
    }

    private void validateFiles(MultipartFile[] files) throws CRFException {
        if (files.length == 0) {
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "KycDetails#MultipartFile1");
        } else {
            for (MultipartFile file: files) {
                String fileName = file.getOriginalFilename();
                if (file.isEmpty() || fileName == null) {
                    throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "KycDetails#MultipartFile2");
                } else {
                    String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                    if (!EnumUtils.isValidEnum(FileEntity.FileType.class, extension.toUpperCase())) {
                        throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "KycDetails#MultipartFile3");
                    }
                }
            }
        }
    }

    @RequestMapping("/sendmobileuploadlink")
    public ApiResponseJsonEntity sendMobileUploadLink(HttpServletResponse response, @RequestParam(value = "fileRole") int fileRole) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "");

        } else {

            MobileUploadRequest mobileUploadRequest;

            try {
                mobileUploadRequest = mobileUploadService.getMobileUploadRequestForCustomer(userDetails.getCustomerId(), fileRole);

            } catch (Exception ex) {
                mobileUploadRequest = null;
            }

            if (mobileUploadRequest != null && mobileUploadRequest.getCountRequest() > systemService.getSystemParameter().getEmailMobileFileuploadLinkLimit()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                throw new CRFException(ServerResponseConstants.LIMIT_EXCEEDED_SMARTPHONE_UPLOAD_LINK_CODE, ServerResponseConstants.LIMIT_EXCEEDED_SMARTPHONE_UPLOAD_LINK_TEXT, "");

            } else {

                mobileUploadService.createMobileUploadRequestForCustomer(userDetails.getUsername(), fileRole);
                apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
                apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }

        apiResponse.setResponseDate(new Date());
        return apiResponse;
    }

    @RequestMapping("/checkmobileuploadreq")
    public ApiResponseJsonEntity checkMobileUploadRequest(HttpServletResponse response, @RequestParam(value = "fileRole") int fileRole) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "");

        } else {

            if (mobileUploadService.isMobileUploadCompleted(userDetails.getUsername(), fileRole)) {

                response.setStatus(HttpServletResponse.SC_OK);
                apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
                apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
                apiResponse.setResponseDate(new Date());
                return apiResponse;

            } else
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "");
        }
    }

    @RequestMapping("/cancelmobileupload")
    public ApiResponseJsonEntity cancelMobileUpload(HttpServletResponse response, @RequestParam(value = "fileRole") int fileRole) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "");

        } else {

            mobileUploadService.cancelMobileUploadRequestByCustomer(userDetails.getCustomerId(), fileRole);
            apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
            apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
            response.setStatus(HttpServletResponse.SC_OK);
        }

        apiResponse.setResponseDate(new Date());
        return apiResponse;
    }

    @RequestMapping("/accountresubmit")
    public ApiResponseJsonEntity accountReSubmit(HttpServletRequest request, HttpServletResponse response) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.INVALID_EMAIL_CODE, ServerResponseConstants.INVALID_EMAIL_TEXT, "");

        } else {
            customerService.accountReSubmit(userDetails.getCustomerId());

            // as the last step of the re-submission process, logout the user and force to login
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

            apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
            apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
            response.setStatus(HttpServletResponse.SC_OK);
        }

        apiResponse.setResponseDate(new Date());
        return apiResponse;
    }

}
