/*
 * Telclic Communication Service for AngularJS 1, Version 1.0 JS
 */
var tcCom = angular.module("tcCom", [ 'ngResource' ]).config([ '$httpProvider', function($httpProvider) {

    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

} ]).service('tcComService', [ '$resource', '$http', '$window', function($resource, $http, $window) {
    
    this.callAPI = function(apiPath, urlParams, callBackSuccess, callBackError) {

        callAPIImpl(apiPath, urlParams, callBackSuccess, callBackError, 1);
    };

    this.callAPIForDataList = function(apiPath, urlParams, callBackSuccess, callBackError) {

        callAPIImpl(apiPath, urlParams, callBackSuccess, callBackError, 2);
    };

    this.callAPIForSingleData = function(apiPath, urlParams, callBackSuccess, callBackError) {

        callAPIImpl(apiPath, urlParams, callBackSuccess, callBackError, 3);
    };

    this.callAPIForPaginatedDataList = function(apiPath, urlParams, callBackSuccess, callBackError) {

        callAPIImpl(apiPath, urlParams, callBackSuccess, callBackError, 4);
    };

    this.callApiWithJSONParam = function(apiPath, JSONParams, callBackSuccess, callBackError) {

        var headers = {
            method : "POST",
            isArray : false,
            headers : {
                'Content-Type' : 'application/json;charset=UTF-8',
            }
        };

        var api = $resource(apiPath, {}, {
            save : headers
        });

        api.save(JSONParams).$promise.then(function(response) {
            
            var validAPIResponse = response !== undefined && response.responseCode !== undefined;

            if (validAPIResponse && response.responseCode === 0) {

                callBackSuccess(response);

            } else if (validAPIResponse) {
                
                callBackError({
                    data : {
                        responseText : response.responseText
                    }
                });
                
            } else {

                errorCheckAndCallBackImpl(response, callBackError);
            }

        }, function(error) {

            errorCallBackImpl(error, callBackError);
        });
    };

    /*
     * type:
     * 
     * 1: normal call. Returns with the whole response object.
     * 
     * 2: list data. Returns with the dataList
     * 
     * 3: getSingleData OR count data. Returns with the singleDataObject
     * 
     * 4: list data. Returns with the Response: dataList + page (pagination information)
     */
    var callAPIImpl = function(apiPath, urlParams, callBackSuccess, callBackError, type) {

        var api = $resource(apiPath);

        api.save($.param(urlParams)).$promise.then(function(response) {
            
            var validAPIResponse = response !== undefined && response.responseCode !== undefined;

            if (validAPIResponse && response.responseCode === 0) {

                if (type === 1) {

                    callBackSuccess(response);

                } else if (type === 2) {

                    callBackSuccess(response.dataList);

                } else if (type === 3) {

                    callBackSuccess(response.singleData);

                } else if (type === 4) {

                    callBackSuccess({
                        dataList : response.dataList,
                        page : response.page
                    });

                } else {

                    callBackSuccess(response);
                }

            } else if (validAPIResponse) {
                
                callBackError({
                    data : {
                        responseText : response.responseText
                    }
                });
                
            } else {

                errorCheckAndCallBackImpl(response, callBackError);
            }

        }, function(error) {

            errorCallBackImpl(error, callBackError);
        });
    };

    this.uploadSingleFile = function(apiPath, uploadFormData, callBackSuccess, callBackError) {

        $http.post(apiPath, uploadFormData, {

            headers : {
                transformRequest : angular.identity,
                'Content-Type' : undefined
            }

        }).then(function(response) {

            var validAPIResponse = response !== undefined && response.responseCode !== undefined;
            var resCode = -1;
            if (!validAPIResponse) {

                if (response.data.responseCode !== undefined) {
                    resCode = response.data.responseCode;
                    validAPIResponse = true;
                }

            } else {

                resCode = response.responseCode;
            }

            if (resCode === 0) {

                callBackSuccess(response);

            } else if (resCode > -1) {
                
                callBackError({
                    data : {
                        responseText : response.responseText === undefined ? response.data.responseText : response.responseText
                    }
                });
                
            } else {

                errorCheckAndCallBackImpl(response, callBackError);
            }

        }, function(error) {

            errorCallBackImpl(error, callBackError);
        });
    };

    /*
     * If the HTML statusCode is not 200-Success
     */
    var errorCallBackImpl = function(error, callBackError) {

        if (error == undefined || error == null || error.data == undefined || error.data == null || error.data.responseText == undefined || error.data.responseText == null) {

            var errorData = {
                data : {
                    responseText : "Failure."
                }
            };

            callBackError(errorData);

        } else {

            callBackError(error);
        }
    };
    
    /*
     * If the HTML statusCode is 200-Success but the API response code is not success.
     * The error case will be called regardless the HTML status code.
     * Check for expired session too.
     */
    var errorCheckAndCallBackImpl = function(response, callBackError) {
        
        if (response !== undefined && response !== null && typeof response == 'object') {

            /*
             * The session probably expired, however, in this case we will receive statusCode-200 due to the spring security configuration. When the session is expired the
             * server will send the loginPage back instead of the expected result.
             */
            
            try {
                
                //check for the <!DOCTYPE html> element. If we found it we need to redirect as we received an HTML page as a response
                var pattern = new RegExp("\\\"[0-9]{1,2}\\\"\\:\\\"\\<\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"\\!\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"D\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"O\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"C\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"T\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"Y\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"P\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"E\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"\\s\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"h\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"t\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"m\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"l\\\"\\,\\\"[0-9]{1,2}\\\"\\:\\\"\\>\\\"\\,");
                var res = pattern.test(JSON.stringify(response));
                
                if (res) {
                    
                    //redirecting by refreshing the page
                    $window.location.href = "";
                    
                } else {
                
                    callBackError({
                        data : {
                            responseText : response.responseText
                        }
                    });
                }
                
            } catch (err) {
                
                callBackError({
                    data : {
                        responseText : response.responseText
                    }
                });
            }

        } else {

            callBackError({
                data : {
                    responseText : response.responseText
                }
            });
        }
    };

} ]);
