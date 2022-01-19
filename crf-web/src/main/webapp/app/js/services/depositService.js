crfApp.service('depositsService', ['tcComService', function(tcComService) {

    this.listCustomerDepositAccountsForAdmin = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForPaginatedDataList("depositaccountadmin/list", urlParams, callBackSuccess, callBackError);
    };
    
    this.listDepositAccountsPaymentsForAdmin = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForPaginatedDataList("depositaccountadmin/interestpayment/list", urlParams, callBackSuccess, callBackError);
    };
    
    this.approveDeposit = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("depositaccountadmin/approve", regData, callBackSuccess, callBackError);
    };

    this.approveDepositWithdrawal = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("depositaccountadmin/approve/withdrawal", regData, callBackSuccess, callBackError);
    };
    
    this.listCustomerDepositAccounts = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForPaginatedDataList("depositaccount/list", urlParams, callBackSuccess, callBackError);
    };
    
    this.saveDepositAccount = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("depositaccount/save", regData, callBackSuccess, callBackError);
    };
    
    this.calcDepositWithdrawalStats = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("depositaccount/calc/withdrawal/stats", regData, callBackSuccess, callBackError);
    };
    
    this.requestDepositWithdrawal = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("depositaccount/request/withdrawal", regData, callBackSuccess, callBackError);
    };
    
    this.processDepositInterestPayment = function(regData, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("depositaccountadmin/process/interestpayment", regData, callBackSuccess, callBackError);
    };
    
    this.listCustomerDepositAccountDocuments = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForPaginatedDataList("depositaccount/doc/list", urlParams, callBackSuccess, callBackError);
    };
    
    this.listCustomerDepositAccountStatements = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callAPIForPaginatedDataList("depositaccount/statement/list", urlParams, callBackSuccess, callBackError);
    };
    
}]);
