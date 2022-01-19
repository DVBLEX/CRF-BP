<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-fluid no-padding" ng-cloak>

    <div class="popup-wrapper" ng-if="isPopupShowed === true">
        <div class="popup-body">
            <div class="popup-content">
                <div class="popup-title">
                    Information
                </div>
                <hr>
                <div class="popup-text">
                    <json-formatter json="viewStringJson" open="3"></json-formatter>
                </div>
                <hr>
                <div>
                    <button ng-click="showAMLPopup(false)" class="btn btn-primary btn-sm">
                        <span class="glyphicon glyphicon-arrow-left"></span> Back
                    </button>
                </div>
            </div>
        </div>
    </div>

    <div class="d-table w-100" style="margin-bottom: 5px;">
        <nav aria-label="breadcrumb" class="d-table-cell">
            <ol class="breadcrumb d-table-cell w-100">
                <li class="breadcrumb-item active">Customers</li>
            </ol>
        </nav>
        <div class="d-table-cell tar">
            <button ng-click="refreshTableData();" class="btn btn-primary btn-sm" ng-hide="isVerifyingCustomer" ng-disabled="disableControls">
                <span class="glyphicon glyphicon-refresh"></span> Refresh
            </button>
            <button ng-click="closeVerifyingCustomer();" class="btn btn-primary btn-sm" ng-show="isVerifyingCustomer"
                ng-disabled="disableControls">
                <span class="glyphicon glyphicon-arrow-left"></span> Back
            </button>
        </div>
    </div>

    <div ng-show="!isVerifyingCustomer">

        <table class="tc-table">

            <thead>
                <tr>
                    <td>Title</td>
                    <td><a href="" ng-click="sortType = 'firstName'; sortReverse = !sortReverse;"> First Name <span ng-show="sortType == 'firstName' && !sortReverse"
                            class="glyphicon glyphicon-sort-by-attributes font-size-10"></span> <span ng-show="sortType == 'firstName' && sortReverse"
                            class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a></td>
                    <td><a href="" ng-click="sortType = 'lastName'; sortReverse = !sortReverse;"> Last Name <span ng-show="sortType == 'lastName' && !sortReverse"
                            class="glyphicon glyphicon-sort-by-attributes font-size-10"></span> <span ng-show="sortType == 'lastName' && sortReverse"
                            class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a></td>
                    <td>Email</td>
                    <td>Mobile Number</td>
                    <td>Type</td>
                    <td>Verify</td>
                </tr>
            </thead>

            <colgroup>
                <col width="6%">
                <col width="15%">
                <col width="15%">
                <col width="15%">
                <col width="15%">
                <col width="14%">
                <col width="10%">
            </colgroup>

            <tbody ng-repeat="row in rows = (tcTable.data | orderBy:sortType:sortReverse)">
                <tr ng-click="tcTable.selectRow($event)">
                    <td title="Title">{{row.title}}</td>
                    <td title="First Name">{{row.firstName}}</td>
                    <td title="Last Name">{{row.lastName}}</td>
                    <td title="Email">{{row.email}}</td>
                    <td title="Mobile Number">{{row.msisdn}}</td>
                    <td class="text-center" title="Type"><span class="label label-info" ng-if="row.type == 1">INVESTOR</span> <span class="label label-info"
                        ng-if="row.type == 2">BORROWER</span> <span class="label label-info" ng-if="row.type == 3">INVESTOR & BORROWER</span></td>
                    <td class="text-center" title="Verify">
                        <button class="btn btn-success btn-intable" ng-click="verifyCustomer(row); $event.stopPropagation();"
                            ng-if="!row.isPassportScanDenied && row.isPhotoUploaded && row.isPassportScanUploaded && !row.isPassportScanVerified" title="Verify Customer">
                            <span class="glyphicon glyphicon-ok"></span>
                        </button> <span ng-if="row.isPassportScanVerified" style="color: green;"><span class="glyphicon glyphicon-ok"></span> Verified</span> <span
                        ng-if="row.isPassportScanDenied" style="color: red;"><span class="glyphicon glyphicon-remove"></span> Denied</span> <span ng-if="" style="color: red;"><span
                            class="glyphicon glyphicon-remove"></span> Deleted</span> <span ng-if="" style="color: red;"><span class="glyphicon glyphicon-remove"></span> Suspended</span>
                    </td>
                </tr>
            </tbody>

            <tr ng-show="tcTable.count == 0">
                <td colspan="7" class="tc-table-extension-cell text-center">No customers returned</td>
            </tr>

            <!-- Telclic Table Pagination Component -->
            <tr>
                <td colspan="7" class="tc-table-extension-cell text-left"><tc-table-pagination></tc-table-pagination></td>
            </tr>
        </table>

    </div>

    <div ng-show="isVerifyingCustomer">
        <!-- Verify customer -->
        <form name="dataFormCustomerVerify" autocomplete="off" novalidate>

            <div class="form-group well col-md-12" style="padding-top: 10px; padding-bottom: 10px;">
                <div class="row col-md-12">
                    <div class="col-xs-12 col-md-6">
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Title</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.title}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>First Name</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.firstName}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Last Name</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.lastName}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>National ID Number</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.nationalIdNumber}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Date of Birth</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.dateOfBirthString}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Country (Nationality)</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{getCountryNameFromISOCode(customer.nationality)}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Country (Residence)</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{getCountryNameFromISOCode(customer.residnenceCountry)}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Customer Type</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{getCustomerTypeText(customer.type)}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Customer Category</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{getCustomerCategoryText(customer.category)}}</div>
                        </div>
                    </div>
                    <div class="col-xs-12 col-md-6">
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Address Line 1</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.address1}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Address Line 2</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.address2}}</div>
                        </div>
                        <div class="row" ng-if="customer.address3">
                            <div class="col-xs-4 col-md-4">
                                <label>Address Line 3</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.address3}}</div>
                        </div>
                        <div class="row" ng-if="customer.address4">
                            <div class="col-xs-4 col-md-4">
                                <label>Address Line 4</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.address4}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Email</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.email}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label>Mobile Number</label>
                            </div>
                            <div class="col-xs-8 col-md-8">{{customer.msisdn}}</div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4 col-md-4">
                                <label ng-if="customer.isAmlVerified" class="AML-success-highlight">AML Check Passed</label>
                                <label ng-if="!customer.isAmlVerified" class="AML-wrong-highlight">AML Match Found</label>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-8 col-md-8">
                                <a href="" ng-class="!customer.isAmlVerified ? 'AML-wrong-highlight' : 'AML-success-highlight'" ng-click="showAMLPopup(true)">More information</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%--STEP 1--%>
            <div class="form-group col-md-12 no-padding" ng-cloak ng-show="isStep1 === true">

                <div class="photo-and-document-wrapper">
                    <div class="pull-left scf-imgCompare no-padding">

                        <div class="POID-label">
                            <label class="scf-imgCompareLabel">
                                {{getCustomerIdTypeText(customer.id1Type)}}
                            </label>
                            <div>
                                <span>
                                    Expire Date: <b>{{customer.dateId1ExpiryString}}</b>
                                </span>
                                <span> | </span>
                                <span>
                                    Number: <b>{{customer.id1Number}}</b>
                                </span>
                            </div>
                        </div>

                        <div file-viewer canvas-id="'POID1'"></div>

                    </div>

                    <div ng-if="POIDDocument2Type !== ''" class="pull-left scf-imgCompare no-padding">

                        <div class="POID-label">
                            <label class="scf-imgCompareLabel">
                                {{getCustomerIdTypeText(customer.id2Type)}}
                            </label>
                            <div>
                                <span>
                                    Expire Date: <b>{{customer.dateId2ExpiryString}}</b>
                                </span>
                                <span> | </span>
                                <span>
                                    Number: <b>{{customer.id2Number}}</b>
                                </span>
                            </div>
                        </div>

                        <div file-viewer canvas-id="'POID2'"></div>

                    </div>
                </div>

            </div>

            <div class="form-group well col-md-12" ng-show="isStep1 === true">
                <div class="col-md-10 col-md-offset-1">
                    <fieldset ng-repeat="reason in POIDVerificationDenialReasonList">
                        <div class="col-md-6 text-right">
                            <label>{{reason.question}}</label>
                        </div>
                        <div class="col-md-4 text-left">
                            <label class="scf-label-radio">
                                <input type="radio" name="" ng-model="reason.radio" ng-change="radioSelected(reason, 'firstOtherReason')" value="YES" required />
                                YES
                            </label>&nbsp;&nbsp;&nbsp;
                            <label class="scf-label-radio"><input type="radio" name="" ng-model="reason.radio" ng-change="radioSelected(reason, 'firstOtherReason')"
                                value="NO" required /> NO
                            </label>
                        </div>
                    </fieldset>

                    <div class="col-sm-8 col-sm-offset-2 form-group" ng-show="isOtherReasonSelected1">
                        <label for="otherReason1">Other reason</label> <input type="text" class="form-control" id="otherReason1" name="otherReason1" ng-model="formData.otherReason1" maxlength="128"
                            required>
                    </div>

                    <div class="col-sm-8 col-sm-offset-2 form-group">
                        <div class="animate-if" ng-if="verifyCustomerErrorMessage !== ''">
                            <div class="alert alert-danger" role="alert">
                                <span class="glyphicon glyphicon-remove"></span><span class="sr-only">Error: </span> <span>{{verifyCustomerErrorMessage}}</span>
                            </div>
                        </div>
                    </div>

                    <div class="col-sm-8 col-sm-offset-2 form-group">
                        <button ng-click="customerVerificationSubmit(POIDVerificationDenialReasonList, 2)" type="submit" class="btn btn-primary btn-block">
                            Next <span class="glyphicon glyphicon-chevron-right"></span>
                        </button>
                    </div>

                </div>
            </div>


            <%--STEP 2--%>
            <div class="form-group col-md-12 no-padding" ng-cloak ng-show="isStep2 === true">

                <div class="photo-and-document-wrapper">
                    <div class="pull-left scf-imgCompare no-padding">

                        <label class="scf-imgCompareLabel">{{getCustomerPOADocument(customer.poa1Type)}}</label>

                        <div file-viewer canvas-id="'POA1'" class=""></div>

                    </div>

                    <div ng-if="POADocument2Type !== ''" class="pull-left scf-imgCompare no-padding">

                        <label class="scf-imgCompareLabel">{{getCustomerPOADocument(customer.poa2Type)}}</label>

                        <div file-viewer canvas-id="'POA2'"></div>

                    </div>
                </div>
            </div>

            <div class="form-group well col-md-12" ng-show="isStep2 === true">
                <div class="col-md-10 col-md-offset-1">
                    <fieldset ng-repeat="reason in POAVerificationDenialReasonList">
                        <div class="col-md-6 text-right">
                            <label>{{reason.question}}</label>
                        </div>
                        <div class="col-md-4 text-left">
                            <label class="scf-label-radio">
                                <input type="radio" name="" ng-model="reason.radio" ng-change="radioSelected(reason, 'secondOtherReason')" value="YES" required />
                                YES
                            </label>&nbsp;&nbsp;&nbsp;
                            <label class="scf-label-radio"><input type="radio" name="" ng-model="reason.radio" ng-change="radioSelected(reason, 'secondOtherReason')"
                                                                  value="NO" required /> NO
                            </label>
                        </div>
                    </fieldset>

                    <div class="col-sm-8 col-sm-offset-2 form-group" ng-show="isOtherReasonSelected2">
                        <label for="otherReason2">Other reason</label> <input type="text" class="form-control" id="otherReason2" name="otherReason2" ng-model="formData.otherReason2" maxlength="128"
                                                                             required>
                    </div>

                    <div class="col-sm-8 col-sm-offset-2 form-group">
                        <div class="animate-if" ng-if="verifyCustomerErrorMessage !== ''">
                            <div class="alert alert-danger" role="alert">
                                <span class="glyphicon glyphicon-remove"></span><span class="sr-only">Error: </span> <span>{{verifyCustomerErrorMessage}}</span>
                            </div>
                        </div>
                    </div>

                    <div class="col-sm-8 col-sm-offset-2 form-group">
                        <button ng-click="customerVerificationSubmit(POAVerificationDenialReasonList, 3)" type="submit" class="btn btn-primary btn-block">
                            Next <span class="glyphicon glyphicon-chevron-right"></span>
                        </button>
                    </div>
                    <div class="col-sm-8 col-sm-offset-2 form-group">
                        <button type="submit" class="btn btn-warning btn-block" ng-click="returnToPreviewStep(1)">
                            <span class="glyphicon glyphicon-chevron-left"></span> Back
                        </button>
                    </div>

                </div>
            </div>

            <%--STEP 3--%>
            <div class="form-group col-md-12 no-padding" ng-cloak ng-show="isStep3 === true">
                <div class="photo-and-document-wrapper">
                    <div class="pull-left scf-imgCompare no-padding">

                        <div class="POID-label">
                            <label class="scf-imgCompareLabel">
                                {{getCustomerIdTypeText(customer.id1Type)}}
                            </label>
                            <div>
                                <span>
                                    Expire Date: <b>{{customer.dateId1ExpiryString}}</b>
                                </span>
                                <span> | </span>
                                <span>
                                    Number: <b>{{customer.id1Number}}</b>
                                </span>
                            </div>
                        </div>

                        <div file-viewer canvas-id="'POID1ForConfirmation'"></div>

                    </div>

                    <div class="pull-left scf-imgCompare no-padding">
                        <label class="scf-imgCompareLabel">Photo</label>
                        <img class="img-thumbnail" ng-src="data:{{photoFileType}};base64,{{photoImage}}">
                    </div>
                </div>
            </div>

            <div class="form-group well col-md-12" ng-if="isStep3 === true">
                <div class="col-md-10 col-md-offset-1">
                    <fieldset ng-repeat="reason in photoVerificationDenialReasonList">
                        <div class="col-md-6 text-right">
                            <label>{{reason.question}}</label>
                        </div>
                        <div class="col-md-4 text-left">
                            <label class="scf-label-radio">
                                <input type="radio" name="" ng-model="reason.radio" ng-change="radioSelected(reason, 'thirdOtherReason')" value="YES" required />
                                YES
                            </label>&nbsp;&nbsp;&nbsp;
                            <label class="scf-label-radio"><input type="radio" name="" ng-model="reason.radio" ng-change="radioSelected(reason, 'thirdOtherReason')"
                                                                  value="NO" required /> NO
                            </label>
                        </div>
                    </fieldset>

                    <div class="col-sm-8 col-sm-offset-2 form-group" ng-show="isOtherReasonSelected3">
                        <label for="otherReason3">Other reason</label> <input type="text" class="form-control" id="otherReason3" name="otherReason3" ng-model="formData.otherReason3" maxlength="128"
                                                                             required>
                    </div>

                    <div class="col-sm-8 col-sm-offset-2 form-group">
                        <div class="animate-if" ng-if="verifyCustomerErrorMessage !== ''">
                            <div class="alert alert-danger" role="alert">
                                <span class="glyphicon glyphicon-remove"></span><span class="sr-only">Error: </span> <span>{{verifyCustomerErrorMessage}}</span>
                            </div>
                        </div>
                    </div>

                    <div class="col-sm-8 col-sm-offset-2 form-group">
                        <button type="button" class="btn btn-block" style="width: 100%"
                                ng-class="{'btn-success': !isThereAnyYesAnswer(), 'btn-danger': isThereAnyYesAnswer() }"
                                ng-click="customerVerificationSubmit(photoVerificationDenialReasonList, 0)" ng-disabled="verifyCustomerSubmitButtonDisabled">
                            <span ng-show="!isThereAnyYesAnswer()">Verify</span>
                            <span ng-show="isThereAnyYesAnswer()">Refuse</span>
                            <span class="glyphicon glyphicon-chevron-right"></span>
                        </button>
                    </div>
                    <div class="col-sm-8 col-sm-offset-2 form-group">
                        <button type="submit" class="btn btn-warning btn-block" ng-click="returnToPreviewStep(2)">
                            <span class="glyphicon glyphicon-chevron-left"></span> Back
                        </button>
                    </div>

                </div>
            </div>

        </form>
    </div>

    <%@ include file="../spinner.html"%>

</div>
