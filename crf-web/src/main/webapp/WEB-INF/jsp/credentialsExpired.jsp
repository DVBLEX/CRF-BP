<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html ng-app="loginApp">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="format-detection" content="telephone=no">

<title>JMGCFinance</title>

<link rel="stylesheet" href="lib/bootstrap-3.3.7/css/bootstrap.min.css" />
<link rel="stylesheet" href="lib/jquery-ui-1.12.1/css/jquery-ui.min.css" />
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
<script type="text/javascript" src="app/js/services/tc-comservice-1.0.js"></script>
<script type="text/javascript" src="app/js/loginApp.js"></script>
</head>

<body ng-controller="credentialsExpiredController" ng-cloak>

    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="login.html"> <img src="app/img/crf_logo.jpeg" alt="" style="height: 120%; float: left;" /> &nbsp; <c:out value="${appName}" /> <c:if
                        test="${isTestEnvironment == true}">
                        <span>(Sandbox)</span>
                    </c:if>
                </a>
            </div>
        </div>
    </nav>

    {{recaptchaKey=
    <c:out value="${recaptchaKey}" />
    ;""}}

    <h3 class="text-center">Expired Password Credentials</h3>
    <h4 class="text-center">Please update your password below</h4>

    <div class="container-fluid crf-center" ng-cloak>

        <div ng-hide="passwdUpdateSuccess">
            <form name="dataForm" autocomplete="off" novalidate>
                <div class="form-group">
                    <label for="passwordOld">Current Password</label> <input type="password" class="form-control" name="passwordOld" placeholder="Current Password" title="Password"
                        ng-model="formData.passwordOld" ng-pattern="passwordRegexp" ng-maxlength="32" maxlength="32" required>
                </div>
                <div class="form-group">
                    <div class="alert alert-info">
                        <b>Password Policy:</b><br> Your password needs to be at least 8 characters long and a maximum of 32 characters. It must contain at least 1 capital
                        letter, 1 number and 1 special character. Allowed special characters: & ? ! $ #
                    </div>
                </div>
                <div class="form-group">
                    <label for="password">New Password</label> <input type="password" class="form-control" name="password" placeholder="Password" title="Password"
                        ng-model="formData.password" ng-pattern="passwordRegexp" ng-maxlength="32" maxlength="32" required>
                </div>
                <div class="form-group">
                    <label for="passwordConfirm">Confirm New Password</label> <input type="password" class="form-control" name="passwordConfirm" placeholder="Password"
                        title="Password" ng-model="formData.passwordConfirm" ng-maxlength="32" maxlength="32">
                </div>
                <div class="form-group">
                    {{recaptchaKey=
                    <c:out value="${recaptchaKey}" />
                    ;""}}
                    <div vc-recaptcha key="recaptchaKey" theme="clean" on-create="setWidgetId(widgetId)" on-success="setResponse(response)" on-expire="cbExpiration()"></div>
                </div>
                <div ng-if="credentialsExpiredUpdateErrorResponse !== ''">
                    <div class="alert alert-danger" role="alert">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> {{credentialsExpiredUpdateErrorResponse}}
                    </div>
                </div>
                <div class="form-group">
                    <button type="button" class="btn btn-primary btn-block" ng-disabled="passwdUpdateSuccess" ng-click="passwordUpdate()">Update Password</button>
                </div>

                {{t1=
                <c:out value="${key}" />
                ;""}} {{t2=
                <c:out value="${key2}" />
                ;""}} {{userName=
                <c:out value="${userName}" />
                ;""}}

            </form>
        </div>

        <div ng-if="passwdUpdateSuccess">
            <div class="text-center alert alert-success">
                <h4>Your password has been successfully updated!</h4>
                <div style="padding-top: 20px;">
                    You can log into the system with your new password. <br> <br> <a class="alert-link" href="login.html">Click here to Log In!</a>
                </div>
            </div>
        </div>

        <%@ include file="../../app/views/spinner.html"%>

    </div>

</body>
</html>