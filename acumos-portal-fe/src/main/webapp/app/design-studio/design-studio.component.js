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
DSController.$inject = ['$scope','$window','$rootScope','$mdDialog','$state','$injector','browserStorageService','apiService'];

function DSController($scope,$window,$rootScope,$mdDialog ,$state,$injector, browserStorageService, apiService) {
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
		apiService.getDSMenu().then(
				function successCallback(response) {
					var body = response.data.response_body;
					console.log("DS,",body);
					$scope.isWorkbenchActive = body.workbenchActive;
					$scope.isAcucomposeActive = body.acucomposeActive;
					$scope.blocks = body.blocks.filter(function(item) {
						return item.active;
					});
				}, function errorCallback(error) {
					console.error(error);
				});
		
		$scope.openBlockUrl = function(url) {
			window.open(url, '_blank');
		};
		
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
			iframeEl.parentNode.removeChild(iframeEl);
			if (event.data === "navigateToMyModel") {
				location.href = "#/manageModule";
			} else if (event.data === "navigateToMarketplace") {
				location.href = "#/marketPlace";
			} else if (event.data === "navigateToDesignStudio") {
				location.reload();
				location.href = "/index.html#/designStudio";
			} else if (event.data === "navigateToAcuCompose") {
				location.href = "/index.html#/acuCompose";
			} else if (event.data === "navigateToHome") {
				location.href = "/index.html#/home";
			} else {
				let childMessage = event.data.split("?");
				if (childMessage[0] === "navigateToMyModelDetails") {
					let solutionId = childMessage[1];
					let revisionId = childMessage[2];
					location.href = "/index.html#/marketSolutions?solutionId="
							+ solutionId + "&revisionId=" + revisionId
							+ "&parentUrl=mymodel";
				}
			}

		};
	}
}
		
	
		