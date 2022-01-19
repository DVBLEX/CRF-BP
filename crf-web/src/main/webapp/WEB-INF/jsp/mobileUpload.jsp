<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="format-detection" content="telephone=no">

<title>JMGCFinance</title>

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
</head>
<body ng-app="customerSetUpApp">

    <c:choose>
        <c:when test="${deviceType == \"'mobile'\" || deviceType == \"'tablet'\"}">
            <div ng-controller="takePhotoByPhoneController" class="reg-mainContainer" ng-cloak>

                <div class="noLeftPaddingForSubDivs" style="padding: 15px;" ng-show="!passportScanFileUploaded">

                    <c:choose>
                        <c:when test="${fileRole == \"'1'\"}">
                            <h4>Upload your Proof of ID photo</h4>
                        </c:when>
                        <c:when test="${fileRole == \"'2'\"}">
                            <h4>Upload your photo</h4>
                        </c:when>
                        <c:otherwise>
                            <h4>Upload your photo</h4>
                        </c:otherwise>
                    </c:choose>

                    <div class="form-group col-sm-12" style="padding-top: 20px;">

                        <div class="col-sm-12">
                            <label class="browseFilesButton btn btn-default btn-block"> <span class="glyphicon glyphicon-file"></span> Take or upload photo<input
                                type="file" id="passportScanFile" name="passportScanFile" file-model="passportScanFile" validate-file />
                            </label>
                        </div>

                    </div>

                    <div class="col-sm-12" ng-if="contactPassportScanErrorResponse !== ''">
                        <b>Selected file:</b> {{passportScanFileName}}
                    </div>

                    <div class="form-group col-md-12" style="padding-top: 20px;">
                        <c:choose>
                            <c:when test="${fileRole == \"'1'\"}">
                                Supported file types: JPG, JPEG, PDF
                            </c:when>
                            <c:when test="${fileRole == \"'2'\"}">
                                Supported file types: JPG, JPEG
                            </c:when>
                            <c:otherwise>
                                Supported file types: JPG, JPEG, PDF
                            </c:otherwise>
                        </c:choose>
                    </div>

                    {{fileRole=
                    <c:out value="${fileRole}" />
                    ;""}} {{input1=
                    <c:out value="${userName}" />
                    ;""}} {{input2=
                    <c:out value="${key}" />
                    ;""}} {{input3=
                    <c:out value="${key2}" />
                    ;""}}
                </div>

                <div class="form-group col-md-12" ng-if="contactPassportScanErrorResponse !== ''">
                    <div class="alert alert-danger" role="alert">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> {{contactPassportScanErrorResponse}}
                    </div>
                </div>

                <div class="form-group col-md-12" ng-if="passportScanFileUploaded">
                    <div class="alert alert-success" role="alert">
                        <span>
                            <div class="form-group">
                                <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
                                <c:choose>
                                    <c:when test="${fileRole == \"'1'\"}">
                                        Proof of ID is uploaded successfully.
                                    </c:when>
                                    <c:when test="${fileRole == \"'2'\"}">
                                        Photo is uploaded successfully.
                                    </c:when>
                                    <c:otherwise>
                                        File is uploaded successfully.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </span>
                    </div>
                </div>

                <%@ include file="../../app/views/spinner.html"%>
            </div>
        </c:when>

        <c:otherwise>
            <div>&nbsp;</div>
            <div class="text-center alert alert-warning col-md-12">
                <h4>Smartphone upload</h4>
                <p style="padding-top: 20px;">
                    Please open this page on your smartphone. <br>
                </p>
            </div>
        </c:otherwise>
    </c:choose>

</body>
</html>