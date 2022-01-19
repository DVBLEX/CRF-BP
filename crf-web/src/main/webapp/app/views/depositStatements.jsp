<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-fluid no-padding">

    <%@ include file="spinner.html"%>

    <div class="d-table w-100" style="margin-bottom: 5px;">
        <nav aria-label="breadcrumb" class="d-table-cell">
            <ol class="breadcrumb d-table-cell w-100">
                <li class="breadcrumb-item active">Deposit Statements</li>
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
                <td>Date</td>
                <td>Deposit Account Number</td>
                <td>Description</td>
                <td>Transaction Amount</td>
                <td>Balance Amount</td>
            </tr>
        </thead>

        <colgroup>
            <col width="10%">
            <col width="10%">
            <col width="20%">
            <col width="10%">
            <col width="10%">
        </colgroup>

        <tbody ng-repeat="row in rows = (tcTable.data | orderBy:sortType:sortReverse)">
            <tr ng-click="tcTable.selectRow($event)">
                <td title="Date">{{row.dateCreatedString}}</td>
                <td title="Deposit Account Number">{{row.accountNumber}}</td>
                <td title="Description">{{row.description}}</td>
                <td title="Transaction Amount">{{row.amountTransaction}}</td>
                <td title="Balance Amount">{{row.amountBalance}}</td>
            </tr>
        </tbody>

        <tr ng-show="tcTable.count == 0">
            <td colspan="5" class="tc-table-extension-cell text-center">No statements returned</td>
        </tr>

        <!-- Telclic Table Pagination Component -->
        <tr>
            <td colspan="5" class="tc-table-extension-cell text-left"><tc-table-pagination></tc-table-pagination></td>
        </tr>
    </table>

</div>