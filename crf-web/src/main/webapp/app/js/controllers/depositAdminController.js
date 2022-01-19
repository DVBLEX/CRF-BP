crfApp.controller('DepositsAdminController', ['$scope', '$rootScope', '$filter', 'depositsService', function($scope, $rootScope, $filter, depositsService) {

    $rootScope.activeNavbar('#navbarDeposits');
    
    $scope.filterStatusDefault = "1"; // INITIATED by default
    $scope.filterStatus = $scope.filterStatusDefault;
    $scope.approveDepositButtonDisabled = false;
    $scope.approveDepositWithdrawalButtonDisabled = false;
    $scope.approveDepositErrorResponse = "";
    $scope.approveDepositWithdrawalErrorResponse = "";
    
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
            size : pageRecordCount,
            status : ($scope.filterStatus === undefined || $scope.filterStatus === null || $scope.filterStatus === '') ? -1 : $scope.filterStatus,
            bankTransferRef : $scope.filterBankTransferRef
        };

        depositsService.listCustomerDepositAccountsForAdmin(urlParams, $scope.listCustomerDepositAccountsForAdminCallBack, $scope.errorHandler);
    });

    $scope.listCustomerDepositAccountsForAdminCallBack = function(d) {
        $scope.showSpinner = false;
        $scope.tcTable.setData(d);
    };

    $scope.refreshTableData = function() {
        $scope.tcTable.reloadTable();
    };
    
    $scope.clearFilters = function() {
        
        $scope.filterStatus = $scope.filterStatusDefault;
        $scope.filterBankTransferRef = "";
    };

    $scope.clearFiltersAndRefresh = function() {

        $scope.clearFilters();
        $scope.refreshTableData();
    };
    
    $scope.showApproveDepositModal = function(selectedRow) {

        $('#approveDepositModal').modal({
            backdrop : 'static',
            keyboard : false,
            show : true
        });
        
        $scope.selectedRow = selectedRow;
        $scope.formData = {};
    };
    
    $scope.closeApproveDepositModal = function() {

        $scope.selectedRow = {};
        $scope.formData = {};
        $scope.approveDepositErrorResponse = "";
        
        $('#approveDepositModal').modal('hide');
    };
    
    $scope.approveDeposit = function() {
        
        $scope.approveDepositErrorResponse = "";
        
        if ($scope.depositApproveForm.bankTransferRefConfirm.$invalid) {
            $scope.approveDepositErrorResponse = "Please confirm the Bank Transfer Reference.";
            
        } else if ($scope.selectedRow.bankTransferReference !== $scope.formData.bankTransferRefConfirm) {
            $scope.approveDepositErrorResponse = "The entered Bank Transfer Reference does not match with the associated product Bank Transfer Reference.";
        
        } else if ($scope.depositApproveForm.depositAmountConfirm.$invalid) {
            $scope.approveDepositErrorResponse = "Please enter the Deposit Amount (euro) as it appears on the bank statement.";
        
        } else if (parseFloat($scope.formData.depositAmountConfirm.replaceAll(',', '')) < parseFloat($scope.selectedRow.depositProductJson.depositMinAmount)) {
            $scope.approveDepositErrorResponse = "Invalid Deposit Amount. The amount entered falls below the minimum allowed for this product.";
        
        } else if (parseFloat($scope.formData.depositAmountConfirm.replaceAll(',', '')) > parseFloat($scope.selectedRow.depositProductJson.depositMaxAmount)) {
            $scope.approveDepositErrorResponse = "Invalid Deposit Amount. The amount entered is above the maximum allowed for this product.";
            
        } else if ($scope.depositApproveForm.dateStartString.$invalid) {
            $scope.approveDepositErrorResponse = "Please enter Date Transfer.";
            
        } else if (!$scope.formData.agreeTerms) {
            $scope.approveDepositErrorResponse = "You must confirm that the information entered is correct before continuing.";
        
        } else {
            
            $scope.approveDepositErrorResponse = "";
            $scope.approveDepositButtonDisabled = true;
            $scope.showSpinner = true;
            
            var reqData = {
                code : $scope.selectedRow.code,
                bankTransferReference : $scope.formData.bankTransferRefConfirm,
                depositAmount : $scope.formData.depositAmountConfirm.replaceAll(',', ''),
                dateStartString : $filter('date')($scope.formData.dateStartString, "dd/MM/yyyy")
            };
    
            depositsService.approveDeposit(reqData, function(response) {
    
                $scope.showSpinner = false;
                $scope.approveDepositErrorResponse = "";
                $scope.approveDepositButtonDisabled = false;
                $scope.closeApproveDepositModal();
                
                $rootScope.showResultMessage(true, "You have successfully approved the deposit! The customer will receive a confirmation email.");
                
                $scope.filterStatus = "2"; //ACTIVE
                $scope.refreshTableData();
                
            }, function(error) {
    
                $scope.showSpinner = false;
                $scope.approveDepositButtonDisabled = false;
                $scope.approveDepositErrorResponse = error.data.responseText;
            });
        }
    };
    
    $scope.showApproveDepositWithdrawalModal = function(selectedRow) {

        $('#approveDepositWithdrawalModal').modal({
            backdrop : 'static',
            keyboard : false,
            show : true
        });
        
        $scope.selectedRow = selectedRow;
        $scope.formData = {};
    };
    
    $scope.closeApproveDepositWithdrawalModal = function() {

        $scope.selectedRow = {};
        $scope.formData = {};
        $scope.approveDepositWithdrawalErrorResponse = "";
        
        $('#approveDepositWithdrawalModal').modal('hide');
    };
    
    $scope.approveDepositWithdrawal = function() {
        
        $scope.approveDepositWithdrawalErrorResponse = "";
        
        if (!$scope.formData.agreeTerms) {
            $scope.approveDepositWithdrawalErrorResponse = "You must confirm that you have processed the transfer to the bank account shown above.";
        
        } else {
            
            $scope.approveDepositWithdrawalErrorResponse = "";
            $scope.approveDepositWithdrawalButtonDisabled = true;
            $scope.showSpinner = true;
            
            var reqData = {
                code : $scope.selectedRow.code
            };
            
            depositsService.approveDepositWithdrawal(reqData, function(response) {
    
                $scope.showSpinner = false;
                $scope.approveDepositWithdrawalErrorResponse = "";
                $scope.approveDepositWithdrawalButtonDisabled = false;
                $scope.closeApproveDepositWithdrawalModal();
                
                $rootScope.showResultMessage(true, "You have confirmed the deposit withdrawal! The customer will receive a confirmation email.");
                
                $scope.filterStatus = "4"; //WITHDRAWN
                $scope.refreshTableData();
                
            }, function(error) {
    
                $scope.showSpinner = false;
                $scope.approveDepositWithdrawalButtonDisabled = false;
                $scope.approveDepositWithdrawalErrorResponse = error.data.responseText;
            });
        }
    };
    
    $scope.refreshTableData();
    
}]);