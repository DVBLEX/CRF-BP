<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item active">Deposits</li>
    </ol>
</nav>

<div class="container-fluid no-padding">

    <table class="tc-table">

        <thead>
            <tr>
                <td>Product</td>
                <td>Account Number</td>
                <td>Deposit Amount</td>
                <td>Interest Rate</td>
                <td>Term (years)</td>
                <td>Status</td>
                <td>Interest Payment</td>
                <td>Interest Earnings</td>
                <td>Bank Transfer Ref</td>
                <td>Open Date</td>
                <td>Start Date</td>
                <td>Maturity Date</td>
                <td>Withdrawal Date</td>
                <td>Withdrawal</td>
            </tr>
        </thead>

        <colgroup>
            <col width="10%">
            <col width="10%">
            <col width="2%">
            <col width="2%">
            <col width="2%">
            <col width="5%">
            <col width="5%">
            <col width="5%">
            <col width="5%">
            <col width="5%">
            <col width="5%">
            <col width="5%">
            <col width="5%">
            <col width="10%">
        </colgroup>

        <tbody ng-repeat="row in rows = (tcTable.data | orderBy:sortType:sortReverse)">
            <tr ng-click="tcTable.selectRow($event)">
                <td title="Product">{{row.depositProductJson.name}}</td>
                <td title="Account Number">{{row.accountNumber}}</td>
                <td title="Deposit Amount">{{row.depositAmount}}</td>
                <td title="Interest Rate">{{row.interestRate}}</td>
                <td title="Term (years)">{{row.termYears}}</td>
                <td class="text-center" title="Status"><span class="label label-primary" ng-if="row.status == 1">INITIATED</span> <span class="label label-success"
                    ng-if="row.status == 2">ACTIVE</span> <span class="label label-primary" ng-if="row.status == 3">WITHDRAWAL REQUESTED</span><span class="label label-primary"
                    ng-if="row.status == 4">WITHDRAWN</span><span class="label label-warning" ng-if="row.status == 5">INITIATED - EXPIRED</span><span class="label label-success"
                    ng-if="row.status == 10">MATURED</span></td>
                <td class="text-center" title="Interest Payment"><span class="label label-primary" ng-if="row.interestPayoutFrequency == 1">QUARTERLY</span> <span
                    class="label label-primary" ng-if="row.interestPayoutFrequency == 2">YEARLY</span> <span class="label label-primary" ng-if="row.interestPayoutFrequency == 3">HALF-YEARLY</span></td>
                <td title="Interest Earnings">{{row.interestEarnedAmount}}</td>
                <td title="Bank Transfer Ref">{{row.bankTransferReference}}</td>
                <td title="Open Date">{{row.dateOpenString}}</td>
                <td title="Start Date">{{row.dateStartString}}</td>
                <td title="Maturity Date">{{row.dateMaturityString}}</td>
                <td title="Withdrawal Date">{{row.dateWithdrawalString}}</td>
                <td class="text-center" title="Withdrawal">
                    <button class="btn btn-success btn-intable" ng-click="showRequestDepositWithdrawalModal(row); $event.stopPropagation();"
                        ng-if="row.status == 2 || row.status == 10" title="Request Withdrawal">
                        <span class="glyphicon glyphicon-download-alt"></span>
                    </button> <span ng-if="row.status == 3" style="color: green;"><span class="glyphicon glyphicon-ok"></span> Requested</span> <span ng-if="row.status == 1"
                    style="color: orange;">Unavailable</span> <span ng-if="row.status == 4" style="color: green;"><span class="glyphicon glyphicon-ok"></span> Withdrawn</span>
                </td>
            </tr>
        </tbody>

        <tr ng-show="tcTable.count == 0">
            <td colspan="14" class="tc-table-extension-cell text-center">No deposits returned</td>
        </tr>

        <!-- Telclic Table Pagination Component -->
        <tr>
            <td colspan="14" class="tc-table-extension-cell text-left"><tc-table-pagination></tc-table-pagination></td>
        </tr>
    </table>

    <!-- Request Deposit Withdrawal Modal -->
    <div class="modal fade" id="requestDepositWithdrawalModal" tabindex="-1" role="dialog" aria-labelledby="requestDepositWithdrawalModalLabel"
        ng-form="requestDepositWithdrawalForm">
        <fieldset>
            <div class="modal-dialog modal-dialog-record modal-lg" role="document">
                <div class="modal-content medium-font-size">
                    <div class="modal-header">
                        <button type="button" class="close" ng-click="closeRequestDepositWithdrawalModal()" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title" id="requestDepositWithdrawalModalLabel">Request for Deposit Withdrawal</h4>

                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-12">

                                <form name="requestDepositWithdrawalForm" autocomplete="off" novalidate>

                                    <table class="table table-bordered" style="background-color: #f5f5f5;">
                                        <tbody>
                                            <tr>
                                                <td style="width: 50%;">Product</td>
                                                <td>{{selectedRow.depositProductJson.name}}</td>
                                            </tr>
                                            <tr>
                                                <td>Start Date</td>
                                                <td>{{selectedRow.dateStartString}}</td>
                                            </tr>
                                            <tr>
                                                <td>Maturity Date</td>
                                                <td>{{selectedRow.dateMaturityString}}</td>
                                            </tr>
                                            <tr>
                                                <td>Deposit Amount</td>
                                                <td>{{selectedRow.depositAmount | currency:'\u20AC'}}</td>
                                            </tr>
                                            <tr>
                                                <td>Interest Earnings Already Paid @ {{selectedRow.interestRate}}%</td>
                                                <td>{{selectedRow.interestEarnedAmount | currency:'\u20AC'}}</td>
                                            </tr>
                                            <tr ng-if="selectedRow.status != 10">
                                                <td>Interest Earnings @ {{selectedRow.prematureWithdrawalInterestRate}}%</td>
                                                <td>{{formData.totalInterest | currency:'\u20AC'}}</td>
                                            </tr>
                                            <tr>
                                                <td>Accrued Interest Earnings</td>
                                                <td>{{formData.accruedInterest | currency:'\u20AC'}}</td>
                                            </tr>
                                            <tr>
                                                <td>Withdrawal Fee</td>
                                                <td>{{selectedRow.withdrawalFee | currency:'\u20AC'}}</td>
                                            </tr>
                                            <tr>
                                                <td>Withdrawal Amount (deposit + accrued interest - fee)</td>
                                                <td>{{formData.depositPlusInterestAmount | currency:'\u20AC'}}</td>
                                            </tr>
                                        </tbody>
                                    </table>

                                    <div class="alert alert-info" style="color: #000000;" ng-if="selectedRow.status != 10">
                                        If a withdrawal request is submitted within {{selectedRow.depositProductJson.prematureWithdrawalMinDays}} days of the deposit start date you
                                        will not receive interest on your deposit.<br>If a withdrawal request is submitted before the maturity date, a lower interest rate of
                                        {{selectedRow.prematureWithdrawalInterestRate}}% is applied for the entire period between the start date and the withdrawal date.
                                    </div>

                                    <div class="alert alert-info" style="color: #000000;">Once the withdrawal request has been approved, the amount of
                                        {{formData.depositPlusInterestAmount | currency:'\u20AC'}} will be transferred to your bank account. Please make sure your bank account
                                        details are up to date.</div>

                                    <div class="form-group col-md-12 no-padding">
                                        <label class=""><input type="checkbox" name="agreeTerms" ng-model="formData.agreeTerms" required> I agree with the
                                            information above and would like to submit a request for withdrawal</label>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <div class="animate-if" ng-if="requestDepositWithdrawalErrorResponse !== ''">
                                            <div class="alert alert-danger" role="alert">
                                                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> <span>{{requestDepositWithdrawalErrorResponse}}</span>
                                            </div>
                                        </div>
                                    </div>

                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer text-alight-center">
                        <button ng-click="requestDepositWithdrawal();" class="btn btn-success outline-none width-modal-btn" ng-disabled="requestDepositWithdrawalButtonDisabled">
                            <span class="glyphicon glyphicon-ok"></span> Submit
                        </button>
                        <button type="button" class="btn btn-danger outline-none width-modal-btn" ng-click="closeRequestDepositWithdrawalModal()">
                            <b><span class="glyphicon glyphicon-remove"></span> Cancel</b>
                        </button>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>

    <%@ include file="spinner.html"%>

</div>
