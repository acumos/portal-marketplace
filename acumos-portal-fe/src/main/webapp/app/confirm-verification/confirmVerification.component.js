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
		.module('confirmVerification')
		.component(
				'confirmVerification',
				{
					templateUrl : './app/confirm-verification/confirmVerification.template.html',
					controller : function($scope, $http, $stateParams) {
						
						$scope.verificationMessage = "Verifying Your Account....";
						$scope.resend = false;
						
						$scope.confirmVerification = function(){
								
								var dataObj = {
						        		  "request_body": {
						        			  				"loginName" : $stateParams.user,
						        			  				"verifyToken" : $stateParams.token
						        		  				}
						        		}
								$http({ method : 'POST',
				                       url : '/api/users/verifyUser',
				                       data : dataObj
				                 }).success(function(data, status, headers,config) {
				                	  $scope.verificationMessage = "Your Account has been activated. Please sing-in to portal.";
				                 }).error(function(data, status, headers, config) {
				                	 //alert("Token Not verified")
				                	 $scope.verificationMessage = "The token is invalid or expired. Click below button to regenerate verification email.";
				                	 $scope.resend = true;
				                 });
						}
						$scope.confirmVerification();
						
						$scope.regenerateVerifyToken = function(){
							
							//
							var dataObj = {
					        		  "request_body": {
					        			  				"loginName" : $stateParams.user
					        		  				}
					        		}
							$http({ method : 'POST',
			                       url : '/api/users/resendVerifyToken',
			                       data : dataObj
			                 }).success(function(data, status, headers,config) {
			                	  //alert("Token has been sent to your registered email.");
			                	  $scope.verificationMessage = "New verification email has been sent to your registered email address.";
			                	  $scope.resend = true;
			                 }).error(function(data, status, headers, config) {
			                	 $scope.verificationMessage = "Cannot generate the verification email.";
			                	 $scope.resend = true;
			                	 //alert("unable to send verification email")
			                 });
						}
					}
				});