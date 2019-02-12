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

angular.module('headerNav')
	.component('headerNav',{
	templateUrl : 'app/header/md-header-nav.template.html',
	//template : '<div ng-include="getTemplateUrl()"></div>',
	
	//templateUrl : '/app/header/header-nav.template.html',
	controller : function($scope, $state, $timeout, $rootScope, $window, $http, $mdDialog, $interval, apiService, $location, $sce, productService, jwtHelper, $anchorScroll, browserStorageService) {
		$scope.$on('menuClickToggle', function(){
			$scope.toggleHeaderClass();
			$rootScope.hambergerClicked=false;
		})
		componentHandler.upgradeAllRegistered();
		$rootScope.sidebarHeader = false;
		$scope.provider = sessionStorage.getItem("provider");
		$scope.notificationObj = [];
		$scope.notificationManageObj = [];
		$rootScope.notificationCount=0;
		$scope.loginUserID='';
		$scope.page = 0;
		$scope.moreNotif = false;
		$rootScope.setLoader = false;
		$rootScope.userRoleAdmin = false;
		$rootScope.userRolePublisher = false;
		
		$rootScope.parentActive = '';
		$rootScope.toggleHeader = true; 
		$scope.toggleHeaderClass=function($event) {
			if($rootScope.toggleHeader==true){
				$rootScope.toggleHeader=false;
			}else{
				$rootScope.toggleHeader=true;
			}
			$rootScope.$broadcast('toggleHeader');
		};
		
		if(browserStorageService.isAdmin() === 'true') $rootScope.userRoleAdmin = true;
		if(browserStorageService.isPublisher() === 'true') $rootScope.userRolePublisher = true;
		$scope.$on('roleCheck', function() {
			if(browserStorageService.isAdmin() === 'true') $rootScope.userRoleAdmin = true;
			if(browserStorageService.isPublisher() === 'true') $rootScope.userRolePublisher = true;
		});
		
		$scope.cas = {
				login : 'false'
        };
        apiService.getCasEnable().then( function(response){
        	$scope.cas.login = response.data.response_body;
        });
        
        $rootScope.isSignUp = {
				enabled : 'true'
        };
        apiService.isSignUpEnabled().then( function(response){
        	$rootScope.isSignUp.enabled = response.data.response_body;
        });
		 var search = $window.location.search     //to check query parameter on url
         .split(/[&||?]/)
         .filter(function (x) { return x.indexOf("=") > -1; })
         .map(function (x) { return x.split(/=/); })
         .map(function (x) {
             x[1] = x[1].replace(/\+/g, " ");
             return x;
         })
         .reduce(function (acc, current) {
             acc[current[0]] = current[1];
             return acc;
         }, {});

        var ticketId = search.ticket;
        $scope.casLogin = function(ticketId){     //CAS Authorization Login
            apiService.casSignIn(ticketId).then(function successCallback(response) {

                if(response.data.content.active == "false"){
                    $rootScope.$emit('isLFAccDisabledEvent',  response.data.content);
                } else {
                    var emailId = response.data.content.emailId;
                    var username = response.data.content.userName;
                    $scope.userData = {"request_body":{"username": username, "emailId": emailId}};
                    apiService.getJwtAuth($scope.userData).then(function successCallback(response) {

                        var authToken = jwtHelper.decodeToken(response.data.jwtToken);
                        $scope.userDetails = [];
                        $scope.signinservice = authToken;
                        productService.setData($scope.signinservice.mlpuser);

                        $scope.localStore = [];
                        $scope.localStore.push(productService.test.firstName, productService.test.userId);
                        $scope.userDetails.userName = productService.test.firstName;
                        $scope.userDetails.userId = productService.test.userId;

                        browserStorageService.setAuthToken(response.data.jwtToken);
                        browserStorageService.setAdmin(response.data.admin);
                        browserStorageService.setPublisher(response.data.publisher);
                        browserStorageService.setUserDetail(JSON.stringify($scope.localStore));
                        $scope.userDetails = JSON.stringify($scope.localStore);
                        if(response.data.admin){
                            browserStorageService.setUserRole('Admin');
                        }
                        $rootScope.$broadcast('roleCheck');
                        localStorage.setItem('firstLogin', response.data.firstLogin);
                        localStorage.setItem('loginPassExpire', '');

                        $scope.$emit('transferUp', {
                            message : true,
                            username : $scope.userDetails.userName
                        });
                    }, function errorCallback(response) {

                });
            }
        }, function errorCallback(response) {
            console.log("Error: ", response);
            oauthDetails = {};
            $scope.userPassInvalid = true;
        });
        };


          if (JSON.parse(browserStorageService.getUserDetail())) {
  			$scope.userDetails = JSON.parse(browserStorageService
  					.getUserDetail());
  			$scope.userDetails.userName = $scope.userDetails[0];
  			$scope.loginUserID = $scope.userDetails[1];
  		}else if(ticketId){
  			 console.log(ticketId);
  			 $scope.casLogin(ticketId);
  		 }
				 
		$scope.$on('userDetailsChanged', function(a){
			$scope.userDetails = JSON.parse(browserStorageService.getUserDetail());
			$scope.userDetails.userName = $scope.userDetails[0];
		})
		
		$scope.$on("updateNotifications",function(event)
		 {
			$scope.getNotificationMessage($scope.userDetails[1], 0,true);			
		 });
			 
		var notificationCount = 0;
		$scope.getNotificationMessage=function (userId, page,methodCallFlag){
			var req = {
			    	  "request_body": {
				    	    "page": page,
				    	    "size": 20
				    	 },
				    	  "request_from": "string",
				    	  "request_id": "string"
				    	};
			
			apiService.getNotification(userId,req).then(function(response) {

				$scope.auth = browserStorageService.getAuthToken();

				if(response.data!=null && response.data.response_body.length >0 ){
					if(methodCallFlag){
						$rootScope.notificationCount = 0;
						notificationCount = 0;
						$scope.notificationManageObj=[];
					}
					angular.forEach(response.data.response_body,function(value,key){
						
						if(response.data.response_body[key].viewed == null){
							$scope.notificationManageObj
							.push({
								message : $sce.trustAsHtml(value.message),
								start : value.start,
								notificationId : value.notificationId
							});
							notificationCount = notificationCount + 1;
						}
						
					});
					$scope.totalCount = response.data.response_body.length;
					$rootScope.notificationCount = notificationCount;
					if($scope.totalCount == 20){
						$scope.page = $scope.page + 1;
						$scope.totalCount = 0;
						$scope.moreNotif = true;
						$scope.getNotificationMessage(userId,$scope.page,false);
					}else{
						$scope.moreNotif = false;
					}
				}else{
					if($scope.page == 0){
						$rootScope.notificationCount = 0;	
					}else{
						$rootScope.notificationCount = notificationCount;
					}
					
					//$scope.notificationManageObj=[];
				}
			});
		}
		

		if($scope.loginUserID!=null && $scope.loginUserID!=''){
			$scope.getNotificationMessage($scope.loginUserID, $scope.page, true);
			$interval(function () {
				var userId = JSON.parse(browserStorageService.getUserDetail())[1]
				if(userId){
					$scope.page = 0;
					$scope.getNotificationMessage(userId,$scope.page,true);
				}
		    }, 30000);
		}
		
		$scope.viewNotification=function (notificationId){
			var userId = JSON.parse(browserStorageService.getUserDetail())[1];
			
			var req = {
				    method: 'PUT',
				    url: '/api/notifications/view/'+notificationId+'/user/'+userId
				};
			$http(req).success(function(data, status, headers,config) {
				if(data!=null){
					$scope.page =0;
					//$scope.notificationManageObj=[];
					//$rootScope.notificationCount=0;
					$scope.getNotificationMessage($scope.loginUserID, $scope.page, true);
					$state.go('notificationModule');
				 }
			}).error(function(data, status, headers, config) {
				
			});
		}
		
		$scope.deleteNotification=function (notificationId){
			var userId = JSON.parse(browserStorageService.getUserDetail())[1];
			apiService
			.deleteNotifications(notificationId, userId)
			.then(function(response) {
				$scope.page =0;
				//$scope.notificationManageObj=[];
				//$rootScope.notificationCount=0;
				$rootScope.notificationCount = $rootScope.notificationCount-1;
				$scope.getNotificationMessage($scope.loginUserID, $scope.page, true);
			});
			/*var req = {
				    method: 'DELETE',
				    //url: '/api/notifications/delete/'+notificationId
				    url: '/api/notifications/drop/'+notificationId+'/user/'+$scope.loginUserID
				};
			$http(req).success(function(data, status, headers,config) {
				if(data!=null){
					$scope.getNotificationMessage($scope.loginUserID);
				  }
			}).error(function(data, status, headers, config) {
				
			});*/
		}
			
		
		//$scope.headerName=1;
		//get User image
		$scope.getUserImage = function (userId){
			var req = {
				    method: 'Get',
				    url: '/api/users/userProfileImage/' + userId
				};
			$http(req).success(function(data, status, headers,config) {
				if(data.status){
				    $scope.userImage = data.response_body;
				    $scope.showAltImage = false;
				}
			}).error(function(data, status, headers, config) {
				
			});
		}
		
		$scope.login = function() {
			$scope.successfulLogin = false;
			$scope.successfulLoginMsg = false;
			$scope.successfulLoginSigninSignup = true;
			$rootScope.successfulAdmin = false;
			$scope.showAltImage = true;
			
			if (browserStorageService.getUserDetail() == ""
					|| browserStorageService.getUserDetail() == undefined) {
			} else if (JSON.parse(browserStorageService.getUserDetail())) {
				$scope.userDetails = JSON.parse(browserStorageService
						.getUserDetail());
				$scope.userDetails.userName = $scope.userDetails[0];
				$scope.userDetails.userId = $scope.userDetails[1];
				$scope.userAdmin = $scope.userDetails[2];
				console.log("GET LOCAL: ", JSON.parse(browserStorageService
						.getUserDetail()));
				$scope.successfulLogin = true;
				$scope.successfulLoginMsg = true;
				$scope.successfulLoginSigninSignup = false;
				$scope.successfulLoginMsg = false;
				$rootScope.sidebarHeader = true;
				$scope.toggleHeaderClass();
                //TODO Need to change
				apiService.getUserRole($scope.userDetails.userId)
				.then( function(response){
					console.log("User Roles : ", response);
					if (response.data.status){
						for(var i=0;i<response.data.response_body.length;i++){
							var userRole = response.data.response_body[i]
							if(userRole.name == "Admin"){
								$rootScope.successfulAdmin = true;
								
							}
						}
					}
				},function(error){
					
				});
				
				$scope.getUserImage($scope.userDetails.userId);
				
			}


			$scope.$on('transferUp', function(event, data) {
				//console.log('on working');
				var userId = JSON.parse(browserStorageService.getUserDetail())[1];
				$scope.emitedmessage = data.message;
				$scope.userfirstname = data.username;
				$scope.getNotificationMessage( userId, $scope.page);
				if ($scope.emitedmessage == 'true'
						|| $scope.emitedmessage == true) {
					$scope.successfulLogin = true;
					$scope.successfulLoginMsg = true;
					$scope.successfulLoginSigninSignup = false;
					$rootScope.sidebarHeader = true;
					if($state) {
						//console.log($state);
					}
					$scope.login();
					$timeout(function() {
						$scope.successfulLoginMsg = false;
					}, 2000);
					var urlPath = document.URL.slice(-15);
					if(urlPath != 'modelerResource')$state.go("home");
					

				}
			});
		}
		
		
		$rootScope.$on("CallLoginMethod", function(){
            $scope.login();
            //console.log("CallLoginMethod");
        });
		
		$scope.login();
		
		$scope.logout = function() {
		//$window.location.reload();
//			sessionStorage.setItem("userDetail", "");
//			sessionStorage.setItem("userRole", "");
			browserStorageService.clearUserRole();
			browserStorageService.removeUserDetail();
			localStorage.removeItem("soluId");
			localStorage.removeItem("solutionId");
			browserStorageService.removeAuthToken();
			browserStorageService.removeMktPlaceStorage();
			
			$scope.successfulLoginSigninSignup = true;
			$scope.successfulLogin = false;
			$rootScope.successfulAdmin = false;
			$scope.showAltImage = true;
			$rootScope.sidebarHeader = false;
			$scope.sidebarHeader = false;
			$scope.loginUserID = "";
			if(sessionStorage.getItem("provider") != "LFCAS"){
				$state.go("home");
				$timeout(function() {
					location.reload();
				}, 0);
			}else if(sessionStorage.getItem("provider") == "LFCAS"){
				$window.open('https://identity.linuxfoundation.org/cas/logout?url=' + window.location.origin, '_self');
				localStorage.removeItem("login");
			}
			sessionStorage.removeItem("provider");
			sessionStorage.clear();
    		localStorage.clear();
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
		
		$scope.globalSearch = function(val) {
			if(val == undefined) {
				val = "";
			}
			if($rootScope.valueToSearch == undefined) {
				$rootScope.valueToSearch = "";
			}
			//a quick check to make sure the next search is different
			//done so that it doesn't automatically refresh upon clicking search
			//on an empty search bar
			var refreshSearch = true;
			if($rootScope.valueToSearch == val) {
				refreshSearch = false;
			}
			componentHandler.upgradeAllRegistered();
			// used javascript as model is not getting refreshed
			var val = document.getElementsByName('search')[0].value;
			$rootScope.valueToSearch = val;

			if(val){
				var stateName = $state.$current.name;
				angular.element('.mdl-textfield').addClass('is-focused');
				//Search keyword cannot be empty
				if($rootScope.valueToSearch == '' || $rootScope.valueToSearch === undefined
						|| $rootScope.valueToSearch === null) return;
				$scope.search = $rootScope.valueToSearch;
				$scope.searchText = $rootScope.valueToSearch;
				if(stateName != 'marketPlace' && stateName != 'manageModule') {
					$window.location.href = '/index.html#/marketPlace';
				}
				
			}

			if(refreshSearch){
				$rootScope.$broadcast('scanner-started', {
					searchValue : val
				});
			}
		}
		
		$scope.addSearchFocus = function($event,searchText){ 
			var searchText = document.getElementsByName('search')[0].value
			if(searchText == false || searchText == undefined ) {
				angular.element('.mdl-textfield').removeClass('is-focused');
				angular.element('.sidebar-search-container input').val('');
				
				$scope.globalSearch(searchText);
			}else {
				angular.element('.mdl-textfield').addClass('is-focused');
			}
			$event.stopImmediatePropagation(true);
		};
		
		//Notification functionlity
		$scope.notification = null;
		//Emit for notification
		$scope.$on("notification", function(evt,data){ 
			
		});
		
		$rootScope.showPrerenderedDialog = function(ev, dialogId) {
		    $mdDialog.show({
		      contentElement: dialogId,
		      parent: angular.element(document.body),
		      targetEvent: ev,
		      clickOutsideToClose: true
		    });
		  };
		  
		$scope.showNotification=function(){
			$state.go('notificationModule');
		}  
		
		//get siteInstanceName
		$rootScope.enableOnBoarding = true;
		$rootScope.enableDCAE = false;
		apiService
		.getSiteConfig("site_config")
		.then(
				function(response) {
					$scope.siteConfig = angular.fromJson(response.data.response_body.configValue);
					angular
                    .forEach(
                            $scope.siteConfig.fields,
                            function( value, key) {
                            	 if($scope.siteConfig.fields[key].name == 'siteInstanceName'){
                                     $rootScope.siteInstanceName = $scope.siteConfig.fields[key].data;
                                 }if($scope.siteConfig.fields[key].name == 'Headerlogo' && $scope.siteConfig.fields[key].data != undefined){
                                     $rootScope.headerImage = $scope.siteConfig.fields[key].data.base64;
                                 }if($scope.siteConfig.fields[key].name == 'coBrandingLogo' && $scope.siteConfig.fields[key].data){
                                     //$rootScope.coBrandingImage = $scope.siteConfig.fields[key].data.base64;
                                 }if($scope.siteConfig.fields[key].name == 'Footerlogo'){   
                                     $rootScope.footerImage = $scope.siteConfig.fields[key].data.base64;
									} if($scope.siteConfig.fields[key].name == 'enableOnBoarding'){
	                                    if($scope.siteConfig.fields[key].data.name == 'Enabled'){
	                                    	$rootScope.enableOnBoarding = true;
	                                    } else {
	                                    	$rootScope.enableOnBoarding = false;
	                                    }
	                                } if($scope.siteConfig.fields[key].name == 'EnableDCAE'){
	                                    if($scope.siteConfig.fields[key].data.name == 'Enabled'){
	                                    	$rootScope.enableDCAE = true;
	                                    } else {
	                                    	$rootScope.enableDCAE = false;
	                                    }
	                                }if($scope.siteConfig.fields[key].name == 'Choose Background color' && $scope.siteConfig.fields[key].data  && $rootScope.coBrandingImage){
	                                     $rootScope.coBrandingBg = $scope.siteConfig.fields[key].data;
	                                 }if($scope.siteConfig.fields[key].name == 'Add tooltip to logo' && $scope.siteConfig.fields[key].data  && $rootScope.coBrandingImage){
	                                     $rootScope.logoToolTip = $scope.siteConfig.fields[key].data;
	                                 }
                                $window.document.title = $rootScope.siteInstanceName;
                            });
				},
				function(error) {console.log(error);
		});
		
		//Get Logo Images
		$scope.getLogoImages = function(){
          	 var getLogoImagesReq = {
						method : 'GET',
						url : '/api/site/content/global/coBrandLogo' 
				};

          	 $http(getLogoImagesReq)
					.success(function(data, status, headers, config) {
						if (data) {
							$rootScope.coBrandingImage = '/api/site/content/global/coBrandLogo';
						}
					}).error(function(error) {
						console.log(error);
					});
			}
			$scope.getLogoImages();

		
		  // Fix to load the drop downs in header. Dynamically loaded elements are not registered to material UI.
		  //Hence need to call the register method after the DOM is completely loaded.
		  angular.element(document).ready(function () {
			  if(!(typeof(componentHandler) == 'undefined')){
					componentHandler.upgradeAllRegistered();
			}
		  });
		  
		  $scope.showDocUrl = false;
		  apiService.getDocUrl().then( function(response){
				$scope.docUrl = response.data.response_body;
			});
		  
		//ForgotPassword
          $scope.$on('forgotPassword', function(event, data) {
        	  $scope.emailAddress = '';
        	  $scope.forgot.$setPristine();
        	  $scope.forgot.$setUntouched();      
              if(event.defaultPrevented == false ){
	              $mdDialog.show({
	                    contentElement: '#myDialogForgtpswd',
	                    parent: angular.element(document.body),
	                    targetEvent: event,
	                    clickOutsideToClose: true
	               });
	              event.defaultPrevented = true;
              }
          });
          
          $scope.closePoup = function(){
        	  $mdDialog.hide();
          };
          $scope.forgotPaswd = function(){
              if($scope.forgot.$valid){
                    var dataObj = {"request_body": {"emailId" : $scope.emailAddress}}
                    $http({ method : 'PUT',
                           url : '/api/users/forgetPassword',
                           data : dataObj
                     }).success(function(data, status, headers,config) {
                         $mdDialog.hide();
                         if(data.error_code === "100"){
                        	 $scope.styleclass = 'c-success';
                        	 $scope.icon= '';
                             $scope.msg = "Temporary Password has been sent to your email id.";
                         }
                         else if(data.error_code === "500"){
                             $scope.msg = "Email id not exist.";
                             $scope.styleclass = 'c-warning';
                             $scope.icon = 'report_problem';
                         } else { 
                        	 $scope.msg = "Email id not registered.";
                        	 $scope.styleclass = 'c-warning';
                        	 $scope.icon = 'report_problem';
                         }
                         
                         $location.hash('mdl-layout__header-row');
                         $anchorScroll();
                         $scope.showAlertMessage = true;
                         $timeout(function() {
                         	$scope.showAlertMessage = false;
                         }, 5000);
                         
                     }).error(function(data, status, headers, config) {
                         
                     });
                }
            
          }
          
			$scope.$on("manageTags",function(event, data)
					  {   
							 if(data)
							 $scope.loginUserID = data.userId;							 
						  	$scope.manageTag(event);				  				  
					  });
			$scope.manageTag = function(event,userId){  				 
				  $scope.getalltags();				  			
				  $mdDialog.show({
			        contentElement: '#myDialogTags',
			        parent: angular.element(document.body),
			        targetEvent: event
			    });   				
			}
			 $scope.selected = [];
			 $scope.prevSelected = [];						
			 $scope.showdone = false;
			 $scope.processStatus = false;
			 $scope.flag = false;	
			 $scope.disabledFlag=false;
			 $scope.getalltags = function()
			 {
				if (JSON.parse(browserStorageService.getUserDetail()))
					{
				var loginID = JSON.parse(browserStorageService
		  					.getUserDetail());		  			
				 $scope.loginUserID = loginID[1];
					}
				  var dataObj = {
							"request_body" : {
								 "fieldToDirectionMap": {},
								 "page": 9,
								 "size": 0
							},
							"request_from" : "string",
							"request_id" : "string"
						}				  
					apiService
						.getPreferredTag($scope.loginUserID, dataObj)
						.then(
								function(response) {											                           
									$scope.siteConfigTag = response.data.response_body.prefTags;														
									$scope.ListTag = $scope.siteConfigTag;
									for(var i = 0; i < 2 ; i++)
									 {					 
										 if ($scope.siteConfigTag[i].preferred == "Yes") {
											 $scope.selected.push($scope.siteConfigTag[i]);
											 $scope.prevSelected.push($scope.siteConfigTag[i].tagName);
										  }
									 }
								},
								function(error) {
									console.log(error);
								}); 
			 }			 				  				
			 $scope.toggle = function (item, list) {				 				 			
			    var idx = list.indexOf(item);
			     if (idx > -1) {
			       list.splice(idx, 1);
			     }
			     else {
			       list.push(item);
			     }          
			   };
			 $scope.processCountinue = function()
			 {
				 $scope.processStatus = true;					 
				 $scope.siteConfigTag = $scope.selected;				 
				 $scope.showdone = true;
				 $scope.flag = true;				
			 }			 
			 $scope.back = function()
			 {
				 $scope.processStatus = false;
				 if($scope.flag)
				  {					
					$scope.siteConfigTag = $scope.ListTag ;				    
				    $scope.showdone = false;
				    $scope.flag = !$scope.flag;
				    return;
				  }
				 else(!$scope.flag)
				  {					 									
					 for(var i =0;i< $scope.selected.length;i++)
					 {
						 $scope.selected[i].preferred = false;
					 }					 
					 $scope.siteConfigTag = $scope.ListTag ;
					 $scope.showdone = false;
					 $scope.selected = [];
				  }				 
			 }
			 $scope.cancel = function () {
				 $scope.selected = [];	
				 $scope.prevSelected = [];
				 $scope.showdone = false;
				 $scope.processStatus = false;
				 $scope.flag = false;	
				 $scope.disabledFlag=false;
				 $scope.siteConfigTag= [];
				 $mdDialog.hide();
			  };
			 $scope.submitTag = function () {
				 var submitTag = [];
				 $scope.disabledFlag=true;
				 for(var i =0;i<$scope.siteConfigTag.length; i++)
			      {
					 submitTag.push($scope.siteConfigTag[i].tagName);
				  }
				 $scope.prevSelected;
				 var dataObj = {
							"request_body" : {
								 "dropTagList":  $scope.prevSelected,
								 "tagList":	submitTag							 
							},
							"request_from" : "string",
							"request_id" : "string"
						}
				apiService
					.setPreferredTag($scope.loginUserID, dataObj)
					.then(
							function(data, status, headers, config) {
								if(data.status == 200)
									{
									 $scope.cancel();									
									 $scope.current = $state.current;
									 if($scope.current.component == "marketPlace")
									 $rootScope.$broadcast('loadMarketplace');
									}
							},
							function(error) {
								 $scope.cancel();								
								 console.log(error);
							}); 
			  }; 
          var originatorEv;
          $scope.openMenu = function($mdMenu, ev) {
              originatorEv = ev;
      		angular.element('.notelist_1').parent().addClass('menu_notification_container');
              $mdMenu.open(ev);
            };
            

	},

});