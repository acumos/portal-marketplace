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

angular
		.module('resetPswd')
		.component(
				'resetPswd',
				{
					templateUrl : '/app/header/resetPswd.template.html',
					controller : function($scope,$compile, $location, $http) {
						//$scope.userid = sessionStorage.getItem("SessionName");
					
					$scope.changePswd = function(){
						if($scope.resetPswd.$invalid){return}
						//API CALL
						$scope.userDetails = JSON.parse(sessionStorage.getItem("userDetail"));
						//var userId = sessionStorage.getItem("userDetail");
						//console.log(sessionStorage.getItem("userDetail"));
						var req = {
							    method: 'PUT',
							    url: '/api/users/changePassword',
							    data:{
							    	"userId":$scope.userDetails[1],
							    	"oldPassword":$scope.oldPswd,
							    	"newPassword":$scope.newPswd
							    },
							    headers: {'Content-Type': 'application/json'}
							}
						//console.log(angular.toJson(req));
						//console.log(angular.toJson(req.data));
						$http(req).success(function(data, status, headers,config) {
							alert("Your password updated successfully")
		                	 $scope.ribbonMsg = "Your Password is updated successfully";
		                	 $scope.ribbonShow = true;
		                	 $location.path('/marketPlace')
		                     }).error(function(data, status, headers, config) {
		                       // called asynchronously if an error occurs
		                       // or server returns response with an error status.
		                    	 $scope.ribbonShow = true;
		                    	 $scope.ribbonMsg = "Your Password is not updated";
		                       //console.log(status);
		                 });
					};
				
					}
				});
