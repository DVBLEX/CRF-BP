crfApp.controller('DepositStatementController', ['$scope', '$rootScope', 'depositsService', function($scope, $rootScope, depositsService) {

    $rootScope.activeNavbar('#navbarDepositStatements');
    
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

        depositsService.listCustomerDepositAccountStatements(urlParams, $scope.listCustomerDepositAccountStatementsCallBack, $scope.errorHandler);
    });

    $scope.listCustomerDepositAccountStatementsCallBack = function(d) {
        $scope.showSpinner = false;
        $scope.tcTable.setData(d);
    };

    $scope.refreshTableData = function() {
        $scope.tcTable.reloadTable();
    };
    
    $scope.refreshTableData();
    
}]);