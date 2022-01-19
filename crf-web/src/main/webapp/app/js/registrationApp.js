var registrationApp = angular.module('registrationApp', ['ngAnimate', 'vcRecaptcha', 'tcCom', 'ngIntlTelInput'])

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

registrationApp.controller('registrationController', ['$scope', '$rootScope', "$filter", '$window', 'vcRecaptchaService', 'registrationService',
    function($scope, $rootScope, $filter, $window, vcRecaptchaService, registrationService) {

        $scope.init = function(isTestEnvironment, email, title, firstName, lastName) {
            $rootScope.isTestEnvironment = isTestEnvironment;

            $scope.email = email;
            $scope.title = title;
            $scope.firstName = firstName;
            $scope.lastName = lastName;
        }

        $scope.formData = {};
        $scope.registrationSuccess = false;
        $scope.step1IsCompleted = false;
        $scope.step2IsCompleted = false;
        $scope.step3IsCompleted = false;
        $scope.step4IsCompleted = false;
        $scope.step5IsCompleted = false;
        $scope.formStepsSelected = [true, false, false, false, false];
        $scope.verifyCodeHasSent = false;
        $scope.step1ErrorMessage = "";
        $scope.msisdnVerificationStep1ErrorResponse = "";
        $scope.msisdnVerificationStep2ErrorResponse = "";
        $scope.passwordSubmittedSuccess = false;
        $scope.passwordSubmitErrorResponse = "";
        $scope.isMsisdnVerified = false;
        $scope.step4ErrorMessage = "";
        $scope.registrationErrorResponse = "";
        $scope.regSubmitButtonDisabled = false;
        $scope.passwordRegexp = /(?=(:?.*[^A-Za-z0-9].*))(?=(:?.*[A-Z].*){1,})(?=(:?.*\d.*){1,})(:?^[\w\&\?\!\$\#\*\+\=\%\^\@\-\.\,\_]{8,32}$)/;
        $scope.dateTodayString = $filter('date')(new Date(), "yyyy-MM-dd");

        $scope.countries = [ 
            {name: 'Afghanistan', code: 'AF'},
            {name: 'Åland Islands', code: 'AX'},
            {name: 'Albania', code: 'AL'},
            {name: 'Algeria', code: 'DZ'},
            {name: 'American Samoa', code: 'AS'},
            {name: 'Andorra', code: 'AD'},
            {name: 'Angola', code: 'AO'},
            {name: 'Anguilla', code: 'AI'},
            {name: 'Antarctica', code: 'AQ'},
            {name: 'Antigua and Barbuda', code: 'AG'},
            {name: 'Argentina', code: 'AR'},
            {name: 'Armenia', code: 'AM'},
            {name: 'Aruba', code: 'AW'},
            {name: 'Australia', code: 'AU'},
            {name: 'Austria', code: 'AT'},
            {name: 'Azerbaijan', code: 'AZ'},
            {name: 'Bahamas', code: 'BS'},
            {name: 'Bahrain', code: 'BH'},
            {name: 'Bangladesh', code: 'BD'},
            {name: 'Barbados', code: 'BB'},
            {name: 'Belarus', code: 'BY'},
            {name: 'Belgium', code: 'BE'},
            {name: 'Belize', code: 'BZ'},
            {name: 'Benin', code: 'BJ'},
            {name: 'Bermuda', code: 'BM'},
            {name: 'Bhutan', code: 'BT'},
            {name: 'Bolivia', code: 'BO'},
            {name: 'Bosnia and Herzegovina', code: 'BA'},
            {name: 'Botswana', code: 'BW'},
            {name: 'Bouvet Island', code: 'BV'},
            {name: 'Brazil', code: 'BR'},
            {name: 'British Indian Ocean Territory', code: 'IO'},
            {name: 'Brunei Darussalam', code: 'BN'},
            {name: 'Bulgaria', code: 'BG'},
            {name: 'Burkina Faso', code: 'BF'},
            {name: 'Burundi', code: 'BI'},
            {name: 'Cambodia', code: 'KH'},
            {name: 'Cameroon', code: 'CM'},
            {name: 'Canada', code: 'CA'},
            {name: 'Cape Verde', code: 'CV'},
            {name: 'Cayman Islands', code: 'KY'},
            {name: 'Central African Republic', code: 'CF'},
            {name: 'Chad', code: 'TD'},
            {name: 'Chile', code: 'CL'},
            {name: 'China', code: 'CN'},
            {name: 'Christmas Island', code: 'CX'},
            {name: 'Cocos (Keeling) Islands', code: 'CC'},
            {name: 'Colombia', code: 'CO'},
            {name: 'Comoros', code: 'KM'},
            {name: 'Congo', code: 'CG'},
            {name: 'Congo, The Democratic Republic of the', code: 'CD'},
            {name: 'Cook Islands', code: 'CK'},
            {name: 'Costa Rica', code: 'CR'},
            {name: 'Cote D\'Ivoire', code: 'CI'},
            {name: 'Croatia', code: 'HR'},
            {name: 'Cuba', code: 'CU'},
            {name: 'Cyprus', code: 'CY'},
            {name: 'Czech Republic', code: 'CZ'},
            {name: 'Denmark', code: 'DK'},
            {name: 'Djibouti', code: 'DJ'},
            {name: 'Dominica', code: 'DM'},
            {name: 'Dominican Republic', code: 'DO'},
            {name: 'Ecuador', code: 'EC'},
            {name: 'Egypt', code: 'EG'},
            {name: 'El Salvador', code: 'SV'},
            {name: 'Equatorial Guinea', code: 'GQ'},
            {name: 'Eritrea', code: 'ER'},
            {name: 'Estonia', code: 'EE'},
            {name: 'Ethiopia', code: 'ET'},
            {name: 'Falkland Islands (Malvinas)', code: 'FK'},
            {name: 'Faroe Islands', code: 'FO'},
            {name: 'Fiji', code: 'FJ'},
            {name: 'Finland', code: 'FI'},
            {name: 'France', code: 'FR'},
            {name: 'French Guiana', code: 'GF'},
            {name: 'French Polynesia', code: 'PF'},
            {name: 'French Southern Territories', code: 'TF'},
            {name: 'Gabon', code: 'GA'},
            {name: 'Gambia', code: 'GM'},
            {name: 'Georgia', code: 'GE'},
            {name: 'Germany', code: 'DE'},
            {name: 'Ghana', code: 'GH'},
            {name: 'Gibraltar', code: 'GI'},
            {name: 'Greece', code: 'GR'},
            {name: 'Greenland', code: 'GL'},
            {name: 'Grenada', code: 'GD'},
            {name: 'Guadeloupe', code: 'GP'},
            {name: 'Guam', code: 'GU'},
            {name: 'Guatemala', code: 'GT'},
            {name: 'Guernsey', code: 'GG'},
            {name: 'Guinea', code: 'GN'},
            {name: 'Guinea-Bissau', code: 'GW'},
            {name: 'Guyana', code: 'GY'},
            {name: 'Haiti', code: 'HT'},
            {name: 'Heard Island and Mcdonald Islands', code: 'HM'},
            {name: 'Holy See (Vatican City State)', code: 'VA'},
            {name: 'Honduras', code: 'HN'},
            {name: 'Hong Kong', code: 'HK'},
            {name: 'Hungary', code: 'HU'},
            {name: 'Iceland', code: 'IS'},
            {name: 'India', code: 'IN'},
            {name: 'Indonesia', code: 'ID'},
            {name: 'Iran, Islamic Republic Of', code: 'IR'},
            {name: 'Iraq', code: 'IQ'},
            {name: 'Ireland', code: 'IE'},
            {name: 'Isle of Man', code: 'IM'},
            {name: 'Israel', code: 'IL'},
            {name: 'Italy', code: 'IT'},
            {name: 'Jamaica', code: 'JM'},
            {name: 'Japan', code: 'JP'},
            {name: 'Jersey', code: 'JE'},
            {name: 'Jordan', code: 'JO'},
            {name: 'Kazakhstan', code: 'KZ'},
            {name: 'Kenya', code: 'KE'},
            {name: 'Kiribati', code: 'KI'},
            {name: 'Korea, Democratic People\'s Republic of', code: 'KP'},
            {name: 'Korea, Republic of', code: 'KR'},
            {name: 'Kuwait', code: 'KW'},
            {name: 'Kyrgyzstan', code: 'KG'},
            {name: 'Lao People\'s Democratic Republic', code: 'LA'},
            {name: 'Latvia', code: 'LV'},
            {name: 'Lebanon', code: 'LB'},
            {name: 'Lesotho', code: 'LS'},
            {name: 'Liberia', code: 'LR'},
            {name: 'Libyan Arab Jamahiriya', code: 'LY'},
            {name: 'Liechtenstein', code: 'LI'},
            {name: 'Lithuania', code: 'LT'},
            {name: 'Luxembourg', code: 'LU'},
            {name: 'Macao', code: 'MO'},
            {name: 'Macedonia, The Former Yugoslav Republic of', code: 'MK'},
            {name: 'Madagascar', code: 'MG'},
            {name: 'Malawi', code: 'MW'},
            {name: 'Malaysia', code: 'MY'},
            {name: 'Maldives', code: 'MV'},
            {name: 'Mali', code: 'ML'},
            {name: 'Malta', code: 'MT'},
            {name: 'Marshall Islands', code: 'MH'},
            {name: 'Martinique', code: 'MQ'},
            {name: 'Mauritania', code: 'MR'},
            {name: 'Mauritius', code: 'MU'},
            {name: 'Mayotte', code: 'YT'},
            {name: 'Mexico', code: 'MX'},
            {name: 'Micronesia, Federated States of', code: 'FM'},
            {name: 'Moldova, Republic of', code: 'MD'},
            {name: 'Monaco', code: 'MC'},
            {name: 'Mongolia', code: 'MN'},
            {name: 'Montserrat', code: 'MS'},
            {name: 'Morocco', code: 'MA'},
            {name: 'Mozambique', code: 'MZ'},
            {name: 'Myanmar', code: 'MM'},
            {name: 'Namibia', code: 'NA'},
            {name: 'Nauru', code: 'NR'},
            {name: 'Nepal', code: 'NP'},
            {name: 'Netherlands', code: 'NL'},
            {name: 'Netherlands Antilles', code: 'AN'},
            {name: 'New Caledonia', code: 'NC'},
            {name: 'New Zealand', code: 'NZ'},
            {name: 'Nicaragua', code: 'NI'},
            {name: 'Niger', code: 'NE'},
            {name: 'Nigeria', code: 'NG'},
            {name: 'Niue', code: 'NU'},
            {name: 'Norfolk Island', code: 'NF'},
            {name: 'Northern Mariana Islands', code: 'MP'},
            {name: 'Norway', code: 'NO'},
            {name: 'Oman', code: 'OM'},
            {name: 'Pakistan', code: 'PK'},
            {name: 'Palau', code: 'PW'},
            {name: 'Palestinian Territory, Occupied', code: 'PS'},
            {name: 'Panama', code: 'PA'},
            {name: 'Papua New Guinea', code: 'PG'},
            {name: 'Paraguay', code: 'PY'},
            {name: 'Peru', code: 'PE'},
            {name: 'Philippines', code: 'PH'},
            {name: 'Pitcairn', code: 'PN'},
            {name: 'Poland', code: 'PL'},
            {name: 'Portugal', code: 'PT'},
            {name: 'Puerto Rico', code: 'PR'},
            {name: 'Qatar', code: 'QA'},
            {name: 'Reunion', code: 'RE'},
            {name: 'Romania', code: 'RO'},
            {name: 'Russian Federation', code: 'RU'},
            {name: 'Rwanda', code: 'RW'},
            {name: 'Saint Helena', code: 'SH'},
            {name: 'Saint Kitts and Nevis', code: 'KN'},
            {name: 'Saint Lucia', code: 'LC'},
            {name: 'Saint Pierre and Miquelon', code: 'PM'},
            {name: 'Saint Vincent and the Grenadines', code: 'VC'},
            {name: 'Samoa', code: 'WS'},
            {name: 'San Marino', code: 'SM'},
            {name: 'Sao Tome and Principe', code: 'ST'},
            {name: 'Saudi Arabia', code: 'SA'},
            {name: 'Senegal', code: 'SN'},
            {name: 'Serbia and Montenegro', code: 'CS'},
            {name: 'Seychelles', code: 'SC'},
            {name: 'Sierra Leone', code: 'SL'},
            {name: 'Singapore', code: 'SG'},
            {name: 'Slovakia', code: 'SK'},
            {name: 'Slovenia', code: 'SI'},
            {name: 'Solomon Islands', code: 'SB'},
            {name: 'Somalia', code: 'SO'},
            {name: 'South Africa', code: 'ZA'},
            {name: 'South Georgia and the South Sandwich Islands', code: 'GS'},
            {name: 'Spain', code: 'ES'},
            {name: 'Sri Lanka', code: 'LK'},
            {name: 'Sudan', code: 'SD'},
            {name: 'Suriname', code: 'SR'},
            {name: 'Svalbard and Jan Mayen', code: 'SJ'},
            {name: 'Swaziland', code: 'SZ'},
            {name: 'Sweden', code: 'SE'},
            {name: 'Switzerland', code: 'CH'},
            {name: 'Syrian Arab Republic', code: 'SY'},
            {name: 'Taiwan, Province of China', code: 'TW'},
            {name: 'Tajikistan', code: 'TJ'},
            {name: 'Tanzania, United Republic of', code: 'TZ'},
            {name: 'Thailand', code: 'TH'},
            {name: 'Timor-Leste', code: 'TL'},
            {name: 'Togo', code: 'TG'},
            {name: 'Tokelau', code: 'TK'},
            {name: 'Tonga', code: 'TO'},
            {name: 'Trinidad and Tobago', code: 'TT'},
            {name: 'Tunisia', code: 'TN'},
            {name: 'Turkey', code: 'TR'},
            {name: 'Turkmenistan', code: 'TM'},
            {name: 'Turks and Caicos Islands', code: 'TC'},
            {name: 'Tuvalu', code: 'TV'},
            {name: 'Uganda', code: 'UG'},
            {name: 'Ukraine', code: 'UA'},
            {name: 'United Arab Emirates', code: 'AE'},
            {name: 'United Kingdom', code: 'GB'},
            {name: 'United States', code: 'US'},
            {name: 'United States Minor Outlying Islands', code: 'UM'},
            {name: 'Uruguay', code: 'UY'},
            {name: 'Uzbekistan', code: 'UZ'},
            {name: 'Vanuatu', code: 'VU'},
            {name: 'Venezuela', code: 'VE'},
            {name: 'Vietnam', code: 'VN'},
            {name: 'Virgin Islands, British', code: 'VG'},
            {name: 'Virgin Islands, U.S.', code: 'VI'},
            {name: 'Wallis and Futuna', code: 'WF'},
            {name: 'Western Sahara', code: 'EH'},
            {name: 'Yemen', code: 'YE'},
            {name: 'Zambia', code: 'ZM'},
            {name: 'Zimbabwe', code: 'ZW'}
          ];
        
        $scope.selectStep = function(stepIndex) {
            if ($scope.formStepsSelected[stepIndex] == false) {

                $scope.step1IsCompleted = stepIndex > 0;
                $scope.step2IsCompleted = stepIndex > 1;
                $scope.step3IsCompleted = stepIndex > 2;
                $scope.step4IsCompleted = stepIndex > 3;
                $scope.step5IsCompleted = stepIndex > 4;

                for (var i = 0; i < $scope.formStepsSelected.length; i++) {
                    $scope.formStepsSelected[i] = false;
                }
                $scope.formStepsSelected[stepIndex] = true;
            }
        };

        $scope.step1Submit = function() {

            if (!$scope.formData.agreeTerms) {
                $scope.step1ErrorMessage = "You must accept the Terms and Conditions before continuing.";

            } else {
                $scope.step1ErrorMessage = "";
                $scope.step1IsCompleted = true;
                $scope.selectStep(1);
            }
        };

        $scope.step2Submit = function() {

            if (!$scope.isMsisdnVerified) {
                $scope.msisdnVerificationStep2ErrorResponse = "Please enter a valid Mobile Number.";

            } else {
                $scope.msisdnVerificationStep1ErrorResponse = "";
                $scope.msisdnVerificationStep2ErrorResponse = "";
                $scope.selectStep(2);
            }
        };

        $scope.step3Submit = function() {

            if ($scope.passwordVerificationForm.password.$invalid) {
                $scope.passwordSubmitErrorResponse = "Please enter a valid Password.";

            } else if ($scope.passwordVerificationForm.passwordConfirm.$invalid) {
                $scope.passwordSubmitErrorResponse = "Please enter a valid Confirm Password.";

            } else if ($scope.formData.password !== $scope.formData.passwordConfirm) {
                $scope.passwordSubmitErrorResponse = "Confirm Password does not match Password.";

            } else {
                $scope.passwordSubmitErrorResponse = "";
                $scope.passwordSubmittedSuccess = true;
                $scope.step3IsCompleted = true;
                $scope.selectStep(3);
            }
        };
        
        $scope.step4Submit = function() {

            const today = new Date();
            const minAge = 18;
            const dateMinAge = new Date(today.getFullYear() - minAge, today.getMonth(), today.getDate());
            const eighteenYearsInMilliseconds = today.getTime() - dateMinAge.getTime();

            if ($scope.accountDetailsForm.dobString.$invalid) {
                $scope.step4ErrorMessage = "Please enter Date of Birth.";

            } else if ((today.getTime() - $scope.formData.dobString.getTime()) < eighteenYearsInMilliseconds) {
                $scope.step4ErrorMessage = "Invalid age, must be at least 18 years.";

            } else if ($scope.accountDetailsForm.nationalIdNumber.$invalid) {
                $scope.step4ErrorMessage = "Please enter National ID Number / Social Security Number";
                
            } else if ($scope.accountDetailsForm.nationality.$invalid) {
                $scope.step4ErrorMessage = "Please select Country (Nationality)";
            
            } else if ($scope.accountDetailsForm.address1.$invalid) {
                    $scope.step4ErrorMessage = "Please enter Address Line 1";

            } else if ($scope.accountDetailsForm.address2.$invalid) {
                $scope.step4ErrorMessage = "Please enter Address Line 2";
                    
            } else if ($scope.accountDetailsForm.residenceCountry.$invalid) {
                $scope.step4ErrorMessage = "Please select Country of Residence";

            } else if ($scope.accountDetailsForm.postcode.$invalid) {
                $scope.step4ErrorMessage = "Please enter Postcode";

            } else {
                $scope.step4IsCompleted = true;
                $scope.step4ErrorMessage = "";
                $scope.selectStep(4);
            }
        };

        $scope.setResponse = function(response) {
            $scope.response = response;
        };
        $scope.setWidgetId = function(widgetId) {
            $scope.widgetId = widgetId;
        };
        $scope.cbExpiration = function() {
            vcRecaptchaService.reload($scope.widgetId);
        };

        $scope.reSendVerificationCode = function() {

            vcRecaptchaService.reload($scope.widgetId);
            $scope.verifyCodeHasSent = false;
            $scope.formData.smsVerificationCode = "";
            $scope.smsCodeVerificationForm.$setPristine();
            $scope.msisdnVerificationStep1ErrorResponse = "";
            $scope.msisdnVerificationStep2ErrorResponse = "";
        };

        $scope.sendMsisdnVerificationCode = function() {

            $scope.verifyCodeHasSent = false;

            if ($scope.verificationCodeForm.mobileNumber.$invalid) {
                $scope.msisdnVerificationStep1ErrorResponse = "Please enter a valid Mobile Number.";

            } else if ($scope.verificationCodeForm.mobileNumberConfirm.$invalid) {
                $scope.msisdnVerificationStep1ErrorResponse = "Please enter a valid Confirm Mobile Number.";

            } else if ($scope.formData.mobileNumber !== $scope.formData.mobileNumberConfirm) {
                $scope.msisdnVerificationStep1ErrorResponse = "Confirm Mobile Number does not match Mobile Number.";

            } else if (vcRecaptchaService.getResponse() === "" || vcRecaptchaService.getResponse() == undefined || vcRecaptchaService.getResponse() == null) {
                $scope.msisdnVerificationStep1ErrorResponse = "Please resolve the captcha.";

            } else {

                $scope.showSpinner = true;

                var urlParams = {
                    mobileNumber : $scope.formData.mobileNumber,
                    firstName : $scope.firstName,
                    lastName : $scope.lastName,
                    recaptchaResponse : vcRecaptchaService.getResponse()
                };

                registrationService.sendMsisdnVerificationCode(urlParams, function(response) {

                    $scope.verifyCodeHasSent = true;
                    $scope.showSpinner = false;
                    $scope.msisdnVerificationStep1ErrorResponse = "";

                }, function(error) {

                    $scope.showSpinner = false;
                    vcRecaptchaService.reload($scope.widgetId);
                    $scope.msisdnVerificationStep1ErrorResponse = error.data.responseText;
                });
            }
        };

        $scope.verifySMSCode = function() {

            if ($scope.smsCodeVerificationForm.$invalid) {

                $scope.msisdnVerificationStep2ErrorResponse = "Please enter a valid Verification Code.";

            } else {

                $scope.showSpinner = true;

                var urlParams = {
                    mobileNumber : $scope.formData.mobileNumber,
                    code : $scope.formData.smsVerificationCode
                };

                registrationService.verifySMSCode(urlParams, function(response) {

                    $scope.isMsisdnVerified = true;
                    $scope.step2IsCompleted = true;
                    $scope.t = response.data;
                    $scope.showSpinner = false;
                    $scope.msisdnVerificationStep2ErrorResponse = "";

                }, function(error) {

                    $scope.showSpinner = false;
                    $scope.msisdnVerificationStep2ErrorResponse = error.data.responseText;
                });
            }
        };

        $scope.previewAddress = function() {

            var addrPreview = $scope.formData.address1;
            addrPreview += "<br>" + $scope.formData.address2;

            if ($scope.formData.address3 != undefined && $scope.formData.address3 != "") {
                addrPreview += "<br>" + $scope.formData.address3;
            }
            if ($scope.formData.address4 != undefined && $scope.formData.address4 != "") {
                addrPreview += "<br>" + $scope.formData.address4;
            }

            addrPreview += "<br>" + $scope.formData.postcode;

            return addrPreview;
        };

        $scope.processRegistration = function() {

            $scope.regSubmitButtonDisabled = true;
            $scope.showSpinner = true;

            var urlParams = {
                email : $scope.email,
                password : $scope.formData.password,
                mobileNumber : $scope.formData.mobileNumber,
                dobString : $filter('date')($scope.formData.dobString, "dd/MM/yyyy"),
                nationalIdNumber : $scope.formData.nationalIdNumber,
                nationalityCountryISO : $scope.formData.nationality.code,
                residenceCountryISO : $scope.formData.residenceCountry.code,
                address1 : $scope.formData.address1,
                address2 : $scope.formData.address2,
                address3 : $scope.formData.address3,
                address4 : $scope.formData.address4,
                postCode : $scope.formData.postcode,
                token : $scope.t
            };

            registrationService.processRegistration(urlParams, function(response) {

                $window.onbeforeunload = null;
                $scope.step4IsCompleted = true;
                $scope.showSpinner = false;
                $scope.registrationSuccess = true;
                $scope.registrationErrorResponse = "";

            }, function(error) {

                $scope.regSubmitButtonDisabled = false;
                $scope.showSpinner = false;
                $scope.registrationErrorResponse = error.data.responseText;
            });

        };

    }]);

registrationApp.service('registrationService', ['tcComService', function(tcComService) {

    this.sendMsisdnVerificationCode = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("registration/sendsmscode", urlParams, callBackSuccess, callBackError);
    };

    this.verifySMSCode = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("registration/verifysmscode", urlParams, callBackSuccess, callBackError);
    };

    this.processRegistration = function(urlParams, callBackSuccess, callBackError) {

        tcComService.callApiWithJSONParam("registration/processregistration", urlParams, callBackSuccess, callBackError);
    };

}]);

registrationApp.filter('split', function() {
    return function(input, splitChar, splitIndex) {
        if (angular.isDefined(input))
            return input.split(splitChar)[splitIndex];
    }
});

registrationApp.filter('to_trusted_html', ['$sce', function($sce) {
    return function(text) {
        return $sce.trustAsHtml(text);
    };
}]);

registrationApp.directive('avoidSpecialChars', [function() {
    function link(scope, elem, attrs, ngModel) {
        ngModel.$parsers.push(function(viewValue) {
            var reg = /^[^!@#$%\&*()_+={}|[\]\\:;"<>?,./]*$/;
            if (viewValue.match(reg)) {
                return viewValue;
            }
            var transformedValue = ngModel.$modelValue;
            ngModel.$setViewValue(transformedValue);
            ngModel.$render();
            return transformedValue;
        });
    }

    return {
        restrict : 'A',
        require : 'ngModel',
        link : link
    };
}]);

registrationApp.directive('avoidSpecialCharsV2', [function() {
    function link(scope, elem, attrs, ngModel) {
        ngModel.$parsers.push(function(viewValue) {
            var reg = /^[^!@#$%\*()_+={}|[\]\\:;"<>?,./]*$/;
            if (viewValue.match(reg)) {
                return viewValue;
            }
            var transformedValue = ngModel.$modelValue;
            ngModel.$setViewValue(transformedValue);
            ngModel.$render();
            return transformedValue;
        });
    }

    return {
        restrict : 'A',
        require : 'ngModel',
        link : link
    };
}]);

registrationApp.directive('avoidNumbers', [function() {
    function link(scope, elem, attrs, ngModel) {
        ngModel.$parsers.push(function(viewValue) {
            var reg = /^[^0-9]*$/;
            if (viewValue.match(reg)) {
                return viewValue;
            }
            var transformedValue = ngModel.$modelValue;
            ngModel.$setViewValue(transformedValue);
            ngModel.$render();
            return transformedValue;
        });
    }

    return {
        restrict : 'A',
        require : 'ngModel',
        link : link
    };
}]);

registrationApp.directive('alphanumeric', function() {
    return {
        restrict : 'A',
        require : 'ngModel',
        link : function(scope, element, attr, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                var reg = new RegExp('^[a-zA-Z0-9]*$');
                if (reg.test(viewValue)) {
                    return viewValue;
                } else {
                    var overrideValue = (reg.test(ctrl.$modelValue) ? ctrl.$modelValue : '');
                    element.val(overrideValue);
                    return overrideValue;
                }
            });
        }
    };
});
