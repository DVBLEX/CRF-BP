crfApp.controller('DepositsDocumentController', ['$scope', '$rootScope', '$timeout', 'depositsService', function($scope, $rootScope, $timeout, depositsService) {

    $rootScope.activeNavbar('#navbarDepositDocuments');
    
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

        depositsService.listCustomerDepositAccountDocuments(urlParams, $scope.listCustomerDepositAccountDocumentsCallBack, $scope.errorHandler);
    });

    $scope.listCustomerDepositAccountDocumentsCallBack = function(d) {
        $scope.showSpinner = false;
        $scope.tcTable.setData(d);
    };

    $scope.refreshTableData = function() {
        $scope.tcTable.reloadTable();
    };
    
    $scope.downloadDepositAccountDocument = function(row) {

        row.downloaded = true;
        
        $timeout(function(){
            row.downloaded = false;
        }, 60000);
    }
    
    $scope.refreshTableData();
    
}]);