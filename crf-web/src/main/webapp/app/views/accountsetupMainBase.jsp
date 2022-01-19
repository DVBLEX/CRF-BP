<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

{{customerType=
<c:out value="${customerType}" />
;""}}

<div class="row" ng-if="showPopUpMessage === true">
    <div class="col-md-8 col-md-offset-2 pop-up-message-wrapper">
        <span>
            For best experience use Chrome browser
        </span>
        <span class="circle remove" ng-click="removePopUpMessage()">
            <i class="glyphicon glyphicon-remove remove-pop-up-message"></i>
        </span>
    </div>
</div>

<div class="row">
    <div ng-hide="isAccountSetupCompleted">

        <div class="panel" ng-class="{'panel-primary': !step1IsCompleted, 'panel-success': step1IsCompleted}">
            <div class="panel-heading" ng-click="selectStep(0)">
                <div class="panel-title">1. Identification</div>
            </div>

            <div class="panel-body" ng-show="formStepsSelected[0]">
                <div class="col-md-12" ng-show="!step1IsCompleted">
                    <label>Please select the option below.</label>
                    <hr>
                    <div class="form-group col-lg-6 option-wrapper">
                        <p><label>Option 1: Copy Documents</label></p>

                        <p>1(a) Proof of Identity (POID)</p>
                        <span>A photocopy of <b>TWO</b> of the following:</span>
                        <ul>
                            <li>Passport</li>
                            <li>Driving Licence</li>
                            <li>National Identity Card</li>
                        </ul>
                        <hr>
                        <p>1(b) Proof of Address (POA)</p>
                        <span>A photocopy of <b>TWO</b> of the following to include your name and home address:</span>
                        <ul>
                            <li>Utility bill (ESB; Gas; Phone - Landline; Waste)</li>
                            <li>Bank statement</li>
                            <li>Tax notice from the Revenue Commissioners</li>
                            <li>Social Welfare document</li>
                            <li>Motor tax document</li>
                            <li>Home or motor insurance certificate or renewal notice</li>
                        </ul>
                        <p>
                            <b>Note:</b>
                            When providing two photocopies of your POA,
                            these documents must be from separate entities and dated within the last 6 months.
                        </p>

                        <div class="btn-wrapper">
                            <button type="button" class="btn btn-primary btn-block" ng-click="selectOption(1)">
                                <span class="glyphicon glyphicon-chevron-right"></span> Select Option 1
                            </button>
                        </div>


                        <hr class="vertical-line">
                    </div>

                    <div class="form-group col-lg-6 option-wrapper">
                        <p><label>Option 2: Original or Certified Documents</label></p>

                        <p>2(a) Proof of Identity (POID)</p>
                        <span>A certified copy of <b>ONE</b> of the documents listed in Option 1(a) adjacent.</span>
                        <p>
                            <b>Note:</b>
                            A certified copy of your POID must be certified by one of the following professionals:
                        </p>
                        <ul>
                            <li>Garda Siochana / Police Officer</li>
                            <li>Regulated financial or credit institution</li>
                            <li>Practising solicitor / Notary Public</li>
                            <li>Practising Chartered or Certified Public Accountant</li>
                            <li>Justice of the Peace</li>
                            <li>Commissioner for Oaths</li>
                            <li>Embassy / Consular Staff</li>
                        </ul>
                        <hr>
                        <p>2(b) Proof of Address (POA)</p>
                        <p>
                                An up to date (dated within the last 6 months) original or certified copy of
                                <b>ONE</b> of the documents listed in Option 1(b) adjacent.
                        </p>

                        <div class="btn-wrapper">
                            <button type="button" class="btn btn-primary btn-block" ng-click="selectOption(2)">
                                <span class="glyphicon glyphicon-chevron-right"></span> Select Option 2
                            </button>
                        </div>

                    </div>

                </div>
            </div>
        </div>

        <div class="panel"
            ng-class="{'panel-primary': !step1IsCompleted || !step2IsCompleted, 'panel-success': step2IsCompleted && step1IsCompleted, 'disabledAccordion': !step1IsCompleted}">
            <div class="panel-heading" ng-click="selectStep(1)" ng-class="{'linkDisabled': !step1IsCompleted}">
                <div class="panel-title">
                    2. Photo of documents <span class="glyphicon glyphicon-ok" ng-show="step2IsCompleted"></span>
                </div>
            </div>

            <div class="panel-body" ng-show="formStepsSelected[1] && step1IsCompleted">

                <div class="col-sm-12">
                    <form name="contactPassportScanDataForm" autocomplete="off" novalidate>

                        <!-- File Upload part -->
                        <div>

                            <div class="form-group col-md-12 noLeftPaddingForSubDivs">

                                <h4>Documents upload, supported file types: JPG, JPEG, JPE, JIF, JFIF, JFI, PNG, PDF</h4>

                                <div class="col-md-12">
                                    <hr>
                                </div>

                                <div class="col-md-12 document-label">Proof of Identification</div>

                                <div class="col-md-12">
                                    <hr>
                                </div>

                                <div ng-if="selectedOption === 1 || selectedOption === 2">
                                    <div class="form-group col-md-8">
                                        <div class="col-md-4">
                                            <div class="document-upload-wrapper">
                                                <label for="POIDDocumentType1">Type document</label>
                                                <select  ng-class="{'error-highlight': isError && contactPassportScanDataForm.POIDDocumentType1.$invalid}"
                                                         class="form-control" id="POIDDocumentType1" name="POIDDocumentType1" ng-model="formData.POIDDocumentType1" required>
                                                    <option value="1">Passport</option>
                                                    <option value="2">Driving Licence</option>
                                                    <option value="3">National Identity Card</option>
                                                </select>
                                            </div>
                                        </div>

                                        <div class="col-md-4">
                                            <div class="document-upload-wrapper">
                                                <label for="expiryDate1">Expiry Date</label>
                                                <input type="date" class="form-control" id="expiryDate1"
                                                       ng-class="{'error-highlight': isError && contactPassportScanDataForm.expiryDate1.$invalid}"
                                                       name="expiryDate1" ng-model="formData.expiryDate1" placeholder="dd/MM/yyyy"
                                                       min={{dateTodayString}} ng-min={{dateTodayString}} required>
                                            </div>
                                        </div>

                                        <div class="col-md-4">
                                            <div class="document-upload-wrapper">
                                                <label for="POIDNumber1">Number</label>
                                                <input type="text" class="form-control" id="POIDNumber1" name="POIDNumber1" ng-model="formData.POIDNumber1"
                                                       ng-class="{'error-highlight': isError && !numberRegexp.test(formData.POIDNumber1)}">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div file-multi-apploader uploaded-file="formData.POIDDocument1" canvas-id="'POID1'"></div>
                                    </div>
                                </div>

                                <div ng-if="selectedOption === 1">
                                    <div class="form-group col-md-8 margin-top-40">
                                        <div class="col-md-4">
                                            <div class="document-upload-wrapper">
                                                <label for="POIDDocumentType2">Type document</label>
                                                <select ng-class="{'error-highlight': isError && contactPassportScanDataForm.POIDDocumentType2.$invalid}"
                                                        class="form-control" id="POIDDocumentType2" name="POIDDocumentType2" ng-model="formData.POIDDocumentType2" required>
                                                    <option value="1">Passport</option>
                                                    <option value="2">Driving Licence</option>
                                                    <option value="3">National Identity Card</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="document-upload-wrapper">
                                                <label for="expiryDate2">Expiry Date</label>
                                                <input type="date" class="form-control" id="expiryDate2"
                                                       ng-class="{'error-highlight': isError && contactPassportScanDataForm.expiryDate2.$invalid}"
                                                       name="expiryDate2" ng-model="formData.expiryDate2" placeholder="dd/MM/yyyy"
                                                       min={{dateTodayString}} ng-min={{dateTodayString}} required>
                                            </div>
                                        </div>

                                        <div class="col-md-4">
                                            <div class="document-upload-wrapper">
                                                <label for="POIDNumber2">Number</label>
                                                <input type="text" class="form-control" id="POIDNumber2" name="POIDNumber2" ng-model="formData.POIDNumber2"
                                                       ng-class="{'error-highlight': isError && !numberRegexp.test(formData.POIDNumber2)}">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div file-multi-apploader uploaded-file="formData.POIDDocument2" canvas-id="'POID2'"></div>
                                    </div>
                                </div>

                                <div class="col-md-12">
                                    <hr>
                                </div>

                                <div class="document-label col-md-12">Proof of Address</div>

                                <div class="col-md-12">
                                    <hr>
                                </div>

                                <div ng-if="selectedOption === 1 || selectedOption === 2">
                                    <div class="form-group col-md-12">
                                        <div class="col-md-4">
                                            <div class="document-upload-wrapper">
                                                <label for="POADocumentType1">Type document</label>
                                                <select ng-class="{'error-highlight': isError && contactPassportScanDataForm.POADocumentType1.$invalid}"
                                                        class="form-control" id="POADocumentType1" name="POADocumentType1" ng-model="formData.POADocumentType1" required>
                                                    <option value="4">Utility bill</option>
                                                    <option value="5">Bank statement</option>
                                                    <option value="6">Tax notice from the Revenue Commissioners</option>
                                                    <option value="7">Social Welfare document</option>
                                                    <option value="8">Motor tax document</option>
                                                    <option value="9">Home or motor insurance certificate or renewal notice</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div file-multi-apploader uploaded-file="formData.POADocument1" canvas-id="'POA1'"></div>
                                    </div>
                                </div>

                                <div ng-if="selectedOption === 1">
                                    <div class="form-group col-md-12 margin-top-40">
                                        <div class="col-md-4">
                                            <div class="document-upload-wrapper">
                                                <label for="POADocumentType2">Type document</label>
                                                <select ng-class="{'error-highlight': isError && contactPassportScanDataForm.POADocumentType2.$invalid}"
                                                        class="form-control" id="POADocumentType2" name="POADocumentType2" ng-model="formData.POADocumentType2" required>
                                                    <option value="4">Utility bill</option>
                                                    <option value="5">Bank statement</option>
                                                    <option value="6">Tax notice from the Revenue Commissioners</option>
                                                    <option value="7">Social Welfare document</option>
                                                    <option value="8">Motor tax document</option>
                                                    <option value="9">Home or motor insurance certificate or renewal notice</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div file-multi-apploader uploaded-file="formData.POADocument2"  canvas-id="'POA2'"></div>
                                    </div>
                                </div>

                                <div class="col-md-12" ng-if="selectedOption === 1 || selectedOption === 2">
                                    <hr>
                                </div>

                                <div class="form-group col-md-12" ng-if="photoOfDocumentsError !== ''">
                                    <div class="alert alert-danger" role="alert">
                                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> {{photoOfDocumentsError}}
                                    </div>
                                </div>

                                <div class="col-sm-8 col-sm-offset-2 form-group">
                                    <button type="submit" class="btn btn-primary btn-block" ng-click="validateDocuments()">
                                        Next <span class="glyphicon glyphicon-chevron-right"></span>
                                    </button>
                                </div>
                            </div>

                        </div>

                    </form>
                </div>

            </div>
        </div>

        <div class="panel"
             ng-class="{'panel-primary': !step3IsCompleted || !step2IsCompleted || !step1IsCompleted, 'panel-success': step3IsCompleted && step2IsCompleted && step1IsCompleted, 'disabledAccordion': !step1IsCompleted || !step2IsCompleted}">
            <div class="panel-heading" ng-click="selectStep(2)" ng-class="{'linkDisabled': !step1IsCompleted || !step2IsCompleted}">
                <div class="panel-title">
                    3. Photo of Yourself <span class="glyphicon glyphicon-ok" ng-show="step3IsCompleted"></span>
                </div>
            </div>

            <div class="panel-body" ng-show="formStepsSelected[2] && step2IsCompleted">

                <div class="col-sm-12">
                    <form name="contactPhotoDataForm" autocomplete="off" novalidate>

                        <!-- Classic File Upload from your computer or device -->
                        <div class="form-group col-md-12 noLeftPaddingForSubDivs">

                            <h4>Photo upload, supported file types: JPG, JPEG, JPE, JIF, JFIF, JFI, PNG or make the photo with the webcam</h4>

                            <div class="col-md-12">
                                <hr>
                            </div>

                            <div class="col-md-6">

                                <div file-multi-apploader uploaded-file="formData.photoOfYourself" canvas-id="'photo'"></div>

                            </div>


                            <div class="col-md-6" ng-class="{'webcam-none': isMobile === true}">
                                <button ng-show="showWebcam === false" type="submit"
                                        class="btn btn-primary file-upload-margin-bottom btn-webcam"
                                        ng-click="renderWebcam()">
                                    Show webcam
                                </button>

                                <button ng-show="showWebcam === true" type="submit"
                                        class="btn btn-primary file-upload-margin-bottom btn-webcam"
                                        ng-click="hideWebcam()">
                                    Hide webcam
                                </button>
                                <div ng-show="showWebcam === true">
                                    <div id="my_camera"></div>
                                    <button type="submit" class="btn btn-primary btn-block"
                                            ng-click="takeSnapshot()">
                                        Take Snapshot
                                    </button>

                                </div>
                                <div ng-show="isErrorWebcam === true">
                                    <div class="alert alert-danger" role="alert">
                                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                                        <span class="sr-only">Error: </span> {{textErrorWebcam}}
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-12">
                                <hr>
                            </div>

                            <div class="col-sm-8 col-sm-offset-2 form-group">
                                <button type="submit" class="btn btn-success btn-block" ng-disabled="formData.photoOfYourself === ''" ng-click="finishAccountSetup()">
                                    Submit
                                </button>
                            </div>



                        </div>

                        <%--                        <div class="form-group col-md-12 noLeftPaddingForSubDivs" ng-show="!photoFileUploaded && !photoLinkHasSent">--%>

                        <%--                            <!-- On Computer, Send a link to upload picture from your mobile and continue the process on computer -->--%>
                        <%--                            <c:if test="${deviceType == \"'normal'\"}">--%>
                        <%--                                <div>--%>
                        <%--                                    <h4>Take a photo of yourself from smartphone via Email</h4>--%>

                        <%--                                    <div class="form-group col-md-6" style="padding-top: 20px;">--%>
                        <%--                                        <div class="col-md-6">--%>
                        <%--                                            <input type="text" class="form-control" name="photoEmailTo" value="${userEmail}" readonly />--%>
                        <%--                                        </div>--%>
                        <%--                                        <div class="col-md-6">--%>
                        <%--                                            <button type="button" class="btn btn-primary btn-block" ng-disabled="photoLinkHasSent" ng-click="sendPhotoFromPhoneLink()">--%>
                        <%--                                                <span class="glyphicon glyphicon-envelope"></span> Send Link--%>
                        <%--                                            </button>--%>
                        <%--                                        </div>--%>
                        <%--                                    </div>--%>

                        <%--                                    <div class="form-group col-md-6" style="padding-top: 20px;">Please open the email on your smartphone and follow the instructions.</div>--%>

                        <%--                                </div>--%>
                        <%--                            </c:if>--%>
                        <%--                        </div>--%>

                        <%--                        <div class="form-group col-md-12" ng-show="!photoFileUploaded && photoLinkHasSent">--%>

                        <%--                            <div class="alert alert-success" role="alert">--%>
                        <%--                                <span> <span class="glyphicon glyphicon-ok" aria-hidden="true" style="padding-right: 10px;"></span>We have sent you the link via email to <b>${userEmail}</b>.--%>
                        <%--                                    Please check your inbox and follow the instructions. If you don't see an email, please check your spam/junk folder as it might have been--%>
                        <%--                                    filtered out.--%>
                        <%--                                </span>--%>
                        <%--                            </div>--%>

                        <%--                            <div ng-show="!photoMobileUploadCancelled" style="font-size: x-large;">Waiting for smartphone upload...</div>--%>
                        <%--                            <div ng-show="photoMobileUploadCancelled" style="font-size: x-large;">Cancelling smartphone upload...</div>--%>
                        <%--                            <div class="col-md-12 text-center">--%>
                        <%--                                <img src="/crf-web/app/img/loadingSpinner.gif" style="max-width: 120px; padding-bottom: 20px;">--%>
                        <%--                            </div>--%>

                        <%--                            <div class="col-sm-8 col-sm-offset-2 form-group">--%>
                        <%--                                <button type="button" class="btn btn-danger btn-block" ng-disabled="photoMobileUploadCancelled" ng-click="cancelPhotoMobileUpload()">--%>
                        <%--                                    Cancel <span class="glyphicon glyphicon-remove"></span>--%>
                        <%--                                </button>--%>
                        <%--                            </div>--%>
                        <%--                        </div>--%>

                        <%--                        <div class="form-group col-md-12" ng-if="contactPhotoErrorResponse !== ''">--%>
                        <%--                            <div class="alert alert-danger" role="alert">--%>
                        <%--                                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <span class="sr-only">Error: </span> {{contactPhotoErrorResponse}}--%>
                        <%--                            </div>--%>
                        <%--                        </div>--%>

                        <%--                        <div class="col-md-12" ng-if="photoFileUploaded">--%>
                        <%--                            <div class="alert alert-success" role="alert">--%>
                        <%--                                <span>--%>
                        <%--                                    <div class="form-group">--%>
                        <%--                                        <span class="glyphicon glyphicon-ok" aria-hidden="true"></span> Photo is uploaded successfully.--%>
                        <%--                                    </div>--%>
                        <%--                                    <div class="form-group">--%>
                        <%--                                        <img class="img-thumbnail my-img" ng-src="{{photoImage}}">--%>
                        <%--                                    </div>--%>
                        <%--                                </span>--%>
                        <%--                            </div>--%>

                        <%--                            <div class="col-sm-8 col-sm-offset-2 form-group">--%>
                        <%--                                <a href="#" class="btn btn-danger btn-block" ng-click="changePhoto()" tabindex="-1">Change the photo<span class="glyphicon glyphicon-remove"></span></a>--%>
                        <%--                            </div>--%>

                        <%--                            <div class="col-sm-8 col-sm-offset-2" ng-if="photoFileUploaded == true">--%>
                        <%--                                <button type="submit" class="btn btn-primary btn-block" ng-disabled="!step1IsCompleted || !step2IsCompleted" ng-click="finishAccountSetup()">Submit</button>--%>
                        <%--                            </div>--%>
                        <%--                        </div>--%>
                    </form>
                </div>

            </div>
        </div>


    </div>
</div>

<div ng-if="isAccountSetupCompleted">
    <div class="text-center alert alert-success col-md-12">
        <h4>Thank you!</h4>
        <div style="padding-top: 20px;">
            Your account details will be verified soon and we will notify you in due course. <br> In case of further information required we will get in touch with you. <br>
            <br>
            <div class="col-md-6 col-md-offset-3">
                <a class="btn btn-success btn-block" href="login.html">Click here to Log In!</a>
            </div>
        </div>
    </div>
</div>
