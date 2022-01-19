crfApp.controller('DepositsCustomerController', ['$scope', '$rootScope', 'depositsService', function($scope, $rootScope, depositsService) {

    $rootScope.activeNavbar('#navbarDeposits');
    
    $scope.requestDepositWithdrawalButtonDisabled = false;
    $scope.requestDepositWithdrawalErrorResponse = "";
    
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

        depositsService.listCustomerDepositAccounts(urlParams, $scope.listCustomerDepositAccountsCallBack, $scope.errorHandler);
    });

    $scope.listCustomerDepositAccountsCallBack = function(d) {
        $scope.showSpinner = false;
        $scope.tcTable.setData(d);
    };

    $scope.refreshTableData = function() {
        $scope.tcTable.reloadTable();
    };
    
    $scope.showRequestDepositWithdrawalModal = function(selectedRow) {

        $('#requestDepositWithdrawalModal').modal({
            backdrop : 'static',
            keyboard : false,
            show : true
        });
        
        $scope.selectedRow = selectedRow;
        $scope.formData = {};
        $scope.requestDepositWithdrawalErrorResponse = "";
        
        $scope.showSpinner = true;
            
            var reqData = {
                code : $scope.selectedRow.code
            };
    
            depositsService.calcDepositWithdrawalStats(reqData, function(response) {
    
                $scope.showSpinner = false;
                $scope.formData.accruedInterest = response.data.accruedInterest;
                $scope.formData.totalInterest = response.data.totalInterest;
                $scope.formData.depositPlusInterestAmount = response.data.depositPlusInterestAmount;
                
            }, function(error) {
    
                $scope.showSpinner = false;
                $scope.requestDepositWithdrawalErrorResponse = error.data.responseText;
            });
            
    };
    
    $scope.closeRequestDepositWithdrawalModal = function() {

        $scope.selectedRow = {};
        $scope.formData = {};
        $scope.requestDepositWithdrawalErrorResponse = "";
        
        $('#requestDepositWithdrawalModal').modal('hide');
    };
    
    $scope.requestDepositWithdrawal = function() {
        
        $scope.requestDepositWithdrawalErrorResponse = "";
        
        if ($rootScope.customerDetails === undefined || $rootScope.customerDetails.bankAccount.iban === null) {
            $scope.requestDepositWithdrawalErrorResponse = "You must enter your bank account details first. Please do so using the Bank Account tab.";
        
        } else if ($scope.selectedRow === undefined || $scope.selectedRow === null || $scope.selectedRow.code === undefined || $scope.selectedRow.code === '') {
            $scope.requestDepositWithdrawalErrorResponse = "You must select a deposit product first.";
            
        } else if (!$scope.formData.agreeTerms) {
            $scope.requestDepositWithdrawalErrorResponse = "You must agree before submitting a withdrawal request.";
        
        } else {
            $scope.requestDepositWithdrawalErrorResponse = "";
            $scope.requestDepositWithdrawalButtonDisabled = true;
            $scope.showSpinner = true;
            
            var reqData = {
                code : $scope.selectedRow.code
            };
    
            depositsService.requestDepositWithdrawal(reqData, function(response) {
    
                $scope.showSpinner = false;
                $scope.requestDepositWithdrawalErrorResponse = "";
                $scope.requestDepositWithdrawalButtonDisabled = false;
                $scope.closeRequestDepositWithdrawalModal();
                
                $rootScope.showResultMessage(true, "You have successfully requested a deposit withdrawal! You will be notified via email when your request has been approved.");
                
                $scope.refreshTableData();
                
            }, function(error) {
    
                $scope.showSpinner = false;
                $scope.requestDepositWithdrawalButtonDisabled = false;
                $scope.requestDepositWithdrawalErrorResponse = error.data.responseText;
            });
        }    
    };
    
    $scope.refreshTableData();
    
}]);