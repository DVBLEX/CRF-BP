crfApp.controller('CustomersController', ['$scope', '$rootScope', '$timeout', 'customersService', function ($scope, $rootScope, $timeout, customersService) {

    $rootScope.activeNavbar('#navbarCustomers');

    $scope.isVerifyingCustomer = false;
    $scope.verifyCustomerErrorMessage = "";
    $scope.verificationDenialReasonList = [];
    $scope.POIDVerificationDenialReasonList = [];
    $scope.POAVerificationDenialReasonList = [];
    $scope.photoVerificationDenialReasonList = [];
    $scope.customerVerificationDenialList = [];
    $scope.reasonList = [];
    $scope.isCustomerInfoEligible = false;
    $scope.customerVerificationSubmitButtonDisabled = false;

    $scope.isPopupShowed = false;
    $scope.customerAMLJson = '';
    $scope.viewStringJson = '';

    $scope.isStep1 = true;
    $scope.isStep2 = false;
    $scope.isStep3 = false;

    $scope.formData = {};

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

        customersService.listRegisteredCustomers(urlParams, $scope.listRegisteredCustomersCallBack, $scope.errorHandler);
    });

    $scope.listRegisteredCustomersCallBack = function (d) {
        $scope.showSpinner = false;
        $scope.tcTable.setData(d);
    };

    $scope.refreshTableData = function () {
        $scope.tcTable.reloadTable();
    };

    $scope.verifyCustomer = function (row) {

        $scope.selectedCustomerCode = row.code;
        $scope.isVerifyingCustomer = true;

        $scope.listVerificationDenialReasons();
        $scope.getCustomerForVerification();
    };

    $scope.showAMLPopup = function (flag) {
        $scope.isPopupShowed = flag;
    }

    $scope.selectStep = function (step) {
        let selectedStep = +step;
        if (selectedStep === 1) {
            $scope.isStep1 = true;
            $scope.isStep2 = false;
            $scope.isStep3 = false;
        } else if (selectedStep === 2) {
            $scope.isStep1 = false;
            $scope.isStep2 = true;
            $scope.isStep3 = false;
        } else if (selectedStep === 3) {
            $scope.isStep1 = false;
            $scope.isStep2 = false;
            $scope.isStep3 = true;
        }
    }

    $scope.closeVerifyingCustomer = function () {

        $scope.selectedCustomerCode = '';
        $scope.POIDDocument1 = '';
        $scope.POIDDocument2 = '';
        $scope.POADocument1 = '';
        $scope.POADocument2 = '';
        $scope.photoImage = '';
        $scope.isVerifyingCustomer = false;
        $scope.selectStep(1);
        $scope.isOtherReasonSelected1 = false;
        $scope.isOtherReasonSelected2 = false;
        $scope.isOtherReasonSelected3 = false;
        $scope.formData.otherReason1 = "";
        $scope.formData.otherReason2 = "";
        $scope.formData.otherReason3 = "";
        $scope.reasonList = [];
    };

    $scope.getCustomerForVerification = function () {

        $scope.showSpinner = true;

        customersService.getCustomerForVerification({

            customerCode: $scope.selectedCustomerCode

        }, function (data) {

            $scope.showSpinner = false;
            $scope.customer = data.customer;
            $scope.customerVerificationDenialList = data.customerVerificationDenialList;
            $scope.POIDDocument1Type = data.idScans[0].mimeType;
            $scope.POIDDocument2Type = data.idScans.length > 1 ? data.idScans[1].mimeType : '';
            $scope.POADocument1Type = data.poaScans[0].mimeType;
            $scope.POADocument2Type = data.poaScans.length > 1 ? data.poaScans[1].mimeType : '';
            $scope.photoFileType = data.photo.mimeType;

            $scope.customerAMLJson = data.customerAMLResponse;
            const startStringIndex = $scope.customerAMLJson.indexOf('AmlScanResponse=') + 16;
            const endStringIndex = $scope.customerAMLJson.indexOf(', numberOfMatches');
            $scope.viewStringJson = JSON.parse($scope.customerAMLJson.slice(startStringIndex, endStringIndex));

            $timeout(function () {
                $scope.$broadcast("userFiles", data);
            });

            if ($scope.photoFileType.toLowerCase().includes("image")) {

                $scope.photoImage = data.photo.data;
            }

        }, function (error) {

            $scope.showSpinner = false;
            console.log(error);
        });
    };

    $scope.verifyCustomerData = function () {

        $scope.customerVerificationSubmitButtonDisabled = true;
        $scope.showSpinner = true;

        var urlParams = {
            customerCode: $scope.selectedCustomerCode
        };

        customersService.verifyCustomer(urlParams, function (response) {

            $scope.showSpinner = false;
            $scope.verifyCustomerErrorMessage = "";
            $scope.closeVerifyingCustomer();
            $scope.refreshTableData();
            $rootScope.showResultMessage(true, "The customer has been successfully verified!");

        }, function (error) {

            $scope.customerVerificationSubmitButtonDisabled = false;
            $scope.showSpinner = false;
            $scope.verifyCustomerErrorMessage = error.data.responseText;
        });
    };

    $scope.denyCustomerData = function () {

        $scope.customerVerificationSubmitButtonDisabled = true;
        $scope.showSpinner = true;

        var denyData = {
            customerCode: $scope.selectedCustomerCode,
            reasonList: $scope.reasonList
        };

        customersService.denyCustomer(denyData, function (response) {

            $scope.showSpinner = false;
            $scope.verifyCustomerErrorMessage = "";
            $scope.reasonList = [];
            $scope.closeVerifyingCustomer();
            $scope.refreshTableData();
            $rootScope.showResultMessage(false, "The customer has been denied verification! The customer will be notified to take the necessary action.");

        }, function (error) {

            $scope.customerVerificationSubmitButtonDisabled = false;
            $scope.showSpinner = false;
            $scope.verifyCustomerErrorMessage = error.data.responseText;
            $scope.reasonList = [];
        });
    };

    $scope.listVerificationDenialReasons = function () {

        $scope.showSpinner = true;

        customersService.listVerificationDenialReasons({}, function (dataList) {

            $scope.verificationDenialReasonList = dataList;

            $scope.POIDVerificationDenialReasonList = dataList.filter(function (denialReason) {
                return denialReason.isPOIDRelated === true;
            }).map(function (el) {
                return {...el}
            });

            $scope.POAVerificationDenialReasonList = dataList.filter(function (denialReason) {
                return denialReason.isPOARelated === true;
            }).map(function (el) {
                return {...el}
            });

            $scope.photoVerificationDenialReasonList = dataList.filter(function (denialReason) {
                return denialReason.isPhotoRelated === true;
            }).map(function (el) {
                return {...el}
            });

            $scope.showSpinner = false;

        }, $scope.errorHandler);
    };

    $scope.radioSelected = function (reason, numberReason) {

        if (reason.radio == 'YES') {
            if (reason.id == 100 && numberReason === 'firstOtherReason') {
                $scope.isOtherReasonSelected1 = true;
            } else if (reason.id == 101 && numberReason === 'secondOtherReason') {
                $scope.isOtherReasonSelected2 = true;
            } else if (reason.id == 102 && numberReason === 'thirdOtherReason') {
                $scope.isOtherReasonSelected3 = true;
            }

        } else if (reason.radio == 'NO') {
            if (reason.id == 100 && numberReason === 'firstOtherReason') {
                $scope.isOtherReasonSelected1 = false;
                $scope.formData.otherReason1 = "";
            } else if (reason.id == 101 && numberReason === 'secondOtherReason') {
                $scope.isOtherReasonSelected2 = false;
                $scope.formData.otherReason2 = "";
            } else if (reason.id == 102 && numberReason === 'thirdOtherReason') {
                $scope.isOtherReasonSelected3 = false;
                $scope.formData.otherReason3 = "";
            }

        }
    };

    $scope.isThereAnyYesAnswer = function () {

        let denialReasonList = [...$scope.POIDVerificationDenialReasonList, ...$scope.POAVerificationDenialReasonList, ...$scope.photoVerificationDenialReasonList];
        for (var i = 0; i < denialReasonList.length; i++) {
            if (denialReasonList[i].radio === "YES") {
                return true;
            }
        }
        return false;
    };

    $scope.returnToPreviewStep = function (step) {
        let previousStep = +step;

        $scope.verifyCustomerErrorMessage = '';

        if (previousStep === 2) {
            $scope.POAVerificationDenialReasonList.forEach(function (el) {
                $scope.reasonList = $scope.reasonList.filter(function (reason) {
                    return reason.reasonId !== el.id
                });
            });
        }
        if (previousStep === 1) {
            $scope.POIDVerificationDenialReasonList.forEach(function (el) {
                $scope.reasonList = $scope.reasonList.filter(function (reason) {
                    return reason.reasonId !== el.id
                });
            });
        }
        $scope.selectStep(previousStep);

    }

    $scope.customerVerificationSubmit = function (list, step) {
        let denialReasonList = list;
        let nextStep = step;
        let otherReason = '';

        if (nextStep === 2) {
            if ($scope.isOtherReasonSelected1 && $scope.dataFormCustomerVerify.otherReason1.$invalid) {

                $scope.verifyCustomerErrorMessage = "Please specify the other reason.";
                return;
            }

        } else if (nextStep === 3) {
            if ($scope.isOtherReasonSelected2 && $scope.dataFormCustomerVerify.otherReason2.$invalid) {

                $scope.verifyCustomerErrorMessage = "Please specify the other reason.";
                return;
            }

        } else if (nextStep === 0) {
            if ($scope.isOtherReasonSelected3 && $scope.dataFormCustomerVerify.otherReason3.$invalid) {

                $scope.verifyCustomerErrorMessage = "Please specify the other reason.";
                return;
            }

        }

        var merchantVerificationDenialId = -1;

        for (var i = 0; i < denialReasonList.length; i++) {
            if (denialReasonList[i].radio == undefined) {
                $scope.verifyCustomerErrorMessage = "Please answer all questions.";
                return;

            } else if (denialReasonList[i].radio === "YES") {

                for (var x = 0; x < $scope.customerVerificationDenialList.length; x++) {
                    if (denialReasonList[i].id == $scope.customerVerificationDenialList[x].reasonId) {
                        merchantVerificationDenialId = $scope.customerVerificationDenialList[x].id;
                        break;
                    }
                }

                if (nextStep === 2) {
                    otherReason = (denialReasonList[i].id == 100 ? $scope.formData.otherReason1 : '');

                } else if (nextStep === 3) {
                    otherReason = (denialReasonList[i].id == 101 ? $scope.formData.otherReason2 : '');

                } else if (nextStep === 0) {
                    otherReason = (denialReasonList[i].id == 102 ? $scope.formData.otherReason3 : '');

                }

                $scope.reasonList.push({
                    id: merchantVerificationDenialId,
                    reasonId: denialReasonList[i].id,
                    denialReason: denialReasonList[i].description,
                    additionalDescription: otherReason
                });

            }
        }

        if (nextStep === 2) {
            $scope.selectStep(2);

        } else if (nextStep === 3) {
            $scope.selectStep(3);

        } else if (nextStep === 0) {
            if ($scope.reasonList.length === 0) {
                $scope.verifyCustomerData();

            } else {
                $scope.denyCustomerData();
            }
        }

        $scope.verifyCustomerErrorMessage = "";

    };

    $scope.refreshTableData();

}]);

crfApp.directive("fileViewer", function ($http, $compile) {
    return {
        restrict: 'AE',
        scope: {
            canvasId: '=canvasId'
        },
        template:
            '<div ng-show="showPDFFile && !showFile">' +
            '<div class="preview-data">' +
            '<canvas class="img-thumbnail" id="{{canvasId}}"></canvas>' +
            '</div>' +
            '<div class="margin-top-6px">' +
            '<button type="button" class="btn btn-primary pdf-uploader-btn" ng-click="onPrevPage()">' +
            '<span class="glyphicon glyphicon-chevron-left"></span> Previous' +
            '</button>' +
            '<button type="button" class="btn btn-primary pdf-uploader-btn" ng-click="onNextPage()">' +
            'Next <span class="glyphicon glyphicon-chevron-right"></span>' +
            '</button>' +
            '&nbsp; &nbsp;' +
            '<span>Page: <span>{{pageNumber}}</span> / <span>{{pageCount}}</span></span>' +
            '</div>' +
            '</div>' +
            '<div ng-show="!showPDFFile && showFile">' +
            '<div class="preview clearfix">' +
            '<div class="preview-data clearfix" ng-repeat="data in previewData track by $index">' +
            '<img class="img-thumbnail" ng-src={{data}}></img>' +
            '</div>' +
            '</div>' +
            '</div>',
        link: function (scope) {

            var formData = new FormData();
            scope.previewData = [];
            scope.showPDFFile = false;
            scope.showFile = false;
            scope.pageCount = 1;
            scope.pageNumber = 1;

            // Loaded via <script> tag, create shortcut to access PDF.js exports.
            var pdfjsLib = window['pdfjs-dist/build/pdf'];
            // The workerSrc property shall be specified.
            pdfjsLib.GlobalWorkerOptions.workerSrc = 'lib/pdf.js-2.7.570-dist/pdf.worker.min.js';

            var pdfDoc = null,
                pageNum = 1,
                pageRendering = false,
                pageNumPending = null,
                scale = 0.8,
                canvas = null,
                ctx = null;

            function previewFile(file) {

                var reader = new FileReader();
                var obj = new FormData().append('file', file);
                reader.onload = function (data) {
                    var src = data.target.result;
                    var size = ((file.size / (1024 * 1024)) > 1) ? (file.size / (1024 * 1024)) + ' mB' : (file.size / 1024) + ' kB';
                    scope.$apply(function () {
                        scope.previewData = [];
                        scope.previewData.push(src);
                        scope.showPDFFile = false;
                        scope.showFile = true;
                    });
                }
                reader.readAsDataURL(file);
                scope.inputFile = '';
            }

            function previewPDFFile(file) {

                var fileReader = new FileReader();
                fileReader.onload = function () {
                    var pdfData = new Uint8Array(this.result);

                    // Asynchronously downloads PDF.
                    pdfjsLib.getDocument({data: pdfData}).promise.then(function (pdfDoc_) {
                        pageNum = 1;
                        pdfDoc = pdfDoc_;
                        scope.$apply(function () {
                            scope.pageCount = pdfDoc.numPages;
                            scope.showPDFFile = true;
                            scope.showFile = false;
                            scope.pageNumber = pageNum;
                        });

                        // Initial/first page rendering
                        renderPage(pageNum);
                    });
                };
                fileReader.readAsArrayBuffer(file);
            }

            function renderPage(num) {
                pageRendering = true;
                // Using promise to fetch the page
                pdfDoc.getPage(num).then(function (page) {
                    var viewport = page.getViewport({scale: scale});
                    canvas.height = viewport.height;
                    canvas.width = viewport.width;

                    // Render PDF page into canvas context
                    var renderContext = {
                        canvasContext: ctx,
                        viewport: viewport
                    };
                    var renderTask = page.render(renderContext);

                    // Wait for rendering to finish
                    renderTask.promise.then(function () {
                        pageRendering = false;
                        if (pageNumPending !== null) {
                            // New page rendering is pending
                            renderPage(pageNumPending);
                            pageNumPending = null;
                        }
                    });
                });

                // Update page counters
                scope.pageNumber = num;
            }

            /**
             * If another page rendering in progress, waits until the rendering is
             * finised. Otherwise, executes rendering immediately.
             */
            function queueRenderPage(num) {
                if (pageRendering) {
                    pageNumPending = num;
                } else {
                    renderPage(num);
                }
            }

            /**
             * Displays previous page.
             */
            scope.onPrevPage = function () {
                if (pageNum <= 1) {
                    return;
                }
                pageNum--;
                queueRenderPage(pageNum);
            }

            /**
             * Displays next page.
             */
            scope.onNextPage = function () {
                if (pageNum >= pdfDoc.numPages) {
                    return;
                }
                pageNum++;
                queueRenderPage(pageNum);
            }

            scope.convertDataURIToBinary = function (dataURI) {
                var raw = window.atob(dataURI);
                var rawLength = raw.length;
                var array = new Uint8Array(new ArrayBuffer(rawLength));

                for (var i = 0; i < rawLength; i++) {
                    array[i] = raw.charCodeAt(i);
                }
                return array;
            }

            scope.$on("userFiles", function (event, data) {
                if (data !== undefined) {
                    var documentData;
                    switch (scope.canvasId) {
                        case "POID1":
                            documentData = data.idScans[0];
                            break;
                        case "POID2":
                            documentData = data.idScans[1];
                            break;
                        case "POA1":
                            documentData = data.poaScans[0];
                            break;
                        case "POA2":
                            documentData = data.poaScans[1];
                            break;
                        case "POID1ForConfirmation":
                            documentData = data.idScans[0];
                            break;
                    }
                    canvas = document.getElementById(scope.canvasId);
                    ctx = canvas.getContext('2d');
                    var documentBinary = scope.convertDataURIToBinary(documentData.data);
                    var docType = documentData.mimeType.toLowerCase();
                    var docFile = new File([documentBinary], "document." + documentData.type.toLowerCase(), {type: docType});
                    if (docType === "application/pdf") {
                        previewPDFFile(docFile);
                    } else {
                        previewFile(docFile);
                    }
                }


            });
        }
    }
});
