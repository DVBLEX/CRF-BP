crfApp.controller('UsersController', ['$scope', '$rootScope', '$timeout', 'usersService', function ($scope, $rootScope, $timeout, usersService) {

    $rootScope.activeNavbar('#navbarUsers');

    $scope.emailRegexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}])|(([\w\-]+\.)+[a-zA-Z]{2,}))$/;
    $scope.isPopupShowed = false;
    $scope.showSpinner = false;
    $scope.isEditing = false;
    $scope.editUserErrorMessage = '';
    $scope.formData = {};
    $scope.dateCreatedUser = '';
    $scope.username = '';
    $scope.popupTitle = '';
    $scope.isPopupForEditing = false;
    $scope.isPopupForRemoving = false;

    $scope.showPopup = function (flag) {
        $scope.isPopupShowed = flag;
        if (flag === false) {
            $scope.formData.firstName = '';
            $scope.formData.lastName = '';
            $scope.formData.email = '';
            $scope.dateCreatedUser = '';
            $scope.editUserErrorMessage = '';
            $scope.isPopupForEditing = false;
            $scope.isPopupForRemoving = false;
        }
    };

    $scope.errorHandler = function (error) {

        $scope.showSpinner = false;
        console.log(error);
    };

    $scope.tcTable = new TCTable(function (currentPage, pageRecordCount) {

        $scope.showSpinner = true;
        $scope.sortType = null;
        $scope.sortReverse = false;

        var urlParams = {
            page: currentPage - 1,
            size: pageRecordCount
        };

        usersService.listRegisteredUsers(urlParams, $scope.listRegisteredUsersCallBack, $scope.errorHandler);
    });

    $scope.listRegisteredUsersCallBack = function (d) {
        $scope.showSpinner = false;
        $scope.tcTable.setData(d);
    };

    $scope.refreshTableData = function () {
        $scope.tcTable.reloadTable();
    };

    $scope.showAddPopup = function () {

        $scope.showPopup(true);

        $scope.popupTitle = 'Add new user';
        $scope.isPopupForRemoving = false;
        $scope.isPopupForEditing = false;
    };

    $scope.showEditPopup = function (row) {

        $scope.showPopup(true);

        $scope.formData.code = row.code;
        $scope.formData.firstName = row.firstName;
        $scope.formData.lastName = row.lastName;
        $scope.formData.email = row.email;
        $scope.dateCreatedUser = row.dateCreatedString;
        $scope.username = row.username;

        $scope.popupTitle = 'Edit user';
        $scope.isPopupForRemoving = false;
        $scope.isPopupForEditing = true;
    };

    $scope.showRemovePopup = function (row) {

        $scope.showPopup(true);

        $scope.formData.code = row.code;
        $scope.formData.firstName = row.firstName;
        $scope.formData.lastName = row.lastName;
        $scope.formData.email = row.email;
        $scope.dateCreatedUser = row.dateCreatedString;
        $scope.username = row.username;

        $scope.popupTitle = 'Remove user';
        $scope.isPopupForRemoving = true;
        $scope.isPopupForEditing = false;
    };

    $scope.editUser = function() {


            if ($scope.formData.firstName === undefined || $scope.formData.firstName === null || $scope.formData.firstName === "") {
                $scope.editUserErrorMessage = "Please enter First Name.";

            } else if ($scope.formData.lastName === undefined || $scope.formData.lastName === null || $scope.formData.lastName === "") {
                $scope.editUserErrorMessage = "Please enter Last Name.";

            } else if ($scope.regUserFormData.email.$invalid && $scope.isPopupForEditing === false) {
                $scope.editUserErrorMessage = "Please enter a valid Email";

            } else {
                $scope.showSpinner = true;
                $scope.isEditing = true;

                if ($scope.isPopupForEditing === false) {

                    let urlParams = {
                        firstName: $scope.formData.firstName,
                        lastName: $scope.formData.lastName,
                        email: $scope.formData.email
                    };

                    usersService.userEmailRegURL(urlParams, function(response){

                        $rootScope.showResultMessage(true, "An email with the registration page has been sent to " + $scope.formData.email + "!");

                        $scope.showSpinner = false;
                        $scope.isEditing = false;
                        $scope.formData = {};
                        $scope.editUserErrorMessage = '';

                        $scope.showPopup(false);

                    },function(error) {
                        $scope.showSpinner = false;
                        $scope.isEditing = false;
                        $scope.editUserErrorMessage = error.data.responseText;
                    });

                } else {

                    let urlParams = {
                        code: $scope.formData.code,
                        firstName: $scope.formData.firstName,
                        lastName: $scope.formData.lastName,
                        email: $scope.formData.email,
                        dateCreatedString: $scope.dateCreatedUser,
                        username: $scope.username
                    };

                    usersService.editUser(urlParams, function(response){

                        $rootScope.showResultMessage(true, "User edited!");

                        $scope.showSpinner = false;
                        $scope.isEditing = false;
                        $scope.formData = {};
                        $scope.dateCreatedUser = '';
                        $scope.username = '';
                        $scope.editUserErrorMessage = '';

                        $scope.showPopup(false);

                        $scope.refreshTableData();

                    },function(error) {
                        $scope.showSpinner = false;
                        $scope.isEditing = false;
                        $scope.editUserErrorMessage = error.data.responseText;
                    });

                }

            }

    };

    $scope.removeUser = function () {

        let urlParams = {
            code: $scope.formData.code
        };

        usersService.deleteUser(urlParams, function(response){

            $rootScope.showResultMessage(true, "User deleted!");

            $scope.showSpinner = false;
            $scope.isEditing = false;
            $scope.formData = {};
            $scope.dateCreatedUser = '';
            $scope.username = '';
            $scope.editUserErrorMessage = '';

            $scope.showPopup(false);

            $scope.refreshTableData();

        },function(error) {
            $scope.showSpinner = false;
            $scope.isEditing = false;
            $scope.editUserErrorMessage = error.data.responseText;
        });
    };

    $scope.refreshTableData();

}]);
