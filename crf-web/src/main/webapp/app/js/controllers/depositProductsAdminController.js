crfApp.controller('DepositProductsAdminController', ['$scope', '$rootScope','depositProductsService', function ($scope, $rootScope, depositProductsService) {

    $rootScope.activeNavbar('#navbarProducts');

    $scope.numberRegexp = /^\d+$/;
    $scope.isPopupShowed = false;
    $scope.showSpinner = false;
    $scope.isEditing = false;
    $scope.editProductErrorMessage = '';
    $scope.formData = {};
    $scope.popupTitle = '';
    $scope.isPopupForEditing = false;

    $scope.showPopup = function (flag) {
        $scope.isPopupShowed = flag;
    }

    $scope.errorHandler = function (error) {

        $scope.showSpinner = false;
        console.log(error);
    };

    $scope.tcTable = new TCTable(function (currentPage, pageRecordCount) {

        $scope.showSpinner = true;
        $scope.sortType = null;
        $scope.sortReverse = false;

        var urlParams = {
            page: currentPage - 1,
            size: pageRecordCount
        };

        depositProductsService.listDepositProductsForAdmin(urlParams, $scope.listDepositProductsCallBack, $scope.errorHandler);
    });

    $scope.listDepositProductsCallBack = function (d) {

        $scope.showSpinner = false;
        $scope.tcTable.setData(d);

    };

    $scope.refreshTableData = function () {
        $scope.tcTable.reloadTable();
    };

    $scope.showEditPopup = function (row) {

        $scope.showPopup(true);

        $scope.formData = {...row};

        $scope.editProductErrorMessage = '';
        $scope.popupTitle = 'Edit product';
        $scope.isPopupForEditing = true;
    }

    $scope.editProduct = function() {

        const minAmount = 5000;
        const minRate = 0.01;
        const maxRate = 10;
        const minFeeAndDays = 0;
        const maxFee = 1000;
        const minYears = 1;
        const maxDays = 80;

        if ($scope.editProductFormData.name.$invalid) {
            $scope.editProductErrorMessage = 'Please enter a valid Name.';

        } else if ($scope.editProductFormData.depositMinAmount.$invalid
            || parseFloat($scope.formData.depositMinAmount.replaceAll(',', '')) < minAmount) {

            $scope.editProductErrorMessage = 'Please enter a valid Minimum deposit amount, min value allowed = 5000.00';

        } else if ($scope.editProductFormData.depositMaxAmount.$invalid
            || parseFloat($scope.formData.depositMinAmount.replaceAll(',', '')) > parseFloat($scope.formData.depositMaxAmount.replaceAll(',', ''))) {

            $scope.editProductErrorMessage = 'Please enter a valid Maximum deposit amount, max value cannot be less than Minimum deposit amount value.';

        } else if ($scope.editProductFormData.quarterlyInterestRate.$invalid
            || parseFloat($scope.formData.quarterlyInterestRate.replaceAll(',', '')) < minRate
            || parseFloat($scope.formData.quarterlyInterestRate.replaceAll(',', '')) > maxRate) {

            $scope.editProductErrorMessage = 'Please enter a valid Quarterly Interest Rate, min value = 0.01, max value = 10.00.';

        } else if ($scope.editProductFormData.yearlyInterestRate.$invalid
            || parseFloat($scope.formData.yearlyInterestRate.replaceAll(',', '')) < minRate
            || parseFloat($scope.formData.yearlyInterestRate.replaceAll(',', '')) > maxRate) {

            $scope.editProductErrorMessage = 'Please enter a valid Yearly Interest Rate, min value = 0.01, max value = 10.00.';

        } else if ($scope.editProductFormData.twiceYearlyInterestRate.$invalid
            || parseFloat($scope.formData.twiceYearlyInterestRate.replaceAll(',', '')) < minRate
            || parseFloat($scope.formData.twiceYearlyInterestRate.replaceAll(',', '')) > maxRate) {

            $scope.editProductErrorMessage = 'Please enter a valid Twice-Yearly Interest Rate, min value = 0.01, max value = 10.00.';

        } else if ($scope.editProductFormData.termYears.$invalid
            || $scope.formData.termYears < minYears) {

            $scope.editProductErrorMessage = 'Invalid Term Years. Please enter a valid Term: min value = 1, max value = 99.';

        } else if ($scope.editProductFormData.prematureWithdrawalMinDays.$invalid
            || $scope.formData.prematureWithdrawalMinDays < minFeeAndDays
            || $scope.formData.prematureWithdrawalMinDays > maxDays) {

            $scope.editProductErrorMessage = 'Please enter a valid Premature withdrawal min days, max value = 80.';

        } else if ($scope.editProductFormData.prematureWithdrawalInterestRate.$invalid
            || parseFloat($scope.formData.prematureWithdrawalInterestRate.replaceAll(',', '')) < minRate
            || parseFloat($scope.formData.prematureWithdrawalInterestRate.replaceAll(',', '')) > maxRate) {

            $scope.editProductErrorMessage = 'Please enter a valid Premature withdrawal interest rate, min value = 0.01, max value = 10.00.';

        } else if ($scope.editProductFormData.withdrawalFee.$invalid
            || parseFloat($scope.formData.withdrawalFee.replaceAll(',', '')) < minFeeAndDays
            || parseFloat($scope.formData.withdrawalFee.replaceAll(',', '')) > maxFee) {

            $scope.editProductErrorMessage = 'Please enter a valid Withdrawal fee, max value = 1000.00.';

        } else if ($scope.editProductFormData.description.$invalid) {

            $scope.editProductErrorMessage = 'Please enter a valid Description';

        } else {
            $scope.showSpinner = true;
            $scope.isEditing = true;

            if ($scope.isPopupForEditing === true) {

                let urlParams = {
                    code: $scope.formData.code,
                    depositMaxAmount: $scope.formData.depositMaxAmount.replaceAll(',', ''),
                    depositMinAmount: $scope.formData.depositMinAmount.replaceAll(',', ''),
                    description: $scope.formData.description,
                    name: $scope.formData.name,
                    prematureWithdrawalInterestRate: $scope.formData.prematureWithdrawalInterestRate.replaceAll(',', ''),
                    prematureWithdrawalMinDays: $scope.formData.prematureWithdrawalMinDays,
                    quarterlyInterestRate: $scope.formData.quarterlyInterestRate.replaceAll(',', ''),
                    termYears: $scope.formData.termYears,
                    twiceYearlyInterestRate: $scope.formData.twiceYearlyInterestRate.replaceAll(',', ''),
                    withdrawalFee: $scope.formData.withdrawalFee.replaceAll(',', ''),
                    yearlyInterestRate: $scope.formData.yearlyInterestRate.replaceAll(',', '')
                };

                depositProductsService.editProduct(urlParams, function(response){

                    $rootScope.showResultMessage(true, "Product edited!");

                    $scope.showSpinner = false;
                    $scope.isEditing = false;
                    $scope.formData = {};
                    $scope.editProductErrorMessage = '';

                    $scope.showPopup(false);

                    $scope.refreshTableData();

                },function(error) {
                    $scope.showSpinner = false;
                    $scope.isEditing = false;
                    $scope.editProductErrorMessage = error.data.responseText;
                });

            }

        }

    };

    $scope.refreshTableData();

}]);
