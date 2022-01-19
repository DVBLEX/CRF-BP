crfApp.service('customersService', ['tcComService', function(tcComService) {

    this.listRegisteredCustomers = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForPaginatedDataList("customeradmin/list", urlParams, callBackSuccess, callBackError);
    };
    
    this.getCustomerForVerification = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForSingleData("customeradmin/getforverification", urlParams, callBackSuccess, callBackError);
    };
    
    this.listVerificationDenialReasons = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForDataList("customeradmin/verification/denial/reason/list", urlParams, callBackSuccess, callBackError);
    };
    
    this.verifyCustomer = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPI("customeradmin/verify", urlParams, callBackSuccess, callBackError);
    };
    
    this.denyCustomer = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("customeradmin/verification/deny", regData, callBackSuccess, callBackError);
    };
    
    this.emailRegURL = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("customeradmin/emailRegURL", urlParams, callBackSuccess, callBackError);
    };
    
    this.getCustomerDetails = function(callBackSuccess, callBackError) {

        tcComService.callAPIForSingleData("customer/get/details", {}, callBackSuccess, callBackError);
    };
    
    this.submitBankDetails = function(bankData, callBackSuccess, callBackError) {
        
        tcComService.callApiWithJSONParam("customer/submitbankdetails", bankData, callBackSuccess, callBackError);
    };
    
}]);
