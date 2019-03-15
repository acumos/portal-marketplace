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
		.module('onboardingHistory', [ 'jkAngularRatingStars' ])
		.component(
				'onboardingHistory',
				{

					templateUrl : './app/onboarding-history/onboarding-history.template.html',
					controller : function($scope, $location, $http, $rootScope,
							$stateParams, $sessionStorage, $localStorage,
							$mdDialog, $state, $window, apiService, $anchorScroll, $timeout, $document, $filter, $sce, browserStorageService) {
						
						var user= JSON.parse(browserStorageService.getUserDetail());
						
						$scope.showAlertMessage = false;
						if(user) $scope.loginUserID = user[1];
						$scope.pageNumber = 0;
						$scope.totalPages = 0;
						$scope.allOnboardingListLength = 0;
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
											
						//Open popup 
			            $scope.showViewResult = function(taskId, solId, revId, modDate, statusCd){
			        	  $mdDialog.show({
			        		  contentElement: '#ViewResult',
			        		  parent: angular.element(document.body),
			        		  clickOutsideToClose: true
			        	  });
			        	  $scope.viewTaskResults(taskId, solId, revId, modDate, statusCd);
			            }			            
			            
			            $scope.closePoup = function(){
			              $scope.descriptionPop = '';
		              	  $mdDialog.hide();
		                }
			            
			            $scope.filterChange = function(pagination, size) {
			            	$scope.allOnBoardingHistoryTaskList = [];
			            	$scope.allOnboardingListLength = 0;
			            	$scope.requestResultSize = size;
			            	$scope.loadOnBoardingHistoryTaskList(0)
			            } 
						$scope.loadOnBoardingHistoryTaskList = function(pageNumber, filterValue) {							
							$scope.allOnBoardingHistoryTaskList = [];
							$scope.onboardingListsFirst = [];
							$scope.onboardingLists = [];							
							$scope.SetDataLoaded = true;
							$rootScope.setLoader = true;
							$scope.pageNumber = pageNumber;
							$scope.selectedPage = pageNumber;							
							var reqObject = {
											  "request_body": {																							
												    "pageRequest": {
												        "fieldToDirectionMap": {},
												        "page": pageNumber,
												        "size": $scope.requestResultSize
												      },
												      "taskStatus" : filterValue == "undefined" ? null : filterValue
											  }
											}							
							apiService
							    .onBoardingHistoryTaskList($scope.loginUserID, reqObject)
									.then(
											function successCallback(response) {
												$scope.allOnBoardingHistoryTaskList = response.data.response_body;	
												$scope.totalPages = response.data.totalPages;
												$scope.totalElements = response.data.totalElements;
												$scope.allOnboardingListLength = response.data.totalElements;
												$scope.SetDataLoaded = false;
												$rootScope.setLoader = false;																																																																																			
											},function errorCallback(response) {
												$scope.SetDataLoaded = false;
												$rootScope.setLoader = false;
											});																			
						}
						if($scope.loginUserID)
							$scope.loadOnBoardingHistoryTaskList(0);						
												
						//get step result of the particular task id
			            $scope.viewTaskResults = function(taskId, solId, revId, modDate, statusCd){
			            	$scope.stepResults = null;
			            	$scope.divCreateSln = false;
							$scope.divAddArtifact = false;
							$scope.divCreateTOSCA = false;
							$scope.divDockerize = false;
							$scope.divAddDockerImage = false;
							$scope.resultCreateSln = false;
							$scope.resultAddArtifact = false;
							$scope.resultCreateTOSCA = false;
							$scope.resultDockerize = false;
							$scope.resultAddDockerImage = false;	
							$scope.statusCreateSln = 'SU';
							$scope.statusAddArtifact = 'SU';
							$scope.statusCreateTOSCA = 'SU';
							$scope.statusDockerize = 'SU';
							$scope.statusAddDockerImage = 'SU';
			            	apiService
							    .onBoardingHistoryStepResult(taskId)
									.then(
											function successCallback(response) {
												$scope.stepResults = response.data.response_body;
												$scope.modDate = modDate,
												$scope.solId = solId;
												$scope.revId = revId;
												$scope.statusCd = statusCd;
												for(var i=0 ; i< $scope.stepResults.length; i++){
													if($scope.stepResults[i].name == 'CreateSolution' && $scope.stepResults[i].statusCode == 'FA' )
													 {																									
															$scope.divCreateSln = true;
															$scope.resultCreateSln = true;
															$scope.errorMSGCS = $scope.stepResults[i].result;	
															$scope.statusCreateSln = 'FA';
															break;
													  }
													else if($scope.stepResults[i].name == 'CreateSolution' && $scope.stepResults[i].statusCode == 'SU'){
														$scope.resultCreateSln = true;
														
													}
																										
													if($scope.stepResults[i].name == 'AddArtifact' && $scope.stepResults[i].statusCode == 'FA' )
													 {														
															$scope.divAddArtifact = true;
															$scope.errorMSGAA = $scope.stepResults[i].result;
															console.log("err:", $scope.errorMSG);
															$scope.statusAddArtifact = 'FA';
															$scope.resultAddArtifact = true;
															break;
													  }
													else if($scope.stepResults[i].name == 'AddArtifact' && $scope.stepResults[i].statusCode == 'SU' ){
														$scope.resultAddArtifact = true;
														
													}
													if($scope.stepResults[i].name == 'CreateTOSCA' && $scope.stepResults[i].statusCode == 'FA' )
													{														
															$scope.divCreateTOSCA = true;
															$scope.errorMSGCT = $scope.stepResults[i].result;
															console.log("err:", $scope.errorMSG);
															$scope.resultAddArtifact = true;
															$scope.resultCreateTOSCA = true;
															$scope.statusCreateTOSCA = 'FA';
															break;
														}
													else if($scope.stepResults[i].name == 'CreateTOSCA' || $scope.stepResults[i].statusCode == 'SU'  ){
														$scope.resultCreateTOSCA = true;
														
													}
													if($scope.stepResults[i].name == 'Dockerize' && $scope.stepResults[i].statusCode == 'FA' )
													  {														
															$scope.divDockerize = true;
															$scope.errorMSGDZ = $scope.stepResults[i].result;
															console.log("err:", $scope.errorMSG);
															$scope.resultAddArtifact = true;
															$scope.resultCreateTOSCA = true;
															$scope.statusDockerize = 'FA';
															$scope.resultDockerize = true;
															break;
														}
													else if($scope.stepResults[i].name == 'Dockerize' && $scope.stepResults[i].statusCode == 'SU' ){
														$scope.resultDockerize = true;
														
													}
													if($scope.stepResults[i].name == 'AddDockerImage' && $scope.stepResults[i].statusCode == 'FA' )
														{														
															$scope.divAddDockerImage = true;
															$scope.errorMSGAD = $scope.stepResults[i].result;
															console.log("err:", $scope.errorMSG);
															$scope.resultAddArtifact = true;
															$scope.resultCreateTOSCA = true;
															$scope.resultDockerize = true;
															$scope.resultAddDockerImage = true;
															$scope.statusAddDockerImage = 'FA';
															break;
														}
													else if($scope.stepResults[i].name == 'AddDockerImage' && $scope.stepResults[i].statusCode == 'SU' ){
														$scope.resultAddDockerImage = true;
														
													}
												}
												
											},function errorCallback(response) {
												
											});				            	
			            }			            
						
					}
				});



