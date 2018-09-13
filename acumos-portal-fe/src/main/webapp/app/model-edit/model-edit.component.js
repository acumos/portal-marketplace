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
		.module('modelEdit')
		.service('modelUploadService', function($http, $q, $rootScope) {
			
			var self = this;
			var cancelFunc = function(reason) {};
			
			self.cancelUpload = function(reason) {
				self.cancelFunc(reason);
			}

			self.uploadFileToUrl = function(file, uploadUrl) {
				// FormData, object of key/value pair for form fields and values
				var fileFormData = new FormData();
				fileFormData.append('file', file);

				var deffered = $q.defer();
				var canceller = $q.defer();
				
				self.cancelFunc = function(reason) {
					canceller.resolve(reason);
					deffered.reject(reason);
				}
				
				$http.post(uploadUrl, fileFormData, {
					transformRequest : angular.identity,
					timeout : canceller.promise,
					headers : {
						'Content-Type' : undefined
					},uploadEventHandlers: {
				        progress: function (e) {
				                  if (e.lengthComputable) {
				                     $rootScope.progressBar = (e.loaded / e.total) * 100;
				                     $rootScope.progressCounter = $rootScope.progressBar;
				                  }
				        }
				    }

				}).success(function(response) {
					deffered.resolve(response);

				}).error(function(response) {
					deffered.reject(response);
				});

				return deffered.promise;
			}
		})
		.component(
				'modelEdit',
				{

					templateUrl : './app/model-edit/md-model-edit-workflow.template.html',
					controller : function($scope, $location, $http, $rootScope,
							$stateParams, $sessionStorage, $localStorage,
							$anchorScroll, $timeout, FileUploader, apiService,
							$mdDialog, $filter, modelUploadService, $parse, $document, $mdToast, $state, $interval, $sce, browserStorageService) {
						if($stateParams.deployStatus == true){
						$scope.workflowTitle='Export/Deploy to Cloud';$scope.tab='cloud'
						}
						else {$scope.workflowTitle='On-Boarding';$scope.tab='onboard'}

						$scope.revisionId = $stateParams.revisionId;
						$scope.status;
						$scope.activePublishBtn = false;
						$scope.activePublishBtnPB = false;
						$scope.showSolutionImage = false;
						$scope.showSolutionDocs = false;
						$scope.showPublicSolutionDocs = false;
						$scope.supportingPublicDocs = [];
						$scope.supportingDocs = [];
						$scope.tags1 = [];
						componentHandler.upgradeAllRegistered();
						$scope.solutionCompanyDescStatus = false;
						$scope.solutionPublicDescStatus = false;
						$scope.icon = false;
						$scope.modelDocumentation = false;
						$scope.iconImages = ["CLI","curl", "dotnet","javascript", "java", "go",
											"scala","ruby", "rust", 'REST API',"nodejs", "swift", 
											"python", "R"];
						$scope.previewImage = "images/img-list-item.png";
						$rootScope.progressBar = 0;
						$scope.imageerror = false;
						$scope.imagetypeerror = false;
						$scope.docerror = false;
						$scope.flag = false;
						if ($stateParams.solutionId) {
							$scope.solutionId = $stateParams.solutionId;
							localStorage.setItem('solutionId',
									$scope.solutionId);
						}
						
						if(browserStorageService.getUserDetail()){
							$scope.loginUserId = JSON.parse(browserStorageService.getUserDetail());
						}

						apiService.getKubernetesDocUrl().then( function(response){
							$scope.kubernetesDocUrl = response.data.response_body;
						});

						$scope.getComment = function() {
							if (browserStorageService.getUserDetail()) {
								$scope.loginUserID = JSON.parse(browserStorageService
										.getUserDetail())[1];
							}
							var reqObj = {
									  "request_body": {
										    "page": 0,
										    "size": 0
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
										commentReply.name = userDetail.data.response_body.loginName
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
						
						//the comment to reply to
						$scope.commentToReply = {};
						$scope.showPostReply = false;
						$scope.showEditComment = false;
						
						$scope.setReply = function(comment) {
							$scope.comment = comment;
							$scope.replyCommentText = '';
							$rootScope.showPrerenderedDialog("", '#replyToComments'); 
						}
						
						$scope.editedComment = {};
						$scope.setEdit = function(comment) {
							$scope.showPostReply = false;
							$scope.showEditComment = true;
							
							$scope.editedComment = comment;
							$scope.editedCommentText = comment.text;
							
							$location.hash('editedComment');
							$anchorScroll();
						}
						
						
						$scope.editComment = function() {
							$scope.showPostReply = false;
							$scope.showEditComment = false;
							var commentObj = {
									  "request_body": {
										    "text": $scope.editedCommentText,
										    "commentId": $scope.editedComment.commentId,
										    "threadId": $scope.editedComment.threadId,
										    "parentId": $scope.editedComment.parentId,
										    "url": $scope.solutionId,
										    "userId": $scope.loginUserID
										  },
										};
							apiService.updateComment(commentObj).then(function(response) {
								$scope.getComment();
							});
						};
						
						$scope.newcomment = "";
						$scope.postReply = function(){
							$scope.showPostReply = false;
							$scope.showEditComment = false;
							if(browserStorageService.getUserDetail()){
								$scope.loginUserId = JSON.parse(browserStorageService.getUserDetail());
							}
							
								var commentObj = {
										  "request_body": {
											    "text": $scope.replyCommentText,
											    "threadId": $scope.comment.threadId,
											    "parentId": $scope.comment.commentId,
											    "url": $scope.solutionId,
											    "userId": $scope.loginUserId[1]
											  },
											};
								apiService.createComment(commentObj).then(function(response) {
									$scope.closeDialog();
									$scope.replyCommentText = '';
								});
						}
						
						
						$scope.deleteComment = function(comment) {
							apiService.deleteComment(comment.threadId,comment.commentId).then(function(response) {
								$scope.getComment();
							});
						}
						

						$scope.solutionId = localStorage.getItem('solutionId');
						$scope.currentDate = new Date();
						$scope.showPrerenderedDialog = function(ev) {
							componentHandler.upgradeAllRegistered();
							$mdDialog.show({
								contentElement : '#myDialog',
								parent : angular.element(document.body),
								targetEvent : ev,
								clickOutsideToClose : true
							});

							$scope.closeDialog = function() {
								$mdDialog.cancel();
							}
						};

						$scope.handleError = false;
						$scope.handleSuccess = false;
						$scope.tagUpdated = false; // check if tags updated
						$scope.descUpdated = false; // for checking if sol desc
						$scope.showAlertMessage = false;
						// is updated

						var locationUrl = $location.absUrl();
						var pId = locationUrl.split("/")[4] = "marketSolutions?solutionId=";
						console.log(pId);

						$scope.shareWithUrl = locationUrl.substring(0,
								locationUrl.lastIndexOf("/"));
						$scope.shareWithUrl = $scope.shareWithUrl
								+ "/marketSolutions?solutionId="
								+ $scope.solutionId

						$scope.loadCategory = function() {
							apiService
									.getModelTypes()
									.then(
											function(response) {
												$scope.allCategory = response.data.response_body;
											},
											function(error) {
												$timeout(function() {
													$scope.handleError = true;
												}, 2000);
												$scope.handleError = false;
												$scope.status = 'Unable to load customer data: '
														+ error.data.error;
											});
						}
						$scope.loadCategory();

						$scope.loadToolkitType = function() {
							apiService
									.getToolkitTypes()
									.then(
											function(response) {
												$scope.alltoolkitType = response.data.response_body;
											},
											function(error) {
												$timeout(function() {
													$scope.handleError = true;
												}, 2000);
												$scope.handleError = false;
												$scope.status = 'Unable to load customer data: '
														+ error.data.error;
											});
						}
						$scope.loadToolkitType();

						$scope.showVersion = function() {
							if(angular.element('.md-version-ddl1').css('display') == 'none'){
								angular.element('.md-version-ddl1').show();
							} else {
								angular.element('.md-version-ddl1').hide();
							}	
						}
						
						$document.on('click', function(){
							if(angular.element('.md-version-ddl1')){
								angular.element('.md-version-ddl1').hide();
							}
						});
						
						$scope.loadVersionDetails = function(solutionId, revisionId, versionId, modifiedDate){
							$scope.version = $scope.versionList.filter(function (versions) { return versions.revisionId == revisionId;})[0];
							$scope.solution.solutionId = solutionId; 
							$scope.revisionId = revisionId;
							$scope.versionId = versionId;
							angular.element('.md-version-ddl1').hide();
							$scope.completedOnDate = modifiedDate;
							$scope.loadData();
							$scope.getProtoFile();
						}
						
						$scope.getPublishRequestDetail = function(){
							var searchPublishRequestUrl = "api/publish/request/search/revision/" + $scope.revisionId ;
							$http(
									{
										method : 'GET',
										url : searchPublishRequestUrl
									})
									.then(
											function successCallback(response) {
												$scope.publishRequest = response.data.response_body;
											},function errorCallback(response) {
												//Do nothing
										});
						}
						
						$scope.loadData = function() {
							$scope.apiUrl;
							angular.element('.md-version-ddl1').hide();
							if($scope.tagUpdated = true){
								$scope.tags1 = $scope.tags1;
							}else{
								$scope.tags1 = [];
							}
							
							if ($scope.solutionId) {
								$scope.solutionId = $scope.solutionId;
							}
							apiService
									.getSolutionDetail($scope.solutionId)
									.then(
											function(response) {
												if (response.data.response_body) {
													$scope.solution = response.data.response_body;
													$scope.versionList = [];
													$scope.categoryname = $scope.solution.modelType;
													$scope.toolkitname = $scope.solution.tookitType;
													$scope.solutionName = $scope.solution.name;
													$scope.popupSolutionId = $scope.solution.solutionId;
													if($scope.completedOnDate){
														$scope.solution.created = $scope.completedOnDate;
														$scope.solution.modified = $scope.completedOnDate;
													}
													if ($scope.solution.revisions != null) {
														var counter = 0;
														var length = $scope.solution.revisions.length;
														//**adding list of versions
														
														while(counter < length){
															($scope.versionList).push(response.data.response_body.revisions[counter]);
															counter++;
														}
														($scope.versionList).sort(function(a, b) {
															  // sort version according to created date
															  const genreA = a.created;
															  const genreB = b.created;

															  var comparison = 0;
															  if (genreA < genreB) {
															    comparison = 1;
															  } else if (genreA > genreB) {
															    comparison = -1;
															  }
															  return comparison; }
														);
														$scope.version = $scope.versionList.filter(function (versions) { return versions.revisionId == $scope.revisionId;})[0];
														if( !$scope.revisionId ){
															$scope.revisionId = $scope.versionList[0].revisionId;
															$scope.versionId = $scope.versionList[0].version;
															$scope.version = $scope.versionList[0];
															$scope.solution.created = $scope.versionList[0].modified;
															$scope.solution.modified = $scope.versionList[0].modified;
														} else {
															$scope.revisionId = $scope.version.revisionId;
															$scope.versionId = $scope.version.version;
														}
														$scope.getComment();
														$scope.getProtoFile();
													}
													$scope.solutionEditorCompanyDesc = $scope.solution.description;
													$scope.isModelActive = $scope.solution.active;
													// $scope.solutionDesc =
													// $scope.solution.description;
													if ($scope.solution.solutionTagList) {
														for (var i = 0; i < $scope.solution.solutionTagList.length; i++) {
															$scope.tags1
																	.push({
																		text : $scope.solution.solutionTagList[i].tag
																	});
														}
													} else if ($scope.solution.solutionTag) {
														$scope.tags1
																.push({
																	text : $scope.solution.solutionTag
																});

													}
													if($scope.solution.picture) {
														$scope.showSolutionImage = true;
														$scope.imgURLdefault = " "
													}
													$scope.showSolutionDocs = false;
													$scope.showPublicSolutionDocs = false;
													$scope.supportingPublicDocs = [];
													$scope.supportingDocs = [];
													$scope.getArtifacts();
													$scope.getUserImage();
													//$scope.getModelValidation();
													$scope.getSolCompanyDesc();
													$scope.getSolPublicDesc();
													$scope.getPublicSolutionDocuments();
													$scope.getCompanySolutionDocuments();
													$scope.getAuthorList();
													$scope.getPublishRequestDetail();
												} else {
													// alert("Error Fetching
													// Data");

													$scope.msg = "Error Fetching Data ";
													$scope.icon = 'report_problem';
													$scope.styleclass = 'c-error';
													$scope.showAlertMessage = true;
													$timeout(
															function() {
																$scope.showAlertMessage = false;
															}, 200);
												}

											},
											function(error) {
												$timeout(function() {
													$scope.handleError = true;
												}, 2000);
												$scope.handleError = false;
												$scope.status = 'Unable to load customer data: '
														+ error.data.error;
											});
							$scope.publishalert = '';
						}
						$scope.loadData();

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
												});
						}

						$scope.getSolCompanyDesc = function() {
							var req = {
								method : 'GET',
								url : '/api/solution/revision/' + $scope.revisionId  + "/OR/description"
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionCompanyDesc = data.response_body.description;
												$scope.solutionCompanyDesc1 = $sce.trustAsHtml(data.response_body.description);
												if($scope.solutionCompanyDesc){
													$scope.solutionCompanyDescStatus = true;
												}
											}).error(
											function(data, status, headers,
													config) {
											});
						}
						//$scope.getSolCompanyDesc();

						$scope.getSolPublicDesc = function() {
							var req = {
								method : 'GET',
								url : '/api/solution/revision/' + $scope.revisionId  + "/PB/description"
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionPublicDesc = data.response_body.description;
												$scope.solutionPublicDesc1 = $sce.trustAsHtml(data.response_body.description);
												if($scope.solutionPublicDesc){
													$scope.solutionPublicDescStatus = true;
												}
											}).error(
											function(data, status, headers,
													config) {
											});
						}
						//$scope.getSolPublicDesc();
						
						
						$scope.getAuthorList = function(tag,ev){
							apiService.getAuthors($scope.solutionId, $scope.revisionId).then(function(response) {
								$scope.AuthorsTag = response.data.response_body;
							});
						}
						//$scope.getAuthorList();	
						
						
						$scope.tagRemoved1 = function(tag,ev){
							$scope.deleteuser = tag.name;
							 	$scope.removeauthor = tag;
					        	  $mdDialog.show({
					        		  contentElement: '#confirmPopupDeleteAuthor',
					        		  parent: angular.element(document.body),
					        		  targetEvent: ev,
					        		  clickOutsideToClose: true
					        	  });
					        	  return false;
					          }
						$scope.deleteAuthor = function(){
					    	var obj = {
				    				"request_body": {
							  			name: $scope.removeauthor.name,
							  			contact: $scope.removeauthor.contact
				    				}
					        };
			    		
					    	apiService.removeAuthor($scope.solutionId, $scope.revisionId, obj).then(function successCallback(response) {
					    		$scope.AuthorsTag = response.data.response_body;
					    		$scope.cancelAuthor();
					    	},
					    	function errorCallback(response) {
					    		$scope.msg = "Error while removing Author";
								$scope.icon = 'report_problem';
								$scope.styleclass = 'c-error';
								$scope.showAlertMessage = true;
								$timeout(
										function() {
											$scope.showAlertMessage = false;
										}, 8000);
					    	});
					  
					    	
					    	$mdDialog.hide();
                        };
						  $scope.closePoup = function(){
						              	  $mdDialog.hide();
						              	  $scope.result = true;
						              	  return false;
						                }
						    
						  $scope.setAuthor = function(){						    	
						    	var vart = $scope.AddAuthor.$valid;
						    	if($scope.AddAuthor.$valid) {
						    		var obj = {
							    				"request_body": {
										  			name: $scope.Author.Name,
										  			contact: $scope.Author.cntinfo
							    				}
								        };
						    		
						    	apiService.addAuthor($scope.solutionId, $scope.revisionId, obj).then(function successCallback(response) {
						    		$scope.AuthorsTag = response.data.response_body;
						    		$scope.Author.Name = "";
							    	$scope.Author.cntinfo= "";
							    	$scope.AddAuthor.cntinfo.$touched = false;
							    	$scope.AddAuthor.Name.$touched = false;
						    	},
						    	function errorCallback(response) {
						    		$scope.msg = "Error while adding Author :  " + response.data.response_detail;
									$scope.icon = 'report_problem';
									$scope.styleclass = 'c-error';
									$scope.showAlertMessage = true;
									$timeout(
											function() {
												$scope.showAlertMessage = false;
											}, 8000);
						    	});
						    	}
							}
						  $scope.updateTag = function(tag)
						  {
							  $scope.selectedtagindex = $scope.AuthorsTag.indexOf(tag);							  
							  $scope.flag = true;
							  $scope.Author =[];
							  $scope.Author.Name = tag.name;
							  $scope.Author.cntinfo = tag.contact;
							  $scope.updateName = tag.name;
							  $scope.updateCntInfo = tag.contact;							  
						  }
						  
						  $scope.updateAuthor = function(){							   							    
						    	if($scope.AddAuthor.$valid) {	
						    		//$scope.Author.Name;
						    		//$scope.Author.cntinfo;
						    		$scope.removeauthor = [];						    		
						    		$scope.removeauthor.name = $scope.updateName;
						    		$scope.removeauthor.contact = $scope.updateCntInfo;
						    		$scope.deleteAuthor();
						    		$scope.getAuthorList();
						    		$scope.setAuthor();
						    		
						    		$scope.flag = false;
								};						    		
						    }
							
						  
						  $scope.cancelAuthor = function(){
							  	$scope.Author.Name = "";
						    	$scope.Author.cntinfo= "";
						    	$scope.AddAuthor.cntinfo.$touched = false;
						    	$scope.AddAuthor.Name.$touched = false;
						    	$scope.flag = false;
						    	$scope.selectedtagindex = -1;
						  }
						  

						$scope.updateSolution = function() {
							if($scope.categoryname&&$scope.toolkitname)$scope.pToP = true;
							$scope.solution.PrevSolname = $scope.solution.name;
							$scope.solution.name = $scope.solutionName;							
							$scope.solution.solutionId = $scope.popupSolutionId;
							/*if ($scope.revisionsVersion) {
								$scope.revisions.version = $scope.revisionsVersion;
							}*/
							if ($scope.activeFalse == false) {
								$scope.solution.active = $scope.activeFalse;
							}
							var solution = {
								"request_body" : {
									//"accessType" : $scope.solution.accessType,
									"active" : $scope.solution.active,
									"created" : $scope.solution.created,
									// "description" :
									// $scope.solution.description,
									// "downloadCount": 0,
									// "loginName": "string",
									// "metadata": "string",
									"modelType" : $scope.categoryname,
									// "modified": "2017-08-11T14:04:53.784Z",
									"name" : $scope.solution.name,
									"ownerId" : $scope.solution.ownerId,
									// "ownerName": "string",
									// "pageNo": 0,
									// "revisions": [
									// {
									// "created": "2017-08-11T14:04:53.784Z",
									// "description": "string",
									// "metadata": "string",
									// "modified": "2017-08-11T14:04:53.784Z",
									// "ownerId": "string",
									// "revisionId": "string",
									// "solutionId": "string",
									// "version": "string"
									// }
									// ],
									// "size": 0,
									"solutionId" : $scope.solution.solutionId,
									// "solutionRating": 0,
									// "solutionTag": "string",
									// "solutionTagList": [
									// {
									// "tag": "string"
									// }
									// ],
									// "sortingOrder": "string",
									"tookitType" : $scope.toolkitname
								}
							// ,
							// "request_from": "string",
							// "request_id": "string"
							}

							apiService
									.updateSolutions(solution)
									.then(
											function(response) {
												$scope.status = response.status;
												$scope.detail = response.data.response_detail;

												/*
												 * $timeout(function() {
												 * alert("Updated: " +
												 * $scope.detail); }, 10);
												 */
												/*
												 * if($scope.solution.active ==
												 * false){
												 * $location.hash('manage-models');
												 * $anchorScroll();
												 * 
												 * $scope.msg = "Solution
												 * Deleted Successfully";
												 * $scope.icon = '';
												 * $scope.styleclass =
												 * 'c-success';
												 * $scope.showAlertMessage =
												 * true; $timeout(function() {
												 * $scope.showAlertMessage =
												 * false; }, 3500); } else{
												 */
												$location.hash('manage-models');
												$anchorScroll();

												$scope.msg = "Updated: "
														+ $scope.detail;
												$scope.icon = '';
												$scope.styleclass = 'c-success';
												$scope.showAlertMessage = true;
												$timeout(
														function() {
															$scope.showAlertMessage = false;
															if($scope.solution.active == false){
																$state.go('manageModule');
															}
														}, 3500);
												/* } */
												$scope.tagUpdated = true;
												$scope.loadData();
												
											},
											function(response) {
												$location.hash('manage-models');
												$anchorScroll();
												$scope.msg = response.data.response_detail;
												$scope.icon = 'report_problem';
												$scope.styleclass = 'c-warning';
												$scope.solutionName = $scope.solution.PrevSolname;
												$scope.solution.name = $scope.solution.PrevSolname;
												$scope.showAlertMessage = true;
												$timeout(
														function() {
															$scope.showAlertMessage = false;
														}, 2500);
												$scope.handleError = false;
											});
						}
						
						$scope.solutionPublicDescLength = false;
						$scope.solutionCompanyDescLength = false;
						$scope.checkCompDescrLength = function()
						{
							if($scope.solutionCompanyDesc){

								$scope.solutionCompanyDescString = $scope.solutionCompanyDesc ? String($scope.solutionCompanyDesc).replace(/<[^>]+>/gm, '') : '';
								
								if($scope.solutionCompanyDesc.indexOf('src="') > -1){
								    var newValue = $scope.solutionCompanyDesc.split('src="')[1].split('"')[0];
								    $scope.solutionCompanyDescLength = true;
								}
								else if($scope.solutionCompanyDescString.replace(/\s/g, "") == ""){
									$scope.solutionCompanyDescLength = false;
								}
								else{
									$scope.solutionCompanyDescLength = true;									
								}
							}
							else{
								$scope.solutionCompanyDescLength = false;								
							}
							
						}
						
						$scope.updateCompanyDescription = function() {
							
							
							if($scope.solutionCompanyDescLength = true){
								$scope.showDCKEditor = false
							}else{
								$scope.showDCKEditor = true
							} 
							
							var solution = {
									"request_body": {
										"description" : $scope.solutionCompanyDesc
									}
							};
							var req = {
								method : 'POST',
								url : '/api/solution/revision/' + $scope.revisionId + "/OR/description",
								data : solution
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionCompanyDesc = data.response_body.description;
												$scope.solutionCompanyDescStatus = true;
												$location.hash('manage-models');
												$anchorScroll();
												$scope.getSolCompanyDesc();
												
												$scope.msg = "Updated: Solution Description";
												$scope.icon = '';
												$scope.styleclass = 'c-success';
												$scope.showAlertMessage = true;
												$timeout(
														function() {
															$scope.showAlertMessage = false;
															if($scope.solution.active == false){
																$state.go('manageModule');
															}
														}, 3500);
											}).error(
											function(data, status, headers,
													config) {
												 
												$scope.solutionCompanyDesc = '';
												$scope.solutionCompanyDescLength = false;
											});
						}
						
						$scope.checkPubDescrLength = function()
						{
							if($scope.solutionPublicDesc){
								$scope.solutionPublicDescString = $scope.solutionPublicDesc ? String($scope.solutionPublicDesc).replace(/<[^>]+>/gm, '') : '';
								
								if($scope.solutionPublicDesc.indexOf('src="') > -1){
								    var newPBValue = $scope.solutionPublicDesc.split('src="')[1].split('"')[0];
								    $scope.solutionPublicDescLength = true;
								}
								else if($scope.solutionPublicDescString.replace(/\s/g, "") == "" ){
									$scope.solutionPublicDescLength = false ;
								}
								else{
									$scope.solutionPublicDescLength = true;																		
								}
							}else{
								$scope.solutionPublicDescLength = false;								
								
							}
						}

						$scope.updatePublicDescription = function() {
							

							if($scope.solutionPublicDescLength = true){
								$scope.showCKEditor = false
							}else{
								$scope.showCKEditor = true
							} 
							
							var solution = {
										"request_body": {
										"description" : $scope.solutionPublicDesc
									}
							};
							var req = {
								method : 'POST',
								url : '/api/solution/revision/' + $scope.revisionId + "/PB/description",
								data : solution
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionPublicDesc = data.response_body.description
												$scope.solutionPublicDescStatus = true;
												$location.hash('manage-models');
												$anchorScroll();
												$scope.getSolPublicDesc();
												$scope.msg = "Updated: Solution Description";
											$scope.icon = '';
											$scope.styleclass = 'c-success';
											$scope.showAlertMessage = true;
											$timeout(
													function() {
														$scope.showAlertMessage = false;
														if($scope.solution.active == false){
															$state.go('manageModule');
														}
													}, 3500);
											}).error(
											function(data, status, headers,
													config) {
												$scope.solutionPublicDesc = '';
												$scope.solutionPublicDescLength = false;
											});
						}

						$scope.copiedCompanyDesc = false;
						$scope.copyPublicToCompany = function() {
							var solution = {
										"request_body": {
										"description" : $scope.solutionPublicDesc
									}
							};
							var req = {
								method : 'POST',
								url : '/api/solution/revision/' + $scope.revisionId + "/OR/description",
								data : solution
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionCompanyDesc1 = data.response_body.description;
												$scope.solutionCompanyDesc = data.response_body.description;
												$scope.copiedCompanyDesc = true;
											}).error(
											function(data, status, headers,
													config) {
											});

						}
						
						
						$scope.copyFromOtherRevision = function(publicOrOrg) {
							return apiService
							.getSolutionDescription(publicOrOrg, $scope.solutionId, $scope.fromRevisionId.revisionId)
							.then(
									function(response) {

										if(response.data.response_body.description != null ){
											var copySolutionDesc = {
													"request_body": {
														"description" : response.data.response_body.description
													}
												};
											var req = {
													method : 'POST',
													url : '/api/solution/revision/' + $scope.revisionId + '/' + publicOrOrg + '/description',
													data : copySolutionDesc
												};
											
											$http(req)
											.success(
													function(data, status, headers,
															config) {
														if('OR' == publicOrOrg){
															$scope.solutionCompanyDesc = data.response_body.description;
															$scope.solutionCompanyDesc1 = data.response_body.description;
														}
														else {
															$scope.solutionPublicDesc = data.response_body.description;
															$scope.solutionPublicDesc1 = data.response_body.description;
														}
													}).error(
													function(data, status, headers,
															config) {
													});
											
										} else {
										}
									},
									function(error) {

										$scope.status = error.data.error;
									});
						}

						$scope.copiedPublicDesc = false;
						$scope.copyCompanyToPublic = function() {
							var solution = {
									"request_body": {
										"description" : $scope.solutionCompanyDesc
									}
							};
							var req = {
								method : 'POST',
								url : '/api/solution/revision/' + $scope.revisionId + "/PB/description",
								data : solution
							};
							
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionPublicDesc = data.response_body.description;
												$scope.copiedPublicDesc = true;
											}).error(
											function(data, status, headers,
													config) {
											});
						}
						
						

						$scope.loadAllTags = function(query) {
							$scope.getTags = {
								"request_body" : {
									"page" : 0
								}
							}

							return apiService
									.getAllTag($scope.getTags)
									.then(
											function(response) {

												$scope.status = response.data.response_detail;
												$scope.allTags = response.data.response_body.tags;
												return $scope.allTags;
											},
											function(error) {

												$scope.status = error.data.error;
											});
						}
						$scope.loadAllTags();
						$scope.createTagMethod = false;
						$scope.tagAdded = function(tag) {
							$scope.isTagExists = false;
							angular.forEach($scope.allTags, function(item) {
								if (tag.text == item.text) {
									$scope.isTagExists = true; 
								}
							});

							if($scope.isTagExists == true){
								$scope.addTag(tag);
							}else{
								$scope.createTag(tag);
							}
							
						};
						
						$scope.addTag = function(tag){
							apiService.updateAddTag($scope.solution.solutionId,
									tag.text).then(function(response) {
								$scope.status = response.data.response_detail;
								
								if( $scope.createTagMethod == false){
									var toast = $mdToast.simple()
							        .content('Tag Added')
							        .position('bottom right')
							        .theme('success-toast')
							        .hideDelay(2000);
									$mdToast.show(toast);
								}
								 
							     var refreshTag = $scope.tags1;
							     chkCount();
							     
							}, function(error) {
							});
						}
						
						$scope.createTag = function(tag){
							var addtag = tag;
							var dataObj = {
									  		"request_body": {
									  			"tag": tag.text
									  			}
											}
							apiService.createTags(dataObj).then(function(response) {

								$scope.status = response.data.response_detail;
								
								chkCount();
								var toast = $mdToast.simple()
						        .content('Tag Added')
						        .position('bottom right')
						        .theme('success-toast')
						        .hideDelay(2000);
								$mdToast.show(toast);
								$scope.createTagMethod = true;
								$scope.addTag(addtag);

							}, function(error) {
								console.log("Tag Error: ",error)
							});
						
						}					

						$scope.tagRemoved = function(tag) {

							console.log('Removed: ' + tag.text);
							apiService.deleteTag($scope.solution.solutionId,
									tag.text).then(function(response) {

								$scope.status = response.data.response_detail;
								chkCount();
								var toast = $mdToast.simple()
						        .content('Tag Removed')
						        .position('bottom right')
						        .theme('success-toast')
						        .hideDelay(2000);
								$mdToast.show(toast);

							}, function(error) {
								$scope.status = error.data.response_detail;
								var toast = $mdToast.simple()
						        .content('Unexpected Server Error: '+$scope.status)
						        .position('bottom right')
						        .theme('success-toast')
						        .hideDelay(2000);
								$mdToast.show(toast);
							});
						};

						$scope.publishtoMarket = function(pub_value) {
							var userId = sessionStorage.getItem("SessionName");
							$scope.currentModelAccess = pub_value;
							if (pub_value == 'OR') {
								var flow= 'company';
							} else if (pub_value == 'PB') {
								var flow= 'public';
							}

							if ($scope.currentModelAccess == $scope.version.accessTypeCode) {
								if ($scope.version.accessTypeCode == 'OR') {
									$scope.accessName = 'Company/Organization';
								} else if ($scope.version.accessTypeCode == 'PB') {
									$scope.accessName = 'Public';
								}

								$location.hash('manage-models');
								$anchorScroll();
								$scope.msg = "Solution Already Published in "
										+ $scope.accessName + " marketplace";
								$scope.icon = 'report_problem';
								$scope.styleclass = 'c-warning';
								$scope.showAlertMessage = true;
	
								$timeout(function() {
									$scope.showAlertMessage = false;
								}, 2500);
				
								
								if( $scope.validationEnabled == true ){
									$scope.getModelValidation(flow);
								}
								 return;

							}

							if ($scope.solution.ownerId) {
								if ($scope.version.accessTypeCode == 'PR'
										|| $scope.version.accessTypeCode == 'OR'  || $scope.version.accessTypeCode == 'PB') {
									var data = $.param({
										visibility : pub_value,
										userId : $scope.solution.ownerId,
										revisionId : $scope.revisionId
									});
									apiService
											.updatePublishSolution(
													$scope.solution.solutionId,
													data)
											.then(
													function(response) {
														if( $scope.validationEnabled == true ){
															$scope.getModelValidation(flow);
														}
														
														$scope.handleSuccess = true;
														$timeout(
																function() {
																	$scope.handleSuccess = false;
																}, 4500);
														// scroll to top :
														// scrolltotop
														$scope.styleclass = 'c-error';
														if(response.data.error_code == 500){
															$scope.errorMsg = response.data.response_detail;
															$scope.msg = "Solution Not Published";
															$scope.icon = 'report_problem';
															
														}else if (response.data.error_code == 100){
															
															$scope.trackId = response.data.response_detail;
															$scope.msg = "Solution Published Successfully";
															$scope.icon = '';
															$scope.styleclass = 'c-success';
															$scope.modelDocumentation = true;
														}else{
															$scope.status = response.data.response_detail;
															$scope.msg = "Unexpected Error Occured";
															$scope.icon = 'report_problem';
														}
														
														$location.hash('manage-models');
														$anchorScroll();
														$scope.showAlertMessage = true;
														$timeout(
																function() {
																	$scope.showAlertMessage = false;
																}, 2500);
														$scope.loadData();
														

													},
													function(error) {
														$scope.handleError = true;
														$timeout(
																function() {
																	$scope.handleError = false;
																}, 4500);

														$scope.status = error.data.response_detail;
														$location
																.hash('manage-models');
														$anchorScroll();
														$scope.msg = "Error encountered: "
																+ $scope.status;
														$scope.icon = 'report_problem';
														$scope.styleclass = 'c-warning';
														$scope.showAlertMessage = true;
														$timeout(
																function() {
																	$scope.showAlertMessage = false;
																}, 2500);

													});

								} else {
									$scope.handleError = true;
									$timeout(function() {
										$scope.handleError = false;
									}, 4500);

									$scope.status = 'This solution already published in the Market place';
									$location.hash('manage-models');
									$anchorScroll();
									$scope.msg = "This solution already published in the Market place";
									$scope.icon = 'info_outline';
									$scope.styleclass = 'c-info';
									$scope.showAlertMessage = true;
									$timeout(function() {
										$scope.showAlertMessage = false;
									}, 3500);

								}
							} else {
								$scope.handleError = true;
								$timeout(function() {
									$scope.handleError = false;
								}, 4500);

								$scope.status = 'Please sign in as owner  to publish solution';
								$location.hash('manage-models');
								$anchorScroll();
								$scope.msg = "Please sign in as owner to publish solution";
								$scope.icon = 'info_outline';
								$scope.styleclass = 'c-info';
								$scope.showAlertMessage = true;
								$timeout(function() {
									$scope.showAlertMessage = false;
								}, 3500);
							}

						}

						$scope.deleteSolution = function() {

						}

						/** * shared with team members functionalities START* */

						$scope.getAllUsersList = function() {
							return apiService
									.getAllUsersLists()
									.then(
											function(response) {
												$scope.allUserDetails = response.data.response_body;
												/* return $scope.allUserDetails; */
												/*Check functionality*/
												/*return response.data.response_body
														.map(function(item) {
															return item.firstName;
														});*/

											}, function(error) {
											});

						}
						$scope.getAllUsersList();

						$scope.loadShareWithTeam = function() {
							apiService
									.getShareWithTeam($scope.solutionId)
									.then(
											function(response) {
												if (response.data.error_code == "500") {
													console
															.log("loadShareWithTeam Error: "
																	+ response.data.response_detail);
													$scope.sharedWith = '';
												} else {
													$scope.sharedWith = response.data.response_body.userList;
													
												}

											},
											function(error) {
												$timeout(function() {
													$scope.handleError = true;
												}, 2000);
												$scope.handleError = false;
												$scope.status = 'Unable to load customer data: '
														+ error.data.error;
											});

						}
						$scope.loadShareWithTeam();

						$scope.shareWithTeam = function(asyncSelected) {
							if (asyncSelected == $scope.solution.ownerName
									.substr(0, $scope.solution.ownerName
											.indexOf(' '))) {
								/*alert("Cannot share with yourself");*/
								$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
	                               $anchorScroll();
	                               $scope.msg = "Shared Successfully";
	                               $scope.icon = 'report_problem';
	                               $scope.styleclass = 'c-warning';
	                               $scope.showAlertMessage = true;
	                               $timeout(function() {
	                                   $scope.showAlertMessage = false;
	                               }, 2000);
								return

							}

							angular.forEach($scope.allUserDetails, function(
									item) {
								if (asyncSelected == item.firstName) {

									$scope.sharedWithUserName = item.firstName;
									$scope.shareWithUser = item.userId;
								}
							});

							apiService
									.insertShareWithTeam($scope.solutionId,
											$scope.shareWithUser)
									.then(
											function(response) {

												if (response.data.error_code == "500") {
													$scope.msg = "Solution is already shared with "
															+ $scope.sharedWithUserName;
													$scope.icon = 'info_outline';
													$scope.styleclass = 'c-info';
													$scope.showAlertMessage = true;
													$timeout(
															function() {
																$scope.showAlertMessage = false;
															}, 3500);

													$scope.asyncSelected = '';
												} else {
													$scope.asyncSelected = '';
													$scope.loadShareWithTeam();
													$scope.msg = "Solution shared with "
															+ $scope.sharedWithUserName;
													$scope.icon = '';
													$scope.styleclass = 'c-success';
													$scope.showAlertMessage = true;
													$timeout(
															function() {
																$scope.showAlertMessage = false;
															}, 2000);
												}
												$scope.status = response.data.response_detail;
											},
											function(error) {
											});
						}

						$scope.removeSharedUser = function(userFname) {
							angular.forEach($scope.sharedWith, function(
									item) {
								if (userFname == item.firstName) {

									$scope.sharedWithUserName = item.firstName;
									$scope.shareWithUser = item.userId;
								}
							});

							apiService
									.deleteShareWithTeam($scope.solutionId,
											$scope.shareWithUser)
									.then(
											function(response) {

												$scope.status = response.data.response_detail;
												if (response.data.error_code == "100") {
													$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
						                               $anchorScroll();
						                               $scope.msg = "User: "+ $scope.sharedWithUserName+ " removed successfully as a co-author.";
						                               $scope.icon = '';
						                               $scope.styleclass = 'c-success';
						                               $scope.showAlertMessage = true;
						                               $timeout(function() {
						                                   $scope.showAlertMessage = false;
						                               }, 2000);
						                               
													$scope.loadShareWithTeam();
													$scope.getAllUsersList();
												} else {
													alert("Unexpected error occured");
												}

											},
											function(error) {

												alert("Error "
														+ error.data.response_detail)

											});

						}
						
						/** User chips */
						var allGroups = [];
						$scope.getUsersChips = function() {
							$scope.uData = [];
							$scope.allUserDetails = [];
							$scope.allGroups1 =[];
							$scope.userActiveStatus = true;
							apiService
									.getAllActiveUser($scope.userActiveStatus)
									.then(
											function(response) {
												$scope.loadShareWithTeam();
												$scope.allUserDetails = response.data.response_body;
												console.log("allGroups: ",$scope.allGroups);
												$scope.allGroups1 = $scope.allGroups;
												
												angular.forEach($scope.allUserDetails, function(item1, key1) {
													angular.forEach($scope.sharedWith, function(item2, key2) {
														if(item1.userId == item2.userId){
															$scope.allUserDetails.splice(key1, 1);
														}
														
													});
												});
												$scope.allUserDetails.map(function(item) {
													//$scope.allGroups.push(item.firstName); // item.lastName
													
													$scope.allGroups.push({
														firstName : item.firstName,
														lastName : item.lastName,
														userEmailId : item.emailId,
														userID : item.userId
													});
													
												});

											}, function(error) {
											});

						}
						$scope.getUsersChips();

						$scope.queryGroups = function(search) {
							/***call here**/
							//$scope.getUsersChips();
							$scope.allGroupsDetails = []
							$scope.allGroups.map(function(item) {
								if( item.userID != $scope.solution.ownerId){
									$scope.allGroupsDetails.push({
										'firstName' : item.firstName,
										'lastName' : item.lastName,
										'userId' : item.userID,
										'emailId' : item.userEmailId
									}); // item.lastName
								}
							});
							
							var firstPass = $filter('filter')($scope.allGroupsDetails, search);
							return firstPass
									.filter(function(item) {
										 $scope.selectedGroups;
										 return $scope.selectedGroups.indexOf(item) === -1;
									});
						};

						$scope.addGroup = function(group) {
							$scope.selectedGroups.push(group);
						};

						$scope.allGroups = allGroups;
						$scope.selectedGroups = [];

						$scope.$watchCollection('selectedGroups', function() {
							$scope.availableGroups = $scope.queryGroups();
							$scope.selectedGroups;
							$scope.availableGroups;
						});

						$scope.shareWithMultiple = function() {
							$scope.sharedWithOwner = false;
							$scope.reqSharedWith = [];
							$scope.selectedGroups;
							$scope.allUserDetails;
							$scope.selectedId = [];
							$scope.selectedGroups.map(function(item1) {
								$scope.allUserDetails.map(function(item2) {
									if (item1.userId == item2.userId) {
										// $scope.selectedGroups =
										// $scope.allUserDetails.userId
										$scope.selectedId.push(item2.userId);
									}
								})

							})
							angular.forEach($scope.selectedId, function(item) {
									if($scope.loginUserId[1] == item){
										$scope.sharedWithOwner = true;
										
									}
							});
							
							angular.forEach($scope.selectedId, function(itemA) {
								angular.forEach($scope.sharedWith, function(itemB) {
									if(itemB.userId == itemA){
										$scope.alreadySharedWithUserId = [];
										$scope.alreadySharedWithUser = true;
										$scope.alreadySharedWithUserId.push(itemB.userId);
									}
								});
							});
							

							$scope.reqSharedWith = {
								"request_body" : $scope.selectedId
							};
							$scope.reqSharedWith;
							if($scope.sharedWithOwner == true){
								$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
	                               $anchorScroll();
	                               $scope.msg = "Cannot share the solution with yourself.";
	                               $scope.icon = 'report_problem';
	                               $scope.styleclass = 'c-warning';
	                               $scope.showAlertMessage = true;
	                               $timeout(function() {
	                                   $scope.showAlertMessage = false;
	                               }, 2000);
								return
							}
							/*else if($scope.alreadySharedWithUser == true){
								$scope.alreadySharedWithUsersName = [];
								$scope.allUserDetails.map(function(item1) {
									$scope.alreadySharedWithUserId.map(function(item2) {
										if (item1.userId == item2) {
											$scope.alreadySharedWithUsersName.push({
												'firstName' : item1.firstName,
												'lastName' : item1.lastName
											});
										}
									})
								})
								
								$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
	                               $anchorScroll();
	                               $scope.msg = "Already shared with "+$scope.alreadySharedWithUsersName[0].firstName+' '+$scope.alreadySharedWithUsersName[0].lastName;
	                               $scope.icon = 'report_problem';
	                               $scope.styleclass = 'c-warning';
	                               $scope.showAlertMessage = true;
	                               $timeout(function() {
	                                   $scope.showAlertMessage = false;
	                               }, 2000);
								return
							}*/
							else{
								apiService.insertMultipleShare($scope.solutionId,
										$scope.reqSharedWith).then(
										function(response) {
											if(response.data.error_code == 100){
												/*alert("Shared Successfully");*/
												$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
					                               $anchorScroll();
					                               $scope.msg = "Shared Successfully";
					                               $scope.icon = '';
					                               $scope.styleclass = 'c-success';
					                               $scope.showAlertMessage = true;
					                               $timeout(function() {
					                                   $scope.showAlertMessage = false;
					                               }, 2000);
												$scope.loadShareWithTeam();
												// $scope.selectedGroups = ""
												// $scope.searchText=undefined;
												clear();
											}
											console.log(response);
											clear();
										},
										function(error) {
											alert("Error "
													+ error.data.response_detail);
										});
							}
						}
						
						function clear() {
						      /*self.selectedItem = null;
						      self.searchText = "";*/
						      //$scope.selectedGroups = '';
							$scope.selectedGroups = [];
						      //self.display = "";
						    }
						/** *chips ends** */
						
						/** * shared with team members functionalities ENDS* */
						
						/**** get User image starts****/
						$scope.showAltImage = true;
						$scope.getUserImage = function(){
							var req = {
								    method: 'Get',
								    url: '/api/users/userProfileImage/' + $scope.solution.ownerId
								};
							$http(req).success(function(data, status, headers,config) {
								if(data.status){
								    $scope.userImage = data.response_body;
								    $scope.showAltImage = false;
								}
							}).error(function(data, status, headers, config) {
								
							})
						};
						
						/**** get User image ends****/

						/** ****** Export to local *** */
						$scope.getArtifacts = function() {

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
													if(response.data.response_body[x].artifactTypeCode == "DI"){
														$scope.artifactId = response.data.response_body[x].artifactId;
														$scope.artifactType = response.data.response_body[x].artifactTypeCode;
														$scope.artifactDesc = response.data.response_body[x].description;
														$scope.artifactName = response.data.response_body[x].name;
														$scope.artifactVersion = response.data.response_body[x].version;
														$scope.artifactUri = response.data.response_body[x].uri;
													}
												}

											},
											function errorCallback(response) {
												alert("Error: "
														+ response.status
														+ "Detail: "
														+ response.data.response_detail);
											});

						}

						if (localStorage.getItem("userDetail")) {
							$scope.auth = localStorage
									.getItem("auth_token");
							$scope.loginUserID = JSON.parse(localStorage.getItem("userDetail"))[1];
						}
						$scope.exportToLocal = function(artifactId) {
							if(browserStorageService.getUserDetail()){
								$scope.loginUserId = JSON.parse(browserStorageService.getUserDetail());
							}
							var url = '/api/downloads/'
									+ $scope.solution.solutionId
									+ '?artifactId=' + artifactId
									+ '&revisionId=' + $scope.revisionId
									+ '&userId=' + $scope.loginUserId[1];
							$http({
								method : 'GET',
								url : url,
								responseType : "arraybuffer"
							})
									.success(
											function(data, status, headers,
													config) {
												headers = headers();
												var anchor = angular
														.element('<a/>');
												// FOR IE
												if (navigator.appVersion
														.toString().indexOf(
																'.NET') > 0)
													window.navigator
															.msSaveBlob(
																	new Blob(
																			[ data ],
																			{
																				type : headers['content-type']
																			}),
																	headers['x-filename']);
												else {
													// FOR Chrome and Forefox
													anchor.css({
														display : 'none'
													}); // Make sure it's not
													// visible
													angular.element(
															document.body)
															.append(anchor); // Attach
													// to
													// document
													anchor
															.attr({
																href : window.URL
																		.createObjectURL(new Blob(
																				[ data ],
																				{
																					type : headers['content-type']
																				})),
																target : '_blank',
																download : headers['x-filename']
															})[0].click();
													anchor.remove();
												}
											}).error(
											function(data, status, headers,
													config) {
												alert("Error: " + status
														+ "Detail: " + data);
											});
						}

						/** ****** Export to local ends *** */

						/** ****** Export/Deploy to Azure starts *** */
						$scope.checkboxExport = 'azure';
						$scope.authenticateAnddeployToAzure = function() {
							var imageTagUri = '';
							if ($scope.artifactType != null
									&& $scope.artifactType == 'DI') {
								imageTagUri = $scope.artifactUri;
							}
							/*var authDeployObject = {
								'client' : $scope.applicationId,
								'tenant' : $scope.tenantId,
								'key' : $scope.secretKey,
								'subscriptionKey' : $scope.subscriptionKey,
								'rgName' : $scope.resourceGroup,
								'acrName' : $scope.acrName,
								'storageAccount' : $scope.storageAccount,
								'imagetag' : imageTagUri,
								'solutionId' : $scope.solution.solutionId

							};
							
							if($scope.solution.tookitType != "CP") {
								var url = '/azure/singleImageAsyncDeployment?acrName=' + $scope.acrName + '&client=' + $scope.applicationId + '&imagetag=' + imageTagUri + '&key=' + $scope.secretKey + '&rgName=' + $scope.resourceGroup + '&storageAccount=' + $scope.storageAccount + '&subscriptionKey=' + $scope.subscriptionKey + '&tenant=' + $scope.tenantId;
								$http({
									method : 'GET',
									url : url,
								})
								
							} else {
								var url = '/azure/compositeSolutionAsyncAzure?acrName=' + $scope.acrName + '&client=' + $scope.applicationId + '&solutionId=' + $scope.solution.solutionId +  '&key=' + $scope.secretKey + '&rgName=' + $scope.resourceGroup +  '&storageAccount=' + $scope.storageAccount + '&subscriptionKey=' + $scope.subscriptionKey + '&tenant=' + $scope.tenantId + '&solutionVersion=' + $scope.revisionId;
								$http({
									method : 'GET',
									url : url,
								})
							}*/
							
							 if($scope.solution.tookitType != "CP") {
                                                      var reqObject = '';
                                                      if($scope.checkboxExport == 'azure'){
                                                            var url = '/azure/singleImageAzureDeployment';
                                                            reqObject = {
                                                                        /*'request_body' : {*/
                                                                              'acrName': $scope.acrName,
                                                                              'client': $scope.applicationId,
                                                                              'key': $scope.secretKey,
                                                                              'rgName': $scope.resourceGroup,
                                                                              'solutionId': $scope.solution.solutionId,
                                                                              'solutionRevisionId': $scope.revisionId,
                                                                              'storageAccount': $scope.storageAccount,
                                                                              'subscriptionKey':  $scope.subscriptionKey,
                                                                              'tenant': $scope.tenantId,
                                                                              'userId':  $scope.loginUserId[1],
                                                                              'imagetag': imageTagUri
                                                                        /*}*/
                                                            }
                                                      }
                                                      else if($scope.checkboxExport == 'rackspace'){
                                                            var url =  '/openstack/singleImageOpenstackDeployment';
                                                            reqObject ={
                                                'vmName': $scope.vmName,
                                                'solutionId': $scope.solution.solutionId,
                                                'solutionRevisionId': $scope.revisionId,
                                                'userId':  $scope.loginUserId[1],
                                                'imagetag': imageTagUri
                                                            }
                                                      }
                                                      $http({
                                                            method : 'POST',
                                                            url : url,
                                                            data: reqObject
                                                            
                                                      }).then(function(response) {
                                                            	$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
                            									$anchorScroll(); 							// used to scroll to the id 
                            									$scope.msg = "Deployment Started Successfully"; 
                            									$scope.icon = '';
                            									$scope.styleclass = 'c-success';
                            									$scope.showAlertMessage = true;
                            									$timeout(function() {
                            										$scope.showAlertMessage = false;
                            									}, 5000);
                                                            },
                                                            function(error) {
                                                            	console.warn("Error occured:", error);
                                                            	$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
                            									$anchorScroll(); 							// used to scroll to the id 
                            									$scope.msg = "Deployment Failed: " + error.data.message; 
                            									$scope.icon = 'report_problem';
                            									$scope.styleclass = 'c-error';
                            									$scope.showAlertMessage = true;
                            									$timeout(function() {
                            										$scope.showAlertMessage = false;
                            									}, 5000);

                                                            });
                                                      
                                                } else {
                                                      var reqObject = '';
                                                      if($scope.checkboxExport == 'azure'){
                                                            var url = '/azure/compositeSolutionAzureDeployment';
                                                            reqObject = {
                                                                        /*'request_body' : {*/
                                                                              'acrName': $scope.acrName,
                                                                              'client': $scope.applicationId,
                                                                              'key': $scope.secretKey,
                                                                              'rgName': $scope.resourceGroup,
                                                                              'solutionId': $scope.solution.solutionId,
                                                                              'solutionRevisionId': $scope.revisionId,
                                                                              'storageAccount': $scope.storageAccount,
                                                                              'subscriptionKey':  $scope.subscriptionKey,
                                                                              'tenant': $scope.tenantId,
                                                                              'userId':  $scope.loginUserId[1],
                                                                        /*}*/
                                                            }
                                                      }
                                                      else if($scope.checkboxExport == 'rackspace'){
                                                            var url = "/openstack/compositeSolutionOpenstackDeployment";
                                                            reqObject ={
                                                'vmName': $scope.vmName,
                                                'solutionId': $scope.solution.solutionId,
                                                'solutionRevisionId': $scope.revisionId,
                                                'userId':  $scope.loginUserId[1],
                                                'imagetag': imageTagUri
                                                            }
                                                      }
                                                      $http({
                                                            method : 'POST',
                                                            url : url,
                                                            data: reqObject
                                                      }).then(function(response) {
                                                          	$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
                          									$anchorScroll(); 							// used to scroll to the id 
                          									$scope.msg = "Deployment Started Successfully"; 
                          									$scope.icon = '';
                          									$scope.styleclass = 'c-success';
                          									$scope.showAlertMessage = true;
                          									$timeout(function() {
                          										$scope.showAlertMessage = false;
                          									}, 5000);
                                                      },
                                                      function(error) {
                                                    	  console.warn("Error occured:", error);
                                                        	$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
                        									$anchorScroll(); 							// used to scroll to the id 
                        									$scope.msg = "Deployment Failed: " + error.data.message;
                        									$scope.icon = 'report_problem';
                        									$scope.styleclass = 'c-error';
                        									$scope.showAlertMessage = true;
                        									$timeout(function() {
                        										$scope.showAlertMessage = false;
                        									}, 5000);
                                                      });
                                                }

						}
						
						/*Deploy to Local method*/
						$scope.deployToLocal = function(){
							
						}
						
						//Default values
						$scope.positionM1 = "mime_type";$scope.positionM3 = "image_binary";$scope.positionM2 = 1;$scope.positionM4 = 2;
						$scope.fieldM1 = "mime_type";$scope.fieldM3 = "image_binary";$scope.fieldM4 = "image_binary";$scope.fieldM2 = "mime_type";
						//Deploy to Broker
						$scope.deployCloudVal = function(){
							
							var reqObj;
							if($scope.dbType == 'csv') {
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
	                                      'userId':  $scope.loginUserId[1],
	                                      
	                                      //fields if dbType == 'csv'
	                                      'username': $scope.csvUsername,
	                                      'password': $scope.csvPassword,
	                                      'host': $scope.csvHost,
	                                      'port': $scope.csvPort
	                                      
									};
							}
							else if($scope.dbType == 'zip') {
								var obj1 =  '{'+ $scope.positionM1 + ':' + $scope.positionM2+','+ $scope.positionM3+":" +$scope.positionM4 +'}';
								var obj2 =  '{'+ $scope.fieldM1 + ':' + $scope.fieldM2+','+ $scope.fieldM3+":" +$scope.fieldM4 +'}';
								var obj3 = '{"url":"'+$scope.brokerURL+'"}';
								reqObj = {
										//Zip json attributes
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
                                  'userId':  $scope.loginUserId[1],
                                  //}
									};
							}
							var req = {
									 method: 'POST',
									 url: '/azure/compositeSolutionAzureDeployment',
									 //data: { test: reqObj }
									 data: reqObj
									}

									$http(req).then(
										function(data){
											console.log(angular.toJson(data));
										},
										function(data){
                                                                                	console.log("Error: ", data);
                                                                                });
							
						}
						/** ****** Export/Deploy to Azure ends *** */

						/** file uploading function** */

						var uploader = $scope.uploader = new FileUploader({
							url : 'upload.php'
						});

						/** ***** image upload****** */
						$scope.updateSolImage = function(uploadid) {
							if($scope.icon){
								$scope.solImage = angular.element(document.querySelector('#'+ uploadid))[0].files[0];
							}
							var file = $scope.solImage;
							var fileName = file.name;
							var validFormats = ['jpg','jpeg','png','gif'];
							$scope.solutionImageName = fileName;

							var ext = fileName.split('.').pop();
				            var size = file.size;
				           
				            if(validFormats.indexOf(ext) == -1 && $scope.icon == true){
				            	$scope.error = true;
				                // return value;
				            }else{
				            // validImage(true);
				            $scope.error = false;

							console.clear();
							var uploadUrl = "/api/solution/" + $scope.solution.solutionId + "/updateImage";
							var promise = modelUploadService.uploadFileToUrl(
									file, uploadUrl);

							promise
									.then(
											function(response) {
												console.log(response);
												$scope.showSolutionImage = true;
												$scope.solution = response.response_body;
												$scope.imgURLdefault = "";
											})
											.catch(function() {
												
											});
				            }
						}
						
						$scope.cancelSolImage = function(){
							$scope.imageerror = false;
							$scope.imagetypeerror = false;
							$scope.previewImage = $scope.imgURLdefault;
							$scope.filename = "";
							$scope.solImage = "";
							$scope.selectedIcon = "";
						};
						
						/** ***** File upload****** */
						$scope.updateSolutionFiles = function(uploadid) {

							var file = $scope.solutionFile;

							var uploadUrl = "/api/solution/" + $scope.solution.solutionId + "/revision/" + $scope.revisionId + "/OR/document";
							var promise = modelUploadService.uploadFileToUrl(
									file, uploadUrl);

							promise
									.then(
											function(response) {
												$scope.modelUploadError = false;
												$scope.supportingDocs.push(response.response_body);
												$scope.showSolutionDocs = true;
												$scope.showFileUpload = !$scope.showFileUpload;
												$rootScope.progressBar = 0;
											})
											.catch(function(error) {
												$scope.modelUploadError = true;
												$scope.modelUploadErrorMsg = error;
												$rootScope.progressBar = 0;
												$scope.showFileUpload = !$scope.showFileUpload;
											});
							$scope.privatefilename = "";
						}
						
						$scope.updatePublicSolutionFiles = function(uploadid) {
							//$scope.solutionFile = angular.element(document.querySelector('#'+ uploadid))[0].files[0];
							var file = $scope.solutionFile;
							
							var uploadUrl = "/api/solution/" + $scope.solution.solutionId + "/revision/" + $scope.revisionId + "/PB/document";
							var promise = modelUploadService.uploadFileToUrl(
									file, uploadUrl);

							promise
									.then(
											function(response) {
												$scope.modelUploadErrorPublic = false;
												$scope.supportingPublicDocs.push(response.response_body);
												$scope.showPublicSolutionDocs = true;
												$rootScope.progressBar = 0;
												$scope.showFileUpload = !$scope.showFileUpload;
											})
											.catch(function(error) {
												$scope.modelUploadErrorPublic = true;
												$scope.modelUploadErrorMsgPublic = error;
												$rootScope.progressBar = 0;
												$scope.showFileUpload = !$scope.showFileUpload;
											});
							$scope.publicfilename = "";
						}
						
						
						$scope.getSolutionImages = function(){
	                       	 /*var getSolutionImagesReq = {
										method : 'GET',
										url : '/site/api-manual/Solution/solutionImages/'+$scope.solutionId
								};

	                       	 $http(getSolutionImagesReq)
									.success(
											function(data, status, headers,
													config) {
												if(data.response_body.length > 0) {
													$scope.showSolutionImage = true;
													$scope.solutionImageName = data[0];
													$scope.imgURLdefault = "/site/binaries/content/gallery/acumoscms/solution/" + $scope.solutionId + "/" + data.response_body[0];

													$scope.previewImage = $scope.imgURLdefault;
												
												}
											}).error(
													function(data, status, headers,
															config) {
														$scope.showSolutionImage = false;
													});*/
							}
							$scope.getSolutionImages();
						
							$scope.getCompanySolutionDocuments = function(){
		                       	 var getSolutionDocumentsReq = {
											method : 'GET',
											url : '/api/solution/'+$scope.solutionId + "/revision/" + $scope.revisionId + "/OR/document"
									};
		                       	 $http(getSolutionDocumentsReq)
										.success(
												function(data, status, headers,
														config) {
													$scope.supportingDocs = [];
													if(data.response_body.length > 0)
													    $scope.showSolutionDocs = true;
													console.log("Ger Solution Supporting Docs : " + angular.toJson(data.response_body.name))
													$scope.supportingDocs = data.response_body;
												}).error(
														function(data, status, headers,
																config) {
															return "No Contents Available"
														});
								}							
								
								$scope.getPublicSolutionDocuments = function(){
			                       	 var getSolutionDocumentsReq = {
												method : 'GET',
												url : '/api/solution/'+$scope.solutionId + "/revision/" + $scope.revisionId + "/PB/document"
										};
			                       	 $http(getSolutionDocumentsReq)
											.success(
													function(data, status, headers,
															config) {
														$scope.supportingPublicDocs = [];
														if(data.response_body.length > 0)
														$scope.showPublicSolutionDocs = true;
														$scope.supportingPublicDocs = data.response_body;
													}).error(
															function(data, status, headers,
																	config) {
																return "No Contents Available"
															});
									}
									//$scope.getPublicSolutionDocuments();
								
								$scope.removeDoc = function(doc, path){
									var removeSolutionDocumentsReq = {
											method : 'DELETE',
											url : '/api/solution/'+$scope.solutionId  + "/revision/" + $scope.revisionId +  "/" + path + "/document/" + doc
									};
		                       	 $http(removeSolutionDocumentsReq)
										.success(
												function(data, status, headers,
														config) {
													console.log("Remove Solution Supporting Docs : " + angular.toJson(data.response_body))
													$scope.supportingPulicDocs = [];
													$scope.showPublicSolutionDocs = false;
													
													$scope.supportingDocs = [];
													$scope.showSolutionDocs = false;
													$scope.getCompanySolutionDocuments();
													$scope.getPublicSolutionDocuments();
													 
												}).error(
														function(data, status, headers,
																config) {
															$scope.getCompanySolutionDocuments();
															$scope.getPublicSolutionDocuments();
															return false;
														});
								}
								
								$scope.copyDocsFromOtherRevision = function(path){
									var copySolutionDocumentsReq = {
											method : 'GET',
											url : '/api/solution/'+$scope.solutionId  + "/revision/" + $scope.revisionId +  "/" + path + "/copyDocuments/" + $scope.sourceRevisionId.revisionId
									};
									$http(copySolutionDocumentsReq)
									.success(
											function(data, status, headers,
													config) {
												$scope.getPublicSolutionDocuments();
												$scope.getCompanySolutionDocuments();
											}).error(
													function(data, status, headers,
															config) {
														return "No Contents Available"
											});
								}

						// FILTERS

						// a sync filter
						uploader.filters.push({
							name : 'syncFilter',
							fn : function(item /* {File|FileLikeObject} */,
									options) {
								console.log('syncFilter');
								return this.queue.length < 10;
							}
						});

						// an async filter
						uploader.filters.push({
							name : 'asyncFilter',
							fn : function(item /* {File|FileLikeObject} */,
									options, deferred) {
								console.log('asyncFilter');
								setTimeout(deferred.resolve, 1e3);
							}
						});

						// CALLBACKS

						uploader.onWhenAddingFileFailed = function(
								item /* {File|FileLikeObject} */, filter,
								options) {
							console.info('onWhenAddingFileFailed', item,
									filter, options);
						};
						uploader.onAfterAddingFile = function(fileItem) {
							console.info('onAfterAddingFile', fileItem);
						};
						uploader.onAfterAddingAll = function(addedFileItems) {
							console.info('onAfterAddingAll', addedFileItems);
						};
						uploader.onBeforeUploadItem = function(item) {
							console.info('onBeforeUploadItem', item);
						};
						uploader.onProgressItem = function(fileItem, progress) {
							console.info('onProgressItem', fileItem, progress);
						};
						uploader.onProgressAll = function(progress) {
							console.info('onProgressAll', progress);
						};
						uploader.onSuccessItem = function(fileItem, response,
								status, headers) {
							console.info('onSuccessItem', fileItem, response,
									status, headers);
						};
						uploader.onErrorItem = function(fileItem, response,
								status, headers) {
							console.info('onErrorItem', fileItem, response,
									status, headers);
						};
						uploader.onCancelItem = function(fileItem, response,
								status, headers) {
							console.info('onCancelItem', fileItem, response,
									status, headers);
						};
						uploader.onCompleteItem = function(fileItem, response,
								status, headers) {
							console.info('onCompleteItem', fileItem, response,
									status, headers);
						};
						uploader.onCompleteAll = function() {
							console.info('onCompleteAll');
						};

						console.info('uploader', uploader);

						/** file uploading function ends ** */

						var count = 1;
						$scope.isBusy = false;
						var check = 0;
						var dataObj = {};
						var toBeSearch = '';
						$scope.mlsolutions = [];
						$scope.pageNumber = 0;
						$scope.modelCount = 0;
						var duplicate = false;
						$scope.loadMLSolution = function() {

							if ($scope.isBusy)
								return;
							else
								$scope.isBusy = true;
							$scope.dataLoading = true;
							toBeSearch = $scope.searchBox;
							if ($scope.viewNoMLsolution == 'No More ML Solutions'
									&& $scope.pageNumber != 0) {
								return;
							}
							$scope.MlSoltionCount = false;
							dataObj = {
								"request_body" : {
									"modelType" : $scope.categoryFilter,
									"page" : $scope.pageNumber,
									"searchTerm" : toBeSearch,
									"sortingOrder" : "ASC",
									"size" : 9
								},
								"request_from" : "string",
								"request_id" : "string"
							}
							console.log(">>>>>", angular.toJson(dataObj));
							apiService
									.insertSolutionDetail(dataObj)
									.then(
											function(response) {
												angular
														.forEach(
																response.data.response_body.content,
																function(value,
																		key) {
																	if (response.data.response_body.content[key].active) {
																		$scope.modelCount = $scope.modelCount + 1;
																	}
																});
												$scope.isBusy = false;
												$scope.MlSoltionCount = true;
												if ($scope.pageNumber == 0) {
													$scope.mlsolutions = response.data.response_body.content
												}
												if (response.data.response_body.content.length == 9) {
													$scope.viewNoMLsolution = 'View More ML Solutions';
												} else {
													$scope.viewNoMLsolution = 'No More ML Solutions';
												}
												$scope.tags = response.data.response_body.allTagsSet;
												$scope.dataLoading = false;
												if (response.data.response_body.content.length >= 0) {
													for (var i = 0; i < response.data.response_body.content.length; i++) {
														duplicate = false;
														angular
																.forEach(
																		$scope.mlsolutions,
																		function(
																				value,
																				key) {
																			if (value.solutionId === response.data.response_body.content[i].solutionId) {
																				duplicate = true;
																			}
																		});
														if (!duplicate) {
															$scope.mlsolutions
																	.push({
																		solutionId : response.data.response_body.content[i].solutionId,
																		solution_name : response.data.response_body.content[i].name,
																		marketplace_sol_id : response.data.response_body.content[i].marketplace_sol_id,
																		name : response.data.response_body.content[i].name,
																		ownerId : response.data.response_body.content[i].ownerId,
																		created : response.data.response_body.content[i].created,
																		active : response.data.response_body.content[i].active,
																		description : response.data.response_body.content[i].description,
																		viewCount : response.data.response_body.content[i].viewCount,
																		downloadCount : response.data.response_body.content[i].downloadCount,
																		solutionRating : response.data.response_body.content[i].solutionRating,
																		ownerName : response.data.response_body.content[i].ownerName
																	});
														}
													}

												}
												if (response.data.response_body.content.length == 9) {
													$scope.pageNumber += 1;
												}
												$scope.isBusy = false;
												$scope.searchResults = false;
												if (dataObj.request_body.searchTerm
														&& dataObj.request_body.searchTerm !== ''
														&& dataObj.request_body.searchTerm !== undefined) {
													$scope.searchResults = true;
												}
												$scope.allDeletedData = true;
												angular
														.forEach(
																$scope.mlsolutions,
																function(value,
																		key) {

																	if (value.active === true) {
																		$scope.allDeletedData = false;
																	}

																});
											},
											function(error) {
												$scope.status = 'Unable to load data: '
														+ error.data.error;
												console.log($scope.status);
											});
							count += 9;

						}
						$scope.loadMLSolution();

						
					/******Model Validataion status*****/
					$scope.getModelValidation = function(flow){

						$scope.completedSteps = [];
						if(flow == 'public'){
							$scope.idTab = '#public-market';
						} else {
							$scope.idTab = '#company-market';
						}
						
						var clearInterval = $interval(function(){

							apiService.getMessagingStatus($scope.loginUserId[1], $scope.trackId).then(
									function(response) {
								
								var data = response.data.response_body;
								var counter = 0;
		
								for(var i=0 ; i< data.length; i++){
									var stepName = data[i].name;
									var statusCode =  data[i].statusCode;
									var hideStep = false;
									switch(stepName){
										case 'SecurityScan': {
												if($scope.scShow == true ){
													counter = 2; ( statusCode == 'FA' ) ?  $scope.errorSS = data[i].result : $scope.errorSS = ''; break; 
												} else {
													hideStep = true;
												}
											}
										case 'LicenseCheck' : {
											if($scope.lcShow == true ){
												counter = 4; 
												( statusCode == 'FA' ) ?  $scope.errorLC = data[i].result : $scope.errorLC = ''; break;
											}else {
												hideStep = true;
											}
										}
										case 'TextCheck' :  {
											if($scope.tcShow == true ){
												counter = 6; 
												( statusCode == 'FA' ) ?  $scope.errorTC = data[i].result : $scope.errorTC = ''; break;
											}else {
												hideStep = true;
											}
										}
									}
									if(!hideStep){
										angular.element(angular.element($scope.idTab + ' li div')[counter]).removeClass('completed incomplet active');
										if(statusCode == 'FA'){
											angular.element(angular.element($scope.idTab + ' li div')[counter]).addClass('incomplet');
											angular.element(angular.element($scope.idTab + ' li')[counter+1]).removeClass('green completed');
										}else if(statusCode == 'ST'){
											angular.element(angular.element($scope.idTab + ' li div')[counter]).addClass('active');
											angular.element(angular.element($scope.idTab + ' li')[counter+1]).addClass('progress-status green');
											
										} else if(statusCode == 'SU'){
											angular.element(angular.element($scope.idTab + ' li div')[counter]).addClass('completed');
											angular.element(angular.element($scope.idTab + ' li')[counter+1]).addClass('green completed');
											$scope.completedSteps[stepName] = stepName;
										}
									}
								}											
							});
						
							var allStepsCount = Object.keys($scope.completedSteps);
							if($scope.completedSteps && $scope.Workflow && allStepsCount.length == (3-$scope.Workflow.ignore_list.length) ){
								$interval.cancel(clearInterval);
								angular.element(angular.element($scope.idTab + ' li div')[8]).addClass('completed');
							}
							
						}, 5000);
							
					}
					
					//redirect to manage model page after all validation is completed and redirect using View Model button.
					$scope.viewModel = function(){
						$state.go('manageModule');
					}
					
					//Drag Drop for image icon
					
					$scope.dropCallback = function(event, ui) {
						$scope.previewImage = $scope.draggedTitle;
						$scope.icon = true;
						/*srcToFile($scope.draggedTitle, 'new.png', 'image/png')
						.then(function(file){
						    var fd = new FormData();
						    fd.append('file1', file);
						    $scope.solImage = file;
						});*/
					    //console.log('hey, you dumped me :-(' , $scope.iconFile);
					  };
					  
					$scope.startCallback = function(event, iconImage) {
						    console.log('You started draggin: ' + event.currentTarget.src);
						    $scope.draggedTitle = event.currentTarget.src;
						    $scope.previewImage = $scope.draggedTitle;
						    $scope.icon = false;
						    $scope.solImage = "";
						    srcToFile($scope.draggedTitle, $scope.draggedTitle.split('/').pop(), 'image/png')
							.then(function(file){
							    var fd = new FormData();
							    fd.append('file1', file);
							    $scope.solImage = file;
							});
					};  
					
					$scope.selectIcon = function(iconImage) {
                        $scope.previewImage = 'images/solutions/'+ iconImage +'.png';
                        $scope.selectedIcon = iconImage;
                        $scope.icon = false;
                        $scope.filename = iconImage +'.png';
                        srcToFile($scope.previewImage, iconImage + '.png', 'image/png')
                        .then(function(file){
                            var fd = new FormData();
                            fd.append('file1', file);
                            $scope.solImage = file;
                            //$scope.filename = $scope.solImage.name +".png";
                        });
                        
                        
                }; 
				
				//load src and convert to a File instance object
				function srcToFile(src, fileName, mimeType){
				    return (fetch(src)
				        .then(function(res){return res.arrayBuffer();})
				        .then(function(buf){return new File([buf], fileName, {type:mimeType});})
				    );
				}
				

					//check the count of success
					$scope.statusCount = 0;
					function chkCount(){
						var count = 0, Pbcount = 0, Orcount = 0;
						if($scope.solution){
							if($scope.solution.name)count++;
							if($scope.solution.modelTypeName && $scope.solution.tookitTypeName)count++;
						}
						if($scope.supportingDocs.length > 0){
							Orcount++;
						}
						if($scope.supportingPublicDocs.length > 0){
							Pbcount++;
							
						}
						
						if($scope.tags1.length > 0){
							count++;
						}else if($scope.tags1.length < 1){
							count -1;
						}
						
						if($scope.showSolutionImage && (  $scope.solImage || ($scope.imgURLdefault != 'images/default-model.png' && $scope.imgURLdefault != 'images/img-list-item.png'))){
							count++;
							console.log(">>>>>>> imgURLdefault: ",$scope.imgURLdefault)
						}

						if($scope.company){
							if($scope.company.skipStep == true){
								Orcount++;
							}
						}

						if($scope.public){
							if($scope.public.skipStep == true){
								Pbcount++;
							}
						}
						
						
						if($scope.solutionCompanyDesc)Orcount++;
						if($scope.solutionPublicDesc)Pbcount++;
						$scope.statusCount = count + Orcount;
						$scope.pbstatusCount = count + Pbcount;
						if($scope.statusCount > 5){
							$scope.activePublishBtn = true;
							$scope.modelDocumentation = true;
						}	
						if($scope.pbstatusCount > 5){
							$scope.activePublishBtnPB = true;
							$scope.modelDocumentation = true;
						}
					}
					
					$scope.$watch('solution.name', function() {chkCount();});
					$scope.$watch('solutionCompanyDesc', function() {chkCount();});
					$scope.$watch('solutionPublicDesc', function() {chkCount();});
					$scope.$watch('solution.modelTypeName', function() {chkCount();});
					$scope.$watch('solution.tookitTypeName', function() {chkCount();});
					$scope.$watch('supportingDocs', function() {chkCount();});
					$scope.$watch('supportingPublicDocs', function() {chkCount();});
					$scope.$watch('tags1', function() {chkCount();});
					//$scope.$watch('tags', function() {chkCount();});
					$scope.$watch('solImage', function() {
						chkCount();
						});
					$scope.$watch('imgURLdefault', function() {
						chkCount();
						});
					
					$scope.$watch('file', function() {chkCount();});
					$scope.$watch('user', function() {chkCount();});
					$scope.$watch('popupAddSubmit', function() {chkCount();});
					$scope.$watch('solutionFile', function() {
						if($scope.solutionFile) {
							$scope.publicfilename = $scope.solutionFile.name;
							$scope.privatefilename = $scope.solutionFile.name;
						}
					});
					
					$scope.skipStep = function(){
						if($scope.company){
							if($scope.company.skipStep == true){
								$scope.company.step4 = true
								chkCount();
							}
						}else{
							//empty else required
						} 
						if($scope.public){
							if($scope.public.skipStep == true){
								$scope.public.step4 = true
								chkCount();
							}
							
						}else{ 
							//empty else required
						}
					}
					
	                function build_url(verb, params) {
		                var options = Object.assign({
		                	base:"dsce/dsce/",
		                    //base: urlBase,
		                	//base: 'http://localhost:8088/dsce/',
		                    protobuf: 'artifact/fetchProtoBufJSON'
		                }, querystring.parse());
		                
	                    return options.base + verb + '?' + Object.keys(params).map(function(k) {
	                        return k + '=' + encodeURIComponent(params[k]);
	                    }).join('&');
	                }
	                
	                $scope.closeOtherPopovers = function(variableName, variableValue){
	                	$scope.variableName = variableName;
	                	$scope.variableValue = variableValue;
	                	if($scope.variableName == 'showSolName' && $scope.variableValue == true){
	                		$scope.showDCKEditor = $scope.showSolCat = $scope.showCKEditor = $scope.showFileUpload = $scope.showImageUpload = false;
	                	}else if( $scope.variableName == 'showDCKEditor' && $scope.variableValue == true ){
	                		$scope.showSolName = $scope.showSolCat = $scope.showCKEditor = $scope.showFileUpload = $scope.showImageUpload = false;
	                	}else if( $scope.variableName == 'showSolCat' && $scope.variableValue == true ){
	                		$scope.showSolName = $scope.showDCKEditor = $scope.showCKEditor = $scope.showFileUpload = $scope.showImageUpload = false;
	                	}else if( $scope.variableName == 'showFileUpload' && $scope.variableValue == true ){
	                		$scope.showSolName = $scope.showDCKEditor = $scope.showCKEditor = $scope.showSolCat = $scope.showImageUpload = false;
	                	}else if( $scope.variableName == 'showImageUpload' && $scope.variableValue == true ){
	                		$scope.showSolName = $scope.showDCKEditor = $scope.showCKEditor = $scope.showSolCat = $scope.showFileUpload = false;
	                	}else if( $scope.variableName == 'showCKEditor' && $scope.variableValue == true ){
	                		$scope.showSolName = $scope.showDCKEditor = $scope.showSolCat = $scope.showFileUpload = $scope.showImageUpload = false;
	                	}
	                };
					
					//WorkFlow Validation
	                $scope.workFLowValidation = function(flow){
	                	$scope.tcShow = true;
	                	$scope.lcShow = true;
	                	$scope.scShow = true;

	                	//** added this to restrict the tag length to 32 characters
						angular.element('tags-input input')[0].setAttribute("maxlength", "32");
						angular.element('tags-input input')[1].setAttribute("maxlength", "32");
	                	
	                	var flowConfigKey = ""
	                	if(flow == "Company"){
	                		flowConfigKey = "local_validation_workflow";
	                	}else if(flow == "Public"){
	                		flowConfigKey = "public_validation_workflow";
	                	}
		                	 apiService
		 	    			.getSiteConfig(flowConfigKey)
		 	    			.then(
		 	    					function(response) {
		 	    						$scope.Workflow = angular.fromJson(response.data.response_body.configValue);
		 	    						angular
		 	    		                  .forEach(
		 	    		                          $scope.Workflow.ignore_list,
		 	    		                          function( value, key) {
		 	    		                        	 if(value == "Text Check" ){
		 	    		                        		 $scope.tcShow = false;
		 	    		                        	 }if(value == "License Check"){
		 	    		                        		$scope.lcShow = false;
		 	    		                        	 }if(value == "Security Scan"){
		 	    		                        		$scope.scShow = false;
		 	    		                        	 }
		 	    		                          });
		 	    					},
		 	    					function(error) {console.log(error);
		 	    			});
	                	
	                };
	               
					$scope.validationEnabled = false;
					
                    $scope.getValidationStatus = function(){

                   		apiService.getValidationstatus().then( function(response){
                 			$scope.validationEnabled = response.data;             			
                 		});
                     }  
               		
                     $scope.getValidationStatus(); 
	                	
					/***** pre populated images for demo purpose according to the name of solution*****/
					$scope.imgURLCL = "images/alarm.png";
					$scope.imgURLRG = "images/image-classifier.png";
					$scope.imgURLDT = "images/anomaly.png";
					$scope.imgURLPR = "images/topology.png";
					$scope.imgURLnull = "images/vmpredict2.png";
					
					$scope.imgURLcommercial = "images/commercial_pixelate.jpg";
					$scope.imgURLemotion ="images/emotion_classifier.png";
					$scope.imgURLthreat ="images/threat_analytics.png";
					$scope.imgURLvideo ="images/video_analytics.png";
					$scope.imgURLChat = "images/ChatBot.png";
					$scope.imgURLSensitive = "images/Sensitive.png";
					$scope.imgURLdefault ="images/img-list-item.png";
						
				    $scope.$on('$destroy', function () {
				    	angular.element($scope.idTab + ' li div').removeClass('completed incomplet active');
				    	angular.element($scope.idTab + ' li').removeClass('green completed');
		            });
				    
					$scope.closeUploadPopup = function(){
						if (($scope.privatefilename || $scope.publicfilename) && $rootScope.progressBar < 85){
							modelUploadService.cancelUpload("Upload cancelled by user");
						} else {
							$scope.showFileUpload = !$scope.showFileUpload;
						} 
						$scope.privatefilename = '';
						$scope.publicfilename = '';
						$scope.solutionFile = '';						
						$rootScope.progressBar = 0;
						$scope.docerror = false;
					};
					
					$scope.closeDialog = function() {
						$mdDialog.cancel();
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
				    
				    
				    $scope.showWithdrawRequestModal = function(){
		        	  $mdDialog.show({
		        		  contentElement: '#withdrawRequestModal',
		        		  parent: angular.element(document.body),
		        		  clickOutsideToClose: true
		        	  });
		        	  $scope.withdrawRequestForm.$setUntouched();
		              $scope.withdrawRequestForm.$setPristine();
		            }
				    
				    $scope.withdrawPublishRequest = function(){
				    	var withdrawPublishRequestUrl = "api/publish/request/withdraw/" + $scope.publishRequest.publishRequestId ;
						$http(
								{
									method : 'PUT',
									url : withdrawPublishRequestUrl
								})
								.then(
										function successCallback(response) {
											$scope.publishRequest = response.data.response_body;
											$mdDialog.cancel();
										},function errorCallback(response) {
											//Do nothing
									});
					}
				    
					}

				});
