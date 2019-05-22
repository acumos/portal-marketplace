/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
angular.module('catalog')
		.component('catalog', {
			templateUrl : './app/catalog/catalog.template.html',
			controller : function($scope, $location, $http, $rootScope,
					$stateParams, $sessionStorage, $localStorage,
					$mdDialog, $state, $window, apiService, $anchorScroll, $timeout, $document, $filter, $sce, browserStorageService) {									
				
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
                
				$scope.setStartCount = function(val) {
					if (val == "preBunch") {
						$scope.setPageStart = $scope.setPageStart - 5
					} else if (val == "nextBunch") {
						$scope.setPageStart = $scope.setPageStart + 5
					} else if (val == "pre") {
						if ($scope.selectedPage == $scope.setPageStart) {
							$scope.setPageStart = $scope.setPageStart - 1;
							$scope.selectedPage = $scope.selectedPage - 1;
						} else {
							$scope.selectedPage = $scope.selectedPage - 1;
						}
					} else if (val == "next")
						if ($scope.selectedPage == $scope.setPageStart + 4) {
							$scope.setPageStart = $scope.setPageStart + 1;
							$scope.selectedPage = $scope.selectedPage + 1;
						} else {
							$scope.selectedPage = $scope.selectedPage + 1;
						}
				};
	            
	            $scope.filterChange = function(pagination, size) {
	            	$scope.allCatalogList = [];
	            	$scope.allCatalogListLength = 0;
	            	$scope.requestResultSize = size;
	            	$scope.loadCatalog(0);
	            }	            
	            // Access Type
				$scope.accessType = [{'name':'Public','value':'PB'},{'name':'Restricted','value':'RS'}]
				$scope.CatalogType = [{'name':'All Catalog','value':'all'},{'name':'My Catalog','value':'self'}]				
				$scope.orderByField = 'created'; $scope.reverseSortcatalog = true;
				
				
				
				$scope.loadCatalog = function(pageNumber, filterValue) {							
					$scope.allCatalogList = [];													
					$scope.SetDataLoaded = true;
					$rootScope.setLoader = true;
					$scope.pageNumber = pageNumber;
					$scope.selectedPage = pageNumber;							
					var reqObject = {											  							
						"request_body": {
							"fieldToDirectionMap": {"created":"ASC"},
					        "page": pageNumber,
					        "size": $scope.requestResultSize
						  }
					};
					apiService.getCatalogs(reqObject)
						.then(
							function successCallback(response) {
								var resp = response.data.response_body;
								$scope.allCatalogList = resp.content;											
								$scope.totalPages = resp.totalPages;
								$scope.totalElements = resp.totalElements;
								$scope.allCatalogListLength = resp.totalElements;
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
							}, function errorCallback(response) {
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
							});
				};
				
				if($scope.loginUserID)
					$scope.loadCatalog(0);	
				
				// styleclass ------ icons
				// c-success ------- 
				// c-warning ------- report_problem
				// c-error --------- report_problem
				// c-info ---------- info_outline

				$scope.setAlertMessage = function(msg, styleclass, icon) {
					$scope.msg = msg;
					if(icon){
						$scope.icon = icon;
					}else{
						$scope.icon = '';
					}
					
					$scope.styleclass = styleclass;
					$scope.showAlertMessage = true;
					$timeout(function() {
							$scope.showAlertMessage = false;
						}, 3000);
				};
				
				$scope.createCatalog = function() {
					var reqObject = {
						"request_body" : {
							"name" : $scope.catalog.name,
							"accessTypeCode" : $scope.catalog.accessTypeCode,
							"description" : $scope.catalog.description,
							"selfPublish" : $scope.catalog.selfPublish || false,
							"publisher" : $rootScope.siteInstanceName,
							"url" : "http://localhost"
						}
					};
					
					apiService.createCatalog(reqObject).then(
						function successCallback(response) {
							$scope.setAlertMessage("Catalog \"" + reqObject.request_body.name + "\" created successfully.", 'c-success');
							$mdDialog.hide();
							$scope.loadCatalog(0);
						}, function errorCallback(response) {
							$scope.setAlertMessage("Error occurred while creating catalog \"" + reqObject.request_body.name + "\".", 'c-error', 'report_problem');
							console.error(response);
							$mdDialog.hide();
							$scope.SetDataLoaded = false;
							$rootScope.setLoader = false;
						});
				};
				
				$scope.clearCatalog = function() {
					$scope.catalog = {};
				}
				
				$scope.isValidCatalog = function(catalog) {
					return (catalog.name && catalog.accessTypeCode && catalog.description);
				}
				
				$scope.openCatalogDialog = function() {
					$mdDialog.show({
						templateUrl : '../app/catalog/catalog-modal.template.html',
						clickOutsideToClose : true,
						locals: { parent: $scope },
						controller : function DialogController($scope, parent) {
							$scope.parent = parent;
							$scope.closePoup = function(){
								$mdDialog.hide();
							}
						}
					});
				}
				
				$scope.addCatalog = function() {									
					$scope.clearCatalog();
					$scope.isEdit = false;
					$scope.openCatalogDialog();
				}
				
				$scope.editCatalog = function(catalog) {
					$scope.isEdit = true;
					$scope.catalog = jQuery.extend(true, {}, catalog);
					$scope.openCatalogDialog();
				};
				
				$scope.updateCatalog = function() {							
					var reqObject = {
						"request_body" : {
							"catalogId" : $scope.catalog.catalogId,
							"name" : $scope.catalog.name,
							"accessTypeCode" : $scope.catalog.accessTypeCode,
							"description" : $scope.catalog.description,
							"selfPublish" : $scope.catalog.selfPublish,
							"publisher" : $scope.catalog.publisher,
							"url" : $scope.catalog.url
						}
					};
					
					apiService.updateCatalog(reqObject).then(
							function successCallback(response) {
								$scope.setAlertMessage("Catalog \"" + reqObject.request_body.name + "\" updated successfully.", 'c-success');
								$mdDialog.hide();
								$scope.loadCatalog(0);
							}, function errorCallback(response) {
								$scope.setAlertMessage("Error occurred while updating catalog \"" + reqObject.request_body.name + "\".", 'c-error', 'report_problem');
								console.error(response);
								$mdDialog.hide();
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
							});							
				};
				
				$scope.deleteCatalog = function(catalog) {
					apiService.deleteCatalog(catalog.catalogId).then(
							function successCallback(response) {
								$scope.setAlertMessage("Catalog \"" + catalog.name + "\" successfully deleted.", 'c-success');
								$mdDialog.hide();
								$scope.loadCatalog(0);
							}, function errorCallback(response) {
								$scope.setAlertMessage("Error occurred while deleting catalog \"" + catalog.name + "\".", 'c-error', 'report_problem');
								console.error(response);
								$mdDialog.hide();
								$scope.SetDataLoaded = false;
								$rootScope.setLoader = false;
							});							
				};
				
				$scope.showDeleteDialog = function(catalog) {
					$mdDialog.show({
						templateUrl : '../app/catalog/catalog-delete-modal.template.html',
						clickOutsideToClose : true,
						locals: { parent: $scope, catalog: catalog },
						controller : function DialogController($scope, parent, catalog) {
							$scope.parent = parent;
							$scope.catalog = catalog;
							$scope.closePoup = function(){
								$mdDialog.hide();
							}
						}
					});
				}
	            $scope.showViewDetailsPopup = function(ev,catalog){
	            	
	            	$scope.mdDescription = catalog.description;
	            	$scope.mdname = catalog.name;
	            	$scope.mdpublisher = catalog.publisher;
	            	$scope.mdsolutionCount = catalog.solutionCount;
	            	$scope.mdaccessTypeCode = catalog.accessTypeCode;
	            	$scope.mdselfPublish = catalog.selfPublish;
	            	
	            	$mdDialog.show({
	          		  contentElement: '#mdlViewDetails',
	          		  parent: angular.element(document.body),
	          		  targetEvent: ev,
	          		  clickOutsideToClose: true
	          	  });
	            }
	            
	            $scope.mdClosePoup = function(){
					$mdDialog.hide();
				}
				
				/*$scope.search = function(pageNumber, filterValue)
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
					
					apiService
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
								});							
				};		*/			
			}
		});