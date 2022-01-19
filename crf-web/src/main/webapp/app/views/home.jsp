<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item active">Home</li>
    </ol>
</nav>
<div class="container-fluid">

    <div class="no-padding" ng-if="(isInvestorOperator == 'true' || isBorrowerOperator == 'true') && isCustomerAccountVerified == 'false'">

        <div class="alert alert-info medium-font-size" style="text-align: center;" role="alert">Your account is not yet verified. You will be notified via email as soon as
            your account gets verified.</div>
    </div>

    <div class="no-padding" ng-if="(isInvestorOperator == 'true' || isBorrowerOperator == 'true')">

        <div class="alert alert-warning medium-font-size" style="text-align: center;" role="alert"
            ng-if="customerDetails !== undefined && customerDetails.bankAccount.iban === null">
            Please add your bank account details required to facilitate money transfers.<br>You can update your details at any time using the Bank Account menu.<br> Once
            your account is verified and the bank details have been provided you will be able to select one or more deposit products below.
        </div>

        <div class="col-lg-8 col-lg-offset-2" style="padding: 0px; margin-bottom: 15px;">

            <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;">
                <div class="well well-dashboard">
                    <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                        <span class="glyphicon glyphicon-equalizer" style="font-size: 40px; color: indianred; line-height: inherit;"></span>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                        <span class="medium-font-size">Type</span><br> <span class="label label-primary" ng-if="isInvestorOperator == 'true'">INVESTOR</span> <span
                            class="label label-info" ng-if="isBorrowerOperator == 'true'">BORROWER</span>
                    </div>
                </div>
            </div>

            <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;">
                <div class="well well-dashboard">
                    <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                        <span class="glyphicon glyphicon-file" style="font-size: 40px; color: burlywood; line-height: inherit;"></span>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                        <span class="medium-font-size">Category</span><br> <span class="label label-primary" ng-if="customerCategory == 1">INDIVIDUAL</span> <span
                            class="label label-primary" ng-if="customerCategory == 2">COMPANY</span>
                    </div>
                </div>
            </div>

            <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;">
                <div class="well well-dashboard">
                    <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                        <span class="glyphicon glyphicon-user" style="font-size: 40px; color: lightskyblue; line-height: inherit;"></span>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                        <span class="medium-font-size">Status</span> <br> <span class="label label-primary" ng-if="isCustomerAccountVerified == 'false'">PENDING
                            VERIFICATION</span> <span class="label label-success" ng-if="isCustomerAccountVerified == 'true'">VERIFIED</span>
                    </div>
                </div>
            </div>

            <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;" ng-if="isInvestorOperator == 'true' && isCustomerAccountVerified == 'true'">
                <div class="well well-dashboard">
                    <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                        <span class="glyphicon glyphicon-list-alt" style="font-size: 40px; color: gray; line-height: inherit;"></span>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                        <span class="medium-font-size">Active Deposits</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                            title="This is the number of active deposits that earn interest over time"> <span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"></span>
                        </a><br> <span class="larger-font-size" style="font-weight: bold;">{{investorStatsObject.activeDepositCount}}</span>
                    </div>
                </div>
            </div>

            <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;" ng-if="isInvestorOperator == 'true' && isCustomerAccountVerified == 'true'">
                <div class="well well-dashboard">
                    <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                        <span class="glyphicon glyphicon-eur" style="font-size: 40px; color: gray; line-height: inherit;"></span>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                        <span class="medium-font-size">Total Deposit</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                            title="This is the sum of all active deposits"> <span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"></span>
                        </a><br> <span class="larger-font-size" style="font-weight: bold;">{{investorStatsObject.totalActiveDepositAmountString | currency:'\u20AC'}}</span>
                    </div>
                </div>
            </div>

            <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;" ng-if="isInvestorOperator == 'true' && isCustomerAccountVerified == 'true'">
                <div class="well well-dashboard">
                    <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                        <span class="glyphicon glyphicon-eur" style="font-size: 40px; color: gray; line-height: inherit;"></span>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                        <span class="medium-font-size">Accrued Interest</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                            title="The amount of interest accumulated from your active deposits for the current quarter or year"> <span class="glyphicon glyphicon-info-sign"
                            data-toggle="tooltip"></span>
                        </a><br> <span class="larger-font-size" style="font-weight: bold;">{{investorStatsObject.totalAccruedInterestAmountString | currency:'\u20AC'}}</span>
                    </div>
                </div>
            </div>

            <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;"
                ng-if="isInvestorOperator == 'true' && isCustomerAccountVerified == 'true' && investorStatsObject.initiatedDepositCount > 0">
                <div class="well well-dashboard">
                    <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                        <span class="glyphicon glyphicon-list-alt" style="font-size: 40px; color: gray; line-height: inherit;"></span>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                        <span class="medium-font-size">Initiated Deposits</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                            title="This is the number of deposits that have been intitiated but not approved. A deposit is approved when it is confirmed the funds have been transferred">
                            <span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"></span>
                        </a><br> <span class="larger-font-size" style="font-weight: bold;">{{investorStatsObject.initiatedDepositCount}}</span>
                    </div>
                </div>
            </div>

            <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;" ng-if="isInvestorOperator == 'true' && isCustomerAccountVerified == 'true'">
                <div class="well well-dashboard">
                    <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                        <span class="glyphicon glyphicon-eur" style="font-size: 40px; color: gray; line-height: inherit;"></span>
                    </div>
                    <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                        <span class="medium-font-size">Total Interest</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                            title="The all time total amount of interest earned from your deposits"> <span class="glyphicon glyphicon-info-sign"
                            data-toggle="tooltip"></span>
                        </a><br> <span class="larger-font-size" style="font-weight: bold;">{{investorStatsObject.totalInterestEarnedAmountString | currency:'\u20AC'}}</span>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <div class="no-padding" ng-show="isInvestorOperator == 'true' && isCustomerAccountVerified == 'true'">
        <div class="medium-font-size no-padding" ng-repeat="depositProduct in depositProductList">
            <table class="table table-bordered" style="background-color: #f5f5f5;">
                <tr>
                    <td><b>{{depositProduct.name}}</b></td>
                    <td><b>{{depositProduct.description}}</b> <br>Minimum deposit amount: {{depositProduct.depositMinAmount | currency:'\u20AC'}} <br>Maximum deposit
                        amount: {{depositProduct.depositMaxAmount | currency:'\u20AC'}}</td>
                    <td><b>{{depositProduct.quarterlyInterestRate}}%</b> Quarterly Interest Rate <br> <b>{{depositProduct.yearlyInterestRate}}%</b> Yearly Interest Rate <br>
                        Term: {{depositProduct.termYears}} year(s)</td>
                    <td>
                        <button type="button" class="btn btn-md btn-block"
                            style="background: inherit; color: #5cb85c; border: none; font-size: 15px; margin-top: 15px; font-weight: bold;"
                            ng-click="showCreateDepositModal(depositProduct)" ng-disabled="customerDetails !== undefined && customerDetails.bankAccount.iban === null">SELECT</button>
                    </td>
                </tr>
            </table>
        </div>

        <div class="modal fade" id="createDepositModal" tabindex="-1" role="dialog" aria-labelledby="createDepositModalLabel" ng-form="depositCreateForm">
            <fieldset>
                <div class="modal-dialog modal-dialog-record modal-lg" role="document">
                    <div class="modal-content medium-font-size">
                        <div class="modal-header">
                            <button type="button" class="close" ng-click="closeCreateDepositModal()" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title" id="createDepositModalLabel">{{selectedDepositProduct.name}}</h4>

                        </div>
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-sm-12">

                                    <form name="depositCreateForm" autocomplete="off" novalidate>

                                        <table class="table table-bordered" style="background-color: #f5f5f5;">
                                            <thead>
                                                <tr>
                                                    <th>Minimum deposit amount</th>
                                                    <th>Maximum deposit amount</th>
                                                    <th>Interest rate <span ng-if="formData.interestPayoutFrequency == 1">per quarter</span><span
                                                        ng-if="formData.interestPayoutFrequency == 2">per year</span> <span ng-if="formData.interestPayoutFrequency == 3">per
                                                            6 months</span></th>
                                                    <th>Term (years)</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <tr>
                                                    <td>{{selectedDepositProduct.depositMinAmount | currency:'\u20AC'}}</td>
                                                    <td>{{selectedDepositProduct.depositMaxAmount | currency:'\u20AC'}}</td>
                                                    <td><span ng-if="formData.interestPayoutFrequency == 1">{{selectedDepositProduct.quarterlyInterestRate}}%</span><span
                                                        ng-if="formData.interestPayoutFrequency == 2">{{selectedDepositProduct.yearlyInterestRate}}%</span> <span
                                                        ng-if="formData.interestPayoutFrequency == 3">{{selectedDepositProduct.twiceYearlyInterestRate}}%</span></td>
                                                    <td>{{selectedDepositProduct.termYears}}</td>
                                                </tr>
                                            </tbody>
                                        </table>

                                        <div class="alert alert-info" style="color: #000000;">Please enter your deposit amount below and confirm to find out the potential
                                            interest gains at the end of the selected term</div>

                                        <div class="form-group col-md-12 no-padding">
                                            <label for="interestPayoutFrequency">Interest Payout Frequency</label> <select class="form-control" name="interestPayoutFrequency"
                                                ng-model="formData.interestPayoutFrequency" ng-disabled="isConfirmingProductInterest" required>
                                                <option value="1">Quarterly @ {{selectedDepositProduct.quarterlyInterestRate}}%</option>
                                                <option value="2">Yearly @ {{selectedDepositProduct.yearlyInterestRate}}%</option>
                                                <option value="3">Half-Yearly @ {{selectedDepositProduct.twiceYearlyInterestRate}}%</option>
                                            </select>
                                        </div>

                                        <div class="form-group col-md-6 no-padding">
                                            <label for="depositAmount">Deposit Amount (euro)</label> <input type="text" class="form-control" name="depositAmount"
                                                ng-model="formData.depositAmount" format-amount="currency" ng-maxlength="13" maxlength="13"
                                                ng-disabled="isConfirmingProductInterest" required>
                                        </div>
                                        <div class="form-group col-md-6 no-padding">
                                            <label for="depositAmountConfirm">Confirm Deposit Amount (euro)</label> <input type="text" class="form-control"
                                                name="depositAmountConfirm" ng-model="formData.depositAmountConfirm" format-amount="currency" ng-maxlength="13" maxlength="13"
                                                ng-disabled="isConfirmingProductInterest" required>
                                        </div>
                                        <div class="form-group col-md-12 no-padding">
                                            <div class="animate-if" ng-if="createDepositErrorResponse !== ''">
                                                <div class="alert alert-danger" role="alert">
                                                    <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> <span>{{createDepositErrorResponse}}</span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="form-group col-md-12 no-padding" ng-hide="isConfirmingProductInterest">
                                            <button type="button" class="btn btn-primary btn-block" ng-disabled="isConfirmingProductInterest" ng-click="confirmDepositAmount()">Confirm</button>
                                        </div>
                                        <div class="form-group col-md-12 no-padding" ng-show="isConfirmingProductInterest">
                                            <button type="button" class="btn btn-primary btn-block" ng-click="changeDepositAmount()">Change Deposit Amount</button>
                                        </div>

                                        <div class="form-group col-md-12 no-padding" ng-if="createDepositInfoMessage !== ''">
                                            <div class="alert alert-info" style="color: #000000; margin-bottom: 0px;">
                                                <span ng-bind-html="createDepositInfoMessage | to_trusted_html"></span>
                                            </div>
                                        </div>

                                        <div class="form-group col-sm-12 bg-info margin-top" ng-if="createDepositInfoMessage !== ''">
                                            <div class="row">
                                                <div class="col-sm-3">
                                                    <label>Transfer Reference</label>
                                                </div>
                                                <div class="col-sm-9">{{bankTransferRef}}</div>
                                            </div>
                                            <div class="row">
                                                <div class="col-sm-3">
                                                    <label>Account</label>
                                                </div>
                                                <div class="col-sm-9">{{bankDetailsObject.bankAccountName}}</div>
                                            </div>
                                            <div class="row">
                                                <div class="col-sm-3">
                                                    <label>IBAN</label>
                                                </div>
                                                <div class="col-sm-9">{{bankDetailsObject.iban}}</div>
                                            </div>
                                            <div class="row">
                                                <div class="col-sm-3">
                                                    <label>BIC</label>
                                                </div>
                                                <div class="col-sm-9">{{bankDetailsObject.bic}}</div>
                                            </div>
                                            <div class="row">
                                                <div class="col-sm-3">
                                                    <label>Bank name</label>
                                                </div>
                                                <div class="col-sm-9">{{bankDetailsObject.name}}</div>
                                            </div>
                                            <div class="row">
                                                <div class="col-sm-3">
                                                    <label>Bank address</label>
                                                </div>
                                                <div class="col-sm-9">{{bankDetailsObject.address}}</div>
                                            </div>
                                        </div>

                                    </form>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer text-alight-center">
                            <button ng-click="createDeposit();" class="btn btn-success outline-none width-modal-btn" ng-disabled="createDepositButtonDisabled">
                                <span class="glyphicon glyphicon-ok"></span> Submit
                            </button>
                            <button type="button" class="btn btn-danger outline-none width-modal-btn" ng-click="closeCreateDepositModal()">
                                <b><span class="glyphicon glyphicon-remove"></span> Cancel</b>
                            </button>
                        </div>
                    </div>
                </div>
            </fieldset>
        </div>
    </div>

    <!-- ADMIN PAGE START -->
    <div class="col-md-4" ng-show="isAdmin == 'true'">
        <form name="sendreglinkFormData" id="sendreglinkFormData" autocomplete="off" novalidate>
            <h4>Send Customer Registration Link</h4>

            <div class="form-group">
                <label for="title">Title</label> <select class="form-control" name="title" ng-model="formData.title" required>
                    <option value="Mr">Mr</option>
                    <option value="Mrs">Mrs</option>
                    <option value="Miss">Miss</option>
                    <option value="Ms">Ms</option>
                    <option value="Sir">Sir</option>
                    <option value="Dr">Dr</option>
                </select>
            </div>

            <div class="form-group">
                <label for="firstName">First Name</label> <input type="text" class="form-control" name="firstName" ng-model="formData.firstName" ng-maxlength="32" maxlength="32"
                    ng-trim="true" avoid-special-chars avoid-numbers required>
            </div>

            <div class="form-group">
                <label for="lastName">Last Name</label> <input type="text" class="form-control" name="lastName" ng-model="formData.lastName" ng-maxlength="32" maxlength="32"
                    ng-trim="true" avoid-special-chars avoid-numbers required>
            </div>

            <div class="form-group">
                <label for="email">Email</label> <input type="text" class="form-control" name="email" ng-model="formData.email" ng-pattern="emailRegexp" ng-maxlength="64"
                    maxlength="64" required>
            </div>

            <div class="form-group">
                <label for="customerType">Customer Type</label> <select class="form-control" name="customerType" ng-model="formData.customerType" required>
                    <option value="1">Investor</option>
                    <!-- <option value="2">Borrower</option> -->
                    <!-- <option value="3">Investor & Borrower</option> -->
                </select>
            </div>

            <div class="form-group">
                <label for="customerCategory">Customer Category</label> <select class="form-control" name="customerCategory" ng-model="formData.customerCategory" required>
                    <option value="1">Individual</option>
                    <!-- <option value="2">Company</option> -->
                </select>
            </div>

            <div class="form-group">
                <div class="animate-if" ng-if="sendRegLinkErrorMessage !== ''">
                    <div class="alert alert-danger" role="alert">
                        <span class="glyphicon glyphicon-remove"></span><span class="sr-only">Error: </span> <span>{{sendRegLinkErrorMessage}}</span>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" ng-disabled="isSendingRegLink" ng-click="sendRegLink()">Submit</button>
            </div>
        </form>
    </div>

    <div class="col-md-8" style="margin-bottom: 15px;">

        <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;" ng-if="isAdmin == 'true'">
            <div class="well well-dashboard">
                <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                    <span class="glyphicon glyphicon-user" style="font-size: 40px; color: lightskyblue; line-height: inherit;"></span>
                </div>
                <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                    <span class="medium-font-size">Investors</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                        title="This is the number of active investors registered on the platform"> <span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"></span>
                    </a><br> <span class="larger-font-size" style="font-weight: bold;">{{adminStatsObject.investorCount}}</span>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;" ng-if="isAdmin == 'true'">
            <div class="well well-dashboard">
                <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                    <span class="glyphicon glyphicon-list-alt" style="font-size: 40px; color: gray; line-height: inherit;"></span>
                </div>
                <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                    <span class="medium-font-size">Active Deposits</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                        title="This is the number of active deposits that earn interest over time"> <span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"></span>
                    </a><br> <span class="larger-font-size" style="font-weight: bold;">{{adminStatsObject.activeDepositCount}}</span>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;" ng-if="isAdmin == 'true'">
            <div class="well well-dashboard">
                <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                    <span class="glyphicon glyphicon-eur" style="font-size: 40px; color: gray; line-height: inherit;"></span>
                </div>
                <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                    <span class="medium-font-size">Invested Amount</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                        title="The total amount of money currently invested"> <span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"></span>
                    </a><br> <span class="larger-font-size" style="font-weight: bold;">{{adminStatsObject.totalActiveDepositAmountString | currency:'\u20AC'}}</span>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-sm-6 col-xs-6" style="padding: 5px;" ng-if="isAdmin == 'true'">
            <div class="well well-dashboard">
                <div class="col-md-2 col-sm-2 col-xs-2" style="padding: 0px;">
                    <span class="glyphicon glyphicon-eur" style="font-size: 40px; color: gray; line-height: inherit;"></span>
                </div>
                <div class="col-md-10 col-sm-10 col-xs-10" style="padding: 0px; padding-top: 10px; text-align: right;">
                    <span class="medium-font-size">Interest Amount</span> <a ng-href="" data-toggle="tooltip" tooltip-loader data-placement="bottom"
                        title="The total amount of interest paid out to investors"> <span class="glyphicon glyphicon-info-sign" data-toggle="tooltip"></span>
                    </a><br> <span class="larger-font-size" style="font-weight: bold;">{{adminStatsObject.totalInterestPaidAmountString | currency:'\u20AC'}}</span>
                </div>
            </div>
        </div>
    </div>
    <!-- ADMIN PAGE END -->

    <%@ include file="spinner.html"%>

</div>