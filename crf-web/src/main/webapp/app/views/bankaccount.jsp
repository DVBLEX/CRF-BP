<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-fluid no-padding">

    <div class="d-table w-100" style="margin-bottom: 5px;">
        <nav aria-label="breadcrumb" class="d-table-cell">
            <ol class="breadcrumb d-table-cell w-100">
                <li class="breadcrumb-item active">Bank Account</li>
            </ol>
        </nav>
        <div class="d-table-cell tar">
            <button ng-click="editBankDetails();" ng-show="isFormReadonly" class="btn btn-primary btn-sm" ng-disabled="disableControls">
                <span class="glyphicon glyphicon-edit"></span> Edit
            </button>

            <button ng-click="cancelEditBankDetails();" ng-hide="isFormReadonly" class="btn btn-danger btn-sm" ng-disabled="disableControls">
                <span class="glyphicon glyphicon-remove"></span> Cancel
            </button>
        </div>
    </div>

    <form name="dataFormBankDetails" autocomplete="off" novalidate>
        <div class="form-group col-md-6">
            <label for="bankName">Bank Name</label> <input type="text" class="form-control" name="bankName" ng-model="formData.bankName" ng-minlength="2" ng-maxlength="64"
                maxlength="64" avoid-special-chars ng-readonly="isFormReadonly" required>
        </div>

        <div class="form-group col-md-6">
            <label for="bankAccountName">Bank Account Name</label> <input type="text" class="form-control" name="bankAccountName" ng-model="formData.bankAccountName"
                ng-minlength="2" ng-maxlength="64" maxlength="64" avoid-special-chars ng-readonly="isFormReadonly" required>
        </div>

        <div class="form-group col-md-6">
            <label for="bankAddress">Bank Address</label> <input type="text" class="form-control" name="bankAddress" ng-model="formData.bankAddress" ng-minlength="2"
                ng-maxlength="256" maxlength="256" avoid-special-chars ng-readonly="isFormReadonly" required>
        </div>

        <div class="form-group col-md-6">
            <label for="iban">IBAN</label> <input type="text" class="form-control" name="iban" ng-model="formData.iban" ng-minlength="15" ng-maxlength="39" maxlength="39"
                alphanumeric capitalize ng-readonly="isFormReadonly" required>
        </div>

        <div class="form-group col-md-6">
            <label for="bic">BIC</label> <input type="text" class="form-control" name="bic" ng-model="formData.bic" ng-minlength="8" ng-maxlength="11" maxlength="11" alphanumeric
                capitalize ng-readonly="isFormReadonly" required>
        </div>

        <div class="form-group col-md-12">
            <div class="animate-if" ng-if="bankDetailsSubmitErrorResponse !== ''">
                <div class="alert alert-danger" role="alert">
                    <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> {{bankDetailsSubmitErrorResponse}}
                </div>
            </div>
        </div>

        <div class="form-group col-md-12" ng-hide="isFormReadonly">
            <button type="button" class="btn btn-success btn-block" ng-disabled="bankDetailsSubmitButtonDisabled || dataFormBankDetails.$pristine" ng-click="submitBankDetails()">Submit
                Bank Details</button>
        </div>
    </form>

    <%@ include file="spinner.html"%>

</div>