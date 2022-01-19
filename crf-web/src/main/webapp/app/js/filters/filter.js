crfApp.filter('to_trusted_html', [ '$sce', function($sce) {
    return function(text) {
        return $sce.trustAsHtml(text);
    };
} ]);