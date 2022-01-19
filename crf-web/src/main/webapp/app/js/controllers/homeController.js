crfApp.controller('HomeController', ['$scope', '$rootScope', 'homeService', 'customersService', 'depositsService', function($scope, $rootScope, homeService, customersService, depositsService) {

    $rootScope.activeNavbar('#navbarHome');

    $scope.emailRegexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}])|(([\w\-]+\.)+[a-zA-Z]{2,}))$/;
    $scope.isSendingRegLink = false;
    $scope.isConfirmingProductInterest = false;
    $scope.createDepositButtonDisabled = true;
    $scope.sendRegLinkErrorMessage = "";
    $scope.createDepositInfoMessage = "";
    $scope.createDepositErrorResponse = "";
    $scope.depositProductList = [];
    $scope.selectedDepositProduct = {};
    $scope.bankDetailsObject = {};
    $scope.investorStatsObject = {};
    $scope.adminStatsObject = {};
    
    $scope.formData = {};
    
    $scope.sendRegLink = function() {

        if ($scope.sendreglinkFormData.title.$invalid) {
            $scope.sendRegLinkErrorMessage = "Please select Title.";
            
        } else if ($scope.formData.firstName === undefined || $scope.formData.firstName === null || $scope.formData.firstName === "") {
            $scope.sendRegLinkErrorMessage = "Please enter First Name.";
            
        } else if ($scope.formData.lastName === undefined || $scope.formData.lastName === null || $scope.formData.lastName === "") {
            $scope.sendRegLinkErrorMessage = "Please enter Last Name.";
            
        } else if ($scope.sendreglinkFormData.email.$invalid) {
            $scope.sendRegLinkErrorMessage = "Please enter a valid Email";
            
        } else if ($scope.sendreglinkFormData.customerType.$invalid) {
            $scope.sendRegLinkErrorMessage = "Please select Customer Type.";
            
        } else if ($scope.sendreglinkFormData.customerCategory.$invalid) {
            $scope.sendRegLinkErrorMessage = "Please select Customer Category.";

        } else {
            $scope.showSpinner = true;
            $scope.isSendingRegLink = true;

            var urlParams = {
                title : $scope.formData.title,
                firstName : $scope.formData.firstName,
                lastName : $scope.formData.lastName,
                email : $scope.formData.email,
                type : parseInt($scope.formData.customerType),
                category : parseInt($scope.formData.customerCategory)
            };

            customersService.emailRegURL(urlParams, function(response){

                $rootScope.showResultMessage(true, "An email with the registration page has been sent to " + $scope.formData.email + "!");
                
                $scope.showSpinner = false;
                $scope.isSendingRegLink = false;
                $scope.formData = {};
                $scope.sendreglinkFormData.$setPristine();
                $scope.sendreglinkFormData.$setUntouched();
                $scope.sendRegLinkErrorMessage = "";

            },function(error) {
                $scope.showSpinner = false;
                $scope.isSendingRegLink = false;
                $scope.sendRegLinkErrorMessage = error.data.responseText;
            });
        }
    };
    
    $scope.listDepositProducts = function() {

        $scope.showSpinner = true;

        homeService.listDepositProducts({}, function(response) {

            $scope.depositProductList = response.dataList;
            
            if ($rootScope.isAdmin == "true") {
                $scope.adminStatsObject = response.singleData;
                
            } else if ($rootScope.isInvestorOperator == "true") {
                $scope.investorStatsObject = response.singleData;    
            }
            
            $scope.showSpinner = false;

        }, $scope.errorHandler);
    };
    
    $scope.showCreateDepositModal = function(depositProduct) {

        $('#createDepositModal').modal({
            backdrop : 'static',
            keyboard : false,
            show : true
        });
        
        $scope.selectedDepositProduct = depositProduct;
        $scope.createDepositInfoMessage = "";
        $scope.formData = {};
        $scope.formData.interestPayoutFrequency = "2"; // 1 = quarterly; 2 = yearly
        $scope.isConfirmingProductInterest = false;
    };
    
    $scope.closeCreateDepositModal = function() {

        $scope.selectedDepositProduct = {};
        $scope.formData = {};
        $scope.createDepositInfoMessage = "";
        $scope.createDepositErrorResponse = "";
        $scope.isConfirmingProductInterest = false;
        $scope.bankTransferRef = "";
        
        $('#createDepositModal').modal('hide');
    };
    
    $scope.createDeposit = function() {
        
        if ($scope.depositCreateForm.interestPayoutFrequency.$invalid) {
            $scope.createDepositErrorResponse = "Please select Interest Payout Frequency.";
        
        } else if ($scope.depositCreateForm.depositAmount.$invalid) {
            $scope.createDepositErrorResponse = "Please enter Deposit Amount.";
            
        } else if ($scope.depositCreateForm.depositAmountConfirm.$invalid) {
            $scope.createDepositErrorResponse = "Please enter Confirm Deposit Amount.";
            
        } else if ($scope.formData.depositAmount !== $scope.formData.depositAmountConfirm) {
            $scope.createDepositErrorResponse = "The entered Deposit Amount does not match with Confirm Deposit Amount.";
            
        } else {
            
            $scope.createDepositErrorResponse = "";
            $scope.createDepositButtonDisabled = true;
            
            $scope.showSpinner = true;
    
            var reqData = {
                bankTransferReference : $scope.bankTransferRef,
                depositAmount : $scope.formData.depositAmount.replaceAll(',', ''),
                interestPayoutFrequency : parseInt($scope.formData.interestPayoutFrequency),
                interestRate : $scope.formData.interestPayoutFrequency === "1" ? $scope.selectedDepositProduct.quarterlyInterestRate : $scope.selectedDepositProduct.yearlyInterestRate,
                termYears : $scope.selectedDepositProduct.termYears,
                depositProductJson : {
                    code : $scope.selectedDepositProduct.code
                }
            };
    
            depositsService.saveDepositAccount(reqData, function(response) {
    
                $scope.createDepositErrorResponse = "";
                $scope.closeCreateDepositModal();
                $('#createDepositModal').modal('hide').on('hidden.bs.modal', $scope.saveDepositAccountCallbackOnModalClose); // used to handle a condition where modal is not fully closed before page is changed
                $scope.showSpinner = false;
                $rootScope.showResultMessage(true, "You have successfully added the product to your customer account! You will receive an email with further details.");
                
            }, function(error) {
    
                $scope.isConfirmingProductInterest = false;
                $scope.showSpinner = false;
                $scope.createDepositErrorResponse = error.data.responseText;
            });
        }
    };
    
    $scope.saveDepositAccountCallbackOnModalClose = function() {
        
        $rootScope.go('/customer/deposits');
    };
    
    $scope.confirmDepositAmount = function() {
        
        if ($scope.depositCreateForm.interestPayoutFrequency.$invalid) {
            $scope.createDepositErrorResponse = "Please select Interest Payout Frequency.";
        
        } else if ($scope.depositCreateForm.depositAmount.$invalid) {
            $scope.createDepositErrorResponse = "Please enter Deposit Amount.";
            
        } else if ($scope.depositCreateForm.depositAmountConfirm.$invalid) {
            $scope.createDepositErrorResponse = "Please enter Confirm Deposit Amount.";
            
        } else if ($scope.formData.depositAmount !== $scope.formData.depositAmountConfirm) {
            $scope.createDepositErrorResponse = "The entered Deposit Amount does not match with Confirm Deposit Amount.";
            
        } else if (parseFloat($scope.formData.depositAmount.replaceAll(',', '')) < parseFloat($scope.selectedDepositProduct.depositMinAmount)) {
            $scope.createDepositErrorResponse = "Invalid Deposit Amount. Please enter amount between the minimum and maximum allowed for this product.";
            
        } else if (parseFloat($scope.formData.depositAmount.replaceAll(',', '')) > parseFloat($scope.selectedDepositProduct.depositMaxAmount)) {
            $scope.createDepositErrorResponse = "Invalid Deposit Amount. Please enter amount between the minimum and maximum allowed for this product.";
            
        } else {
        
            $scope.createDepositInfoMessage = "";
            $scope.createDepositErrorResponse = "";
            $scope.bankTransferRef = "";
            $scope.bankDetailsObject = {};
        
            $scope.isConfirmingProductInterest = true;
            $scope.showSpinner = true;
    
            var reqData = {
                depositAmount : $scope.formData.depositAmount.replaceAll(',', ''),
                interestRate : $scope.formData.interestPayoutFrequency === "1" ? $scope.selectedDepositProduct.quarterlyInterestRate : $scope.selectedDepositProduct.yearlyInterestRate,
                termYears : $scope.selectedDepositProduct.termYears,
                depositProductJson : {
                    code : $scope.selectedDepositProduct.code
                }
            };
    
            homeService.calcDepositProductInterest(reqData, function(response) {
    
                $scope.showSpinner = false;
                $scope.createDepositErrorResponse = "";
                $scope.createDepositInfoMessage = "Please note, if the term starts today, at the end of the term on <b>" + response.data.dateMaturityString + "</b>, a total of <b>€" + response.data.totalInterest 
                    + "</b> in interest will be earned.</br>The actual term will start from the date the deposit is received.</br>There could be a bank transfer fee so it is advised to" 
                    + " add &euro;50 extra in order to offset the potential bank fee.</br>" 
                    +"Please transfer your deposit along with the transfer reference to the bank account shown below.";
                $scope.bankTransferRef = response.data.bankTransferReference;
                $scope.bankDetailsObject = response.data.bankDetailsJson;
                $scope.createDepositButtonDisabled = false;
                
            }, function(error) {
    
                $scope.isConfirmingProductInterest = false;
                $scope.showSpinner = false;
                $scope.createDepositErrorResponse = error.data.responseText;
            });
        }
    };
    
    $scope.getCustomerDetails = function() {

        customersService.getCustomerDetails(function(data) {

            $rootScope.customerDetails = data;

        }, $rootScope.errorHandler);
    };
    
    $scope.changeDepositAmount = function() {
        
         $scope.isConfirmingProductInterest = false;
         $scope.createDepositButtonDisabled = true;
    };
    
    if ($rootScope.isAdmin == 'true') {
        $scope.listDepositProducts();
        
    } else if ($rootScope.isInvestorOperator == 'true') {
        $scope.listDepositProducts();
        $scope.getCustomerDetails();
    }
    
}]);
