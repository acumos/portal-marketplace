/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
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

app.component('sidebarNav',{
	templateUrl : '/app/header/sidebar-nav.template.html',
	controller : function($scope, $state, $timeout, $rootScope, $window, $http, $location, apiService, browserStorageService) {
		
		$scope.dropdownDS = function(){
			if (document.getElementById("ds-nav-sub-menu").style.display === "block") {
				$('#ds-nav-menu').removeClass('active');
				document.getElementById("ds-nav-sub-menu").style.display = "none";
			} else {
				$('#ds-nav-menu').addClass('active');
				document.getElementById("ds-nav-sub-menu").style.display = "block";
			}
		}
		
		$scope.dropModelBuilder = function(){
			if (document.getElementById("ds-nav-modelB-sub").style.display === "block") {
				$('#ds-nav-modelB').removeClass('selected-item');
				document.getElementById("ds-nav-modelB-sub").style.display = "none";
			} else {
				$('#ds-nav-modelB').addClass('selected-item');
				document.getElementById("ds-nav-modelB-sub").style.display = "block";
			}
		}
		
		$scope.dropModelComposer = function(){
			if (document.getElementById("ds-nav-modelC-sub").style.display === "block") {
				$('#ds-nav-modelC').removeClass('selected-item');
				document.getElementById("ds-nav-modelC-sub").style.display = "none";
			} else {
				$('#ds-nav-modelC').addClass('selected-item');
				document.getElementById("ds-nav-modelC-sub").style.display = "block";
			}
		}
		
		/*var pathArray = location.href.split( '/' );
	    var protocol = pathArray[0];
	    var host = pathArray[2];
	    var baseURL = protocol + '//' + host;
	    var urlBase = baseURL + '/jupyter/'; */
	    
	    $scope.showJupyterUrl = false;
		$scope.jupyterUrl = '';
		
		apiService.getJupyterUrl().then( function(response){
			$scope.jupyterUrl = response.data.response_body;
			if(browserStorageService.getAuthToken()!='')
				$scope.showJupyterUrl = true;
		});
		
		$scope.isActive = function (viewLocation) {
		     //var active = (viewLocation === $location.path());
		     return (viewLocation === $location.path());
		};
		$scope.showQandAUrl = false;
		$scope.qAndAUrl = '';
		
		apiService.getQandAUrl().then( function(response){
			$scope.qAndAUrl = response.data.response_body;
			if(browserStorageService.getAuthToken()!='')
				$scope.showQandAUrl = true;
		});

		//Hide show of sidebar in mobile device
		$scope.hamberClick = function(){$rootScope.$broadcast('menuClickToggle');}
	}
});