/*
===============LICENSE_START=======================================================
Acumos
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
	//template : '<div ng-include="getTemplateUrl()"></div>',
	
	//templateUrl : '/app/header/header-nav.template.html',
	controller : function($scope, $state,$anchorScroll, $timeout, $location, $rootScope, $window, $http, $mdDialog, apiService) {
		$scope.loginUserID='';
		$scope.totalCount = 0;
		$scope.page = 0;
		//$rootScope.notificationCount= 0;
		$scope.notificationManageObj=[];
		$scope.selectAll = false;
		
		
		if (JSON.parse(localStorage.getItem("userDetail"))) {
			$scope.userDetails = JSON.parse(localStorage
					.getItem("userDetail"));
			$scope.userDetails.userName = $scope.userDetails[0];
			$scope.loginUserID = $scope.userDetails[1];
		}
		
		$scope.getNotificationMessage=function (userId,page){
			
			var req = {
		    	  "request_body": {
			    	    "page": page,
			    	    "size": 20
			    	 },
			    	  "request_from": "string",
			    	  "request_id": "string"
			    	};
				
				apiService.getNotification(userId,req).then(function(response) {
					
				if(response.data!=null && response.data.response_body.length >0 ){
					angular.forEach(
							response.data.response_body,
					function( value, key) {
						$scope.notificationManageObj
						.push({
							message : value.message,
							start : value.start,
							viewed : value.viewed,
							notificationId : value.notificationId
						});
					});
					//$scope.notificationManageObj=response.data.response_body;
					$scope.totalCount = response.data.response_body.length;
					if($scope.totalCount == 20){
						$scope.page = $scope.page + 1;
						$scope.getNotificationMessage($scope.loginUserID,$scope.page);
					}
					/*angular.forEach(data.response_body,function(value,key){
						if(data.response_body[key].viewed == null){
							$rootScope.notificationCount = $rootScope.notificationCount + 1;
						}
						
					});*/
					
				}else{
					$rootScope.notificationCount=0;
					$scope.notificationManageObj=[];
				}
			});
			
		}
		$scope.getNotificationMessage($scope.loginUserID, $scope.page);
		
		$scope.refreshNotification=function(){
			$scope.page = 0;
			$scope.notificationManageObj=[];
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
		/*$scope.markRead=function(){
			
			for (var i = 0; i < $scope.notificationManageObj.length; i++) {
                if ($scope.notificationManageObj[i].Selected) {
                    var fruitId = $scope.notificationManageObj[i].notificationId;
                    var fruitName = $scope.notificationManageObj[i].title;
                   // var message += "Value: " + fruitId + " Text: " + fruitName + "\n";
                    //alert("message=="+fruitId+"===="+fruitName);
                }
            }
			//$scope.viewNotification($scope.loginUserID);
		};*/
		
		$scope.markRead = function(){
			for (var i = 0; i < $scope.notificationManageObj.length; i++) {
                if ($scope.notificationManageObj[i].Selected) {
                	if ($scope.notificationManageObj[i].viewed != null)
                	{
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
					
					apiService
					.markReadNotifications(notificationId, $scope.loginUserID)
					.then(function(response) {
						$rootScope.notificationCount = $rootScope.notificationCount - 1;
						$scope.notificationManageObj=[];
						$scope.page = 0;
						$scope.getNotificationMessage($scope.loginUserID, $scope.page);
						
					});
                }
                	
					$scope.notificationManageObj[i].Selected = false;
                }
            }
			$scope.selectAll= false;
			angular.element(document.querySelector("#checkbox-label")).removeClass("is-checked");
		
       };
		
		$scope.trashNotification = function(){
			for (var i = 0; i < $scope.notificationManageObj.length; i++) {
                if ($scope.notificationManageObj[i].Selected) {
                    var notificationId = $scope.notificationManageObj[i].notificationId;
                    var notificationName = $scope.notificationManageObj[i].title;
					
					apiService
					.deleteNotifications(notificationId, $scope.loginUserID)
					.then(function(response) {
						$scope.notificationManageObj=[];
						$rootScope.notificationCount = $rootScope.notificationCount-1;
						$scope.page = 0;
						$scope.getNotificationMessage($scope.loginUserID, $scope.page);
					});
                }
            }
			$scope.selectAll= false;
			angular.element(document.querySelector("#checkbox-label")).removeClass("is-checked");
       };
       
       $scope.removeSelectAll = function(){
    	   debugger
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

	},

});