app
            .component(
                        'signinContent',
                        {
                              template : '<a ng-click="$root.showAdvancedLogin()" modal-data="$ctrl.modalData" class="no-outline">Sign In</a>',
                              controller : function($uibModal, $scope,$rootScope,productService,$window,$mdDialog, apiService) {
                                    
                                    $ctrl = this;
                                    $ctrl.testData = {
                                          name : 'test',
                                          value : 'test'
                                    }

                                    $scope.cas = {
                            				login : 'false'
                                    };
                                    apiService.getCasEnable().then( function(response){
                                    	$scope.cas.login = response.data.response_body;
                                    });
                                    $scope.broadcastmessage = "";
                                    /*$scope.cas = {
                                    				login : 'true',
                                    				validate : {
                                    							url : window.location.origin
                                    							}
                                    			};*/
                                    
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
	                                            localStorage.setItem('userDetail', JSON.stringify($scope.localStore));
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
                                    	}
                                      };
                                      
                                                                     
                                      function signinController($scope, $auth, $timeout, jwtHelper, apiService, $location, $anchorScroll) {
                                    	  //var $ctrl = this;
                                    	  console.info("in signin controller");
                                    	  $scope.signin = function() {

                                                console.info("in handle close");
                                                //console.info("DATA: ", $ctrl.modalData);
                                               //sessionStorage.setItem('authToken', "");
                                                                                                                                
                                                $scope.userData = {"request_body":{"username": $scope.modalData.name, "password": $scope.modalData.value}}
                                                $scope.login();
                                                

                                          };
                                          $scope.signinAuthenticate= function(provider) {
                                          	  sessionStorage.setItem('provider', provider);
                                              if(provider == "globaluid"){
                                                //window.location.href = "#/attLogin";
                                                $window.open('https://www.e-access.att.com/empsvcs/hrpinmgt/pagLogin/?sysName=ACUMOS&retURL=http://localhost:8085/index.html#/attGlobalLogin', '_self');
                                              }else if(provider == "google"){
                                                $auth.authenticate(provider).
                              	                  then(function(response) {
                              	                    console.log(response);
                              	                  localStorage.setItem('auth_token', response.access_token);
                              	                    
                              	                    console.log("Success: ", response);
                              	                    $scope.socialsigninresponse = response;
                              	                    apiService.getGoogleTokenInfo($scope.socialsigninresponse.access_token).then(function successCallback(response) {
                                                       console.log(response);
                                                       $scope.userData = {"emailId": response.data.email}
                                                       $scope.socialLogin();
                                                  }, function errorCallback(response) {
                                                	  console.log(response);
                                                  });
                              	                    
                              	                    
                              	                  /*  var currentUser = $scope.gauth.currentUser.get(); 
                                   					var profile = currentUser.getBasicProfile(); 
                                   					var idToken = currentUser.getAuthResponse().id_token; 
                                  					var accessToken = currentUser.getAuthResponse().access_token; 
                                      				var userDetails= { 
                                      						token: accessToken, 
                                  							idToken: idToken,  
                                      						name: profile.getName(),  
                                      						email: profile.getEmail(),  
                                       						uid: profile.getId(),  
                                      						provider: "google",  
                                      						imageUrl: profile.getImageUrl() 
                                       					} */

                              	                    
                              	                    /*
                              	                    apiService.insertSocialSignIn(oauthDetails).then(function successCallback(response) {
                                                  			console.log("Success: ", response);
                              	                        	oauthDetails = {};
                              	                        	$scope.signinservice = response.data;
                              	                        	productService.setData($scope.signinservice);
                              	                        	var test = productService.test;
                              	                        	console.log("test"+test);
                              	                        	$ctrl.$close();
                              			                  	}, function errorCallback(response) {
                              			                        console.log("Error: ", response);
                              			                        oauthDetails = {};
                              			                        $scope.userPassInvalid = true;
                              			                  	});*/
                              	                    
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
                                              
                                              
                                          $scope.login = function(){$scope.userIdDisabled = false;
                                        	  /* apiService.insertSignIn($scope.userData).then(function successCallback(response) {
                                              console.log(response);
                                              if(response.data.loginPassExpire == true){
                                                    $('.modal').hide();
                                                    $('.modal-backdrop').hide();
                                                    localStorage.setItem('loginPassExpire', response.data.loginPassExpire);
                                                    window.location = 'index.html#/forgotPswd';
                                                    return;
                                              }
                                              localStorage.setItem('loginPassExpire', '');
                                              console.log("Success: ", response);
                                              $timeout(function() {
                                                          alert("Signed-In successfully");
                                                    },0);
                                              $scope.signinservice = response.data;
                                              productService.setData($scope.signinservice);
                                              var test = productService.test;*/
                                              
                                              apiService.getJwtAuth($scope.userData).then(function successCallback(response) {
                                            	  angular.forEach(response.data.userAssignedRolesList, function(value, key) {
                                            		  if(value.name == 'Admin' || value.name == 'admin'){
                                            			  localStorage.setItem('userRole', 'Admin');
                                            		  }
                                            		});
                                            	  localStorage.setItem('auth_token', response.data.jwtToken);
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
	                                                
	                                                /*$location.hash('sign-in-dialog');  // id of a container on the top of the page - where to scroll (top)
	                                                $anchorScroll(); 
	                                                $scope.msg = "Signed-In successfully."; 
	                                                $scope.icon = 'report_problem';
	                                                $scope.styleclass = 'c-success';
	                                                $scope.showAlertMessage = true;
	                                                $timeout(function() {
	                                                	$scope.showAlertMessage = false;
	                                                }, 5000);
	
	                                                */
	                                                $scope.signinservice = authToken;
	                                                productService.setData($scope.signinservice.mlpuser);
	                                                var test = productService.test;
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
                                                   console.log("Error: ", response);
                                                   $scope.userPassInvalid = true;
                                                   if(response.data.message == "Inactive user"){
                                                	   $scope.userIdDisabled = true;
                                                	   $scope.userPassInvalid = false;
                                                	   	 /*$mdDialog.hide();
                                                         alert("User Id is disabled");*/
                                                   }
                                             });
                                              //console.log("test"+test);
                                              //$ctrl.$close();
                                              

                                       /* }, function errorCallback(response) {
                                              console.log("Error: ", response);
                                              $scope.userPassInvalid = true;
                                              if(response.data.message == "Login Inactive"){
                                                    location.reload(true);
                                                    alert("User Id is disabled");
                                              }
                                        });*/
                                          };  
                                          
                                          $scope.socialLogin = function(){
                                        	  apiService.insertSocialSignIn($scope.userData).then(function successCallback(response) {
                                        		  console.log(response);
                                        		  //localStorage.setItem('auth_token', response.data.jwtToken);
                                                  if(response.data.loginPassExpire == true){
                                                        
                                                        $('.modal').hide();
                                                        $('.modal-backdrop').hide();
                                                        localStorage.setItem('loginPassExpire', response.data.loginPassExpire);
                                                        window.location = 'index.html#/forgotPswd';
                                                        return;
                                                  }
                                                  angular.forEach(response.data.userAssignedRolesList, function(value, key) {
                                            		  if(value.name == 'Admin' || value.name == 'admin'){
                                            			  localStorage.setItem('userRole', 'Admin');
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
                                          /*$ctrl.signinAuthenticate = function (provider) {
                                              console.log("OATH Service Provider - " + provider);
                                              var oauthDetails = {};
                                              $rootScope.$on('event:social-sign-in-success', function(event, userDetails){
                                                  console.log(userDetails);
                                              switch (provider) {
                                                  case 'google' :   
                                                      oauthDetails = {
                                                                        //MLPUser Object data
                                                                        'firstName' : userData.name,
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
                                                                      'providerUserId':userData.email,
                                                                      'displayName':userData.family_name,
                                                                       'createdDate' : $scope.date,
                                                                       'modifiedDate': $scope.date,
                                                                       'rank':2,
                                                                       'accessToken':'Test'
                                                                      };
                                                      apiService.insertSocialSignIn(oauthDetails).then(function successCallback(response) {
                                                            console.log("Success: ", response);
                                                            oauthDetails = {};
                                                      $scope.signinservice = response.data;
                                                      productService.setData($scope.signinservice);
                                                      var test = productService.test;
                                                      console.log("test"+test);
                                                      $ctrl.$close();
                                                      }, function errorCallback(response) {
                                                            console.log("Error: ", response);
                                                            oauthDetails = {};
                                                            $scope.userPassInvalid = true;
                                                      });
                                                          $http({
                                                                  method : 'POST',
                                                                  url : 'api/oauth/login',
                                                                  data : {
                                                                        //MLPUser Object data
                                                                        'firstName' : userData.name,
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
                                                                      'providerUserId':userData.email,
                                                                      'displayName':userData.family_name,
                                                                       'createdDate' : $scope.date,
                                                                       'modifiedDate': $scope.date,
                                                                       'rank':2,
                                                                       'accessToken':'Test'
                                                                      },
                                                            
                                                             * data : { username : $ctrl.modalData.name, password :
                                                             * $ctrl.modalData.value },
                                                             
                                                            }).then(function successCallback(response) {
                                                                  console.log("Success: ", response);
                                                              
                                                              $scope.signinservice = response.data;
                                                              productService.setData($scope.signinservice);
                                                              var test = productService.test;
                                                              console.log("test"+test);
                                                              $ctrl.$close();
                                                            }, function errorCallback(response) {
                                                                  console.log("Error: ", response);
                                                                  $scope.userPassInvalid = true;
                                                            });
                                                      break;
                                                  case 'facebook' :
                                                  case 'linkedin' :
                                                  case 'github' :
                                                  case 'globaluid' :
                                                  default:
                                              }
                                          });
                                          };*/
                                    	  }

                                    
                                    /*$rootScope.open = function() {
                                          $uibModal
                                                      .open({
                                                            template : '<sign-in-modal '
                                                                        + '$close="$close(result)" $dismiss="$dismiss(reason)"></sign-in-modal> ',
                                                    	  templateUrl : './app/header/sign-in.template.html',  
                                                    	  controller : [
                                                                'modalData',
                                                                function(modalData, apiService) {
                                                                      var $ctrl = this;
                                                                      $ctrl.modalData = modalData;

                                                                } ],
                                                            controllerAs : '$ctrl',
                                                            resolve : {
                                                                  modalData : $ctrl.testData
                                                            }
                                                      }).result.then(function(result) {
                                                                        console.info("result");
                                                                        console.info(result);
                                                                        
                                                                        $scope.userfirstname = productService.test.firstName;
                                                                        $scope.userid = productService.test.userId;
                                                                        
                                                                        $scope.localStore = [];
                                                                        $scope.localStore.push($scope.userfirstname, $scope.userid);
                                                                        
                                                                        console.log("$scope.localStore: ",$scope.localStore);
                                                
                                                $rootScope.$on("signIn",function(){
                                                    $scope.name = productService.getData();
                                                  });
                                                
                                                $scope.$on('transferUsername', function(event,data) {
                                                      console.log('transferUsername');
                                                      $scope.emitedusername = data.username;

                                                });
                                                
                                                $scope.$emit('transferUp', {
                                                      message : true,
                                                      username : $scope.userfirstname
                                                });
                                                
                                                //$scope.loginUserID = JSON.parse(localStorage.getItem('userDetail'));
                                                
                                                localStorage.setItem('userDetail', JSON.stringify($scope.localStore));
                                                console.log("$scope.localStore: ",$scope.localStore);

                                                //$window.sessionStorage.setItem("acumosUserSession",productService.test.userId);
                                                
                                                console.info("close");
                                          });
                                    };*/
                                      
                                      
                                     
                              }
                        });

/*app.component('signInModal', {
      templateUrl : './app/header/sign-in.template.html',
      bindings : {
            $close : '&',
            $dismiss : '&',
            greeting : '<',
            modalData : '<',
            modalInstance : '<',
            resolve : '<'
      },
      controller : function($http, $scope, $rootScope,productService,$timeout,apiService, jwtHelper, $window, $auth) {
            var $ctrl = this;

            $ctrl.signIn = function() {

                  //console.info("in handle close");
                  //console.info("DATA: ", $ctrl.modalData);
                 sessionStorage.setItem('authToken', "");
                  
                $http({
                        method : 'POST',
                        url : 'api/auth/jwtToken',
                        data : {"request_body":{"username": $ctrl.modalData.name, "password": $ctrl.modalData.value}}
                  }).then(function successCallback(response) {
                        console.log(response);
                        if(response && response.data.jwtToken){
                              var authToken = jwtHelper.decodeToken(response.data.jwtToken);
                              //var authToken = jwtHelper.decodeToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkaWxpcCIsImNyZWF0ZWQiOjE1MDMwNjI3Njg3MjIsImV4cCI6MTUwMzA2Mjc3OCwibWxwdXNlciI6eyJ1c2VySWQiOiJhNzExMmNlMi1lYjIxLTQyMTAtOTY2Ny04YjQ4NGU0NDRiNGUiLCJmaXJzdE5hbWUiOiJkaWxpcCIsIm1pZGRsZU5hbWUiOm51bGwsImxhc3ROYW1lIjoia3VtYXIiLCJvcmdOYW1lIjpudWxsLCJlbWFpbCI6ImRpbGlwMTAyNkBnbWFpbC5jb20iLCJsb2dpbk5hbWUiOiJkaWxpcCIsImxvZ2luUGFzcyI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjoxNTAyNzAyOTQ1MDAwLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6ZmFsc2UsImxhc3RMb2dpbiI6MTUwMjcwMjk0NTAwMCwiY3JlYXRlZCI6MTUwMjcwMjk0NTAwMCwibW9kaWZpZWQiOjE1MDI5NzE1MzMwMDB9fQ.jMUB_Aiyf9le7t9WKQhOadJkNpkNKETIs5e_MSGy6KnCtziJBv2HxCDvSZFopl4dpD94rPw-TckPnNtgDOwldg");
                              sessionStorage.setItem('authToken', authToken);
                              console.log(authToken);
                              if(response.data.loginPassExpire == true){
                              
                              $('.modal').hide();
                              $('.modal-backdrop').hide();
                              localStorage.setItem('loginPassExpire', response.data.loginPassExpire);
                              window.location = 'index.html#/forgotPswd';
                              return;
                            }
                            localStorage.setItem('loginPassExpire', '');
                            //console.log("Success: ", response);
                            $timeout(function() {
                                          alert("Signed-In successfully");
                                    },0);
                            $scope.signinservice = authToken.mlpuser;//response.data;
                            productService.setData($scope.signinservice);
                            var test = productService.test;
                            console.log("test"+test);
                            $ctrl.$close();
                       }else{
                              $ctrl.userPassInvalid = true;
                              sessionStorage.removeItem(authToken);
                        }
                        

                  }, function errorCallback(response) {
                        console.log("Error: ", response);
                        $ctrl.userPassInvalid = true;
                        sessionStorage.removeItem(authToken);
                  });
                  
                  $scope.userData = {"request_body":{"username": $ctrl.modalData.name, "password": $ctrl.modalData.value}}
                  apiService.insertSignIn($scope.userData).then(function successCallback(response) {
                        console.log(response);
                        if(response.data.loginPassExpire == true){
                              
                              $('.modal').hide();
                              $('.modal-backdrop').hide();
                              localStorage.setItem('loginPassExpire', response.data.loginPassExpire);
                              window.location = 'index.html#/forgotPswd';
                              return;
                        }
                        localStorage.setItem('loginPassExpire', '');
                        console.log("Success: ", response);
                        $timeout(function() {
                                    alert("Signed-In successfully");
                              },0);
                        $scope.signinservice = response.data;
                        productService.setData($scope.signinservice);
                        var test = productService.test;
                        console.log("test"+test);
                        $ctrl.$close();

                  }, function errorCallback(response) {
                        console.log("Error: ", response);
                        $ctrl.userPassInvalid = true;
                        if(response.data.message == "Login Inactive"){
                              location.reload(true);
                              alert("User Id is disabled");
                        }
                  });
                  

            };
            $scope.signinAuthenticate= function(provider) {
            	var oauthDetails = {
                        //MLPUser Object data
                        'firstName' : 'neera',
                        'lastName' : 'N/A',
                        'emailId' : 'neeraj.s.sonar@gmail.com',
                        'username' : 'N/A',
                        'password' : 'N/A - Provider user',
                        'active' : true,
                        'lastLogin' : '',
                        'created' : '',
                        'modified' : '',
                        'providerCd':'GP',
                        'providerUserId':'neeraj.s.sonar@gmail.com',
                        'displayName':'n',
                        'createdDate' : '',
                        'modifiedDate': '',
                        'rank':'2',
                        'jwtToken':'Test'
                      };
                console.log(provider);
                console.log("signin");
                sessionStorage.setItem('provider', provider);
                if(provider == "globaluid"){
                  //window.location.href = "#/attLogin";
                  $window.open('https://www.e-access.att.com/empsvcs/hrpinmgt/pagLogin/?sysName=ACUMOS&retURL=http://localhost:8085/index.html#/attGlobalLogin', '_self');
                }else{
                  $auth.authenticate(provider).
	                  then(function(response) {
	                    // Signed in with Google.
	                    console.log(response);
	                   // $location.path('/');
	                    
	                    console.log("Success: ", response);
	                    $scope.socialsigninresponse = response;
	                    
	                    
	                    var currentUser = $scope.gauth.currentUser.get(); 
     					var profile = currentUser.getBasicProfile(); 
     					var idToken = currentUser.getAuthResponse().id_token; 
    					var accessToken = currentUser.getAuthResponse().access_token; 
        				var userDetails= { 
        						token: accessToken, 
    							idToken: idToken,  
        						name: profile.getName(),  
        						email: profile.getEmail(),  
         						uid: profile.getId(),  
        						provider: "google",  
        						imageUrl: profile.getImageUrl() 
         					} 

	                    
	                    
	                    apiService.insertSocialSignIn(oauthDetails).then(function successCallback(response) {
                    			console.log("Success: ", response);
	                        	oauthDetails = {};
	                        	$scope.signinservice = response.data;
	                        	productService.setData($scope.signinservice);
	                        	var test = productService.test;
	                        	console.log("test"+test);
	                        	$ctrl.$close();
			                  	}, function errorCallback(response) {
			                        console.log("Error: ", response);
			                        oauthDetails = {};
			                        $scope.userPassInvalid = true;
			                  	});
	                    
	                  }),function errorCallback(response) {
                	  		// Something went wrong.
                	  		console.log(response);
              			};
                }
                  $('#myModal').modal('hide');
                };
                
            	    
            $ctrl.handleDismiss = function() {
                  console.info("in handle dismiss");
                  $ctrl.$dismiss();
            };
            $ctrl.signinAuthenticate = function (provider) {
                console.log("OATH Service Provider - " + provider);
                var oauthDetails = {};
                $rootScope.$on('event:social-sign-in-success', function(event, userDetails){
                    console.log(userDetails);
                switch (provider) {
                    case 'google' :   
                        oauthDetails = {
                                          //MLPUser Object data
                                          'firstName' : userData.name,
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
                                        'providerUserId':userData.email,
                                        'displayName':userData.family_name,
                                         'createdDate' : $scope.date,
                                         'modifiedDate': $scope.date,
                                         'rank':2,
                                         'accessToken':'Test'
                                        };
                        apiService.insertSocialSignIn(oauthDetails).then(function successCallback(response) {
                              console.log("Success: ", response);
                              oauthDetails = {};
                        $scope.signinservice = response.data;
                        productService.setData($scope.signinservice);
                        var test = productService.test;
                        console.log("test"+test);
                        $ctrl.$close();
                        }, function errorCallback(response) {
                              console.log("Error: ", response);
                              oauthDetails = {};
                              $scope.userPassInvalid = true;
                        });
                            $http({
                                    method : 'POST',
                                    url : 'api/oauth/login',
                                    data : {
                                          //MLPUser Object data
                                          'firstName' : userData.name,
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
                                        'providerUserId':userData.email,
                                        'displayName':userData.family_name,
                                         'createdDate' : $scope.date,
                                         'modifiedDate': $scope.date,
                                         'rank':2,
                                         'accessToken':'Test'
                                        },
                              
                               * data : { username : $ctrl.modalData.name, password :
                               * $ctrl.modalData.value },
                               
                              }).then(function successCallback(response) {
                                    console.log("Success: ", response);
                                
                                $scope.signinservice = response.data;
                                productService.setData($scope.signinservice);
                                var test = productService.test;
                                console.log("test"+test);
                                $ctrl.$close();
                              }, function errorCallback(response) {
                                    console.log("Error: ", response);
                                    $scope.userPassInvalid = true;
                              });
                        break;
                    case 'facebook' :
                    case 'linkedin' :
                    case 'github' :
                    case 'globaluid' :
                    default:
                }
            });
            };
      },
});


*/