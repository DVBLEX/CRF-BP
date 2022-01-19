crfApp.controller('PaymentsAdminController', ['$scope', '$rootScope', '$filter', 'depositsService', function($scope, $rootScope, $filter, depositsService) {

    $rootScope.activeNavbar('#navbarPayments');
    
    $scope.processDepositInterestPaymentErrorResponse = "";
    $scope.processDepositInterestPaymentButtonDisabled = false;
    
    $scope.errorHandler = function(error) {

        $scope.showSpinner = false;
        console.log(error);
    };
    
    $scope.tcTable = new TCTable(function(currentPage, pageRecordCount) {

        $scope.showSpinner = true;
        $scope.sortType = null;
        $scope.sortReverse = false;

        var urlParams = {
            page : currentPage - 1,
            size : pageRecordCount
        };

        depositsService.listDepositAccountsPaymentsForAdmin(urlParams, $scope.listDepositAccountsPaymentsForAdminCallBack, $scope.errorHandler);
    });

    $scope.listDepositAccountsPaymentsForAdminCallBack = function(d) {
        $scope.showSpinner = false;
        $scope.tcTable.setData(d);
    };

    $scope.refreshTableData = function() {
        $scope.tcTable.reloadTable();
    };
    
    $scope.showProcessDepositInterestPaymentModal = function(selectedRow) {

        $('#processDepositInterestPaymentModal').modal({
            backdrop : 'static',
            keyboard : false,
            show : true
        });
        
        $scope.selectedRow = selectedRow;
        $scope.formData = {};
    };
    
    $scope.closeDepositInterestPaymentModal = function() {

        $scope.selectedRow = {};
        $scope.formData = {};
        $scope.processDepositInterestPaymentErrorResponse = "";
        
        $('#processDepositInterestPaymentModal').modal('hide');
    };
    
    $scope.processDepositInterestPayment = function() {
        
        $scope.processDepositInterestPaymentErrorResponse = "";
        
        if (!$scope.formData.agreeTerms) {
            $scope.processDepositInterestPaymentErrorResponse = "You must confirm that you have processed the transfer to the bank account shown above.";
        
        } else {
            
            $scope.processDepositInterestPaymentErrorResponse = "";
            $scope.processDepositInterestPaymentButtonDisabled = true;
            $scope.showSpinner = true;
            
            var reqData = {
                code : $scope.selectedRow.code
            };
            
            depositsService.processDepositInterestPayment(reqData, function(response) {
    
                $scope.showSpinner = false;
                $scope.processDepositInterestPaymentErrorResponse = "";
                $scope.processDepositInterestPaymentButtonDisabled = false;
                $scope.closeDepositInterestPaymentModal();
                
                $rootScope.showResultMessage(true, "You have processed the deposit interest payment!");
                
                $scope.refreshTableData();
                
            }, function(error) {
    
                $scope.showSpinner = false;
                $scope.processDepositInterestPaymentButtonDisabled = false;
                $scope.processDepositInterestPaymentErrorResponse = error.data.responseText;
            });
        }
    };
    
    
    $scope.refreshTableData();
    
}]);