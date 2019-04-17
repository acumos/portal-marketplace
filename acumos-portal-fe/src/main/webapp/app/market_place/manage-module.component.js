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
 
angular.module('manageModule')
		.component(
				'manageModule',
				{

					templateUrl : './app/market_place/md-manage-module.template.html',
					controller : function($scope, $compile, $location, $http, $q,
							$sessionStorage, $localStorage, $rootScope,
							$timeout, $state, apiService, $window, browserStorageService, $mdDialog, $anchorScroll) {
						$scope.autoHeight = true;
						$scope.hidePrivate = true;
						$scope.hidePublic = true;
						$scope.hideDelete = true;
						$scope.defaultSize = 4;
						$scope.seeAllSelected = false;
						$scope.Loadcheck = false;
						$scope.selectedChip = [];
						$scope.mlsolutions = [];
						$scope.fieldToSort = { "modified" : "DESC" };
						if(localStorage.getItem("viewMM")){
						  if(localStorage.getItem("viewMM") == 'false')$scope.Viewtile = false;else $scope.Viewtile = true;
						}else $scope.Viewtile = true;
						
						$scope.$watch('Viewtile', function() {
							   localStorage.setItem("viewMM", $scope.Viewtile);
							});
						$rootScope.urlPath = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);
						if ($rootScope.urlPath == 'manageModule') {
							$scope.parentUrl = false
						} else {
							$scope.parentUrl = true
						}
						
						localStorage.setItem('HeaderNameVar','manageModule');
						$scope.actions = [ {name: "Most Liked", value: "ML"}, 
						                   {name: "Fewest Liked", value: "FL"},
						                   {name: "Most Downloaded", value: "MD"},
						                   {name: "Fewest Downloaded", value: "LD"}, 
						                   {name: "Highest Reach", value: "HR"},
						                   {name: "Lowest Reach", value: "LR"},
						                   {name: "Most Recent", value: "MR"},
						                   {name: "Older", value: "OLD"},
						                   {name: "Name", value: "name"},
						                   {name: "Created Date", value: "created"} ];
						
						$scope.filterids = [ "001", "002", "003", "004", "005",
								"006" ];

						if (JSON.parse(browserStorageService.getUserDetail())) {
							$scope.userDetails = JSON.parse(browserStorageService
									.getUserDetail());
							$scope.userDetails.userName = $scope.userDetails[0];
							$scope.loginUserID = $scope.userDetails[1];
						}
						/*Start call for Sections*/
						var check = 0;
						var dataObj = {};

						$scope.pageNumber = 0;
						$scope.categoryFilter = [];
						$scope.privacyFilter = '';
						$scope.tagFilter = [];
						$scope.modelCount = 0;
						var dataObjPrivate = {};
						var dataObjPublic = {};
						var dataObjDelete = {};
						$scope.pageNumPrivate = 0;
						$scope.pageNumPublic = 0;
						$scope.pageNumDelete = 0;
						$scope.mlSolutionPrivate=[];
						$scope.mlSolutionPublic=[];
						$scope.mlSolutionDelete=[];
						$scope.tags =[];
						$scope.newTagName=[];
						$scope.privateAlertMessage=false;
						$scope.publicAlertMessage=false;
						$scope.searchActiveType='';
						
						$scope.mlSolutionPrivateCount = 0;
						$scope.mlSolutionPublicCount = 0;
						$scope.mlSolutionDeletedCount = 0;
						
						$scope.totalPrivateSolCount = 0;
						$scope.totalPublicSolCount = 0;
						$scope.totalDeletedSolCount = 0;
						
						var privatePrevCounter = 0;
						var publicPrevCounter = 0;
						var deletedPrevCounter = 0;
						
						var privatePrevTotal = 0;
						var publicPrevTotal = 0;
						var deletedPrevTotal = 0;
                        
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
						
						//added functionality for deleted model category filter starts
						$scope.delete1=true;
						$scope.get = function(){
							if($scope.checked){ 
								 $scope.removeFilter();
                            } else {
                            	$scope.delete1=false;
                            	$scope.Navigation(0,'N');
                            }
						}
						
						//added functionality for deleted model category filter ends
						
						$scope.favouriteList = function() {
							if ($scope.loginUserID) {
								apiService
										.getFavoriteSolutions($scope.loginUserID)
										.then(function(response) {
													$scope.favouriteSolutions = response.data.response_body;
												},
											function(error) {
										});
							}
						};
						$scope.favouriteList();
						
						$scope.getPrivateModels=function(){
							
							$scope.mlsolutions = [];
							if($scope.sortBy == "ML" || $scope.sortBy == "FL" || $scope.sortBy == "HR" || $scope.sortBy == "LR" )
							 {
								$scope.mlSolutionPrivate =[];
								$scope.msg = "No Data";
								$scope.icon = 'info_outline';
								$scope.styleclass = 'c-info';
								$scope.privateAlertMessage = true;
								$scope.totalPrivateSolCount = 0;
								return;
							}
							$scope.dataLoading = true;
							$scope.privateDataLoaded = true;
							dataObj = {
									  "request_body": {
										    "active": true,
										    "published" : false,
										    "tags" : $scope.tagFilter,
										    "nameKeyword" :  $scope.toBeSearch,
										    "modelTypeCodes": $scope.categoryFilter,
										    "userId": $scope.loginUserID,
											"sortBy": $scope.sortBy,
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumPrivate,
										      "size" : $scope.defaultSize
										    }
										  }
										};
							
							
							if($scope.Loadcheck){
								$scope.SetDataLoaded = true;
								$rootScope.setLoader = true;
							}
							else{
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
							}
							apiService.fetchUserSolutions(dataObj).then(function(response) {
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
								var data = response.data;
								privatePrevTotal = privatePrevCounter;
								$scope.privateDataLoaded = false;
								
								$scope.mlSolutionPrivate = $scope.getUniqueSolutions(data.response_body.content);
								$scope.totalPrivateSolCount = data.response_body.totalElements;
    
								$scope.totalPages = data.response_body.pageCount;
								
								if($scope.mlSolutionPrivate == 0){
									$scope.startPageSizePR = 0;
									$scope.endPageSizePR = 0;
								}
								else {
								
								$scope.loadpagePR =  $scope.pageNumPrivate;
								$scope.startPageSizePR = $scope.loadpagePR *  $scope.defaultSize + 1;
								$scope.endPageSizePR =(($scope.loadpagePR + 1) * $scope.defaultSize) < $scope.totalPrivateSolCount ? (($scope.loadpagePR + 1) * $scope.defaultSize) : $scope.totalPrivateSolCount; 
								}
								
								if($scope.mlSolutionPrivate.length != 0){
									privatePrevCounter = $scope.mlSolutionPrivate.length;
								}
								
								if($scope.prOperator == 'Add' || $scope.prOperator == undefined){
									$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivate.length + $scope.mlSolutionPrivateCount;
									if($scope.toBeSearch.length > 0){
										$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivate.length;
									}
								}else if($scope.prOperator == 'Subtract'){
									$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivateCount - privatePrevTotal;
								}
								
								angular.forEach($scope.mlSolutionPrivate, function(value,key) {
									angular.forEach($scope.favouriteSolutions,
											function(mlsolutionValue,mlsolutionKey) {
												if (mlsolutionValue.solutionId == value.solutionId) {
													$scope.mlSolutionPrivate[key].selectFav = true;
												}
									});
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
								}, function(error) {
										$scope.isBusy = false
								});
							
						    }; 
				
						
						$scope.getPublicModels=function(){
							$scope.dataLoading = true;
							$scope.publicDataLoaded = true;
							$scope.mlsolutions = [];
							
							dataObj = {
									  "request_body": {
										    "published" : true,
										    "active": true,
										    "nameKeyword" :  $scope.toBeSearch,
										    "tags" : $scope.tagFilter,
										    "modelTypeCodes": $scope.categoryFilter,
										    "userId": $scope.loginUserID,
											"sortBy": $scope.sortBy,
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumPublic,
										      "size" : $scope.defaultSize
										    }
										  }
										};
							
							
							
							if($scope.Loadcheck){
								$scope.SetDataLoaded = true;
								$rootScope.setLoader = true;
							}
							else{
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
							}
							
							apiService.fetchUserSolutions(dataObj).then(function(response) {
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
								
								var data = response.data;
							
								publicPrevTotal = publicPrevCounter;
								$scope.publicDataLoaded = false;
								
								$scope.mlSolutionPublic = $scope.getUniqueSolutions(data.response_body.content);
								$scope.totalPublicSolCount = data.response_body.totalElements;
								$scope.totalPages = data.response_body.pageCount;
								
								 if($scope.mlSolutionPublic == 0){
										
										$scope.startPageSizePB = 0;
										$scope.endPageSizePB = 0;
								 } else {
								
								    $scope.loadpagePB = $scope.pageNumPublic;
								    $scope.startPageSizePB = $scope.loadpagePB *  $scope.defaultSize + 1;
									$scope.endPageSizePB =  (($scope.loadpagePB + 1) * $scope.defaultSize) < $scope.totalPublicSolCount ?  (($scope.loadpagePB + 1) * $scope.defaultSize) : $scope.totalPublicSolCount
								 }
								
								if($scope.mlSolutionPublic.length != 0){
									publicPrevCounter = $scope.mlSolutionPublic.length;
								}
								
								if($scope.pbOperator == 'Add' || $scope.pbOperator == undefined){
									$scope.mlSolutionPublicCount = $scope.mlSolutionPublic.length + $scope.mlSolutionPublicCount;
									if($scope.toBeSearch.length > 0){
										$scope.mlSolutionPublicCount = $scope.mlSolutionPublic.length;
									}
								}else if($scope.pbOperator == 'Subtract'){
									$scope.mlSolutionPublicCount = $scope.mlSolutionPublicCount - publicPrevTotal;
								}
								
								angular.forEach($scope.mlSolutionPublic, function(value,key) {
									angular.forEach($scope.favouriteSolutions,
											function(mlsolutionValue,mlsolutionKey) {
												if (mlsolutionValue.solutionId == value.solutionId) {
													$scope.mlSolutionPublic[key].selectFav = true;
												}
									});
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
								}, function(error) {
										$scope.isBusy = false
								});
						};
						$scope.getDeleteModels=function(){
							$scope.mlsolutions = [];
							if($scope.sortBy == "ML" || $scope.sortBy == "FL" || $scope.sortBy == "HR" || $scope.sortBy == "LR" ||
							   $scope.sortBy == "name" || $scope.sortBy == "created" || $scope.sortBy == "ownerName" || $scope.sortBy == "OLD" || $scope.sortBy == "MR")
							 {
								$scope.mlSolutionDelete =[];
								$scope.totalDeletedSolCount = 0;
								return;
							}
							
							$scope.dataLoading = true;
							$scope.deleteDataLoaded = true;
							dataObj = {
									  "request_body": {
										    "active": false,
										    "nameKeyword" :  $scope.toBeSearch,
										    "tags" : $scope.tagFilter,
										    "modelTypeCodes": $scope.categoryFilter,
										    "userId": $scope.loginUserID,
											"sortBy": $scope.sortBy,
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumDelete,
										      "size" : $scope.defaultSize
										    }
										  }
										};
							
							if($scope.Loadcheck){
								$scope.SetDataLoaded = true;
								$rootScope.setLoader = true;
							}
							else{
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
							}
							
							apiService.fetchUserSolutions(dataObj).then(function(response) {
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
								
								publicPrevTotal = deletedPrevCounter;
								var data = response.data;

								$scope.deleteDataLoaded = false;
								$scope.mlSolutionDelete = $scope.getUniqueSolutions(data.response_body.content);
								$scope.totalDeletedSolCount = data.response_body.totalElements;
								$scope.totalPages = data.response_body.pageCount;
								if($scope.mlSolutionDelete == 0){										
									$scope.startPageSizeN = 0;
									$scope.endPageSizeN = 0;
								} else {
									$scope.loadpageN = $scope.pageNumDelete;
								    $scope.startPageSizeN = $scope.loadpageN *  $scope.defaultSize + 1;
									$scope.endPageSizeN = (($scope.loadpageN + 1) * $scope.defaultSize) < $scope.totalDeletedSolCount ? (($scope.loadpageN + 1) * $scope.defaultSize) : $scope.totalDeletedSolCount; 
								}
								 
								if($scope.mlSolutionDelete.length != 0){
									deletedPrevCounter = $scope.mlSolutionDelete.length;
								}
								  
								if($scope.dlOperator == 'Add' || $scope.dlOperator == undefined){
									$scope.mlSolutionDeletedCount = $scope.mlSolutionDelete.length + $scope.mlSolutionDeletedCount;
									if($scope.toBeSearch.length > 0){
										$scope.mlSolutionDeletedCount = $scope.mlSolutionDelete.length;
									}
								}else if($scope.dlOperator == 'Subtract'){
									$scope.mlSolutionDeletedCount = $scope.mlSolutionDeletedCount - publicPrevTotal;
								}
								
								angular.forEach($scope.mlSolutionDelete, function(value,key) {
									angular.forEach($scope.favouriteSolutions,
											function(mlsolutionValue,mlsolutionKey) {
												if (mlsolutionValue.solutionId == value.solutionId) {
													$scope.mlSolutionDelete[key].selectFav = true;
												}
									});
								});
								if(data.response_body.filteredTagSet.length > 0){
									for(var i=0;i< data.response_body.filteredTagSet.length;i++){
										var tag=data.response_body.filteredTagSet[i];
										$scope.tags.push(tag);
									}
									$scope.tags=$scope.getUniqueArrayElements($scope.tags);	
								}
								}, function(error) {
										$scope.isBusy = false
								});
						};
						
						
						//uncomment this to fetch section wise solutions and comment loadMore() calling*/
						/*for fetching section wise data*/
						function getModels(){
							if($scope.searchBox!=null && $scope.searchBox!='')
								 $scope.toBeSearch[0] = $scope.searchBox;
							else 
								 $scope.toBeSearch = [];		
							$scope.getPrivateModels();
							$scope.getPublicModels();
							$scope.getDeleteModels();
                            $rootScope.valueToSearch = '';
						
						}
						getModels();
						
                 ///Pagination Starts for counting navigation
						
						$scope.Navigation = function( selectedPage,Type ){
							   
							   if($scope.defaultSize <= 10)
								   $scope.defaultSize = 10;
							   else
								   $scope.defaultSize;
									   
							   $scope.mlsolutions = [];
							   $scope.pageNumber = selectedPage ;
							   $scope.selectedPage = selectedPage;
							   
							if(Type == 'PR'){
								$scope.seeAllSelected = true;
								$scope.hidePrivate = true;
								$scope.hidePublic = false;								
								$scope.hideDelete = false;
								$scope.filterOptions = ['MY UNPUBLISHED MODELS'];
								$scope.privacyFilter = 'PR';
								$scope.activeType = true;
								
								$scope.loadMore('PR');	
							}
							
                         if(Type == 'PB'){
                        	 $scope.seeAllSelected = true;
								$scope.hidePrivate = false;
								$scope.hidePublic = true;
								$scope.hideDelete = false;
								$scope.filterOptions = ['PUBLISHED TO PUBLIC MARKETPLACE'];
								$scope.privacyFilter = 'PB';
								$scope.activeType = true;
								
								$scope.loadMore('PB');	
						}
							
                         if(Type == 'N'){
                        	 $scope.seeAllSelected = true;
								$scope.hidePrivate = false;
								$scope.hidePublic = false;
								$scope.hideDelete = true;
								$scope.filterOptions = ['MY DELETED MODELS'];
								$scope.activeType = false;
								
								$scope.loadMore('N');
							}
							
						}
						///Pagination ends for counting navigation
						
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
							}
						}
                        
                     
						$scope.privateNav=function(NavName,loadmore){

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
							}
						}

      
						$scope.removeFilter=function(){
							$scope.hidePrivate = true;
							$scope.hidePublic = true;
							$scope.hideDelete = true;
							$scope.seeAllSelected = false;
							$scope.Loadcheck = false;
							$scope.defaultSize = 4;
							$scope.pageNumPrivate = 0;
							$scope.pageNumPublic = 0;
							$scope.pageNumDelete = 0;
							$scope.mlSolutionPrivateCount = 0;
							$scope.mlSolutionPublicCount = 0;
							$scope.mlSolutionDeletedCount = 0;
							$scope.categoryFilter = [];
							$scope.privacyFilter = '';
							$scope.prOperator = 'Add';
							$scope.pbOperator = 'Add';
							$scope.dlOperator = 'Add';
							$scope.filterOptions = [];
							$scope.mlSolutionPrivate=[];
							$scope.mlSolutionPublic=[];
							$scope.mlSolutionDelete=[];
							$scope.getPrivateModels();
							$scope.getPublicModels();
							$scope.getDeleteModels();
							$scope.selectedPage = 0;
							
						}
						
						$scope.imgURL = "https://www.extremetech.com/wp-content/uploads/2015/10/AI.jpg";
						$scope.isBusy = false;
						 
						$scope.getModel = function(id) {
							window.location.href = "#/models/" + id;
							var newScope = $scope.$new(true, $scope);
							newScope = angular.merge(newScope, 'model-Details');
							var html = '<model-Details></model-Details>';
							var element = $('#section_content');
							element.html($compile(html)(newScope));
						}
						// Rest API for category
						$http({
							method : 'GET',
							url : '/api/filter/modeltype',
						}).success(function(data, status, headers, config) {
							$scope.category = data.response_body;
						}).error(function(data, status, headers, config) {
						});
						// Access type
						$http({
							method : 'GET',
							url : '/api/filter/accesstype',
						}).success(function(data, status, headers, config) {
							$scope.privacyCheckBox = data.response_body;
						}).error(function(data, status, headers, config) {
						});
						 
						
						//Change pagination Size starts 
						$scope.paginationSize = function(size, accessType){
							$scope.Loadcheck = true;
							$scope.selectedPage = 0;
							$scope.defaultSize = size;
							if(accessType == 'PR'){
								$scope.getPrivateModels();
							}else if(accessType == 'PB'){
								
								$scope.getPublicModels();
							}
							else if(accessType == 'N'){
								$scope.getDeleteModels();
							}
						}
						//Change pagination Size ends
						
						// filter functionlity
						var privacyArr = [];
						var caegoryArr = [];
						var url = 'solutions';
						var categoryUrl = '';
						var tagArr = [];
						var sortByUrl = '';
						var sortByIdUrl = '';
						var searchUrl = '';
						var privacyUrl = ''
						$scope.filterChange = function(checkbox, type) {
							$scope.mlSolutionPrivateCount = 0;
							$scope.mlSolutionPublicCount = 0;
							$scope.mlSolutionDeletedCount = 0;
							$scope.pageNumPrivate = 0;
							$scope.pageNumPublic = 0;
							$scope.pageNumDelete = 0;
							if(checkbox == 'yes'){
								$scope.activeType = false;
							}else if(checkbox == 'no' )$scope.activeType = true;
								$scope.pageNumber = 0;
								$scope.mlsolutions = [];
								$scope.modelCount = 0;
								$scope.isBusy = false;
							if (type == 'solution') {

							} else if (type == 'privacy') {
								$scope.searchActiveType=true;
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
								$scope.mlSolutionDeletedCount = 0;
							} else if(type == 'tag'){
								var dupli = false,index=0;
								angular.forEach(tagArr, function(value,
										key) {
									if (value === checkbox) {
										dupli = true;
										index = key;
									}
								});
								if(dupli == false)tagArr.push(checkbox);
								else tagArr.splice(index, 1);
							}
							$scope.categoryFilter = caegoryArr;
							$scope.privacyFilter = privacyUrl;
							$scope.tagFilter = tagArr;
							
							if(privacyUrl!=null && privacyUrl!=''){
								$scope.searchActiveType=true;
							}else if(categoryUrl!=null && categoryUrl!=''){
								$scope.searchActiveType='';
							}
							
							if(type == 'sortBy'){$scope.sortBy = checkbox.value;}else if(type == 'sortById')$scope.sortById = checkbox.value;
							
							if( $scope.sortBy == 'name' ){
                               $scope.fieldToSort = { "name" : "ASC" };
                            }
                        	if( $scope.sortBy == 'created' ){
                               $scope.fieldToSort = { "created" : "DESC" };
                            }
                        	if( $scope.sortBy == 'ownerName' ){
                               $scope.fieldToSort = { "ownerName" : "ASC" };
                            }
							if( $scope.sortBy == 'OLD' ){
                                $scope.fieldToSort = { "modified" : "ASC" };
                             }
                        	if( $scope.sortBy == 'MR' ){
                                $scope.fieldToSort = { "modified" : "DESC" };
                             }
                        	if( $scope.sortBy == 'HR' ){
                                $scope.fieldToSort = { "viewCount" : "DESC" };
                             }
                        	if( $scope.sortBy == 'LR' ){
                                $scope.fieldToSort = { "viewCount" : "ASC" };
                             }
                        	if( $scope.sortBy == 'ML' ){
                                $scope.fieldToSort = { "ratingAverageTenths" : "DESC" };
                             }
                        	if( $scope.sortBy == 'FL' ){
                                $scope.fieldToSort = { "ratingAverageTenths" : "ASC" };
                             }
                        	if( $scope.sortBy == 'MD' ){
                                $scope.fieldToSort = { "downloadCount" : "DESC" };
                             }
                        	if( $scope.sortBy == 'FD' ){
                                $scope.fieldToSort = { "downloadCount" : "ASC" };
                             }
							getModels();
						}
						
						$scope.selectChip = function(index){
							$scope.selectedChip[index] = !$scope.selectedChip[index];
						};
						
						$scope.onClickModel = function(id, ownerId, revisionId){

							$scope.updateViewCount = function() {
								$scope.solutionId = id;
								apiService
										.updateViewCount($scope.solutionId)
										.then(
												function(response) {
													$scope.status = response.status;
													$scope.detail = response.data.response_detail;
													$state.go('marketSolutions', {solutionId : id, revisionId : revisionId, parentUrl:'mymodel' });
												},
												function(error) {
													
													$state.go('marketSolutions', {solutionId : id, revisionId : revisionId, parentUrl:'mymodel'});
												});

							};
							$scope.updateViewCount();
						}
						
						if( JSON.parse(browserStorageService.getUserDetail()) ){
				            $scope.userDetails = JSON.parse(browserStorageService.getUserDetail());
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
						
						$scope.getUniqueSolutions=function(collection){
							var solutions = [];
							var output = [];
							angular.forEach(collection, function(item) {
								// we check to see whether our object exists
								var key = item['solutionId'];
								// if it's not already part of our keys array
								if (output.indexOf(key) === -1) {
									// push this item to our final output array
									output.push(item.solutionId);
									solutions.push(item);
								}
							});
							return solutions;
						}
						var count = 1;
						$scope.isBusy = false;
						var check = 0;
						var dataObj = {};

						var duplicate = false;
						$scope.imageUrls = {};
						
						$scope.loadMore = function(type) {
							if ($scope.isBusy)
								return;

							$scope.dataLoading = true;							

							if($scope.viewNoMLsolution == 'No More ML Solutions' && $scope.pageNumber != 0){return;}
							$scope.MlSoltionCount = false;
							
							
							dataObj = {
									  "request_body": {
										    "active": $scope.activeType,
										    "nameKeyword" :  $scope.toBeSearch,
										    "tags" : $scope.tagFilter,
										    "modelTypeCodes": $scope.categoryFilter,
										    "userId": $scope.loginUserID,
										    "sortBy": $scope.sortBy,
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumber,
										      "size" : $scope.defaultSize
										    }
										  }
										};
							
							if(type =='PR'){
								dataObj.request_body.published = false;
							}
							if(type =='PB'){
								dataObj.request_body.published = true;
							}
							if($scope.activeType == false){
								delete(dataObj.request_body.accessTypeCodes);
							}
							$scope.SetDataLoaded = true;
							$rootScope.setLoader = true;
							apiService.fetchUserSolutions(dataObj).then(function(response) {
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
								$scope.totalPages = response.data.response_body.pageCount;
											angular.forEach(response.data.response_body.content,function(value,key) {
												if(response.data.response_body.content[key].active){$scope.modelCount = $scope.modelCount+1;}
											});
												$scope.isBusy = false;$scope.MlSoltionCount = true;
												if($scope.pageNumber==0){
													$scope.mlsolutions = response.data.response_body.content;
													angular.forEach($scope.mlsolutions, function(value,key) {
														angular.forEach($scope.favouriteSolutions,
																function(mlsolutionValue,mlsolutionKey) {
																	if (mlsolutionValue.solutionId == value.solutionId) {
																		$scope.mlsolutions[key].selectFav = true;
																	}
														});
													});
												}
												
												if(response.data.response_body.content.length>=1){
													$scope.viewNoMLsolution = 'View More ML Solutions';
													}
												else {
													$scope.viewNoMLsolution = 'No More ML Solutions';
													}

												if(response.data.response_body.filteredTagSet.length > 0){
													for(var i=0;i< response.data.response_body.filteredTagSet.length;i++){
														var tag=response.data.response_body.filteredTagSet[i];
														$scope.tags.push(tag);
													}
													$scope.tags=$scope.getUniqueArrayElements($scope.tags);
												}

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
																		ratingCount: response.data.response_body.content[i].ratingCount,
																		onboardingStatusFailed: response.data.response_body.content[i].onboardingStatusFailed,
																		pendingApproval: response.data.response_body.content[i].pendingApproval,
																		latestRevisionId: response.data.response_body.content[i].latestRevisionId

																	});
															angular.forEach($scope.favouriteSolutions,
																	function(mlsolutionValue,mlsolutionKey) {
																		if (mlsolutionValue.solutionId == response.data.response_body.content[i].solutionId) {
																			$scope.mlsolutions[$scope.mlsolutions.length].selectFav = true;
																		}
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
																			}
																	});
															});
												});
												
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
												
												if(type == 'PB') {
													$scope.mlSolutionPublic =  $scope.getUniqueSolutions($scope.mlsolutions);
													$scope.totalPages = response.data.response_body.pageCount;
													if($scope.mlsolutions == 0){
														$scope.startPageSizePB = 0;
														$scope.endPageSizePB = 0;
													}
													else{
													$scope.loadpagePB = $scope.pageNumber;
													$scope.startPageSizePB = $scope.loadpagePB *  $scope.defaultSize + 1;
													$scope.endPageSizePB =  (($scope.loadpagePB + 1) * $scope.defaultSize) < $scope.totalPublicSolCount ?  (($scope.loadpagePB + 1) * $scope.defaultSize) : $scope.totalPublicSolCount
													}
												} else if(type == 'PR') {
													$scope.mlSolutionPrivate =  $scope.getUniqueSolutions($scope.mlsolutions);
													$scope.totalPages = response.data.response_body.pageCount;
													if($scope.mlsolutions == 0){
														$scope.startPageSizePR = 0;
														$scope.endPageSizePR = 0;
													}
													else{
														$scope.loadpagePR = $scope.pageNumber;
														$scope.startPageSizePR = $scope.loadpagePR *  $scope.defaultSize + 1;
														$scope.endPageSizePR =  (($scope.loadpagePR + 1) * $scope.defaultSize) < $scope.totalPrivateSolCount ?  (($scope.loadpagePR + 1) * $scope.defaultSize) : $scope.totalPrivateSolCount
														}
				
												} else if(type == 'N') {
													$scope.mlSolutionDelete =  $scope.getUniqueSolutions($scope.mlsolutions);
													$scope.totalPages = response.data.response_body.pageCount;
													if($scope.mlsolutions == 0){
														$scope.startPageSizeN = 0;
														$scope.endPageSizeN = 0;
													}
													else {
													$scope.loadpageN = $scope.pageNumber;
												    $scope.startPageSizeN = $scope.loadpageN *  $scope.defaultSize + 1;
													$scope.endPageSizeN = (($scope.loadpageN + 1) * $scope.defaultSize) < $scope.totalDeletedSolCount ? (($scope.loadpageN + 1) * $scope.defaultSize) : $scope.totalDeletedSolCount; 
												}
												}

											});
											
							count += 9;
						}
						
						$scope.getAvgRating = function(avgRatingValue){							   
							    if(avgRatingValue !== null)
								{
									var starPercentage = (avgRatingValue / 5) * 100;
									const starPercentageRounded = ($window.Math.round(starPercentage / 10) * 10);	
									return {"width" : + starPercentageRounded + "%"};										
								}
							  }
						 
						$scope.updateFavorite = function(solutionId, key, type){
							var favourite;
							if(type == 'PR'){
								favourite = $scope.mlSolutionPrivate[key].selectFav;
							} else if(type == 'PB'){
								favourite = $scope.mlSolutionPublic[key].selectFav;
							} else if(type == 'D'){
								favourite = $scope.mlSolutionDelete[key].selectFav;
							}
							
							var dataObj = {
									  "request_body": {
										    "solutionId": solutionId,
										    "userId": $scope.loginUserID
										  },
										  "request_from": "string",
										  "request_id": "string"
										}
							if(favourite){
								apiService.createFavorite(dataObj)
								.then(function(response) {
									$scope.favouriteSolutions.push({'solutionId':solutionId});
								});
							}else if(!favourite){
								apiService.deleteFavorite(dataObj)
								.then(function(response) {
									$scope.favouriteList();
								});
							}
						};
						
						$scope.Tag = false;
						$scope.slnID = null;
						$scope.showListTag = function(solutionID) {
							if($scope.slnID != solutionID )
								$scope.Tag = false;
						  $scope.Tag = !$scope.Tag;
						  $scope.slnID = solutionID;
						}
						
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
						
						//popup for error models
						$scope.showPopupPeer = function(name, details, status){
							$scope.errorModelName = name;
							$scope.errorModelDetails = details;
							$scope.errorModelStatus = status;
				        	  $mdDialog.show({
				        		  contentElement: '#errorModelPopup',
				        		  parent: angular.element(document.body),
				        		  clickOutsideToClose: true
				        	  });
				          };
				          
				        //Close popup
						$scope.closePoup = function(){
							$mdDialog.hide();
						};
						
						//Delete Error Model
						$scope.openDeleteConfirmPopup = function(privateSolutionModelDetails){
							$scope.deletePrModel = privateSolutionModelDetails;
							
							$mdDialog.show({
				        		  contentElement: '#confirmPopupDeleteModel',
				        		  parent: angular.element(document.body),
				        		  clickOutsideToClose: true
				        	  });
						}
						
						$scope.deleteErrorModel = function(){
							$scope.deletePrModel;
							
							var solution = {
								"request_body" : {
									"active" : false,
									"created" : $scope.deletePrModel.created,
									"name" : $scope.deletePrModel.name,
									"ownerId" : $scope.deletePrModel.ownerId,
									"solutionId" : $scope.deletePrModel.solutionId,
								}
							}

							apiService
									.updateSolutions(solution)
									.then(
											function(response) {
												$scope.status = response.status;
												$scope.detail = response.data.response_detail;
												$location.hash('md-manage-module-template');  
												$anchorScroll(); 
												$scope.closePoup();
												$scope.msg = "Solution deleted successfully.";
												$scope.icon = '';
												$scope.styleclass = 'c-success';
												$scope.showAlertMessage = true;
												$scope.getPrivateModels();
												$scope.getDeleteModels();
												$timeout(
														function() {
															$scope.showAlertMessage = false;
														}, 3500);
											},
											function(response) {
												$scope.msg = response.data.response_detail;
												$location.hash('md-manage-module-template');  
												$anchorScroll();
												$scope.closePoup();
												$scope.icon = 'report_problem';
												$scope.styleclass = 'c-warning';
												$scope.showAlertMessage = true;
												$scope.getPrivateModels();
												$scope.getDeleteModels();
												$timeout(
														function() {
															$scope.showAlertMessage = false;
														}, 2500);
											});
						
						}
						
					}
				});
