/**
 * To check width of the screen when resized and hide and show items according to the width.
 * Made for responsive nature of the portal.
 * USE:
 * 		<div my-directive>
 *    		<p ng-if="width > 768" >This will show if screen width is more than 768 {{width}}</p>
 *      	<p  ng-if="width < 320">This will show if screen width is more than 320 {{width}}</p>  
 *	   	</div>
 */

app.directive('windowResize', ['$window', function ($window) {
	/*debugger*/
     return {
        link: link,
        restrict: 'A'           
     };
     function link(scope, element, attrs){
        scope.width = $window.innerWidth;
        
            function onResize(){
                console.log($window.innerWidth);
                // uncomment for only fire when $window.innerWidth change   
                if (scope.width !== $window.innerWidth)
                {
                    scope.width = $window.innerWidth;
                    scope.$digest();
                }
            };

            function cleanUp() {
                angular.element($window).off('resize', onResize);
            }

            angular.element($window).on('resize', onResize);
            scope.$on('$destroy', cleanUp);
     }    
 }]);