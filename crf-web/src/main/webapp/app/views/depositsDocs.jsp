<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-fluid no-padding">

    <%@ include file="spinner.html"%>

    <div class="d-table w-100" style="margin-bottom: 5px;">
        <nav aria-label="breadcrumb" class="d-table-cell">
            <ol class="breadcrumb d-table-cell w-100">
                <li class="breadcrumb-item active">Deposit Documents</li>
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
                <td>Deposit Account Number</td>
                <td>Document Type</td>
                <td>Created Date</td>
                <td></td>
            </tr>
        </thead>

        <colgroup>
            <col width="10%">
            <col width="10%">
            <col width="10%">
            <col width="2%">
        </colgroup>

        <tbody ng-repeat="row in rows = (tcTable.data | orderBy:sortType:sortReverse)">
            <tr ng-click="tcTable.selectRow($event)">
                <td title="Account Number">{{row.accountNumber}}</td>
                <td class="text-center" title="Type"><span class="label label-primary" ng-if="row.type == 1">DEPOSIT INITIATED</span> <span class="label label-primary"
                    ng-if="row.type == 2">DEPOSIT INTEREST PAYMENT</span></td>
                <td title="Created Date">{{row.dateCreatedString}}</td>
                <td title="Download" class="text-center">
                    <form target="_blank" method="post" action="depositaccount/doc/download" class="form-inline" ng-submit="downloadDepositAccountDocument(row)">
                        <button type="submit" class="btn btn-info btn-sm" ng-disabled="row.downloaded" title="Download">
                            <span class="glyphicon glyphicon-download-alt"></span>
                        </button>
                        <input type="hidden" name="code" ng-value="row.code" />
                    </form>
                </td>
            </tr>
        </tbody>

        <tr ng-show="tcTable.count == 0">
            <td colspan="4" class="tc-table-extension-cell text-center">No documents returned</td>
        </tr>

        <!-- Telclic Table Pagination Component -->
        <tr>
            <td colspan="4" class="tc-table-extension-cell text-left"><tc-table-pagination></tc-table-pagination></td>
        </tr>
    </table>

</div>