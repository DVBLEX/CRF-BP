crfApp.service('changePasswordService', [ 'tcComService', function(tcComService) {

    this.changePassword = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("operator/password/change", urlParams, callBackSuccess, callBackError);
    };

} ]);