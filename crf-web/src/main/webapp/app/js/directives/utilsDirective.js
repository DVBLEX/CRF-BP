crfApp.directive('avoidSpecialChars', [function() {
    function link(scope, elem, attrs, ngModel) {
        ngModel.$parsers.push(function(viewValue) {
            var reg = /^[^!@#$%\&*()_+={}|[\]\\:;"<>?,./]*$/;
            if (viewValue.match(reg)) {
                return viewValue;
            }
            var transformedValue = ngModel.$modelValue;
            ngModel.$setViewValue(transformedValue);
            ngModel.$render();
            return transformedValue;
        });
    }

    return {
        restrict : 'A',
        require : 'ngModel',
        link : link
    };
}]);

crfApp.directive('avoidNumbers', [function() {
    function link(scope, elem, attrs, ngModel) {
        ngModel.$parsers.push(function(viewValue) {
            var reg = /^[^0-9]*$/;
            if (viewValue.match(reg)) {
                return viewValue;
            }
            var transformedValue = ngModel.$modelValue;
            ngModel.$setViewValue(transformedValue);
            ngModel.$render();
            return transformedValue;
        });
    }

    return {
        restrict : 'A',
        require : 'ngModel',
        link : link
    };
}]);

crfApp.directive('capitalize', [ function() {
    return {
        require : 'ngModel',
        link : function(scope, element, attrs, modelCtrl) {
            var capitalize = function(inputValue) {
                if (inputValue == undefined)
                    inputValue = '';
                var capitalized = inputValue.toUpperCase();
                if (capitalized !== inputValue) {
                    // see where the cursor is before the update so that we can set it back
                    var selection = element[0].selectionStart;
                    modelCtrl.$setViewValue(capitalized);
                    modelCtrl.$render();
                    // set back the cursor after rendering
                    element[0].selectionStart = selection;
                    element[0].selectionEnd = selection;
                }
                return capitalized;
            }
            modelCtrl.$parsers.push(capitalize);
            capitalize(scope[attrs.ngModel]);
        }
    };
} ]);

crfApp.directive('alphanumeric', function() {
    return {
        restrict : 'A',
        require : 'ngModel',
        link : function(scope, element, attr, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                var reg = new RegExp('^[a-zA-Z0-9]*$');
                if (reg.test(viewValue)) {
                    return viewValue;
                } else {
                    var overrideValue = (reg.test(ctrl.$modelValue) ? ctrl.$modelValue : '');
                    element.val(overrideValue);
                    return overrideValue;
                }
            });
        }
    };
});

crfApp.directive('tooltipLoader', [function() {
    return function(scope, element, attrs) {
        element.tooltip({
            trigger: "hover click",
            placement: "bottom"
        });
    };
} ]);

crfApp.directive('formatAmount', ['$filter', function ($filter) {
    return {
        require: '?ngModel',
        link: function (scope, elem, attrs, ctrl) {
            if (!ctrl) return;

            ctrl.$formatters.unshift(function (a) {
                return $filter(attrs.formatAmount)(ctrl.$modelValue, "")
            });

            ctrl.$parsers.unshift(function (viewValue) {
                              
                elem.priceFormat({
                    prefix: '',
                    centsSeparator: '.',
                    thousandsSeparator: ',',
                    centsLimit: 2
                });                
                                 
                return elem[0].value;
            });
        }
    };
}]);