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
	controller : function($scope, $state, $timeout, $rootScope, $window, $http, $location, apiService) {
		$scope.isActive = function (viewLocation) {
		     //var active = (viewLocation === $location.path());
		     return (viewLocation === $location.path());
		};
		$scope.showQandAUrl = false;
		$scope.qAndAUrl = '';
		$scope.userRoleAdmin = false;
		
		apiService.getQandAUrl().then( function(response){
			$scope.qAndAUrl = response.data.response_body;
			if(sessionStorage.getItem("auth_token")!='')
				$scope.showQandAUrl = true;
		});
		/* if(localStorage.getItem("HeaderNameVar")=="manageModule"){
			 $scope.sideBar=true; 
		 }else{
			 $scope.sideBar=false;  
		 }
		
		 
		$scope.provider = sessionStorage.getItem("provider");
		$scope.getTemplate = function () {
			var temp="";
			if(localStorage.getItem("virtualPageName")=='ManageModule'){
				temp='/app/header/header-manage.html';
			}else{
				temp='/app/header/header-nav.template.html';
			}
            return temp;
       }
		$scope.login = function() {
			$scope.successfulLogin = false;
			$scope.successfulLoginMsg = false;
			$scope.successfulLoginSigninSignup = true;
			$scope.successfulAdmin = false;
			$scope.showAltImage = true;
			
			if (sessionStorage.getItem("userDetail") == ""
					|| sessionStorage.getItem("userDetail") == undefined) {
			} else if (JSON.parse(sessionStorage.getItem("userDetail"))) {
				$scope.userDetails = JSON.parse(sessionStorage
						.getItem("userDetail"));
				$scope.userDetails.userName = $scope.userDetails[0];
				$scope.userDetails.userId = $scope.userDetails[1];
				$scope.userAdmin = $scope.userDetails[2];
				console.log("GET LOCAL: ", JSON.parse(sessionStorage
						.getItem("userDetail")));
				$scope.successfulLogin = true;
				$scope.successfulLoginMsg = true;
				$scope.successfulLoginSigninSignup = false;
				$scope.successfulLoginMsg = false;
				
                //TODO Need to change
				apiService.getUserRole($scope.userDetails.userId)
				.then( function(response){
					console.log("User Roles : ", response);
					if (response.data.status){
						for(var i=0;i<response.data.response_body.length;i++){
							var userRole = response.data.response_body[i]
							if(userRole.name == "Admin"){
								$scope.successfulAdmin = true;
								
							}
						}
					}
				},function(error){
					
				});
				
				
				//get User image
				function getUserImage(){
					var req = {
						    method: 'Get',
						    url: '/api/users/userProfileImage/' + $scope.userDetails.userId
						};
					$http(req).success(function(data, status, headers,config) {
						if(data.status){
						    $scope.userImage = data.response_body;
						    $scope.showAltImage = false;
						}
					}).error(function(data, status, headers, config) {
						
					});
				};
				getUserImage();
				
			}

			$scope.$on('transferUp', function(event, data) {
				//console.log('on working');
				$scope.emitedmessage = data.message;
				$scope.userfirstname = data.username;

				if ($scope.emitedmessage == 'true'
						|| $scope.emitedmessage == true) {
					$scope.successfulLogin = true;
					$scope.successfulLoginMsg = true;
					$scope.successfulLoginSigninSignup = false;
					if($state) {
						//console.log($state);
					}
					$state.go("manageModule");
					$timeout(function() {
						$scope.successfulLoginMsg = false;
					}, 2000);

				}
			});
		}
		$rootScope.$on("CallLoginMethod", function(){
            $scope.login();
            //console.log("CallLoginMethod");
        });
		
		$scope.login();
		
		$scope.logout = function() {
			sessionStorage.setItem("userDetail", "");
			sessionStorage.removeItem("userDetail");
			localStorage.removeItem("soluId");
			localStorage.removeItem("solutionId");
			sessionStorage.removeItem("provider");
			$scope.successfulLoginSigninSignup = true;
			$scope.successfulLogin = false;
			$scope.successfulAdmin = false;
			$scope.showAltImage = true;
			$state.go("home");
		}
		//Emit value from deactivate user
		$scope.$on("MyLogOutEvent", function(evt,data){ 
			$scope.logout();
		});
		
		$scope.successfulSignUpMsg = false;
		$scope.$on('signUpSuccessful', function(event, data) {
			//console.log("Sign up headernavcomponent");
			$scope.successfulSignUpMsg = true;
			$timeout(function() {
				$scope.successfulSignUpMsg = false;
			}, 5500);
		});
		
		$scope.globalSearch = function() {
			$rootScope.valueToSearch = $scope.search;
			$rootScope.$broadcast('scanner-started', {
				searchValue : $scope.search
			});
			$window.location.href = '/index.html#/marketPlace';
		}
		//Notification functionlity
		$scope.notification = null;
		//Emit for notification
		$scope.$on("notification", function(evt,data){ 
			
		});
	*/
		//Check if user is admin

		if(sessionStorage.getItem("userRole") == 'Admin')$scope.userRoleAdmin = true;
		
		$scope.$on('roleCheck', function() {
			$scope.userRoleAdmin = true;
		});
		
		//Hide show of sidebar in mobile device
		$scope.hamberClick = function(){$rootScope.$broadcast('menuClickToggle');}
	}
});