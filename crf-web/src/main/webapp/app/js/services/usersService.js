crfApp.service('usersService', ['tcComService', function(tcComService) {

    this.listRegisteredUsers = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForPaginatedDataList("admin/list", urlParams, callBackSuccess, callBackError);
    };

    this.userEmailRegURL = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("adminregistration/emailRegURL", regData, callBackSuccess, callBackError);
    };

    this.editUser = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("admin/edit", regData, callBackSuccess, callBackError);
    };

    this.deleteUser = function(regData, callBackSuccess, callBackError) {

        tcComService.callAPI("admin/delete", regData, callBackSuccess, callBackError);
    };

}]);
