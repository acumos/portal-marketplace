'use strict';
 
angular
		.module('manageModule', [ 'infinite-scroll' ])
		.component(
				'manageModule',
				{
					/* For section wise displaying of models template url: md-manage-module.template.html 
					 * For all models displaying together template ur: manage-module.template.html 
					 */
					templateUrl : './app/market_place/md-manage-module.template.html',
					controller : function($scope, $compile, $location, $http, $q,
							$sessionStorage, $localStorage, $rootScope,
							$timeout, $state, apiService) {
						$scope.autoHeight = true;
						$scope.hidePrivate = true;
						$scope.hidePublic = true;
						$scope.hideCompany = true;
						$scope.hideDelete = true;
						$scope.defaultSize = 4;
						$scope.seeAllSelected = false;
						
						if(localStorage.getItem("viewMM")){
						  if(localStorage.getItem("viewMM") == 'false')$scope.Viewtile = false;else $scope.Viewtile = true;
						}else $scope.Viewtile = true;
						
						$scope.$watch('Viewtile', function() {
							   localStorage.setItem("viewMM", $scope.Viewtile);
							});
						
						$rootScope.urlPath = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);
						console.log("manage: "+$rootScope.urlPath)
						if ($rootScope.urlPath == 'manageModule') {
							$scope.parentUrl = false
						} else {
							$scope.parentUrl = true
						}
						
						localStorage.setItem('HeaderNameVar','manageModule');
						$scope.actions = [ 	{name:"Most Liked",value:"ML"}, 
						                	{name:"Fewest Liked",value:"FL"},
						                	{name:"Most Downloaded",value:"MD"},
						                	{name:"Fewest Downloaded",value:"LD"}, 
						                	{name:"Highest Reach",value:"HR"} 
						                ];
						$scope.filterids = [ "001", "002", "003", "004", "005",
								"006" ];
						// Hardcode Privacy filters
						// $scope.privacyCheckBox = [
						// {"key":"PR","value":"Private"},{"key":"PB","value":"Public"}];
						// User ID from localstorage
						// $scope.loginUserID =
						// localStorage.getItem('userDetail');

						if (JSON.parse(localStorage.getItem("userDetail"))) {
							$scope.userDetails = JSON.parse(localStorage
									.getItem("userDetail"));
							$scope.userDetails.userName = $scope.userDetails[0];
							$scope.loginUserID = $scope.userDetails[1];
						}
						/*Start call for Sections*/
						var check = 0;
						var dataObj = {};
						var toBeSearch = '';
						$scope.pageNumber = 0;
						$scope.categoryFilter = '';
						$scope.privacyFilter = '';
						$scope.modelCount = 0;
						var dataObjPrivate = {};
						var dataObjPublic = {};
						var dataObjCompany = {};
						var dataObjDelete = {};
						$scope.pageNumPrivate = 0;
						$scope.pageNumPublic = 0;
						$scope.pageNumCompany = 0;
						$scope.pageNumDelete = 0;
						$scope.mlSolutionPrivate=[];
						$scope.mlSolutionCompany=[];
						$scope.mlSolutionPublic=[];
						$scope.mlSolutionDelete=[];
						$scope.tags =[];
						$scope.newTagName=[];
						$scope.privateAlertMessage=false;
						$scope.companyAlertMessage=false;
						$scope.publicAlertMessage=false;
						$scope.searchActiveType='';
						
						$scope.mlSolutionPrivateCount = 0;
						$scope.mlSolutionPublicCount = 0;
						$scope.mlSolutionCompanyCount = 0;
						$scope.mlSolutionDeletedCount = 0;
						
						var privatePrevCounter = 0;
						var publicPrevCounter = 0;
						var companyPrevCounter = 0;
						var deletedPrevCounter = 0;
						
						var privatePrevTotal = 0;
						var publicPrevTotal = 0;
						var companyPrevTotal = 0;
						var deletedPrevTotal = 0;
						
						$scope.getPrivateModels=function(){
							$scope.dataLoading = true;
							var url = '/api/models/' + $scope.loginUserID;
							toBeSearch = $scope.searchBox;

							dataObj = {
									"request_body" : {
										"modelType" :  $scope.categoryFilter,
										"accessType" : 'PR',
										"activeType" : 'Y',
										"page" : $scope.pageNumPrivate,
										"searchTerm" : toBeSearch,
										"sortBy" : $scope.sortBy,
										"size" : $scope.defaultSize
									}
								}
							
							$http({
								method : 'POST',
								url : url,
								data : dataObj
							}).success(function(data, status, headers,config) {
								privatePrevTotal = privatePrevCounter;

								$scope.mlSolutionPrivate = data.response_body.content;
								if($scope.mlSolutionPrivate.length != 0){
									privatePrevCounter = $scope.mlSolutionPrivate.length;
								}
								
								if($scope.prOperator == 'Add' || $scope.prOperator == undefined){
									$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivate.length + $scope.mlSolutionPrivateCount;
									if(toBeSearch){
										$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivate.length;
									}
								}else if($scope.prOperator == 'Subtract'){
									$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivateCount - privatePrevTotal;
								}
								
								console.log("$scope.mlSolutionPrivateCount: "+$scope.mlSolutionPrivateCount);
								angular.forEach($scope.mlSolutionPrivate, function(value,key) {
									$scope.getSolutionImages(value.solutionId);
								});
								if(data.response_body.filteredTagSet.length > 0){
									for(var i=0;i< data.response_body.filteredTagSet.length;i++){
										var tag=data.response_body.filteredTagSet[i];
										$scope.tags.push(tag);
									}
									$scope.tags=$scope.getUniqueArrayElements($scope.tags);
								}
								$scope.showAlertMessagePri=false;
								if($scope.mlSolutionPrivate.length <1){
									$scope.msg = "No Data";
									$scope.icon = 'info_outline';
									$scope.styleclass = 'c-info';
									$scope.privateAlertMessage = true;
									$timeout(function() {
										$scope.privateAlertMessage = false;
									}, 3500);
								}
								}).error(
									function(data, status, headers,
											config) {
										$scope.isBusy = false
										console.log(status);
									});
							
						    }; 
											
						
						$scope.getCompanyModels=function(){

							var url = '/api/models/' + $scope.loginUserID;
							toBeSearch = $scope.searchBox;

							dataObj = {
									"request_body" : {
										"modelType" : $scope.categoryFilter,
										"accessType" : 'OR',
										"activeType" : 'Y',
										"page" : $scope.pageNumCompany,
										"searchTerm" : toBeSearch,
										"sortBy" : $scope.sortBy,
										"size" : $scope.defaultSize
									}
								}
							$http({
								method : 'POST',
								url : url,
								data : dataObj
							}).success(function(data, status, headers,
													config) {
								companyPrevTotal = companyPrevCounter;
								$scope.mlSolutionCompany = data.response_body.content;
								if($scope.mlSolutionCompany.length != 0){
									companyPrevCounter = $scope.mlSolutionCompany.length;
								}
								
								if($scope.cpOperator == 'Add' || $scope.cpOperator == undefined){
									$scope.mlSolutionCompanyCount = $scope.mlSolutionCompany.length + $scope.mlSolutionCompanyCount;
									if(toBeSearch){
										$scope.mlSolutionCompanyCount = $scope.mlSolutionCompany.length;
									}
								}else if($scope.cpOperator == 'Subtract'){
									$scope.mlSolutionCompanyCount = $scope.mlSolutionCompanyCount - companyPrevTotal;
								}
								
								angular.forEach($scope.mlSolutionCompany, function(value,key) {
									$scope.getSolutionImages(value.solutionId);
								});
								if(data.response_body.filteredTagSet.length > 0){
									for(var i=0;i< data.response_body.filteredTagSet.length;i++){
										var tag=data.response_body.filteredTagSet[i];
										$scope.tags.push(tag);
									}
									$scope.tags=$scope.getUniqueArrayElements($scope.tags);	
								}
								if($scope.mlSolutionCompany.length <1){
									$scope.msg = "No Data";
									$scope.icon = 'info_outline';
									$scope.styleclass = 'c-info';
									$scope.companyAlertMessage = true;
									$timeout(function() {
										$scope.companyAlertMessage = false;
									}, 3500);
								}
								}).error(
									function(data, status, headers,
											config) {
										$scope.isBusy = false
										console.log(status);
									});
						}; 
						
						
						$scope.getPublicModels=function(){
							$scope.dataLoading = true;

							var url = '/api/models/' + $scope.loginUserID;
							toBeSearch = $scope.searchBox;
							
							dataObj = {
									"request_body" : {
										"modelType" : $scope.categoryFilter,
										"accessType" : 'PB',
										"activeType" : 'Y',
										"page" : $scope.pageNumPublic,
										"searchTerm" : toBeSearch,
										"sortBy" : $scope.sortBy,
										"size" : $scope.defaultSize
									}
								}
							$http({
								method : 'POST',
								url : url,
								data : dataObj
							}).success(function(data, status, headers,
													config) {
							
								publicPrevTotal = publicPrevCounter;
								$scope.mlSolutionPublic = data.response_body.content;
								if($scope.mlSolutionPublic.length != 0){
									publicPrevCounter = $scope.mlSolutionPublic.length;
								}
								
								if($scope.pbOperator == 'Add' || $scope.pbOperator == undefined){
									$scope.mlSolutionPublicCount = $scope.mlSolutionPublic.length + $scope.mlSolutionPublicCount;
									if(toBeSearch){
										$scope.mlSolutionPublicCount = $scope.mlSolutionPublic.length;
									}
								}else if($scope.pbOperator == 'Subtract'){
									$scope.mlSolutionPublicCount = $scope.mlSolutionPublicCount - publicPrevTotal;
								}
								
								angular.forEach($scope.mlSolutionPublic, function(value,key) {
									$scope.getSolutionImages(value.solutionId);
								});
								if(data.response_body.filteredTagSet.length > 0){
									for(var i=0;i< data.response_body.filteredTagSet.length;i++){
										var tag=data.response_body.filteredTagSet[i];
										$scope.tags.push(tag);
									}
									$scope.tags=$scope.getUniqueArrayElements($scope.tags);
								 }
								if($scope.mlSolutionPublic.length <1){
									$scope.msg = "No Data";
									$scope.icon = 'info_outline';
									$scope.styleclass = 'c-info';
									$scope.publicAlertMessage = true;
									$timeout(function() {
										$scope.publicAlertMessage = false;
									}, 3500);
								}
								}).error(function(data, status, headers,config) {
										$scope.isBusy = false
										console.log(status);
									});
						};
						$scope.getDeleteModels=function(){
							$scope.dataLoading = true;
							var url = '/api/models/' + $scope.loginUserID;
							toBeSearch = $scope.searchBox;
							dataObj = {
									"request_body" : {
										"modelType" : $scope.categoryFilter,
										"activeType" : 'N',
										"accessType" : $scope.privacyFilter,
										"page" : $scope.pageNumDelete,
										"searchTerm" : toBeSearch,
										"sortBy" : $scope.sortBy,
										"size" : $scope.defaultSize
									}
								}
							$http({
								method : 'POST',
								url : url,
								data : dataObj
							}).success(function(data, status, headers,
													config) {
								publicPrevTotal = deletedPrevCounter;
								$scope.mlSolutionDelete = data.response_body.content;

								if($scope.mlSolutionDelete.length != 0){
									deletedPrevCounter = $scope.mlSolutionDelete.length;
								}
								  
								if($scope.dlOperator == 'Add' || $scope.dlOperator == undefined){
									$scope.mlSolutionDeletedCount = $scope.mlSolutionDelete.length + $scope.mlSolutionDeletedCount;
									if(toBeSearch){
										$scope.mlSolutionDeletedCount = $scope.mlSolutionDelete.length;
									}
								}else if($scope.dlOperator == 'Subtract'){
									$scope.mlSolutionDeletedCount = $scope.mlSolutionDeletedCount - publicPrevTotal;
								}
								
								angular.forEach($scope.mlSolutionDelete, function(value,key) {
									$scope.getSolutionImages(value.solutionId);
								});
								}).error(
									function(data, status, headers,
											config) {
										$scope.isBusy = false
										console.log(status);
									});
						};
						
						
						//uncomment this to fetch section wise solutions and comment loadMore() calling*/
						/*for fetching section wise data*/
						function getModels(){
							$scope.getPrivateModels();
							$scope.getCompanyModels();
							$scope.getPublicModels();
							$scope.getDeleteModels();
						}
						getModels();
						
                        $scope.publicNav=function(NavName, loadmore){
							
							if(NavName=='Next'){
								$scope.pageNumPublic = $scope.pageNumPublic+1;
								$scope.pbOperator = 'Add';
								$scope.getPublicModels();
							}else if(NavName=='Pre'){
								if($scope.pageNumPublic > 0){
									$scope.pageNumPublic = $scope.pageNumPublic-1;
									$scope.pbOperator = 'Subtract';
								}
								$scope.getPublicModels();
							}else if(NavName=='SeeAll'){
								$scope.seeAllSelected = true;
								$scope.hidePrivate = false;
								$scope.hidePublic = true;
								$scope.hideCompany = false;
								$scope.hideDelete = false;
								$scope.filterOptions = ['PUBLISHED TO PUBLIC MARKETPLACE'];
								$scope.pageNumber = $scope.pageNumPublic;
								$scope.pageNumPublic++;
								$scope.privacyFilter = 'PB';
								if(!loadmore){
									$scope.mlsolutions.length = 0;
									$scope.pageNumber = 0;
								}
								$scope.activeType = 'Y';
								$scope.loadMore('PB');	
							}
						}
                        
                        $scope.CompanyNav=function(NavName, loadmore){
							
							if(NavName=='Next'){
								$scope.pageNumCompany = $scope.pageNumCompany+1;
								$scope.cpOperator = 'Add';
								$scope.getCompanyModels();
							}else if(NavName=='Pre'){
								if($scope.pageNumCompany > 0){
									$scope.pageNumCompany = $scope.pageNumCompany-1;
									$scope.cpOperator = 'Subtract';
								}
								$scope.getCompanyModels();
							}else if(NavName=='SeeAll'){
								$scope.seeAllSelected = true;
								$scope.hidePrivate = false;
								$scope.hidePublic = false;
								$scope.hideCompany = true;
								$scope.hideDelete = false;
								$scope.filterOptions = ['PUBLISHED TO COMPANY MARKETPLACE'];
								$scope.pageNumber = $scope.pageNumCompany;
								$scope.pageNumCompany++;
								$scope.privacyFilter = 'OR';
								$scope.activeType = 'Y';
								if(!loadmore){
									$scope.mlsolutions.length = 0;
									$scope.pageNumber = 0;
								}
								$scope.loadMore('OR');	
							}
						} 
                        
						$scope.privateNav=function(NavName, loadmore){
							
							if(NavName=='Next'){
								$scope.pageNumPrivate = $scope.pageNumPrivate+1;
								$scope.prOperator = 'Add';
								$scope.getPrivateModels();		
							}else if(NavName=='Pre'){
								if($scope.pageNumPrivate > 0){
									$scope.pageNumPrivate = $scope.pageNumPrivate-1;
									$scope.prOperator = 'Subtract';
								}
								$scope.getPrivateModels();		
							}else if(NavName=='SeeAll'){
								$scope.seeAllSelected = true;
								$scope.hidePrivate = true;
								$scope.hidePublic = false;
								$scope.hideCompany = false;
								$scope.hideDelete = false;
								$scope.filterOptions = ['MY UNPUBLISHED MODELS'];
								$scope.pageNumber = $scope.pageNumPrivate;
								$scope.pageNumPrivate++;
								$scope.privacyFilter = 'PR';
								$scope.activeType = 'Y';
								if(!loadmore){
									$scope.mlsolutions.length = 0;
									$scope.pageNumber = 0;
								}
								$scope.loadMore('PR');	
							}
												
						}
						
                        $scope.deleteNav=function(NavName, loadmore){
							
							if(NavName=='Next'){
								$scope.pageNumDelete = $scope.pageNumDelete+1;
								$scope.dlOperator = 'Add';
								$scope.getDeleteModels();
							}else if(NavName=='Pre'){
								if($scope.pageNumDelete > 0){
									$scope.pageNumDelete = $scope.pageNumDelete-1;
									$scope.dlOperator = 'Subtract';									
								}
								$scope.getDeleteModels();	
							}else if(NavName=='SeeAll'){
								$scope.seeAllSelected = true;
								$scope.hidePrivate = false;
								$scope.hidePublic = false;
								$scope.hideCompany = false;
								$scope.hideDelete = true;
								$scope.filterOptions = ['MY DELETED MODELS'];
								$scope.pageNumber = $scope.pageNumDelete;
								$scope.pageNumDelete++;
								$scope.activeType = 'N';
								if(!loadmore){
									$scope.mlsolutions.length = 0;
									$scope.pageNumber = 0;
								}
								$scope.loadMore('N');
							}
						}

                        
						$scope.removeFilter=function(){
							$scope.hidePrivate = true;
							$scope.hidePublic = true;
							$scope.hideCompany = true;
							$scope.hideDelete = true;
							$scope.seeAllSelected = false;
							$scope.pageNumPrivate = 0;
							$scope.pageNumPublic = 0;
							$scope.pageNumCompany = 0;
							$scope.pageNumDelete = 0;
							$scope.mlSolutionPrivateCount = 0;
							$scope.mlSolutionPublicCount = 0;
							$scope.mlSolutionCompanyCount = 0;
							$scope.mlSolutionDeletedCount = 0;
							$scope.categoryFilter = '';
							$scope.privacyFilter = '';
							$scope.mlSolutionPrivate=[];
							$scope.mlSolutionCompany=[];
							$scope.mlSolutionPublic=[];
							$scope.mlSolutionDelete=[];
							$scope.getPrivateModels();
							$scope.getCompanyModels();
							$scope.getPublicModels();
							$scope.getDeleteModels();						
						}
						//$scope.getDeleteModels();
						/*End call for Sections*/
						// Rest call for data fetching
						// API calls
						// $scope.data =
						// {"request_body":{"modelType":"","page":0,"sortingOrder":"ASC","size":9},"request_from":"string","request_id":"string"};
						$scope.imgURL = "https://www.extremetech.com/wp-content/uploads/2015/10/AI.jpg";
						$scope.isBusy = false;
						/*var check = 0;
						var dataObj = {};
						var toBeSearch = '';
						$scope.pageNumber = 0;
						$scope.categoryFilter = '';
						$scope.privacyFilter = '';
						var duplicate = false;*/
						$scope.onLoad = function() {
							
							$scope.dataLoading = true;
							$scope.pageNumPrivate = 0;
							$scope.pageNumPublic = 0;
							$scope.pageNumCompany = 0;
							$scope.pageNumDelete = 0;
							$scope.mlSolutionPrivate.length = 0;
							$scope.mlSolutionPublic.length = 0;
							$scope.mlSolutionCompany.length = 0;
							$scope.mlSolutionDelete.length = 0;
							
							$scope.mlSolutionPrivate=[];
							$scope.mlSolutionPublic=[];
						    $scope.mlSolutionCompany=[]; 
						    $scope.mlSolutionDelete=[];
							if ($scope.isBusy)
								return;
							else
								$scope.isBusy = true;
							var url = '/api/models/' + $scope.loginUserID;
							toBeSearch = $scope.searchBox;
							dataObj = {
								"request_body" : {
									"modelType" : $scope.categoryFilter,
									"accessType" : $scope.privacyFilter,
									"activeType" : $scope.searchActiveType,
									"page" : $scope.pageNumber,
									"searchTerm" : toBeSearch,
									"sortBy":$scope.sortBy,
									// "sortingOrder" : "ASC",
									"size" : 9
								},
								"request_from" : "string",
								"request_id" : "string"
							}

							console.log(angular.toJson(dataObj))
							$http({
								method : 'POST',
								url : url,
								data : dataObj
							})
									.success(
											function(data, status, headers,
													config) {
												// if(data.response_body.content
												// &&
												// data.response_body.content.length
												// !==0){
												 
											   //$scope.tags = data.response_body.filteredTagSet;
												  

												if (data.response_body
														&& data.response_body.filteredTagSet
														&& data.response_body.filteredTagSet.length !== 0) {
													$scope.tags = data.response_body.filteredTagSet;
												}

												$scope.isBusy = false;

												if ($scope.pageNumber == 0 && data.response_body.content.length > 0) {
														 
													for (var i = 0; i < data.response_body.content.length; i++) {
														 if(data.response_body.content[i].accessType=='PR' && 
																 (data.response_body.content[i].active==true || data.response_body.content[i].active=='true')){
															 $scope.mlSolutionPrivate.push(data.response_body.content[i]);
														 }
                                                         if(data.response_body.content[i].accessType=='PB' && 
                                                        		 (data.response_body.content[i].active==true || data.response_body.content[i].active=='true')){
                                                        	 $scope.mlSolutionPublic.push(data.response_body.content[i]);
														 }
                                                         
                                                         if(data.response_body.content[i].accessType=='OR' && 
                                                        		 (data.response_body.content[i].active==true || data.response_body.content[i].active=='true')){
                                                        	 $scope.mlSolutionCompany.push(data.response_body.content[i]);
														 }
                                                         if(data.response_body.content[i].active==false || data.response_body.content[i].active=='false'){
                                                        	 $scope.mlSolutionDelete.push(data.response_body.content[i]);
														 }

													}
													//$scope.mlsolutions = data.response_body.content;
												} /*else {

													$scope.childLoginMethod = function() {
														$rootScope
																.$emit(
																		"CallLoginMethod",
																		{});
														console
																.log("CallLoginMethod inside managemodule");
													}
													$scope.childLoginMethod();

													if (data.response_body == null) {
														console
																.log("Error: No Data Found");
														$scope.noDataFound = true
														$timeout(
																function() {
																	$scope.noDataFound = false;
																}, 2000);

													} else {
														console.log(data);
														for (var i = 0; i < data.response_body.content.length; i++) {
															duplicate = false;
															angular
																	.forEach(
																			$scope.mlsolutions,
																			function(
																					value,
																					key) {
																				if (value.solutionId === data.response_body.content[i].solutionId) {
																					duplicate = true;
																					// return;
																				}
																			});
															if (!duplicate) {
																$scope.mlsolutions
																		.push({
																			// id:$scope.mlsolutions[i].id,
																			solutionId : data.response_body.content[i].solutionId,
																			solution_name : data.response_body.content[i].name,
																			marketplace_sol_id : data.response_body.content[i].marketplace_sol_id,
																			name : data.response_body.content[i].name,
																			ownerId : data.response_body.content[i].ownerId,
																			created : data.response_body.content[i].created,
																			active : data.response_body.content[i].active,
																			description : data.response_body.content[i].description,
																			accessType : data.response_body.content[i].accessType,
																			viewCount : data.response_body.content[i].viewCount,
																			downloadCount : data.response_body.content[i].downloadCount,
																			solutionRating : data.response_body.content[i].solutionRating
																		// UserDefVersion:$scope.mlsolutions[i].UserDefVersion,
																		// comments:
																		// $scope.mlsolutions[i].comments,
																		// views:
																		// $scope.mlsolutions[i].view,
																		// sol_img:$scope.mlsolutions[i].sol_img,
																		// downloads:
																		// $scope.mlsolutions[i].downloads
																		});
															}
														}
														// $scope.starCount =
														// data.response_body.content.solutionRating
														// $stateParams.solutionId
														// =
														// $scope.mlsolutions[0].solutionId;
														// localStorage.setItem('soluId',$scope.mlsolutions[0].solutionId);
														// $window.sessionStorage.setItem("SavedString",$scope.solutionId);
														// $sessionStorage.soluId
														// = $scope.solutionId;
													}

												}*/
												//$scope.isBusy = false
												//$scope.pageNumber += 1;
												//$scope.dataLoading = false;
												// }

											}).error(
											function(data, status, headers,
													config) {
												// called asynchronously if an
												// error occurs
												// or server returns response
												// with an error status.
												$scope.isBusy = false
												console.log(status);
											});

						}
						//$scope.onLoad();
						
						/* * $scope.$on("userLoggedIn", function(evt,data){
						 * $scope.data = JSON.parse(data); $scope.loginUserID =
						 * $scope.data.userId; $scope.onLoad("manageModule");
						 * });*/
						 
						$scope.getModel = function(id) {
							console.log(id);
							window.location.href = "#/models/" + id;
							var newScope = $scope.$new(true, $scope);
							newScope = angular.merge(newScope, 'model-Details');
							var html = '<model-Details></model-Details>';
							var element = $('#section_content');
							// element.attr("ng-view");
							element.html($compile(html)(newScope));
							console.log(newScope);
						}
						// Rest API for category
						$http({
							method : 'GET',
							url : '/api/filter/modeltype',
						}).success(function(data, status, headers, config) {
							$scope.category = data.response_body;
						}).error(function(data, status, headers, config) {
							// called asynchronously if an error occurs
							// or server returns response with an error
							// status.
							console.log(status);
						});
						// Access type
						$http({
							method : 'GET',
							url : '/api/filter/accesstype',
						}).success(function(data, status, headers, config) {
							$scope.privacyCheckBox = data.response_body;
						}).error(function(data, status, headers, config) {
							// called asynchronously if an error occurs
							// or server returns response with an error
							// status.
							console.log(status);
						});
						// Filter by id
						
						/* * $scope.filterids = [{ name:"001", value:"1"},{
						 * name:"002", value:"2"},{ name:"003", value:"3"}, {
						 * name:"004", value:"4"},{ name:"005", value:"5"},{
						 * name:"006", value:"6"} ];*/
						 
						// filter functionlity
						var privacyArr = [];
						var caegoryArr = [];
						var url = 'solutions';
						var categoryUrl = '';
						var sortByUrl = '';
						var sortByIdUrl = '';
						var searchUrl = '';
						var privacyUrl = ''
						$scope.filterChange = function(checkbox, type) {
							$scope.mlSolutionPrivateCount = 0;
							$scope.mlSolutionPublicCount = 0;
							$scope.mlSolutionCompanyCount = 0;
							$scope.mlSolutionDeletedCount = 0;
							if(checkbox == 'yes'){
								$scope.activeType = 'N';
							}else if(checkbox == 'no' )$scope.activeType = 'Y';
							$scope.pageNumber = 0;
							$scope.mlsolutions = [];
							$scope.modelCount = 0;
							$scope.isBusy = false;
							if (type == 'solution') {

							} else if (type == 'privacy') {
								$scope.searchActiveType='Y';
								var bool = false;
								privacyUrl = '';
								url = '';
								for (var j = 0; j < privacyArr.length; j++) {
									if (privacyArr[j].match(checkbox)) {
										privacyArr.splice(j, 1);
										bool = true;
										break;
									}
									;
								}
								if (bool == false) {
									privacyArr.push(checkbox);
								}
								angular.forEach(privacyArr,
										function(value, key) {
											privacyUrl = privacyUrl + value
													+ ",";
										});
								privacyUrl = privacyUrl.slice(0, -1);
							}

							else if (type == 'caegory') {
								categoryUrl = '';
								url = '';
								var bool1 = false;
								for (var j = 0; j < caegoryArr.length; j++) {
									if (caegoryArr[j].match(checkbox)) {
										caegoryArr.splice(j, 1);
										bool1 = true;
										break;
									}
									;
								}
								if (bool1 == false) {
									caegoryArr.push(checkbox);
								}
								angular.forEach(caegoryArr,
										function(value, key) {
											categoryUrl = categoryUrl + value
													+ ",";
										});
								categoryUrl = categoryUrl.slice(0, -1);
							} else if(type == 'searchFilter'){
								if($scope.searchBox!=null && $scope.searchBox!=''){
									$scope.searchActiveType='';
								}
								$scope.mlSolutionPrivateCount = 0;
								$scope.mlSolutionPublicCount = 0;
								$scope.mlSolutionCompanyCount = 0;
								$scope.mlSolutionDeletedCount = 0;
							}
							$scope.categoryFilter = categoryUrl;
							$scope.privacyFilter = privacyUrl;
							
							if(privacyUrl!=null && privacyUrl!=''){
								$scope.searchActiveType='Y';
							}else if(categoryUrl!=null && categoryUrl!=''){
								$scope.searchActiveType='';
							}
							
							if(type == 'sortBy'){$scope.sortBy = checkbox.value;}else if(type == 'sortById')$scope.sortById = checkbox.value;
							getModels();
							/* * else if(type == 'sortBy'){ sortByUrl='';url='';
							 * $scope.sortedby = checkbox; $scope.sortBy =
							 * checkbox; sortByUrl = "sortBy="+$scope.sortedby; }
							 * else if(type == 'sortById'){
							 * sortByIdUrl='';url=''; $scope.sortedbyid =
							 * checkbox; $scope.sortById = checkbox; sortByIdUrl =
							 * "sortById="+$scope.sortedbyid; } else if(type ==
							 * 'searchFilter'){ url=''; if($scope.searchBox ==
							 * ''){searchUrl='';} else {debugger;searchUrl =
							 * "search="+$scope.searchBox;$scope.master =
							 * false;categoryUrl='';sortByUrl='';sortByIdUrl='';
							 * $scope.sortedby='';$scope.sortedbyid=''} }
							 

							
							 * if(categoryUrl.length){url = categoryUrl;}
							 * if(sortByUrl.length && $scope.sortedby){
							 * if(url.length){url = url+'&'+sortByUrl}else url =
							 * sortByUrl} if(sortByIdUrl.length &&
							 * $scope.sortedbyid){ if(url.length){url =
							 * url+'&'+sortByIdUrl}else url = sortByIdUrl}
							 * if(searchUrl.length && $scope.searchBox){
							 * if(url.length){url = url+'&'+searchUrl}else url =
							 * searchUrl}
							 * 
							 * if(url.length){url = 'solutions?'+url;} else {url =
							 * 'solutions';}
							 * console.log('url');console.log(url);*/
							/*if($scope.categoryFilter =='' && $scope.privacyFilter =='' && ($scope.searchBox =='' || $scope.searchBox==undefined)){
								$scope.getPrivateModels();
								$scope.getCompanyModels();
								$scope.getPublicModels();
								$scope.getDeleteModels();
							}else{
								$scope.onLoad();
								
							}*/
							

						}
						
						$scope.onClickModel = function(id, ownerId){
							/*if($scope.loginUserID == ownerId){
								$state.go('marketSolutions',{'solutionId':id})
								//window.location.href = "#/modelEdit/" + id;
							}else{
								window.location.href = "#/marketSolutions/" + id;
							}*/
							$scope.updateViewCount = function() {
								$scope.solutionId = id;
								apiService
										.updateViewCount($scope.solutionId)
										.then(
												function(response) {
													$scope.status = response.status;
													$scope.detail = response.data.response_detail;
													$state.go('marketSolutions', {solutionId : id, parentUrl:'mymodel' });
												},
												function(error) {
													$scope.status = 'Unable to load data: '
															+ error.data.error;
													console.log("Error: "+error.data);
													$state.go('marketSolutions', {solutionId : id, parentUrl:'mymodel'});
												});

							}
							$scope.updateViewCount();
						}
						
						if( JSON.parse(localStorage.getItem("userDetail")) ){
				            $scope.userDetails = JSON.parse(localStorage.getItem("userDetail"));
				            $scope.userDetails.userName = $scope.userDetails[0];
				            $scope.loginUserID = $scope.userDetails[1];
						}
						
						$scope.getUniqueArrayElements=function(array){
							  var result = [];
							  for(var x = 0; x < array.length; x++){
								 if(array[x]!=null && array[x]!=''){ 
								  if(result.indexOf(array[x]) == -1)
								        result.push(array[x]);
								  }
							  } 
							  return result;
							}
						
					
						var count = 1;
						$scope.isBusy = false;
						var check = 0;
						var dataObj = {};
						var toBeSearch = '';
						var duplicate = false;
						$scope.imageUrls = {};
						
						$scope.getSolutionImages = function(solutionId) {
							apiService.getSolutionImage(solutionId)
							.then(function(response) {
								if(response.data.response_body.length > 0)
									  $scope.imageUrls[solutionId] = "/site/binaries/content/gallery/acumoscms/solution/" + solutionId + "/" + response.data.response_body[0];
								  else
									  $scope.imageUrls[solutionId] = "images/default-model.png";
								}, function(data) {
									$scope.imageUrls[solutionId] = "images/default-model.png";
								});
						}
						
						$scope.loadMore = function(type) {
							debugger;
							if ($scope.isBusy)
								return;
							else
								$scope.isBusy = true;
							// $scope.mlsolutions = [];
							$scope.dataLoading = true;

							/*
							 * if($scope.mlsolutions.length <= counter){
							 * $scope.nodata = true; $scope.dataLoading = false;
							 * $scope.isBusy = true; return; }
							 */
							// $scope.dataLoading = false;
							toBeSearch = $scope.searchBox;
							if($scope.viewNoMLsolution == 'No More ML Solutions' && $scope.pageNumber != 0){return;}
							$scope.MlSoltionCount = false;
							dataObj = {
								"request_body" : {
									"modelType" : $scope.categoryFilter,
									"accessType" : $scope.privacyFilter,
									"page" : $scope.pageNumber,
									"sortBy":$scope.sortBy,
									"searchTerm" : toBeSearch,
									"size" : 9,
									"activeType": $scope.activeType,
								}
							}

							console.log(angular.toJson(dataObj));
							//$scope.getPrivateModels();
							/*
							 * $http({ method : 'POST', url : url, data :
							 * dataObj, // params: //
							 * {"category":caegoryArr,"sortby":$scope.sortedby,"sortbyid":$scope.sortedbyid}, })
							 */
							
							apiService
									.getAllModels(dataObj, $scope.loginUserID)
									.then(
											function(response) {
											angular.forEach(response.data.response_body.content,function(value,key) {
												if(response.data.response_body.content[key].active){$scope.modelCount = $scope.modelCount+1;}
											});
												$scope.isBusy = false;$scope.MlSoltionCount = true;
												if($scope.pageNumber==0){
													$scope.mlsolutions = response.data.response_body.content;
													angular.forEach($scope.mlsolutions, function(value,key) {
														$scope.getSolutionImages(value.solutionId);
													});
												}
												
												if(response.data.response_body.content.length==9){
													$scope.viewNoMLsolution = 'View More ML Solutions';
													}
												else {
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
																				// return;
																			}
																		});
														if (!duplicate) {
															$scope.getSolutionImages(response.data.response_body.content[i].solutionId);
															$scope.mlsolutions
																	.push({
																		// id:$scope.mlsolutions[i].id,
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
																		ownerName: response.data.response_body.content[i].ownerName,
																		modelType : response.data.response_body.content[i].modelType,
																		ratingCount: response.data.response_body.content[i].ratingCount

																	// solutionTag
																	// :
																	// response.data.response_body.content[i].
																	// UserDefVersion:$scope.mlsolutions[i].UserDefVersion,
																	// comments:
																	// $scope.mlsolutions[i].comments,
																	// views:
																	// $scope.mlsolutions[i].view,
																	// sol_img:$scope.mlsolutions[i].sol_img,
																	// downloads:
																	// $scope.mlsolutions[i].downloads
																	});
														}
													}

												} 
												apiService
												.getFavoriteSolutions($scope.loginUserID)
												.then(
														function(response) {
															angular.forEach(response.data.response_body, 
																	function(favValue, favKey){
																	angular.forEach($scope.mlsolutions,
																			function(mlsolutionValue,mlsolutionKey){
																			if(response.data.response_body[favKey].solutionId == $scope.mlsolutions[mlsolutionKey].solutionId){
																				$scope.mlsolutions[mlsolutionKey].selectFav = true;
																				console.log("fav solution");
																			}
																	});
															});
															console.log(response.data.response_body);
														},function(error) {
															$scope.status = 'Unable to load data: '
																+ error.data.error;
														console.log($scope.status);
													});
												/*
													 * else { console.log(data);
													 * $scope.mlsolutions =
													 * data.response_body.content;
													 * $stateParams.solutionId =
													 * $scope.mlsolutions[0].solutionId;
													 * $sessionStorage.solutionId
													 * =$scope.mlsolutions[0].solutionId;
													 * console.log("sessionStorage.solutionId"+$sessionStorage.solutionId);
													 * //$window.sessionStorage.setItem("SavedString",$scope.solutionId);
													 * //$sessionStorage.soluId =
													 * $scope.solutionId; }
													 */
												if (response.data.response_body.content.length == 9) {
													$scope.pageNumber += 1;
												}
												// $scope.pageNumber += 1;
												$scope.isBusy = false;
												// Show deleted solutions on
												// search starts
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
												// Show deleted solutions on
												// search ends
												// console.clear()
												
												if(type == 'PB') {
													$scope.mlSolutionPublic = $scope.mlsolutions;
													$scope.mlSolutionPublicCount = $scope.mlsolutions.length;
												} else if(type == 'PR') {
													$scope.mlSolutionPrivate = $scope.mlsolutions;
													$scope.mlSolutionPrivateCount = $scope.mlsolutions.length;
												} else if(type == 'OR') {
													$scope.mlSolutionCompany = $scope.mlsolutions;
													$scope.mlSolutionCompanyCount = $scope.mlsolutions.length;
												} else if(type == 'N') {
													$scope.mlSolutionDelete = $scope.mlsolutions;
													$scope.mlSolutionDeletedCount = $scope.mlsolutions.length;
												}

											},
											function(error) {
												$scope.status = 'Unable to load data: '
														+ error.data.error;
												console.log($scope.status);
											});
							
											
							count += 9;
							// console.clear()
						}
						/*for all models loading all together*/						
						$scope.loadMore();
						
						$scope.updateFavorite = function(solutionId, key){
							//$scope.selectFav = !$scope.selectFav;
							var dataObj = {
									  "request_body": {
										    "solutionId": solutionId,
										    "userId": $scope.loginUserID
										  },
										  "request_from": "string",
										  "request_id": "string"
										}
							if($scope.mlsolutions[key].selectFav){
								apiService.createFavorite(dataObj)
								.then(function(response) {
									console.log('Favorite created');
								});
							}else if(!$scope.mlsolutions[key].selectFav){
								apiService.deleteFavorite(dataObj)
								.then(function(response) {
									console.log('Favorite deleted');
								});
							}
						};
						
						
						// Images URL
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
						
						$scope.$on('scanner-started', function(event, args) {
							$scope.pageNumber = 0;
							$scope.searchBox = args.searchValue;
							getModels();
							//$scope.loadMore();
							// do what you want to do
						});
						
						
						$scope.getSolutionsCount = function(){
							apiService
							.getSolutionsCount($scope.loginUserID)
							.then(
									function(response) {
										console.log(response.data);
										$scope.solutionsCount = response.data;
										
									},function(error) {
										$scope.status = 'Unable to load data: '+ error.data.error;
									console.log($scope.status);
								});
						}
						$scope.getSolutionsCount();
					}

				});
