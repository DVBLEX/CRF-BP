package com.crf.server.base.service;


import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.common.ServerUtil;
import com.crf.server.base.entity.FileData;
import com.crf.server.base.entity.FileEntity;
import com.crf.server.base.entity.Operator;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.exception.CRFValidationException;
import com.crf.server.base.repository.OperatorRepository;
import com.domenicseccareccia.jpegautorotate.JpegAutorotate;
import com.domenicseccareccia.jpegautorotate.JpegAutorotateException;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@CommonsLog
public class AccountSetUpService {


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


    public List<FileData> convertMultipartToFileData(MultipartFile[] files, int entityType, int entityNumber) throws IOException {
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

   public void validatePOI(Integer idType, String idNumber) throws CRFValidationException {
        if (idType == null || (idType != ServerConstants.ID_TYPE_PASSPORT
                && idType != ServerConstants.ID_TYPE_NATIONAL_ID_CARD
                && idType != ServerConstants.ID_TYPE_DRIVING_LICENSE)) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_ID_TYPE_CODE, ServerResponseConstants.INVALID_ID_TYPE_TEXT, "");
        }
        if (idNumber == null || idNumber.isEmpty() || !idNumber.matches(ServerConstants.REGEXP_BASIC_ID_NUMBER)) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_ID_NUMBER_CODE, ServerResponseConstants.INVALID_ID_NUMBER_TEXT, "");
        }
    }

    public void validatePOA(Integer poaType) throws CRFValidationException {
        if (poaType == null || (poaType != ServerConstants.POA_TYPE_UTILITY_BILL
                && poaType != ServerConstants.POA_TYPE_BANK_STATEMENT
                && poaType != ServerConstants.POA_TYPE_TAX_NOTICE
                && poaType != ServerConstants.POA_TYPE_SOCIAL_WELFARE
                && poaType != ServerConstants.POA_TYPE_MOTOR_TAX
                && poaType != ServerConstants.POA_TYPE_HOME_OR_MOTOR_INSURANCE_CERT)) {
            throw new CRFValidationException(ServerResponseConstants.INVALID_POA_TYPE_CODE, ServerResponseConstants.INVALID_POA_TYPE_TEXT, "");
        }
    }

    public Date getValidateExpiryDate(String expiryDate) throws CRFException {
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

    public void validateFiles(MultipartFile[] files) throws CRFException {
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

}
