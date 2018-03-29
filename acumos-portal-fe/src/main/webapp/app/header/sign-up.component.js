app	
		.component(
				'signUp',
				{
					template : '<a ng-click="$root.showAdvancedSignup()" class="no-outline">Sign Up <span class="signUp">Now</span></a>',
					controller : function($uibModal, $scope, $rootScope, $mdDialog, $location, $anchorScroll) {
						$ctrl = this;
						
						$ctrl.dataForModal = {
							name : 'signup',
							value : 'ValueToEdit'
						}
						
						$rootScope.showAdvancedSignup = function(ev) {
                            $mdDialog.show({
                              controller: signupController,
                              templateUrl: './app/header/sign-up.template.html',
                              parent: angular.element(document.body),
                              targetEvent: ev,
                              clickOutsideToClose:true,
                              /*controllerAs : '$ctrl',
                              resolve : {
                                    modalData : $ctrl.testData
                              }*/
                              
                            })
                            .then(function(answer) {
                            	console.info("result");
                                //console.info(result);
                                
                                $scope.userfirstname = productService.test.firstName;
                                $scope.userid = productService.test.userId;
                                
                                $scope.localStore = [];
                                $scope.localStore.push($scope.userfirstname, $scope.userid);
                                
                                console.log("$scope.localStore: ",$scope.localStore);
        
			                    /*$rootScope.$on("signIn",function(){
			                        $scope.name = productService.getData();
			                      });*/
			                    
			                    /*$scope.$on('transferUsername', function(event,data) {
			                          console.log('transferUsername');
			                          $scope.emitedusername = data.username;
			
			                    });*/
			                    
			                    $scope.$emit('transferUp', {
			                          message : true,
			                          username : $scope.userfirstname
			                    });
			                    
			                    //$scope.loginUserID = JSON.parse(localStorage.getItem('userDetail'));
			                    
			                    localStorage.setItem('userDetail', JSON.stringify($scope.localStore));
			                    console.log("$scope.localStore: ",$scope.localStore);
			                    
			                    //$window.sessionStorage.setItem("acumosUserSession",productService.test.userId);
			                    
			                    console.info("close");
                              //$scope.status = 'You said the information was "' + answer + '".';
                            }, function() {
                              //$scope.status = 'You cancelled the dialog.';
                            });
                          };
						
                          function signupController($http, $scope,$rootScope,productService, modalProvider, $timeout, $auth, apiService){
                      		var $ctrl = this;
                    		// $scope.userPassInvalid = false;
                    		// $scope.successfulLogin = true;
    						$scope.inputtype = 'password';
    						$scope.inputtypeconfirm = 'password';
    						$scope.showPasswd = function(){
    							if($scope.inputtype == 'text'){
    								$scope.inputtype = 'password';
    							} else {
    								$scope.inputtype = 'text';
    							}
    						}
    						$scope.showConfirmPasswd = function(){
    							if($scope.inputtypeconfirm == 'text'){
    								$scope.inputtypeconfirm = 'password';
    							} else {
    								$scope.inputtypeconfirm = 'text';
    							}
    						}
    						
                    		$scope.openlogin = function() {
                    			$rootScope.showAdvancedLogin();
                    			//modalProvider.openPopupModal();
                    			$mdDialog.cancel();
                    		}, $scope.closelogin = function() {
                    			modalProvider.closePopupModal();
                    		}
                    		$scope.signUp = function() {
                    			console.info("in handle close");
                    			//console.info("DATA: ", $ctrl.modalData);

                    			// $ctrl.$close();
                    			var userDetails = {"request_body":{
                    				'firstName' : $scope.firstName,
                    				'lastName' : $scope.lastName,
                    				'emailId' : $scope.uemail,
                    				'username' : $scope.username,
                    				'password' : $scope.pwd,
                    				'active' : true,
                    				'lastLogin' : $scope.date,
                    				'created' : $scope.date,
                    				'modified' : $scope.date}};
                    			
                    			apiService.insertSignUp(userDetails).then(function successCallback(response) {
                    				userDetails = {};
                    				if(response.data.error_code == 100){
                    					/*$timeout(function() {
                        					alert("Signed-Up successfully\nPlease Sign In to log into the portal.");
                        				},0);*/
                    					$mdDialog.hide();
                    					$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                        $anchorScroll(); 
                                        $scope.msg = "Signed-Up successfully\nPlease Sign In to log into the portal."; 
                                        $scope.icon = '';
                                        $scope.styleclass = 'c-success';
                                        $scope.showAlertMessage = true;
                                        $timeout(function() {
                                        	$scope.showAlertMessage = false;
                                        }, 5000);
                        				//$rootScope.showAdvancedLogin();
                        				$scope.successfulLogin = true;
                    				}
                    				else if(response.data.error_code == 202){
                    					//alert("Username Already Exists");
                    					$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                        $anchorScroll(); 
                                        $scope.msg = "Username Already Exists"; 
                                        $scope.icon = 'report_problem';
                                        $scope.styleclass = 'c-error';
                                        $scope.showAlertMessage = true;
                                        $timeout(function() {
                                        	$scope.showAlertMessage = false;
                                        }, 4000);
                    					/*break*/
                    				}
                    				else if(response.data.error_code == 203){
                    					//alert("Email Already Exists");
                    					$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                        $anchorScroll(); 
                                        $scope.msg = "Email Already Exists"; 
                                        $scope.icon = 'report_problem';
                                        $scope.styleclass = 'c-error';
                                        $scope.showAlertMessage = true;
                                        $timeout(function() {
                                        	$scope.showAlertMessage = false;
                                        }, 4000);
                    					/*break*/
                    				}
                    				
                    			}, function errorCallback(response) {
                    				userDetails = {};
                    				console.log("Error: ", response);
                    				$scope.userPassInvalid = true;
                    			});
                    			
                    			/*$http({
                    				method : 'POST',
                    				url : 'api/users/register',
                    				data : {
                    					'firstName' : $scope.firstName,
                    					'lastName' : $scope.lastName,
                    					'emailId' : $scope.uemail,
                    					'username' : $scope.username,
                    					'password' : $scope.pwd,
                    					'active' : true,
                    					'lastLogin' : $scope.date,
                    					'created' : $scope.date,
                    					'modified' : $scope.date
                    				},
                    				{"request_body":{'firstName' : $scope.firstName,
                    					'lastName' : $scope.lastName,
                    					'emailId' : $scope.uemail,
                    					'username' : $scope.username,
                    					'password' : $scope.pwd,
                    					'active' : true,
                    					'lastLogin' : $scope.date,
                    					'created' : $scope.date,
                    					'modified' : $scope.date}}
                    			
                    			 * data : { username : $ctrl.modalData.name, password :
                    			 * $ctrl.modalData.value },
                    			 
                    			}).then(function successCallback(response) {
                    				
                    				$timeout(function() {
                    					alert("Signed-Up successfully\nPlease Sign In to log into the portal.");
                    				},0);
                    				console.log("Success: ", response);
                    				$scope.successfulLogin = true;
                    				$ctrl.$close();
                    				//modalProvider.openPopupModal();
                    			}, function errorCallback(response) {
                    				console.log("Error: ", response);
                    				$scope.userPassInvalid = true;
                    			});
                    */
                    			/*
                    			 * var req = { method : 'POST', url : '/api/auth/login', data : {
                    			 * username : $ctrl.modalData.name, password : $ctrl.modalData.value },
                    			 * headers : { 'Content-Type' : 'application/json' } }
                    			 * 
                    			 * $http(req).then(function(data){ $scope.data = data;
                    			 * console.log($scope.data ); })
                    			 */

                    		};
                    		$scope.handleDismiss = function() {
                    			console.info("in handle dismiss");
                    			$mdDialog.cancel();
                    		};
                    		
                    		/*
                             *  Function to OAUTH based on service provider
                             */
                            $scope.signupAuthenticate = function (provider) {
                            	var oauthDetails = {};
                                console.log("OATH Service Provider - " + provider);
                                //$rootScope.$on('event:social-sign-in-success', function(event, userDetails){
                                //console.log(userDetails);
                                
                                switch (provider){
                                	case 'google': 
                                		/*oauthDetails = {
                            					//MLPUser Object data
                            					'firstName' : userDetails.name,
                            					'lastName' : 'N/A',
                            					'emailId' : userDetails.email,
                            					'username' : userDetails.email,
                            					'password' : 'N/A - Provider user',
                            					'active' : true,
                            					'lastLogin' : $scope.date,
                            					'created' : $scope.date,
                            					'modified' : $scope.date,
                            					
                            					          					
                            					//MLPUserLoginProvider data
                            					
                            					
                            				    'providerCd':'GP',
                            				    'providerUserId':userDetails.email,
                            				    'displayName':userDetails.name,
                            				     'createdDate' : $scope.date,
                            				     'modifiedDate': $scope.date,
                            				     'rank':2,
                            				     'accessToken': 'Test'
                            				    };*/
                                		$auth.authenticate(provider).
                    	                  then(function(response) {
                    	                    console.log("Success: ", response);
                    	                    $scope.socialsigninresponse = response;
                    	                    
                    	                    apiService.getGoogleUserProfile($scope.socialsigninresponse.access_token).then(function successCallback(response) {
                                             console.log(response);
                                             var firstName = response.data.displayName.split(' ').slice(0, -1).join(' ');
                                             var lastName = response.data.displayName.split(' ').slice(-1).join(' ');

                                             oauthDetails = {
                                 					//MLPUser Object data
                                 					'firstName' : firstName,
                                 					'lastName' : lastName,
                                 					'emailId' : response.data.emails[0].value,
                                 					'username' : response.data.emails[0].value,
                                 					'password' : 'google',
                                 					'active' : true,
                                 					'lastLogin' : $scope.date,
                                 					'created' : $scope.date,
                                 					'modified' : $scope.date,
                                 					'auth_token': $scope.socialsigninresponse.access_token,
                                 					
                                 					          					
                                 					//MLPUserLoginProvider data
                                 					
                                 					
                                 				    'providerCd':'GP',
                                 				    'providerUserId':response.data.emails[0].value,
                                 				    'displayName':response.data.displayName,
                                 				     'createdDate' : $scope.date,
                                 				     'modifiedDate': $scope.date,
                                 				     'rank':2,
                                 				     'accessToken': $scope.socialsigninresponse.access_token,
                                 				    };
                                             apiService.insertSocialSignUp(oauthDetails).then(function successCallback(response) {
                                     			/*oauthDetails = {};
                                 				console.log("Success: ", response);
                                 				$scope.successfulLogin = true;
                                 				 $scope.signupservice = response.data;
                                                  productService.setData($scope.signupservice);
                                                  var test = productService.test;
                                                  console.log("test"+test);*/
                                                  $timeout(function() {
                                  					alert("Signed-Up successfully\nPlease Sign In to log into the portal.");
                                  				},0);
                                  				$rootScope.showAdvancedLogin();
                                  				console.log("Success: ", response);
                                  				$scope.successfulLogin = true;
                                  				$mdDialog.hide();
                                                  //$ctrl.$close();
                                 			}, function errorCallback(response) {
                                 				oauthDetails = {};
                                 				console.log("Error: ", response);
                                 				$scope.userPassInvalid = true;
                                 			});
                    	                    }, function errorCallback(response) {
                    	                    	console.log(response);
                    	                    });
                    	                    
                    	                  
                    	                    
                    	                  }),function errorCallback(response) {
                                    	  		// Something went wrong.
                                    	  		console.log(response);
                                  			};
                                		
                                		/*$http({
                        				method : 'POST',
                        				url : 'api/oauth/login/register',
                        				data : {
                        					//MLPUser Object data
                        					'firstName' : userDetails.name,
                        					'lastName' : 'N/A',
                        					'emailId' : userDetails.email,
                        					'username' : 'N/A',
                        					'password' : 'N/A - Provider user',
                        					'active' : true,
                        					'lastLogin' : $scope.date,
                        					'created' : $scope.date,
                        					'modified' : $scope.date,
                        					
                        					          					
                        					//MLPUserLoginProvider data
                        					
                        					
                        				    'providerCd':'GP',
                        				    'providerUserId':userDetails.email,
                        				    'displayName':userDetails.name,
                        				     'createdDate' : $scope.date,
                        				     'modifiedDate': $scope.date,
                        				     'rank':2,
                        				     'accessToken': 'Test'
                        				    },
                        			
                        			 * data : { username : $ctrl.modalData.name, password :
                        			 * $ctrl.modalData.value },
                        			 
                        			}).then(function successCallback(response) {
                        				console.log("Success: ", response);
                        				$scope.successfulLogin = true;
                        				 $scope.signupservice = response.data;
                                         productService.setData($scope.signupservice);
                                         var test = productService.test;
                                         console.log("test"+test);
                                         $ctrl.$close();
                        			}, function errorCallback(response) {
                        				console.log("Error: ", response);
                        				$scope.userPassInvalid = true;
                        			});*/
                                		break;
                                		
                                	case 'facebook': 
                                		$auth.authenticate(provider).
                     	                  then(function(response) {
                     	                    console.log("Success: ", response);
                     	                    $scope.socialsigninresponse = response;
                     	                    
                     	                    apiService.getFacebookUserProfile($scope.socialsigninresponse.access_token).then(function successCallback(response) {
                                              console.log(response);
                                              oauthDetails = {
                                  					//MLPUser Object data
                                  					'firstName' : response.data.displayName,
                                  					'lastName' : 'N/A',
                                  					'emailId' : response.data.emails[0].value,
                                  					'username' : response.data.emails[0].value,
                                  					'password' : 'google',
                                  					'active' : true,
                                  					'lastLogin' : $scope.date,
                                  					'created' : $scope.date,
                                  					'modified' : $scope.date,
                                  					'auth_token': $scope.socialsigninresponse.access_token,
                                  					
                                  					          					
                                  					//MLPUserLoginProvider data
                                  					
                                  					
                                  				    'providerCd':'FB',
                                  				    'providerUserId':response.data.emails[0].value,
                                  				    'displayName':response.data.displayName,
                                  				     'createdDate' : $scope.date,
                                  				     'modifiedDate': $scope.date,
                                  				     'rank':2,
                                  				     'accessToken': $scope.socialsigninresponse.access_token,
                                  				    };
                                              apiService.insertSocialSignUp(oauthDetails).then(function successCallback(response) {
                                      			$timeout(function() {
                                   					alert("Signed-Up successfully\nPlease Sign In to log into the portal.");
                                   				},0);
                                   				$rootScope.showAdvancedLogin();
                                   				console.log("Success: ", response);
                                   				$scope.successfulLogin = true;
                                   				$mdDialog.hide();
                                                   //$ctrl.$close();
                                  			}, function errorCallback(response) {
                                  				oauthDetails = {};
                                  				console.log("Error: ", response);
                                  				$scope.userPassInvalid = true;
                                  			});
                     	                    }, function errorCallback(response) {
                     	                    	console.log(response);
                     	                    });
                     	                    
                     	                  
                     	                    
                     	                  }),function errorCallback(response) {
                                     	  		// Something went wrong.
                                     	  		console.log(response);
                                   			};

                                		break;
                                		
                                	case 'linkedin':
                                		
                                		oauthDetails = {
                            					//MLPUser Object data
                            					'firstName' : userDetails.name,
                            					'lastName' : 'N/A',
                            					'emailId' : userDetails.email,
                            					'username' : userDetails.email,
                            					'password' : 'N/A - Provider user',
                            					'active' : true,
                            					'lastLogin' : $scope.date,
                            					'created' : $scope.date,
                            					'modified' : $scope.date,
                            					
                            					          					
                            					//MLPUserLoginProvider data
                            					
                            					
                            				    'providerCd':'LI',
                            				    'providerUserId':userDetails.email,
                            				    'displayName':userDetails.name,
                            				     'createdDate' : $scope.date,
                            				     'modifiedDate': $scope.date,
                            				     'rank':4,
                            				     'accessToken': 'Test'
                            				    };
                        		apiService.insertSocialSignUp(oauthDetails).then(function successCallback(response) {
                        			oauthDetails = {};
                    				console.log("Success: ", response);
                    				$scope.successfulLogin = true;
                    				 $scope.signupservice = response.data;
                                     productService.setData($scope.signupservice);
                                     var test = productService.test;
                                     console.log("test"+test);
                                     $ctrl.$close();
                    			}, function errorCallback(response) {
                    				oauthDetails = {};
                    				console.log("Error: ", response);
                    				$scope.userPassInvalid = true;
                    			});
                                		/*$http({
                            				method : 'POST',
                            				url : 'api/oauth/login/register',
                            				data : {
                            					//MLPUser Object data
                            					'firstName' : userDetails.name,
                            					'lastName' : 'N/A',
                            					'emailId' : userDetails.email,
                            					'username' : 'N/A',
                            					'password' : 'N/A - Provider user',
                            					'active' : true,
                            					'lastLogin' : $scope.date,
                            					'created' : $scope.date,
                            					'modified' : $scope.date,
                            					
                            					          					
                            					//MLPUserLoginProvider data
                            					
                            					
                            				    'providerCd':'LI',
                            				    'providerUserId':userDetails.email,
                            				    'displayName':userDetails.name,
                            				     'createdDate' : $scope.date,
                            				     'modifiedDate': $scope.date,
                            				     'rank':4,
                            				     'accessToken': 'Test'
                            				    },
                            			
                            			 * data : { username : $ctrl.modalData.name, password :
                            			 * $ctrl.modalData.value },
                            			 
                            			}).then(function successCallback(response) {
                            				console.log("Success: ", response);
                            				$scope.successfulLogin = true;
                            				 $scope.signupservice = response.data;
                                             productService.setData($scope.signupservice);
                                             var test = productService.test;
                                             console.log("test"+test);
                                             $ctrl.$close();
                            			}, function errorCallback(response) {
                            				console.log("Error: ", response);
                            				$scope.userPassInvalid = true;
                            			});*/
                                		break;
                                	case 'github':
                                		$auth.authenticate(provider).
                    	                  then(function(response) {
                    	                    console.log("Success: ", response);
                    	                    $scope.socialsigninresponse = response;
                    	                  apiService.getGithubAccessToken($scope.socialsigninresponse.code).then(function successCallback(response) {
                    	                	  //alert(angular.toJson(response))
                    	                	  $scope.accessToken = response.data;
                    	                    apiService.getGithubUserProfile($scope.accessToken).then(function successCallback(response) {
                                             console.log(response);
                                             var firstName = response.data.name.split(' ').slice(0, -1).join(' ');
                                             var lastName = response.data.name.split(' ').slice(-1).join(' ');
                                             oauthDetails = {
                                 					//MLPUser Object data
                                 					'firstName' : firstName,
                                 					'lastName' : lastName,
                                 					'emailId' : response.data.email,
                                 					'username' : response.data.login,
                                 					'password' : 'github',
                                 					'active' : true,
                                 					'lastLogin' : $scope.date,
                                 					'created' : $scope.date,
                                 					'modified' : $scope.date,
                                 					
                                 					          					
                                 					//MLPUserLoginProvider data
                                 					
                                 				    'providerCd':'GH',
                                 				    'providerUserId':response.data.login,
                                 				    'displayName':response.data.name,
                                 				     'createdDate' : $scope.date,
                                 				     'modifiedDate': $scope.date,
                                 				     'rank':2,
                                 				     'accessToken': $scope.accessToken
                                 				    };
                                             apiService.insertSocialSignUp(oauthDetails).then(function successCallback(response) {
                                     			oauthDetails = {};
                                 				console.log("Success: ", response);
                                 				$scope.successfulLogin = true;
                                 				 $scope.signupservice = response.data;
                                                  productService.setData($scope.signupservice);
                                                  var test = productService.test;
                                                  console.log("test"+test);
                                                  $timeout(function() {
                                  					alert("Signed-Up successfully\nPlease Sign In to log into the portal.");
                                  				},0);
                                  				$rootScope.showAdvancedLogin();
                                  				console.log("Success: ", response);
                                  				$scope.successfulLogin = true;
                                  				$mdDialog.hide();
                                                  //$ctrl.$close();
                                 			}, function errorCallback(response) {
                                 				oauthDetails = {};
                                 				console.log("Error: ", response);
                                 				$scope.userPassInvalid = true;
                                 			});
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
                                		break;
                                		default:
                                }
                              /*  $rootScope.$on('event:social-sign-in-success', function(event, userDetails){
                                	console.log(userDetails.name);*/
                                	//Now lets check if this user already exist ( use email to do the check)
                                    
                                    
                                    /*function (err) {
                                    console.log("Error: ", err);
                                    $ctrl.userPassInvalid = true;
                                }*/
                            	//{name: <user_name>, email: <user_email>, imageUrl: <image_url>, uid: <UID by social vendor>, provider: <Google/Facebook/LinkedIN>, token: < accessToken for Facebook & google, no token for linkedIN>}, idToken: < google idToken >
                            	//socialLoginService.logout() For logout
                            	//$rootScope.$on('event:social-sign-out-success', function(event, logoutStatus){}) 
                            	//Braodcast event which will be triggered after successful logout.

                         

                            };

                    	}
                          
						/*$ctrl.open = function() {
							$uibModal
									.open({
										template : '<my-signup greeting="$ctrl.greeting" modal-data="$ctrl.modalData" $close="$close(result)" $dismiss="$dismiss(reason)"></my-signup>',
										controller : [
												'modalData',
												function(modalData) {
													var $ctrl = this;
													$ctrl.greeting = 'I am a modal!'
													$ctrl.modalData = modalData;
												} ],
										controllerAs : '$ctrl',
										resolve : {
											modalData : $ctrl.dataForModal
										}
									}).result
									.then(
											function(result) {
												//alert("Signed up successfully!");
												$scope.$emit('signUpSuccessful', {
                                                    message : true,
												});
                                              
											},
											function(reason) {
											});
						};*/
					}
				});

/*angular.module('AcumosApp').component('mySignup', {
	templateUrl : './app/header/sign-up.template.html',
	bindings : {
		$close : '&',
		$dismiss : '&',
		greeting : '<',
		modalData : '<'
	},
	controller : function($http, $scope,$rootScope,productService, modalProvider, $timeout, apiService) {
		var $ctrl = this;
		// $scope.userPassInvalid = false;
		// $scope.successfulLogin = true;
		$ctrl.openlogin = function() {
			$rootScope.showAdvancedLogin();
			//modalProvider.openPopupModal();
			$ctrl.$dismiss();
		}, $ctrl.closelogin = function() {
			modalProvider.closePopupModal();
		}
		$ctrl.signUp = function() {
			console.info("in handle close");
			console.info("DATA: ", $ctrl.modalData);

			// $ctrl.$close();
			var userDetails = {"request_body":{'firstName' : $scope.firstName,
				'lastName' : $scope.lastName,
				'emailId' : $scope.uemail,
				'username' : $scope.username,
				'password' : $scope.pwd,
				'active' : true,
				'lastLogin' : $scope.date,
				'created' : $scope.date,
				'modified' : $scope.date}};
			
			apiService.insertSignUp(userDetails).then(function successCallback(response) {
				userDetails = {};
				$timeout(function() {
					alert("Signed-Up successfully\nPlease Sign In to log into the portal.");
				},0);
				$rootScope.showAdvancedLogin();
				console.log("Success: ", response);
				$scope.successfulLogin = true;
				$ctrl.$close();
				//modalProvider.openPopupModal();
			}, function errorCallback(response) {
				userDetails = {};
				console.log("Error: ", response);
				$scope.userPassInvalid = true;
			});
			
			$http({
				method : 'POST',
				url : 'api/users/register',
				data : {
					'firstName' : $scope.firstName,
					'lastName' : $scope.lastName,
					'emailId' : $scope.uemail,
					'username' : $scope.username,
					'password' : $scope.pwd,
					'active' : true,
					'lastLogin' : $scope.date,
					'created' : $scope.date,
					'modified' : $scope.date
				},
				{"request_body":{'firstName' : $scope.firstName,
					'lastName' : $scope.lastName,
					'emailId' : $scope.uemail,
					'username' : $scope.username,
					'password' : $scope.pwd,
					'active' : true,
					'lastLogin' : $scope.date,
					'created' : $scope.date,
					'modified' : $scope.date}}
			
			 * data : { username : $ctrl.modalData.name, password :
			 * $ctrl.modalData.value },
			 
			}).then(function successCallback(response) {
				
				$timeout(function() {
					alert("Signed-Up successfully\nPlease Sign In to log into the portal.");
				},0);
				console.log("Success: ", response);
				$scope.successfulLogin = true;
				$ctrl.$close();
				//modalProvider.openPopupModal();
			}, function errorCallback(response) {
				console.log("Error: ", response);
				$scope.userPassInvalid = true;
			});

			
			 * var req = { method : 'POST', url : '/api/auth/login', data : {
			 * username : $ctrl.modalData.name, password : $ctrl.modalData.value },
			 * headers : { 'Content-Type' : 'application/json' } }
			 * 
			 * $http(req).then(function(data){ $scope.data = data;
			 * console.log($scope.data ); })
			 

		};
		$ctrl.handleDismiss = function() {
			console.info("in handle dismiss");
			$ctrl.$dismiss();
		};
		
		
         *  Function to OAUTH based on service provider
         
        $ctrl.signupAuthenticate = function (provider) {
        	var oauthDetails = {};
            console.log("OATH Service Provider - " + provider);
            $rootScope.$on('event:social-sign-in-success', function(event, userDetails){
            console.log(userDetails);
            
            switch (provider){
            	case 'google': 
            		oauthDetails = {
        					//MLPUser Object data
        					'firstName' : userDetails.name,
        					'lastName' : 'N/A',
        					'emailId' : userDetails.email,
        					'username' : userDetails.email,
        					'password' : 'N/A - Provider user',
        					'active' : true,
        					'lastLogin' : $scope.date,
        					'created' : $scope.date,
        					'modified' : $scope.date,
        					
        					          					
        					//MLPUserLoginProvider data
        					
        					
        				    'providerCd':'GP',
        				    'providerUserId':userDetails.email,
        				    'displayName':userDetails.name,
        				     'createdDate' : $scope.date,
        				     'modifiedDate': $scope.date,
        				     'rank':2,
        				     'accessToken': 'Test'
        				    };
            		apiService.insertSocialSignUp(oauthDetails).then(function successCallback(response) {
            			oauthDetails = {};
        				console.log("Success: ", response);
        				$scope.successfulLogin = true;
        				 $scope.signupservice = response.data;
                         productService.setData($scope.signupservice);
                         var test = productService.test;
                         console.log("test"+test);
                         $ctrl.$close();
        			}, function errorCallback(response) {
        				oauthDetails = {};
        				console.log("Error: ", response);
        				$scope.userPassInvalid = true;
        			});
            		$http({
    				method : 'POST',
    				url : 'api/oauth/login/register',
    				data : {
    					//MLPUser Object data
    					'firstName' : userDetails.name,
    					'lastName' : 'N/A',
    					'emailId' : userDetails.email,
    					'username' : 'N/A',
    					'password' : 'N/A - Provider user',
    					'active' : true,
    					'lastLogin' : $scope.date,
    					'created' : $scope.date,
    					'modified' : $scope.date,
    					
    					          					
    					//MLPUserLoginProvider data
    					
    					
    				    'providerCd':'GP',
    				    'providerUserId':userDetails.email,
    				    'displayName':userDetails.name,
    				     'createdDate' : $scope.date,
    				     'modifiedDate': $scope.date,
    				     'rank':2,
    				     'accessToken': 'Test'
    				    },
    			
    			 * data : { username : $ctrl.modalData.name, password :
    			 * $ctrl.modalData.value },
    			 
    			}).then(function successCallback(response) {
    				console.log("Success: ", response);
    				$scope.successfulLogin = true;
    				 $scope.signupservice = response.data;
                     productService.setData($scope.signupservice);
                     var test = productService.test;
                     console.log("test"+test);
                     $ctrl.$close();
    			}, function errorCallback(response) {
    				console.log("Error: ", response);
    				$scope.userPassInvalid = true;
    			});
            		break;
            		
            	case 'facebook': 
            		
            		oauthDetails = {
        					//MLPUser Object data
        					'firstName' : userDetails.name,
        					'lastName' : 'N/A',
        					'emailId' : userDetails.email,
        					'username' : userDetails.email,
        					'password' : 'N/A - Provider user',
        					'active' : true,
        					'lastLogin' : $scope.date,
        					'created' : $scope.date,
        					'modified' : $scope.date,
        					
        					          					
        					//MLPUserLoginProvider data
        					
        					
        				    'providerCd':'FB',
        				    'providerUserId':userDetails.email,
        				    'displayName':userDetails.name,
        				     'createdDate' : $scope.date,
        				     'modifiedDate': $scope.date,
        				     'rank':3,
        				     'accessToken': 'Test'
        				    };
        		apiService.insertSocialSignUp(oauthDetails).then(function successCallback(response) {
        			oauthDetails = {};
    				console.log("Success: ", response);
    				$scope.successfulLogin = true;
    				 $scope.signupservice = response.data;
                     productService.setData($scope.signupservice);
                     var test = productService.test;
                     console.log("test"+test);
                     $ctrl.$close();
    			}, function errorCallback(response) {
    				oauthDetails = {};
    				console.log("Error: ", response);
    				$scope.userPassInvalid = true;
    			});
            		$http({
        				method : 'POST',
        				url : 'api/oauth/login/register',
        				data : {
        					//MLPUser Object data
        					'firstName' : userDetails.name,
        					'lastName' : 'N/A',
        					'emailId' : userDetails.email,
        					'username' : 'N/A',
        					'password' : 'N/A - Provider user',
        					'active' : true,
        					'lastLogin' : $scope.date,
        					'created' : $scope.date,
        					'modified' : $scope.date,
        					
        					          					
        					//MLPUserLoginProvider data
        					
        					
        				    'providerCd':'FB',
        				    'providerUserId':userDetails.email,
        				    'displayName':userDetails.name,
        				     'createdDate' : $scope.date,
        				     'modifiedDate': $scope.date,
        				     'rank':3,
        				     'accessToken': 'Test'
        				    },
        			
        			 * data : { username : $ctrl.modalData.name, password :
        			 * $ctrl.modalData.value },
        			 
        			}).then(function successCallback(response) {
        				console.log("Success: ", response);
        				$scope.successfulLogin = true;
        				 $scope.signupservice = response.data;
                         productService.setData($scope.signupservice);
                         var test = productService.test;
                         console.log("test"+test);
                         $ctrl.$close();
        			}, function errorCallback(response) {
        				console.log("Error: ", response);
        				$scope.userPassInvalid = true;
        			});
            		break;
            		
            	case 'linkedin':
            		
            		oauthDetails = {
        					//MLPUser Object data
        					'firstName' : userDetails.name,
        					'lastName' : 'N/A',
        					'emailId' : userDetails.email,
        					'username' : userDetails.email,
        					'password' : 'N/A - Provider user',
        					'active' : true,
        					'lastLogin' : $scope.date,
        					'created' : $scope.date,
        					'modified' : $scope.date,
        					
        					          					
        					//MLPUserLoginProvider data
        					
        					
        				    'providerCd':'LI',
        				    'providerUserId':userDetails.email,
        				    'displayName':userDetails.name,
        				     'createdDate' : $scope.date,
        				     'modifiedDate': $scope.date,
        				     'rank':4,
        				     'accessToken': 'Test'
        				    };
    		apiService.insertSocialSignUp(oauthDetails).then(function successCallback(response) {
    			oauthDetails = {};
				console.log("Success: ", response);
				$scope.successfulLogin = true;
				 $scope.signupservice = response.data;
                 productService.setData($scope.signupservice);
                 var test = productService.test;
                 console.log("test"+test);
                 $ctrl.$close();
			}, function errorCallback(response) {
				oauthDetails = {};
				console.log("Error: ", response);
				$scope.userPassInvalid = true;
			});
            		$http({
        				method : 'POST',
        				url : 'api/oauth/login/register',
        				data : {
        					//MLPUser Object data
        					'firstName' : userDetails.name,
        					'lastName' : 'N/A',
        					'emailId' : userDetails.email,
        					'username' : 'N/A',
        					'password' : 'N/A - Provider user',
        					'active' : true,
        					'lastLogin' : $scope.date,
        					'created' : $scope.date,
        					'modified' : $scope.date,
        					
        					          					
        					//MLPUserLoginProvider data
        					
        					
        				    'providerCd':'LI',
        				    'providerUserId':userDetails.email,
        				    'displayName':userDetails.name,
        				     'createdDate' : $scope.date,
        				     'modifiedDate': $scope.date,
        				     'rank':4,
        				     'accessToken': 'Test'
        				    },
        			
        			 * data : { username : $ctrl.modalData.name, password :
        			 * $ctrl.modalData.value },
        			 
        			}).then(function successCallback(response) {
        				console.log("Success: ", response);
        				$scope.successfulLogin = true;
        				 $scope.signupservice = response.data;
                         productService.setData($scope.signupservice);
                         var test = productService.test;
                         console.log("test"+test);
                         $ctrl.$close();
        			}, function errorCallback(response) {
        				console.log("Error: ", response);
        				$scope.userPassInvalid = true;
        			});
            		break;
            	case 'github':
            		default:
            }
            $rootScope.$on('event:social-sign-in-success', function(event, userDetails){
            	console.log(userDetails.name);
            	//Now lets check if this user already exist ( use email to do the check)
                
                
                function (err) {
                console.log("Error: ", err);
                $ctrl.userPassInvalid = true;
            }
        	//{name: <user_name>, email: <user_email>, imageUrl: <image_url>, uid: <UID by social vendor>, provider: <Google/Facebook/LinkedIN>, token: < accessToken for Facebook & google, no token for linkedIN>}, idToken: < google idToken >
        	//socialLoginService.logout() For logout
        	//$rootScope.$on('event:social-sign-out-success', function(event, logoutStatus){}) 
        	//Braodcast event which will be triggered after successful logout.

        });

        };

	},
});
*/