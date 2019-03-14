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

app.component('notificationModule',{
	templateUrl : '/app/notification/notification-details.template.html',
	controller : function($scope, $state,$anchorScroll, $timeout, $location, $rootScope, $window, $http, $mdDialog, $sce, apiService, browserStorageService, $filter) {
		$scope.loginUserID='';
		$scope.totalCount = 0;
		$scope.page = 0;
		//$rootScope.notificationCount= 0;
		$scope.notificationManageObj=[];
		$scope.selectAll = false;
		
		$scope.orderByField = 'start';
		$scope.reverseSort = true;

		if (JSON.parse(browserStorageService.getUserDetail())) {
			$scope.userDetails = JSON.parse(browserStorageService
					.getUserDetail());
			$scope.userDetails.userName = $scope.userDetails[0];
			$scope.loginUserID = $scope.userDetails[1];
		}

		if (browserStorageService.getUserDetail()) {
			$scope.auth = browserStorageService
					.getAuthToken();
		}

		$scope.getNotificationMessage=function (userId,page){
			
			var req = {
		    	  "request_body": {
			    	    "page": page,
			    	    "size": 20
			    	 } 
			    	};
				
				apiService.getNotification(userId,req).then(function(response) {
					
				if(response.data != null && response.data.response_body.length > 0 ){
					angular.forEach(response.data.response_body,function( value, key) {
						$scope.notificationManageObj
						.push({
							message : $sce.trustAsHtml(value.message),
							start : value.start,
							startdateForSorting : $filter('date')(value.start, "MM/dd/yyyy"),
							viewed : value.viewed,
							notificationId : value.notificationId
						});
					});
					$scope.totalCount = response.data.response_body.length;
					if($scope.totalCount == 20){
						$scope.page = $scope.page + 1;
						$scope.getNotificationMessage($scope.loginUserID,$scope.page);
					}
				}else{
					
					/*$rootScope.notificationCount=0;
					$scope.notificationManageObj=[];*/
				}
			});
			
		}
		$scope.getNotificationMessage($scope.loginUserID, $scope.page);
		
		$scope.refreshNotification=function(){
			$scope.page = 0;
			$scope.notificationManageObj=[];
			$scope.selectAllStatus = false;
			$scope.getNotificationMessage($scope.loginUserID, $scope.page);
		}
		
        $scope.viewNotification=function (notificationId){
			var req = {
				    method: 'PUT',
				    url: '/api/notifications/view/'+notificationId+'/user/'+$scope.loginUserID
				};
			$http(req).success(function(data, status, headers,config) {
				if(data!=null){
					$scope.notificationManageObj=[];
					$scope.page = 0;
					$scope.getNotificationMessage($scope.loginUserID, $scope.page);
				 }
			}).error(function(data, status, headers, config) {
				
			});
		}
		
		$scope.markRead = function(){
			$scope.methodCallCounter = 0;
			$scope.methodResponseCounter = 0;
			for (var i = 0; i < $scope.notificationManageObj.length; i++) {
                if ($scope.notificationManageObj[i].Selected){
                	if ($scope.notificationManageObj[i].viewed != null){
	                	$anchorScroll(); 							// used to scroll to the id 
						$scope.msg = "Already marked as read."; 
						$scope.icon = 'report_problem';
						$scope.styleclass = 'c-success';
						$scope.showReadAlertMessage = true;
						$timeout(function() {
							$scope.showReadAlertMessage = false;
						}, 2500);
                	}else{
	                    var notificationId = $scope.notificationManageObj[i].notificationId;
	                    var notificationName = $scope.notificationManageObj[i].title;
	                    $scope.methodCallCounter = $scope.methodCallCounter + 1;
						apiService
						.markReadNotifications(notificationId, $scope.loginUserID)
						.then(function(response) {
							$rootScope.notificationCount = $rootScope.notificationCount - 1;
							$scope.methodResponseCounter = $scope.methodResponseCounter + 1;
							if($scope.methodResponseCounter == $scope.methodCallCounter){
								$scope.refreshNotification();
							}
							
							
						});
                	}
					$scope.notificationManageObj[i].Selected = false;
                }
            }
			$scope.removeSelectAll();
			$scope.selectAll= false;
			angular.element(document.querySelector("#checkbox-label")).removeClass("is-checked");
		
       };
		
		$scope.trashNotification = function(){
			$scope.trashMethodCallCounter = 0;
			$scope.trashMethodResponseCounter = 0;
			
			for (var i = 0; i < $scope.notificationManageObj.length; i++) {
                if ($scope.notificationManageObj[i].Selected) {
                    var notificationId = $scope.notificationManageObj[i].notificationId;
                    var notificationName = $scope.notificationManageObj[i].title;
                    $scope.trashMethodCallCounter = $scope.trashMethodCallCounter + 1;
					apiService
					.deleteNotifications(notificationId, $scope.loginUserID)
					.then(function(response) {
						$scope.notificationManageObj=[];
						$rootScope.notificationCount = $rootScope.notificationCount-1;
						$scope.trashMethodResponseCounter = $scope.trashMethodResponseCounter + 1;
						if($scope.trashMethodResponseCounter == $scope.trashMethodCallCounter){
							$scope.refreshNotification();
						}
					});
                }
            }
			$scope.removeSelectAll();
			$scope.selectAll= false;
			angular.element(document.querySelector("#checkbox-label")).removeClass("is-checked");
			
       };
       
       $scope.removeSelectAll = function(){
    	   if($scope.selectAll == true){
    		   $scope.selectAll = false;
    		   $scope.selectAllStatus = false;
    	   }
       }
       
       
       $scope.setSelectAll = function(selected){
    	   $scope.selectAll = selected;
    	   $scope.selectAllStatus = true;
    	  /*f($scope.selectAll)
    	  	{$scope.selectAll = false;}
    	  else 
    	    {$scope.selectAll = true;}*/
    	   for (var i = 0; i < $scope.notificationManageObj.length; i++) {
   	        $scope.notificationManageObj[i].Selected = $scope.selectAll;
   	        if($scope.selectAll)
   	        angular.element(document.querySelector("#checkBox_label_" + i)).addClass("is-checked");
   	        else 
   	        angular.element(document.querySelector("#checkBox_label_" + i)).removeClass("is-checked");
   	       // $("checkBox").checked(true);
       	  }
    	  
       };
       
       //get Admin user.
       $scope.userDetailsFetch = function(){
    	   $scope.adminDetails = [];
			apiService
			.getAllUserCount()
			.then(
					function(response) {
						$scope.userDetails = response.data.response_body;
						angular.forEach($scope.userDetails,function(value,key){
							if(value.active == "true"){
								if(value.userAssignedRolesList[0].name == "Admin" || value.userAssignedRolesList[0].name == "admin"){
									if($scope.adminDetails.length < 1){
										$scope.adminDetails.push({
											created : value.created,
											firstName : value.firstName,
											emailId : value.emailId,
											lastName : value.lastName
										});
									}else if($scope.adminDetails[0].created > value.created){
										$scope.adminDetails1 = [];
										$scope.adminDetails1.push({
											created : value.created,
											firstName : value.firstName,
											emailId : value.emailId,
											lastName : value.lastName
										});
										$scope.adminDetails = $scope.adminDetails1;
									}
								}
							}
						});
						
					},
					function(error) {
						console.log(error);
					});
			}
			$scope.userDetailsFetch();
			
			$scope.filterByDateSubject = function(notification) {	
				if(!$scope.search) return true; 
				return ( (angular.lowercase(notification.startdateForSorting).indexOf(angular.lowercase($scope.search)) !== -1) ||
						(angular.lowercase((notification.message.toString())).indexOf(angular.lowercase($scope.search)) !== -1) );  		
		    };
			

	},

});