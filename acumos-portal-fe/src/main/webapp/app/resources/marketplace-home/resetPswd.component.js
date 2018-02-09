'use strict';

angular
		.module('resetPswd')
		.component(
				'resetPswd',
				{
					templateUrl : './js/marketplace-home/resetPswd.template.html',
					controller : function($scope,$compile, $location, $http) {
						console.log('userIdJincy');
						//$scope.userid = sessionStorage.getItem("SessionName");
					
					$scope.changePswd = function(){
						if($scope.resetPswd.$invalid){return}
						//API CALL
						var userId = sessionStorage.getItem("SessionName");
						var req = {
							    method: 'PUT',
							    url: 'http://localhost:8080/users/changePassword',
							    data:{
							    	"userId":userId ,
							    	"oldPassword":$scope.oldPswd,
							    	"newPassword":$scope.newPswd
							    },
							    headers: {'Content-Type': 'application/json'}
							}
						console.log(angular.toJson(req));
						console.log(angular.toJson(req.data));
						$http(req).success(function(data, status, headers,config) {
		                	 console.log(data);
		                	 $scope.ribbonMsg = "Your Password is updated successfully";
		                	 $scope.ribbonShow = true;
		                     }).error(function(data, status, headers, config) {
		                       // called asynchronously if an error occurs
		                       // or server returns response with an error status.
		                    	 $scope.ribbonShow = true;
		                    	 $scope.ribbonMsg = "Your Password is not updated";
		                       console.log(status);
		                 });
					};
				
					}
				});
