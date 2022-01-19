var registrationUserApp = angular.module('registrationUserApp', ['ngAnimate', 'tcCom', 'ngIntlTelInput'])

    .config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
    }])

    .config(['ngIntlTelInputProvider', function(ngIntlTelInputProvider) {
        ngIntlTelInputProvider.set({
            utilsScript : "lib/intl-tel-input-12.0.3/js/utils.js",
            initialCountry : 'ie',
            preferredCountries : [],
            autoPlaceholder : "off",
            separateDialCode : true,
            nationalMode : false
        });
    }]);

registrationUserApp.controller('registrationUserController', ['$scope', '$rootScope', 'tcComService',
    function($scope, $rootScope, tcComService) {

        $scope.init = function(isTestEnvironment, email, token, firstName, lastName) {

            $rootScope.isTetsEnvironment = isTestEnvironment;

            $scope.email = email;
            $scope.token = token;
            $scope.firstName = firstName;
            $scope.lastName = lastName;

        }

        $scope.formData = {};
        $scope.passwordRegexp = /(?=(:?.*[^A-Za-z0-9].*))(?=(:?.*[A-Z].*){1,})(?=(:?.*\d.*){1,})(:?^[\w\&\?\!\$\#\*\+\=\%\^\@\-\.\,\_]{8,32}$)/;
        $scope.registrationUserSuccess = false;
        $scope.passwordErrorResponse = "";
        $scope.passwordSubmitButtonDisabled = false;
        $scope.showSpinner = false;

        $scope.registerUser = function() {

            if ($scope.dataForm.password.$invalid) {
                $scope.passwordErrorResponse = "Please enter valid Password.";

            } else if ($scope.dataForm.passwordConfirm.$invalid) {
                $scope.passwordErrorResponse = "Please enter valid Confirm Password.";

            } else if ($scope.formData.password !== $scope.formData.passwordConfirm) {
                $scope.passwordErrorResponse = "Confirm Password does not match Password.";

            } else {
                $scope.passwordErrorResponse = "";
                $scope.passwordSubmitButtonDisabled = true;
                $scope.showSpinner = true;

                var urlParams = {
                    email : $scope.email,
                    firstName : $scope.firstName,
                    lastName : $scope.lastName,
                    password : $scope.formData.password,
                    token : $scope.token,
                };

                tcComService.callApiWithJSONParam("adminregistration/process", urlParams, function(response) {

                    $scope.showSpinner = false;
                    $scope.registrationUserSuccess = true;
                    $scope.passwordErrorResponse = "";

                }, function(error) {

                    $scope.passwordSubmitButtonDisabled = true;
                    $scope.showSpinner = false;
                    $scope.passwordErrorResponse = error.data.responseText;
                });
            }
        };

    }]);
