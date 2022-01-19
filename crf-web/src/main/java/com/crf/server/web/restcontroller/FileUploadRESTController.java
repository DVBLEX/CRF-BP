package com.crf.server.web.restcontroller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crf.server.base.common.ServerConstants;
import com.crf.server.base.common.ServerResponseConstants;
import com.crf.server.base.component.FileValidator;
import com.crf.server.base.entity.FileBucket;
import com.crf.server.base.entity.FileBucketUnAuth;
import com.crf.server.base.entity.FileData;
import com.crf.server.base.entity.FileEntity.FileType;
import com.crf.server.base.entity.MobileUploadRequest;
import com.crf.server.base.exception.CRFException;
import com.crf.server.base.jsonentity.ApiResponseJsonEntity;
import com.crf.server.base.service.FileService;
import com.crf.server.base.service.MobileUploadService;
import com.crf.server.base.service.SystemService;
import com.crf.server.web.security.MyUserDetails;

@RestController
@RequestMapping("/upload")
public class FileUploadRESTController {

    private static final ZoneId currentZone = ZoneId.systemDefault();

    private FileValidator       fileValidator;
    private FileService         fileService;
    private MobileUploadService mobileUploadService;
    private SystemService       systemService;

    @Autowired
    public void setFileValidator(FileValidator fileValidator) {
        this.fileValidator = fileValidator;
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

    @InitBinder("fileBucket")
    protected void initBinderFileBucket(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @RequestMapping(value = "/accountsetup/singleUpload", method = RequestMethod.POST)
    public ApiResponseJsonEntity accountSetupSingleUpload(HttpServletResponse response, FileBucket fileBucket, BindingResult result) throws CRFException, Exception {

        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null)
            throw new Exception();

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        if (fileBucket == null || fileBucket.getFile() == null) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "");
        }

        MultipartFile multipartFile = fileBucket.getFile();
        String fileName = multipartFile.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        FileType type = FileType.valueOf(extension.toUpperCase());

        if (type == null)
            throw new Exception();

        fileService.saveCustomerFile(fileBucket.getFile().getBytes(), type, multipartFile.getContentType(), fileBucket.getFileRole(), userDetails.getCustomerId());

        // In case of PDF we don't put the data into the response.
        // it is displayed by another request on the front-end.
        if (type != FileType.PDF) {

            // it is image so we need to read it from database because it might be resized and compressed
            FileData fileData = fileService.getCustomerFileData(fileBucket.getFileRole(), userDetails.getCustomerId());
            apiResponse.setSingleData(fileData.getData());
        }

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/accountsetup/singleUploadByPhone", method = RequestMethod.POST)
    public ApiResponseJsonEntity accountSetupSingleUploadByPhone(HttpServletResponse response, FileBucketUnAuth fileBucket, BindingResult result) throws CRFException, Exception {

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        if (!fileBucket.getEmail().matches(ServerConstants.REGEXP_EMAIL) || !fileBucket.getT1().matches(ServerConstants.REGEX_SHA256)
            || !fileBucket.getT2().matches(ServerConstants.REGEX_SHA256)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RegexFormatCheck#Parameters");
        }

        MobileUploadRequest mobileUploadRequest = mobileUploadService.getMobileUploadRequestWithCustomer(fileBucket.getEmail(), fileBucket.getT1(), fileBucket.getT2());

        if (mobileUploadRequest == null || fileBucket == null || fileBucket.getFile() == null || fileBucket.getFileRole() != mobileUploadRequest.getFileRole()
            || !mobileUploadRequest.getIsValid() || mobileUploadRequest.getIsCompleted()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "RequestValidation");
        }

        LocalDateTime requestTime = LocalDateTime.ofInstant(mobileUploadRequest.getDateLastRequest().toInstant(), currentZone);
        LocalDateTime now = LocalDateTime.now();

        if (requestTime.isBefore(now.minusMinutes(systemService.getSystemParameter().getEmailMobileFileuploadLinkValidMinutes()))) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new CRFException(ServerResponseConstants.URL_EXPIRED_CODE, ServerResponseConstants.URL_EXPIRED_TEXT, "Expired#MobileUploadURL");
        }

        MultipartFile multipartFile = fileBucket.getFile();
        String fileName = multipartFile.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        FileType type = FileType.valueOf(extension.toUpperCase());

        if (type == null)
            throw new Exception();

        fileService.saveCustomerFile(fileBucket.getFile().getBytes(), type, multipartFile.getContentType(), fileBucket.getFileRole(), mobileUploadRequest.getCustomerId());

        mobileUploadService.setMobileUploadRequestCompleted(mobileUploadRequest.getId());

        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/accountsetup/showFile", method = RequestMethod.POST)
    public ApiResponseJsonEntity accountSetupShowFile(HttpServletResponse response, @RequestParam(value = "fileRole") int fileRole) throws CRFException, Exception {

        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "accountSetupShowFile");

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        FileData fileData = fileService.getCustomerFileData(fileRole, userDetails.getCustomerId());

        List<String> dataList = new ArrayList<>();
        dataList.add(fileData.getType());

        apiResponse.setSingleData(fileData.getData());
        apiResponse.setDataList(dataList);
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/accountsetup/getFileType", method = RequestMethod.POST)
    public ApiResponseJsonEntity accountSetupGetFileType(HttpServletResponse response, @RequestParam(value = "fileRole") int fileRole) throws CRFException, Exception {

        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails == null)
            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "accountSetupGetFileType");

        ApiResponseJsonEntity apiResponse = new ApiResponseJsonEntity();

        FileType fileType = fileService.getCustomerFileType(fileRole, userDetails.getCustomerId());

        apiResponse.setSingleData(fileType.toString());
        apiResponse.setResponseCode(ServerResponseConstants.SUCCESS_CODE);
        apiResponse.setResponseText(ServerResponseConstants.SUCCESS_TEXT);
        response.setStatus(HttpServletResponse.SC_OK);

        return apiResponse;
    }

    @RequestMapping(value = "/accountsetup/showPDFFile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> showPDFFile(HttpServletResponse response) {

        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));

        try {
            if (userDetails == null)
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "showPDFFile#1");

            FileData fileData = fileService.getCustomerFileData(ServerConstants.FILE_ROLE_ID_DOCUMENT, userDetails.getCustomerId());

            if (!fileData.getType().equalsIgnoreCase(FileType.PDF.toString()))
                throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "showPDFFile#2");

            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(fileData.getData(), headers, HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(new byte[0], headers, HttpStatus.OK);
        }
    }
}
