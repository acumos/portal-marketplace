/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
Modifications Copyright (C) 2019 Nordix Foundation.
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
		.module('modelDetails', [ 'jkAngularRatingStars' ])
		.component(
				'modelDetails',
				{

					templateUrl : './app/model-details/md-model-details.template.html',
					controller : function($scope, $location, $http, $rootScope,
							$stateParams, $sessionStorage, $localStorage,
							$mdDialog, $state, $window, apiService, $anchorScroll, $timeout, $document, $sce, browserStorageService, modelUploadService) {
						
						$scope.Math = window.Math;
						$rootScope.isONAPCompatible = false;
	
						$scope.showMicroService = false;
						$scope.devEnv = '1';
						$location.hash('md-model-detail-template');  
						$anchorScroll(); 
						$scope.licenseOption = 'Upload';
						$scope.revisionId = $stateParams.revisionId;
						$scope.loginUserID = "";
						if (browserStorageService.getUserDetail()) {
							$scope.loginUserID = JSON.parse(browserStorageService.getUserDetail())[1];
						}
						
						if($stateParams.publishRequestId != null){
							apiService.isPublishOwnRequestsEnabled().then(function(response) {
								
								$scope.publishOwnRequestsEnabled = response.data.response_body;
								$scope.audit = true;
								$scope.requestedByMe = false;
								$scope.publishRequestId = $stateParams.publishRequestId;
								$scope.requestStatusCode = $stateParams.requestStatusCode;
								if ($scope.loginUserID == $stateParams.requestUserId && $scope.publishOwnRequestsEnabled ==='false'){
									$scope.requestedByMe = true;
								}
							});
						}
						if($scope.audit)
						$scope.clearForm = function(){
							deploy.reset();
							deployCloud.brokerlink.value = "";
							$scope.vmName = "";
							$scope.deploy.$setUntouched();
							$scope.deployCloud.$setUntouched();
							$scope.rackspace.$setUntouched();
						}
						
						var user= JSON.parse(browserStorageService.getUserDetail());
						$scope.userDetailsLogged = user;
						
						if (browserStorageService.getUserDetail()) {
							$scope.auth = browserStorageService
									.getAuthToken();
						}
						
						$scope.showAlertMessage = false;
						
						$scope.getAllRatings = function(){
							var dataObjRating = {
							  "request_body": {
								    "page": 0,
								    "size": 0
									}
							};

							var req = {
								    method: 'POST',
								    url: 'api/solution/getRating/'+ $stateParams.solutionId,
								    data : dataObjRating
								};
							$http(req)
							.success(function(data, status, headers,config) {
								if(data.response_body){
									$scope.allUserRatings = data.response_body.content;
								}
								$scope.ratingCount1 = 0;
								$scope.ratingCount2 = 0;
								$scope.ratingCount3 = 0;
								$scope.ratingCount4 = 0;
								$scope.ratingCount5 = 0;
								$scope.perratingCount1 = 0;
								$scope.perratingCount2 = 0;
								$scope.perratingCount3 = 0;
								$scope.perratingCount4 = 0;
								$scope.perratingCount5 = 0;

								
								angular.forEach($scope.allUserRatings, function(value, key) {
                    				if(value.rating == 1){
                    					$scope.ratingCount1 = $scope.ratingCount1+1
                    				}else if(value.rating == 2){
                    					$scope.ratingCount2 = $scope.ratingCount2+1
                    				} else if(value.rating == 3){
                    					$scope.ratingCount3 = $scope.ratingCount3+1
                    				}else if(value.rating == 4){
                    					$scope.ratingCount4 = $scope.ratingCount4+1
                    				}else if(value.rating == 5){
                    					$scope.ratingCount5 = $scope.ratingCount5+1
                    				}
                    			});
								
								$scope.totalRatingsCount = $scope.ratingCount1 + $scope.ratingCount2 + $scope.ratingCount3 + $scope.ratingCount4 + $scope.ratingCount5;
								
								$scope.perratingCount1 = $scope.ratingCount1/$scope.totalRatingsCount * 100;
								$scope.perratingCount2 = $scope.ratingCount2/$scope.totalRatingsCount * 100;
								$scope.perratingCount3 = $scope.ratingCount3/$scope.totalRatingsCount * 100;
								$scope.perratingCount4 = $scope.ratingCount4/$scope.totalRatingsCount * 100;
								$scope.perratingCount5 = $scope.ratingCount5/$scope.totalRatingsCount * 100;
								
							}).error(function(data, status, headers, config) {
								
							});
						}
													
						$scope.getAllRatings();
						
						$scope.getAverageRatings = function(){
							$stateParams.solutionId

									var req = {

										    method: 'GET',
										    url: 'api/solution/avgRating/'+ $stateParams.solutionId,
										};
									$http(req)
									.success(function(data, status, headers,config) {
										 
										$scope.averageRatings = data.response_body;
										if($scope.averageRatings !== null)
											{
												var starPercentage = ($scope.averageRatings.ratingAverageTenths / 5) * 100;
												var starPercentageRounded = ($window.Math.round(starPercentage / 10) * 10);
												if((angular.element(document.querySelector(".stars-inner")).length)>0){
													angular.element(document.querySelector(".stars-inner"))[0].style.width= starPercentageRounded + "%";
												}										
											}
									}).error(function(data, status, headers, config) {
										console.warn("Error: ",data);
									});
						}
						$scope.getAverageRatings();
						
						$scope.onItemRating = function(rating){
							
							var dataObjRating = {
								"request_body" : {
									"solutionId" : $stateParams.solutionId,
									"rating" : rating,
									"userId" : user[1],
									"textReview": $scope.ratingReview
								},
								  "request_from" : "string",
								  "request_id" : "string"
							}
							
							 
							if(!$scope.mlSolutionGetRating.content || $scope.mlSolutionGetRating.content[0].rating == 0)
								{
								//create a new rating for the model. User rates the solution first time.
									apiService
									.createRatingSolution(dataObjRating)
									.then(
											function(response) {
												console.log("Rating response : " + angular.toJson(response));
												/************* if else commented since ratings doens't responsd with correct status at offshore **********/
												if(response.status == 200 || response.status == 201){
													$scope.getSolutionratings();
													$location.hash('md-model-detail-template');  // id of a container on the top of the page - where to scroll (top)
													$anchorScroll(); 							// used to scroll to the id 
													$scope.msg = "You've rated this model successfully. "; 
													$scope.icon = '';
													$scope.styleclass = 'c-success';
													$scope.showAlertMessage = true;
													$timeout(function() {
														$scope.showAlertMessage = false;
													}, 2500);
												}else{
													$scope.getSolutionratings();
													$location.hash('md-model-detail-template');
													$anchorScroll();
													$scope.msg = "Unexpected Error occurred.";
													$scope.icon = 'report_problem';
													$scope.styleclass = 'c-error';
													$scope.showAlertMessage = true;
													$timeout(function() {
														$scope.showAlertMessage = false;
													}, 2500);
												}
												$mdDialog.hide();
												$scope.getAllRatings();
												$scope.getAverageRatings();
											},
											function(error) {
												$mdDialog.hide();
												console.log(error);
											});
								
								}
							else{
								// update rating if user has already rated the solution								
								apiService.updateRatingSolution(dataObjRating)
								.then(
										function(response) {
											console.log("Rating response : " + angular.toJson(response));
											/************* if else commented since ratings doens't responsd with correct status at offshore **********/
											if(response.status == 200 || response.status == 201){
												$scope.getSolutionratings();
												$location.hash('md-model-detail-template');
												$anchorScroll();
												$scope.msg = "Rating Updated successfully.";
												$scope.icon = 'report_problem';
												$scope.styleclass = 'c-success';
												$scope.showAlertMessage = true;
												$timeout(function() {
													$scope.showAlertMessage = false;
												}, 2500);
											}else{
												$scope.getSolutionratings();
												$location.hash('md-model-detail-template');
												$anchorScroll();
												$scope.msg = "Unexpected Error occurred.";
												$scope.icon = 'report_problem';
												$scope.styleclass = 'c-error';
												$scope.showAlertMessage = true;
												$timeout(function() {
													$scope.showAlertMessage = false;
												}, 2500);
												
											}
											$mdDialog.hide();
											$scope.getSolutionratings();
											$scope.getAverageRatings();
											
										},
										function(error) {
											console.log(error);
											$mdDialog.hide();
										});
							}
			               }
						
						$scope.getSolutionratings=function(){
							
							if(user){
								var url = '/api/solutions/ratings/'+$stateParams.solutionId+'/user/'+user[1];
							var dataObj = {
									"request_body": {
										    "fieldToDirectionMap": {},
										    "page": 0,
										    "size": 20
										  },
										  "request_from": "string",
										  "request_id": "string"
										}
								
							$http({
								method : 'POST',
								url : url,
								data : dataObj
							}).success(function(data, status, headers,config) {
									if(data.response_body){
										$scope.mlSolutionGetRating = data.response_body;
										$scope.ratingReview = $scope.mlSolutionGetRating.textReview;
									}
								 
								}).error(
									function(data, status, headers,config) {
										console.log("Error: "+status);
									});
							}
						}; 
						$scope.getSolutionratings();
						
					  //API for rating the model End
						
						//get User image.
						var pathArray = location.href.split( '/' );
						var protocol = pathArray[0];
						var host = pathArray[2];
						var baseURL = protocol + '//' + host;
						$scope.showAltImage = true;
						$scope.getUserImage = function (userId){
							if(userId != ""){
							var req = {
								    method: 'Get',
								    url: '/api/users/userProfileImage/' + userId
								};
							$http(req)
							.success(function(data, status, headers,config) {
								if(data.status){
								    $scope.userImage = data.response_body;
								    $scope.showAltImage = false;
								}
							}).error(function(data, status, headers, config) {
								data.response_body
							});
							}
						}
					
						$scope.goToRelatedSolutions = function(solutionId, revisionId){
							$state.go('marketSolutions', {solutionId : solutionId, revisionId : revisionId, parentUrl: 'marketSolutions'});
						}
						
						if($stateParams.parentUrl){
							$rootScope.routeParentUrl = $stateParams.parentUrl;
							$window.sessionStorage.setItem("SavedString",$rootScope.routeParentUrl);
						}else{
							/*$rootScope.routeParentUrl;*/
						}
						$rootScope.routeParentUrl = $window.sessionStorage.getItem("SavedString");
						$rootScope.routeParentUrl;
						
						 
						if ($rootScope.routeParentUrl == 'mymodel') {
							$rootScope.parentActive = 'mymodel'
						} else if($rootScope.routeParentUrl == 'marketplace') {
							$rootScope.parentActive = 'marketplace'
						}

						componentHandler.upgradeAllRegistered();
						
						$rootScope.urlPath = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);
						
						if($rootScope.urlPath == 'manageModule'){
							$scope.parentUrl = false
						}else{
							$scope.parentUrl = true
						}

						if ($stateParams.solutionId == ''
								|| $stateParams.solutionId == null) {
						} else {
							localStorage.setItem("solutionId",
									$stateParams.solutionId);
						}

						if (localStorage.getItem("solutionId")) {
							console.log("local storage solutionId",
									localStorage.solutionId);
							$scope.solutionId = localStorage
									.getItem("solutionId");

						}
						
					$scope.getModelAuthors = function(){
						$http({
							method : 'GET',
							url : 'api/solution/'+ $stateParams.solutionId +'/revision/'+ $stateParams.revisionId +'/authors',
						})
								.success(
										function(data, status, headers, config) {
											console.log(data.response_body);
											$scope.authorList = data.response_body;
											
										})
								.error(function(data, status, headers, config) {
										// called asynchronously if an error occurs
										// or server returns response with an error
										// status.
										console.log(status);
										});
					}
					
					$scope.getModelAuthors();
					
					var modelType = '';
					$scope.apiUrl;

						$scope.apiUrl = '/api/solutions/'
								+ $stateParams.solutionId + '/' + $stateParams.revisionId;
				
					
					$scope.getModelDetails = function() {
						
						$http({
							method : 'GET',
							url : $scope.apiUrl,
						})
								.success(
										function(data, status, headers, config) {
											 
											if( !user || data.response_body.ownerId == user[1] ){
												$scope.cantRate = true;
											}
											else {
												$scope.cantRate = false;
											}
											$scope.tags = data.response_body.solutionTagList;
											$scope.modelOwnerId = data.response_body.ownerId;
											modelType = data.response_body.modelType;
											$scope.solution = data.response_body;
											$scope.firstRate = data.response_body.solutionRating;
											$scope.ratingCount = data.response_body.ratingCount;
											console.log($scope.solution);
											$scope.getUserImage($scope.modelOwnerId);
											relatedSoltion();
											$scope.disableEdit();
											$scope.enableDownload();
											if (data.response_body.revisions) {
												var length = data.response_body.revisions.length;
												
												var counter = 0;
												//**adding list of versions
												$scope.versionList = [];
												$scope.publisherList = [];
												while(counter < length){
													($scope.versionList).push(data.response_body.revisions[counter]);
													if(data.response_body.revisions[counter].publisher !== null)
														($scope.publisherList).push(data.response_body.revisions[counter].publisher);
													counter++;
												}
												
												($scope.versionList).sort(function(a, b) {
													  // sort version according to created date
													  const genreA = a.onboarded;
													  const genreB = b.onboarded;

													  let comparison = 0;
													  if (genreA < genreB) {
													    comparison = 1;
													  } else if (genreA > genreB) {
													    comparison = -1;
													  }
													  return comparison; }
												);
												if($scope.revisionId){
													$scope.version = $scope.versionList.filter(function (versions) { return versions.revisionId == $scope.revisionId;})[0];
													$scope.revisionId = $scope.version.revisionId;
													$scope.versionId = $scope.version.version;
												} else {
													$scope.versionId = $scope.versionList[0].version;
													$scope.revisionId = $scope.versionList[0].revisionId;
													$scope.version = $scope.versionList[0];
												}								
												$scope.getComment();
												$scope.getArtifacts();
												
											}

											if (JSON.parse(browserStorageService
													.getUserDetail())) {
												$scope.userDetails = JSON
														.parse(browserStorageService
																.getUserDetail());
												$scope.userName = $scope.userDetails[0];
												$scope.loginUserID = $scope.userDetails[1];
											}

											if (JSON.parse(browserStorageService
													.getUserDetail())
													&& $scope.solution.ownerId == $scope.loginUserID) {
												$scope.isUser = true
											} else {
												$scope.isUser = false
											}
											$stateParams.solutionId = $scope.solution.solutionId
											if (data.response_body.revisions != null) {
												if($scope.revisionId){
													$scope.version = $scope.versionList.filter(function (versions) { return versions.revisionId == $scope.revisionId;})[0];
													$scope.revisionId = $scope.version.revisionId;
													$stateParams.revisionId = $scope.revisionId;
												} else {
													$scope.revisionId = $scope.versionList[0].revisionId;
													$scope.version = $scope.versionList[0];
													$stateParams.revisionId = $scope.revisionId;
												}
											}
											
											$scope.checkOnapCompatibility();
											if($scope.loginUserID !== null && $scope.loginUserID !== "" && $scope.loginUserID !== undefined)
											   $scope.getLicenseFile();
											
											var solutionName = $scope.solution.name;
										})
										
								.error(function(data, status, headers, config) {
									// called asynchronously if an error occurs
									// or server returns response with an error
									// status.
									console.log(status);
								});
						}
					 $scope.getModelDetails();
						// };
					 
					 $scope.checkOnapCompatibility = function (){
						 if($rootScope.enableDCAE && $scope.loginUserID) {
								var check_onap_url = 'api/webBasedOnBoarding/checkOnapCompatible/' + $scope.solution.solutionId + '/'+$scope.revisionId;
								$http(
										{
											method : 'GET',
											url : check_onap_url
										})
										.then(
												function successCallback(response) {
													$rootScope.isONAPCompatible = (response.data.response_detail === "true");
												},function errorCallback(response) {
													//Do nothing
											});
							}
					 }
						
					$scope.getProtoFile = function(){
						 $scope.modelSignature = "";
						 var url = 'api/getProtoFile?solutionId='+$scope.solution.solutionId+'&version='+$scope.versionId;
							$http(
									{
										method : 'GET',
										url : url
									})
									.then(
											function successCallback(response) {
												console.log(response);
												$scope.modelSignature = response.data;
												if(!$scope.modelSignature){
													$scope.signatureNotFound = true;
												}
											});
					}
					
					$scope.isLicenseFound = false;
                                        $scope.isLoadingLicense = true;
					$scope.getLicenseFile = function() {
						$scope.modelLicense = "";
						// avoid license json checks during digest cycle
						$scope.isLicenseJson = false;
						$scope.isLicenseFound = false;
						var url = 'api/getLicenseFile?solutionId='+$scope.solution.solutionId+'&version='+$scope.versionId;
						$http({
								method : 'GET',
								url : url
						}).then(function successCallback(response) {
							if (response.data) {
								$scope.isLoadingLicense = false;
								$scope.isLicenseFound = true;
								$scope.modelLicense = response.data;
								// avoid license json checks during digest cycle
								$scope.isLicenseJson = angular.isObject($scope.modelLicense);
							} else {
								$scope.modelLicenseError = "No license found";
								$scope.isLoadingLicense = false;
							}		
						}, function errorCallback(err){
							console.error(err);
							$scope.isLoadingLicense = false;
							$scope.modelLicenseError = "No license found";
						});
				 	}
					
					$scope.getCatalogsList = function(){
	                    apiService
				        .getCatalogsForSolutions($scope.solutionId)
				        .then(function(response) {
				                    if(response.status == 200){
				                    	  $scope.solutionCatalogsList = response.data.response_body;
				                    	  if($scope.solutionCatalogsList && $scope.solutionCatalogsList.length > 0){
					                    	  $scope.selectedCatalogId = $scope.solutionCatalogsList[0].catalogId;
						  					  $scope.catalogName = $scope.solutionCatalogsList[0].name;
						  					  $scope.getSolutionDocuments();
						  					  $scope.getSolutionDescription();
				                    	  }
				                    }
				         });
					 }
					
					$scope.changeSelectedCatalogId = function(catalogId, catalogName){
						$scope.selectedCatalogId = catalogId;
						$scope.catalogName = catalogName;
						$scope.documents = [];
						$scope.solutionDescription = "";
						$scope.getSolutionDocuments();
						$scope.getSolutionDescription();
						
					}
						 
					
					
					$scope.totalCommentCount = 0;
					$scope.postComment = function() {
							if (browserStorageService.getUserDetail()) {
								$scope.loginUserID = JSON.parse(browserStorageService
										.getUserDetail())[1];
								$scope.userFullName = $scope.userDetails[0];
							}
							
							var threadObj = {
									  "request_body": {
										    "revisionId": $scope.revisionId,
										    "solutionId": $scope.solutionId
										    
										  }
										 
										};
								apiService.createThread(threadObj).then(function(response) {
									console.log(response);
									var commentObj = {
											  "request_body": {
												    "text": $scope.comment,
												    "threadId": response.data.response_body.threadId,
												    "url": $scope.solutionId,
												    "userId": $scope.loginUserID
												  },
												};
									apiService.createComment(commentObj).then(function(response) {
										$scope.getComment();
										$scope.comment = '';
									});
								});
						}
						
						$scope.getSolutionDocuments = function(){
	                       	 var getSolutionDocumentsReq = {
										method : 'GET',
										url : '/api/solution/'+$scope.solutionId + "/revision/" + $scope.revisionId + '/' + $scope.selectedCatalogId  + "/document"
								};
	                       	 $http(getSolutionDocumentsReq)
									.success(
											function(data) {
												$scope.supportingDocs = data.response_body;
												$scope.documents = [];
												var fileName="";var fileExtension = '';
												angular.forEach($scope.supportingDocs, function(value, key) {
													fileName = value.name;
													fileExtension = fileName.split('.').pop();
													$scope.documents.push({"name":value.name,"ext":fileExtension,"documentId":value.documentId});
												});
											});
							}
						
						$scope.newcomment = {};
						$scope.postReply = function(key, comment){
							if (browserStorageService.getUserDetail()) {
								$scope.loginUserID = JSON.parse(browserStorageService
										.getUserDetail())[1];
								$scope.userFullName = $scope.userDetails[0];
							}
							
								var commentObj = {
										  "request_body": {
											    "text": $scope.newcomment[key].text,
											    "threadId": comment.threadId,
											    "parentId": comment.commentId,
											    "url": $scope.solutionId,
											    "userId": $scope.loginUserID
											  },
											};
								apiService.createComment(commentObj).then(function(response) {
									$scope.getComment();
									$scope.comment = '';
								});
						
							$scope.newcomment = {};
						}
						
						$scope.editComment = false;
						$scope.editReply = false;
						$scope.commentNewest = false;
						$scope.getComment = function() {
							if (browserStorageService.getUserDetail()) {
								$scope.loginUserID = JSON.parse(browserStorageService
										.getUserDetail())[1];
							}
							var reqObj = {
									  "request_body": {
										    "page": 0,
										    "size": 100
										  },
										};
							
							 apiService.getComment($scope.solutionId, $scope.revisionId, reqObj).then(function(response) {
								 
								$scope.totalCommentCount = response.data.response_body.content.length;
								$scope.commentList = [];
								//list of indexes to go through after initial read, only for reply comments(i.e. they have a parentId not null)
								$scope.replyList = [];
								angular.forEach(response.data.response_body.content,function(value,key) {
									if(response.data.response_body.content[key].parentId == null){
										var commentIndex = key-$scope.replyList.length; //takes into account offset
										
										$scope.commentList.push({
											created : response.data.response_body.content[key].created,
											text : response.data.response_body.content[key].text,
											commentId : response.data.response_body.content[key].commentId,
											threadId : response.data.response_body.content[key].threadId,
											userId : response.data.response_body.content[key].userId,
											stringDate : response.data.response_body.content[key].stringDate,
											replies: []
										});
						
										var userObject = {
												  "request_body": {
													  		    "userId": value.userId
												  					}};
										apiService.getUserAccountDetails(userObject).then(function(userDetail){
											console.log("User,", userDetail);
												$scope.commentList[commentIndex].name = userDetail.data.response_body.loginName;
												$scope.commentList[commentIndex].firstName = userDetail.data.response_body.firstName;
												$scope.commentList[commentIndex].lastName = userDetail.data.response_body.lastName;
									    });
										apiService.getUserProfileImage(value.userId).then(function(userImage){
												$scope.commentList[commentIndex].image = userImage.data.response_body;
										});
									}
									else {//it is a reply to a comment(has a parent-id)
										$scope.replyList.push(key);
									}
								});
								//go through each comment that is a reply to a base comment(i.e. comments with parentId != null)
								//this comes after initial population so all comments with no parent id(base comments) will be in commentList
								//, just need to push to replies list
								angular.forEach($scope.replyList, function(value,key) {
									var commentReply = response.data.response_body.content[value];
									
									var userObject = {
									  "request_body": {
										  		    "userId": commentReply.userId
									  					}};
									apiService.getUserAccountDetails(userObject).then(function(userDetail){
										commentReply.name = userDetail.data.response_body.loginName;
										commentReply.firstName = userDetail.data.response_body.firstName;
										commentReply.lastName = userDetail.data.response_body.lastName;
								    });
									apiService.getUserProfileImage(commentReply.userId).then(function(userImage){
										commentReply.image = userImage.data.response_body;
									});
									
									//loops through all current comments to find the proper parent comment to add onto its replies
									for(var commentIndex = 0; commentIndex < $scope.commentList.length; commentIndex++) {
										if($scope.commentList[commentIndex].commentId == commentReply.parentId) {
											$scope.commentList[commentIndex].replies.push(commentReply);
											//found the right comment, so it should break from loop
											break;
										}
									}
								});
							});
							
						};
						
						$scope.scrollToComment = function(view){
							$scope.view = view;
							
							if($scope.view == 'edit'){
								$location.hash('editComment');
								$anchorScroll();
								angular.element('#editComment').focus();
							}
							else{
								$location.hash('discussionTab');
								$anchorScroll();
							}
							
						}
						
						$scope.editComment = function(comment) {
							var commentObj = {
									  "request_body": {
										    "text": comment.text,
										    "commentId": comment.commentId,
										    "threadId": comment.threadId,
										    "parentId": comment.parentId,
										    "url": $scope.solutionId,
										    "userId": $scope.loginUserID
										  },
										};
							apiService.updateComment(commentObj).then(function(response) {
								$scope.getComment();
							});
						};
						
						$scope.deleteComment = function(comment) {
							apiService.deleteComment(comment.threadId,comment.commentId).then(function(response) {
								$scope.getComment();
							});
						}

						var session = sessionStorage.getItem("SessionName")
						if (session) {
							console.log(session);
						}
						var sol_id = $location.path().split('/')[2]

						// Initialize the default start rating
						$('#input-2').rating({
							step : 1,
							size : 'xxs',
							starCaptionClasses : {
								1 : 'text-danger',
								2 : 'text-warning',
								3 : 'text-info',
								4 : 'text-primary',
								5 : 'text-success'
							}
						});

						$scope.publishalert = '';

						// show solution download popup based on User logedin
						// session
						$scope.solutionDownloadWindow = function() {
							var session = sessionStorage.getItem("SessionName")
							if (session === "undefined") {

								console.log("Not signin");
								$('#myModal').modal('show');
							} else {
								console.log("Singedin");
								$('#mydownloadModal').modal('show');

							}

						}

						// Publish the specific solution to Market place by
						// specific version

						$scope.solutionsDownload = function(version) {

							console.log(version);
							$http({
								method : 'GET',
								url : '/api/download/' + version,
							}).success(function(data, status, headers, config) {
								console.log(data);
							}).error(function(data, status, headers, config) {
								// called asynchronously if an error
								// occurs
								// or server returns response with an
								// error status.
								console.log(status);
							});

						}
						
						$scope.performSVScan = function(solutionId, revisionId, workflowId, successFunc) {
							apiService.performSVScan(solutionId, revisionId, workflowId)
								.then(function(response) {
									var workflow = response.data.response_body;
									if (!workflow.workflowAllowed) {
										$mdDialog.show({
											templateUrl : '../app/error-page/sv-modal.template.html',
											clickOutsideToClose : true,
											locals: {
												reasons: workflow.reason,
												summary: workflow.summary,
												isError: response.data.error_code == "sv_error",
												isUsageDenied: response.data.error_code == "rtu_error"
											},
											controller : function DialogController($scope, reasons, summary, isError, isUsageDenied) {
												$scope.reasons = reasons;
												$scope.isError = isError;
												$scope.isUsageDenied = isUsageDenied;
												$scope.summary = summary;
												$scope.closePoup = function(){
													$mdDialog.hide();
												}
											}
										});
									} else {
										successFunc();
									}
		                        },
		                    	function(error) {
									$location.hash('manage-models');
									$anchorScroll();
									$scope.msg = "An exception occurred during SV scan";
									$scope.icon = '';
									$scope.styleclass = 'c-error';
									$scope.showAlertMessage = true;
									$timeout(function() {
											$scope.showAlertMessage = false;
										}, 5000);
									$mdDialog.hide();
									console.error("SV Exception occured,", error);
		                        });
						};
						
						$scope.downloadArtifact = function(artifactId) {
							$scope.performSVScan($scope.solution.solutionId, $scope.revisionId, "download", function() {
								$window.location.assign("/api/downloads/" + $scope.solution.solutionId
									+ "?artifactId=" + artifactId
									+ "&revisionId=" + $scope.revisionId
									+ "&userId=" + $scope.loginUserID
									+ "&jwtToken=" + $scope.auth);
								$scope.getModelDetails();
							});
						}

						$scope.documentDownload = function(documentId) {
							$window.location.assign("/api/solution/revision/document/" + documentId + "?jwtToken=" + $scope.auth);
						}
						
						// Publish the solution to Market place if both solution
						// author and loged in user same
						$scope.publishtoMarket = function(pub_value) {

							var userId = sessionStorage.getItem("SessionName")

							if (userId === $scope.solution.ownerId) {

								if ($scope.version.accessTypeCode == $scope.priVar) {

									var data = $.param({
										visibility : pub_value
									});

									apiService.putPublishSolution
											.then(
													function(response) {
														console.log(data);
														$scope.publishalert = {
															type : 'success',
															msg : 'Well done! You successfully publish solution to Market Place.'
														};
														setTimeout(
																function() {
																	$scope
																			.$apply(function() {
																				$scope.publishalert = '';
																			});
																}, 3000);
													}, function(error) {
														// called asynchronously
														// if an error occurs
														// or server returns
														// response with an
														// error status.
														console.log(error);
													});
								} else {

									$scope.publishalert = {
										type : 'danger',
										msg : 'This solution already published in the Market place'
									};
									setTimeout(function() {
										$scope.$apply(function() {
											$scope.publishalert = '';
										});
									}, 3000);

								}
							} else {
								alert("Please sign in as owner  to publish solution");
							}

						}

						// /
						$scope.isReadonly = false; // default test value
						$scope.changeOnHover = false; // default test value
						$scope.maxValue = 5; // default test value
						$scope.ratingValue = 2; // default test value
						
						angular.element('.md-version-ddl1').hide();
						$document.on('click', function(){
							if(angular.element('.md-version-ddl1')){
								angular.element('.md-version-ddl1').hide();
							}
						});
						
						$scope.showVersion = function() {
							if(angular.element('.version-list').css('display') == 'none'){
								angular.element('.version-list').show();
							} else {
								angular.element('.version-list').hide();
							}	
						}
						
						$scope.showCatalog = function() {
							if(angular.element('.catalog-list').css('display') == 'none'){
								angular.element('.catalog-list').show();
							} else {
								angular.element('.catalog-list').hide();
							}	
						}
						$scope.loadVersionDetails = function(solutionId, revisionId, versionId){
							$scope.version = $scope.versionList.filter(function (versions) { return versions.revisionId == revisionId;})[0];
							$scope.solution.solutionId = solutionId; 
							$scope.revisionId = $scope.version.revisionId;
							$stateParams.revisionId = $scope.version.revisionId;
							$scope.versionId = versionId;
							$scope.solutionDescription = "";
							angular.element('.version-list').hide();
							$scope.getArtifacts();
							$scope.getSolutionDescription();
							$scope.checkOnapCompatibility();
							$scope.getLicenseFile();
						}
										
						
						/***************** get solution descriptions ***********************/
						
						$scope.getSolutionDescription = function() {
							
							var req = {
									method : 'GET',
									url : '/api/solution/revision/' + $scope.revisionId  + '/' + $scope.selectedCatalogId  + "/description"
								};
								$http(req)
										.success(
												function(data) {
													$scope.solutionDescription = (data.response_body.description);
													$scope.solutionDescriptionRating = $scope.solutionDescription.replace(/<[^>]+>/gm, '');
													$scope.ratingCheck($scope.solutionDescriptionRating);
													
												})
										.error(
												function(data) {
													$scope.solutionDescription = "";
												});
						}
						
						$scope.solutionCatalogsList = [];
						if($stateParams.catalogId == null && $stateParams.catalogName == null){
							$scope.getCatalogsList();
						}else{
							$scope.solutionCatalogsList = [ {"name" : $stateParams.catalogName, 'catalogId' : $stateParams.catalogId } ];
							$scope.selectedCatalogId = $scope.solutionCatalogsList[0].catalogId;
		  					$scope.catalogName = $scope.solutionCatalogsList[0].name;
		  					$scope.getSolutionDocuments();
		  					$scope.getSolutionDescription();
		  				}
						
						

						
							/**********************************END*****************************/
						
						
						
						         var ratingDescription;
							    $scope.ratingCheck = function(ratingDescription){
								
								$scope.ratedescriptioncheck = ratingDescription.length;
																		
							}
						
						
						
						
						
						$scope.download = function(artifactId) {
							
							$scope.loginUserID = "";
							if (browserStorageService.getUserDetail()) {
								$scope.loginUserID = JSON.parse(browserStorageService
										.getUserDetail())[1];
							}

							var url = 'api/downloads/'+$scope.solution.solutionId+'?artifactId='+artifactId+'&revisionId='+$scope.revisionId+'&userId='+$scope.loginUserID;
							
							 $http({ method : 'GET', url : url, responseType : "arraybuffer", })
							 .success(function(data, status, headers, config) { 
								 headers = headers(); 
								 var anchor = angular.element('<a/>'); //FOR IE 
								 if(navigator.appVersion.toString().indexOf('.NET') > 0) 
									 window.navigator.msSaveBlob(new Blob([data], { type: headers['content-type'] }), headers['x-filename']); 
								 else { //FOR Chrome and Forefox 
									 anchor.css({display: 'none'}); // Make sure it's not visible
									  angular.element(document.body).append(anchor); 
									  // Attach to document 
									  anchor.attr({ href: window.URL.createObjectURL(new Blob([data], {type: headers['content-type'] })), target: '_blank', download: headers['x-filename']
									  })[0].click(); 
									  anchor.remove(); 
								 }
							  $rootScope.showPrerenderedDialog("", '#downloadConfirm'); 
							  $mdDialog.hide();
							  })
							  .error(function(data, status, headers, config) {
								  $rootScope.showPrerenderedDialog("", '#downloadConfirm');
								  console.log(status); $mdDialog.hide(); });
							 
						}
						
						$scope.versionDownload = function(artifactId) {
							
							$scope.loginUserID = "";
							if (browserStorageService.getUserDetail()) {
								$scope.loginUserID = JSON.parse(browserStorageService
										.getUserDetail())[1];
							}

							var url = 'api/downloads/'+$scope.solution.solutionId+'?artifactId='+artifactId+'&revisionId='+$scope.revisionId+'&userId='+$scope.loginUserID;
							
							 $http({ method : 'GET', url : url, responseType : "arraybuffer", })
							 .success(function(data, status, headers, config) { 
								 headers = headers(); 
								 var anchor = angular.element('<a/>'); // FOR
																		// IE
								 if(navigator.appVersion.toString().indexOf('.NET') > 0) 
									 window.navigator.msSaveBlob(new Blob([data], { type: headers['content-type'] }), headers['x-filename']); 
								 else { // FOR Chrome and Forefox
									 anchor.css({display: 'none'}); // Make sure
																	// it's not
																	// visible
									  angular.element(document.body).append(anchor); 
									  // Attach to document
									  anchor.attr({ href: window.URL.createObjectURL(new Blob([data], {type: headers['content-type'] })), target: '_blank', download: headers['x-filename']
									  })[0].click(); 
									  anchor.remove(); 
								 }
							  $rootScope.showPrerenderedDialog("", '#downloadConfirm'); 
							  //$mdDialog.hide();
							  })
							  .error(function(data, status, headers, config) {
								  $rootScope.showPrerenderedDialog("", '#downloadConfirm');
								  console.log(status);
								 // $mdDialog.hide(); 
								  });
							 
						}
						/** ****** Export/Deploy to Azure starts *** */
						
						$scope.getArtifacts = function() {
							$scope.showMicroService = false;
                                                	$scope.signatureNotFound = false;
							var isDockerArtifactFound = false;
							$scope.isOnnxOrPFAModelFound = false;
							$scope.dockerUrlOfModel = '';
							
							$http(
									{
										method : 'GET',
										url : '/api/solutions/'
												+ $scope.solutionId
												+ '/revisions/'
												+ $scope.revisionId
									})
									.then(
											function successCallback(response) {
												$scope.artifactDownload = response.data.response_body;
												for (var x = 0; x < response.data.response_body.length; x++) {
													if(response.data.response_body[x].artifactType == "DI"){
														$scope.artifactType = response.data.response_body[x].artifactType;
														$scope.artifactUri = response.data.response_body[x].artifactUri;
														// in case of license artifacts count will be 3
														if($scope.artifactDownload.length == 2 || $scope.artifactDownload.length == 3){
															$scope.dockerUrlOfModel = $scope.artifactUri;
															$scope.signatureNotFound = false;
														}
														isDockerArtifactFound = true;
													}
													
													if(response.data.response_body[x].artifactType == "MI" || response.data.response_body[x].artifactType == "CD" || response.data.response_body[x].artifactType == "BP" ){
														$scope.artifactType = response.data.response_body[x].artifactType;
														$scope.artifactUri = response.data.response_body[x].artifactUri;
														// in case of license artifacts count will be 3
														if($scope.artifactDownload.length >=3){
															$scope.dockerUrlOfModel = "";
															$scope.signatureNotFound = false;
														}
														else{
															
															$scope.dockerUrlOfModel = $scope.artifactUri;
														}
														
													}
													
													if( response.data.response_body[x].name.endsWith('.pfa') || response.data.response_body[x].name.endsWith('.onnx') ){
														$scope.isOnnxOrPFAModelFound = true;
													}
												}
												if( $scope.isOnnxOrPFAModelFound != true  &&  $scope.dockerUrlOfModel  == ''){
														$scope.getProtoFile();
														
														if( isDockerArtifactFound == false ){
															if($scope.loginUserID !== null && $scope.loginUserID !== "" && $scope.loginUserID !== undefined){
																apiService
																	.getMSStatus( $scope.solution.solutionId, $scope.revisionId, $scope.loginUserID ) .then(
																			function(response) {
																				var microserviceStatus = response.data.response_body;
																				if( !microserviceStatus || ( microserviceStatus && microserviceStatus.statusCode == "FA" )){
																						$scope.showMicroService = true;	
																				}
																});
															} else{
																$scope.showMicroService = true;
															}
														}
												}
											},
											function errorCallback(response) {
											$scope.getProtoFile();
										});
												
						}
						
						$scope.authenticateAnddeployToAzure = function() {
							$scope.performSVScan($scope.solution.solutionId, $scope.revisionId, "deploy", function() {
								var imageTagUri = '';
								if ($scope.artifactType != null
										&& $scope.artifactType == 'DI') {
									imageTagUri = $scope.artifactUri;
								}
								if($scope.solution.tookitType != "CP") {
	                                var reqObject = '';
	                                if($scope.exportTo == 'azure'){
	                                	
	                                      var url = '/azure/singleImageAzureDeployment';
	                                      reqObject = {
	                                    	
	                                                        'acrName': $scope.acrName,
	                                                        'client': $scope.applicationId,
	                                                        'key': $scope.secretKey,
	                                                        'rgName': $scope.resourceGroup,
	                                                        'solutionId': $scope.solution.solutionId,
	                                                        'solutionRevisionId': $scope.revisionId,
	                                                        'storageAccount': $scope.storageAccount,
	                                                        'subscriptionKey':  $scope.subscriptionKey,
	                                                        'tenant': $scope.tenantId,
	                                                        'imagetag': imageTagUri,
	                                                        'userId':  $scope.loginUserID
	                                                        
	                                                        
	                                      }
	                                }
	                                else if($scope.exportTo == 'rackspace'){
	                                      var url =  '/openstack/singleImageOpenstackDeployment';
	                                      reqObject ={
	                          'vmName': $scope.vmName,
	                          'solutionId': $scope.solution.solutionId,
	                          'solutionRevisionId': $scope.revisionId,
	                          'imagetag': imageTagUri,
	                          'userId':  $scope.loginUserID
	                                      }
	                                }
	                                $http({
	                                      method : 'POST',
	                                      url : url,
	                                      data: reqObject
	                                      
	                                }).then(function(response) {
	                                	$mdDialog.hide();
	                                	$location.hash('md-model-detail-template');  // id of a container on the top of the page - where to scroll (top)
										$anchorScroll(); 							// used to scroll to the id 
										$scope.msg = "Deployment Started Successfully. "; 
										$scope.icon = '';
										$scope.styleclass = 'c-success';
										$scope.showAlertMessage = true;
										$timeout(function() {
											$scope.showAlertMessage = false;
										}, 2000);
	                                      },
	                                      function(error) {
	                                            console.warn("Error occured")
	
	                                      });
	                                
	                          } else {
	                                var reqObject = '';
	                                if($scope.exportTo == 'azure'){
	                                      var url = '/azure/compositeSolutionAzureDeployment';
	                                      reqObject = {
	                                                        'acrName': $scope.acrName,
	                                                        'client': $scope.applicationId,
	                                                        'key': $scope.secretKey,
	                                                        'rgName': $scope.resourceGroup,
	                                                        'solutionId': $scope.solution.solutionId,
	                                                        'solutionRevisionId': $scope.revisionId,
	                                                        'storageAccount': $scope.storageAccount,
	                                                        'subscriptionKey':  $scope.subscriptionKey,
	                                                        'tenant': $scope.tenantId,
	                                                        'userId':  $scope.loginUserID
	                                      }
	                                }
	                                else if($scope.exportTo == 'rackspace'){
	                                      var url = "/openstack/compositeSolutionOpenstackDeployment";
	                                      reqObject ={
	                          'vmName': $scope.vmName,
	                          'solutionId': $scope.solution.solutionId,
	                          'solutionRevisionId': $scope.revisionId,
	                          'imagetag': imageTagUri,
	                          'userId':  $scope.loginUserID
	                                      }
	                                }
	                                $http({
	                                      method : 'POST',
	                                      url : url,
	                                      data: reqObject
	                                }).then(function(response) {
	                                	$mdDialog.hide();
	                                	$location.hash('md-model-detail-template');  // id of a container on the top of the page - where to scroll (top)
										$anchorScroll(); 							// used to scroll to the id 
										$scope.msg = "Deployment Started Successfully. "; 
										$scope.icon = '';
										$scope.styleclass = 'c-success';
										$scope.showAlertMessage = true;
										$timeout(function() {
											$scope.showAlertMessage = false;
										}, 2000);
	                                },
	                                function(error) {
	                                      console.warn("Error occured")
	
	                                });
	                          }
							});
						}
						
/*Deploy to k8s*/
						
						$scope.deployToK8s = function(){
						
							$scope.userId = $scope.loginUserID;
							$scope.envId = $scope.ClusterData;
							apiService.deployToK8s($scope.userId , $scope.solution.solutionId , $scope.revisionId , $scope.envId)
		                     .then(
		                         function successCallback(response) {
		                        	$mdDialog.hide();
		                         	$location.hash('tab1-panel'); 
		 							$anchorScroll(); 				
		 							$scope.msg = "The deployment process has been started, will take some time to complete. Notification will be sent on completion."; 
		 							$scope.icon = 'info_outline';
		 							$scope.styleclass = 'c-info';
		 							$scope.showAlertMessage = true;
		 							$timeout(function() {
		 								$scope.showAlertMessage = false;
		 							}, 6000);
		                        
		                         },
		                         function errorCallback(response) {
		                        		$mdDialog.hide();
			                         	$location.hash('tab1-panel'); 
			 							$anchorScroll(); 				
			 							$scope.msg = response.data.response_detail; 
			 							$scope.icon = 'info_outline';
										$scope.styleclass = 'c-error';
			 							$scope.showAlertMessage = true;
			 							$timeout(function() {
			 								$scope.showAlertMessage = false;
			 							}, 6000);
		                         });
							
							
						}
						$scope.Clusters = [];
						$scope.clusterData = function(){
							 $scope.userId = $scope.loginUserID;
							
							apiService.getData($scope.userId)
							.then(
									function successCallback(response){
										$scope.Clusters = response.data.response_body.k8ConfigValueList;
									},
									function errorCallback(response){
										console.log();
										
									});
						}
						$scope.clusterData();
						
						/*Deploy to Local method*/
						$scope.deployLocalPackage = function(){
							$scope.performSVScan($scope.solutionId, $scope.revisionId, "deploy", function() {
								$window.location.assign("/package/getSolutionZip/" + $scope.solutionId + "/" + $scope.revisionId);
							});
						}
						
						apiService.getKubernetesDocUrl().then( function(response){
							$scope.kubernetesDocUrl = response.data.response_body;
						});
						
						//Default values
						$scope.positionM1 = "mime_type";$scope.positionM3 = "image_binary";$scope.positionM2 = 1;$scope.positionM4 = 2;
						$scope.fieldM1 = "mime_type";$scope.fieldM3 = "image_binary";$scope.fieldM4 = "image_binary";$scope.fieldM2 = "mime_type";
						
						
						//Deploy to Broker
						$scope.deployCloudVal = function(){
							var reqObj;
							if($scope.dbType=='csv') {
								reqObj = {
	                                      'acrName': $scope.acrName,
	                                      'client': $scope.applicationId,
	                                      'key': $scope.secretKey,
	                                      'rgName': $scope.resourceGroup,
	                                      'solutionId': $scope.solution.solutionId,
	                                      'solutionRevisionId': $scope.revisionId,
	                                      'storageAccount': $scope.storageAccount,
	                                      'subscriptionKey':  $scope.subscriptionKey,
	                                      'tenant': $scope.tenantId,
	                                      
	                                      //fields if dbType == 'csv'
	                                      'username': $scope.csvUsername,
	                                      'password': $scope.csvPassword,
	                                      'host': $scope.csvHost,
	                                      'port': $scope.csvPort,
	                                      
	                                      //added userId in request parameter
	                                      'userId' : $scope.loginUserID
	                                      
									};
							}
							else if($scope.dbType=='zip') {
								var obj1 =  '{'+ $scope.positionM1 + ':' + $scope.positionM2+','+ $scope.positionM3+":" +$scope.positionM4 +'}';
								var obj2 =  '{'+ $scope.fieldM1 + ':' + $scope.fieldM2+','+ $scope.fieldM3+":" +$scope.fieldM4 +'}';
								var obj3 = '{"url":"'+$scope.brokerURL+'"}';
								reqObj = {
										//zip attributes
										  urlAttribute:obj3,
	                                      jsonPosition:obj1,
	                                      jsonMapping : obj2,
	                                      
	                                      'acrName': $scope.acrName,
	                                      'client': $scope.applicationId,
	                                      'key': $scope.secretKey,
	                                      'rgName': $scope.resourceGroup,
	                                      'solutionId': $scope.solution.solutionId,
	                                      'solutionRevisionId': $scope.revisionId,
	                                      'storageAccount': $scope.storageAccount,
	                                      'subscriptionKey':  $scope.subscriptionKey,
	                                      'tenant': $scope.tenantId,
	                                      
	                                      //added userId in request parameter
	                                      'userId' : $scope.loginUserID
									};
							}
							var req = {
									 method: 'POST',
									 url: '/azure/compositeSolutionAzureDeployment',
									 data: reqObj
									}

									$http(req).then(
										function(data){
											console.log(angular.toJson(data))
											
										},
										function(data){
											 
											});
							
						}
						
						$scope.handleDismiss = function() {
							console.info("in handle dismiss");
							$mdDialog.cancel();
						};

						$scope.updateRating = function(rating) {
							 
							if ($scope.loginUserID != ""
									&& $scope.loginUserID != $scope.solution.ownerId) {
								$scope.solution.solutionRating = rating;
								var ratingDetails = {
									"request_body" : {

										"rating" : rating,
										"solutionId" : $scope.solution.solutionId,
										"userId" : $scope.loginUserID
									},
									"request_from" : "string",
									"request_id" : "string"
								}
									apiService.createRatingSolution(
											ratingDetails).then(
											function(response) {

											}, function(error) {
												//alert(error.response_detail);
											});
							}
						}

						$scope.imgURLdefault ="images/default-model.png";

							
								$scope.onClickModel = function(id, ownerId){
									$scope.updateViewCount = function(){
										$scope.solutionId = id;
										apiService
												.updateViewCount($scope.solutionId)
												.then(
														function(response) {
															$scope.status = response.status;
															$scope.detail = response.data.response_detail;
															$state.go('marketSolutions', {solutionId : id, revisionId : $scope.revisionId, parentUrl:'mymodel' });
														},
														function(error) {
															$scope.status = 'Unable to load data: '
																	+ error.data.error;
															console.log("Error: "+error.data);
															$state.go('marketSolutions', {solutionId : id, revisionId : $scope.revisionId, parentUrl:'mymodel'});
														});

									};
									$scope.updateViewCount();
								}
								
								$scope.showAll = function(){
									$state.go('marketPlace');
									$rootScope.relatedModelType = modelType;
									$rootScope.parentActive = 'marketplace'
								};

						//API call for related solution
						
							function relatedSoltion(){
								var dataObj = {
										"request_body" : {
											"modelType" : $scope.solution.modelType,
											"page" : 0,
											"size" : 6
										},
										"request_from" : "string",
										"request_id" : "string"
									}
								
								apiService
									.relatedSolutions(dataObj)
									.then(
											function(response) {
												
												$scope.relatedSolutions = response.data.response_body.content;
												if( ($scope.relatedSolutions).length ){
													angular.forEach($scope.relatedSolutions, function(solution, key) {
														if (solution.solutionId == $scope.solution.solutionId) {
															($scope.relatedSolutions).splice(key, 1);
														}
													});
												}
											},
											function(error) {
												console.log(error);
											});
							}
							
							$scope.scrolltoId = function(id){
								$location.hash(id);
								$anchorScroll();
							}
							
							$scope.disableEdit = function(){
								$scope.editModel = true;
								if($scope.solution.active == false){$scope.editModel = true;}
								else{
									if($scope.solution.ownerId != $scope.loginUserID){
										if($scope.solution.ownerListForSol != null){
											angular.forEach($scope.solution.ownerListForSol, function(owner, key) {
												if ($scope.loginUserID == owner.userId) {
													$scope.editModel = false;
												}
											
											});
											
										}else{ 
											$scope.editModel = true;
										} 
									}else{
										$scope.editModel = false;
									}
								}
							}
							
							$scope.enableDownload = function() {
								$scope.downloadModel = false;
								if ($scope.solution.active) {
									$scope.downloadModel = true;
								}
							}
							
							/*read cloud enabled from the properties file*/
							$scope.enableDeployToCloud = function(){
						        apiService
						        .getCloudEnabled()
						        .then(
						                function(response) {
						                    if(response.status == 200){
						                        $scope.checkDeployToCloudResponse = JSON.parse(response.data.response_body);
						                       
						                    }
						                },
						                function(error) {
						                    console.log(error);
						                });
						    };
						    $scope.enableDeployToCloud();
						    
						    $scope.showModalPublishReq = function(req, modelName, publishRequestId){
				            	$scope.pbReqId = publishRequestId;
				            	$scope.requestApprovalModal = req;
				            	$scope.requestedModelName = modelName;
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

							$scope.publishReqeuest = function(publishVal){
								$scope.publishVal = publishVal;
								var publishRequestCode = 'DC';
								if(publishVal == 'approve'){
									publishRequestCode = 'AP'
								}
								
								var publishRequestUrl = 'api/publish/request/' + $scope.pbReqId;
								var reqObj = {
										  "request_body": {
											    "publishRequestId": $scope.publishRequestId,
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
													$mdDialog.hide();
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
													$state.go('publishRequest');
												},function errorCallback(response) {
													if (response.data.error_code == "sv_info" || response.data.error_code == "sv_error") {
														$mdDialog.show({
															templateUrl : '../app/error-page/sv-modal.template.html',
															clickOutsideToClose : true,
															locals: {
																reasons: response.data.response_detail,
																isError: response.data.error_code == "sv_error"
															},
															controller : function DialogController($scope, reasons, isError) {
																$scope.reasons = reasons;
																$scope.isError = isError;
																$scope.closePoup = function(){
																	$mdDialog.hide();
																}
															}
														});
													} else {
														$mdDialog.hide();
														$scope.msg = "Error Occured while updating the publish request";
														$scope.icon = 'report_problem';
														$scope.styleclass = 'c-error';
														$scope.showAlertMessage = true;
														$timeout(
																function() {
																	$scope.showAlertMessage = false;
																}, 3000);
													}
											});
								
									
							}
							 $scope.showazurePopup = function(ev){
								 					
								 	if(ev === "deploylocal"){
								 		$rootScope.showPrerenderedDialog("", '#myDialog'); 
								 		
								 	}
								 	else{
								  $mdDialog.show({
									  contentElement: '#deploy',
									  parent: angular.element(document.body),
									  targetEvent: ev,
									  clickOutsideToClose: true
								  });
							 }
							 }
							 
							 $scope.createMicroservice = function(){
								 
									$mdDialog.hide().then(function(){
										$scope.msg = "Micro service creation has been launched, you will see the micro service in the Model Artifacts once it will be created.";
										$scope.icon = 'info_outline';
										$scope.styleclass = 'c-info';
										$scope.showAlertMessage = true;
										$timeout(function() {$scope.showAlertMessage = false;}, 5000);
									});	
									 var requestObj =	{
										 "request_body": {									 
										   "deploymentEnv": $scope.devEnv,
										   "modName": $scope.solution.name,
										   "revisionId": $scope.revisionId,
										   "solutioId": $scope.solutionId,									   
										 }
										};
									 
									 $scope.showMicroService = false;
									 apiService
								        .createMicroservice(requestObj)
								        .then(function(response) {
							        		$scope.getArtifacts();
                                                                           	if(response.data.status_code == 0){
								        		$scope.icon = 'info_outline';
								        		$scope.styleclass = 'c-error';
								        		$scope.msg = "Micro service generation failed.";
								        	}
                                                                           	$scope.showAlertMessage = true;
											$timeout(function() {$scope.showAlertMessage = false;}, 5000);
								         },  function(response) {
								             $scope.showMicroService = true;
								         });
							 }
							 
							 $scope.showMicroservice = function(){
								 $mdDialog.show({
									  contentElement: '#createMicroservice',
									  parent: angular.element(document.body),
									  clickOutsideToClose: true
								  });
							 }
							 						 
							 $scope.closeLicensePopup = function(){
								 $mdDialog.hide();
								 $scope.file = '';
								 $scope.filename = '';
								 $scope.modelUploadError = false;
							 }
							 
							 $scope.uploadLicenseFile = function(){
								$scope.modelLicUploadError = false;

								var uploadUrl = "api/license/upload/" + $scope.loginUserID + "/" + $scope.solutionId + "/" + $scope.revisionId + "/" + $scope.versionId;
								var promise = modelUploadService.uploadFileToUrl($scope.file, uploadUrl);
								
								$scope.uploadingFile = true;
								promise.then(
									function(response) {
										$scope.modelUploadError = false;
										$rootScope.progressBar = 100;
										$mdDialog.hide();
										$scope.msg = ($scope.modelLicense)?"License updated successfully":"License uploaded successfully";
										$scope.icon = '';
										$scope.styleclass = 'c-success';
										$scope.showAlertMessage = true;
										$timeout(function() {
											$scope.showAlertMessage = false;
										}, 5000);
										$scope.isLicenseFound = true;
										// update license content after upload
										$scope.modelLicense = response.response_body;
										$scope.changeLicense = false;
									  $scope.getLicenseFile();
									},
									function(error) {
										if (error.error_code == "sv_info" || error.error_code == "sv_error") {
											$mdDialog.show({
												templateUrl : '../app/error-page/sv-modal.template.html',
												clickOutsideToClose : true,
												locals: {
													reasons: error.response_detail,
													isError: error.error_code == "sv_error"
												},
												controller : function DialogController($scope, reasons, isError) {
													$scope.reasons = reasons;
													$scope.isError = isError;
													$scope.closePoup = function(){
														$mdDialog.hide();
													}
												}
											});
										} else {
											$scope.modelUploadError = true;
										  if(error){
											  		$scope.modelUploadErrorMsg = [error];
													if(error['response_detail'] && (error.response_detail).indexOf('$') != -1){
														$scope.modelUploadErrorMsg = (error.response_detail).substring(1,(error.response_detail).length-1).split('$.');
														$scope.modelUploadErrorMsg.shift();
													}
												}
											$scope.file = '';
											$rootScope.progressBar = 0;
										}
									});
							};
							 
							 $scope.resetProgress = function(){
								 $rootScope.progressBar = 0;
								 $scope.modelcUploadError = false;
							}
							$scope.changeLicense = false;
						   $scope.allTemplates = [];
						   $scope.getAllLicenseTemplates = function(){
							   apiService.getAllLicenseProfile()
				               .then(
				                       function(response) {                   	  
				                           if(response.data.response_body.length) {
				                        	  $scope.allTemplates = response.data.response_body;
				                        	  $scope.selectedLicense = -1;
				                           } 
				                });

						   }
						      
						   $scope.getAllLicenseTemplates();
						   
						   $scope.createLicenseFile = function(licenseText) {
							   
							   var request = licenseText; 
								if(licenseText){
									 apiService.uploadLicenseFile($scope.loginUserID, $scope.solutionId, $scope.revisionId, $scope.versionId, request)
						               .then(function(response){ 

						            	    $scope.msg = ($scope.modelLicense)?"License updated successfully":"License uploaded successfully"; 
											$scope.icon = '';
											$scope.styleclass = 'c-success';
											$scope.showAlertMessage = true;
											$timeout(function() {
												$scope.showAlertMessage = false;
											}, 2500);
						            	   $scope.getLicenseFile();
						            	   $scope.changeLicense = false;
						             });
								}
						   }
						   
							// TODO license-profile-editor handlers
							var selLicProfileTplMsg;
							var bindEvent = function(element, eventName, eventHandler) {
									if (element.addEventListener) {
										element.addEventListener(eventName, eventHandler, false);
									} else if (element.attachEvent) {
										element.attachEvent('on' + eventName, eventHandler);
									}
								},
								unbindEvent = function(element, eventName, eventHandler) {
									if (element.removeEventListener) {
										element.removeEventListener(eventName, eventHandler, false);
									} else if (element.detachEvent) {
										element.detachEvent('on' + eventName, eventHandler);
									}
								},
								winMsgHandler = function(event) {
									// message listener
									if (event.data.key === 'output') {
										var licenseText = JSON.stringify(event.data.value);	
										$scope.licenseOption = 'selectLicProfile';
										$scope.createLicenseFile(licenseText);
										$mdDialog.hide();
									} else if (event.data.key === 'action') {
										if (event.data.value === 'cancel') {
											$mdDialog.hide();
										}
									} else if (event.data.key === 'init_iframe') {
										// if licenseProfileEditorInitMsg then send me
										var iframe = document.getElementById('iframe-license-profile-editor');

										if (selLicProfileTplMsg && iframe) {
											// send message to License Profile Editor iframe
											iframe.contentWindow.postMessage(selLicProfileTplMsg, '*');
										}
									}
								},
								showLicenseProfileEditorDialog = function(event) {

									var onCompleteLicProfileTplDialog = function(scope, element, options) {
										var iframe = document.getElementById('iframe-license-profile-editor');

										if (selLicProfileTplMsg && iframe) {
											// send message to License Profile Editor iframe
											iframe.contentWindow.postMessage(selLicProfileTplMsg, '*');
										}
									};

									// open the license profile modal
									$mdDialog.show({
										controller: function DialogController($scope, $mdDialog) {
											$scope.closeDialog = function() {
												$mdDialog.hide();
											};
										},
										templateUrl:'./app/modular-resource/license-profile-editor-dialog.template.html',
										parent: angular.element(document.body),
										targetEvent: event,
										clickOutsideToClose:false,
										onComplete: onCompleteLicProfileTplDialog
									});
								};

							if (window.licProfEdMsgHandlerRef) {
								unbindEvent(window, 'message', window.licProfEdMsgHandlerRef);
							}
							bindEvent(window, 'message', winMsgHandler);
							window.licProfEdMsgHandlerRef = winMsgHandler;

							$scope.createNewLicenseProfileTemplate = function(event) {
								selLicProfileTplMsg = undefined;
								showLicenseProfileEditorDialog(event);
							};
							$scope.modifyLicenseProfileTemplate = function(event, isUpdateLicense) {
								if(isUpdateLicense){									
									var template = $scope.modelLicense;
									var selectedLic = JSON.stringify($scope.modelLicense);
								} else {
									var selectedLic = $scope.allTemplates[$scope.selectedLicense];
									var template = JSON.parse(selectedLic.template);
								}

								if (selectedLic) {
									try {
										var msgObj = {
											"key": "input",
											"value": template
										};
										selLicProfileTplMsg = msgObj;
									} catch (e) {
										console.error("failed parsing license profile template input", e);
									}
								}
								showLicenseProfileEditorDialog(event);
							};
						}
							
				});
angular.module('angular-star-rating', []).directive('angularStarRating',
		angularStarRating);

function angularStarRating() {
	var directive = {

		restrict : 'EA',
		scope : {
			'value' : '=value',
			'active' : '=active',
			'max' : '=max',
			'hover' : '=hover',
			'isReadonly' : '=isReadonly'
		},
		link : linkFunc,
		template : '<span ng-class="{isReadonly: isReadonly}">'
				+ '<i ng-class="renderObj" '
				+ 'ng-repeat="renderObj in renderAry" '
				+ 'ng-click="!active||setValue($index)" '
				+ 'ng-mouseenter="!active||changeValue($index, changeOnHover )" >'
				+ '</i>' + '</span>',
		replace : true
	};
	return directive;
}

function linkFunc(scope, element, attrs, ctrl) {
	if (scope.max === undefined)
		scope.max = 5; // default
	console.log(scope.test);

	function renderValue() {
		scope.renderAry = [];
		for (var i = 0; i < scope.max; i++) {
			if (i < scope.value) {
				scope.renderAry.push({
					'fa fa-star fa-2x' : true
				});
			} else {
				scope.renderAry.push({
					'fa fa-star-o fa-2x' : true
				});
			}
		}
	}

	scope.setValue = function(index) {
		if (!scope.isReadonly && scope.isReadonly !== undefined) {
			scope.value = index + 1;
		}
	};

	scope.changeValue = function(index) {
		if (scope.hover) {
			scope.setValue(index);
		} else {
			// !scope.changeOnhover && scope.changeOnhover != undefined
		}
	};

	scope.$watch('value', function(newValue, oldValue) {
		if (newValue) {
			renderValue();
		}
	});
	scope.$watch('max', function(newValue, oldValue) {
		if (newValue) {
			renderValue();
		}
	});
}