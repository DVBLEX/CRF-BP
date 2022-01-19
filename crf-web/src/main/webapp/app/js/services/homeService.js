crfApp.service('homeService', ['tcComService', function(tcComService) {

    this.listDepositProducts = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPI("depositproduct/list", urlParams, callBackSuccess, callBackError);
    };
    
    this.calcDepositProductInterest = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("depositproduct/calc/interest", regData, callBackSuccess, callBackError);
    };
    
    this.submitContactForm = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPI("operator/contact/form/submit", urlParams, callBackSuccess, callBackError);
    };
    
}]);