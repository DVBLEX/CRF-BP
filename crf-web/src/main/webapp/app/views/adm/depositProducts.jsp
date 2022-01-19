<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-fluid no-padding" ng-cloak>

    <div class="popup-wrapper" ng-show="isPopupShowed === true">
        <div class="popup-body">
            <div class="popup-content">
                <div class="popup-title">
                    {{ popupTitle }}
                </div>

                <hr>

                <form name="editProductFormData" id="editProductFormData" autocomplete="off" novalidate>
                    <div class="popup-text">
                        <div class="form-group">
                            <label for="name">Name</label>
                            <input type="text" class="form-control" id="name" name="name"
                                   ng-model="formData.name" ng-maxlength="32" maxlength="32"
                                   ng-trim="true" required>
                        </div>

                        <div class="form-group">
                            <label for="depositMinAmount">Minimum deposit amount</label>
                            <input type="text" class="form-control" id="depositMinAmount" name="depositMinAmount"
                                   ng-model="formData.depositMinAmount" format-amount="currency" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" required>
                        </div>

                        <div class="form-group">
                            <label for="depositMaxAmount">Maximum deposit amount</label>
                            <input type="text" class="form-control" id="depositMaxAmount" name="depositMaxAmount"
                                   ng-model="formData.depositMaxAmount" format-amount="currency" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" required>
                        </div>

                        <div class="form-group">
                            <label for="quarterlyInterestRate">Quarterly Interest Rate (max 10.00)</label>
                            <input type="text" class="form-control" id="quarterlyInterestRate" name="quarterlyInterestRate"
                                   ng-model="formData.quarterlyInterestRate" format-amount="currency" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" required>
                        </div>

                        <div class="form-group">
                            <label for="yearlyInterestRate">Yearly Interest Rate  (max 10.00)</label>
                            <input type="text" class="form-control" id="yearlyInterestRate" name="yearlyInterestRate"
                                   ng-model="formData.yearlyInterestRate" format-amount="currency" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" required>
                        </div>

                        <div class="form-group">
                            <label for="twiceYearlyInterestRate">Twice-Yearly Interest Rate (max 10.00)</label>
                            <input type="text" class="form-control" id="twiceYearlyInterestRate" name="twiceYearlyInterestRate"
                                   ng-model="formData.twiceYearlyInterestRate" format-amount="currency" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" required>
                        </div>

                        <div class="form-group">
                            <label for="termYears">Term (years)</label>
                            <input type="text" class="form-control" id="termYears" name="termYears"
                                   ng-model="formData.termYears" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" ng-pattern="numberRegexp" required>
                        </div>

                        <div class="form-group">
                            <label for="prematureWithdrawalMinDays">Premature withdrawal min days (max 80)</label>
                            <input type="text" class="form-control" id="prematureWithdrawalMinDays" name="prematureWithdrawalMinDays"
                                   ng-model="formData.prematureWithdrawalMinDays" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" ng-pattern="numberRegexp" required>
                        </div>

                        <div class="form-group">
                            <label for="prematureWithdrawalInterestRate">Premature withdrawal interest rate (max 10.00)</label>
                            <input type="text" class="form-control" id="prematureWithdrawalInterestRate" name="prematureWithdrawalInterestRate"
                                   ng-model="formData.prematureWithdrawalInterestRate" format-amount="currency" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" required>
                        </div>

                        <div class="form-group">
                            <label for="withdrawalFee">Withdrawal fee (max 1000.00)</label>
                            <input type="text" class="form-control" id="withdrawalFee" name="withdrawalFee"
                                   ng-model="formData.withdrawalFee" format-amount="currency" ng-maxlength="13" maxlength="13"
                                   ng-trim="true" required>
                        </div>

                        <div class="form-group">
                            <label for="description">Description</label>
                            <textarea class=" form-control popup-textarea" id="description" name="description"
                                      ng-model="formData.description" ng-maxlength="256" maxlength="256"
                                      ng-trim="true" required>

                            </textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="animate-if" ng-if="editProductErrorMessage !== ''">
                            <div class="alert alert-danger" role="alert">
                                <span class="glyphicon glyphicon-remove"></span><span class="sr-only">Error: </span> <span>{{editProductErrorMessage}}</span>
                            </div>
                        </div>
                    </div>
                </form>

                <hr>

                <div class="popup-btn">
                    <button ng-click="showPopup(false)" ng-disabled="isEditing" class="btn btn-primary btn-sm btn-width-120px">
                        <span class="glyphicon glyphicon-arrow-left"></span> Back
                    </button>
                    <button ng-click="editProduct()" ng-disabled="isEditing" class="btn btn-success btn-sm btn-width-120px">
                        Submit
                    </button>
                </div>

            </div>
        </div>
    </div>

    <div class="d-table w-100" style="margin-bottom: 5px;">
        <nav aria-label="breadcrumb" class="d-table-cell">
            <ol class="breadcrumb d-table-cell w-100">
                <li class="breadcrumb-item active">Products</li>
            </ol>
        </nav>

        <div class="d-table-cell tar">

            <button ng-click="refreshTableData();" class="btn btn-primary btn-sm" ng-disabled="disableControls">
                <span class="glyphicon glyphicon-refresh"></span> Refresh
            </button>

        </div>
    </div>

    <div>

        <table class="tc-table">

            <thead>
            <tr>
                <td>
                    <a href="" ng-click="sortType = 'name'; sortReverse = !sortReverse;">
                        Name
                        <span ng-show="sortType == 'name' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'name' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'depositMinAmount'; sortReverse = !sortReverse;">
                        Minimum deposit amount
                        <span ng-show="sortType == 'depositMinAmount' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'depositMinAmount' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'depositMaxAmount'; sortReverse = !sortReverse;">
                        Maximum deposit amount
                        <span ng-show="sortType == 'depositMaxAmount' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'depositMaxAmount' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'quarterlyInterestRate'; sortReverse = !sortReverse;">
                        Quarterly Interest Rate
                        <span ng-show="sortType == 'quarterlyInterestRate' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'quarterlyInterestRate' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'yearlyInterestRate'; sortReverse = !sortReverse;">
                        Yearly Interest Rate
                        <span ng-show="sortType == 'yearlyInterestRate' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'yearlyInterestRate' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'twiceYearlyInterestRate'; sortReverse = !sortReverse;">
                        Twice-Yearly Interest Rate
                        <span ng-show="sortType == 'twiceYearlyInterestRate' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'twiceYearlyInterestRate' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'termYears'; sortReverse = !sortReverse;">
                        Term (years)
                        <span ng-show="sortType == 'termYears' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'termYears' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td></td>
            </tr>
            </thead>

            <colgroup>
                <col width="11%">
                <col width="20%">
                <col width="20%">
                <col width="15%">
                <col width="10%">
                <col width="10%">
                <col width="10%">
                <col width="4%">
            </colgroup>

            <tbody ng-repeat="row in rows = (tcTable.data | orderBy:sortType:sortReverse) track by $index">
            <tr ng-click="tcTable.selectRow($event)">
                <td title="Name">{{row.name}}</td>
                <td title="Minimum deposit amount">{{row.depositMinAmount}}</td>
                <td title="Maximum deposit amount">{{row.depositMaxAmount}}</td>
                <td title="Quarterly Interest Rate">{{row.quarterlyInterestRate}}</td>
                <td title="Yearly Interest Rate">{{row.yearlyInterestRate}}</td>
                <td title="Twice-Yearly Interest Rate">{{row.twiceYearlyInterestRate}}</td>
                <td title="Term (years)">{{row.termYears}}</td>
                <td class="text-center">
                    <button class="btn btn-primary btn-intable" ng-click="showEditPopup(row); $event.stopPropagation();" title="Edit User">
                        <span class="glyphicon glyphicon-edit"></span>
                    </button>
                </td>
            </tr>
            </tbody>

            <tr ng-show="tcTable.count == 0">
                <td colspan="8" class="tc-table-extension-cell text-center">No customers returned</td>
            </tr>

            <!-- Telclic Table Pagination Component -->
            <tr>
                <td colspan="8" class="tc-table-extension-cell text-left"><tc-table-pagination></tc-table-pagination></td>
            </tr>
        </table>

    </div>

    <%@ include file="../spinner.html"%>

</div>

