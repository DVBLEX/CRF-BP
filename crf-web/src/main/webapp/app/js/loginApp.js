var loginApp = angular.module('loginApp', [ 'ngResource', 'ngAnimate', 'vcRecaptcha', 'tcCom' ])

.config([ '$httpProvider', function($httpProvider) {

    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
} ])

.controller('passwordForgotController', [ '$scope', 'vcRecaptchaService', 'tcComService', function($scope, vcRecaptchaService, tcComService) {

    $scope.emailRegexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}])|(([\w\-]+\.)+[a-zA-Z]{2,}))$/;
    $scope.passwdForgotSendSuccess = false;
    $scope.passwordForgotStep1ErrorResponse = "";
    $scope.showSpinner = false;

    $scope.setResponse = function(response) {
        $scope.response = response;
    };

    $scope.setWidgetId = function(widgetId) {
        $scope.widgetId = widgetId;
    };

    $scope.cbExpiration = function() {
        vcRecaptchaService.reload($scope.widgetId);
    };

    $scope.errorHandler = function(error) {

        $scope.showSpinner = false;
        vcRecaptchaService.reload($scope.widgetId);
        console.log(error);
    };

    $scope.passwordForgotSubmit = function() {

        if ($scope.dataForm.input1.$invalid) {
            $scope.passwordForgotStep1ErrorResponse = "Please enter valid Email.";
            return;
        }

        if (vcRecaptchaService.getResponse($scope.widgetId) === "") {
            $scope.passwordForgotStep1ErrorResponse = "Please resolve the captcha.";
            return;
        }

        $scope.showSpinner = true;

        var urlParams = {
            input1 : $scope.emailSend,
            recaptchaResponse : vcRecaptchaService.getResponse($scope.widgetId)
        };

        tcComService.callAPI("login/password/forgot/send", urlParams, function(response) {

            $scope.showSpinner = false;
            $scope.passwdForgotSendSuccess = true;
            $scope.passwordForgotStep1ErrorResponse = "";

        }, function(error) {

            $scope.showSpinner = false;
            vcRecaptchaService.reload($scope.widgetId);
            $scope.passwordForgotStep1ErrorResponse = error.data.responseText;
        });
    };

} ])

.controller('passwordForgotChangeController', [ '$scope', 'vcRecaptchaService', 'tcComService', function($scope, vcRecaptchaService, tcComService) {

    $scope.passwordRegexp = /(?=(:?.*[^A-Za-z0-9].*))(?=(:?.*[A-Z].*){1,})(?=(:?.*\d.*){1,})(:?^[\w\&\?\!\$\#\*\+\=\%\^\@\-\.\,\_]{8,32}$)/;
    $scope.passwdChangeSuccess = false;
    $scope.passwordForgotStep2ErrorResponse = "";
    $scope.passwordForgotStep2SubmitButtonDisabled = false;
    $scope.showSpinner = false;
    $scope.passwdFocused = true;

    $scope.setResponse = function(response) {
        $scope.response = response;
    };

    $scope.setWidgetId = function(widgetId) {
        $scope.widgetId = widgetId;
    };

    $scope.cbExpiration = function() {
        vcRecaptchaService.reload($scope.widgetId);
    };

    $scope.errorHandler = function(error) {

        $scope.showSpinner = false;
        vcRecaptchaService.reload($scope.widgetId);
        console.log(error);
    };

    $scope.passwordForgotChange = function() {

        if ($scope.dataForm.password.$invalid) {
            $scope.passwordForgotStep2ErrorResponse = "Please enter valid Password.";

        } else if ($scope.dataForm.passwordConfirm.$invalid) {
            $scope.passwordForgotStep2ErrorResponse = "Please enter valid Confirm Password.";

        } else if ($scope.formData.password !== $scope.formData.passwordConfirm) {
            $scope.passwordForgotStep2ErrorResponse = "Confirm Password does not match Password.";

        } else if (vcRecaptchaService.getResponse($scope.widgetId) === "") {
            $scope.passwordForgotStep2ErrorResponse = "Please resolve the captcha.";

        } else {
            $scope.passwordForgotStep2ErrorResponse = "";
            $scope.passwordForgotStep2SubmitButtonDisabled = true;
            $scope.showSpinner = true;

            var urlParams = {
                input1 : $scope.input1,
                input2 : $scope.formData.password,
                input3 : $scope.formData.passwordConfirm,
                input4 : $scope.input4,
                input5 : $scope.input5,
                recaptchaResponse : vcRecaptchaService.getResponse($scope.widgetId)
            };

            tcComService.callAPI("login/password/forgot/change", urlParams, function(response) {

                $scope.showSpinner = false;
                $scope.passwdChangeSuccess = true;
                $scope.passwordForgotStep2ErrorResponse = "";

            }, function(error) {

                $scope.passwordForgotStep2SubmitButtonDisabled = true;
                $scope.showSpinner = false;
                vcRecaptchaService.reload($scope.widgetId);
                $scope.passwordForgotStep2ErrorResponse = error.data.responseText;
            });
        }
    };

} ])

.controller('credentialsExpiredController', [ '$scope', 'vcRecaptchaService', 'tcComService', function($scope, vcRecaptchaService, tcComService) {

    $scope.passwordRegexp = /(?=(:?.*[^A-Za-z0-9].*))(?=(:?.*[A-Z].*){1,})(?=(:?.*\d.*){1,})(:?^[\w\&\?\!\$\#\*\+\=\%\^\@\-\.\,\_]{8,32}$)/;
    $scope.passwdUpdateSuccess = false;
    $scope.credentialsExpiredUpdateErrorResponse = "";
    $scope.showSpinner = false;

    $scope.setResponse = function(response) {
        $scope.response = response;
    };

    $scope.setWidgetId = function(widgetId) {
        $scope.widgetId = widgetId;
    };

    $scope.cbExpiration = function() {
        vcRecaptchaService.reload($scope.widgetId);
    };

    $scope.errorHandler = function(error) {

        $scope.showSpinner = false;
        vcRecaptchaService.reload($scope.widgetId);
        console.log(error);
    };

    $scope.passwordUpdate = function() {

        if ($scope.dataForm.passwordOld.$invalid) {
            $scope.credentialsExpiredUpdateErrorResponse = "Please enter a valid Current Password.";
            
        } else if ($scope.dataForm.password.$invalid) {
            $scope.credentialsExpiredUpdateErrorResponse = "Please enter a valid New Password.";

        } else if ($scope.dataForm.passwordConfirm.$invalid) {
            $scope.credentialsExpiredUpdateErrorResponse = "Please enter a valid Confirm Password.";

        } else if ($scope.formData.password !== $scope.formData.passwordConfirm) {
            $scope.credentialsExpiredUpdateErrorResponse = "Confirm Password does not match New Password.";

        } else if (vcRecaptchaService.getResponse($scope.widgetId) === "") {
            $scope.credentialsExpiredUpdateErrorResponse = "Please resolve the captcha and submit!";
            
        } else {
            $scope.showSpinner = true;
    
            var urlParams = {
                input1 : $scope.userName,
                input2 : $scope.formData.passwordOld,
                input3 : $scope.formData.password,
                input4 : $scope.formData.passwordConfirm,
                input5 : $scope.t1,
                input6 : $scope.t2,
                recaptchaResponse : vcRecaptchaService.getResponse($scope.widgetId)
            };
    
            tcComService.callAPI("login/password/expired/update", urlParams, function(response) {
    
                $scope.showSpinner = false;
                $scope.passwdUpdateSuccess = true;
                $scope.credentialsExpiredUpdateErrorResponse = "";
                $scope.formData = {};
    
            }, function(error) {
    
                $scope.showSpinner = false;
                vcRecaptchaService.reload($scope.widgetId);
                $scope.credentialsExpiredUpdateErrorResponse = error.data.responseText;
            });
        }
    };

} ]);

loginApp.directive('compareTo', function() {
    return {
        require : "ngModel",
        scope : {
            otherModelValue : "=compareTo"
        },
        link : function(scope, element, attributes, ngModel) {

            ngModel.$validators.compareTo = function(modelValue) {
                return modelValue == scope.otherModelValue;
            };

            scope.$watch("otherModelValue", function() {
                ngModel.$validate();
            });
        }
    };
});
