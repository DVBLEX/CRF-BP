<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-fluid no-padding">

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

            <button type="button" class="btn btn-primary btn-sm" name="filterButton" id="depositFilterButton" data-toggle="collapse" data-target="#depositSearchCollapse"
                aria-expanded="true" aria-controls="depositSearchCollapse" aria-pressed="false" ng-class="{ 'tcFilterIsActive': isFilterActive}" ng-disabled="disableControls">
                <span class="glyphicon glyphicon-filter"></span> Filter <span ng-show="isFilterActive">*</span>
            </button>
        </div>
    </div>

    <!-- Filters -->
    <div class="collapse in" id="depositSearchCollapse">
        <div class="div-filter">
            <form class="form-inline" name="depositFiltersForm">

                <div class="form-group">
                    <label for="filterStatus">Status </label> <select class="form-control input-sm" id="filterStatus" name="filterStatus" ng-model="filterStatus"
                        ng-init="filterStatus = filterStatusDefault" ng-disabled="filterStatusDisabled || filtersDisabled">
                        <option value="1">INITIATED</option>
                        <option value="5">INITIATED - EXPIRED</option>
                        <option value="2">ACTIVE</option>
                        <option value="3">WITHDRAWAL REQUESTED</option>
                        <option value="4">WITHDRAWN</option>
                        <option value="10">MATURED</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="filterBankTransferRef"> Bank Transfer Ref </label> <input type="text" class="form-control input-sm" name="filterBankTransferRef"
                        ng-model="filterBankTransferRef" ng-minlength="4" minlength="4" ng-maxlength="16" maxlength="16" alphanumeric capitalize ng-disabled="filtersDisabled" />
                </div>

                <div class="form-group">
                    <button ng-click="refreshTableData()" class="btn btn-primary btn-sm outline-none" ng-disabled="disableControls || filtersDisabled">
                        <span class="glyphicon glyphicon-search"></span> Search
                    </button>
                </div>

                <div class="form-group">
                    <button ng-click="clearFiltersAndRefresh()" class="btn btn-danger btn-sm outline-none" ng-disabled="disableControls || filtersDisabled">
                        <span class="glyphicon glyphicon-erase"></span> Clear
                    </button>
                </div>

            </form>
        </div>
    </div>

    <table class="tc-table">

        <thead>
            <tr>
                <td>Customer Name</td>
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
                <td>Approve</td>
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
                <td title="Customer Name">{{row.customerName}}</td>
                <td title="Account Number">{{row.accountNumber}}</td>
                <td title="Deposit Amount">{{row.depositAmount}}</td>
                <td title="Interest Rate">{{row.interestRate}}</td>
                <td title="Term (years)">{{row.termYears}}</td>
                <td class="text-center" title="Status"><span class="label label-primary" ng-if="row.status == 1">INITIATED</span> <span class="label label-success"
                    ng-if="row.status == 2">ACTIVE</span><span class="label label-primary" ng-if="row.status == 3">WITHDRAWAL REQUESTED</span> <span class="label label-primary"
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
                <td class="text-center" title="Approve">
                    <button class="btn btn-success btn-intable" ng-click="showApproveDepositModal(row); $event.stopPropagation();" ng-if="row.status == 1" title="Approve Deposit">
                        <span class="glyphicon glyphicon-ok"></span>
                    </button> <span ng-if="row.status == 2" style="color: green;"><span class="glyphicon glyphicon-ok"></span> Approved</span>
                    <button class="btn btn-success btn-intable" ng-click="showApproveDepositWithdrawalModal(row); $event.stopPropagation();" ng-if="row.status == 3"
                        title="Approve Deposit Withdrawal Request">
                        <span class="glyphicon glyphicon-ok"></span>
                    </button> <span ng-if="row.status == 4" style="color: green;"><span class="glyphicon glyphicon-ok"></span> Deposit Withdrawn</span>
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

    <!-- Approve Deposit Modal -->
    <div class="modal fade" id="approveDepositModal" tabindex="-1" role="dialog" aria-labelledby="approveDepositModalLabel" ng-form="depositCreateForm">
        <fieldset>
            <div class="modal-dialog modal-dialog-record modal-lg" role="document">
                <div class="modal-content medium-font-size">
                    <div class="modal-header">
                        <button type="button" class="close" ng-click="closeApproveDepositModal()" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title" id="approveDepositModalLabel">Approve Deposit</h4>

                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-12">

                                <form name="depositApproveForm" autocomplete="off" novalidate>

                                    <table class="table table-bordered" style="background-color: #f5f5f5;">
                                        <thead>
                                            <tr>
                                                <th>Customer Name</th>
                                                <th>Account Number</th>
                                                <th>Required Deposit Amount</th>
                                                <th>Bank Transfer Ref</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>{{selectedRow.customerName}}</td>
                                                <td>{{selectedRow.accountNumber}}</td>
                                                <td>{{selectedRow.depositAmount | currency:'\u20AC'}}</td>
                                                <td>{{selectedRow.bankTransferReference}}</td>
                                            </tr>
                                        </tbody>
                                    </table>

                                    <div class="alert alert-info" style="color: #000000;">Please confirm the bank transfer reference, the exact amount received under that
                                        reference and the date of transfer as it appears on the bank statement</div>

                                    <div class="form-group col-md-12 no-padding">
                                        <label for="bankTransferRefConfirm">Confirm Bank Transfer Ref </label> <input type="text" class="form-control" name="bankTransferRefConfirm"
                                            ng-model="formData.bankTransferRefConfirm" ng-minlength="4" minlength="4" ng-maxlength="16" maxlength="16" alphanumeric capitalize
                                            required>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <label for="depositAmountConfirm">Confirm Deposit Amount (euro)</label> <input type="text" class="form-control" name="depositAmountConfirm"
                                            ng-model="formData.depositAmountConfirm" format-amount="currency" ng-maxlength="13" maxlength="13" required>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <label for="dateStartString">Date Transfer</label> <input type="date" class="form-control" id="dateStartString" name="dateStartString"
                                            ng-model="formData.dateStartString" placeholder="dd/MM/yyyy" required>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <label class=""><input type="checkbox" name="agreeTerms" ng-model="formData.agreeTerms" required> I confirm that the
                                            information entered above is correct and it represents the information shown on the bank statement</label>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <div class="animate-if" ng-if="approveDepositErrorResponse !== ''">
                                            <div class="alert alert-danger" role="alert">
                                                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> <span>{{approveDepositErrorResponse}}</span>
                                            </div>
                                        </div>
                                    </div>

                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer text-alight-center">
                        <button ng-click="approveDeposit();" class="btn btn-success outline-none width-modal-btn" ng-disabled="approveDepositButtonDisabled">
                            <span class="glyphicon glyphicon-ok"></span> Submit
                        </button>
                        <button type="button" class="btn btn-danger outline-none width-modal-btn" ng-click="closeApproveDepositModal()">
                            <b><span class="glyphicon glyphicon-remove"></span> Cancel</b>
                        </button>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>

    <!-- Approve Deposit Withdrawal Modal -->
    <div class="modal fade" id="approveDepositWithdrawalModal" tabindex="-1" role="dialog" aria-labelledby="approveDepositWithdrawalModalLabel"
        ng-form="depositApproveWithdrawalForm">
        <fieldset>
            <div class="modal-dialog modal-dialog-record modal-lg" role="document">
                <div class="modal-content medium-font-size">
                    <div class="modal-header">
                        <button type="button" class="close" ng-click="closeApproveDepositWithdrawalModal()" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title" id="approveDepositWithdrawalModalLabel">Approve Deposit Withdrawal</h4>

                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-sm-12">

                                <form name="depositApproveWithdrawalForm" autocomplete="off" novalidate>

                                    <table class="table table-bordered" style="background-color: #f5f5f5;">
                                        <thead>
                                            <tr>
                                                <th>Customer Name</th>
                                                <th>Account Number</th>
                                                <th>Interest Rate</th>
                                                <th>Term (years)</th>
                                                <th>Maturity Date</th>
                                                <th>Deposit Amount</th>
                                                <th>Withdrawal Amount</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>{{selectedRow.customerName}}</td>
                                                <td>{{selectedRow.accountNumber}}</td>
                                                <td>{{selectedRow.interestRate}}%</td>
                                                <td>{{selectedRow.termYears}}</td>
                                                <td>{{selectedRow.dateMaturityString}}</td>
                                                <td>{{selectedRow.depositAmount | currency:'\u20AC'}}</td>
                                                <td>{{selectedRow.depositWithdrawalAmount | currency:'\u20AC'}}</td>
                                            </tr>
                                        </tbody>
                                    </table>

                                    <div class="alert alert-info" style="color: #000000;">
                                        Please verify the bank account details below. If they appear correct, proceed with transferring <b>{{selectedRow.depositWithdrawalAmount
                                            | currency:'\u20AC'}}</b> to the bank account below.
                                    </div>

                                    <div class="alert alert-danger" style="color: #000000;"
                                        ng-if="selectedRow.bankDetailsJson !== null && selectedRow.bankDetailsJson.daysSinceLastUpdate <= 30">The bank account was last
                                        updated {{selectedRow.bankDetailsJson.daysSinceLastUpdate}} days ago. For added security, before proceeding with the transfer, please
                                        contact the investor to confirm they intend to withdraw the deposit.</div>

                                    <div class="alert alert-warning" style="color: #000000;"
                                        ng-if="selectedRow.bankDetailsJson !== null && selectedRow.bankDetailsJson.daysSinceLastUpdate > 30">The bank account was last updated
                                        {{selectedRow.bankDetailsJson.daysSinceLastUpdate}} days ago.</div>

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
                                        <label class=""><input type="checkbox" name="agreeTerms" ng-model="formData.agreeTerms" required> I approve this withrawal
                                            request and I have processed the transfer to the bank account shown above.</label>
                                    </div>

                                    <div class="form-group col-md-12 no-padding">
                                        <div class="animate-if" ng-if="approveDepositWithdrawalErrorResponse !== ''">
                                            <div class="alert alert-danger" role="alert">
                                                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> <span>{{approveDepositWithdrawalErrorResponse}}</span>
                                            </div>
                                        </div>
                                    </div>

                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer text-alight-center">
                        <button ng-click="approveDepositWithdrawal();" class="btn btn-success outline-none width-modal-btn" ng-disabled="approveDepositWithdrawalButtonDisabled">
                            <span class="glyphicon glyphicon-ok"></span> Submit
                        </button>
                        <button type="button" class="btn btn-danger outline-none width-modal-btn" ng-click="closeApproveDepositWithdrawalModal()">
                            <b><span class="glyphicon glyphicon-remove"></span> Cancel</b>
                        </button>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>

    <%@ include file="../spinner.html"%>

</div>
