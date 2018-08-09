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
		.module('userDetail', [ 'ui.bootstrap' , 'ngFileUpload'])
		.directive(
				'uploadImageModel',
				function($parse) {
					return {
						restrict : 'A', // the directive can be used as an
										// attribute only

						/*
						 * link is a function that defines functionality of
						 * directive scope: scope associated with the element
						 * element: element on which this directive used attrs:
						 * key value pair of element attributes
						 */
						link : function(scope, element, attrs) {
							var model = $parse(attrs.uploadImageModel), modelSetter = model.assign; // define
																									// a
																									// setter
																									// for
																									// demoFileModel

							// Bind change event on the element
							element.bind('change', function() {
								// Call apply on scope, it checks for value
								// changes and reflect them on UI
								scope.$apply(function() {
									// set the model value
									
									var size = element[0].files[0].size;
				                	/*if(size >= 800000){
				    	            	scope.imageError = true;
				    	            	modelSetter(scope, "");
				    	            	element.val("");
				    	            	return true;
				    	            }*/
				    	            scope.imageError = false;
				    	            
				    	            /*Error check for cobranding logo in admin*/
				    	            scope.coBrandingLogoError = true;
				    	            var coBrandLogo = element[0].files[0];
				    	            if(coBrandLogo){
				    	            	var validFormats = ['jpg','jpeg','png','gif'];
				    	            	var fileName = coBrandLogo.name;
              							var ext = fileName.split('.').pop();
              							
              							if(validFormats.indexOf(ext) == -1){
               				            	scope.coBrandingLogoError = true;
               				            	scope.showBrandingLogoExtError = true;
               				            }else{
               				            	scope.coBrandingLogoError = false;
               				            	scope.showBrandingLogoExtError = false;
               				            }
				    	            	
				    	            }
				    	            
				    	            modelSetter(scope, element[0].files[0]);
				    	            //return true;
								});
							});
						}
					}
				})
		.service('userImageUploadService', function($http, $q) {

			this.uploadFileToUrl = function(file, uploadUrl) {
				// FormData, object of key/value pair for form fields and values
				var fileFormData = new FormData();
				fileFormData.append('userImage', file);

				var deffered = $q.defer();
				$http.post(uploadUrl, fileFormData, {
					transformRequest : angular.identity,
					headers : {
						'Content-Type' : undefined
					}

				}).success(function(response) {
					deffered.resolve(response);

				}).error(function(response) {
					deffered.reject(response);
				});

				return deffered.promise;
			}
		})
		.component(
				'userDetail',
				{
					templateUrl : './app/userDetail/userDetail.template.html',
					controller : function($scope, $http, $location, $rootScope, $timeout,
							userImageUploadService, $q, $window, apiService, $mdDialog, $anchorScroll, browserStorageService) {
						//$scope.matchString = true;
						$scope.showAltImage = true;
						$scope.disableEmail = true;
						
						if(browserStorageService.getUserDetail()){
							var userName = JSON.parse(browserStorageService.getUserDetail())[0];
							var userId = JSON.parse(browserStorageService.getUserDetail())[1];
						}
						getUserDetail();
						
						/*check if user is of LF*/
						$scope.isLfUser = false;
						$scope.sessionLFCAS = sessionStorage.getItem('provider');
						if($scope.sessionLFCAS == "LFCAS"){
							$scope.isLfUser = true;
						}else{
							$scope.isLfUser = false;
						}

						// Get User data
						
						$scope.moveTo = function(id)
				        {   //adding a scroll effect
							angular.element('.mdl-tabs__tab').removeClass('is-active');
				         	angular.element(document.querySelector('.' + id + '-link')).addClass('is-active')
				            $location.hash(id); 
				            $anchorScroll();
				        }   
						
						// get Image Size
						 function getImageSize(){
							var req = {
								method : 'Get',
								url : '/api/users/imagesize'
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												if (data.status) {													
													$scope.imageSize = data.response_body;													
												}
											}).error(
											function(data, status, headers,
													config) {
												console.log(data);
											});
						}
						getImageSize();
						
						// get User image
						 function getUserImage (){
							var req = {
								method : 'Get',
								url : '/api/users/userProfileImage/' + userId
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												if (data.status) {
													//$scope.userImage = data.response_body;
													$scope.userPic = data.response_body; //$scope.userImage;
													$scope.showAltImage = false;
												}
											}).error(
											function(data, status, headers,
													config) {

											});
						}
						 
						 $scope.refreshApiToken = function (){
						
							var dataObj = {
					        		  "request_body": {
					        							"userId" : userId
					        		  				}
					        		}
							$http({ method : 'POST',
			                       url : '/api/users/refreshApiToken',
			                       data : dataObj
			                 }).success(function(data, status, headers,config) {
			                	getUserDetail()
			                 }).error(function(data, status, headers, config) {
			                 });
						 }
						 
						 function getUserDetail() {
							var req = {
								method : 'POST',
								url : '/api/users/userAccountDetails',
								data : {
									"request_body" : {
										"userId" : userId
									}
								},
								headers : {
									'Content-Type' : 'application/json'
								}
							};
							$http(req).success(
									function(data, status, headers, config) {
										console.log(data);
										$scope.user = data.response_body;
										$scope.userCopy = angular.copy($scope.user);
										$scope.userActive = angular
												.copy(data.response_body);
									}).error(
									function(data, status, headers, config) {

									});
							getUserImage();
						}

						// Change password function
						$scope.changePswd = function() {
							var validCheck = false;
							if ($scope.resetPswd.$invalid) {
								validCheck = true;
								//alert("Enter all fields");
								$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                $anchorScroll(); 
                                $scope.msg = "Enter all fields."; 
                                $scope.icon = 'report_problem';
                                $scope.styleclass = 'c-warning';
                                $scope.showAlertMessage = true;
                                $timeout(function() {
                                	$scope.showAlertMessage = false;
                                }, 2000);
								return;
							}
							if (validCheck == false
									&& $scope.oldPswd == $scope.newPswd) {
								//alert("Old and New password matches");
								$mdDialog.hide();
								$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                $anchorScroll(); 
                                $scope.msg = "Old and New password should not match"; 
                                $scope.icon = 'report_problem';
                                $scope.styleclass = 'c-warning';
                                $scope.showAlertMessage = true;
                                $timeout(function() {
                                	$scope.showAlertMessage = false;
                                }, 2000);
								$scope.oldPswd = '';
								$scope.newPswd = false;
								$scope.cpwd = '';
								return;
							} else
								validCheck = false;
							// API CALL
							if (validCheck == false) {
								
								$scope.userDetails = {
										"userId" : userId,
										"oldPassword" : $scope.oldPswd,
										"newPassword" : $scope.newPswd
									}
								
								// var userId = check[1];
								/*var req = {
									method : 'PUT',
									url : 'api/users/changePassword',
									data : {
										"userId" : userId,
										"oldPassword" : $scope.oldPswd,
										"newPassword" : $scope.newPswd
									},
									headers : {
										'Content-Type' : 'application/json'
									}
								}*/
								// console.log(angular.toJson(req));
								//console.log($scope.userDetails);

								apiService
										.updateUserPass($scope.userDetails)
										.then(
												function(response) {
													if(response.data.error_code == 204 && response.data.response_detail == "Old password does not match")
														{
															//alert(response.data.response_detail);
															$mdDialog.hide();
															$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
							                                $anchorScroll(); 
							                                $scope.msg = response.data.response_detail; 
							                                $scope.icon = 'report_problem';
							                                $scope.styleclass = 'c-warning';
							                                $scope.showAlertMessage = true;
							                                $timeout(function() {
							                                	$scope.showAlertMessage = false;
							                                }, 2000);
														}
														else {
															//alert("Password updated");
															$mdDialog.hide();
															$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
							                                $anchorScroll(); 
							                                $scope.msg = "Password updated"; 
							                                $scope.icon = '';
							                                $scope.styleclass = 'c-success';
							                                $scope.showAlertMessage = true;
							                                $timeout(function() {
							                                	$scope.showAlertMessage = false;
							                                	$rootScope.$broadcast("MyLogOutEvent",{ "request_body" : $scope.user});
							                                }, 2000);
															
														}
													$scope.closePoup();
													
													$scope.oldPswd = '';
													$scope.newPswd = false;
													$scope.cpwd = '';

												},
												function(error) {
													/*if(error.data.response_detail == "Failed"){
														alert("Old password does not match")
													}
													else{
														alert("Error "+ error.data.response_detail)
													}*/
												});

								/*
								 * $http(req).success(function(data, status,
								 * headers,config) {  alert("Password
								 * updated successfully");
								 * $('#myModal').modal('toggle');
								 * //$location.path('/marketPlace')
								 * }).error(function(data, status, headers,
								 * config) { // called asynchronously if an
								 * error occurs // or server returns response
								 * with an error status. alert("Id/Password does
								 * not match");
								 * 
								 * //$scope.resetPswd.$setValidity(true);
								 * console.log($scope.resetPswd);
								 * $scope.resetPswd.$dirty= false;
								 * $scope.resetPswd.$valid= true;
								 * $scope.resetPswd.$valid= true; $scope.oldPswd =
								 * ''; $scope.newPswd = ''; $scope.cpwd = '';
								 * $scope.resetPswd.$setPristine();
								 * $scope.resetPswd.$setUntouched();
								 * $scope.resetPswd.$error = {};
								 * $scope.resetPswd.pwd.$setValidity("password",
								 * false);
								 * $scope.resetPswd.pwd.$setValidity("required",
								 * false); $scope.resetPswd.pwd.$error = {};
								 * //console.log(status); });
								 */

							}

						};
						// Match password
						$scope.matchPswd = function() {
							$scope.matchString = true;
							if ($scope.newPswd === $scope.cpwd) {
								$scope.matchString = false;
							}
						}
						$scope.oldPswdShow = 'Show';
						$scope.newPswdShow = 'Show';
						$scope.showNewPassword = false;
						$scope.showOldPassword = false;
						// Password hide/show on change password
						$scope.showPasswd = function(value) {
							if (value == "new") {
								if ($scope.showNewPassword == true) {
									$scope.showNewPassword = false;
									$scope.newPswdShow = 'Show';
								} else {
									$scope.showNewPassword = true;
									$scope.newPswdShow = 'Hide';
								}

							}
							if (value == "old") {
								if ($scope.showOldPassword == true) {
									$scope.showOldPassword = false;
									$scope.oldPswdShow = 'Show';
								} else {
									$scope.showOldPassword = true;
									$scope.oldPswdShow = 'Hide';
								}
							}
						}
						// Update User Detail
						$scope.imageurl = "images/profile-icon-01-48X48.png";
						
						// Update/Deactivate account
						
						/*Deactivate Account Confirmation Pop-up*/
						$scope.dialogDeactivateAccount = function(ev) {
		                	  $scope.error = false;
			                	$mdDialog.show({
			                      contentElement: '#dialogDeactivateAccount',
			                      parent: angular.element(document.body),
			                      targetEvent: ev,
			                      clickOutsideToClose: true
			                    });
						};
						
						/*Deactivate Account*/
						$scope.updateDeactivate = function(value) {
							
							if (value === "deactivate") {
								var json = {};
								$scope.userActive.emailId = $scope.userActive.email;
								$scope.userActive.username = $scope.userActive.loginName;
								$scope.userActive.active = "N";
								json = {
									"request_body" : $scope.userActive
								};
								console.log(json);
								var req1 = {
									method : 'PUT',
									url : '/api/users/updateUser',
									data : json,
									headers : {
										'Content-Type' : 'application/json'
									}
								};
								$http(req1)
										.success(
												function(data, status, headers,
														config) {
													//alert("User deactivated successfully..");
													$mdDialog.hide();
													$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
					                                $anchorScroll(); 
					                                $scope.msg = "User deactivated successfully."; 
					                                $scope.icon = '';
					                                $scope.styleclass = 'c-success';
					                                $scope.showAlertMessage = true;
					                                $timeout(function() {
					                                	$scope.showAlertMessage = false;
					                                	$rootScope.$broadcast(
																"MyLogOutEvent",
																data);
					                                }, 4000);
													/*$rootScope.$broadcast(
															"MyLogOutEvent",
															data);*/
												}).error(
												function(data, status, headers,
														config) {
													getUserDetail();
													alert('fail')
												});
							} else if (value === "update") {
								$scope.user.emailId = $scope.user.email;
								$scope.user.username = $scope.user.loginName;
								if(!$scope.showAltImage){
									//var picFile = new Blob([$scope.userImage], {type: 'image/PNG'});
									$scope.user.picture=$scope.userImage;	
								}
								if ($scope.user.active == false) {
									$scope.user.active = "N"
								} else
									$scope.user.active = "Y";
								var data = {
									"request_body" : $scope.user
								};
								console.log(data)
								var req = {
									method : 'PUT',
									url : '/api/users/updateUser',
									data : data,
									headers : {
										'Content-Type' : 'application/json'
									}
								};
								$http(req)
										.success(
												function(data, status, headers,
														config) {
													
													//alert('User detail updated successfully..')
													$mdDialog.hide();
													$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
					                                $anchorScroll(); 
					                                $scope.msg = 'User detail updated successfully..'; 
					                                $scope.icon = '';
					                                $scope.styleclass = 'c-success';
					                                $scope.showAlertMessage = true;
					                                $timeout(function() {
					                                	$scope.showAlertMessage = false;
					                                }, 2000);
													if( $scope.user.firstName != $scope.userCopy.firstName ){

														if (JSON.parse(browserStorageService.getUserDetail())) {
															var userDetails = JSON.parse(browserStorageService.getUserDetail())
															userDetails[0] = $scope.user.firstName;
															sessionStorage.setItem("userDetail", JSON.stringify(userDetails));
														}
														
														$rootScope.$broadcast('userDetailsChanged');
													}
												})
										.error(
												function(data, status, headers,
														config) {
													getUserDetail();
													alert('User detail update fail')
												});
							}

						}
						
					 $scope.saveEmail = function() {	
						$scope.userCopy.emailId = $scope.user.email;
						$scope.userCopy.username = $scope.user.loginName;
						
						if ($scope.userCopy.active == false)
							$scope.userCopy.active = "N"
						else
							$scope.userCopy.active = "Y";
						
						var data = {
							"request_body" : $scope.userCopy
						};
						console.log(data)
						var req = {
							method : 'PUT',
							url : '/api/users/updateUser',
							data : data,
							headers : {
								'Content-Type' : 'application/json'
							}
						};
						$http(req)
								.success(
										function(data, status, headers,
												config) {
											getUserDetail();
											//alert('User email updated successfully..')
											$mdDialog.hide();
											$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
			                                $anchorScroll(); 
			                                $scope.msg = 'User email updated successfully..'; 
			                                $scope.icon = '';
			                                $scope.styleclass = 'c-success';
			                                $scope.showAlertMessage = true;
			                                $timeout(function() {
			                                	$scope.showAlertMessage = false;
			                                	$rootScope.$broadcast(
														"MyLogOutEvent",
														data);
			                                }, 2000);
											/*$rootScope.$broadcast(
													"MyLogOutEvent",
													data);*/
										})
								.error(
										function(data, status, headers,
												config) {
											getUserDetail();
											alert('User email update fail')
										});
					}

						// Upload Image
					 	$scope.extensionError = false;
					 	$scope.nullFileError = true;
					 	$scope.sizeError = false;
					 	$scope.disableUsrImgBtn = true;
					 	$scope.checkValid = function(){
					 		var file = $scope.userImage;
							var fileFormData = new FormData();
							var validFormats = ['jpg','jpeg','png','gif'];
							var fileName = file.name;
							var ext = fileName.split('.').pop(); 
				            var size = bytesToSize(file.size);
				           
				            if(validFormats.indexOf(ext) == -1){
				            	$scope.extensionError = true;
				            	$scope.disableUsrImgBtn = true;
				            }else if(fileName == '' || fileName == undefined || fileName == null){
				            	$scope.nullFileError = true;
				            	$scope.disableUsrImgBtn = true;
				            }else if (size > $scope.imageSize){
				            	$scope.sizeError = true;
				            	$scope.disableUsrImgBtn = true;
				            }
			            	else{
				            	$scope.extensionError = false;
							 	$scope.nullFileError = false;
							 	$scope.sizeError = false;
							 	$scope.disableUsrImgBtn = false;
				            }
					 	}
					 	
					 	function bytesToSize(bytes) {
					 	   var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
					 	   if (bytes == 0) return '0 Byte';
					 	   var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
					 	   return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
					 	};
						$scope.uploadImg = function(){
							var file = $scope.userImage;
							var fileFormData = new FormData();
							var validFormats = ['jpg','jpeg','png','gif'];
							var fileName = file.name;
							var ext = fileName.split('.').pop();//substr($('#userImage').value.lastIndexOf('.')+1);
				            var size = file.size;
				           
				            if(validFormats.indexOf(ext) == -1 || size >= 800000){
				            	$scope.error = true;
				                //return value;
				            }else{
				            //validImage(true);
				            $scope.error = false;
				            
							fileFormData.append('file', file);
							fileFormData.append('userId', userId);

							console.log("User Id : " + userId);
							var uploadUrl = "api/users/updateUserImage/"
									+ userId;
							var promise = userImageUploadService.uploadFileToUrl(
									file, uploadUrl);

							promise
									.then(
											function(response) {
												
												$scope.serverResponse = response;
												$scope.closePoup();
												getUserImage();
												$window.location.reload();
											},
											function() {
												$scope.serverResponse = 'An error has occurred';
											})
				            }
				        }
						
						$scope.openChangePassword = function(){
							document.getElementsByName("resetPswd")[0].reset();
							$scope.resetPswd.$setPristine(true);
							$scope.resetPswd.$setValidity();
							$scope.resetPswd.$setUntouched();
							$scope.matchString = false;
						};
						
						$scope.closeChangePhoto = function(){
							$scope.error = false;
							$('#userImage').val('');
						};
						//Popup implementation
						$scope.showPrerenderedDialog = function(ev) {
		                	$mdDialog.show({
		                      contentElement: '#myDialog',
		                      parent: angular.element(document.body),
		                      targetEvent: ev,
		                      clickOutsideToClose: true
		                    });
		                  };
		                  $scope.uploadPhoto = function(ev) {
		                	  $scope.error = false;
			                	$mdDialog.show({
			                      contentElement: '#myDialogPhoto',
			                      parent: angular.element(document.body),
			                      targetEvent: ev,
			                      clickOutsideToClose: true
			                    });
			                  };
		                  $scope.closePoup = function(){
		                	  $mdDialog.hide();
		                	 $scope.userImage = "";
		                	 $scope.extensionError = false;
		                	 $scope.sizeError = false;
		                	 angular.element('#userImage').val('');
		                	 $scope.nullFileError = true;
		                   	 $scope.uploadImage.$setPristine();
		                     $scope.uploadImage.$setUntouched();
		                    
		                  }
		                  
		                  /**** Notification Preference Start****/
                          $scope.notificationPriority = [
                                                             {
                                                                 "prName": "LO",
                                                                 "prValue": "Low"
                                                             },
                                                             {
                                                                 "prName": "ME",
                                                                 "prValue": "Medium"
                                                            },
                                                             {
                                                                "prName": "HI",
                                                                 "prValue": "High"
                                                             },
                                                         ];
                          
                          
                         /* $scope.checkNotificationPriority = function(prVal){
                              $scope.notificationPriority = prVal;
                              console.log("$scope.notificationPriority :",$scope.notificationPriority);
                          }*/
                          
                          //get notification pref
                          $scope.getNotificationPref = function(){

								apiService
										.getUserNotificationPref(userId)
										.then(
												function(response) {
													
													if(response.data.response_body.length > 0){
														$scope.userNotificationPref = response.data.response_body[0];
													}

												},
												function(error) {
													
												});
                          }
                          $scope.getNotificationPref();
                          
                          $scope.putNotificationPref = function(){
                        	  
                        	  if($scope.userNotificationPref.userNotifPrefId){
                        		  
                        		  var notification_req_body = {
            								"request_body" : {
            								    "msgSeverityCode": $scope.userNotificationPref.msgSeverityCode,
            								    "notfDelvMechCode": "EM",
            								    "userId": userId,
            								    "userNotifPrefId": $scope.userNotificationPref.userNotifPrefId
            								}
            							}
                        		  
                        		 apiService
									.updateNotificationPref('update', notification_req_body)
									.then(
											function(response) {
												
												if(response.data.error_code == 500){
													$scope.nofiticationPrefMsg = response.data.response_detail;
													$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
					                                $anchorScroll(); 
					                                $scope.msg = 'Something Went Wrong'; 
					                                $scope.icon = 'report_problem';
					                                $scope.styleclass = 'c-error';
					                                $scope.showAlertMessage = true;
					                                $timeout(function() {
					                                	$scope.showAlertMessage = false;
					                                }, 2000);
					                                $scope.getNotificationPref();
												}else{
													$scope.nofiticationPrefMsg = response.data.response_detail;
													$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
					                                $anchorScroll(); 
					                                $scope.msg = $scope.nofiticationPrefMsg; 
					                                $scope.icon = '';
					                                $scope.styleclass = 'c-success';
					                                $scope.showAlertMessage = true;
					                                $timeout(function() {
					                                	$scope.showAlertMessage = false;
					                                }, 2000);
					                                $scope.getNotificationPref();
												}
												

											},
											function(error) {
												
											});
                        	  }else{
                        		  
                        		  var notification_req_body = {
            								"request_body" : {
            								    "msgSeverityCode": $scope.userNotificationPref.msgSeverityCode,
            								    "notfDelvMechCode": "EM",
            								    "userId": userId
            								}
            							}
                        		  
                        		  apiService
									.updateNotificationPref('create', notification_req_body)
									.then(
											function(response) {
												
												if(response.data){
													$scope.nofiticationPrefMsg = response.data.response_detail;
													$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
					                                $anchorScroll(); 
					                                $scope.msg = $scope.nofiticationPrefMsg; 
					                                $scope.icon = '';
					                                $scope.styleclass = 'c-success';
					                                $scope.showAlertMessage = true;
					                                $timeout(function() {
					                                	$scope.showAlertMessage = false;
					                                }, 2000);
					                                $scope.getNotificationPref();
												}else{
													$scope.nofiticationPrefMsg = response.data.response_detail;
													$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
					                                $anchorScroll(); 
					                                $scope.msg = 'Something Went Wrong'; 
					                                $scope.icon = 'report_problem';
					                                $scope.styleclass = 'c-error';
					                                $scope.showAlertMessage = true;
					                                $timeout(function() {
					                                	$scope.showAlertMessage = false;
					                                }, 2000);
					                                $scope.getNotificationPref();
												}

											},
											function(error) {
												
											});
                        	  }
                          }
                          
                         
                          
                          /**** Notification Preference End****/

					}
				});