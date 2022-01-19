crfApp.service('depositProductsService', ['tcComService', function(tcComService) {

    this.listDepositProductsForAdmin = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForPaginatedDataList("depositproductadmin/list", urlParams, callBackSuccess, callBackError);
    };

    this.editProduct = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("depositproductadmin/edit", regData, callBackSuccess, callBackError);
    };


}]);
