customerSetUpApp.service('customerSetUpService', [ 'tcComService', function(tcComService) {

    this.uploadFile = function(uploadFormData, callBackSuccess, callBackError) {

        tcComService.uploadSingleFile("upload/accountsetup/singleUpload", uploadFormData, callBackSuccess, callBackError);
    };

    this.sendMobileUploadLink = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPI("accountsetup/sendmobileuploadlink", urlParams, callBackSuccess, callBackError);
    };

    this.uploadFileByPhone = function(uploadFormData, callBackSuccess, callBackError) {

        tcComService.uploadSingleFile("upload/accountsetup/singleUploadByPhone", uploadFormData, callBackSuccess, callBackError);
    };

    this.cancelMobileUpload = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPI("accountsetup/cancelmobileupload", urlParams, callBackSuccess, callBackError);
    };

    this.checkMobileUploadCompleted = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPI("accountsetup/checkmobileuploadreq", urlParams, callBackSuccess, callBackError);
    };

    this.showUploadedFile = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPI("upload/accountsetup/showFile", urlParams, callBackSuccess, callBackError);
    };

    this.getContactFileType = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForSingleData("upload/accountsetup/getFileType", urlParams, callBackSuccess, callBackError);
    };

    this.accountReSubmit = function(callBackSuccess, callBackError) {

        tcComService.callAPI("accountsetup/accountresubmit", {}, callBackSuccess, callBackError);
    };
    
    this.submitIdDetails = function(uploadFormData, callBackSuccess, callBackError) {

        tcComService.uploadSingleFile("accountsetup/submitKYCDetails", uploadFormData, callBackSuccess, callBackError);
    };

    this.getAccountReSubmissionData = function(callBackSuccess, callBackError) {

        tcComService.callAPI("customer/getforresubmission", {}, callBackSuccess, callBackError);
    };

    this.getFlagsAccountReSubmissionData = function(callBackSuccess, callBackError) {

        tcComService.callAPI("customer/getflags", {}, callBackSuccess, callBackError);
    };
    
} ]);
