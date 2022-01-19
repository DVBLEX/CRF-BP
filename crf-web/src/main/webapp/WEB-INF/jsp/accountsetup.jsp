<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html ng-app="customerSetUpApp">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=10">
<meta name="format-detection" content="telephone=no">

<title>JMGCFinance - Customer Setup</title>

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
<script type="text/javascript" src="app/js/services/tc-commons-1.0.js"></script>
<script type="text/javascript" src="app/js/customerSetUpApp.js"></script>
<script type="text/javascript" src="app/js/services/customerSetUpService.js"></script>
<script type="text/javascript" src="lib/pdf.js-2.7.570-dist/pdf.min.js"></script>
<script type="text/javascript" src="lib/webcamjs/webcam.min.js"></script>
</head>
<body ng-controller="customerSetUpController" ng-cloak>

    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container-fluid" ng-cloak>
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#"> <img src="app/img/crf_logo.jpeg" alt="" style="height: 120%; float: left;" /> &nbsp; <c:out value="${appName}" /> <c:if test="${isTestEnvironment == true}">
                        <span>(Sandbox)</span>
                    </c:if>
                </a>
            </div>

            <div id="navbar" class="navbar-collapse collapse" aria-expanded="false" style="height: 1px;">
                <ul class="nav navbar-nav">
                    <!-- tabs go here -->
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li class="dropdown"><a href="" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false" title="${username}"><span
                            class="glyphicon glyphicon-user"></span> ${firstName} ${lastName} <span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="" ng-click="go('/changePassword');"><span class="glyphicon glyphicon-lock"></span>Change Password</a></li>
                            <li role="separator" class="divider"></li>
                            <li><a href="<c:url value="/perform_logout" />" data-method="delete" rel="nofollow"><span class="glyphicon glyphicon-log-out"></span>Log Out</a></li>
                        </ul></li>
                </ul>
            </div>
        </div>
    </nav>

    <h3 class="text-center">Customer Setup</h3>

    <div class="container-fluid" ng-cloak>

        {{deviceType=
        <c:out value="${deviceType}" />
        ;""}}

        <c:choose>
            <c:when test="${isPassportScanDenied == true}">

                <!-- Re-Submission layout -->
                <%@ include file="../../app/views/accountsetupMainReSubmission.jsp"%>

            </c:when>
            <c:otherwise>

                <!-- Main Layout for AccountSetup -->
                <%@ include file="../../app/views/accountsetupMainBase.jsp"%>

            </c:otherwise>
        </c:choose>

        <%@ include file="../../app/views/spinner.html"%>

    </div>

</body>
</html>
