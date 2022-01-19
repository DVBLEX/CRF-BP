crfApp = angular.module('crfApp', [ 'angularSpinner', 'ngResource', 'ngRoute', 'tcCom', 'ngIntlTelInput', 'jsonFormatter' ]);

crfApp.config([ '$httpProvider', '$routeProvider', function($httpProvider, $routeProvider) {

    $routeProvider.when('/', {
        templateUrl : 'app/views/home.jsp',
        controller : 'HomeController'
    }).when('/changePassword', {
        templateUrl : 'app/views/changePassword.html',
        controller : 'ChangePasswordController'
    }).when('/users', {
        templateUrl : 'app/views/adm/users.jsp',
        controller : 'UsersController'
    }).when('/customers', {
        templateUrl : 'app/views/adm/customers.jsp',
        controller : 'CustomersController'
    }).when('/deposits', {
        templateUrl : 'app/views/adm/deposits.jsp',
        controller : 'DepositsAdminController'
    }).when('/depositProducts', {
        templateUrl : 'app/views/adm/depositProducts.jsp',
        controller : 'DepositProductsAdminController'
    }).when('/payments', {
        templateUrl : 'app/views/adm/payments.jsp',
        controller : 'PaymentsAdminController'
    }).when('/customer/deposits', {
        templateUrl : 'app/views/deposits.jsp',
        controller : 'DepositsCustomerController'
    }).when('/customer/deposit/docs', {
        templateUrl : 'app/views/depositsDocs.jsp',
        controller : 'DepositsDocumentController'
    }).when('/customer/deposit/stmts', {
        templateUrl : 'app/views/depositStatements.jsp',
        controller : 'DepositStatementController'
    }).when('/customer/bankaccount', {
        templateUrl : 'app/views/bankaccount.jsp',
        controller : 'BankAccountCustomerController'    
    });

    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

    $httpProvider.interceptors.push([ '$q', '$window', function($q, $window) {
        return {
            'responseError' : function(rejection) {
                var status = rejection.status;

                if (status == 401 || status == 403 || status == 405) {
                    $window.location.href = "/crf-web/login.html?denied";
                }

                return $q.reject(rejection);
            }
        };
    } ]);
} ]);

crfApp.config([ 'ngIntlTelInputProvider', function(ngIntlTelInputProvider) {
    ngIntlTelInputProvider.set({
        utilsScript : "lib/intl-tel-input-12.0.3/js/utils.js",
        initialCountry : 'ie',
        preferredCountries : [],
        autoPlaceholder : "off",
        nationalMode : false
    });
} ]);

crfApp.run([ '$rootScope', '$location', function($rootScope, $location) {

    $rootScope.go = function(path) {
        $location.path(path);

        $('#navbar').collapse('hide');
    };

    $rootScope.$on('$routeChangeStart', function(event, next, current) {
        if (!current) {
            $rootScope.go('/');
        }
    });

} ]);

crfApp.controller('CRFController', [
    '$scope',
    '$rootScope',
    '$window',
    '$timeout',
    '$location',
    '$filter',
    'usSpinnerService',
    'homeService',
    function($scope, $rootScope, $window, $timeout, $location, $filter, usSpinnerService, homeService) {

        $rootScope.isLoading = false;
        $rootScope.currentYear = new Date().getFullYear();
        $scope.contactUsErrorResponse = "";
        $scope.submitButtonDisabled = false;
        $rootScope.customerDetails = null;
        $scope.formData = {};

        $scope.init = function(username, isAdmin, isInvestorOperator, isBorrowerOperator, customerCategory, isCustomerAccountVerified) {

            $rootScope.currentUsername = username;
            $rootScope.isAdmin = isAdmin;
            $rootScope.isInvestorOperator = isInvestorOperator;
            $rootScope.isBorrowerOperator = isBorrowerOperator;
            $rootScope.customerCategory = customerCategory;
            $rootScope.isCustomerAccountVerified = isCustomerAccountVerified;
        }

        $rootScope.startSpinner = function() {
            $rootScope.isLoading = true;
            usSpinnerService.spin("pad-spinner");
        }

        $rootScope.stopSpinner = function() {
            $rootScope.isLoading = false;
            usSpinnerService.stop("pad-spinner");
        }

        var timer;
        $rootScope.showResultMessage = function(isSuccess, message) {
            $timeout.cancel(timer);

            var showTime = 4500;
            if (isSuccess) {
                $rootScope.showSuccess = message;
                timer = $timeout(function() {
                    $rootScope.showSuccess = "";
                }, showTime);
            } else {
                $rootScope.showError = message;
                timer = $timeout(function() {
                    $rootScope.showError = "";
                }, showTime);
            }
        };

        $rootScope.showResultCancel = function() {
            $timeout.cancel(timer);
            $rootScope.showSuccess = "";
            $rootScope.showError = "";
        };

        $rootScope.validateAPIResponse = function(response) {
            if (response === null || response == undefined || response.dataList === null || response.dataList == undefined) {
                    $window.location.href = "/crf-web/login.html?denied";
            }
        };

        $rootScope.activeNavbar = function(navbarId) {
            $('#navbar .navbar-nav').find('li.active').removeClass('active');
            $(navbarId).addClass('active');
        };

        $rootScope.hideNavbarMenuOptions = function(navbarId) {
            $(navbarId).removeClass('active');
            $(navbarId).addClass('hidden');
        };

        $rootScope.showGenericConfirmationModal = function(text, callbackFunction) {

            var timer;
            $scope.confirmationModalText = text;
            $scope.callbackFunction = function() {
                callbackFunction();
                $('#genericConfirmationModal').modal('hide');

                $timeout.cancel(timer);
                timer = $timeout(function() {
                    $scope.modalButtonsForm.$invalid = false;
                    $scope.modalButtonsForm.$valid = true;
                }, 5000);
            }
            $('#genericConfirmationModal').modal('show');
        };

        $rootScope.closeGenericConfirmationModal = function() {
            $('#genericConfirmationModal').modal('hide');

            $timeout.cancel(timer);
            timer = $timeout(function() {
                $scope.modalButtonsForm.$invalid = false;
                $scope.modalButtonsForm.$valid = true;
            }, 5000);
        };
        
        $rootScope.countries = [ 
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
        
        $rootScope.getCountryNameFromISOCode = function(isoCode) {
            
            for (let i = 0; i<$rootScope.countries.length; i++) {
                if ($rootScope.countries[i].code === isoCode) {
                    return $rootScope.countries[i].name;
                }
            }
            
            return "";
        };
        
        $rootScope.getCustomerIdTypeText = function(type) {
            
            if (type === "1" || type === 1) {
                return "Passport";
            } else if (type === "2" || type === 2) {
                return "Driving Licence";
            } else if (type === "3" || type === 3) {
                return "National Identity Card";
            } else {
                return "";
            }
        };

        $rootScope.getCustomerPOADocument = function(type) {

            if (type === "4" || type === 4) {
                return "Utility bill";
            } else if (type === "5" || type === 5) {
                return "Bank statement";
            } else if (type === "6" || type === 6) {
                return "Tax notice from the Revenue Commissioners";
            } else if (type === "7" || type === 7) {
                return "Social Welfare document";
            } else if (type === "8" || type === 8) {
                return "Motor tax document";
            } else if (type === "9" || type === 9) {
                return "Home or motor insurance certificate or renewal notice";
            } else {
                return "";
            }
        };
        
        $rootScope.getCustomerTypeText = function(customerType) {
            
            if (customerType === "1" || customerType === 1) {
                return "Investor";
            } else if (customerType === "2" || customerType === 2) {
                return "Borrower";
            } else if (customerType === "3" || customerType === 3) {
                return "Investor & Borrower";
            } else {
                return "";
            }
        };
        
        $rootScope.getCustomerCategoryText = function(customerCategory) {
            
            if (customerCategory === "1" || customerCategory === 1) {
                return "Individual";
            } else if (customerCategory === "2" || customerCategory === 2) {
                return "Company";
            } else {
                return "";
            }
        };
        
        $rootScope.showContactUsFormModal = function() {

            $('#contactUsModal').modal({
                backdrop : 'static',
                keyboard : false,
                show : true
            });
        };
        
        $rootScope.closeContactUsModal = function() {
    
            $scope.contactUsErrorResponse = "";
            $scope.submitButtonDisabled = false;
            $scope.formData = {};
            
            $('#contactUsModal').modal('hide');
        };
        
        $rootScope.submitContactForm = function() {
        
            $scope.contactUsErrorResponse = "";
            
            if ($scope.contactForm.queryType.$invalid) {
                $scope.contactUsErrorResponse = "Please select a topic.";
                
            } else if ($scope.formData.queryType === "") {
                $scope.contactUsErrorResponse = "Please select a topic.";
                
            } else if ($scope.contactForm.queryDetails.$invalid) {
                $scope.contactUsErrorResponse = "Please include the details about your query. The details of your enquiry must be between 15 and 5000 characters.";
            
            } else {
                
                $scope.submitButtonDisabled = true;
                $scope.showSpinner = true;
                
                var urlParams = {
                    queryType : $scope.formData.queryType,
                    queryDetails : $scope.formData.queryDetails
                };
        
                homeService.submitContactForm(urlParams, function(response) {
        
                    $scope.showSpinner = false;
                    $scope.contactUsErrorResponse = "";
                    $scope.submitButtonDisabled = false;
                    $scope.closeContactUsModal();
                    
                    $rootScope.showResultMessage(true, "Thank you for your query. You will receive a response within the next 24 hours.");
                    
                }, function(error) {
        
                    $scope.showSpinner = false;
                    $scope.submitButtonDisabled = false;
                    $scope.contactUsErrorResponse = error.data.responseText;
                });
            }
        };
        
    } ]);
