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
		.module('forgotPswd',['ui.bootstrap'])
		.component(
				'forgotPswd',
				{
					templateUrl : './app/forgotPswd/forgotPswd.template.html',
					controller : function($scope, $http, $location) {
						
						if(localStorage.getItem('loginPassExpire') == ''){
							$scope.forgotPassword = true;
						}
						else $scope.expiredPassword = true;
						
						$scope.forgotPasswd = function(){
							if($scope.forgotPswd.$valid){
								
								//
								var dataObj = {
						        		  "request_body": {
						        			  				"emailId" : $scope.uemail
						        		  				}
						        		}
								$http({ method : 'PUT',
				                       url : '/api/users/forgetPassword',
				                       data : dataObj
				                 }).success(function(data, status, headers,config) {
				                	 if(data.error_code === "100"){
				                		 alert("Temporary Passpword is send on your email id");
											window.location = 'index.html#/home'; 
				                	 }
				                	 else alert("Email id not registered");
				                 }).error(function(data, status, headers, config) {
				                	 
				                 });
							}
						}
					}
				});
