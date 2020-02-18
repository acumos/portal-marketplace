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
              							var ext = fileName.split('.').pop().toLowerCase();
              							
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
					controller : function($scope, $http, $location, $rootScope, $timeout, $stateParams,
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

						$scope.pageNumber = 0;
						$scope.totalPages = 0;
						$scope.allCatalogListLength = 0;
						$scope.requestResultSize = 10;

						$scope.setPageStart = 0;
		                $scope.selectedPage = 0;
		                
		                $scope.setStartCount = function(val) {
							if (val == "preBunch") {
								$scope.setPageStart = $scope.setPageStart - 5
							} else if (val == "nextBunch") {
								$scope.setPageStart = $scope.setPageStart + 5
							} else if (val == "pre") {
								if ($scope.selectedPage == $scope.setPageStart) {
									$scope.setPageStart = $scope.setPageStart - 1;
									$scope.selectedPage = $scope.selectedPage - 1;
								} else {
									$scope.selectedPage = $scope.selectedPage - 1;
								}
							} else if (val == "next")
								if ($scope.selectedPage == $scope.setPageStart + 4) {
									$scope.setPageStart = $scope.setPageStart + 1;
									$scope.selectedPage = $scope.selectedPage + 1;
								} else {
									$scope.selectedPage = $scope.selectedPage + 1;
								}
						};

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
			                	sessionStorage.setItem('apiTokenStatus', false);
			                	getUserDetail();
			                	
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
										var tokenStatus = sessionStorage.getItem("apiTokenStatus") == "true" ? true : false;
										console.log(tokenStatus);
										if(tokenStatus == true || $scope.user.apiToken == null)
											{
											//$scope.user.apiToken = null;
											$scope.deleteApiTokenFlag= false;
											}
										else
											$scope.deleteApiTokenFlag= true;
										$scope.userCopy = angular.copy($scope.user);
										$scope.userActive = angular
												.copy(data.response_body);
									}).error(
									function(data, status, headers, config) {

									});
							getUserImage();
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
								$scope.userActive.emailId = $scope.userActive.emailId;
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
								$scope.user.emailId = $scope.user.emailId;
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
						$scope.userCopy.emailId = $scope.user.emailId;
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
							var ext = fileName.split('.').pop().toLowerCase(); 
				           
				            if(validFormats.indexOf(ext) == -1){
				            	$scope.extensionError = true;
				            	$scope.disableUsrImgBtn = true;
				            }else if(fileName == '' || fileName == undefined || fileName == null){
				            	$scope.nullFileError = true;
				            	$scope.disableUsrImgBtn = true;
				            }else if (file.size > sizeToBytes($scope.imageSize)){
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
					 	function sizeToBytes(size) {
					 		var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
					 		for (var i = 0; i < sizes.length; i++) { 
					 		  if(size.endsWith(sizes[i]) && i ==0) {
					 			 return parseInt(size.substring(0,size.indexOf(sizes[i])),10)
					 		  }
					 		  else if(size.endsWith(sizes[i]) && i ==1) {
					 			 return parseInt(size.substring(0,size.indexOf(sizes[i])),10) * 1024
					 		  }
					 		  else if(size.endsWith(sizes[i]) && i ==2) {
					 			 return parseInt(size.substring(0,size.indexOf(sizes[i])),10) * 1024 * 1024
					 		  }
					 		  else if(size.endsWith(sizes[i]) && i ==3) {
					 			 return parseInt(size.substring(0,size.indexOf(sizes[i])),10) * 1024 * 1024 * 1024
					 		  }
					 		  else if(size.endsWith(sizes[i]) && i ==4) {
						 	     return parseInt(size.substring(0,size.indexOf(sizes[i])),10) * 1024 * 1024 * 1024 * 1024
						      }
					 		}
					 	}
					 	$scope.uploadImg = function(){
							var file = $scope.userImage;
							var fileFormData = new FormData();
							var validFormats = ['jpg','jpeg','png','gif'];
							var fileName = file.name;
							var ext = fileName.split('.').pop().toLowerCase();//substr($('#userImage').value.lastIndexOf('.')+1);
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
                          
                          $scope.deleteApiToken = function() {                                                             
                              $scope.userCopy.emailId = $scope.user.emailId;
                                    $scope.userCopy.username = $scope.user.loginName;
                                    $scope.userCopy.apiToken = null;
                                    if ($scope.userCopy.active == false)
                                          $scope.userCopy.active = "N"
                                    else
                                          $scope.userCopy.active = "Y";
                                    
                                    var requestData = {
                                          "request_body" : $scope.userCopy
                                    };                                   
                                          var req1 = {
                                                      method : 'PUT',
                                                      url : '/api/users/deleteToken',
                                                      //url : '/api/users/updateUser',
                                                      data : requestData,
                                                      headers : {
                                                            'Content-Type' : 'application/json'
                                                      }
                                                };
                                                $http(req1)
                                                            .success(
                                                                        function(data, status, headers,
                                                                                    config) {         
                                                                  if(status == 200)
                                                                        {
	                                                                          $scope.user.apiToken = null;
	                                                                          $mdDialog.hide();
	                                                                          $location.hash('myDialog');  
	                                                                          $anchorScroll(); 
	                                                                          $scope.msg = "Token deleted successfully."; 
	                                                                          $scope.icon = '';
	                                                                          $scope.styleclass = 'c-success';
	                                                                          $scope.showAlertMessage = true;     
	                                                                          $scope.deleteApiTokenFlag= false;
	                                                                          sessionStorage.setItem('apiTokenStatus', true);
	                                                                          $timeout(function() {
	                                                                                $scope.showAlertMessage = false;                                                                  
	                                                                          }, 4000);
                                                                          }                                                                              
                                                                        }).error(
                                                                        function(data, status, headers,
                                                                                    config) {                                                                           
                                                                              alert('fail')
                                                                        });
                          }                                                  
                          
                          /**** Notification Preference End****/
                          /*** catalog Management start**/
                      	var user= JSON.parse(browserStorageService.getUserDetail());
                      	if(user) $scope.loginUserID = user[1];

                      	$scope.loadCatalog = function(pageNumber) {
  							$scope.allCatalogList = [];
  							$scope.SetDataLoaded = true;
  							$rootScope.setLoader = true;
  							$scope.pageNumber = pageNumber;
  							$scope.selectedPage = pageNumber;
  							
  							var reqObject = {
  								"request_body" : {
  									"fieldToDirectionMap" : {
  										"created" : "DESC"
  									},
  									"page" : pageNumber,
  									"size" : $scope.requestResultSize
  								},
  								"request_from" : "string",
  								"request_id" : "string"
  							};
  							$scope.response_body = [];
  							apiService
  									.getCatalogs(reqObject)
  									.then(
  											function successCallback(response) {
  												var resp = response.data.response_body;
  												$scope.allCatalogList = resp.content;											
  												$scope.totalPages = resp.totalPages;
  												$scope.totalElements = resp.totalElements;
  												$scope.allCatalogListLength = resp.totalElements;
  												$scope.SetDataLoaded = false;
  												$rootScope.setLoader = false;
  											},
  											function errorCallback(response) {
  												$scope.SetDataLoaded = false;
  												$rootScope.setLoader = false;												
  											});
  						}
						if($scope.loginUserID)
							$scope.loadCatalog(0);
						
                      	$scope.filterChange = function(size) {
        	            	$scope.allCatalogList = [];
        	            	$scope.allCatalogListLength = 0;
        	            	$scope.requestResultSize = size;
        	            	$scope.loadCatalog(0);
        	            }	
						
						$scope.favList = [];
						
						$scope.updateFavorite = function(catalogId , flag ) {							
							 if(flag)
								 $scope.createFav(catalogId);
							 else
								 $scope.deleteFav(catalogId);									       						
						}
						
						 $scope.createFav = function(catalogID) {  							  							  					
  							apiService
  									.createFav(catalogID, $scope.loginUserID)
  									.then(
  											function successCallback(response) {  												
  											},
  											function errorCallback(response) {  												
  											});
  						}
						 
					 $scope.deleteFav = function(catalogID) {  							  							  					
  							apiService
  									.deleteFav(catalogID, $scope.loginUserID)
  									.then(
  											function successCallback(response) {  												
  											},
  											function errorCallback(response) {  											
  											});
  						}	
					// if redirected from Marketplace then show the Select Fav Catalog tab
					  $scope.$watch('$stateParams.isCatalogSelected', function() {
						$stateParams.isCatalogSelected ? $scope.selectedIndex = 2 : $scope.selectedIndex = 0;
					  });
					
                          /** catalog Management end **/

					}
				});