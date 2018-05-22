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
							$timeout, $state, apiService) {
						$scope.autoHeight = true;
						$scope.hidePrivate = true;
						$scope.hidePublic = true;
						$scope.hideCompany = true;
						$scope.hideDelete = true;
						$scope.defaultSize = 4;
						$scope.seeAllSelected = false;
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
						$scope.actions = [ 	{name:"Most Liked",value:"ML"}, 
						                	{name:"Fewest Liked",value:"FL"},
						                	{name:"Most Downloaded",value:"MD"},
						                	{name:"Fewest Downloaded",value:"LD"}, 
						                	{name:"Highest Reach",value:"HR"} 
						                ];
						$scope.filterids = [ "001", "002", "003", "004", "005",
								"006" ];

						if (JSON.parse(localStorage.getItem("userDetail"))) {
							$scope.userDetails = JSON.parse(localStorage
									.getItem("userDetail"));
							$scope.userDetails.userName = $scope.userDetails[0];
							$scope.loginUserID = $scope.userDetails[1];
						}
						/*Start call for Sections*/
						var check = 0;
						var dataObj = {};
						var toBeSearch = [];
						$scope.pageNumber = 0;
						$scope.categoryFilter = [];
						$scope.privacyFilter = '';
						$scope.tagFilter = [];
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
							$scope.dataLoading = true;
							if($scope.searchBox!=null && $scope.searchBox!='')
								toBeSearch[0] = $scope.searchBox;
							$scope.privateDataLoaded = true;
							dataObj = {
									  "request_body": {
										    "accessTypeCodes": [
										      "PR"
										    ],
										    "active": true,
										    "tags" : $scope.tagFilter,
										    "nameKeyword" :  toBeSearch,
										    "modelTypeCodes": $scope.categoryFilter,
										    "ownerIds": [	$scope.loginUserID ],
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumPrivate,
										      "size" : $scope.defaultSize
										    }
										  }
										};
							
							apiService.insertSolutionDetail(dataObj).then(function(response) {
								var data = response.data;
								privatePrevTotal = privatePrevCounter;
								$scope.privateDataLoaded = false;
								
								$scope.mlSolutionPrivate = $scope.getUniqueSolutions(data.response_body.content);

								if($scope.mlSolutionPrivate.length != 0){
									privatePrevCounter = $scope.mlSolutionPrivate.length;
								}
								
								if($scope.prOperator == 'Add' || $scope.prOperator == undefined){
									$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivate.length + $scope.mlSolutionPrivateCount;
									if(toBeSearch.length > 0){
										$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivate.length;
									}
								}else if($scope.prOperator == 'Subtract'){
									$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivateCount - privatePrevTotal;
								}
								
								angular.forEach($scope.mlSolutionPrivate, function(value,key) {
									$scope.getSolutionImages(value.solutionId);
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
											
						
						$scope.getCompanyModels=function(){
							$scope.companyDataLoaded = true;
							if($scope.searchBox!=null && $scope.searchBox!='')
								toBeSearch[0] = $scope.searchBox;

							dataObj = {
									  "request_body": {
										    "accessTypeCodes": [
										      "OR"
										    ],
										    "active": true,
										    "tags" : $scope.tagFilter,
										    "nameKeyword" :  toBeSearch,
										    "modelTypeCodes": $scope.categoryFilter,
										    "ownerIds": [	$scope.loginUserID ],
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumCompany,
										      "size" : $scope.defaultSize
										    }
										  }
										};
							
							apiService.insertSolutionDetail(dataObj).then(function(response) {
								var data = response.data;
								companyPrevTotal = companyPrevCounter;
								$scope.companyDataLoaded = false;

								
								$scope.mlSolutionCompany = $scope.getUniqueSolutions(data.response_body.content);
								if($scope.mlSolutionCompany.length != 0){
									companyPrevCounter = $scope.mlSolutionCompany.length;
								}
								
								if($scope.cpOperator == 'Add' || $scope.cpOperator == undefined){
									$scope.mlSolutionCompanyCount = $scope.mlSolutionCompany.length + $scope.mlSolutionCompanyCount;
									if(toBeSearch.length > 0){
										$scope.mlSolutionCompanyCount = $scope.mlSolutionCompany.length;
									}
								}else if($scope.cpOperator == 'Subtract'){
									$scope.mlSolutionCompanyCount = $scope.mlSolutionCompanyCount - companyPrevTotal;
								}
								
								angular.forEach($scope.mlSolutionCompany, function(value,key) {
									$scope.getSolutionImages(value.solutionId);
									angular.forEach($scope.favouriteSolutions,
											function(mlsolutionValue,mlsolutionKey) {
												if (mlsolutionValue.solutionId == value.solutionId) {
													$scope.mlSolutionCompany[key].selectFav = true;
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
								if($scope.mlSolutionCompany.length <1){
									$scope.msg = "No Data";
									$scope.icon = 'info_outline';
									$scope.styleclass = 'c-info';
									$scope.companyAlertMessage = true;
									$timeout(function() {
										$scope.companyAlertMessage = false;
									}, 3500);
								}
								}, function(error) {
										$scope.isBusy = false
								});

							
						}; 
						
						
						$scope.getPublicModels=function(){
							$scope.dataLoading = true;
							$scope.publicDataLoaded = true;
							if($scope.searchBox!=null && $scope.searchBox!='')
								toBeSearch[0] = $scope.searchBox;
							
							dataObj = {
									  "request_body": {
										    "accessTypeCodes": [
										      "PB"
										    ],
										    "active": true,
										    "nameKeyword" :  toBeSearch,
										    "tags" : $scope.tagFilter,
										    "modelTypeCodes": $scope.categoryFilter,
										    "ownerIds": [	$scope.loginUserID ],
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumPublic,
										      "size" : $scope.defaultSize
										    }
										  }
										};
							
							apiService.insertSolutionDetail(dataObj).then(function(response) {
								var data = response.data;
							
								publicPrevTotal = publicPrevCounter;
								$scope.publicDataLoaded = false;
								
								$scope.mlSolutionPublic = $scope.getUniqueSolutions(data.response_body.content);
								if($scope.mlSolutionPublic.length != 0){
									publicPrevCounter = $scope.mlSolutionPublic.length;
								}
								
								if($scope.pbOperator == 'Add' || $scope.pbOperator == undefined){
									$scope.mlSolutionPublicCount = $scope.mlSolutionPublic.length + $scope.mlSolutionPublicCount;
									if(toBeSearch.length > 0){
										$scope.mlSolutionPublicCount = $scope.mlSolutionPublic.length;
									}
								}else if($scope.pbOperator == 'Subtract'){
									$scope.mlSolutionPublicCount = $scope.mlSolutionPublicCount - publicPrevTotal;
								}
								
								angular.forEach($scope.mlSolutionPublic, function(value,key) {
									$scope.getSolutionImages(value.solutionId);
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
							$scope.dataLoading = true;
							if($scope.searchBox!=null && $scope.searchBox!='')
							toBeSearch[0] = $scope.searchBox;
							$scope.deleteDataLoaded = true;
							dataObj = {
									  "request_body": {
										    "active": false,
										    "nameKeyword" :  toBeSearch,
										    "tags" : $scope.tagFilter,
										    "modelTypeCodes": $scope.categoryFilter,
										    "ownerIds": [	$scope.loginUserID ],
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumDelete,
										      "size" : $scope.defaultSize
										    }
										  }
										};
							
							apiService.insertSolutionDetail(dataObj).then(function(response) {
								publicPrevTotal = deletedPrevCounter;
								var data = response.data;

								$scope.deleteDataLoaded = false;
								$scope.mlSolutionDelete = $scope.getUniqueSolutions(data.response_body.content);

								if($scope.mlSolutionDelete.length != 0){
									deletedPrevCounter = $scope.mlSolutionDelete.length;
								}
								  
								if($scope.dlOperator == 'Add' || $scope.dlOperator == undefined){
									$scope.mlSolutionDeletedCount = $scope.mlSolutionDelete.length + $scope.mlSolutionDeletedCount;
									if(toBeSearch.length > 0){
										$scope.mlSolutionDeletedCount = $scope.mlSolutionDelete.length;
									}
								}else if($scope.dlOperator == 'Subtract'){
									$scope.mlSolutionDeletedCount = $scope.mlSolutionDeletedCount - publicPrevTotal;
								}
								
								angular.forEach($scope.mlSolutionDelete, function(value,key) {
									$scope.getSolutionImages(value.solutionId);
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
							$scope.getPrivateModels();
							$scope.getCompanyModels();
							$scope.getPublicModels();
							$scope.getDeleteModels();
                                                  	$rootScope.valueToSearch = '';
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
								$scope.privacyFilter = 'PB';
								if(!loadmore){
									$scope.mlsolutions.length = 0;
									$scope.pageNumber = 0;
								}else {
									$scope.pageNumPublic += 1;
									$scope.pageNumber++;
								}
								$scope.activeType = true;
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
								$scope.privacyFilter = 'OR';
								$scope.activeType = true;
								if(!loadmore){
									$scope.mlsolutions.length = 0;
									$scope.pageNumber = 0;
								}else {
									$scope.pageNumCompany += 1;
									$scope.pageNumber++;
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
								$scope.privacyFilter = 'PR';
								$scope.activeType = true;
								if(!loadmore){
									$scope.mlsolutions.length = 0;
									$scope.pageNumber = 0;
								} else {
									$scope.pageNumPrivate += 1;
									$scope.pageNumber++; 
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
								$scope.activeType = false;
								if(!loadmore){
									$scope.mlsolutions.length = 0;
									$scope.pageNumber = 0;
								}else {
									$scope.pageNumDelete += 1;
									$scope.pageNumber++;
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
							$scope.categoryFilter = [];
							$scope.privacyFilter = '';
							$scope.prOperator = 'Add';
							$scope.cpOperator = 'Add';
							$scope.pbOperator = 'Add';
							$scope.dlOperator = 'Add';
							$scope.mlSolutionPrivate=[];
							$scope.mlSolutionCompany=[];
							$scope.mlSolutionPublic=[];
							$scope.mlSolutionDelete=[];
							$scope.getPrivateModels();
							$scope.getCompanyModels();
							$scope.getPublicModels();
							$scope.getDeleteModels();
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
							$scope.mlSolutionCompanyCount = 0;
							$scope.mlSolutionDeletedCount = 0;
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
								$scope.mlSolutionCompanyCount = 0;
								$scope.mlSolutionDeletedCount = 0;
							} else if(type == 'tag'){/*
								if (tagArr.includes(checkbox))
									tagArr = tagArr.filter(val => val != checkbox);
								else tagArr.push(checkbox);
							*/
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
							getModels();
						}
						
						$scope.selectChip = function(index){
							$scope.selectedChip[index] = !$scope.selectedChip[index];
						};
						
						$scope.onClickModel = function(id, ownerId){

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
						var toBeSearch = [];
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
							if ($scope.isBusy)
								return;

							$scope.dataLoading = true;							
							if($scope.searchBox!=null && $scope.searchBox!='')
								toBeSearch[0] = $scope.searchBox;
							if($scope.viewNoMLsolution == 'No More ML Solutions' && $scope.pageNumber != 0){return;}
							$scope.MlSoltionCount = false;
							
							dataObj = {
									  "request_body": {
										    "accessTypeCodes": [$scope.privacyFilter],
										    "active": $scope.activeType,
										    "nameKeyword" :  toBeSearch,
										    "tags" : $scope.tagFilter,
										    "modelTypeCodes": $scope.categoryFilter,
										    "ownerIds": [ $scope.loginUserID ],
										    "sortBy": $scope.sortBy,
										    "pageRequest": {
										      "fieldToDirectionMap": $scope.fieldToSort,
										      "page" : $scope.pageNumber,
										      "size" : 9
										    }
										  }
										};
							if($scope.activeType == false){
								delete(dataObj.request_body.accessTypeCodes);
							}
							apiService.insertSolutionDetail(dataObj).then(function(response) {

											angular.forEach(response.data.response_body.content,function(value,key) {
												if(response.data.response_body.content[key].active){$scope.modelCount = $scope.modelCount+1;}
											});
												$scope.isBusy = false;$scope.MlSoltionCount = true;
												if($scope.pageNumber==0){
													$scope.mlsolutions = response.data.response_body.content;
													angular.forEach($scope.mlsolutions, function(value,key) {
														$scope.getSolutionImages(value.solutionId);
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
														},function(error) {
															$scope.status = 'Unable to load data: '
																+ error.data.error;
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
													$scope.mlSolutionPublicCount = $scope.mlSolutionPublic.length;
												} else if(type == 'PR') {
													$scope.mlSolutionPrivate =  $scope.getUniqueSolutions($scope.mlsolutions);
													$scope.mlSolutionPrivateCount = $scope.mlSolutionPrivate.length;
												} else if(type == 'OR') {
													$scope.mlSolutionCompany =  $scope.getUniqueSolutions($scope.mlsolutions);
													$scope.mlSolutionCompanyCount = $scope.mlSolutionCompany.length;
												} else if(type == 'N') {
													$scope.mlSolutionDelete =  $scope.getUniqueSolutions($scope.mlsolutions);
													$scope.mlSolutionDeletedCount = $scope.mlSolutionDelete.length;
												}

											},function(error) {
												$scope.status = 'Unable to load data: '
													+ error.data.error;
										});
											
							count += 9;
						}
						
						$scope.updateFavorite = function(solutionId, key, type){
							var favourite;
							if(type == 'PR'){
								favourite = $scope.mlSolutionPrivate[key].selectFav;
							} else if(type == 'PB'){
								favourite = $scope.mlSolutionPublic[key].selectFav;
							} else if(type == 'OR'){
								favourite = $scope.mlSolutionCompany[key].selectFav;
							}else if(type == 'D'){
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
										$scope.solutionsCount = response.data;
										
									},function(error) {
										$scope.status = 'Unable to load data: '+ error.data.error;
								});
						}
						$scope.getSolutionsCount();


					}
				
				});