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

angular.module('signInModal')
			.service('browserStorageService', function() {
				this.setUserDetail = function(detail) {
					if (sessionStorage.getItem('rm')) {
						localStorage.setItem('userDetail', detail);
					} else {
						sessionStorage.setItem('userDetail', detail);
						document.cookie = 'userDetail='+detail;
					}
				}
				
				this.setUserRole = function(role) {
					if (sessionStorage.getItem('rm')) {
						localStorage.setItem('userRole', role);
					} else {
						sessionStorage.setItem('userRole', role);
						document.cookie = 'userRole='+role;
					}
				}
				
				this.setAuthToken = function(token) {
					if (sessionStorage.getItem('rm')) {
						localStorage.setItem('auth_token', token);
					} else {
						sessionStorage.setItem('auth_token', token);
						document.cookie = 'authToken='+token;
					}
				}
				
				this.getUserDetail = function() {
					return sessionStorage.getItem('userDetail') ?
						sessionStorage.getItem('userDetail') :
						localStorage.getItem('userDetail');
				}
				
				this.getUserRole = function() {
					return sessionStorage.getItem('userRole') ?
						sessionStorage.getItem('userRole') :
						localStorage.getItem('userRole');
				}
				
				this.setAdmin = function(admin) {
					if (sessionStorage.getItem('rm')) {
						localStorage.setItem('isAdmin', admin);
					} else {
						sessionStorage.setItem('isAdmin', admin);
					}
				}
				
				this.setPublisher = function(publisher) {
					if (sessionStorage.getItem('rm')) {
						localStorage.setItem('isPublisher', publisher);
					} else {
						sessionStorage.setItem('isPublisher', publisher);
					}
				}
				
				this.isAdmin = function() {
					return sessionStorage.getItem('isAdmin') ?
						sessionStorage.getItem('isAdmin') :
						localStorage.getItem('isAdmin');
				}
				
				this.isPublisher = function() {
					return sessionStorage.getItem('isPublisher') ?
						sessionStorage.getItem('isPublisher') :
						localStorage.getItem('isPublisher');
				}
				
				this.getAuthToken = function() {
					return sessionStorage.getItem('auth_token') ?
						sessionStorage.getItem('auth_token') :
						localStorage.getItem('auth_token');
				}
				
				this.removeUserDetail = function() {
					sessionStorage.removeItem('userDetail');
					localStorage.removeItem('userDetail');
				}
				
				this.clearUserRole = function() {
					sessionStorage.setItem('userRole', "");
					localStorage.setItem('userRole', "");
				}
				
				this.removeAuthToken = function() {
					sessionStorage.removeItem('auth_token');
					localStorage.removeItem('auth_token');
				}
				
				this.setMktPlaceStorage = function(obj) {
					sessionStorage.setItem('mktPlaceStorage', JSON.stringify(obj));
				}
				
				this.getMktPlaceStorage = function() {
					return JSON.parse(sessionStorage.getItem('mktPlaceStorage'));
				}
				
				this.removeMktPlaceStorage = function() {
					sessionStorage.removeItem('mktPlaceStorage');
				}
			})
            .component(
                        'signinContent',
                        {
                              template : '<a ng-click="$root.showAdvancedLogin()" modal-data="$ctrl.modalData" class="no-outline">Sign In</a>',
                              controller : function($uibModal, $scope,$rootScope,productService,$window,$mdDialog, apiService, $state, browserStorageService) {
                                    
                                    $ctrl = this;
                                    $scope.cas = {
                            				login : 'false'
                                    };
                                    apiService.getCasEnable().then( function(response){
                                    	$scope.cas.login = response.data.response_body;
                                    });
                                    $scope.broadcastmessage = "";
                                    
                                    $scope.$on('transfer', function(event, data) {
                                          $scope.broadcastmessage = data.message;
                                    });
                                    
                                    $rootScope.showAdvancedLogin = function(ev) {
                                    	if($scope.cas.login === 'true'){
                                    		sessionStorage.setItem('provider', "LFCAS");
                                    		var retUrl = window.location.origin;
                                    		$window.open('http://identity.linuxfoundation.org/cas/login?service=' + retUrl , '_self');
                                    	}else{
	                                        $mdDialog.show({
	                                          controller: signinController,
	                                          templateUrl: './app/header/sign-in.template.html',
	                                          parent: angular.element(document.body),
	                                          targetEvent: ev,
	                                          clickOutsideToClose:true,
	                                          
	                                        })
	                                        .then(function(answer) {
	                                            
	                                            $scope.userfirstname = productService.test.firstName;
	                                            $scope.userid = productService.test.userId;
	                                            $scope.loginName = productService.test.loginName;
	                                            
	                                            $scope.localStore = [];
	                                            $scope.localStore.push($scope.userfirstname, $scope.userid, $scope.loginName);
	                                            
	                                            console.log("$scope.localStore: ",$scope.localStore);
	                                            
//	                                            sessionStorage.setItem('userDetail', JSON.stringify($scope.localStore));
	                                            browserStorageService.setUserDetail(JSON.stringify($scope.localStore));
	                                            $scope.$emit('transferUp', {
							                          message : true,
							                          username : $scope.userfirstname
							                    });
							                    
							                    //$scope.loginUserID = JSON.parse(sessionStorage.getItem('userDetail'));
							                    
//							                    sessionStorage.setItem('userDetail', JSON.stringify($scope.localStore));
	                                            browserStorageService.setUserDetail(JSON.stringify($scope.localStore));
							                    console.log("$scope.localStore: ",$scope.localStore);
							                    
							                    //$window.sessionStorage.setItem("acumosUserSession",productService.test.userId);
							                    
							                    console.info("close");
							                    if($state.current.name == 'modularResource')
							                    	$state.reload();

	                                          //$scope.status = 'You said the information was "' + answer + '".';
	                                        }, function() {
	                                          //$scope.status = 'You cancelled the dialog.';
	                                        });
                                    	}
                                      };
                                      
                                                                     
                                      function signinController($scope, $auth, $timeout, jwtHelper, apiService, $location, $anchorScroll) {
                                    	  //var $ctrl = this;
                                    	  console.info("in signin controller");
                                    	  $scope.signin = function() {
                                                $scope.userData = {"request_body":{"username": $scope.modalData.name, "password": $scope.modalData.value}}
                                                sessionStorage.setItem('rm', $scope.modalData.cb1 ? "remember" : "");
                                                
                                                $scope.login();

                                          };
                                          $scope.signinAuthenticate= function(provider) {
                                          	  sessionStorage.setItem('provider', provider);
                                              if(provider == "google"){
                                                $auth.authenticate(provider).
                              	                  then(function(response) {
                              	                    console.log(response);
                              	                  browserStorageService.setAuthToken(response.access_token);
                              	                    
                              	                    console.log("Success: ", response);
                              	                    $scope.socialsigninresponse = response;
                              	                    apiService.getGoogleTokenInfo($scope.socialsigninresponse.access_token).then(function successCallback(response) {
                                                       console.log(response);
                                                       $scope.userData = {"emailId": response.data.email}
                                                       $scope.socialLogin();
                                                  }, function errorCallback(response) {
                                                	  console.log(response);
                                                  });
                              	                  }),function errorCallback(response) {
                                              	  		// Something went wrong.
                                              	  		console.log(response);
                                            			};
                                              }else if(provider == "github"){
                                            	  $auth.authenticate(provider).
                            	                  then(function(response) {
                            	                    console.log("Success: ", response);
                            	                    $scope.socialsigninresponse = response;
                            	                  apiService.getGithubAccessToken($scope.socialsigninresponse.code).then(function successCallback(response) {
                            	                	  //alert(angular.toJson(response))
                            	                	  $scope.accessToken = response.data;
                            	                    apiService.getGithubUserProfile($scope.accessToken).then(function successCallback(response) {
                            	                    	console.log(response);
                                                        $scope.userData = {"emailId": response.data.email}
                                                        $scope.socialLogin();
                            	                    }, function errorCallback(response) {
                            	                    	console.log(response);
                            	                    });
                            	                  }, function errorCallback(response) {
                          	                    	console.log(response);
                          	                    });
                            	                    
                            	                  
                            	                    
                            	                  }),function errorCallback(response) {
                                            	  		// Something went wrong.
                                            	  		console.log(response);
                                            	  		alert(angular.toJson(response))
                                          			};
                                              }
                                                $('#myModal').modal('hide');
                                              };
                                              
                                              
                                              $scope.forgotPassword = function(ev){
                                                  $mdDialog.cancel();
                                                  $rootScope.$broadcast('forgotPassword',ev);
                                              }

                                              $scope.regenerateVerification = function(ev){
                                                  $mdDialog.cancel();
                                                  window.location = "/#/confirm_verification?user="+ $scope.modalData.name;
                                                  return;
                                              }

                                          $scope.login = function(){
                                        	  $scope.userIdDisabled = false;
                                        	  $scope.userVerificationPending = false;
                                              apiService.getJwtAuth($scope.userData).then(function successCallback(response) {
                                            	  browserStorageService.setAdmin(response.data.admin);
                                            	  browserStorageService.setPublisher(response.data.publisher);
                                            	  angular.forEach(response.data.userAssignedRolesList, function(value, key) {
                                            		  if(value.name == 'Admin' || value.name == 'admin'){
//                                            			  sessionStorage.setItem('userRole', 'Admin');
                                            			  browserStorageService.setUserRole('Admin');
                                            		  }
                                            		});
                                            	  $rootScope.$broadcast('roleCheck');
                                            	  browserStorageService.setAuthToken(response.data.jwtToken);
                                            	  $rootScope.accessError = false;
                                            	  var authToken = jwtHelper.decodeToken(response.data.jwtToken);
                                                  if(response.data.jwtToken != ""){
	                                                  if(authToken.loginPassExpire == true){
	                                                      $('.modal').hide();
	                                                      $('.modal-backdrop').hide();
	                                                      localStorage.setItem('loginPassExpire', authToken.loginPassExpire);
	                                                      window.location = 'index.html#/forgotPswd';
	                                                      return;
	                                                }
	                                                localStorage.setItem('loginPassExpire', '');
	                                                $scope.signinservice = authToken;
	                                                productService.setData($scope.signinservice.mlpuser);
	                                                var test = productService.test;
	                                                $rootScope.$broadcast('loadCaurosel',{ userId :$scope.signinservice.mlpuser.userId } );
	                                                $scope.showTag = function() {
	                                                $rootScope.$broadcast('manageTags',{ userId :$scope.signinservice.mlpuser.userId });
                                                	}
	                                                /*if(response.data.firstLogin)
	                                                	$scope.showTag(); */
                                                }else{
                                                	console.log("Error: ", response);
                                                    $scope.userPassInvalid = true;
                                                    if(response.data.message == "Login Inactive"){
	                                                    $location.hash('sign-in-dialog');  // id of a container on the top of the page - where to scroll (top)
	  	                                                $anchorScroll(); 
	  	                                                $scope.msg = "User Id is disabled."; 
	  	                                                $scope.icon = 'report_problem';
	  	                                                $scope.styleclass = 'c-error';
	  	                                                $scope.showAlertMessage = true;
	  	                                                $timeout(function() {
	  	                                                	$scope.showAlertMessage = false;
	  	                                                }, 5000);
                                                    }
                                                }
                                                $mdDialog.hide();
                                               },function errorCallback(response) {
                                                   
                                                   if(response.data.message.indexOf("Failed to find active user") > -1){
                                                	   $scope.userIdDisabled = true;
                                                	   $scope.userPassBlocked = false;
                                                	   $scope.userPassInvalid = false;
                                                	   $scope.userVerificationPending = false;
                                                   }
                                                   if(response.data.message == "Verification Pending"){
                                                	   $scope.userIdDisabled = false;
                                                	   $scope.userPassInvalid = false;
                                                	   $scope.userPassBlocked = false;
                                                	   $scope.userVerificationPending = true;
                                                	   	 /*$mdDialog.hide();
                                                         alert("User Id is disabled");*/
                                                   }
                                                   if(response.data.message.indexOf('blocked') > -1){
                                                	   $scope.userPassBlocked = true; 
                                                	   $scope.userBlockedMessage = response.data.message;
                                                	   $scope.userPassInvalid = false;
                                                       $scope.userVerificationPending = false;
                                               	   } else if(response.data.message == "Failed to authenticate user"){
	                                                   $scope.userPassInvalid = true;
	                                                   $scope.userPassBlocked = false;
	                                                   $scope.userVerificationPending = false;
                                               	   }
                                             });
                                          };  
                                          
                                          $scope.socialLogin = function(){
                                        	  apiService.insertSocialSignIn($scope.userData).then(function successCallback(response) {
                                        		  console.log(response);
                                        		  //sessionStorage.setItem('auth_token', response.data.jwtToken);
                                                  if(response.data.loginPassExpire == true){
                                                        
                                                        $('.modal').hide();
                                                        $('.modal-backdrop').hide();
                                                        localStorage.setItem('loginPassExpire', response.data.loginPassExpire);
                                                        window.location = 'index.html#/forgotPswd';
                                                        return;
                                                  }
                                                  angular.forEach(response.data.userAssignedRolesList, function(value, key) {
                                            		  if(value.name == 'Admin' || value.name == 'admin'){
                                            			  browserStorageService.setUserRole('Admin');
                                            		  }
                                            		});
                                                  localStorage.setItem('loginPassExpire', '');
                                                  console.log("Success: ", response);
                                                  $timeout(function() {
                                                              alert("Signed-In successfully");
                                                        },0);
                                                  $scope.signinservice = response.data;
                                                  productService.setData($scope.signinservice);
                                                  var test = productService.test;
                                                  console.log("test"+test);
                                                  //$ctrl.$close();
                                                  $mdDialog.hide();
                    			                  	}, function errorCallback(response) {
                    			                        console.log("Error: ", response);
                    			                        oauthDetails = {};
                    			                        $scope.userPassInvalid = true;
                    			                  	});
                                          };
                                          	    
                                          $scope.handleDismiss = function() {
                                                console.info("in handle dismiss");
                                                $mdDialog.cancel();
                                                  
                                                //$ctrl.$dismiss();
                                          };
                                          
                                    }

                              }
                        });
