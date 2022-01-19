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

<body ng-controller="passwordForgotController" ng-cloak>

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

    {{recaptchaKey=
    <c:out value="${recaptchaKey}" />
    ;""}}

    <h3 class="text-center">Forgot Password</h3>

    <div class="container-fluid crf-center" ng-cloak>

        <div ng-hide="passwdForgotSendSuccess">
            <form name="dataForm" autocomplete="off" novalidate>
                <div class="form-group">
                    <label for="input1">Email</label> <input type="text" class="form-control" id="input1" name="input1" placeholder="Email" title="Email" ng-model="emailSend"
                        ng-pattern="emailRegexp" ng-maxlength="64" maxlength="64" required autofocus>
                </div>
                <div class="form-group">
                    <div vc-recaptcha key="recaptchaKey" theme="clean" on-create="setWidgetId(widgetId)" on-success="setResponse(response)" on-expire="cbExpiration()"></div>
                </div>
                <div class="form-group">
                    <div class="animate-if" ng-if="passwordForgotStep1ErrorResponse !== ''">
                        <div class="alert alert-danger" role="alert">
                            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span><span class="sr-only">Error: </span> <span>{{passwordForgotStep1ErrorResponse}}</span>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <button type="button" class="btn btn-primary btn-block" ng-click="passwordForgotSubmit()">Submit</button>
                </div>
            </form>
        </div>

        <div ng-if="passwdForgotSendSuccess">
            <div class="text-center alert alert-success">
                <h4>If you have entered the correct details, you will receive an email with instructions on how to reset your password.</h4>
                <div style="padding-top: 20px;">
                    Please check your inbox and follow the instructions.<br />If you don't see an email, please check your spam / junk folder as it might have been filtered out.
                </div>
            </div>
        </div>

        <%@ include file="../../app/views/spinner.html"%>

    </div>

</body>
</html>