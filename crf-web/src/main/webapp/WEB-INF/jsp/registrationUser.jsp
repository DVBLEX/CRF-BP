<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html ng-app="registrationUserApp">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="format-detection" content="telephone=no">

    <title>JMGCFinance - Registration</title>

    <base href="${pageContext.request.contextPath}/">

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
    <script type="text/javascript" src="lib/spin-2.3.2/js/spin.min-2.3.2.js"></script>
    <script type="text/javascript" src="lib/intl-tel-input-12.0.3/js/intlTelInput.min.js"></script>
    <script type="text/javascript" src="lib/intl-tel-input-12.0.3/js/utils.js"></script>
    <script type="text/javascript" src="lib/ng-intl-tel-input-2.0.0/js/ng-intl-tel-input.min.js"></script>
    <script type="text/javascript" src="app/js/services/tc-comservice-1.0.js"></script>
    <script type="text/javascript" src="app/js/registrationUserApp.js"></script>
</head>

<body ng-controller="registrationUserController" ng-cloak ng-init="init('${isTestEnvironment}','${email}','${token}','${firstName}','${lastName}')">

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
        <div id="navbar" class="navbar-collapse collapse" aria-expanded="false" style="height: 1px;">
            <ul class="nav navbar-nav navbar-right">

                <li class="dropdown">
                    <span class="registration-username-wrapper">
                        <span class="glyphicon glyphicon-user"></span>
                        ${firstName} ${lastName}
                    </span>
                </li>
            </ul>
        </div>
    </div>
</nav>

<h3 class="text-center">Registration</h3>

<div class="container-fluid crf-center" ng-cloak>

    <div ng-hide="registrationUserSuccess">
        <form name="dataForm" autocomplete="off" novalidate>
            <div class="form-group">
                <label for="password">Email</label> <input type="text" class="form-control" name="email" id="email" placeholder="Email" title="Email" ng-model="email" disabled>
            </div>
            <div class="form-group">
                <div class="alert alert-info">
                    <b>Password Policy:</b><br> Your password needs to be at least 8 characters long and a maximum of 32 characters. It must contain at least 1 capital
                    letter, 1 number and 1 special character. Allowed special characters: & ? ! $ #
                </div>
            </div>
            <div class="form-group">
                <label for="password">Password</label> <input type="password" class="form-control" name="password" id="password" placeholder="Password" title="Password"
                                                              ng-model="formData.password" ng-pattern="passwordRegexp" ng-maxlength="32" maxlength="32" required>
            </div>
            <div class="form-group">
                <label for="passwordConfirm">Confirm Password</label> <input type="password" class="form-control" name="passwordConfirm" id="passwordConfirm" placeholder="Password" title="Password"
                                                                             ng-model="formData.passwordConfirm" ng-maxlength="32" maxlength="32">
            </div>
            <div ng-if="passwordErrorResponse !== ''">
                <div class="alert alert-danger" role="alert">
                    <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> {{passwordErrorResponse}}
                </div>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" ng-disabled="passwordSubmitButtonDisabled || passwdChangeSuccess"
                        ng-click="registerUser()">Submit</button>
            </div>

        </form>

    </div>

    <div ng-if="registrationUserSuccess">
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

    <%@ include file="../../app/views/spinner.html"%>

</div>

</body>
</html>
