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
							$stateParams, $sessionStorage, $localStorage, $window,
							$anchorScroll, $timeout, FileUploader, apiService,
							$mdDialog, $filter, modelUploadService, $parse, $document, $mdToast, $state, $interval, $sce, browserStorageService) {
						$scope.hideTabs = $stateParams.deployStatus; 
						if($stateParams.deployFromDS){
							$scope.workflowTitle='Export/Deploy to Cloud';
							$scope.tab='cloud';
						} else if($stateParams.deployStatus){
							$scope.workflowTitle='Share With Team';
							$scope.tab='share';
						} else {
							$scope.workflowTitle='On-Boarding';
							$scope.tab='onboard';
						}
						if($stateParams.deployValue !== null)
							$scope.checkboxExport = $stateParams.deployValue;
						$scope.revisionId = $stateParams.revisionId;
						$scope.status;
						$scope.activePublishBtn = false;
						$scope.showSolutionImage = false;
						$scope.showSolutionDocs = false;
                                                $scope.modelDescriptionError = false;
                                                $scope.modelDescriptionError = false;
						$scope.supportingDocs = [];
						$scope.tags1 = [];
                                                $scope.checkFlag = true;
						componentHandler.upgradeAllRegistered();
						$scope.solutionCompanyDescStatus = false;
						$scope.icon = false;
						$scope.modelDocumentation = false;
						$scope.selectedCatalogObj = { "accessTypeCode" : 'PB' };
						$scope.iconImages = ["CLI","curl", "dotnet","javascript", "java", "go",
											"scala","ruby", "rust", 'REST API',"nodejs", "swift", 
											"python", "R"];
						$scope.previewImage = "images/img-list-item.png";
						$rootScope.progressBar = 0;
						$scope.imageerror = false;
						$scope.imagetypeerror = false;
						$scope.docerror = false;
						$scope.flag = false;
						$scope.mybody = angular.element(document).find('body');
						$scope.selectedItem = null;
						$scope.searchText = null;
					
						$scope.checkPublish  = false;
						
						if ($stateParams.solutionId) {
							$scope.solutionId = $stateParams.solutionId;
							$scope.revisionId = $stateParams.revisionId;
							localStorage.setItem('solutionId',
									$scope.solutionId);
							localStorage.setItem('revisionId',$scope.revisionId);
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
						$scope.revisionId = localStorage.getItem('revisionId');
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
										});
						}

						$scope.loadToolkitType = function() {
							apiService
									.getToolkitTypes()
									.then(
											function(response) {
												$scope.alltoolkitType = response.data.response_body;
											});
						}

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
							$scope.solutionCompanyDesc1 = '';
							$scope.supportingDocs = [];
							$scope.getCompanySolutionDocuments();
							$scope.getSolCompanyDesc();
							$scope.versionId = versionId;
							angular.element('.md-version-ddl1').hide();
							$scope.completedOnDate = modifiedDate;
							$scope.loadData();
						}
						
						$scope.getPublishRequestDetail = function(){

							apiService.searchPublishRequestWithCatalogIds($scope.revisionId, $scope.selectedCatalogId)
									.then(function successCallback(response) {
												$scope.publishRequest = response.data.response_body;
							});
						}
						
						$scope.updateSolutionPictureSources = function() {
							angular.forEach(document.querySelectorAll('img[src*="/picture"], img[src*="default-model.png"'), function(img, key) {
								img.src = "/api/solutions/" + $scope.solution.solutionId + "/picture#" + new Date().getTime();
							});
						};
						
						$scope.getSolutionPicture = function() {
							apiService.getSolutionPicture($scope.solution.solutionId)
							 .then(
									 function(response) {
										 $scope.showSolutionImage = true;
										 $scope.imgURLdefault = " ";
									 });
						};
						
						$scope.loadData = function() {
							$scope.apiUrl;
							angular.element('.md-version-ddl1').hide();
							if($scope.tagUpdated == true){
								$scope.tags1 = $scope.tags1;
							}else{
								$scope.tags1 = [];
							}
							
							if ($scope.solutionId) {
								$scope.solutionId = $scope.solutionId;
							}
							apiService
									.getSolutionDetail($scope.solutionId, $scope.revisionId)
									.then(
											function(response) {
												if (response.data.response_body) {
													$scope.solution = response.data.response_body;
													$scope.versionList = [];
													$scope.categoryname = $scope.solution.modelType;
													$scope.toolkitname = $scope.solution.tookitType;
													$scope.solutionName = $scope.solution.name;
													$scope.popupSolutionId = $scope.solution.solutionId;
													$scope.popupRevisionId = $scope.solution.revisionId;
													if($scope.completedOnDate){
														$scope.solution.created = $scope.completedOnDate;
														$scope.solution.modified = $scope.completedOnDate;
													}
													if ($scope.solution.revisions != null) {
														var counter = 0;
														var length = $scope.solution.revisions.length;													
														$scope.publisherList = [];
														while(counter < length){
															($scope.versionList).push(response.data.response_body.revisions[counter]);
															if(response.data.response_body.revisions[counter].publisher !== null)
																($scope.publisherList).push(response.data.response_body.revisions[counter].publisher);
															counter++;
														}
														($scope.versionList).sort(function(a, b) {
															  // sort version according to created date
															  const genreA = a.onboarded;
															  const genreB = b.onboarded;

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
															$scope.solution.created = $scope.version.modified;
														}
														
														$scope.getArtifacts();
														$scope.getComment();
														$scope.getProtoFile();
														$scope.getLicenseFile();
													}
													$scope.solutionEditorCompanyDesc = $scope.solution.description;
													$scope.isModelActive = $scope.solution.active;

													if ($scope.solution.solutionTagList) {
														for (var i = 0; i < $scope.solution.solutionTagList.length; i++) {
															$scope.tags1.push($scope.solution.solutionTagList[i].tag);
														}
													} else if ($scope.solution.solutionTag) {
														$scope.tags1.push($scope.solution.solutionTag);
													}
													$scope.showSolutionDocs = false;
													$scope.supportingDocs = [];
													
													$scope.getUserImage();													
													$scope.workFLowValidation($scope.selectedCatalogObj.accessTypeCode);
													$scope.getAuthorList();
													$scope.getSolutionPicture();
													$scope.getPublisher();
												} else {
													$scope.msg = "Error Fetching Data ";
													$scope.icon = 'report_problem';
													$scope.styleclass = 'c-error';
													$scope.showAlertMessage = true;
													$timeout(function() {$scope.showAlertMessage = false;}, 200);
												}

											});
							$scope.publishalert = '';
						}
						$scope.loadData();
						$scope.loadCategory();
						$scope.loadToolkitType();
						
						$scope.getProtoFile = function(){
							 $scope.modelSignature = "";
							 var url = 'api/getProtoFile?solutionId='+$scope.solution.solutionId+'&version='+$scope.versionId;
								$http({method : 'GET',url : url})
									.then(function successCallback(response) {
										$scope.modelSignature = response.data;
									});
						}

						$scope.getSolCompanyDesc = function() {
							var req = {
								method : 'GET',
								url : '/api/solution/revision/' + $scope.revisionId  + '/' + $scope.selectedCatalogId  + "/description"
							};
							$http(req)
									.success(
											function(data) {
												$scope.solutionCompanyDesc = data.response_body.description;
												$scope.newDescriptionLength = $scope.solutionCompanyDesc.replace(/<[^>]+>/gm, '');
												
												$scope.ratingCheck($scope.newDescriptionLength);
												
												$scope.solutionCompanyDesc1 = $sce.trustAsHtml(data.response_body.description);
												if($scope.solutionCompanyDesc){
													$scope.solutionCompanyDescStatus = true;
												}
											})
									.error(
											function(data) {
												$scope.solutionCompanyDesc = "";
												$scope.solutionCompanyDesc1 = "";
												$scope.newDescriptionLength = $scope.solutionCompanyDesc;
												$scope.ratingCheck($scope.newDescriptionLength);
												$scope.solutionCompanyDescStatus = false;
											});
						}
									
						  // utility function for publish button check
                                    
                                    var checkAuthorlength=[];
                                    $scope.PublishConditionCheck = function(checkAuthorlength){
                                          
                                          if(checkAuthorlength.length>0){
                                          $scope.checkPublish = false;
                                          
                                    }
                                    else{                                                                         
                                                $scope.checkPublish = true;

                                         }                                                                                   
                                    }			
									
									
									
									
						$scope.getAuthorList = function(tag,ev){
							apiService.getAuthors($scope.solutionId, $scope.revisionId).then(function(response) {																						
								for(var i = 0 ;i<response.data.response_body.length; i++)
									{
									var keyName = "nameContact";
									var keyVal= response.data.response_body[i].name + response.data.response_body[i].contact;									 
										response.data.response_body[i][keyName] = keyVal;
									}
		                                    $scope.AuthorsTag = response.data.response_body;
                                            $scope.PublishConditionCheck($scope.AuthorsTag);
									
							});
						}
							
						
						$scope.deleteflag = false;
						$scope.tagRemoved1 = function(tag,ev){
							$scope.deleteuser = tag.name;
							$scope.deleteflag = true;
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
					    		for(var i = 0 ;i<response.data.response_body.length; i++)
								{
								var keyName = "nameContact";
								var keyVal= response.data.response_body[i].name + response.data.response_body[i].contact;									 
									response.data.response_body[i][keyName] = keyVal;
								}
								$scope.AuthorsTag = response.data.response_body;
					    		if($scope.deleteflag){					    		    						    		  
							    		$scope.msg = " Author deleted successfully";
							    		$scope.icon = '';
							    		$scope.styleclass = 'c-success';									
							    		$scope.showAlertMessage = true;
							    		$scope.cancelAuthor();
							    		$timeout(function() {$scope.showAlertMessage = false;
                                                      if($scope.AuthorsTag.length>0){
                                                           $scope.checkPublish = false;
                                                      }
                                                      else{                                                       
                                                            $scope.PublishConditionCheck($scope.AuthorsTag);
                                                      }}
                                                      , 3000);
					    			}
					    	},
					    	function errorCallback(response) {
					    		$scope.msg = "Error while removing Author";
								$scope.icon = 'report_problem';
								$scope.styleclass = 'c-error';
								$scope.showAlertMessage = true;
								$timeout(function() {$scope.showAlertMessage = false;}, 8000);
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
						    		$scope.Author.Name = "";
							    	$scope.Author.cntinfo= "";
							    	$scope.AddAuthor.cntinfo.$touched = false;
							    	$scope.AddAuthor.Name.$touched = false;							    	
							    	for(var i = 0 ;i<response.data.response_body.length; i++)
									{
									var keyName = "nameContact";
									var keyVal= response.data.response_body[i].name + response.data.response_body[i].contact;									 
										response.data.response_body[i][keyName] = keyVal;
									}
									$scope.AuthorsTag = response.data.response_body;
									
									if($scope.AuthorsTag){
										$scope.checkPublish = false;
									}
									
									
									
									if($scope.flag ){
							    		$scope.msg = " Author updated  successfully";
							    		
									}
							    	else
							    		$scope.msg = " Author added successfully";
							    		$scope.icon = '';
							    		$scope.styleclass = 'c-success';									
							    		$scope.showAlertMessage = true;
							    		$scope.cancelAuthor();
							    		$timeout(function() {$scope.showAlertMessage = false;}, 4000);
						    	},
						    	function errorCallback(response) {						    		
						    		$scope.msg = "Error while adding Author :  " + response.data.response_detail;
									$scope.icon = 'info_outline';
									$scope.styleclass = 'c-error';
									$scope.showAlertMessage = true;
									$timeout(function() {$scope.showAlertMessage = false;}, 8000);
						    	});
						    	}
							}
						  
						  
						  $scope.getPublisher = function(){
							  
							  apiService.getPublisher($scope.solutionId, $scope.revisionId)
					    		.then(function successCallback(response) {
					    			$scope.publisherName = response.data.response_body;
					    		},
					    		function errorCallback(response) {						    		
					    			$scope.publisherName = "Error Fetching Publisher's Name."
					    		});
							  
						  }
						 
						  
						  $scope.setPublisher = function(){						    	
						    	var vart = $scope.AddPublisher.$valid;
						    	if($scope.AddPublisher.$valid) {
						    		var obj = $scope.publisherName;
						    		};
						    		
						    	apiService.addPublisher($scope.solutionId, $scope.revisionId, obj)
						    		.then(function successCallback(response) {
						    			
						    			if(response.data.error_code == 100){
						    				$location.hash('manage-models');
											$anchorScroll();
						    				$scope.msg = response.data.response_detail;
								    		$scope.icon = '';
								    		$scope.styleclass = 'c-success';									
								    		$scope.showAlertMessage = true;
								    		$timeout(function() {$scope.showAlertMessage = false;}, 3000);
						    			}
								    		
						    	},
						    	function errorCallback(response) {						    		
						    		$scope.msg = "Error while adding Publisher :  " + response.data.response_detail;
									$scope.icon = 'report_problem';
									$scope.styleclass = 'c-error';
									$scope.showAlertMessage = true;
									$timeout(function() {$scope.showAlertMessage = false;}, 8000);
						    		});
						    	}
				
						  $scope.updateTag = function(tag){
							  $scope.selectedtagindex = $scope.AuthorsTag.indexOf(tag);							  
							  $scope.flag = true;
							  $scope.Author =[];
							  $scope.Author.Name = tag.name;
							  $scope.Author.cntinfo = tag.contact;
							  $scope.updateName = tag.name;
							  $scope.updateCntInfo = tag.contact;							  
						  }
						  
						  $scope.updateAuthor = function(){	
							  	$scope.isPresent = false;
							  	
						    	if($scope.AddAuthor.$valid) {	
						    	angular.forEach($scope.AuthorsTag, function(value, key){
									       if(value.name + value.contact == $scope.Author.Name + $scope.Author.cntinfo){										    	   
									    	   $scope.isPresent = true;
									    	   return;
									        } 
									      });
						    		
						    		$scope.removeauthor = [];						    		
						    		$scope.removeauthor.name = $scope.updateName;
						    		$scope.removeauthor.contact = $scope.updateCntInfo;
						    		if(!$scope.isPresent){
						    			$scope.deleteAuthor();
						    			$scope.getAuthorList();
						    		}
						    		$scope.setAuthor();						    								    	
								};						    		
						    }
							
						  
						  $scope.cancelAuthor = function(){
							  	$scope.Author.Name = "";
						    	$scope.Author.cntinfo= "";
						    	$scope.AddAuthor.cntinfo.$touched = false;
						    	$scope.AddAuthor.Name.$touched = false;
						    	$scope.flag = false;
						    	$scope.deleteflag = false;
						    	$scope.isPresent = false;
						    	$scope.selectedtagindex = -1;
						  }
						  $scope.closeAuthorPoup = function(){
			                	$mdDialog.hide();
			              		$scope.result = true;
			              		$scope.cancelAuthor();
			              		return false;
			              }	
						  
						$scope.updateSolution = function(type) {
							
							if(type=="modelCategory"){
									if($scope.categoryname&&$scope.toolkitname){
										$scope.pToP = true;
									}else{
										$location.hash('manage-models');
										$anchorScroll();
										$scope.msg = "Please fill out Model Category field.";
										$scope.icon = 'report_problem';
										$scope.styleclass = 'c-warning';
										$scope.showAlertMessage = true;
										$timeout(function() {$scope.showAlertMessage = false;}, 2500);
										return false;
										
									}
							}
							if(type=="modelName"){
								$scope.solution.PrevSolname = $scope.solution.name;
								$scope.solution.name = $scope.solutionName;							
								$scope.solution.solutionId = $scope.popupSolutionId;
							}
							if ($scope.activeFalse == false) {
								$scope.solution.active = $scope.activeFalse;
							}
							var solution = {
									"request_body" : {									
										"active" : $scope.solution.active,
										"created" : $scope.solution.created,									
										"modelType" : $scope.categoryname,									
										"name" : $scope.solution.name,
										"ownerId" : $scope.solution.ownerId,									
										"solutionId" : $scope.solution.solutionId,									
										"tookitType" : $scope.toolkitname
									}
								}
							
							
							apiService
									.updateSolutions(solution)
									.then(
											function(response) {
												$scope.status = response.status;
												$scope.detail = response.data.response_detail;

												$location.hash('manage-models');
												$anchorScroll();
												$scope.loadData();
												$scope.msg = $scope.detail;
												$scope.icon = '';
												$scope.styleclass = 'c-success';
												$scope.showAlertMessage = true;
												$timeout(function() {$scope.showAlertMessage = false;
															if($scope.solution.active == false){
																$state.go('manageModule');
															}}, 3500);
												
												$scope.tagUpdated = true;
												
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
												$timeout(function() {$scope.showAlertMessage = false;}, 2500);
											});
						}
						
						$scope.solutionCompanyDescLength = false;
						$scope.checkCompDescrLength = function()
						{
							if($scope.solutionCompanyDesc){

								$scope.solutionCompanyDescString = $scope.solutionCompanyDesc ? String($scope.solutionCompanyDesc).replace(/<[^>]+>/gm, '') : '';
								$scope.newDescriptionLength = $scope.solutionCompanyDesc.replace(/<[^>]+>/gm, '');
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
							
							if($scope.selectedCatalogId){
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
                                    url : '/api/solution/revision/' + $scope.solutionId + '/' + $scope.revisionId + '/' + $scope.selectedCatalogId  + "/description",
									data : solution
								};
								$http(req)
										.success(
												function(data) {
													$scope.solutionCompanyDesc = data.response_body.description;
													$scope.newDescriptionLength = $scope.solutionCompanyDesc.replace(/<[^>]+>/gm, '');
													
													$scope.ratingCheck($scope.newDescriptionLength);
													
													$scope.loadData();
													$scope.solutionCompanyDescStatus = true;
													$location.hash('manage-models');
													$anchorScroll();
													$scope.getSolCompanyDesc();
													
													$scope.msg = "Solution description updated successfully.";
													$scope.icon = '';
													$scope.styleclass = 'c-success';
													$scope.showAlertMessage = true;
													$timeout(function() {
																$scope.showAlertMessage = false;
																if($scope.solution.active == false){
																	$state.go('manageModule');
																}}, 3500);
												}).error(
												function(data) {
													
													if(data.status == 500){
													
														
														$scope.modelDescriptionError = true;
														$scope.modelDescriptionErrorMsg = data;
														
														
														
													}
													else{
														$scope.solutionCompanyDesc = '';
														$scope.solutionCompanyDescStatus = false;
														$scope.solutionCompanyDescLength = false;
														$scope.newDescriptionLength = $scope.solutionCompanyDesc.replace(/<[^>]+>/gm, '');
													}
													
													
												});
							} else {
								$location.hash('manage-models');
								$anchorScroll();				
								$scope.msg = "Please select a catalog.";
								$scope.styleclass = 'c-error';
								$scope.icon = 'report_problem';
								$scope.showAlertMessage = true;
								$timeout(function() {$scope.showAlertMessage = false;}, 3000);
								$scope.solutionCompanyDesc = '';
								
								$scope.newDescriptionLength = $scope.solutionCompanyDesc.replace(/<[^>]+>/gm, '');
								$scope.showDCKEditor = !$scope.showDCKEditor;
							}
						}

						
						
						
						
						
						
						// functionality for adding ratings begins
			          			
			          		
			          var ratingDescription;
						$scope.ratingCheck = function(ratingDescription){
							
							$scope.ratedescriptioncheck = ratingDescription.length;
																																									
							if($scope.ratedescriptioncheck<500){ 
								$scope.checkFlag = true;
								
							}
								
							
							if($scope.ratedescriptioncheck>500){ 
								$scope.checkFlag = false;
								
							}
						}
												
						$scope.checkMessageButton = function(){
							$scope.checkFlag = false;
						}
																	
						// functionality for adding ratings ends
												
						$scope.copiedCompanyDesc = false;
						$scope.copyPublicToCompany = function() {
							var solution = {
										"request_body": {
										"description" : $scope.solutionPublicDesc
									}
							};
							var req = {
								method : 'POST',
								url : '/api/solution/revision/' + $scope.revisionId + '/' + $scope.selectedCatalogId  + "/description",
								data : solution
							};
							$http(req).success(function(data) {
								$scope.solutionCompanyDesc1 = data.response_body.description;
								$scope.solutionCompanyDesc = data.response_body.description;
								$scope.newDescriptionLength = $scope.solutionCompanyDesc.replace(/<[^>]+>/gm, '');
								$scope.ratingcheck($scope.newDescriptionLength);
								//$scope.loadData();
								$scope.copiedCompanyDesc = true;
							});

						}
						
						
						$scope.isSolutionDescAvailableBtn = false; 				//disable copy desc button untill no desc present.
						$scope.changedDescVersion = function(accessType){
							$scope.isSolutionDescAvailableBtn = false;
							$scope.publicOrOrg = accessType;
							
							$scope.mybody.addClass('waiting'); 
							return apiService
							.getSolutionDescription($scope.selectedCatalogId, $scope.solutionId, $scope.fromRevisionId.revisionId)
									.then(
											function(response) {
												$scope.mybody.removeClass('waiting');
												
												if(response.data.response_body.description != null ){
													$scope.isSolutionDescAvailableBtn = true; 
													$scope.isSolutionDescError = false;
												}else{
													$scope.isSolutionDescAvailableBtn = false;
													$scope.isSolutionDescError = true;
												}
											},function(error) {
												$scope.mybody.removeClass('waiting');
												$scope.isSolutionDescError = true;
											});
						}
						
						$scope.copyFromOtherRevision = function() {
							return apiService
							.getSolutionDescription($scope.selectedCatalogId, $scope.solutionId, $scope.fromRevisionId.revisionId)
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
													url : '/api/solution/revision/' + $scope.solutionId + '/' + $scope.revisionId + '/' + $scope.selectedCatalogId + '/description',
													data : copySolutionDesc
												};
											
											$http(req)
											.success(
													function(data) {
														$scope.solutionCompanyDesc = data.response_body.description;
														$scope.newDescriptionLength = $scope.solutionCompanyDesc.replace(/<[^>]+>/gm, '');
														
														$scope.ratingcheck($scope.newDescriptionLength);
														//$scope.loadData();
														$scope.solutionCompanyDesc1 = data.response_body.description;
														$scope.solutionCompanyDescStatus = true;
														$location.hash('manage-models');
														$anchorScroll();
														$scope.getSolCompanyDesc();
														$scope.msg = "Solution description copied successfully.";
														$scope.icon = '';
														$scope.styleclass = 'c-success';
														$scope.showAlertMessage = true;
														$timeout(function() {
															$scope.showAlertMessage = false;
															if($scope.solution.active == false){
																$state.go('manageModule');
															}}, 3500);
														
													}).error(function (error){
														$scope.solutionCompanyDesc = '';
														$scope.solutionCompanyDescStatus = false;
														$scope.solutionCompanyDescLength = false;
														$scope.newDescriptionLength = '';
														$location.hash('manage-models');
														$anchorScroll();
														$scope.getSolCompanyDesc();
														$scope.msg = "Error occurred while copying descrtiption from other revision.";
														$scope.icon = 'report_problem';
														$scope.styleclass = 'c-error';
														$scope.showAlertMessage = true;
														$timeout(function() {
															$scope.showAlertMessage = false;
															if($scope.solution.active == false){
																$state.go('manageModule');
															}}, 3500);
													});
										} 
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
											});
						}
						$scope.loadAllTags();
						$scope.createTagMethod = false;
						$scope.tagAdded = function(tag) {
							//added this for special character validation
							if($scope.publictags1.tagsAutocomplete.$error.pattern){
								$scope.tags1.pop();
								return;
							}
							
							$scope.isTagExists = false;
							angular.forEach($scope.allTags, function(item, index) {
								if (tag == item) {
									$scope.isTagExists = true; 
								}
							});

							if($scope.isTagExists == true){
								$scope.addTag(tag);
							}else{
								$scope.createTag(tag);
							}
							
						};
						$scope.chipSearch = function(text){
							var firstPass = $filter('filter')($scope.allTags, text);
							if(firstPass.length==0 || firstPass.includes(text)==false){
								firstPass.push(text+" (New Tag)");
							}
							if(firstPass.indexOf(text) != -1){
								firstPass.splice(firstPass.indexOf(text), 1);
								firstPass.unshift(text);
							}
							return firstPass;
						};
						
						$scope.addTag = function(tag){
							apiService.updateAddTag($scope.solution.solutionId,
									tag).then(function(response) {
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

									});
						}
						
						$scope.createTag = function(tag){
							var addtag;
							$scope.tagsTemp = [];
							angular.forEach($scope.tags1, function(item, key) {
								if(item.endsWith('(New Tag)') == true){
									$scope.tagsFoo = tag.replace(' (New Tag)',"");
									$scope.tagsTemp.push($scope.tagsFoo);
								}else{
									$scope.tagsTemp.push(item);
								}
								$scope.tags1 = $scope.tagsTemp;
							});
							if(tag.endsWith('(New Tag)')==true){
								addtag=tag.replace(' (New Tag)',"");
								$scope.allTags.push(addtag);				// to remove New Tag from the scope of allTags until it loads the response
							}else{
								var addtag= tag;
							}
							var dataObj = {
									"request_body": {
										"tag": addtag
									}
							}
							apiService.createTags(dataObj).then(function(response) {

								$scope.status = response.data.response_detail;
								$scope.loadAllTags();

								chkCount();
								var toast = $mdToast.simple()
								.content('Tag Created')
								.position('bottom right')
								.theme('success-toast')
								.hideDelay(2000);
								$mdToast.show(toast);
								$scope.createTagMethod = true;
								$scope.addTag(addtag);

							});

						}					

						$scope.tagRemoved = function(tag) {
							var addtag;
							if(tag.endsWith('(New Tag)')==true){
								addtag=tag.replace(' (New Tag)',"");
								} else {
								var addtag= tag;
							}

							apiService.deleteTag($scope.solution.solutionId,
									addtag).then(function(response) {

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


						$scope.publishtoMarket = function(selectedCatalogId) {
							$scope.closeDialog();							
							if ($scope.solution.ownerId) {
									var data = $.param({
										visibility : $scope.selectedCatalogObj.accessTypeCode,
										userId : $scope.solution.ownerId,
										revisionId : $scope.revisionId,
										ctlg : $scope.selectedCatalogId
									});																														
									apiService.publishSolution($scope.solution.solutionId, data).then(
													function(response) {
																																							
														$scope.handleSuccess = true;
														$timeout(function() {$scope.handleSuccess = false;}, 4500);

														$scope.styleclass = 'c-error';
														if(response.data.error_code == 500){
															$scope.errorMsg = response.data.response_detail;
															$scope.msg = "Solution Not Published";
															$scope.icon = 'report_problem';
															
														}
														
														
														
														else if (response.data.error_code == 100){
															$scope.trackId = response.data.response_detail;
															$scope.msg = "Solution Published Successfully";
															$scope.icon = '';
															$scope.styleclass = 'c-success';
															$scope.modelDocumentation = true;
															$scope.activePublishBtn = false;
															if($scope.selectedCatalogObj.selfPublish){
																$scope.msg = "Solution Published Successfully";
																$scope.getCatalogs();
																$scope.loadData();
																
															} else {
																$scope.msg ="Solution publish request sent for approval";
																$scope.getPublishRequestDetail();
															}	
														}else{
															$scope.styleclass = 'c-error';
															$scope.status = response.data.response_detail;
															$scope.msg = "Unexpected Error Occured";
															$scope.icon = 'report_problem';
														}
														$scope.selectedCatalogId = selectedCatalogId;
														$location.hash('manage-models');
														$anchorScroll();
														$scope.showAlertMessage = true;
														$timeout(function() {$scope.showAlertMessage = false;}, 2500);
														
													},
													function(error) {
														if (error.data.error_code == "sv_info" || error.data.error_code == "sv_error") {
															$mdDialog.show({
																templateUrl : '../app/error-page/sv-modal.template.html',
																clickOutsideToClose : true,
																locals: {
																	reasons: error.data.response_detail,
																	isError: error.data.error_code == "sv_error"
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
                                                                                          $location.hash('manage-models');
                                                                                           $scope.msg = error.data.response_detail;
                                                                                          $scope.icon = 'info_outline';
                                                                                          $scope.styleclass = 'c-error';
                                                                                          $anchorScroll();
                                                                                          $scope.showAlertMessage = true;
                                                                                          $timeout(function() {$scope.showAlertMessage = false;}, 5000);
                                                                                    }
                                                                                    $scope.selectedCatalogId = selectedCatalogId;
                                                                              });                     

                                                }                                         
                                           else {
                                                $scope.status = 'Please sign in as owner  to publish solution';
                                                $location.hash('manage-models');
                                                $anchorScroll();
                                                $scope.msg = "Please sign in as owner to publish solution";
                                                $scope.icon = 'info_outline';
                                                
                                                $scope.styleclass = 'c-info';
                                                $scope.showAlertMessage = true;
                                                $timeout(function() {$scope.showAlertMessage = false;}, 3500);
                                          }
                                          
                                          }
							
							
							
						

						/** * shared with team members functionalities START* */

						$scope.getAllUsersList = function() {
							return apiService
									.getAllUsersLists()
									.then(function(response) {
										$scope.allUserDetails = response.data.response_body;
									});

						}
						$scope.getAllUsersList();

						$scope.loadShareWithTeam = function() {
							apiService
									.getShareWithTeam($scope.solutionId)
									.then(
											function(response) {
												if (response.data.error_code == "500") {
													$scope.sharedWith = '';
												} else {
													$scope.sharedWith = response.data.response_body.userList;
												}
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
													$timeout(function() {$scope.showAlertMessage = false;}, 3500);

													$scope.asyncSelected = '';
												} else {
													$scope.asyncSelected = '';
													$scope.loadShareWithTeam();
													$scope.msg = "Solution shared with "
															+ $scope.sharedWithUserName;
													$scope.icon = '';
													$scope.styleclass = 'c-success';
													$scope.showAlertMessage = true;
													$timeout(function() {$scope.showAlertMessage = false;}, 2000);
												}
												$scope.status = response.data.response_detail;
											});
						}

						
						$scope.sharedWithConfirmation = function(userId, fName, lName){
							$scope.deleteUserId = userId;
							$scope.deleteFName = fName;
							$scope.deleteLName = lName;
					        	  $mdDialog.show({
					        		  contentElement: '#confirmPopupDeleteSharedWith',
					        		  parent: angular.element(document.body),
					        		  clickOutsideToClose: true
					        	  });
					        	  return false;
						}
						
						$scope.closeSharedWithPoup = function(){
		                			$mdDialog.hide();
		              				return false;
	                			}
						
						$scope.removeSharedUser = function(userId, userFname, userLname) {
							
							$scope.shareWithUser = userId;
							$scope.sharedWithUserFName = userFname;
							$scope.sharedWithUserLName = userLname;
							$scope.sharedWithUserName = $scope.sharedWithUserFName+" "+ $scope.sharedWithUserLName;

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
                                                        $timeout(function() {$scope.showAlertMessage = false;}, 3500);
                                                      	$scope.closeSharedWithPoup();
                                                      	$scope.getAllUsersList();
                                                      	$scope.loadShareWithTeam();
													
												} else {
													$scope.closeSharedWithPoup();
												}

											},
											function(error) {
                                                 $scope.closeSharedWithPoup();

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
												$scope.allGroups1 = $scope.allGroups;
												
												angular.forEach($scope.allUserDetails, function(item1, key1) {
													angular.forEach($scope.sharedWith, function(item2, key2) {
														if(item1.userId == item2.userId){
															$scope.allUserDetails.splice(key1, 1);
														}
														
													});
												});
												$scope.allUserDetails.map(function(item) {
													$scope.allGroups.push({
														firstName : item.firstName,
														lastName : item.lastName,
														userEmailId : item.emailId,
														userID : item.userId,
														picture : item.picture
													});
													
												});

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
										'emailId' : item.userEmailId,
										'picture' : item.picture
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
												
												clear();
											}
											console.log(response);
											clear();
										},
										function(error) {
											if (error.data.error_code == "sv_info" || error.data.error_code == "sv_error") {
												$mdDialog.show({
													templateUrl : '../app/error-page/sv-modal.template.html',
													clickOutsideToClose : true,
													locals: {
														reasons: error.data.response_detail,
														isError: error.data.error_code == "sv_error"
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
												alert("Error "
														+ error.data.response_detail);
											}
										});
							}
						}
						
						function clear() {
						      
							$scope.selectedGroups = [];
						     
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
							})
						};
						
						/**** get User image ends****/

						/** ****** Export to local *** */
						$scope.getArtifacts = function() {
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
													if(response.data.response_body[x].artifactTypeCode == "DI"){
														$scope.artifactId = response.data.response_body[x].artifactId;
														$scope.artifactType = response.data.response_body[x].artifactTypeCode;
														$scope.artifactDesc = response.data.response_body[x].description;
														$scope.artifactName = response.data.response_body[x].name;
														$scope.artifactVersion = response.data.response_body[x].version;
														$scope.artifactUri = response.data.response_body[x].uri;													
													}
												}

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
											});
						}

						/** ****** Export to local ends *** */
						
						/***********Deploy to Local method****/
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
						
						$scope.deployLocalPackage = function(){
							$scope.performSVScan($scope.solution.solutionId, $scope.revisionId, "deploy", function() {
								$window.location.assign("/package/getSolutionZip/" + $scope.solution.solutionId + "/" + $scope.revisionId);
							});
						}
						
						apiService.getKubernetesDocUrl().then( function(response){
							$scope.kubernetesDocUrl = response.data.response_body;
						});
						

						/** ****** Export/Deploy to Azure starts *** */
						if($scope.checkboxExport === undefined)
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
                            									$timeout(function() {$scope.showAlertMessage = false;}, 5000);
                                                            },
                                                            function(error) {
                                                            	console.warn("Error occured:", error);
                                                            	$location.hash('manage-models');  // id of a container on the top of the page - where to scroll (top)
                            									$anchorScroll(); 							// used to scroll to the id 
                            									$scope.msg = "Deployment Failed: " + error.data.message; 
                            									$scope.icon = 'report_problem';
                            									$scope.styleclass = 'c-error';
                            									$scope.showAlertMessage = true;
                            									$timeout(function() {$scope.showAlertMessage = false;}, 5000);

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
                        									$timeout(function() {$scope.showAlertMessage = false;}, 5000);
                                                      });
                                                }

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
			 							$scope.icon = 'report_problem';
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
							var validFormats = ['jpg','jpeg','png','gif','JPG','PNG','JPEG','GIF'];
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
							var uploadUrl = "/api/solutions/" + $scope.solution.solutionId + "/picture";
							var promise = modelUploadService.uploadFileToUrl(
									file, uploadUrl);

							promise
									.then(
											function(response) {
												$scope.getSolutionPicture();
												$scope.updateSolutionPictureSources();
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
							if($scope.selectedCatalogId){
								var file = $scope.solutionFile;
	
								var uploadUrl = "/api/solution/" + $scope.solution.solutionId + "/revision/" + $scope.revisionId + '/' + $scope.selectedCatalogId  + "/document";
								var promise = modelUploadService.uploadFileToUrl(file, uploadUrl);
	
								promise
										.then(
												function(response) {
													$scope.modelUploadError = false;
													$scope.supportingDocs.push(response.response_body);
													$scope.showSolutionDocs = true;
													$scope.showFileUpload = !$scope.showFileUpload;
													$rootScope.progressBar = 0;
													chkCount();
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
														$scope.modelUploadErrorMsg = error;
													}
													$rootScope.progressBar = 0;
													$scope.showFileUpload = !$scope.showFileUpload;
												});
								$scope.privatefilename = "";
								
							} else {
									$location.hash('manage-models');
									$anchorScroll();				
									$scope.msg = "Please select a catalog.";
									$scope.styleclass = 'c-error';
									$scope.icon = 'report_problem';
									$scope.showAlertMessage = true;
									$timeout(function() {$scope.showAlertMessage = false;}, 3000);
									$scope.showFileUpload = !$scope.showFileUpload;
								}
						}
						
						
							$scope.getCompanySolutionDocuments = function(){
		                       	 var getSolutionDocumentsReq = {
											method : 'GET',
											url : '/api/solution/'+$scope.solutionId + "/revision/" + $scope.revisionId + '/' + $scope.selectedCatalogId  + "/document"
									};
		                       	 $http(getSolutionDocumentsReq)
										.success(
												function(data) {
													$scope.supportingDocs = [];
													if(data.response_body.length > 0){
														$scope.showSolutionDocs = true;
													}
													
													$scope.supportingDocs = data.response_body;
													
													angular.forEach(data.response_body, function(value, key) {
	                                                    var fileName = value.name;
	                                                    var fileExtension = fileName.split('.').pop();
	                                                    $scope.supportingDocsExt = fileExtension;
                                                    });
													
												});
								}							
															
								$scope.removeDoc = function(doc, path){
									$scope.mybody.addClass('waiting');
									var removeSolutionDocumentsReq = {
											method : 'DELETE',
											url : '/api/solution/'+$scope.solutionId  + "/revision/" + $scope.revisionId +  "/" + $scope.selectedCatalogId + "/document/" + doc
									};
		                       	 	$http(removeSolutionDocumentsReq)
										.success(
												function(data) {
													$scope.mybody.removeClass('waiting');
													$scope.supportingDocs = [];
													$scope.showSolutionDocs = false;
													$scope.getCompanySolutionDocuments();
												});
								}
								
								$scope.copyDocsFromOtherRevision = function(path){
									var copySolutionDocumentsReq = {
											method : 'GET',
											url : '/api/solution/' + $scope.solutionId  + "/revision/" + $scope.revisionId + "/" + $scope.selectedCatalogId + "/copyDocuments/" + $scope.sourceRevisionId.revisionId
									};
									$http(copySolutionDocumentsReq)
									.success(function(data) {
										$scope.getCompanySolutionDocuments();
									});
								}
								
								$scope.isSolutionDocsAvailableBtn = false;

								
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

					
					//redirect to manage model page after all validation is completed and redirect using View Model button.
					$scope.viewModel = function(){
						$state.go('manageModule');
					}
					
					//Drag Drop for image icon
					
					$scope.dropCallback = function(event, ui) {
						$scope.previewImage = $scope.draggedTitle;
						$scope.icon = true;						
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

					function chkCount(){
						var count = 0;
						if($scope.solution){
							if($scope.solution.name)count++;
							if($scope.solution.modelTypeName && $scope.solution.tookitTypeName)count++;
						}
						if($scope.supportingDocs.length > 0){
							count++;
						}
						
						if($scope.tags1.length > 0){
							count++;
						}else if($scope.tags1.length < 1){
							count-1;
						}
						
						if($scope.showSolutionImage && (  $scope.solImage || ($scope.imgURLdefault != 'images/default-model.png' && $scope.imgURLdefault != 'images/img-list-item.png'))){
							count++;
						}

						if($scope.company){
							if($scope.company.skipStep == true){
								count++;
							}else if($scope.company.skipStep == false && $scope.supportingDocs.length == 0){
								count--;
							}
						}
						
						if($scope.solutionCompanyDesc) count++;
						$scope.statusCount = count;
						$scope.activePublishBtn = false;
						if($scope.statusCount > 5){
							$scope.activePublishBtn = true;
							$scope.modelDocumentation = true;
						}

					}
					
					$scope.$watch('solution.name', function() {chkCount();});
					$scope.$watch('solutionCompanyDesc', function() {chkCount();});
					$scope.$watch('solution.modelTypeName', function() {chkCount();});
					$scope.$watch('solution.tookitTypeName', function() {chkCount();});
					$scope.$watch('supportingDocs', function() {chkCount();});
					$scope.$watch('tags1', function() {chkCount();});

					$scope.$watch('solImage', function() {chkCount();});
					$scope.$watch('imgURLdefault', function() {chkCount();});
					$scope.$watch('company.skipStep', function() {chkCount();});

					$scope.$watch('file', function() {chkCount();});
					$scope.$watch('user', function() {chkCount();});
					$scope.$watch('popupAddSubmit', function() {chkCount();});
					$scope.$watch('solutionFile', function() {
						if($scope.solutionFile) {
							$scope.privatefilename = $scope.solutionFile.name;
							chkCount();
						}
					});
					
					$scope.skipStep = function(){
						if($scope.company){
							if($scope.company.skipStep == true){
								$scope.company.step4 = true;
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
						/*angular.element('tags-input input')[0].setAttribute("maxlength", "32");
						angular.element('tags-input input')[1].setAttribute("maxlength", "32");*/
	                	
	                	var flowConfigKey = ""
	                		if(flow.toUpperCase() == "RS"){
	                			flowConfigKey = "local_validation_workflow";
	                		}else if(flow.toUpperCase() == "PB"){
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
						if (($scope.privatefilename || $scope.publicfilename) && ($rootScope.progressBar > 0 && $rootScope.progressBar < 85)){
							modelUploadService.cancelUpload("Upload cancelled by user");
						} else {
							$scope.showFileUpload = !$scope.showFileUpload;
						} 
						
						$scope.privatefilename = '';
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
				        .then(function(response) {
				                    if(response.status == 200){
				                        $scope.checkDeployToCloudResponse = JSON.parse(response.data.response_body);
				                    }
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
										});
					}
				    
					//Delete Model
					$scope.openDeleteConfirmPopup = function(){														
						$mdDialog.show({
			        		  contentElement: '#confirmPopupDeleteModel',
			        		  parent: angular.element(document.body),
			        		  clickOutsideToClose: true
			        	  });
					}
					
					$scope.deleteSolutions = function() {														
							if($scope.categoryname&&$scope.toolkitname)$scope.pToP = true;
							$scope.solution.PrevSolname = $scope.solution.name;
							$scope.solution.name = $scope.solutionName;							
							$scope.solution.solutionId = $scope.popupSolutionId;
							if ($scope.activeFalse == false) {
								$scope.solution.active = $scope.activeFalse;
							}
							var revisionId = null;
							//if($scope.version.accessTypeCode == 'PR')
							if($scope.solution.catalogName == null)
								revisionId = $scope.revisionId;
							var solution = {
								"request_body" : {
									"active" : $scope.solution.active,
									"created" : $scope.solution.created,
									"modelType" : $scope.categoryname,
									"name" : $scope.solution.name,
									"ownerId" : $scope.solution.ownerId,
									"solutionId" : $scope.solution.solutionId,
									"tookitType" : $scope.toolkitname,
									"revisionId" : revisionId
								}
							}							
						 apiService
								.deleteSolution(solution)
								.then(
										function(response) {
											$scope.status = response.status;
											$scope.detail = response.data.response_detail;
											$scope.closePoup();										
											$location.hash('manage-models');
											$anchorScroll();

											$scope.msg = "Model deleted successfully"													
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
											$scope.tagUpdated = true;
											$scope.loadData();																																
										},
										function(response) {
											$scope.closePoup();
											$scope.msg = response.data.response_detail;
											$scope.icon = 'report_problem';
											$scope.styleclass = 'c-warning';
											$scope.showAlertMessage = true;
											$timeout(function() {$scope.showAlertMessage = false;}, 3500);
										}); 
							}
					
					$scope.publishConfirmation = function(type){
						if(type){
			        	  $mdDialog.show({
			        		  contentElement: '#confirmPublishModel',
			        		  parent: angular.element(document.body),
			        		  clickOutsideToClose: true
			        	  });
			        	 
						} else {
							$mdDialog.show({
			        		  contentElement: '#confirmUnpublishModel',
			        		  parent: angular.element(document.body),
			        		  clickOutsideToClose: true
							});
						}
						return false;
					}

					$scope.changeSelectedCatalog = function(catalog){
						if(catalog && catalog.catalogId){
							$scope.selectedCatalogId = catalog.catalogId;
							$scope.selectedCatalog=catalog.name;
							$scope.selectedCatalogObj=catalog;
							$scope.company = {};
							$scope.existingCatalogId = '';
							$scope.showSolutionDocs = false;
							$scope.company.step4 = false;
							$scope.activePublishBtn = false;
							$scope.company.skipStep = false;
							$scope.getPublishRequestDetail();
							$scope.getSolCompanyDesc();
							$scope.getCompanySolutionDocuments();
							
						}
					}

					$scope.changeExistingCatalog = function(){
						if($scope.existingCatalogId){
							$scope.selectedCatalogId = $scope.existingCatalogId; 
							$scope.getPublishRequestDetail();
							$scope.getSolCompanyDesc();
							$scope.showSolutionDocs = false;
							$scope.getCompanySolutionDocuments();
							
						}
					}
					
					$scope.getCatalogs = function(){
						
						var solutionObj = {
								"request_body" : {
									 "fieldToDirectionMap": {"name" : "ASC"},
									    "page": 0,
									    "size": 1000
								}
							}
						
				        apiService
				        .getCatalogsbyUser(solutionObj, $scope.loginUserId[1])
				        .then(
				                function(response) {
				                    if(response.status == 200){				                    	
				                        $scope.catalogsList = response.data.response_body.content;
				                        
				                        apiService
								        .getCatalogsForSolutions($scope.solutionId)
								        .then(
								                function(response) {
								                    if(response.status == 200){
								                    	$scope.catalogsAvailable = [];
								                    	  $scope.solutionCatalogsList = response.data.response_body;
								                    	  if($scope.solutionCatalogsList.length > 0){
									                    	  for(var i=0; i<($scope.catalogsList).length; i++){
									                    		  var catalogFound = false;
									                    		 
									                    		  for(var j=0; j<($scope.solutionCatalogsList).length; j++){
										                    		  if($scope.catalogsList[i].catalogId == $scope.solutionCatalogsList[j].catalogId){
										                    			  catalogFound = true;
										                    		  }
										                    	  }
									                    		  if(!catalogFound)
									                    			  ($scope.catalogsAvailable).push($scope.catalogsList[i]);
									                    	  }
								                    	  } else {
								                    		  $scope.catalogsAvailable = $scope.catalogsList;								                    		  
								                    	  }
								                    	  
								                    	  if($scope.selectedCatalogId) {
								  							$scope.existingCatalogId = $scope.selectedCatalogId;
								  							$scope.selectedCatalogId = '';
								              		      } else {
								              		    	$scope.selectedCatalogId = $scope.catalogsAvailable[0].catalogId;
								                    		$scope.selectedCatalog = $scope.catalogsAvailable[0].name;
								                    		$scope.getSolCompanyDesc();
								                    		$scope.getCompanySolutionDocuments();
								              		      }

							                    		  $scope.getPublishRequestDetail();
								                    }
								                });
				                    }
				                });
				    };
				    
				    $scope.getCatalogs();
				    
				    $scope.unpublish = function(){
				    	$scope.disableSelectedCatalogIds = true;
				    	$scope.closeDialog();
				    				    	
				    	apiService.searchPublishRequestWithCatalogIds($scope.revisionId, $scope.selectedCatalogId)
						.then(function successCallback(response) {
							var data = {
								userId : $scope.solution.ownerId,
								ctlg : $scope.existingCatalogId									
							};
							if(response.data.response_body){
								data.publishRequestId = response.data.response_body.publishRequestId;
							}
							
						    	apiService.unpublishSolution($scope.solutionId, $.param(data))
							        .then(function(response) {
							        	$scope.disableSelectedCatalogIds = false;
							        	if(response.data.error_code == 100){
							        		$scope.msg = "Solution unpublished successfully";
								    		$scope.icon = '';
								    		$scope.styleclass = 'c-success';									
								    		$scope.showAlertMessage = true;
								    		$timeout(function() {$scope.showAlertMessage = false;}, 4000);
								    		$scope.existingCatalogId = '';
								    		$scope.selectedCatalogId = '';
											$scope.getCatalogs();
							        	} else {
							        		$scope.msg = "Error occurred while unpublishing solution";
							        		console.log("Error unpublishing solution,",response);
								    		$scope.icon = '';
								    		$scope.styleclass = 'c-error';									
								    		$scope.showAlertMessage = true;
								    		$timeout(function() {$scope.showAlertMessage = false;}, 4000);
							        	}
							         }, function(error) {
							        		$scope.msg = "Error occurred while unpublishing solution";
							        		console.log("Error unpublishing solution,",error);
								    		$scope.icon = '';
								    		$scope.styleclass = 'c-error';									
								    		$scope.showAlertMessage = true;
								    		$timeout(function() {$scope.showAlertMessage = false;}, 4000);
							         });
							});
					}
				    
				    $scope.getLicenseFile = function() {
						$scope.modelLicense = "";
						$scope.modelLicenseError = "";
						$scope.isLicenseJson = false;
						var url = 'api/getLicenseFile?solutionId='+$scope.solution.solutionId+'&version='+$scope.versionId;
						$http({
								method : 'GET',
								url : url
						}).then(function successCallback(response) {
							console.log(response);
							if (response.data) {
								$scope.modelLicense = response.data;
								$scope.isLicenseJson = angular.isObject($scope.modelLicense);
							} else {
								$scope.modelLicenseError = "No license found";
							}		
						});
				 	}
				    $scope.showazurePopup = function(ev){

				    	$scope.confirmSelection=false;
				    	$mdDialog.show({
				    		contentElement: '#deploy',
				    		parent: angular.element(document.body),
				    		targetEvent: ev,
				    		clickOutsideToClose: true
				    	});

				    } 
				    
					}

				});
