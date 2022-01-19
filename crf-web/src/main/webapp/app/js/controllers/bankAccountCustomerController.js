crfApp.controller('BankAccountCustomerController', ['$scope', '$rootScope', 'customersService', function($scope, $rootScope, customersService) {

    $rootScope.activeNavbar('#navbarBankAccount');
    
    $scope.showSpinner = true;
    $scope.bankDetailsLoaded = false;
    $scope.isFormReadonly = true;
    $scope.bankDetailsSubmitButtonDisabled = false;
    $scope.bankDetailsSubmitErrorResponse = "";
    
    $scope.errorHandler = function(error) {

        $scope.showSpinner = false;
        console.log(error);
    };
    
    if (!$scope.bankDetailsLoaded) {
                
        //load bank details
        $scope.formData = {
            bankName: $rootScope.customerDetails.bankAccount.bankName,
            bankAccountName: $rootScope.customerDetails.bankAccount.bankAccountName,
            bankAddress: $rootScope.customerDetails.bankAccount.bankAddress,
            iban: $rootScope.customerDetails.bankAccount.iban,
            bic: $rootScope.customerDetails.bankAccount.bic
        };
                
        $scope.bankDetailsLoaded = true;
    }
    
    $scope.editBankDetails = function() {
        
        $scope.isFormReadonly = false;
        $scope.bankDetailsSubmitErrorResponse = "";
    };
    
    $scope.cancelEditBankDetails = function() {
        
        $scope.dataFormBankDetails.$setPristine();
        $scope.isFormReadonly = true;
        $scope.bankDetailsSubmitErrorResponse = "";
        $scope.bankDetailsSubmitButtonDisabled = false;
        
        //reload bank details
        $scope.formData = {
            bankName: $rootScope.customerDetails.bankAccount.bankName,
            bankAccountName: $rootScope.customerDetails.bankAccount.bankAccountName,
            bankAddress: $rootScope.customerDetails.bankAccount.bankAddress,
            iban: $rootScope.customerDetails.bankAccount.iban,
            bic: $rootScope.customerDetails.bankAccount.bic
        };
    };
    
    $scope.submitBankDetails = function() {

        if ($scope.dataFormBankDetails.bankName.$invalid) {

            $scope.bankDetailsSubmitErrorResponse = "Please enter a valid Bank Name.";

        } else if ($scope.dataFormBankDetails.bankAccountName.$invalid) {

            $scope.bankDetailsSubmitErrorResponse = "Please enter a valid Bank Account Name.";
            
        } else if ($scope.dataFormBankDetails.bankAddress.$invalid) {

            $scope.bankDetailsSubmitErrorResponse = "Please enter a valid Bank Address.";

        } else if ($scope.dataFormBankDetails.iban.$invalid) {

            $scope.bankDetailsSubmitErrorResponse = "Please enter a valid IBAN. The IBAN number consists of a two-letter country code, followed by two check digits, and up to thirty-five alphanumeric characters.";

        } else if ($scope.dataFormBankDetails.bic.$invalid) {

            $scope.bankDetailsSubmitErrorResponse = "Please enter a valid BIC. BIC means Bank Identification Code, or Bank Identifier Code. " + 
            "It is an eight to eleven character code that is used to identify a specific bank when you make an international transaction.";

        } else {

            $scope.bankDetailsSubmitButtonDisabled = true;
            $scope.showSpinner = true;

            var bankData = {

                bankName : $scope.formData.bankName,
                bankAccountName : $scope.formData.bankAccountName,
                bankAddress : $scope.formData.bankAddress,
                iban : $scope.formData.iban,
                bic : $scope.formData.bic
            };

            customersService.submitBankDetails(bankData, function(response) {

                $scope.dataFormBankDetails.$setPristine();
                $scope.isFormReadonly = true;
                $scope.showSpinner = false;
                $scope.bankDetailsSubmitErrorResponse = "";
                $scope.bankDetailsSubmitButtonDisabled = false;

                $rootScope.showResultMessage(true, "Your bank account details have been successfully submitted!");
                
                //refresh rootScope data as they have just changed
                $rootScope.customerDetails.bankAccount.bankName = $scope.formData.bankName;
                $rootScope.customerDetails.bankAccount.bankAccountName = $scope.formData.bankAccountName;
                $rootScope.customerDetails.bankAccount.bankAddress = $scope.formData.bankAddress;
                $rootScope.customerDetails.bankAccount.iban = $scope.formData.iban;
                $rootScope.customerDetails.bankAccount.bic = $scope.formData.bic;

            }, function(error) {

                $scope.bankDetailsSubmitButtonDisabled = false;
                $scope.showSpinner = false;
                $scope.bankDetailsSubmitErrorResponse = error.data.responseText;
            });
        }
    };
    
    $scope.showSpinner = false;
    
}]);