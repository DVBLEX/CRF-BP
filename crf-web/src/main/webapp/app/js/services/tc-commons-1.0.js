/*
 * Telclic Commons for AngularJS 1, Version 1.0 JS
 */
var tcCommons = angular.module("tcCommons", [ ]).service('tcCommonsService', [ '$sce', function($sce) {

    this.revokeResourceUrl = function(resourceURL) {
        URL.revokeObjectURL(resourceURL);
    };

    this.createResourceUrlFromBase64 = function(b64Data, contentType) {
        var fileData = this.b64toBlob(b64Data, contentType);
        var resourceURL = URL.createObjectURL(fileData);
        return resourceURL;

    };
    
    this.b64toBlob = function(b64Data, contentType, sliceSize) {
        
        contentType = contentType || '';
        sliceSize = sliceSize || 512;
        var byteCharacters = atob(b64Data);
        var byteArrays = [];

        for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
            
            var slice = byteCharacters.slice(offset, offset + sliceSize);

            var byteNumbers = new Array(slice.length);
            
            for (var i = 0; i < slice.length; i++) {
                
                byteNumbers[i] = slice.charCodeAt(i);
            }

            var byteArray = new Uint8Array(byteNumbers);

            byteArrays.push(byteArray);
        }

        var blob = new Blob(byteArrays, {
            type : contentType
        });
        return blob;
    };

} ]);
