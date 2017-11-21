'use strict';

app.component('headerNav',{
	templateUrl : 'app/header/md-header-nav.template.html',
	//template : '<div ng-include="getTemplateUrl()"></div>',
	
	//templateUrl : '/app/header/header-nav.template.html',
	controller : function($scope, $state, $timeout, $rootScope, $window, $http, $mdDialog, $interval, apiService) {
		$scope.provider = sessionStorage.getItem("provider");
		$scope.notificationObj = [];
		$scope.notificationManageObj = [];
		$rootScope.notificationCount=0;
		$scope.loginUserID='';
		$scope.page = 0;
		$scope.moreNotif = false;
	// Hardcode Priva
		/*$scope.getTemplateUrl = function() {
			 if(localStorage.getItem("HeaderNameVar")=="manageModule"){
				 return '/app/header/header-nav.template1.html';
			 }else{
				 return '/app/header/header-nav.template2.html';
			 }
			  
		   };*/
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
		
		if (JSON.parse(localStorage.getItem("userDetail"))) {
			$scope.userDetails = JSON.parse(localStorage
					.getItem("userDetail"));
			$scope.userDetails.userName = $scope.userDetails[0];
			$scope.loginUserID = $scope.userDetails[1];
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
					angular.forEach(
							response.data.response_body,
					function( value, key) {
						$scope.notificationManageObj
						.push({
							message : value.message,
							start : value.start,
							notificationId : value.notificationId
						});
					});
					$scope.totalCount = response.data.response_body.length;
					
					angular.forEach(response.data.response_body,function(value,key){
						
						if(response.data.response_body[key].viewed == null){
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
			sessionStorage.removeItem("auth_token");
			sessionStorage.removeItem("provider");
			sessionStorage.clear();
    		localStorage.clear();
			$scope.successfulLoginSigninSignup = true;
			$scope.successfulLogin = false;
			$rootScope.successfulAdmin = false;
			$scope.showAltImage = true;
			$rootScope.sidebarHeader = false;
			$scope.sidebarHeader = false;
			$scope.loginUserID = "";
			$state.go("home");
			
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
			$rootScope.valueToSearch = $scope.searchText;
			
			if(val){
				$rootScope.$broadcast('scanner-started', {
					searchValue : val
				});
			}
			angular.element('#fixed-header-drawer-exp1').val('');
			
			var urlPathMarketPlace = document.URL.slice(-11);
			var urlPathManageModule = document.URL.slice(-12);
			if(urlPathMarketPlace != 'marketPlace' && urlPathManageModule != 'manageModule')$window.location.href = '/index.html#/marketPlace';
		}
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
                                }if($scope.siteConfig.fields[key].name == 'headerLogo'){
                                    $rootScope.headerImage = $scope.siteConfig.fields[key].data;
                                }if($scope.siteConfig.fields[key].name == 'footerLogo'){
                                    $rootScope.footerImage = $scope.siteConfig.fields[key].data;
                                }
                                
                            });
				},
				function(error) {console.log(error);
		});

		
		  // Fix to load the drop downs in header. Dynamically loaded elements are not registered to material UI.
		  //Hence need to call the register method after the DOM is completely loaded.
		  angular.element(document).ready(function () {
			  if(!(typeof(componentHandler) == 'undefined')){
					componentHandler.upgradeAllRegistered();
			}
		  });
	},

});