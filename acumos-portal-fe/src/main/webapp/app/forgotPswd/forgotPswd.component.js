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
				                	 debugger;
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
