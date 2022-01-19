<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html ng-app="crfApp" ng-controller="CRFController">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="format-detection" content="telephone=no">

<title>JMGCFinance</title>

<link rel="stylesheet" href="lib/bootstrap-3.3.7/css/bootstrap.min.css" />
<link rel="stylesheet" href="lib/jquery-ui-1.12.1/css/jquery-ui.min.css" />
<link rel="stylesheet" href="lib/intl-tel-input-12.0.3/css/intlTelInput.css">
<link rel="stylesheet" href="app/css/tc-table-1.1.css">
<link rel="stylesheet" href="app/css/crf.css" />
<link rel="stylesheet" href="lib/jsonformatter/json-formatter.min.css">

<script type="text/javascript" src="lib/jquery-2.2.4/js/jquery.min-2.2.4.js"></script>
<script type="text/javascript" src="lib/jquery-ui-1.12.1/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="lib/jquery-2.2.4/js/jquery.priceformat.min.js"></script>
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
<script type="text/javascript" src="app/js/crfMainApp.js"></script>

<script type="text/javascript" src="app/js/services/changePasswordService.js"></script>
<script type="text/javascript" src="app/js/services/homeService.js"></script>
<script type="text/javascript" src="app/js/services/customerService.js"></script>
<script type="text/javascript" src="app/js/services/depositService.js"></script>
<script type="text/javascript" src="app/js/services/usersService.js"></script>
<script type="text/javascript" src="app/js/services/depositProductsAdminService.js"></script>

<script type="text/javascript" src="app/js/controllers/homeController.js"></script>
<script type="text/javascript" src="app/js/controllers/changePasswordController.js"></script>
<c:if test="${isAdmin == true}">
    <script type="text/javascript" src="app/js/controllers/depositProductsAdminController.js"></script>
    <script type="text/javascript" src="app/js/controllers/usersController.js"></script>
    <script type="text/javascript" src="app/js/controllers/customerController.js"></script>
    <script type="text/javascript" src="app/js/controllers/depositAdminController.js"></script>
    <script type="text/javascript" src="app/js/controllers/paymentAdminController.js"></script>
</c:if>
<c:if test="${isAdmin != true}">
    <script type="text/javascript" src="app/js/controllers/depositCustomerController.js"></script>
    <script type="text/javascript" src="app/js/controllers/depositDocumentController.js"></script>
    <script type="text/javascript" src="app/js/controllers/depositStatementController.js"></script>
    <script type="text/javascript" src="app/js/controllers/bankAccountCustomerController.js"></script>
</c:if>
<script type="text/javascript" src="app/js/directives/utilsDirective.js"></script>
<script type="text/javascript" src="app/js/filters/filter.js"></script>
<script type="text/javascript" src="app/js/tc-table-1.1.js"></script>
<script type="text/javascript" src="lib/pdf.js-2.7.570-dist/pdf.min.js"></script>
<script type="text/javascript" src="lib/jsonformatter/json-formatter.min.js"></script>
</head>

<body ng-init="nameUser = '${username}'; init('${username}', '${isAdmin}', '${isInvestorOperator}', '${isBorrowerOperator}', '${customerCategory}', '${isCustomerAccountVerified}')"
    ng-cloak>

    <div class="divLoadingBackground" ng-if="isLoading"></div>

    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
                </button>
                <a ng-click="go('/')" class="navbar-brand" href=""> <img src="app/img/crf_logo.jpeg" alt="" style="height: 120%; float: left;" /> &nbsp; <c:out value="${appName}" /> <c:if test="${isTestEnvironment == true}">
                        <span>(Sandbox)</span>
                    </c:if>
                </a>
            </div>

            <div id="navbar" class="navbar-collapse collapse" aria-expanded="false" style="height: 1px;">
                <ul class="nav navbar-nav">

                    <li id="navbarHome" class="active"><a href="" ng-click="go('/')">Home</a></li>

                    <c:choose>

                        <c:when test="${isInvestorOperator == true}">
                            <li id="navbarDeposits" class=""><a href="" ng-click="go('/customer/deposits')">Deposits</a></li>
                            <li id="navbarDepositDocuments" class=""><a href="" ng-click="go('/customer/deposit/docs')">Deposit Documents</a></li>
                            <li id="navbarDepositStatements" class=""><a href="" ng-click="go('/customer/deposit/stmts')">Deposit Statements</a></li>
                            <li id="navbarBankAccount" class=""><a href="" ng-click="go('/customer/bankaccount')">Bank Account</a></li>
                        </c:when>

                        <c:when test="${isBorrowerOperator == true}">

                        </c:when>

                        <c:when test="${isAdmin == true}">
                            <li id="navbarUsers" class=""><a href="" ng-click="go('/users')">Users</a></li>
                            <li id="navbarCustomers" class=""><a href="" ng-click="go('/customers')">Customers</a></li>
                            <li id="navbarDeposits" class=""><a href="" ng-click="go('/deposits')">Deposits</a></li>
                            <li id="navbarProducts" class=""><a href="" ng-click="go('/depositProducts')">Products</a></li>
                            <li id="navbarPayments" class=""><a href="" ng-click="go('/payments')">Payments</a></li>
                        </c:when>

                    </c:choose>
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

    <div class="navbar navbar-default navbar-fixed-bottom">
        <div class="container-fluid">
            <p class="navbar-text pull-left">
                © {{currentYear}} - <a href="https://jmgcfinanace.com" target="_blank"><c:out value="${appName}" /></a>
            </p>

            <div class="navbar-text pull-right">
                <button ng-click="showContactUsFormModal();" class="btn btn-primary btn-md no-padding" ng-disabled="disableControls">Contact Us</button>

            </div>
        </div>
    </div>

    <div ng-if="showSuccess" class="alert alert-success showResult showSuccessResult">
        <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
        <p class="font-size-16">
            <span class="glyphicon glyphicon-ok font-size-16">&nbsp;</span>{{showSuccess}}
        </p>
    </div>

    <div ng-if="showError" class="alert alert-danger showResult showErrorResult">
        <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
        <p class="font-size-16">
            <span class="glyphicon glyphicon-ban-circle font-size-16">&nbsp;</span>{{showError}}
        </p>
    </div>

    <div class="modal fade" id="genericConfirmationModal" tabindex="-1" style="z-index: 9999;" role="dialog" aria-labelledby="genericConfirmationModalLabel">
        <div class="modal-dialog modal-dialog-record" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" ng-click="closeGenericConfirmationModal()" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title" id="genericConfirmationModalLabel">Confirmation</h4>

                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="panel panel-info">
                                <div class="panel-heading font-size-18"></div>
                                <div class="panel-body">
                                    <form name="confirmationForm" autocomplete="off" novalidate>
                                        <div class="col-md-12 form-group" style="padding: 0px;">

                                            <div class="row">
                                                <div class="col-md-12">
                                                    <b>{{confirmationModalText}}</b>
                                                </div>
                                                <br> <br>
                                            </div>

                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <form name="modalButtonsForm" autocomplete="off" novalidate>
                    <div class="modal-footer text-alight-right">
                        <button ng-click="modalButtonsForm.$invalid=true;callbackFunction();" class="btn btn-success outline-none width-modal-btn"
                            data-ng-disabled="modalButtonsForm.$invalid">
                            <span class="glyphicon glyphicon-ok"></span> Confirm
                        </button>

                        <button type="button" class="btn btn-danger outline-none width-modal-btn" ng-click="closeGenericConfirmationModal()">
                            <b><span class="glyphicon glyphicon-remove"></span> Cancel</b>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Contact Us Modal -->
    <div class="modal fade" id="contactUsModal" tabindex="-1" role="dialog" aria-labelledby="contactUsModalLabel" ng-form="contactForm">
        <fieldset>
            <div class="modal-dialog modal-dialog-record modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" ng-click="closeContactUsModal()" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title" id="contactUsModalLabel">Contact Us</h4>

                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-12">

                                <form name="contactForm" autocomplete="off" novalidate>

                                    <div class="form-group col-md-12 no-padding">
                                        <label for="queryType">What can we help you with? </label> <select class="form-control" id="queryType" name="queryType"
                                            ng-model="formData.queryType" required>
                                            <option value="">Please select a topic</option>
                                            <option value="I need to open a deposit">I need to open a deposit</option>
                                            <option value="I have transferred the deposit but it is not confirmed yet">I have transferred the deposit but it is not
                                                confirmed yet</option>
                                            <option value="I need to withdraw my deposit before the maturity date">I need to withdraw my deposit before the maturity date</option>
                                            <option value="I would like to make a suggestion / share product feedback">I would like to make a suggestion / share product
                                                feedback</option>
                                            <option value="Other">Other</option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <label for="queryDetails">Please include details about your query below </label>
                                        <textarea class="form-control" style="width: 100%; height: 165px;" ng-model="formData.queryDetails" name="queryDetails" id="queryDetails"
                                            ng-minlength="15" minlength="15" ng-maxlength="5000" maxlength="5000" required></textarea>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <div class="animate-if" ng-if="contactUsErrorResponse !== ''">
                                            <div class="alert alert-danger" role="alert">
                                                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> <span>{{contactUsErrorResponse}}</span>
                                            </div>
                                        </div>
                                    </div>

                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer text-alight-center">
                        <button ng-click="submitContactForm();" class="btn btn-success outline-none width-modal-btn" ng-disabled="submitButtonDisabled">
                            <span class="glyphicon glyphicon-ok"></span> Submit
                        </button>
                        <button type="button" class="btn btn-danger outline-none width-modal-btn" ng-click="closeContactUsModal()">
                            <b><span class="glyphicon glyphicon-remove"></span> Cancel</b>
                        </button>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>

    <div ng-view></div>

    <span us-spinner="{radius:24, width:8, length: 25}" spinner-key="pad-spinner"></span>

</body>
</html>
