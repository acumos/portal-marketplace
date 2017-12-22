'use strict';
angular
		.module('modelEdit')
		.service('modelUploadService', function($http, $q) {

			this.uploadFileToUrl = function(file, uploadUrl) {
				// FormData, object of key/value pair for form fields and values
				var fileFormData = new FormData();
				fileFormData.append('file', file);

				var deffered = $q.defer();
				$http.post(uploadUrl, fileFormData, {
					transformRequest : angular.identity,
					headers : {
						'Content-Type' : undefined
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
					controller : function($scope, $location, $http,
							$stateParams, $sessionStorage, $localStorage,
							$anchorScroll, $timeout, FileUploader, apiService,
							$mdDialog, $filter, modelUploadService, $parse, $document, $mdToast, $state) {


						$scope.status;
						
						$scope.showSolutionImage = false;
						$scope.showSolutionDocs = false;
						$scope.showPublicSolutionDocs = false;
						$scope.supportingPublicDocs = [];
						$scope.supportingDocs = [];
						componentHandler.upgradeAllRegistered();
						$scope.solutionCompanyDescStatus = false;
						$scope.solutionPublicDescStatus = false;
						$scope.icon = false;
						$scope.iconImages = ["CLI","curl", "dotnet","javascript", "java", "go",
											"scala","ruby", "rust", 'REST API',"nodejs", "swift", 
											"python", "R"];
						$scope.previewImage = "images/Edit_model_default_icon.jpg";

						if ($stateParams.solutionId) {
							$scope.solutionId = $stateParams.solutionId;
							localStorage.setItem('solutionId',
									$scope.solutionId);
						}
						
						if(localStorage.getItem("userDetail")){
							$scope.loginUserId = JSON.parse(localStorage.getItem("userDetail"));
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
												console.warn($scope.status)
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
							$scope.solution.solutionId = solutionId; 
							$scope.revisionId = revisionId;
							$scope.versionId = versionId;
							angular.element('.md-version-ddl1').hide();
							$scope.completedOnDate = modifiedDate;
							$scope.loadData();
						}
						
						$scope.loadData = function() {
							$scope.apiUrl;
							angular.element('.md-version-ddl1').hide();
							$scope.tags = [];
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

															  let comparison = 0;
															  if (genreA < genreB) {
															    comparison = 1;
															  } else if (genreA > genreB) {
															    comparison = -1;
															  }
															  return comparison; }
														);
														
														if( !$scope.revisionId ){
															$scope.revisionId = $scope.versionList[0].revisionId;
															$scope.versionId = $scope.versionList[0].version;
															$scope.solution.created = $scope.versionList[0].modified;
															$scope.solution.modified = $scope.versionList[0].modified;
														}
													}
													$scope.solutionEditorCompanyDesc = $scope.solution.description;
													$scope.isModelActive = $scope.solution.active;
													// $scope.solutionDesc =
													// $scope.solution.description;
													if ($scope.solution.solutionTagList) {
														for (var i = 0; i < $scope.solution.solutionTagList.length; i++) {
															$scope.tags
																	.push({
																		text : $scope.solution.solutionTagList[i].tag
																	});
														}
													} else if ($scope.solution.solutionTag) {
														$scope.tags
																.push({
																	text : $scope.solution.solutionTag
																});

													}
													$scope.showSolutionDocs = false;
													$scope.showPublicSolutionDocs = false;
													$scope.supportingPublicDocs = [];
													$scope.supportingDocs = [];
													$scope.getArtifacts();
													$scope.getUserImage();
													$scope.getModelValidation();
													$scope.getSolCompanyDesc();
													$scope.getSolPublicDesc();
													$scope.getPublicSolutionDocuments();
													$scope.getCompanySolutionDocuments();
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
						

						$scope.getSolCompanyDesc = function() {
							var req = {
								method : 'GET',
								url : '/site/api-manual/Solution/description/org/'
										+ $scope.solutionId + "/" + $scope.revisionId,
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionCompanyDesc = data.description;
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
								url : '/site/api-manual/Solution/description/public/'
										+ $scope.solutionId + "/" + $scope.revisionId,
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionPublicDesc = data.description;
												if($scope.solutionPublicDesc){
													$scope.solutionPublicDescStatus = true;
												}
											}).error(
											function(data, status, headers,
													config) {
											});
						}
						//$scope.getSolPublicDesc();

						$scope.updateSolution = function() {
							if($scope.categoryname&&$scope.toolkitname)$scope.pToP = true;
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
									"accessType" : $scope.solution.accessType,
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

												$scope.loadData();
												$scope.tagUpdated = true;
											},
											function(error) {
												$timeout(function() {
													$scope.handleError = true;
												}, 2000);
												$scope.handleError = false;
												$scope.status = 'Unable to load data: '
														+ error.data.error;
											});
						}

						$scope.updateCompanyDescription = function() {
							// $scope.solutionCompanyDesc =
							// $scope.solutionEditorCompanyDesc;
							var solution = {
								"description" : $scope.solutionCompanyDesc,
								"solutionId" : $scope.solution.solutionId,
								"revisionId" : $scope.revisionId
							};
							var req = {
								method : 'POST',
								url : '/site/api-manual/Solution/description/org',
								data : solution
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionCompanyDesc = data.description;
												$scope.solutionCompanyDescStatus = true;
												$location.hash('manage-models');
												$anchorScroll();

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
												 
												alert(data.error);
												$scope.solutionCompanyDesc = '';
											});
						}

						$scope.updatePublicDescription = function() {
							var solution = {
								"description" : $scope.solutionPublicDesc,
								"solutionId" : $scope.solution.solutionId,
								"revisionId" : $scope.revisionId
							};
							var req = {
								method : 'POST',
								url : '/site/api-manual/Solution/description/public',
								data : solution
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionPublicDesc = data.description;
												$scope.solutionPublicDescStatus = true;
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
												alert(data.error);
												$scope.solutionPublicDesc = '';
											});
						}

						$scope.copyPublicToCompany = function() {
							var solution = {
								"description" : $scope.solutionPublicDesc,
								"solutionId" : $scope.solution.solutionId,
								"revisionId" : $scope.revisionId
							};
							var req = {
								method : 'POST',
								url : '/site/api-manual/Solution/description/org',
								data : solution
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionCompanyDesc = data.description;
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

										if(response.data.description != null ){
											var copySolutionDesc = {
													"description" : response.data.description,
													"solutionId" : $scope.solution.solutionId,
													"revisionId" : $scope.revisionId
												};
											
											var req = {
													method : 'POST',
													url : '/site/api-manual/Solution/description/' + publicOrOrg,
													data : copySolutionDesc
												};
											
											$http(req)
											.success(
													function(data, status, headers,
															config) {
														if('org' == publicOrOrg)
															$scope.solutionCompanyDesc = data.description;
														else 
															$scope.solutionPublicDesc = data.description;
													}).error(
													function(data, status, headers,
															config) {
														alert("Failed to create description");
													});
											
										} else {
											alert("No description Found for Selected Version")
										}
									},
									function(error) {

										$scope.status = error.data.error;
										console.log($scope.status)
									});
						}

						$scope.copyCompanyToPublic = function() {
							var solution = {
								"description" : $scope.solutionCompanyDesc,
								"solutionId" : $scope.solution.solutionId,
								"revisionId" : $scope.revisionId
							};
							var req = {
								method : 'POST',
								url : '/site/api-manual/Solution/description/public',
								data : solution
							};
							
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												$scope.solutionPublicDesc = data.description;
											}).error(
											function(data, status, headers,
													config) {
												alert("Failed to create description");
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
												console.log($scope.status)
											});
						}
						$scope.loadAllTags();

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
								
								/*$location.hash('manage-models');
								$anchorScroll();
								$scope.msg = "Tag Added";
								$scope.icon = '';
								$scope.styleclass = 'c-success';
								$scope.showAlertMessage = true;
								$timeout(function() {
									$scope.showAlertMessage = false;
								}, 2000);*/
								
								 var toast = $mdToast.simple()
							        .content('Tag Added')
							        .position('bottom right')
							        .theme('success-toast')
							        .hideDelay(2000);
							     $mdToast.show(toast);

							}, function(error) {
								console.log("Tag Error: ",error)
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
								
								/*$location.hash('manage-models');
								$anchorScroll();
								$scope.msg = "Tag Added";
								$scope.icon = '';
								$scope.styleclass = 'c-success';
								$scope.showAlertMessage = true;
								$timeout(function() {
									$scope.showAlertMessage = false;
								}, 2000);
								addtag;*/
								
								var toast = $mdToast.simple()
						        .content('Tag Added')
						        .position('bottom right')
						        .theme('success-toast')
						        .hideDelay(2000);
								$mdToast.show(toast);
						     
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

								/*$scope.msg = "Tag Removed";
								$scope.icon = 'info_outline';
								$scope.styleclass = 'c-info';
								$scope.showAlertMessage = true;
								$timeout(function() {
									$scope.showAlertMessage = false;
								}, 2000);*/
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

							if ($scope.currentModelAccess == $scope.solution.accessType) {
								if ($scope.solution.accessType == 'OR') {
									$scope.accessName = 'Company/Organization';
								} else if ($scope.solution.accessType == 'PB') {
									$scope.accessName = 'Public';
								}

								$location.hash('manage-models');
								$anchorScroll();
								$scope.msg = "Solution Already Published in "
										+ $scope.accessName + " marketplace";
								$scope.icon = 'report_problem';
								$scope.styleclass = 'c-warning';
								$scope.showAlertMessage = true;
								$scope.getModelValidation();
								$timeout(function() {
									$scope.showAlertMessage = false;
								}, 2500);
								return


							}

							if ($scope.solution.ownerId) {
								if ($scope.solution.accessType == 'PR'
										|| $scope.solution.accessType == 'OR'  || $scope.solution.accessType == 'PB') {
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
														$scope.handleSuccess = true;
														$timeout(
																function() {
																	$scope.handleSuccess = false;
																}, 4500);
														// scroll to top :
														// scrolltotop
														$location
																.hash('manage-models');
														$anchorScroll();
														$scope.status = response.data.response_detail;
														$scope.msg = "Solution Published Successfully";
														$scope.icon = '';
														$scope.styleclass = 'c-success';
														$scope.showAlertMessage = true;
														$scope.loadData();
														$timeout(
																function() {
																	$scope.showAlertMessage = false;
																}, 2500);

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

						/** * shared with team members functionalities * */

						$scope.getAllUsersList = function() {
							return apiService
									.getAllUsersLists()
									.then(
											function(response) {
												$scope.allUserDetails = response.data.response_body;
												/* return $scope.allUserDetails; */

												return response.data.response_body
														.map(function(item) {
															return item.firstName;
														});

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
												console.warn($scope.status)
											});

						}
						$scope.loadShareWithTeam();

						$scope.shareWithTeam = function(asyncSelected) {
							if (asyncSelected == $scope.solution.ownerName
									.substr(0, $scope.solution.ownerName
											.indexOf(' '))) {
								alert("Cannot share with yourself");
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

												alert("Error "
														+ error.data.response_detail)
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
													alert("User: "
															+ $scope.sharedWithUserName
															+ " removed successfully as a co-autor.");
													$scope.loadShareWithTeam();
												} else {
													alert("Unexpected error occured");
												}

											},
											function(error) {

												alert("Error "
														+ error.data.response_detail)

											});

						}

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

						$scope.exportToLocal = function(artifactId) {
							if(localStorage.getItem("userDetail")){
								$scope.loginUserId = JSON.parse(localStorage.getItem("userDetail"));
							}
							var url = '/api/downloads/'
									+ $scope.solution.solutionId
									+ '?artifactId=' + artifactId
									+ '&revisionId=' + $scope.revisionId
									+ '&userId=' + $scope.loginUserId[1];
							$http({
								method : 'GET',
								url : url,
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

						$scope.authenticateAnddeployToAzure = function() {
							var imageTagUri = '';
							if ($scope.artifactType != null
									&& $scope.artifactType == 'DI') {
								imageTagUri = $scope.artifactUri;
							}

							var authDeployObject = {
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

							apiService
									.authenticateAnddeployToAzure(
											authDeployObject)
									.then(
											function(response) {
												if (response.status == "401"
														|| response.data.error_code == "401") {

													alert("Authorization Failed !!");

												} else {

													alert("Deployed successfully !! ");

												}

											}, function(error) {
												alert('FAILED');
											});
						}

						/** ****** Export/Deploy to Azure ends *** */

						/** file uploading function** */

						var uploader = $scope.uploader = new FileUploader({
							url : 'upload.php'
						});

						/** ***** image upload****** */
						$scope.updateSolImage = function() {
							
							var file = $scope.solImage;
							var fileName = file.name;
							var validFormats = ['jpg','jpeg','png','gif'];
							$scope.solutionImageName = fileName;

							var ext = fileName.split('.').pop();
				            var size = file.size;
				           
				            if(validFormats.indexOf(ext) == -1){
				            	$scope.error = true;
				                // return value;
				            }else{
				            // validImage(true);
				            $scope.error = false;

							console.clear();
							var uploadUrl = "/site/api-manual/Solution/solutionImages/" + $scope.solution.solutionId;
							var promise = modelUploadService.uploadFileToUrl(
									file, uploadUrl);

							promise
									.then(
											function(response) {
												console.log(response);
												$scope.showSolutionImage = true;
												$scope.serverResponse = response.response_body;
												$scope.imgURLdefault = "/site/binaries/content/gallery/acumoscms/solution/"+$scope.solutionId+"/"+response.response_body;
											})
											.catch(function() {
												// $scope.showSolutionImage =
												// true;
												// $scope.solutionImageName =
												// 'An error has occurred';
											});
				            }
						}
						
						/** ***** File upload****** */
						$scope.updateSolutionFiles = function() {
							var file = $scope.solutionFile;

							var uploadUrl = "/site/api-manual/Solution/solutionAssets/" + $scope.solution.solutionId + "/" + $scope.revisionId + "?path=org";
							var promise = modelUploadService.uploadFileToUrl(
									file, uploadUrl);

							promise
									.then(
											function(response) {
												$scope.supportingDocs.push(response.response_body);
												$scope.showSolutionDocs = true;
												// $scope.closePoup();
											})
											.catch(function() {
												alert("Error in uploading the file");
											});
						}
						
						$scope.updatePublicSolutionFiles = function() {
							
							var file = $scope.solutionFile;
							

							var uploadUrl = "/site/api-manual/Solution/solutionAssets/" + $scope.solution.solutionId + "/" + $scope.revisionId + "?path=public";
							var promise = modelUploadService.uploadFileToUrl(
									file, uploadUrl);

							promise
									.then(
											function(response) {
												console.log(response.response_body);
												$scope.supportingPublicDocs.push(response.response_body);
												// $scope.closePoup();
												$scope.showPublicSolutionDocs = true;
											})
											.catch(function() {
												alert("Error in uploading the file");
											});
						}
						
						
						$scope.getSolutionImages = function(){
	                       	 var getSolutionImagesReq = {
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
												}
											}).error(
													function(data, status, headers,
															config) {
														$scope.showSolutionImage = false;
													});
							}
							$scope.getSolutionImages();
						
							$scope.getCompanySolutionDocuments = function(){
		                       	 var getSolutionDocumentsReq = {
											method : 'GET',
											url : '/site/api-manual/Solution/solutionAssets/'+$scope.solutionId + "/" + $scope.revisionId + "?path=org"
									};
		                       	 $http(getSolutionDocumentsReq)
										.success(
												function(data, status, headers,
														config) {
													$scope.supportingDocs = [];
													if(data.response_body.length > 0)
													    $scope.showSolutionDocs = true;
													console.log("Ger Solution Supporting Docs : " + angular.toJson(data.response_body))
													$scope.supportingDocs = data.response_body;
												}).error(
														function(data, status, headers,
																config) {
															return "No Contents Available"
														});
								}
								//$scope.getCompanySolutionDocuments();
								
								
								$scope.getPublicSolutionDocuments = function(){
			                       	 var getSolutionDocumentsReq = {
												method : 'GET',
												url : '/site/api-manual/Solution/solutionAssets/'+$scope.solutionId + "/" + $scope.revisionId + "?path=public"
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
											url : '/site/api-manual/Solution/solutionAssets/'+$scope.solutionId  + "/" + $scope.revisionId +  "/" + doc +"?path="+path
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
															return "No Contents Available"
														});
								}
								
								$scope.copyDocsFromOtherRevision = function(path){
									var copySolutionDocumentsReq = {
											method : 'GET',
											url : '/site/api-manual/Solution/solutionAssets/cp/'+$scope.solutionId  + "/" + $scope.revisionId +  "/" + $scope.sourceRevisionId.revisionId +"?path="+path
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

						/** chips */
						
						/* $scope.sharedUsersChips = function(){ */

						/*
						 * $scope.getMatches = function(searchText) { var
						 * deferred = $q.defer();
						 * 
						 * $timeout(function() { var states =
						 * $scope.getStates.filter(function(state) { return
						 * (state.name.toUpperCase().indexOf(searchText.toUpperCase())
						 * !== -1 ||
						 * state.abbreviation.toUpperCase().indexOf(searchText.toUpperCase())
						 * !== -1); }); deferred.resolve(states); }, 1500);
						 * 
						 * return deferred.promise; }
						 */

						// var allGroups = ['one', 'two', 'three' ];
						var allGroups = [];
						$scope.getUsersChips = function() {
							$scope.uData = [];
							$scope.allUserDetails = [];
							$scope.allGroups1 =[];
							apiService
									.getAllUsersLists()
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
													$scope.allGroups.push(item.firstName);
												});

											}, function(error) {
											});

						}
						$scope.getUsersChips();

						$scope.queryGroups = function(search) {
							var firstPass = $filter('filter')
									($scope.allGroups, search);
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
									if (item1 == item2.firstName) {
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
							
							

							$scope.reqSharedWith = {
								"request_body" : $scope.selectedId
							};
							$scope.reqSharedWith;
							if($scope.sharedWithOwner == true){
								alert("Cannot share the solution with yourself.");
								return
							}
							else{
								apiService.insertMultipleShare($scope.solutionId,
										$scope.reqSharedWith).then(
										function(response) {
											if(response.status == 200){
												alert("Shared Successfully");
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

						$scope.showAltImage = true;
						// get User image
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
						
					/******Model Validataion status*****/
					$scope.getModelValidation = function(){
						apiService.getModelValidationStatus($scope.solutionId,
							$scope.revisionId).then(
							function(response) {
								$scope.validationResponse = response.data.response_body;
								$scope.modelValidationStatus = [];
								if($scope.validationResponse != null){
									angular.forEach($scope.validationResponse.mlModelValidationStepStatus, function(
											item) {
										if(item.validationType == "SS"){					//Security Scan
											$scope.ssValidationStatus = item;
										}else if(item.validationType == "TA"){				//Text Analysis/Check
											$scope.taValidationStatus = item;
											$scope.checkTA = $scope.taValidationStatus.validationStatus;
										}else if (item.validationType == "LC"){				//License Check
											$scope.lcValidationStatus = item;
										}else if (item.validationType == "OQ"){				//Verify Model
											$scope.oqValidationStatus = item;
										}else if (item.validationType == "SP"){				// Submit to Publication
											$scope.spValidationStatus = item;
										}
										 
									});
								}
								
								//calling function after every 10s to check the status 
								//if validation is fetched TA as passed (the last step) stop calling again and again
								if($scope.checkTA == "PS"){
									console.log("calling ends");
								}else{
									$timeout(function() {
										$scope.getModelValidation()
									}, 10000);
								}
							},
							function(error) {
								alert("Error " + error.data.response_detail);
							});
						
						
						
					};
					//$scope.getModelValidation();	
						
					
					//check the count of success
					$scope.statusCount = 0;
					function chkCount(){
						var count = 0, Pbcount = 0, Orcount = 0;
						if($scope.solution){
							if($scope.solution.name)count++;
							if($scope.solution.modelTypeName && $scope.solution.tookitTypeName)count++;
						}
						if($scope.supportingDocs.length > 0)count++;
						if($scope.tags.length > 0)count++;
						if($scope.solImage)count++;
						
						if($scope.solutionCompanyDesc)Orcount++;
						if($scope.solutionPublicDesc)Pbcount++;
						
						$scope.statusCount = count + Orcount;
						$scope.pbstatusCount = count + Pbcount;
						
						
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
						    $scope.icon = false;
						    srcToFile($scope.draggedTitle, $scope.draggedTitle.split('/').pop(), 'image/png')
							.then(function(file){
							    var fd = new FormData();
							    fd.append('file1', file);
							    $scope.solImage = file;
							});
					};  
					
					//load src and convert to a File instance object
					function srcToFile(src, fileName, mimeType){
					    return (fetch(src)
					        .then(function(res){return res.arrayBuffer();})
					        .then(function(buf){return new File([buf], fileName, {type:mimeType});})
					    );
					}
					
					
					$scope.$watch('solution.name', function() {chkCount();});
					$scope.$watch('solutionCompanyDesc', function() {chkCount();});
					$scope.$watch('solutionPublicDes', function() {chkCount();});
					$scope.$watch('solution.modelTypeName', function() {chkCount();});
					$scope.$watch('solution.tookitTypeName', function() {chkCount();});
					$scope.$watch('supportingDocs', function() {chkCount();});
					$scope.$watch('tags', function() {chkCount();});
					$scope.$watch('solImage', function() {chkCount();});
					
					$scope.$watch('file', function() {chkCount();});
					$scope.$watch('user', function() {chkCount();});
					$scope.$watch('popupAddSubmit', function() {chkCount();});
					
					$scope.loadData();
					
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
					$scope.imgURLdefault ="images/default-model.png";
						
						
					}

				});
