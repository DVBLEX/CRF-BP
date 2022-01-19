var customerSetUpApp = angular.module('customerSetUpApp', [ 'ngResource', 'ngRoute', 'ngAnimate', 'vcRecaptcha', 'tcCom', 'tcCommons', 'ngIntlTelInput' ])

.config([ '$httpProvider', function($httpProvider) {

    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
} ])

.config([ 'ngIntlTelInputProvider', function(ngIntlTelInputProvider) {
    ngIntlTelInputProvider.set({
        utilsScript : "js/intTelInputUtils.js",
        initialCountry : 'ie',
        autoPlaceholder : "off",
        separateDialCode : true,
        nationalMode : false
    });
} ]);

customerSetUpApp.controller('customerSetUpController', [
    '$scope',
    '$rootScope',
    '$timeout',
    '$filter',
    'tcCommonsService',
    'vcRecaptchaService',
    'customerSetUpService',
    function($scope, $rootScope, $timeout, $filter, tcCommonsService, vcRecaptchaService, customerSetUpService) {

        $scope.isMobile = false;
        $scope.showPopUpMessage = false;

        if (/Android|iOS|iPadOS|webOS|BlackBerry|IEMobile|iPad|iPhone/i.test(navigator.userAgent)) {
            $scope.isMobile = true;
            $scope.showPopUpMessage = true;
        } else {
            $scope.isMobile = false;
            $scope.showPopUpMessage = false;
        }

        if (/Chrome|CriOS/i.test(navigator.userAgent)) {
            $scope.showPopUpMessage = false;
        }

        if ($scope.isPassportScanDenied == undefined) {
            $scope.isPassportScanDenied = false;
        }

        $scope.formData = {};
        $scope.formStepsSelected = [ true, false, false ];
        $scope.showSpinner = false;
        $scope.contactPassportScanErrorResponse = "";
        $scope.step1IsCompleted = false;
        $scope.step2IsCompleted = false;
        $scope.step3IsCompleted = false;
        $scope.passportScanFileValid = false;
        $scope.passportScanFileUploaded = false;
        $scope.passportScanFileType = "";
        $scope.passportScanImage = "";
        $scope.passportScanImage2 = "";
        $scope.passportScanPDF = "";
        //12MB
        $scope.maxAllowedFileSize = 12582912;
        $scope.passportScanLinkHasSent = false;
        $scope.passportMobileUploadCheckLimitMinutes = 10;
        $scope.passportMobileUploadCheckIntervalSec = 6;
        $scope.passportMobileUploadCheckCount = 0;
        $scope.passportMobileUploadCancelled = false;
        $scope.photoFileValid = false;
        $scope.photoFileUploaded = false;
        $scope.photoFileType = "";
        $scope.photoImage = "";
        $scope.photoFileName = "none";
        $scope.photoScanLinkHasSent = false;
        $scope.photoMobileUploadCheckLimitMinutes = 10;
        $scope.photoMobileUploadCheckIntervalSec = 6;
        $scope.photoMobileUploadCheckCount = 0;
        $scope.photoMobileUploadCancelled = false;
        $scope.emailRegexp = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}\.[\d]{1,3}])|(([\w\-]+\.)+[a-zA-Z]{2,}))$/;
        $scope.phoneNumberRegexp = new RegExp("^\\+[1-9]{1}[0-9]{3,14}$");
        $scope.formData.msisdn = null;
        $scope.serverMSISDNLoaded = false;
        $scope.alternativePDFDisplay = false;
        $scope.isAccountSetupCompleted = false;
        $scope.dateTodayString = $filter('date')(new Date(), "yyyy-MM-dd");

        $scope.removePopUpMessage = function () {
            $scope.showPopUpMessage = false;
        }

        //new functionality of documents upload
        $scope.selectedOption = null;
        $scope.formData.POIDDocument1 = '';
        $scope.formData.POIDDocument2 = '';
        $scope.formData.POIDDocumentType1 = '';
        $scope.formData.POIDDocumentType2 = '';
        $scope.formData.POIDNumber1 = '';
        $scope.formData.POIDNumber2 = '';
        $scope.formData.expiryDate1 = null;
        $scope.formData.expiryDate2 = null;
        $scope.formData.POADocument1 = '';
        $scope.formData.POADocument2 = '';
        $scope.formData.POADocumentType1 = '';
        $scope.formData.POADocumentType2 = '';
        $scope.isResubmission = false;

        $scope.formData.photoOfYourself = '';
        $scope.showWebcam = false;
        $scope.isErrorWebcam = false;
        $scope.textErrorWebcam = '';
        $scope.contactPhotoErrorResponse = "";

        $scope.removeFile = false;

        $scope.photoOfDocumentsError = '';
        $scope.isError = false;
        $scope.isValidFileFromDirective = false;
        $scope.numberRegexp = /[A-Za-z0-9]+/;

        $scope.selectOption = function (option) {

            if ($scope.selectedOption !== +option) {
                $scope.removeFile = true;
                $scope.formData.POIDDocument1 = '';
                $scope.formData.POIDDocument2 = '';
                $scope.formData.POADocument1 = '';
                $scope.formData.POADocument2 = '';
                $scope.formData.POIDDocumentType1 = '';
                $scope.formData.POIDDocumentType2 = '';
                $scope.formData.POIDNumber1 = '';
                $scope.formData.POIDNumber2 = '';
                $scope.formData.expiryDate1 = null;
                $scope.formData.expiryDate2 = null;
                $scope.formData.POADocumentType1 = '';
                $scope.formData.POADocumentType2 = '';

                $scope.photoOfDocumentsError = '';
                $scope.isError = false;

                $scope.$broadcast("clearData");
            } else {
                $scope.removeFile = false;
            }

            $scope.selectedOption = +option;
            $scope.selectStep(1);

        };

        $scope.validateDocuments = function () {

            if (!$scope.numberRegexp.test($scope.formData.POIDNumber1) || $scope.contactPassportScanDataForm.expiryDate1.$invalid
            || $scope.formData.POIDDocument1 === '' || $scope.contactPassportScanDataForm.POIDDocumentType1.$invalid) {
                $scope.photoOfDocumentsError = 'Check POID document.';
                $scope.isError = true;

            } else if ($scope.selectedOption !== 2 && (!$scope.numberRegexp.test($scope.formData.POIDNumber2) || $scope.contactPassportScanDataForm.expiryDate2.$invalid
                || $scope.formData.POIDDocument2 === '' || $scope.contactPassportScanDataForm.POIDDocumentType2.$invalid)) {
                $scope.photoOfDocumentsError = 'Check POID document.';
                $scope.isError = true;

            } else if ($scope.formData.POADocument1 === '' || $scope.contactPassportScanDataForm.POADocumentType1.$invalid) {
                $scope.photoOfDocumentsError = 'Check POA document.';
                $scope.isError = true;

            } else if ($scope.selectedOption !== 2 && ($scope.formData.POADocument2 === '' || $scope.contactPassportScanDataForm.POADocumentType2.$invalid)) {
                $scope.photoOfDocumentsError = 'Check POA document.';
                $scope.isError = true;

            } else if ($scope.formData.POIDDocumentType1 === $scope.formData.POIDDocumentType2
                || $scope.formData.POADocumentType1 === $scope.formData.POADocumentType2) {
                $scope.photoOfDocumentsError = 'Duplicate document type.';
                $scope.isError = true;

            } else {
                $scope.photoOfDocumentsError = '';
                $scope.isError = false;
                $scope.selectStep(2);
            }
        }

        Webcam.set({
            width: '100%',
            height: '100%',
            dest_width: 640,
            dest_height: 480,
            image_format: 'jpeg',
            jpeg_quality: 90
        });

        Webcam.on( 'error', function(err) {
            $timeout(function () {
                $scope.textErrorWebcam = err.message + '.';
                $scope.showWebcam = false;
                $scope.isErrorWebcam = true;
                Webcam.reset();
            });
        } );

        $scope.renderWebcam = function () {

            if ($scope.isErrorWebcam === false) {
                $timeout(function () {
                    Webcam.attach('#my_camera');
                });
                $scope.showWebcam = true;
            } else {
                $scope.showWebcam = false;
            }

        }

        $scope.hideWebcam = function () {
            Webcam.reset();
            $scope.showWebcam = false;
        }

        <!-- Code to handle taking the snapshot and displaying it locally -->
        $scope.takeSnapshot = function () {

            // take snapshot and get image data
            Webcam.snap( function(data_uri) {
                $scope.$broadcast("photoFromWebcam", data_uri);

            } );

        }

        $scope.finishAccountSetup = function() {

            $scope.showSpinner = true;

            var uploadFormData = new FormData();
            uploadFormData.append('kycOption', $scope.selectedOption);
            uploadFormData.append('id1Type', $scope.formData.POIDDocumentType1);
            uploadFormData.append('id1Number', $scope.formData.POIDNumber1);
            uploadFormData.append('dateId1ExpiryString', $filter('date')($scope.formData.expiryDate1, "dd/MM/yyyy"));
            uploadFormData.append('poa1Type', $scope.formData.POADocumentType1);
            if ($scope.formData.POIDDocument1 !== '') {
                uploadFormData.append('id1Files', $scope.formData.POIDDocument1);
            }
            if ($scope.formData.POADocument1 !== '') {
                uploadFormData.append('poa1Files', $scope.formData.POADocument1);
            }
            uploadFormData.append('isResubmission', $scope.isResubmission);
            if ($scope.selectedOption === 1) {
                uploadFormData.append('id2Type', $scope.formData.POIDDocumentType2);
                uploadFormData.append('id2Number', $scope.formData.POIDNumber2);
                uploadFormData.append('dateId2ExpiryString', $filter('date')($scope.formData.expiryDate2, "dd/MM/yyyy"));
                uploadFormData.append('poa2Type', $scope.formData.POADocumentType2);
                if ($scope.formData.POIDDocument2 !== '') {
                    uploadFormData.append('id2Files', $scope.formData.POIDDocument2);
                }
                if ($scope.formData.POADocument2 !== '') {
                    uploadFormData.append('poa2Files', $scope.formData.POADocument2);
                }
            }
            uploadFormData.append('photoFiles', $scope.formData.photoOfYourself);


            $scope.showSpinner = true;

            customerSetUpService.submitIdDetails(uploadFormData, function(response) {

                $scope.showSpinner = false;
                $scope.isAccountSetupCompleted = true;
                if ($scope.isResubmission === true) {
                    $scope.accountReSubmit();
                }

            }, function(error) {

                $scope.showSpinner = false;

            });
        };

        //account re-submission
        $scope.denialListAgreed = false;
        $scope.accountReSubmitSuccess = false;

        $scope.isAccountReSubmission = false;

        $scope.getReSubmissionData = function () {
            $scope.showSpinner = true;

            customerSetUpService.getFlagsAccountReSubmissionData(function (response) {

                if (response.singleData.isPassportScanDenied === true) {
                    $scope.isAccountReSubmission = true;

                    customerSetUpService.getAccountReSubmissionData(function (response) {

                        let date1arr = response.singleData.customer.dateId1ExpiryString.split('/');
                        let date1 = new Date(date1arr[2], date1arr[1] - 1, date1arr[0]);

                        let date2arr = response.singleData.customer.dateId2ExpiryString.split('/');
                        let date2 = new Date(date2arr[2], date2arr[1] - 1, date2arr[0]);

                        $scope.selectedOption = response.singleData.customer.kycOption;
                        $scope.formData.POIDDocumentType1 = response.singleData.customer.id1Type;
                        $scope.formData.POIDDocumentType2 = response.singleData.customer.id2Type;
                        $scope.formData.POADocumentType1 = response.singleData.customer.poa1Type;
                        $scope.formData.POADocumentType2 = response.singleData.customer.poa2Type;
                        $scope.formData.POIDNumber1 = response.singleData.customer.id1Number;
                        $scope.formData.POIDNumber2 = response.singleData.customer.id2Number;
                        $scope.formData.expiryDate1 = date1;
                        $scope.formData.expiryDate2 = date2;

                        $scope.resubmissionData = response.singleData;

                        $timeout(function() {
                            $scope.$broadcast("resubmissionData", $scope.resubmissionData);
                        });

                        $scope.photoImage = response.singleData.photo.data;

                        $scope.isResubmission = true;

                        $scope.showSpinner = false;

                    }, function (error) {

                        $scope.showSpinner = false;
                        console.log(error);
                    });
                }
                $scope.showSpinner = false;

            }, function (error) {

                $scope.showSpinner = false;
                console.log(error);
            });


        };
        $scope.getReSubmissionData();


        $scope.validFileSelected = function(fileRole) {
            
            if (fileRole == 1) {
                
                //passport / national id card
                if ($scope.passportScanFileValid) {
                    $scope.uploadPassportScan();
                }
                
            } else if (fileRole == 2) {
                
                //photo
                if ($scope.photoFileValid) {
                    $scope.uploadPhoto();
                }
            }
        };

        $scope.selectStep = function(stepIndex) {
            if ($scope.formStepsSelected[stepIndex] == false) {

                $scope.step1IsCompleted = stepIndex > 0;
                $scope.step2IsCompleted = stepIndex > 1;
                $scope.step3IsCompleted = stepIndex > 2;

                for (var i = 0; i < $scope.formStepsSelected.length; i++) {
                    $scope.formStepsSelected[i] = false;
                }
                $scope.formStepsSelected[stepIndex] = true;
            }
        };

        $scope.$watch('serverMSISDN', function() {
            if (!$scope.serverMSISDNLoaded && $scope.serverMSISDN != undefined) {
                if ($scope.serverMSISDN != "") {
                    $scope.formData.msisdn = ($scope.serverMSISDN.startsWith("+") ? "" : "+") + $scope.serverMSISDN;
                }
                $scope.serverMSISDNLoaded = true;
            }
        });

        $scope.errorHandler = function(error) {

            $scope.showSpinner = false;
            console.log(error);
        };

        $scope.setResponse = function(response) {
            $scope.response = response;
        };
        $scope.setWidgetId = function(widgetId) {
            $scope.widgetId = widgetId;
        };
        $scope.cbExpiration = function() {
            vcRecaptchaService.reload($scope.widgetId);
        };

        $scope.uploadPassportScan = function() {

            var file = $scope.passportScanFile;
            var uploadFormData = new FormData();
            uploadFormData.append('file', file);
            uploadFormData.append('fileRole', 1);
            $scope.showSpinner = true;

            customerSetUpService.uploadFile(uploadFormData, function(response) {

                angular.element("#passportScanFile").val(null);
                $scope.contactPassportScanErrorResponse = "";
                $scope.passportScanFileUploaded = true;
                $scope.step2IsCompleted = true;
                $scope.showSpinner = false;

                if ($scope.passportScanFileType.toLowerCase() === "jpg" || $scope.passportScanFileType.toLowerCase() === "jpeg") {

                    $scope.passportScanImage = response.data.singleData;

                } else if ($scope.passportScanFileType.toLowerCase() === "pdf") {

                    try {
                        $scope.passportScanPDF = tcCommonsService.createResourceUrlFromBase64(response.data.singleData, 'application/pdf');
                        $scope.alternativePDFDisplay = true;
                    } catch(e) {
                        $scope.alternativePDFDisplay = true;
                    }
                }

            }, function(error) {

                $scope.showSpinner = false;
                $scope.passportScanFileUploaded = false;
                $scope.passportScanFileValid = false;
                angular.element("#passportScanFile").val(null);
                $scope.contactPassportScanErrorResponse = error.data.responseText;
            });
        };

        $scope.uploadPhoto = function() {

            var file = $scope.photoFile;
            var uploadFormData = new FormData();
            uploadFormData.append('file', file);
            uploadFormData.append('fileRole', 2);
            $scope.showSpinner = true;

            customerSetUpService.uploadFile(uploadFormData, function(response) {

                angular.element("#photoFile").val(null);
                $scope.contactPhotoErrorResponse = "";
                $scope.photoFileUploaded = true;
                $scope.step3IsCompleted = true;
                $scope.showSpinner = false;

                if ($scope.photoFileType.toLowerCase() === "jpg" || $scope.photoFileType.toLowerCase() === "jpeg"
                    || $scope.photoFileType.toLowerCase() === "jpe" || $scope.photoFileType.toLowerCase() === "jif"
                    || $scope.photoFileType.toLowerCase() === "jfif" || $scope.photoFileType.toLowerCase() === "jfi"
                    || $scope.photoFileType.toLowerCase() === "png") {

                    $scope.photoImage = response.data.singleData;
                }

            }, function(error) {

                $scope.showSpinner = false;
                $scope.photoFileUploaded = false;
                $scope.photoFileValid = false;
                angular.element("#photoFile").val(null);
                $scope.contactPhotoErrorResponse = error.data.responseText;
            });
        };

        $scope.changePassportScan = function() {

            $scope.step2IsCompleted = false;
            $scope.contactPassportScanErrorResponse = "";
            $scope.passportScanFileType = "";

            if ($scope.passportScanPDF != undefined && $scope.passportScanPDF != null && $scope.passportScanPDF != "") {
                tcCommonsService.revokeResourceUrl($scope.passportScanPDF);
            }
            $scope.passportScanPDF = "";

            $scope.passportScanImage = "";
            $scope.passportScanFile = null;
            $scope.passportScanFileValid = false;
            $scope.passportScanFileUploaded = false;
            angular.element("#passportScanFile").val(null);
        };

        $scope.changePhoto = function() {

            $scope.step3IsCompleted = false;
            $scope.contactPhotoErrorResponse = "";
            $scope.photoFileName = "none";
            $scope.photoFileType = "";
            $scope.photoImage = "";
            $scope.photoFile = null;
            $scope.photoFileValid = false;
            $scope.photoFileUploaded = false;
            angular.element("#photoFile").val(null);
        };

        $scope.sendPassportScanFromPhoneLink = function() {

            $scope.showSpinner = true;
            customerSetUpService.sendMobileUploadLink({

                fileRole : 1

            }, function(response) {

                $scope.showSpinner = false;
                $scope.passportScanLinkHasSent = true;
                $scope.passportMobileUploadCancelled = false;
                $scope.passportMobileUploadCheckCount = 0;
                $timeout(function() {
                    $scope.checkPassportMobileUploadCompleted();
                }, $scope.passportMobileUploadCheckIntervalSec * 1000);

            }, function(error) {

                $scope.showSpinner = false;
                $scope.contactPassportScanErrorResponse = error.data.responseText;
            });
        };

        $scope.sendPhotoFromPhoneLink = function() {

            $scope.showSpinner = true;

            customerSetUpService.sendMobileUploadLink({

                fileRole : 2

            }, function(response) {

                $scope.showSpinner = false;
                $scope.photoLinkHasSent = true;
                $scope.photoMobileUploadCancelled = false;
                $scope.photoMobileUploadCheckCount = 0;
                $timeout(function() {
                    $scope.checkPhotoMobileUploadCompleted();
                }, $scope.photoMobileUploadCheckIntervalSec * 1000);

            }, function(error) {

                $scope.showSpinner = false;
                $scope.contactPhotoErrorResponse = error.data.responseText;
            });
        };

        $scope.checkPassportMobileUploadCompleted = function() {

            customerSetUpService.checkMobileUploadCompleted({

                fileRole : 1

            }, function(response) {

                if (!$scope.passportMobileUploadCancelled) {

                    $scope.passportMobileUploadCheckCount = 0;
                    $scope.passportScanFileUploaded = true;
                    $scope.step2IsCompleted = true;
                    $scope.passportScanLinkHasSent = false;
                    $scope.showUploadedPassportScan();
                }

            }, function(error) {

                $scope.passportMobileUploadCheckCount++;
                if (!$scope.passportMobileUploadCancelled
                    && $scope.passportMobileUploadCheckCount * $scope.passportMobileUploadCheckIntervalSec <= $scope.passportMobileUploadCheckLimitMinutes * 60) {
                    $timeout(function() {
                        $scope.checkPassportMobileUploadCompleted();
                    }, $scope.passportMobileUploadCheckIntervalSec * 1000);
                }
            });
        };

        $scope.getPassportScanFileTypeAndShowFile = function() {

            $scope.showSpinner = true;

            customerSetUpService.getContactFileType({

                fileRole : 1

            }, function(fileType) {

                $scope.showSpinner = false;
                $scope.passportScanFileType = fileType;
                $scope.showUploadedPassportScanDo();

            }, function(error) {

                $scope.showSpinner = false;
                console.log(error);
            });
        };

        $scope.showUploadedPassportScan = function() {

            $scope.getPassportScanFileTypeAndShowFile();
        };

        $scope.showUploadedPassportScanDo = function() {

            if ($scope.passportScanFileType.toLowerCase() != "pdf") {

                $scope.showSpinner = true;

                //in case of image we need to download the image as base64

                customerSetUpService.showUploadedFile({

                    fileRole : 1

                }, function(response) {

                    $scope.showSpinner = false;
                    $scope.passportScanFileType = response.dataList[0];

                    if ($scope.passportScanFileType.toLowerCase() == "jpg" || $scope.passportScanFileType.toLowerCase() == "jpeg") {

                        $scope.passportScanImage = response.singleData;
                    }

                }, function(error) {

                    $scope.showSpinner = false;
                    console.log(error);
                });
            }
        };

        $scope.checkPhotoMobileUploadCompleted = function() {

            customerSetUpService.checkMobileUploadCompleted({

                fileRole : 2

            }, function(response) {

                if (!$scope.photoMobileUploadCancelled) {

                    $scope.photoMobileUploadCheckCount = 0;
                    $scope.photoFileUploaded = true;
                    $scope.step3IsCompleted = true;
                    $scope.photoLinkHasSent = false;
                    $scope.showUploadedPhoto();
                }

            }, function(error) {

                $scope.photoMobileUploadCheckCount++;
                if (!$scope.photoMobileUploadCancelled
                    && $scope.photoMobileUploadCheckCount * $scope.photoMobileUploadCheckIntervalSec <= $scope.photoMobileUploadCheckLimitMinutes * 60) {
                    $timeout(function() {
                        $scope.checkPhotoMobileUploadCompleted();
                    }, $scope.photoMobileUploadCheckIntervalSec * 1000);
                }
            });
        };

        $scope.showUploadedPhoto = function() {

            $scope.showSpinner = true;

            customerSetUpService.showUploadedFile({

                fileRole : 2

            }, function(response) {

                $scope.showSpinner = false;
                $scope.photoFileType = response.dataList[0];

                if ($scope.photoFileType.toLowerCase() === "jpg" || $scope.photoFileType.toLowerCase() === "jpeg") {

                    $scope.photoImage = response.singleData;
                }

            }, function(error) {

                $scope.showSpinner = false;
                console.log(error);
            });
        };

        $scope.cancelPassportMobileUpload = function() {

            $scope.passportMobileUploadCancelled = true;

            customerSetUpService.cancelMobileUpload({

                fileRole : 1

            }, function(response) {

                //do nothing

            }, function(error) {

                console.log(error);
            });

            $timeout(function() {

                $scope.passportScanLinkHasSent = false;
                $scope.passportMobileUploadCheckCount = 0;
                $scope.passportScanFileUploaded = false;
                $scope.step2IsCompleted = false;

            }, $scope.passportMobileUploadCheckIntervalSec * 1000);
        };

        $scope.cancelPhotoMobileUpload = function() {

            $scope.photoMobileUploadCancelled = true;

            customerSetUpService.cancelMobileUpload({

                fileRole : 2

            }, function(response) {

                //do nothing

            }, function(error) {

                console.log(error);
            });

            $timeout(function() {

                $scope.photoLinkHasSent = false;
                $scope.photoMobileUploadCheckCount = 0;
                $scope.photoFileUploaded = false;
                $scope.step3IsCompleted = false;

            }, $scope.photoMobileUploadCheckIntervalSec * 1000);
        };

        $scope.accountReSubmit = function() {
            // used when customer account has been previously denied and the customer resubmits the setup form with the necessary amendments
            
            $scope.showSpinner = true;

            customerSetUpService.accountReSubmit(function(response) {

                $scope.accountReSubmitSuccess = true;
                $scope.isAccountSetupCompleted = true;
                $scope.showSpinner = false;

            }, function(error) {

                $scope.showSpinner = false;
                console.log(error);
            });
        };

    } ]);

customerSetUpApp.controller('takePhotoByPhoneController', [ '$scope', 'tcCommonsService', 'customerSetUpService', function($scope, tcCommonsService, customerSetUpService) {

    $scope.showSpinner = false;
    $scope.passportScanFileUploaded = false;
    $scope.passportScanFileValid = false;
    $scope.passportScanFile = "";
    $scope.contactPassportScanErrorResponse = "";
    //12MB
    $scope.maxAllowedFileSize = 12582912;
    
    $scope.validFileSelected = function(fileRole) {
        $scope.uploadPassportScan();
    };

    $scope.uploadPassportScan = function() {

        $scope.showSpinner = true;
        var file = $scope.passportScanFile;
        var uploadFormData = new FormData();
        uploadFormData.append('file', file);
        uploadFormData.append('fileRole', $scope.fileRole);
        uploadFormData.append('email', $scope.input1);
        uploadFormData.append('t1', $scope.input2);
        uploadFormData.append('t2', $scope.input3);

        customerSetUpService.uploadFileByPhone(uploadFormData, function(response) {

            $scope.showSpinner = false;
            angular.element("#passportScanFile").val(null);
            $scope.passportScanFileUploaded = true;

        }, function(error) {

            $scope.showSpinner = false;
            angular.element("#passportScanFile").val(null);
            $scope.passportScanFileUploaded = false;
            $scope.contactPassportScanErrorResponse = error.data.responseText;
        });
    };

} ]);

customerSetUpApp.directive('fileModel', [ '$parse', '$timeout', function($parse, $timeout) {
    return {
        restrict : 'A',
        link : function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function() {
                $timeout(function() {
                //scope.$apply(function() {
                    if (element[0].files.length > 0) {
                        
                        modelSetter(scope, element[0].files[0]);
                        
                        if (attrs.name === "passportScanFile") {

                            scope.validFileSelected(1);

                        } else if (attrs.name === "photoFile") {

                            scope.validFileSelected(2);
                        }
                        
                    } else {
                        modelSetter(scope, null);
                    }
                });
            });
        }
    };
} ]);

customerSetUpApp.directive('validateFile', [ function() {
    return {
        restrict : 'A',
        link : function(scope, elem, attr, ctrl) {
            $(elem).bind('change', function() {

                if (this.files.length > 0) {

                    var fileName = this.files[0].name;
                    var ext = fileName.toLowerCase().substring(this.files[0].name.lastIndexOf(".") + 1);
                    var isPhoto = attr.name === "photoFile";

                    if ((isPhoto && (ext === 'jpg' || ext === 'jpeg' || ext === 'jpe' || ext === 'jif' || ext === 'jfif' || ext === 'jfi' || ext === 'png')) || (!isPhoto && (ext === 'pdf' || ext === 'jpg' || ext === 'jpeg' || ext === 'png'))) {

                        if (scope.maxAllowedFileSize > 0 && this.files[0].size > scope.maxAllowedFileSize) {

                            if (attr.name === "passportScanFile") {

                                scope.passportScanFileValid = false;
                                scope.contactPassportScanErrorResponse = "The selected file size exceeds the limit allowed. The limit is 12MB.";
                                scope.passportScanFileType = "";

                            } else if (attr.name === "photoFile") {

                                scope.photoFileValid = false;
                                scope.contactPhotoErrorResponse = "The selected file size exceeds the limit allowed. The limit is 12MB.";
                                scope.photoFileType = "";
                                scope.photoFileName = "none";
                            }

                        } else {

                            if (attr.name === "passportScanFile") {

                                scope.passportScanFileValid = true;
                                scope.contactPassportScanErrorResponse = "";
                                scope.passportScanFileType = ext;

                            } else if (attr.name === "photoFile") {

                                scope.photoFileValid = true;
                                scope.contactPhotoErrorResponse = "";
                                scope.photoFileType = ext;
                                scope.photoFileName = fileName;
                            }
                        }
                    } else {

                        if (attr.name === "passportScanFile") {

                            scope.passportScanFileValid = false;
                            scope.contactPassportScanErrorResponse = "Unsupported file type! Supported types are PDF, JPG, JPEG, PNG.";
                            scope.passportScanFileType = "";

                        } else if (attr.name === "photoFile") {

                            scope.photoFileValid = false;
                            scope.contactPhotoErrorResponse = "Unsupported file type! Supported types are JPG, JPEG, JPE, JIF, JFIF, JFI, PNG.";
                            scope.photoFileType = "";
                            scope.photoFileName = "none";
                        }
                    }
                } else {

                    if (attr.name === "passportScanFile") {

                        scope.passportScanFileValid = false;
                        scope.contactPassportScanErrorResponse = "";
                        scope.passportScanFileType = "";

                    } else if (attr.name === "photoFile") {

                        scope.photoFileValid = false;
                        scope.contactPhotoErrorResponse = "";
                        scope.photoFileType = "";
                        scope.photoFileName = "none";
                    }
                }
            });
        }
    }
} ]);

customerSetUpApp.directive('compareTo', function() {
    return {
        require : "ngModel",
        scope : {
            otherModelValue : "=compareTo"
        },
        link : function(scope, element, attributes, ngModel) {

            ngModel.$validators.compareTo = function(modelValue) {
                return modelValue == scope.otherModelValue;
            };

            scope.$watch("otherModelValue", function() {
                ngModel.$validate();
            });
        }
    };
});

customerSetUpApp.filter('to_trusted_html', [ '$sce', function($sce) {
    return function(text) {
        return $sce.trustAsHtml(text);
    };
} ]);

customerSetUpApp.directive("fileMultiApploader",function($http, $compile, $rootScope){
    return {
        restrict : 'AE',
        scope : {
            uploadedFile: '=uploadedFile',
            canvasId: '=canvasId'
        },
        template :
            '<div class="col-md-12">'+
            '<input accept="image/png, image/jpeg, application/pdf" class="fileUpload" id="myPdf" type="file" multiple ng-value="inputFile"/>'+
            '<label class="browseFilesButton upload-file-label btn btn-default btn-block msg file-upload-margin-bottom"> <span class="glyphicon glyphicon-file"></span>'+
            'Upload a file from your device'+
            '</label>'+
            '<div ng-if="isFileError">'+
            '<div class="alert alert-danger" role="alert">'+
            '<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>'+
            '<span class="sr-only">Error: </span> {{fileError}}'+
            '</div>'+
            '</div>'+
            '</div>'+
            '<div class="col-md-12" ng-show="showPDFFile && !showFile">'+
            '<div class="preview-data">'+
            '<div>'+
            '<canvas class="img-thumbnail" id="{{canvasId}}"></canvas>'+
            '</div>'+
            '<div class="preview-controls">'+
            '<span ng-click="removePDFFile()" class="circle remove">'+
            '<i class="glyphicon glyphicon-remove"></i>'+
            '</span>'+
            '</div>'+
            '</div>'+
            '<div>'+
            '<button type="button" class="btn btn-primary pdf-uploader-btn" ng-click="onPrevPage()">'+
            '<span class="glyphicon glyphicon-chevron-left"></span> Previous'+
            '</button>'+
            '<button type="button" class="btn btn-primary pdf-uploader-btn" ng-click="onNextPage()">'+
            'Next <span class="glyphicon glyphicon-chevron-right"></span>'+
            '</button>'+
            '&nbsp; &nbsp;'+
            '<span>Page: <span>{{pageNumber}}</span> / <span>{{pageCount}}</span></span>'+
            '</div>'+
            '</div>'+
            '<div class="col-md-12" ng-show="!showPDFFile && showFile">'+
            '<div class="preview clearfix">'+
            '<div class="preview-data clearfix" ng-repeat="data in previewData track by $index">'+
            '<div>'+
            '<img class="img-thumbnail" ng-src={{data}}></img>'+
            '</div>'+
            '<div class="preview-controls">'+
            '<span ng-click="remove(data)" class="circle remove">'+
            '<i class="glyphicon glyphicon-remove"></i>'+
            '</span>'+
            '</div>'+
            '</div>'+
            '</div>'+
            '</div>',
        link : function(scope,elem){

            var formData = new FormData();
            scope.previewData = [];
            scope.maxAllowedFileSize = 12582912;
            scope.fileError = '';
            scope.isFileError = false;
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

            function uploadFile(e,type){
                canvas = document.getElementById(scope.canvasId);
                ctx = canvas.getContext('2d');

                e.preventDefault();
                var files = "";
                if(type == "formControl"){
                    files = e.target.files;
                } else {
                    files = e.originalEvent.dataTransfer.files;
                }

                for(var i=0;i<files.length;i++){
                    var file = files[i];
                    if (file.type == "application/pdf" && scope.canvasId !== "photo") {
                        previewPDFFile(file);

                    } else {
                        if(file.size >= scope.maxAllowedFileSize){
                            scope.$apply(() => {
                                scope.fileError = 'The selected file size exceeds the limit allowed. The limit is 12MB.';
                                scope.isFileError = true ;
                            });

                        } else if (file.type == "image/jpeg" || file.type == "image/png") {
                            scope.fileError = '';
                            scope.isFileError = false;
                            previewFile(file);

                        } else {
                            scope.$apply(() => {
                                if (scope.canvasId === "photo") {
                                    scope.fileError = 'Unsupported file type! Supported types are JPG, JPEG, JPE, JIF, JFIF, JFI, PNG.';
                                } else {
                                    scope.fileError = 'Unsupported file type! Supported types are JPG, JPEG, JPE, JIF, JFIF, JFI, PNG, PDF.';
                                }
                                scope.isFileError = true ;
                            });

                        }
                    }
                }
            }

            function previewFile(file){

                var reader = new FileReader();
                var obj = new FormData().append('file',file);
                reader.onload = function(data){
                    var src = data.target.result;
                    var size = ((file.size/(1024*1024)) > 1)? (file.size/(1024*1024)) + ' mB' : (file.size/		1024)+' kB';
                    scope.$apply(function(){
                        scope.previewData = [];
                        scope.previewData.push(src);
                        scope.showPDFFile = false;
                        scope.showFile = true;
                        scope.uploadedFile = file;//scope.previewData[0];
                    });
                }
                reader.readAsDataURL(file);
                scope.inputFile = '';
            }

            function previewPDFFile(file) {
                scope.fileError = '';
                scope.isFileError = false;

                var fileReader = new FileReader();
                fileReader.onload = function() {
                    var pdfData = new Uint8Array(this.result);

                    // Asynchronously downloads PDF.
                    pdfjsLib.getDocument({data: pdfData}).promise.then(function(pdfDoc_) {
                        pageNum = 1;
                        pdfDoc = pdfDoc_;
                        scope.$apply(function(){
                            scope.pageCount = pdfDoc.numPages;
                            scope.showPDFFile = true;
                            scope.showFile = false;
                            scope.pageNumber = pageNum;
                            scope.uploadedFile = file;
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
                pdfDoc.getPage(num).then(function(page) {
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
                    renderTask.promise.then(function() {
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

            elem.find('.fileUpload').bind('change',function(e){
                uploadFile(e,'formControl');
            });

            elem.find('.upload-file-label').bind("click",function(e){
                $compile(elem.find('.fileUpload'))(scope).trigger('click');
            });

            scope.remove = function(data) {
                var index= scope.previewData.indexOf(data);
                scope.previewData.splice(index,1);
                scope.showFile = false;
                scope.uploadedFile = '';
            }

            scope.removePDFFile = function() {
                ctx.clearRect(0, 0, canvas.width, canvas.height);
                scope.showPDFFile = false;
                scope.uploadedFile = '';
            }

            scope.$on('clearData', function () {
                if (scope.showPDFFile === true) {
                    ctx.clearRect(0, 0, canvas.width, canvas.height);
                    scope.showPDFFile = false;

                }
                scope.previewData = [];
                scope.uploadedFile = '';
                scope.fileError = '';
                scope.isFileError = false;
                scope.showFile = false;

            });

            scope.convertDataURIToBinary = function (dataURI) {
                var raw = window.atob(dataURI);
                var rawLength = raw.length;
                var array = new Uint8Array(new ArrayBuffer(rawLength));

                for(var i = 0; i < rawLength; i++) {
                    array[i] = raw.charCodeAt(i);
                }
                return array;
            }

            scope.$on("photoFromWebcam", function (event, img) {
                if (scope.canvasId === 'photo') {
                    let imgArr = img.split(',');
                    let webcamBinary = scope.convertDataURIToBinary(imgArr[1]);
                    let webcamFile = new File([webcamBinary], 'photo-from-camera.jpeg', {type: 'image/jpeg'});
                    previewFile(webcamFile);
                }
            })

            scope.$on("resubmissionData", function (event, data) {
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
                        case "photo":
                            documentData = data.photo;
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
