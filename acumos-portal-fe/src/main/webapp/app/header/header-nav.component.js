'use strict';

app.component('headerNav',{
	templateUrl : 'app/header/md-header-nav.template.html',
	//template : '<div ng-include="getTemplateUrl()"></div>',
	
	//templateUrl : '/app/header/header-nav.template.html',
	controller : function($scope, $state, $timeout, $rootScope, $window, $http, $mdDialog, $interval, apiService, $location, productService, jwtHelper, $anchorScroll) {
		componentHandler.upgradeAllRegistered();
		$rootScope.sidebarHeader = false;
		$scope.provider = sessionStorage.getItem("provider");
		$scope.notificationObj = [];
		$scope.notificationManageObj = [];
		$rootScope.notificationCount=0;
		$scope.loginUserID='';
		$scope.page = 0;
		$scope.moreNotif = false;
		
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
		
		$scope.cas = {
				login : 'false'
        };
        apiService.getCasEnable().then( function(response){
        	$scope.cas.login = response.data.response_body;
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
        		  }
        		  else{
		        		  var emailId = response.data.content.emailId;
		        		  var username = response.data.content.userName;
		        		  $scope.userData = {"request_body":{"username": username, "emailId": emailId}};
		        		  apiService.getJwtAuth($scope.userData).then(function successCallback(response) {
		        			  
		                	  localStorage.setItem('auth_token', response.data.jwtToken);
		                	  var authToken = jwtHelper.decodeToken(response.data.jwtToken);
		        		  
		                  angular.forEach(response.data.userAssignedRolesList, function(value, key) {
		            		  if(value.name == 'Admin' || value.name == 'admin'){
		            			  localStorage.setItem('userRole', 'Admin');
		            		  }
		            		});
		                  localStorage.setItem('loginPassExpire', '');
		                  
		                  $scope.signinservice = authToken;
		                  productService.setData($scope.signinservice.mlpuser);
		                  
		                  var test = productService.test;
		                  
		                  $scope.userfirstname = productService.test.firstName;
		                  $scope.userid = productService.test.userId;
		                  
		                  $scope.localStore = [];
		                  $scope.localStore.push($scope.userfirstname, $scope.userid);
		                  
		                  $scope.$emit('transferUp', {
		                        message : true,
		                        username : $scope.userfirstname
		                  });
		                  localStorage.setItem('userDetail', JSON.stringify($scope.localStore));
		    		  }, function errorCallback(response) {
		    			  
		    		  });
        		  }
                  }, function errorCallback(response) {
	                        console.log("Error: ", response);
	                        oauthDetails = {};
	                        $scope.userPassInvalid = true;
	                  	});
          };
		
          
          if (JSON.parse(localStorage.getItem("userDetail"))) {
  			$scope.userDetails = JSON.parse(localStorage
  					.getItem("userDetail"));
  			$scope.userDetails.userName = $scope.userDetails[0];
  			$scope.loginUserID = $scope.userDetails[1];
  		}else if(ticketId){
  			 console.log(ticketId);
  			 $scope.casLogin(ticketId);
  		 }
				 
		$scope.$on('userDetailsChanged', function(a){
			$scope.userDetails = JSON.parse(localStorage.getItem("userDetail"));
			$scope.userDetails.userName = $scope.userDetails[0];
		})
		
		$scope.getNotificationMessage=function (userId, page){
			var req = {
			    	  "request_body": {
				    	    "page": page,
				    	    "size": 20
				    	 },
				    	  "request_from": "string",
				    	  "request_id": "string"
				    	};
			//$rootScope.notificationCount=0;
			//$scope.notificationObj = [];
			apiService.getNotification(userId,req).then(function(response) {
				if (!$scope.moreNotif){
					$rootScope.notificationCount=0;
					$scope.notificationManageObj=[];
				}
				if(response.data!=null && response.data.response_body.length >0 ){
					/*angular.forEach(
							response.data.response_body,
					function( value, key) {
						$scope.notificationManageObj
						.push({
							message : value.message,
							start : value.start,
							notificationId : value.notificationId
						});
					});*/
					$scope.totalCount = response.data.response_body.length;
					
					angular.forEach(response.data.response_body,function(value,key){
						
						if(response.data.response_body[key].viewed == null){
							$scope.notificationManageObj
							.push({
								message : value.message,
								start : value.start,
								notificationId : value.notificationId
							});
							$rootScope.notificationCount = $rootScope.notificationCount + 1;
						}
						
					});
					
					if($scope.totalCount == 20){
						$scope.page = $scope.page + 1;
						$scope.totalCount = 0;
						$scope.moreNotif = true;
						$scope.getNotificationMessage($scope.loginUserID,$scope.page);
					}else{
						$scope.moreNotif = false;
					}
				}else{
					$rootScope.notificationCount=0;
					$scope.notificationManageObj=[];
				}
			});
		}
		
		$interval(function () {
			if($scope.loginUserID){
				$scope.page =0;
				$scope.getNotificationMessage($scope.loginUserID,$scope.page);
			}
	    }, 30000);
		
		/*$scope.getNotificationCount=function (){
			var req = {
				    method: 'Get',
				    url: '/api/notifications/count'
				};
			$http(req).success(function(data, status, headers,config) {
				if(data!=null){
					$scope.notificationCount=data.response_body.count;
				  }
			}).error(function(data, status, headers, config) {
				
			});
		}*/
		
		if($scope.loginUserID!=null && $scope.loginUserID!=''){
			$scope.getNotificationMessage($scope.loginUserID, $scope.page);
			//$scope.getNotificationCount();
		}
		
		$scope.viewNotification=function (notificationId){
			
			var req = {
				    method: 'PUT',
				    url: '/api/notifications/view/'+notificationId+'/user/'+$scope.loginUserID
				};
			$http(req).success(function(data, status, headers,config) {
				if(data!=null){
					$scope.page =0;
					//$scope.notificationManageObj=[];
					//$rootScope.notificationCount=0;
					$scope.getNotificationMessage($scope.loginUserID, $scope.page);
					$state.go('notificationModule');
				 }
			}).error(function(data, status, headers, config) {
				
			});
		}
		
		$scope.deleteNotification=function (notificationId){
			apiService
			.deleteNotifications(notificationId, $scope.loginUserID)
			.then(function(response) {
				$scope.page =0;
				//$scope.notificationManageObj=[];
				//$rootScope.notificationCount=0;
				$rootScope.notificationCount = $rootScope.notificationCount-1;
				$scope.getNotificationMessage($scope.loginUserID, $scope.page);
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
			
			if (localStorage.getItem("userDetail") == ""
					|| localStorage.getItem("userDetail") == undefined) {
			} else if (JSON.parse(localStorage.getItem("userDetail"))) {
				$scope.userDetails = JSON.parse(localStorage
						.getItem("userDetail"));
				$scope.userDetails.userName = $scope.userDetails[0];
				$scope.userDetails.userId = $scope.userDetails[1];
				$scope.userAdmin = $scope.userDetails[2];
				console.log("GET LOCAL: ", JSON.parse(localStorage
						.getItem("userDetail")));
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
				$scope.emitedmessage = data.message;
				$scope.userfirstname = data.username;

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
			localStorage.setItem("userDetail", "");
			localStorage.setItem("userRole", "");
			localStorage.removeItem("userDetail");
			localStorage.removeItem("soluId");
			localStorage.removeItem("solutionId");
			localStorage.removeItem("auth_token");
			
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
			componentHandler.upgradeAllRegistered();
			// used javascript as model is not getting refreshed
			var val = document.getElementsByName('search')[0].value;
			$rootScope.valueToSearch = val;

			if(val){
				var stateName = $state.$current.name;
				angular.element('.mdl-textfield').addClass('is-focused');
				$scope.search = $rootScope.valueToSearch;
				$scope.searchText = $rootScope.valueToSearch;
				if(stateName != 'marketPlace' && stateName != 'manageModule')$window.location.href = '/index.html#/marketPlace';
				
			}

			if(val){

				$rootScope.$broadcast('scanner-started', {
					searchValue : val
				});
			}
			//}
		}
		
		$scope.addSearchFocus = function($event,searchText){ 
			var searchText = document.getElementsByName('search')[0].value
			if(searchText == false || searchText == undefined ) {
				angular.element('.mdl-textfield').removeClass('is-focused');
				angular.element('.sidebar-search-container input').val('');
				$rootScope.valueToSearch = '';
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
                            	 if($scope.siteConfig.fields[key].label == 'siteInstanceName'){
                                     $rootScope.siteInstanceName = $scope.siteConfig.fields[key].data;
                                 }if($scope.siteConfig.fields[key].label == 'Headerlogo' && $scope.siteConfig.fields[key].data != undefined){
                                     $rootScope.headerImage = $scope.siteConfig.fields[key].data.base64;
                                 }if($scope.siteConfig.fields[key].label == 'coBrandingLogo' && $scope.siteConfig.fields[key].data){
                                     //$rootScope.coBrandingImage = $scope.siteConfig.fields[key].data.base64;
                                 }if($scope.siteConfig.fields[key].label == 'Footerlogo'){   
                                     $rootScope.footerImage = $scope.siteConfig.fields[key].data.base64;
									} if($scope.siteConfig.fields[key].label == 'EnableOnboarding'){
	                                    if($scope.siteConfig.fields[key].data.name == 'Enabled'){
	                                    	$rootScope.enableOnBoarding = true;
	                                    } else {
	                                    	$rootScope.enableOnBoarding = false;
	                                    }
	                                } if($scope.siteConfig.fields[key].label == 'EnableDCAE'){
	                                    if($scope.siteConfig.fields[key].data.name == 'Enabled'){
	                                    	$rootScope.enableDCAE = true;
	                                    } else {
	                                    	$rootScope.enableDCAE = false;
	                                    }
	                                }if($scope.siteConfig.fields[key].label == 'Choose Background color' && $scope.siteConfig.fields[key].data  && $rootScope.coBrandingImage){
	                                     $rootScope.coBrandingBg = $scope.siteConfig.fields[key].data;
	                                 }if($scope.siteConfig.fields[key].label == 'Add tooltip to logo' && $scope.siteConfig.fields[key].data  && $rootScope.coBrandingImage){
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
						url : '/site/api-manual/Solution/global/coBrandLogo' 
				};

          	 $http(getLogoImagesReq)
					.success(
							function(data, status, headers,
									config) {
								if(data.response_body.length > 0) {
									$rootScope.coBrandingImage = "/site/binaries/content/gallery/acumoscms/global/coBrandLogo/" + data.response_body[0];
								
								}
							}).error(
									function(data, status, headers,
											config) {
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
              
              $mdDialog.show({
                    contentElement: '#myDialogForgtpswd',
                    parent: angular.element(document.body),
                    targetEvent: event,
                    clickOutsideToClose: true
                });
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
          
          var originatorEv;
          $scope.openMenu = function($mdMenu, ev) {
              originatorEv = ev;
      		angular.element('.notelist_1').parent().addClass('menu_notification_container');
              $mdMenu.open(ev);
            };
            

	},

});