<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html ng-app="registrationApp">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="format-detection" content="telephone=no">

<title>JMGCFinance - Registration</title>

<link rel="stylesheet" href="lib/bootstrap-3.3.7/css/bootstrap.min.css" />
<link rel="stylesheet" href="lib/jquery-ui-1.12.1/css/jquery-ui.min.css" />
<link rel="stylesheet" href="lib/intl-tel-input-12.0.3/css/intlTelInput.css">
<link rel="stylesheet" href="app/css/crf.css" />

<script type="text/javascript" src="lib/jquery-2.2.4/js/jquery.min-2.2.4.js"></script>
<script type="text/javascript" src="lib/jquery-ui-1.12.1/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="lib/angular-1.7.8/js/angular.min.js"></script>
<script type="text/javascript" src="lib/angular-1.7.8/js/angular-resource.min.js"></script>
<script type="text/javascript" src="lib/angular-1.7.8/js/angular-animate.min.js"></script>
<script type="text/javascript" src="lib/angular-1.7.8/js/angular-route.min.js"></script>
<script type="text/javascript" src="lib/angular-spinner-0.5.0/js/angular-spinner.min.js"></script>
<script type="text/javascript" src="lib/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript" src="lib/angular-recaptcha-4.1.0/js/angular-recaptcha.min.js"></script>
<script type="text/javascript" src="lib/spin-2.3.2/js/spin.min-2.3.2.js"></script>
<script type="text/javascript" src="lib/intl-tel-input-12.0.3/js/intlTelInput.min.js"></script>
<script type="text/javascript" src="lib/intl-tel-input-12.0.3/js/utils.js"></script>
<script type="text/javascript" src="lib/ng-intl-tel-input-2.0.0/js/ng-intl-tel-input.min.js"></script>
<script type="text/javascript" src="app/js/services/tc-comservice-1.0.js"></script>
<script type="text/javascript" src="app/js/registrationApp.js"></script>
</head>

<body ng-controller="registrationController" ng-cloak ng-init="init('${isTestEnvironment}','${email}','${title}','${firstName}','${lastName}')"
    onbeforeunload="return confirmPageReload()">

    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="login.html"> <img src="app/img/crf_logo.jpeg" alt="" style="height: 120%; float: left;" /> &nbsp; <c:out value="${appName}" /> <c:if test="${isTestEnvironment == true}">
                        <span>(Sandbox)</span>
                    </c:if>
                </a>
            </div>
        </div>
    </nav>

    <h3 class="text-center">Registration</h3>

    <div class="container-fluid" ng-cloak>

        <div class="row">
            <div ng-hide="registrationSuccess">

                <div class="panel" ng-class="{'panel-primary': !step1IsCompleted, 'panel-success': step1IsCompleted}">
                    <div class="panel-heading" ng-click="selectStep(0)">
                        <div class="panel-title">
                            1. Terms and Conditions <span class="glyphicon glyphicon-ok" ng-show="step1IsCompleted"></span>
                        </div>
                    </div>

                    <div class="panel-body" ng-show="formStepsSelected[0]">
                        <div class="col-sm-12" ng-show="!step1IsCompleted">
                            <form name="accountTypeForm" autocomplete="off" novalidate>
                                <div class="row">

                                    <div class="form-group col-sm-12">
                                        <label class=""><input type="checkbox" name="agreeTerms" ng-model="formData.agreeTerms" required> By clicking Next below you
                                            hereby agree to the <c:out value="${appName}" /> <a href="https://jmgcfinance.com/terms-and-conditions/" target="_blank">Terms and Conditions</a> </label>
                                    </div>

                                    <div class="form-group col-sm-12">
                                        <div class="animate-if" ng-if="step1ErrorMessage !== ''">
                                            <div class="alert alert-danger" role="alert">
                                                <span class="glyphicon glyphicon-remove"></span> <span class="sr-only">Error: </span> {{step1ErrorMessage}}
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-sm-12">
                                        <button type="button" class="btn btn-primary btn-block" ng-click="step1Submit()">
                                            <span class="glyphicon glyphicon-chevron-right"></span> Next
                                        </button>
                                    </div>

                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="panel"
                    ng-class="{'panel-primary': !step2IsCompleted || !step1IsCompleted, 'panel-success': step2IsCompleted && step1IsCompleted, 'disabledAccordion': !step1IsCompleted}">
                    <div class="panel-heading" ng-click="selectStep(1)" ng-class="{'linkDisabled': !step1IsCompleted}">
                        <div class="panel-title">
                            2. Mobile Number Verification <span class="glyphicon glyphicon-ok" ng-show="step2IsCompleted"></span>
                        </div>
                    </div>

                    <div class="panel-body" ng-show="formStepsSelected[1]">

                        <div class="col-sm-12" ng-show="step1IsCompleted || !passwordSubmittedSuccess">

                            <div class="col-sm-12" ng-show="!verifyCodeHasSent || isMsisdnVerified">

                                <form name="verificationCodeForm" autocomplete="off" novalidate>

                                    <div class="row">
                                        <div class="form-group col-sm-6">
                                            <label for="firstName">First Name</label> <input type="text" class="form-control" name="firstName" ng-value="firstName"
                                                ng-maxlength="32" maxlength="32" ng-trim="true" ng-disabled="true">
                                        </div>

                                        <div class="form-group col-sm-6">
                                            <label for="lastName">Last Name</label> <input type="text" class="form-control" name="lastName" ng-value="lastName" ng-maxlength="32"
                                                maxlength="32" ng-trim="true" ng-disabled="true">
                                        </div>

                                        <div class="form-group col-sm-6">
                                            <label for="mobileNumber">Mobile Number</label>
                                            <div style="width: 100%;">
                                                <input type="text" class="form-control" style="width: 100%;" name="mobileNumber" id="mobileNumber" ng-model="formData.mobileNumber"
                                                    ng-disabled="isMsisdnVerified" ng-intl-tel-input required autofocus>
                                            </div>
                                        </div>

                                        <div class="form-group col-sm-6">
                                            <label for="mobileNumberConfirm">Confirm Mobile Number</label>
                                            <div style="width: 100%;">
                                                <input type="text" class="form-control" style="width: 100%;" name="mobileNumberConfirm" id="mobileNumberConfirm"
                                                    ng-model="formData.mobileNumberConfirm" ng-disabled="isMsisdnVerified" ng-intl-tel-input required>
                                            </div>
                                        </div>

                                        <div class="form-group col-sm-12" ng-if="!isMsisdnVerified">
                                            {{recaptchaKey=
                                            <c:out value="${recaptchaKey}" />
                                            ;""}}

                                            <div vc-recaptcha key="recaptchaKey" lang="en" theme="clean" on-create="setWidgetId(widgetId)" on-success="setResponse(response)"
                                                on-expire="cbExpiration()"></div>
                                        </div>

                                        <div class="form-group col-sm-12">
                                            <div class="animate-if" ng-if="msisdnVerificationStep1ErrorResponse !== ''">
                                                <div class="alert alert-danger" role="alert">
                                                    <span class="glyphicon glyphicon-remove"></span> <span class="sr-only">Error: </span> {{msisdnVerificationStep1ErrorResponse}}
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-sm-12" ng-if="!isMsisdnVerified">
                                            <button type="button" class="btn btn-info btn-block" ng-disabled="verifyCodeHasSent" ng-click="sendMsisdnVerificationCode()">
                                                <span class="glyphicon glyphicon-phone"></span> Send Verification Code
                                            </button>
                                        </div>

                                        <div class="col-sm-12" ng-if="isMsisdnVerified">
                                            <div class="alert alert-success" role="alert">
                                                <span class="glyphicon glyphicon-ok"></span> <span class="sr-only">Success: </span> <span>The Mobile Number <strong>{{formData.mobileNumber}}</strong>
                                                    is successfully verified!
                                                </span>
                                            </div>

                                            <button type="button" class="btn btn-primary btn-block" ng-click="step2Submit()">
                                                Next <span class="glyphicon glyphicon-chevron-right"></span>
                                            </button>
                                        </div>

                                    </div>

                                </form>
                            </div>

                            <div class="col-sm-12" ng-show="verifyCodeHasSent && !isMsisdnVerified">

                                <div class="row">

                                    <div class="col-sm-12">

                                        <form name="smsCodeVerificationForm" autocomplete="off" novalidate>

                                            <div>&nbsp;</div>

                                            <div ng-if="!isMsisdnVerified">
                                                <p>
                                                    The verification code was sent via SMS to <strong>{{formData.mobileNumber}}</strong>.
                                                </p>
                                                <p>
                                                    Didn't receive the verification code? <a href="#" ng-click="reSendVerificationCode()" tabindex="-1">Click here</a> to resend or
                                                    change the mobile number.
                                                </p>
                                                <div class="form-group">
                                                    <label for="smsVerificationCode">Verification Code</label> <input type="text" class="form-control" name="smsVerificationCode"
                                                        ng-readonly="isMsisdnVerified" ng-model="formData.smsVerificationCode" ng-minlength="5" ng-maxlength="5" maxlength="5"
                                                        ng-pattern="/^\d{5}$/" required autofocus>
                                                </div>

                                                <div ng-if="msisdnVerificationStep2ErrorResponse !== ''">
                                                    <div class="alert alert-danger" role="alert">
                                                        <span class="glyphicon glyphicon-remove"></span> <span class="sr-only">Error: </span>
                                                        {{msisdnVerificationStep2ErrorResponse}}
                                                    </div>
                                                </div>

                                                <button type="button" class="btn btn-success btn-block" ng-disabled="isMsisdnVerified" ng-click="verifySMSCode()">
                                                    <span class="glyphicon glyphicon-check"></span> Verify Mobile Number
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>

                        </div>

                    </div>
                </div>

                <div class="panel"
                    ng-class="{'panel-primary': !step3IsCompleted || !step1IsCompleted || !step2IsCompleted, 'panel-success': step3IsCompleted && step1IsCompleted && step2IsCompleted, 'disabledAccordion': !step1IsCompleted || !step2IsCompleted}">
                    <div class="panel-heading" ng-click="selectStep(2)" ng-class="{'linkDisabled': !step1IsCompleted || !step2IsCompleted}">
                        <div class="panel-title">
                            3. Account Password Creation <span class="glyphicon glyphicon-ok" ng-show="step3IsCompleted"></span>
                        </div>
                    </div>

                    <div class="panel-body" ng-show="formStepsSelected[2]">
                        <div class="col-sm-12" ng-show="step2IsCompleted || !passwordSubmittedSuccess">
                            <form name="passwordVerificationForm" autocomplete="off" novalidate>
                                <div class="row">
                                    <div class="form-group col-sm-12">
                                        <div class="alert alert-info">
                                            <b>Password Policy:</b><br> Your password needs to be at least 8 characters long and a maximum of 32 characters. It must contain at
                                            least 1 capital letter, 1 number and 1 special character. Allowed special characters: & ? ! $ #
                                        </div>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="password">Password</label> <input type="password" class="form-control" name="password" placeholder="Password" title="Password"
                                            ng-model="formData.password" ng-pattern="passwordRegexp" ng-maxlength="32" maxlength="32" oncopy="return false" onpaste="return false"
                                            required>
                                    </div>
                                    <div class="form-group col-sm-6">
                                        <label for="passwordConfirm">Confirm Password</label> <input type="password" class="form-control" name="passwordConfirm"
                                            placeholder="Confirm Password" title="Password" ng-model="formData.passwordConfirm" ng-maxlength="32" maxlength="32"
                                            oncopy="return false" onpaste="return false">
                                    </div>

                                    <div class="col-sm-12" ng-if="passwordSubmitErrorResponse !== ''">
                                        <div class="alert alert-danger" role="alert">
                                            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> {{passwordSubmitErrorResponse}}
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <button type="button" class="btn btn-primary btn-block" ng-click="step3Submit()">
                                            Next <span class="glyphicon glyphicon-chevron-right"></span>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="panel"
                    ng-class="{'panel-primary': !step4IsCompleted || !step1IsCompleted || !step2IsCompleted || !step3IsCompleted,
                'panel-success': step4IsCompleted && step1IsCompleted && step2IsCompleted && step3IsCompleted, 'disabledAccordion': !step1IsCompleted || !step2IsCompleted || !step3IsCompleted}">
                    <div class="panel-heading" ng-click="selectStep(3)" ng-class="{'linkDisabled': !step1IsCompleted || !step2IsCompleted || !step3IsCompleted}">
                        <div class="panel-title">
                            4. Account Details <span class="glyphicon glyphicon-ok" ng-show="step4IsCompleted"></span>
                        </div>
                    </div>

                    <div class="panel-body" ng-show="formStepsSelected[3]">
                        <div class="col-sm-12">
                            <form name="accountDetailsForm" autocomplete="off" novalidate>
                                <div class="row">
                                    <div class="form-group col-sm-2">
                                        <label for="title">Title</label> <input type="text" class="form-control" name="title" ng-value="title" ng-disabled="true">
                                    </div>

                                    <div class="form-group col-sm-5">
                                        <label for="name">Name</label> <input type="text" class="form-control" name="name" ng-value="firstName + ' ' + lastName" ng-maxlength="32"
                                            maxlength="32" ng-trim="true" ng-disabled="true">
                                    </div>

                                    <div class="form-group col-sm-5">
                                        <label for="dobString">Date of Birth</label> <input type="date" class="form-control" id="dobString" name="dobString"
                                            ng-model="formData.dobString" placeholder="dd/MM/yyyy" min="1900-01-01" max={{dateTodayString}} ng-min="1900-01-01"
                                            ng-max={{dateTodayString}} required>
                                    </div>
                                    
                                    <div class="form-group col-sm-12">
                                        <label for="nationalIdNumber">National ID Number / Social Security Number</label> <input type="text" class="form-control" name="nationalIdNumber" ng-model="formData.nationalIdNumber"
                                            ng-minlength="4" ng-maxlength="32" maxlength="32" ng-trim="true" alphanumeric required>
                                    </div>

                                    <div class="form-group col-sm-2">
                                        <label for="nationality" class="text-nowrap">Country (Nationality)</label> <select class="form-control" name="nationality"
                                            ng-model="formData.nationality" ng-options="country.name for country in countries track by country.code" required>
                                        </select>
                                    </div>

                                    <div class="form-group col-sm-5">
                                        <label for="address1">Address Line 1</label> <input type="text" class="form-control" name="address1" ng-model="formData.address1"
                                            ng-maxlength="64" maxlength="64" ng-trim="true" avoid-special-chars required>
                                    </div>

                                    <div class="form-group col-sm-5">
                                        <label for="address2">Address Line 2</label> <input type="text" class="form-control" name="address2" ng-model="formData.address2"
                                            ng-maxlength="64" maxlength="64" ng-trim="true" avoid-special-chars required>
                                    </div>

                                    <div class="form-group col-sm-2">
                                        <label for="residenceCountry" class="text-nowrap">Country of Residence</label> <select class="form-control" name="residenceCountry"
                                            ng-model="formData.residenceCountry" ng-options="country.name for country in countries track by country.code" required>
                                        </select>
                                    </div>

                                    <div class="form-group col-sm-5">
                                        <label for="address3">Address Line 3 <span class="optional">- Optional</span></label> <input type="text" class="form-control"
                                            name="address3" ng-model="formData.address3" ng-maxlength="64" maxlength="64" ng-trim="true" avoid-special-chars>
                                    </div>

                                    <div class="form-group col-sm-5">
                                        <label for="address4">Address Line 4 <span class="optional">- Optional</span></label> <input type="text" class="form-control"
                                            name="address4" ng-model="formData.address4" ng-maxlength="64" maxlength="64" ng-trim="true" avoid-special-chars>
                                    </div>

                                    <div class="form-group col-sm-2">
                                        <label for="postcode">Postcode</label> <input type="text" class="form-control" name="postcode" ng-model="formData.postcode"
                                            ng-maxlength="16" maxlength="16" ng-trim="true" avoid-special-chars required>
                                    </div>

                                    <div class="animate-if col-sm-12" ng-if="step4ErrorMessage !== ''">
                                        <div class="alert alert-danger" role="alert">
                                            <span class="glyphicon glyphicon-remove"></span><span class="sr-only">Error: </span> <span>{{step4ErrorMessage}}</span>
                                        </div>
                                    </div>

                                    <div class="col-sm-12">
                                        <button type="button" class="btn btn-primary btn-block" ng-click="step4Submit()">
                                            Next <span class="glyphicon glyphicon-chevron-right"></span>
                                        </button>
                                    </div>
                                </div>

                            </form>
                        </div>
                    </div>
                </div>

                <div class="panel"
                    ng-class="{'panel-primary': !step5IsCompleted || !step4IsCompleted || !step1IsCompleted || !step2IsCompleted || !step3IsCompleted,
                'panel-success': step5IsCompleted && step4IsCompleted && step1IsCompleted && step2IsCompleted && step3IsCompleted, 'disabledAccordion': !step1IsCompleted || !step2IsCompleted || !step3IsCompleted || !step4IsCompleted}">
                    <div class="panel-heading" ng-click="selectStep(4)"
                        ng-class="{'linkDisabled': !step1IsCompleted || !step2IsCompleted || !step3IsCompleted || !step4IsCompleted}">
                        <div class="panel-title">
                            5. Review and Submit <span class="glyphicon glyphicon-ok" ng-show="step5IsCompleted"></span>
                        </div>
                    </div>

                    <div class="panel-body" ng-show="formStepsSelected[4]">
                        <form name="submitDataForm" autocomplete="off" novalidate>

                            <div class="col-sm-12">
                                <h5 class="no-margin-top">Please review the details carefully. If you wish to modify them, please select the appropriate step.</h5>
                            </div>

                            <div class="col-sm-12 form-group bg-info margin-top">
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>Title</label>
                                    </div>
                                    <div class="col-sm-9">{{title}}</div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>Name</label>
                                    </div>
                                    <div class="col-sm-9">{{firstName}} {{lastName}}</div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>Date of Birth</label>
                                    </div>
                                    <div class="col-sm-9">{{formData.dobString | date: "dd/MM/yyyy"}}</div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>National ID Number / Social Security Number</label>
                                    </div>
                                    <div class="col-sm-9">{{formData.nationalIdNumber}}</div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>Email</label>
                                    </div>
                                    <div class="col-sm-9">{{email}}</div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>Mobile Number</label>
                                    </div>
                                    <div class="col-sm-9">{{formData.mobileNumber}}</div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>Country (Nationality)</label>
                                    </div>
                                    <div class="col-sm-9">{{formData.nationality.name}}</div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>Address</label>
                                    </div>
                                    <div class="col-sm-9">
                                        <span ng-bind-html="previewAddress() | to_trusted_html"></span>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-3">
                                        <label>Country of Residence</label>
                                    </div>
                                    <div class="col-sm-9">{{formData.residenceCountry.name}}</div>
                                </div>
                            </div>

                            <div class="col-sm-12 form-group  no-padding-right no-padding-left">

                                <div class="animate-if" ng-if="registrationErrorResponse !== ''">
                                    <div class="alert alert-danger" role="alert">
                                        <span class="glyphicon glyphicon-remove"></span> <span class="sr-only">Error: </span> {{registrationErrorResponse}}
                                    </div>
                                </div>
                            </div>

                            <div class="col-sm-12 no-padding-right no-padding-left">
                                <button type="submit" class="btn btn-success btn-block" ng-click="processRegistration()" ng-disabled="regSubmitButtonDisabled">
                                    Submit <span class="glyphicon glyphicon-send"></span>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <script>
                                                                                    function confirmPageReload() {
                                                                                        return "Do you want to reload this page? Any changes you have made will not be saved.";
                                                                                    }
                                                                                </script>

            </div>
        </div>

        <div ng-if="registrationSuccess">
            <div class="text-center alert alert-success col-md-12">
                <h4>Your Registration is successful!</h4>
                <div style="padding-top: 20px;">
                    Please Login with your email and password to continue. <br> <br>
                    <div class="col-md-6 col-md-offset-3">
                        <a class="btn btn-success btn-block" href="login.html">Continue</a>
                    </div>
                </div>
            </div>
        </div>


    </div>

</body>
</html>
