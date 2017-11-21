 'use strict';
(function() {
    var ZXCVBN_SRC = 'app/app-directives/zxcvbn.js';

    var async_load = function() {
        var first, s;
        // create a <script> element using the DOM API
        s = document.createElement('script');

        // set attributes on the script element
        s.src = ZXCVBN_SRC;
        s.type = 'text/javascript';
        s.async = true; // HTML5 async attribute

        // Get the first script element in the document
        first = document.getElementsByTagName('script')[0];

        // insert the <script> element before the first in the document
        return first.parentNode.insertBefore(s, first);
    };

    // attach async_load as callback to the window load event
    if (window.attachEvent != null) {
        window.attachEvent('onload', async_load);
    } else {
        window.addEventListener('load', async_load, false);
    }
}).call(this);
    
app.filter('passwordCount', [function() {
    return function(value, peak) {
        value = angular.isString(value) ? value : '';
        peak = isFinite(peak) ? peak : 8;

        return value && (value.length > peak ? peak + '+' : value.length);
    };
}])
.factory('zxcvbn', [function() {
    return {
        score: function() {
        	if(zxcvbn){
                var compute = zxcvbn.apply(null, arguments);
                return compute && compute.score;
        	}
        }
    };
}])
.directive('okPassword', ['zxcvbn', function(zxcvbn) {
    return {
        // restrict to only attribute and class
        restrict: 'AC',

        // use the NgModelController
        require: 'ngModel',

        // add the NgModelController as a dependency to your link function
        link: function($scope, $element, $attrs, ngModelCtrl) {
            $element.on('blur change keydown', function(evt) {
                $scope.$evalAsync(function($scope) {
                    // update the $scope.password with the element's value
                    var pwd = $scope.password = $element.val();
                   
                    // resolve password strength score using zxcvbn service
                    $scope.passwordStrength = pwd ? (pwd.length >= 8 && zxcvbn.score(pwd) || 0) : null;
                    // define the validity criterion for okPassword constraint
                    ngModelCtrl.$setValidity('okPassword', $scope.passwordStrength >= 1);
                });
            });
        }
    };
}]);