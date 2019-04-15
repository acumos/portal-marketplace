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
		.module('catalog')
		.component(
				'catalog',
				{

					templateUrl : './app/catalog/catalog.template.html',
					controller : function($scope, $location, $http, $rootScope,
							$stateParams, $sessionStorage, $localStorage,
							$mdDialog, $state, $window, apiService, $anchorScroll, $timeout, $document, $filter, $sce, browserStorageService){									
					var user= JSON.parse(browserStorageService.getUserDetail());
						
						$scope.showAlertMessage = false;
						if(user) $scope.loginUserID = user[1];
						$scope.pageNumber = 0;
						$scope.totalPages = 0;
						$scope.allCatalogListLength = 0;
						$scope.requestResultSize = 10;

						$scope.setPageStart = 0;
                        $scope.selectedPage = 0;
                        $scope.showCatalogSection = false;
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
                              if(val == "next")
                                    if($scope.selectedPage == $scope.setPageStart + 4)
                                          {
                                          	$scope.setPageStart = $scope.setPageStart + 1;
                                          	$scope.selectedPage = $scope.selectedPage + 1;
                                          }
                                    else
                                    	$scope.selectedPage = $scope.selectedPage + 1;
                                    }                        										
			            
			            $scope.filterChange = function(pagination, size) {
			            	$scope.allCatalogList = [];
			            	$scope.allCatalogListLength = 0;
			            	$scope.requestResultSize = size;
			            	$scope.loadCatalog(0)
			            } 
			            //Access Type
						$scope.accessType = [{'name':'Select','value':'all'},{'name':'Public','value':'PB'},{'name':'Restricted','value':'RS'}]
						$scope.CatalogType = [{'name':'All Catalog','value':'all'},{'name':'My Catalog','value':'self'}]
						
						$scope.loadCatalog = function(pageNumber, filterValue) {							
							$scope.allCatalogList = [];													
							$scope.SetDataLoaded = true;
							$rootScope.setLoader = true;
							$scope.pageNumber = pageNumber;
							$scope.selectedPage = pageNumber;							
							var reqObject = {											  							
												"request_body": {
													"fieldToDirectionMap": {"created":"DESC"},
											        "page": pageNumber,
											        "size": $scope.requestResultSize
												  },
												  "request_from": "string",
												  "request_id": "string"
											};
						$scope.response_body=[];
						$scope.response_body = {								
									"status": null,
									"status_code": 0,
									"response_detail": "Catalog list fetched successfully",
									"response_code": null,
									"response_body": {
									   "content": [
									     {
									       "created": "2019-04-05T20:47:03Z",
									       "modified": "2019-04-05T20:47:03Z",
									       "catalogId": "12345678-abcd-90ab-cdef-1234567890ab",
									       "accessTypeCode": "PB",
									       "selfPublish": true,
									       "name": "Upgrade default catalog",
									       "publisher": "publisher",
									       "description": "test",
									       "origin": null,
									       "url": "http://localhost",
									       "solutionCount": 1
									     },
									     {
									       "created": "2019-04-11T21:21:43Z",
									       "modified": "2019-04-11T21:21:43Z",
									       "catalogId": "17364879-bb1e-4c6e-8564-e936e2ce6559",
									       "accessTypeCode": "PB",
									       "selfPublish": true,
									       "name": "att.entertainment.videos",
									       "publisher": "My company",
									       "description": "string",
									       "origin": "http://mirror.data.org/api",
									       "url": "http://peer.company.com/api",
									       "solutionCount": 0
									     }
									   ],
									   "number": 0,
									   "size": 20,
									   "totalElements": 2,
									   "pageable": {
									     "sort": {
									       "unsorted": true,
									       "sorted": false,
									       "empty": true
									     },
									     "offset": 0,
									     "pageSize": 20,
									     "pageNumber": 0,
									     "unpaged": false,
									     "paged": true
									   },
									   "sort": null,
									   "totalPages": 1,
									   "first": true,
									   "last": true,
									   "empty": false,
									   "numberOfElements": 2
									},
									"content": null,
									"error_code": "100"
									};

							
							$scope.allCatalogList = $scope.response_body.response_body.content;											
							$scope.totalPages = $scope.response_body.response_body.totalPages;
							$scope.totalElements = $scope.response_body.response_body.totalElements;
							$scope.allCatalogListLength = $scope.response_body.response_body.totalElements;
							$scope.SetDataLoaded = false;
							$rootScope.setLoader = false;
							/* apiService
							    .catalogList($scope.loginUserID, reqObject)
									.then(
											function successCallback(response) {
												$scope.allCatalogList = response.data.response_body;											
												$scope.totalPages = response.data.totalPages;
												$scope.totalElements = response.data.totalElements;
												$scope.allCatalogListLength = response.data.totalElements;
												$scope.SetDataLoaded = false;
												$rootScope.setLoader = false;																																																																																			
											},function errorCallback(response) {
												$scope.SetDataLoaded = false;
												$rootScope.setLoader = false;
											});	*/																		
						}
						if($scope.loginUserID)
							$scope.loadCatalog(0);	
						
						$scope.addCatalog = function()
						{							
							var request_body = {
								  "request_body": {
									"accessTypeCode": $scope.accessLevel,									
									"description": $scope.description,									
									"name": $scope.catalogName,																		
									"selfPublish": $scope.selfPublish,									
								  },
								  "request_from": "string",
								  "request_id": "string"
								}
								
							
							/*apiService
						    .addCatalog(reqObject)
								.then(
										function successCallback(response) {
											$scope.allCatalogList = response.data.response_body;											
											$scope.totalPages = response.data.totalPages;
											$scope.totalElements = response.data.totalElements;
											$scope.allCatalogListLength = response.data.totalElements;
											$scope.SetDataLoaded = false;
											$rootScope.setLoader = false;																																																																																			
										},function errorCallback(response) {
											$scope.SetDataLoaded = false;
											$rootScope.setLoader = false;
										});*/							
						};	
						$scope.ShowCatalog = function()
						{
							$scope.clearCatalog();
							$scope.showAddCatalog = true;
							$scope.showCatalogSection = !$scope.showCatalogSection;
						}
						$scope.clearCatalog = function()
						{
							$scope.accessLevel = "PB";									
							$scope.description = "";						
							$scope.catalogName = "";																	
							$scope.selfPublish = false;
							$scope.showAddCatalog = true;
						}
						$scope.editCatalog = function(Catalog)
						{
							$scope.showAddCatalog = false;
							$scope.showCatalogSection = true; 
							$scope.accessLevel = Catalog.accessTypeCode;
							$scope.description = Catalog.description;
							$scope.catalogName = Catalog.name;
							$scope.selfPublish= Catalog.selfPublish;
							
						};
						
						$scope.updateCatalog = function()
						{							
							var request_body = {
								  "request_body": {
									"accessTypeCode": $scope.accessLevel,									
									"description": $scope.description,									
									"name": $scope.catalogName,																		
									"selfPublish": $scope.selfPublish,									
								  },
								  "request_from": "string",
								  "request_id": "string"
								}
							
							/*apiService
						    .updateCatalog(reqObject)
								.then(
										function successCallback(response) {
											$scope.allCatalogList = response.data.response_body;											
											$scope.totalPages = response.data.totalPages;
											$scope.totalElements = response.data.totalElements;
											$scope.allCatalogListLength = response.data.totalElements;
											$scope.SetDataLoaded = false;
											$rootScope.setLoader = false;																																																																																			
										},function errorCallback(response) {
											$scope.SetDataLoaded = false;
											$rootScope.setLoader = false;
										});*/							
						};
						
						$scope.deleteCatalog = function(Catalog)
						{
							/*apiService
						    .deleteCatalog(Catalog.catalogId)
								.then(
										function successCallback(response) {
											$scope.allCatalogList = response.data.response_body;											
											$scope.totalPages = response.data.totalPages;
											$scope.totalElements = response.data.totalElements;
											$scope.allCatalogListLength = response.data.totalElements;
											$scope.SetDataLoaded = false;
											$rootScope.setLoader = false;																																																																																			
										},function errorCallback(response) {
											$scope.SetDataLoaded = false;
											$rootScope.setLoader = false;
										});*/							
						};	
						$scope.search = function(pageNumber, filterValue)
						{
							$scope.allCatalogList = [];													
							$scope.SetDataLoaded = true;
							$rootScope.setLoader = true;
							$scope.pageNumber = pageNumber;
							$scope.selectedPage = pageNumber;							
							var reqObject = {											  							
												"request_body": {
												"accessTypeCode": $scope.catalogLevel,
											    "pageRequest": {
												  "fieldToDirectionMap": {},
												  "page": pageNumber,
												  "size": $scope.requestResultSize
												 },													
												  "request_from": "string",
												  "request_id": "string"
												}
											};
							
							
							$scope.response_body =	{
								  "status": null,
								  "status_code": 0,
								  "response_detail": "Catalog list fetched successfully",
								  "response_code": null,
								  "response_body": {
									"content": [
									  {
										"created": "2019-04-05T20:47:03Z",
										"modified": "2019-04-05T20:47:03Z",
										"catalogId": "12345678-abcd-90ab-cdef-1234567890ab",
										"accessTypeCode": "PB",
										"selfPublish": false,
										"name": "Upgrade default catalog",
										"publisher": "publisher",
										"description": null,
										"origin": null,
										"url": "http://localhost"
									  }
									],
									"number": 0,
									"size": 20,
									"totalElements": 1,
									"pageable": {
									  "sort": {
										"sorted": false,
										"unsorted": true,
										"empty": true
									  },
									  "offset": 0,
									  "pageNumber": 0,
									  "pageSize": 20,
									  "paged": true,
									  "unpaged": false
									},
									"sort": null,
									"totalPages": 1,
									"first": true,
									"last": true,
									"empty": false,
									"numberOfElements": 1
								  },
								  "content": null,
								  "error_code": "100"
								}
							
							$scope.allCatalogList = $scope.response_body.response_body.content;											
							$scope.totalPages = $scope.response_body.response_body.totalPages;
							$scope.totalElements = $scope.response_body.response_body.totalElements;
							$scope.allCatalogListLength = $scope.response_body.response_body.totalElements;
							$scope.SetDataLoaded = false;
							$rootScope.setLoader = false;
							
							/*apiService
						    .searchCatalog(Catalog.catalogId)
								.then(
										function successCallback(response) {
											$scope.allCatalogList = response.data.response_body;											
											$scope.totalPages = response.data.totalPages;
											$scope.totalElements = response.data.totalElements;
											$scope.allCatalogListLength = response.data.totalElements;
											$scope.SetDataLoaded = false;
											$rootScope.setLoader = false;																																																																																			
										},function errorCallback(response) {
											$scope.SetDataLoaded = false;
											$rootScope.setLoader = false;
										});*/							
						};					
					}
				
				});



