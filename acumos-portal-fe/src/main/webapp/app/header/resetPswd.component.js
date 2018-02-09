'use strict';

angular
		.module('resetPswd')
		.component(
				'resetPswd',
				{
					templateUrl : '/app/header/resetPswd.template.html',
					controller : function($scope,$compile, $location, $http) {
						//$scope.userid = sessionStorage.getItem("SessionName");
					
					$scope.changePswd = function(){
						if($scope.resetPswd.$invalid){return}
						//API CALL
						$scope.userDetails = JSON.parse(localStorage.getItem("userDetail"));
						//var userId = localStorage.getItem("userDetail");
						//console.log(localStorage.getItem("userDetail"));
						var req = {
							    method: 'PUT',
							    url: '/api/users/changePassword',
							    data:{
							    	"userId":$scope.userDetails[1],
							    	"oldPassword":$scope.oldPswd,
							    	"newPassword":$scope.newPswd
							    },
							    headers: {'Content-Type': 'application/json'}
							}
						//console.log(angular.toJson(req));
						//console.log(angular.toJson(req.data));
						$http(req).success(function(data, status, headers,config) {
							alert("Your password updated successfully")
		                	 $scope.ribbonMsg = "Your Password is updated successfully";
		                	 $scope.ribbonShow = true;
		                	 $location.path('/marketPlace')
		                     }).error(function(data, status, headers, config) {
		                       // called asynchronously if an error occurs
		                       // or server returns response with an error status.
		                    	 $scope.ribbonShow = true;
		                    	 $scope.ribbonMsg = "Your Password is not updated";
		                       //console.log(status);
		                 });
					};
				
					}
				});
