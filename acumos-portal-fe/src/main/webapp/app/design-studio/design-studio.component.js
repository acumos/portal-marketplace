/*
===============LICENSE_START=======================================================
Acumos  Apache-2.0
===================================================================================
Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
===================================================================================
This Acumos software file is distributed by AT&T and Tech Mahindra
under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
This file is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
===============LICENSE_END=========================================================
*/

'use strict';
angular
    .module('designStudio',['ui.bootstrap'])
    .component(
        'designStudio',
        {
            templateUrl : './app/design-studio/design-studio.template.html',
            controller : DSController
        }
    );
DSController.$inject = ['$scope','$window','$rootScope','$mdDialog','$state','$injector','browserStorageService'];

function DSController($scope,$window,$rootScope,$mdDialog ,$state,$injector, browserStorageService) {
	componentHandler.upgradeAllRegistered();

	$scope.userDetails = JSON.parse(browserStorageService.getUserDetail());
	if($scope.userDetails === null){
		var modalService = $injector.get('$mdDialog'); 
		modalService.show({
			 templateUrl: '../app/header/sign-in-promt-modal-box.html',
			 clickOutsideToClose: true,
			 controller : function DialogController($scope ) {
				 $scope.closeDialog = function() {
					 modalService.hide();
					 $rootScope.showAdvancedLogin();
			    	 $state.go('home');
		     } }
			});
	} else{
		
		
		var pathArray = location.href.split( '/' );
	    var protocol = pathArray[0];
	    var host = pathArray[2];
	    var baseURL = protocol + '//' + host;
	    var qs = querystring.parse();
	    var urlBase = baseURL + '/workbench/';
	    var iframeEl = document.getElementById('workbenchLaunch');
	    
	    $scope.launchFlag = false;
		$scope.launchWorkbench = function(e){
			iframeEl.onload = function() { 
				window.setTimeout(function() {                                
					iframeEl.contentWindow.postMessage('iframeMsg', '*');
        		}, 500);
				
			};
			$scope.launchFlag = true;
			document.getElementById("workbenchLaunch").style.display="block";
			document.getElementById("workbenchLaunch").src = urlBase;
		}
		
		window.onmessage = function(event) {
			  iframeEl.parentNode.removeChild( iframeEl );
			  if (event.data === "navigateToMyModel") {
			    location.href = "/#/manageModule";
			  } else if(event.data === "navigateToDesignStudio"){
				  location.reload();
				  location.href = "/#/designStudio";
			  } else if(event.data === "navigateToAcuCompose"){
				  location.href = "/#/acuCompose";
			  } else if(event.data === "navigateToHome"){
				  location.href = "/#/home";
			  } 
			  
			};
	}
}
		
	
		