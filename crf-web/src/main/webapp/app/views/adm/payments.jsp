<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-fluid no-padding">
    <%@ include file="../spinner.html"%>

    <div class="d-table w-100" style="margin-bottom: 5px;">
        <nav aria-label="breadcrumb" class="d-table-cell">
            <ol class="breadcrumb d-table-cell w-100">
                <li class="breadcrumb-item active">Deposits</li>
            </ol>
        </nav>
        <div class="d-table-cell tar">
            <button ng-click="refreshTableData();" class="btn btn-primary btn-sm" ng-disabled="disableControls">
                <span class="glyphicon glyphicon-refresh"></span> Refresh
            </button>

        </div>
    </div>

    <table class="tc-table">

        <thead>
            <tr>
                <td>Customer Name</td>
                <td>Deposit Account Number</td>
                <td>Interest Payment</td>
                <td>Interest Amount</td>
                <td>Status</td>
                <td>Interest Period</td>
                <td>Processed Date</td>
                <td>Created Date</td>
                <td>Process</td>
            </tr>
        </thead>

        <colgroup>
            <col width="10%">
            <col width="10%">
            <col width="2%">
            <col width="2%">
            <col width="2%">
            <col width="10%">
            <col width="5%">
            <col width="5%">
            <col width="10%">
        </colgroup>

        <tbody ng-repeat="row in rows = (tcTable.data | orderBy:sortType:sortReverse)">
            <tr ng-click="tcTable.selectRow($event)">
                <td title="Customer Name">{{row.customerName}}</td>
                <td title="Account Number">{{row.accountNumber}}</td>
                <td class="text-center" title="Interest Payment"><span class="label label-primary" ng-if="row.interestPayoutFrequency == 1">QUARTERLY</span> <span
                    class="label label-primary" ng-if="row.interestPayoutFrequency == 2">YEARLY</span> <span class="label label-primary" ng-if="row.interestPayoutFrequency == 3">HALF-YEARLY</span></td>
                <td title="Interest Amount">{{row.interestPaymentAmount}}</td>
                <td class="text-center" title="Status"><span class="label label-success" ng-if="row.isProcessed">PROCESSED</span> <span class="label label-primary"
                    ng-if="!row.isProcessed">PENDING</span></td>
                <td title="Interest Period">{{row.interestPeriodString}}</td>
                <td title="Processed Date">{{row.dateProcessedString}}</td>
                <td title="Created Date">{{row.dateCreatedString}}</td>
                <td class="text-center" title="Process">
                    <button class="btn btn-success btn-intable" ng-click="showProcessDepositInterestPaymentModal(row); $event.stopPropagation();" ng-if="!row.isProcessed"
                        title="Process Interest Payment">
                        <span class="glyphicon glyphicon-ok"></span>
                    </button> <span ng-if="row.isProcessed" style="color: green;"><span class="glyphicon glyphicon-ok"></span> Processed</span>
                </td>
            </tr>
        </tbody>

        <tr ng-show="tcTable.count == 0">
            <td colspan="9" class="tc-table-extension-cell text-center">No payments returned</td>
        </tr>

        <!-- Telclic Table Pagination Component -->
        <tr>
            <td colspan="9" class="tc-table-extension-cell text-left"><tc-table-pagination></tc-table-pagination></td>
        </tr>
    </table>

    <!-- Process Deposit Interest Payment Modal -->
    <div class="modal fade" id="processDepositInterestPaymentModal" tabindex="-1" role="dialog" aria-labelledby="processDepositInterestPaymentModalLabel"
        ng-form="processDepositInterestPaymentForm">
        <fieldset>
            <div class="modal-dialog modal-dialog-record modal-lg" role="document">
                <div class="modal-content medium-font-size">
                    <div class="modal-header">
                        <button type="button" class="close" ng-click="closeDepositInterestPaymentModal()" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title" id="processDepositInterestPaymentModalLabel">Process Deposit Interest Payment</h4>

                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-12">

                                <form name="processDepositInterestPaymentForm" autocomplete="off" novalidate>

                                    <table class="table table-bordered" style="background-color: #f5f5f5;">
                                        <thead>
                                            <tr>
                                                <th>Customer Name</th>
                                                <th>Deposit Account Number</th>
                                                <th>Interest Amount</th>
                                                <th>Date</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>{{selectedRow.customerName}}</td>
                                                <td>{{selectedRow.accountNumber}}</td>
                                                <td>{{selectedRow.interestPaymentAmount | currency:'\u20AC'}}</td>
                                                <td>{{selectedRow.dateCreatedString}}</td>
                                            </tr>
                                        </tbody>
                                    </table>

                                    <div class="alert alert-info" style="color: #000000;">
                                        Please verify the bank account details below. If they appear correct, proceed with transferring <b>{{selectedRow.interestPaymentAmount |
                                            currency:'\u20AC'}}</b> to the bank account below.
                                    </div>

                                    <div class="form-group col-sm-12 bg-info margin-top" ng-if="selectedRow.bankDetailsJson !== null">
                                        <div class="row">
                                            <div class="col-sm-3">
                                                <label>Account</label>
                                            </div>
                                            <div class="col-sm-9">{{selectedRow.bankDetailsJson.bankAccountName}}</div>
                                        </div>
                                        <div class="row">
                                            <div class="col-sm-3">
                                                <label>IBAN</label>
                                            </div>
                                            <div class="col-sm-9">{{selectedRow.bankDetailsJson.iban}}</div>
                                        </div>
                                        <div class="row">
                                            <div class="col-sm-3">
                                                <label>BIC</label>
                                            </div>
                                            <div class="col-sm-9">{{selectedRow.bankDetailsJson.bic}}</div>
                                        </div>
                                        <div class="row">
                                            <div class="col-sm-3">
                                                <label>Bank name</label>
                                            </div>
                                            <div class="col-sm-9">{{selectedRow.bankDetailsJson.name}}</div>
                                        </div>
                                        <div class="row">
                                            <div class="col-sm-3">
                                                <label>Bank address</label>
                                            </div>
                                            <div class="col-sm-9">{{selectedRow.bankDetailsJson.address}}</div>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <label class=""><input type="checkbox" name="agreeTerms" ng-model="formData.agreeTerms" required> I confirm that I have
                                            processed the transfer to the bank account shown above.</label>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <div class="animate-if" ng-if="processDepositInterestPaymentErrorResponse !== ''">
                                            <div class="alert alert-danger" role="alert">
                                                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> <span>{{processDepositInterestPaymentErrorResponse}}</span>
                                            </div>
                                        </div>
                                    </div>

                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer text-alight-center">
                        <button ng-click="processDepositInterestPayment();" class="btn btn-success outline-none width-modal-btn"
                            ng-disabled="processDepositInterestPaymentButtonDisabled">
                            <span class="glyphicon glyphicon-ok"></span> Submit
                        </button>
                        <button type="button" class="btn btn-danger outline-none width-modal-btn" ng-click="closeDepositInterestPaymentModal()">
                            <b><span class="glyphicon glyphicon-remove"></span> Cancel</b>
                        </button>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>

</div>