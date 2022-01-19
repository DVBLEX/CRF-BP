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

                <div class="popup-text" ng-show="isPopupForRemoving">
                    Do you want to remove this user?
                </div>

                <form name="regUserFormData" id="regUserFormData" autocomplete="off" novalidate ng-show="!isPopupForRemoving">
                    <div class="popup-text">
                        <div class="form-group">
                            <label for="firstName">First Name</label>
                            <input type="text" class="form-control" id="firstName" name="firstName"
                                   ng-model="formData.firstName" ng-maxlength="32" maxlength="32"
                                   ng-trim="true" avoid-special-chars avoid-numbers required>
                        </div>

                        <div class="form-group">
                            <label for="lastName">Last Name</label>
                            <input type="text" class="form-control" id="lastName" name="lastName"
                                   ng-model="formData.lastName" ng-maxlength="32" maxlength="32"
                                   ng-trim="true" avoid-special-chars avoid-numbers required>
                        </div>

                        <div class="form-group">
                            <label for="email">Email</label>
                            <input type="text" class="form-control" id="email" name="email" ng-model="formData.email"
                                   ng-pattern="emailRegexp" ng-maxlength="64"
                                   maxlength="64" ng-disabled="isPopupForEditing" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="animate-if" ng-if="editUserErrorMessage !== ''">
                            <div class="alert alert-danger" role="alert">
                                <span class="glyphicon glyphicon-remove"></span><span class="sr-only">Error: </span> <span>{{editUserErrorMessage}}</span>
                            </div>
                        </div>
                    </div>
                </form>

                <hr>

                <div class="popup-btn" ng-show="!isPopupForRemoving">
                    <button ng-click="showPopup(false)" ng-disabled="isEditing" class="btn btn-primary btn-sm btn-width-120px">
                        <span class="glyphicon glyphicon-arrow-left"></span> Back
                    </button>
                    <button ng-click="editUser()" ng-disabled="isEditing" class="btn btn-success btn-sm btn-width-120px">
                        Submit
                    </button>
                </div>

                <div class="popup-btn" ng-show="isPopupForRemoving">
                    <button ng-click="showPopup(false)" ng-disabled="isEditing" class="btn btn-primary btn-sm btn-width-120px">
                        No
                    </button>
                    <button ng-click="removeUser()" ng-disabled="isEditing" class="btn btn-danger btn-sm btn-width-120px">
                        Yes
                    </button>
                </div>
            </div>
        </div>
    </div>

    <div class="d-table w-100" style="margin-bottom: 5px;">
        <nav aria-label="breadcrumb" class="d-table-cell">
            <ol class="breadcrumb d-table-cell w-100">
                <li class="breadcrumb-item active">Users</li>
            </ol>
        </nav>
        <div class="d-table-cell tar">

            <button ng-click="refreshTableData();" class="btn btn-primary btn-sm" ng-disabled="disableControls">
                <span class="glyphicon glyphicon-refresh"></span> Refresh
            </button>

            <button ng-click="showAddPopup()" class="btn btn-success btn-sm btn-width-120px">
                Add
            </button>
        </div>
    </div>

    <div>

        <table class="tc-table">

            <thead>
            <tr>
                <td>
                    <a href="" ng-click="sortType = 'firstName'; sortReverse = !sortReverse;">
                        First Name
                        <span ng-show="sortType == 'firstName' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'firstName' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'lastName'; sortReverse = !sortReverse;">
                        Last Name
                        <span ng-show="sortType == 'lastName' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'lastName' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'email'; sortReverse = !sortReverse;">
                        Email
                        <span ng-show="sortType == 'email' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'email' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td>
                    <a href="" ng-click="sortType = 'dateCreatedUser'; sortReverse = !sortReverse;">
                        Created Date
                        <span ng-show="sortType == 'dateCreatedUser' && !sortReverse" class="glyphicon glyphicon-sort-by-attributes font-size-10"></span>
                        <span ng-show="sortType == 'dateCreatedUser' && sortReverse" class="glyphicon glyphicon-sort-by-attributes-alt font-size-10"></span>
                    </a>
                </td>
                <td></td>
            </tr>
            </thead>

            <colgroup>
                <col width="24%">
                <col width="24%">
                <col width="24%">
                <col width="24%">
                <col width="4%">
            </colgroup>

            <tbody ng-repeat="row in rows = (tcTable.data | orderBy:sortType:sortReverse) track by $index">
            <tr ng-click="tcTable.selectRow($event)">
                <td title="First Name">{{row.firstName}}</td>
                <td title="Last Name">{{row.lastName}}</td>
                <td title="Email">{{row.email}}</td>
                <td title="Created Date">{{row.dateCreatedString}}</td>
                <td class="text-center">
                    <div class="tc-table-btn-wrapper">
                        <button class="btn btn-primary btn-intable" ng-click="showEditPopup(row); $event.stopPropagation();" title="Edit User">
                            <span class="glyphicon glyphicon-edit"></span>
                        </button>

                        <button ng-if="currentUsername !== row.username" class="btn btn-danger btn-intable" ng-click="showRemovePopup(row); $event.stopPropagation();" title="Delete User">
                            <span class="glyphicon glyphicon-remove"></span>
                        </button>
                    </div>
                </td>
            </tr>
            </tbody>

            <tr ng-show="tcTable.count == 0">
                <td colspan="5" class="tc-table-extension-cell text-center">No customers returned</td>
            </tr>

            <!-- Telclic Table Pagination Component -->
            <tr>
                <td colspan="5" class="tc-table-extension-cell text-left"><tc-table-pagination></tc-table-pagination></td>
            </tr>
        </table>

    </div>

    <%@ include file="../spinner.html"%>

</div>

