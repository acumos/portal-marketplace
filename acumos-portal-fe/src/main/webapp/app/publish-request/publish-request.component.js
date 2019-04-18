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
angular
		.module('publishRequest', [ 'jkAngularRatingStars' ])
		.component(
				'publishRequest',
				{

					templateUrl : './app/publish-request/md-publish-request.template.html',
					controller : function($scope, $location, $http, $rootScope,
							$stateParams, $sessionStorage, $localStorage,
							$mdDialog, $state, $window, apiService, $anchorScroll, $timeout, $document, $filter, $sce, browserStorageService) {
						
						var user= JSON.parse(browserStorageService.getUserDetail());
						
						$scope.showAlertMessage = false;
						if(user) $scope.loginUserID = user[1];
						$scope.pageNumber = 0;
						$scope.totalPages = 0;
						$scope.allPublishRequestLength = 0;
						$scope.requestResultSize = 10;

						$scope.setPageStart = 0;
                        $scope.selectedPage = 0;
                        $scope.setStartCount = function(val){
                              if(val == "preBunch"){$scope.setPageStart = $scope.setPageStart-5}
                              if(val == "nextBunch"){$scope.setPageStart = $scope.setPageStart+5}
                              if(val == "pre"){ 
                                    if($scope.selectedPage == $scope.setPageStart)
                                    {
                                    	$scope.setPageStart = $scope.setPageStart - 1;
                                    	$scope.selectedPage = $scope.selectedPage - 1;
                                    }
                                    else
                                    	$scope.selectedPage = $scope.selectedPage - 1;
                                    }
                              if(val == "next"){
                                    if($scope.selectedPage == $scope.setPageStart + 4)
                                          {
                                          	$scope.setPageStart = $scope.setPageStart + 1;
                                          	$scope.selectedPage = $scope.selectedPage + 1;
                                          }
                                    else
                                    	$scope.selectedPage = $scope.selectedPage + 1;
                                    }
                        }
						
						//Comments Popover starts
                        $scope.activeComment = null;
                        $scope.isActiveComment = function (reqId) {
                        	return $scope.activeComment == reqId;
                        }
                        $scope.setActiveComment = function(reqId) {
                        	$scope.activeComment = ($scope.isActiveComment(reqId)) ? null : reqId;
                        }
						//Popover Ends
						
						//Open popup 
			            $scope.showModalPublishReq = function(index, req, modelName, publishRequestId){
			            	$scope.requestIndex = index;
			            	$scope.pbReqId = publishRequestId;
			            	$scope.requestApprovalModal = req;
			            	$scope.requestedModelName = modelName;
			            	$scope.descriptionPop = '';
			        	  $mdDialog.show({
			        		  contentElement: '#publishRequestModal',
			        		  parent: angular.element(document.body),
			        		  clickOutsideToClose: true
			        	  });
			        	  $scope.publishRequestForm.$setUntouched();
			              $scope.publishRequestForm.$setPristine();
			            }
			            
			            $scope.closePoup = function(){
			              $scope.descriptionPop = '';
		              	  $mdDialog.hide();
		                }
			            
			            $scope.filterChange = function(pagination, size) {
			            	$scope.allPublishRequest = [];
			            	$scope.allPublishRequestLength = 0;
			            	$scope.requestResultSize = size;
			            	$scope.loadPublishRequest(0)
			            }
						$scope.loadPublishRequest = function(pageNumber) {
							$scope.allPublishRequest = [];
							$scope.SetDataLoaded = true;
							$rootScope.setLoader = true;
							$scope.pageNumber = pageNumber;
							$scope.selectedPage = pageNumber;
							var getPublishRequestUrl = 'api/publish/request/';
							var reqObject = {
											  "request_body": {
											    "pageRequest": {
											      "fieldToDirectionMap": {"created" : "DESC"},
											      "page": $scope.pageNumber,
											      "size": $scope.requestResultSize
											    }
											  }
											}
							
							$http(
									{
										method : 'POST',
										url : getPublishRequestUrl,
										data: reqObject
									})
									.then(
											function successCallback(response) {
												$scope.allPublishRequest = response.data.response_body;
												console.log($scope.allPublishRequest);
												$scope.totalPages = response.data.totalPages;
												$scope.totalElements = response.data.totalElements;
												$scope.allPublishRequestLength = response.data.totalElements;
												$scope.SetDataLoaded = false;
												$rootScope.setLoader = false;
											},function errorCallback(response) {
												$scope.SetDataLoaded = false;
												$rootScope.setLoader = false;
										});
							
							
							apiService.isPublishOwnRequestsEnabled().then(function(response) {
								
								$scope.publishOwnRequestsEnabled = response.data.response_body;
								
							});
						}
						if($scope.loginUserID)
							$scope.loadPublishRequest(0);
						$scope.showAlertMessage = false;

						$scope.publishReqeuest = function(index, publishVal){
							$scope.publishVal = publishVal;
							var publishRequestCode = 'DC';
							if(publishVal == 'approve'){
								publishRequestCode = 'AP'
							}
							
							var publishRequestUrl = 'api/publish/request/' + $scope.pbReqId;
							var reqObj = {
									  "request_body": {
										    "publishRequestId": $scope.pbReqId,
										    "approverId": $scope.loginUserID,
										    "comment": $scope.descriptionPop,
										    "requestStatusCode": publishRequestCode
										  }
										}
							
							$http(
									{
										method : 'PUT',
										url : publishRequestUrl,
										data: reqObj
									})
									.then(
											function successCallback(response) {
												
												if($scope.publishVal == 'approve'){
													$scope.msg = "The "+ response.data.response_body.solutionName + " publication request has been successfully approved.";
												} else {
													$scope.msg = "The "+ response.data.response_body.solutionName + " publication request has been successfully declined.";
												}
												
												$scope.icon = '';
												$scope.styleclass = 'c-success';
												$scope.showAlertMessage = true;
												$timeout(
														function() {
															$scope.showAlertMessage = false;
														}, 3000);
												$mdDialog.hide();
												if($scope.requestIndex != undefined){
													$scope.allPublishRequest[$scope.requestIndex] = response.data.response_body;
												}
											},function errorCallback(response) {
												$scope.msg = "Error Occured while updating the publish request";
												$scope.icon = 'report_problem';
												$scope.styleclass = 'c-error';
												$scope.showAlertMessage = true;
												$timeout(
														function() {
															$scope.showAlertMessage = false;
														}, 3000);
										});
							
						}
						
					}
				});



