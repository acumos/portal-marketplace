'use strict';

angular
		.module('marketPlace', [ 'infinite-scroll' ])
		.component(
				'marketPlace',
				{
					templateUrl : '/app/market_place/market-place.template.html',
					controller : function($scope, $compile, $location, $http,
							$state, $stateParams, $sessionStorage, $rootScope,
							apiService, $element) {
						$scope.autoHeight = true;
						$scope.tags = [];
					    $element.find('input').on('keydown', function(ev) {
					          ev.stopPropagation();
					    });
					    
					    $scope.viewAllSolutions = function(){
					    	angular.element('.md-select-menu-container').addClass('ddl-column');
					    }
					    
						$scope.searchChange = function(index){
							$scope.mlsolutions = [ $scope.solutionIds[index] ]; 
						};
						
						if(localStorage.getItem("viewMarktetPlace")){
							  if(localStorage.getItem("viewMarktetPlace") == 'false')$scope.Viewtile = false;else $scope.Viewtile = true;
							}else $scope.Viewtile = true;
							
							$scope.$watch('Viewtile', function() {
								   localStorage.setItem("viewMarketPlace", $scope.Viewtile);
								});
						
							$rootScope.urlPath = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);
							console.log("market: "+$rootScope.urlPath)
							if ($rootScope.urlPath == 'manageModule') {
								$scope.parentUrl = false
							} else {
								$scope.parentUrl = true
							}
							$stateParams.parentUrl = 'market place';
						//localStorage.setItem('HeaderNameVar','');
						if ($rootScope.valueToSearch == undefined
								|| $rootScope.valueToSearch == null) {
						} else if ($rootScope.valueToSearch) {
							$scope.searchBox = $rootScope.valueToSearch;
						}
						$scope.mlsolutions = [];
						$scope.pageNumber = 0;$scope.modelCount = 0;

						console.log("market-place-component");
						$scope.actions = [ 	{name:"Most Liked",value:"ML"}, 
						                	{name:"Fewest Liked",value:"FL"},
						                	{name:"Most Downloaded",value:"MD"},
						                	{name:"Fewest Downloaded",value:"FD"},
						                	{name:"Highest Reach",value:"HR"},
						                	{name:"Lowest Reach",value:"LR"},
						                	{name:"Most Recent",value:"MR"},
						                	{name:"Older",value:"OLD"}
						                ];

						$scope.checkBox = [ "Private", "Shared", "Company",
								"Public" ];

						// Rest API for category
						/*
						 * $http({ method : 'GET', url :
						 * '/api/filter/modeltype', }).success(function(data,
						 * status, headers, config) { $scope.category =
						 * data.response_body; }).error(function(data, status,
						 * headers, config) { console.log(status); });
						 */

						apiService
								.getModelTypes()
								.then(
										function(response) {
											$scope.category = response.data.response_body;
										},
										function(error) {
											$timeout(function() {
												$scope.handleError = true;
											}, 2000);
											$scope.handleError = false;
											$scope.status = 'Unable to load data: '
													+ error.data.error;
											console.warn($scope.status)
										});

						// Rest call for data fetching
						// API calls
						var counter = 0;
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
						
						$scope.isBusyOnLoad = false;
						$scope.pageNumber = 0;
						$scope.onLoad = function() {/*
													 * debugger;
													 * if($scope.isBusyOnLoad){return}
													 * $scope.isBusyOnLoad =
													 * true; // if(tempVar ==
													 * "marketPlace"){ var
													 * dataObj = {
													 * "request_body" : { "page" :
													 * $scope.page, "size" :
													 * '8', "sortingOrder" :
													 * "ASC", //"searchTerm" :
													 * "Solution", }, }; {
													 * "request_body": {
													 * "modelType":
													 * $scope.categoryFilter,
													 * "page":
													 * $scope.pageNumber,
													 * "searchTerm":
													 * $scope.searchBox,
													 * "sortingOrder" : "ASC",
													 * "size": 8 },
													 * "request_from": "string",
													 * "request_id": "string" };
													 * console.log(angular.toJson(dataObj));
													 * $http({ method : 'POST',
													 * url : '/api/solutions',
													 * data : dataObj,
													 * }).success(function(data,
													 * status, headers, config) {
													 * console.log(data);
													 * //$scope.mlsolutions =
													 * data; $scope.mlsolutions =
													 * data.response_body.content;
													 * $scope.isBusy = false;
													 * $scope.pageNumber += 1;
													 * 
													 * if (data.response_body ==
													 * null) {
													 * //$scope.noDataFound =
													 * true } else {
													 * console.log(data);
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
													 * }).error(function(data,
													 * status, headers, config) {
													 * console.log(status);
													 * $scope.isBusy = false;
													 * });
													 * 
													 */
							// need to be changed at the timeof commit
									var requestObject = {
											"request_body" : {
												"active" : true,							
											    "pageRequest": {
											        "page": 0,
											        "size": 7
											      }
											}
									}

							apiService
									.insertSolutionDetail(requestObject)
									.then(
											function(response) {
												$scope.solutionIds = [];
												angular.forEach(response.data.response_body.content,function(value,key) {
													$scope.solutionIds.push(response.data.response_body.content[key]);
												});
											},
											function(error) {
												$scope.status = 'Unable to load data: '
														+ error.data.error;
												console.log($scope.status);
											});
						}
						$scope.onLoad();

						if (JSON.parse(localStorage.getItem("userDetail"))) {
							$scope.userDetails = JSON.parse(localStorage
									.getItem("userDetail"));
							$scope.userDetails.userName = $scope.userDetails[0];
							$scope.loginUserID = $scope.userDetails[1];
						}
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
						
						
						// Pagination || Load more
						// var counter = 0;
						$scope.dataLoading = false;

						// filter functionality
						$scope.actionFilter = function(action) {
							alert(action);
						}
						// check
						var count = 1;
						$scope.isBusy = false;
						var check = 0;
						var dataObj = {};
						var toBeSearch = '';
						var duplicate = false;
						$scope.loadMore = function() {
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
							/*dataObj = {
								"request_body" : {
									"modelType" : $scope.categoryFilter,
									"activeType" : 'Y',
									"page" : $scope.pageNumber,
									"searchTerm" : toBeSearch,
									"sortingOrder" : "ASC",
									"sortBy":$scope.sortBy,
									//"sortById":$scope.sortById,
									"size" : 9
								},
								"request_from" : "string",
								"request_id" : "string"
							}*/
							dataObj = {
									"request_body" : {
										"modelTypeCodes" : $scope.categoryFilter,
										"active" : true,
										"nameKeyword" : toBeSearch,
										"sortBy":$scope.sortBy,
									    "pageRequest": {
									        "page": $scope.pageNumber,
									        "size": 9
									      }
									}
							}
							console.log(angular.toJson(dataObj));

							apiService
									.insertSolutionDetail(dataObj)
									.then(
											function(response) {
												getSolution(response);
											},
											function(error) {
												$scope.status = 'Unable to load data: '
														+ error.data.error;
												console.log($scope.status);
											});
							count += 9;
						}
						if($rootScope.relatedModelType){
							$scope.categoryFilter = $rootScope.relatedModelType;
						}

						$scope.loadMore();
						function getSolution(response){
							angular.forEach(response.data.response_body.content,function(value,key) {
								if(response.data.response_body.content[key].active){$scope.modelCount = $scope.modelCount+1;}
							});
								$scope.isBusy = false;$scope.MlSoltionCount = true;
								if($scope.pageNumber==0){
									$scope.mlsolutions = response.data.response_body.content
									}
								if(response.data.response_body.content.length==9){$scope.viewNoMLsolution = 'View More ML Solutions';}
								else {$scope.viewNoMLsolution = 'No More ML Solutions'; $rootScope.valueToSearch = '';}
								$scope.tags = ($scope.tags).concat(response.data.response_body.filteredTagSet);

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
														solutionRating : response.data.response_body.content[i].ratingAverageTenths,
														ownerName: response.data.response_body.content[i].ownerName,
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
									
									if($scope.loginUserID){
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
									}

								} /*
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
						}
						var privacyArr = [];
						var caegoryArr = [];
						var url = 'solutions';
						var categoryUrl = '';
						var sortByUrl = '';
						var sortByIdUrl = '';
						var searchUrl = '';
						$scope.filterChange = function(checkbox, type) {
							$scope.mlsolutions = [];
							$scope.pageNumber = 0;
							$scope.modelCount = 0;
							$scope.isBusy = false;
							if (type == 'solution') {

							}
							/*
							 * else if(type == 'privacy'){ var bool = false; for
							 * (var j=0; j<privacyArr.length; j++) { if
							 * (privacyArr[j].match(checkbox)){
							 * privacyArr.splice(j, 1); bool = true; break; };}
							 * if(bool == false){ privacyArr.push(checkbox); }
							 * console.log(privacyArr); }
							 */
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
								/*angular.forEach(caegoryArr,
										function(value, key) {
											categoryUrl = categoryUrl + value
													+ ",";
										});
								categoryUrl = categoryUrl.slice(0, -1);*/
							}

							//$scope.categoryFilter = categoryUrl;
							$scope.categoryFilter = caegoryArr;
							if(type == 'sortBy'){$scope.sortBy = checkbox.value; $scope.selectedAction = checkbox.name; }else if(type == 'sortById')$scope.sortById = checkbox.value;
							/*
							 * else if(type == 'sortBy'){ sortByUrl='';url='';
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
							 */

							/*
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
							 * console.log('url');console.log(url);
							 */
							$scope.loadMore();

						}
						$scope.$on('scanner-started', function(event, args) {
							$scope.pageNumber = 0;
							$scope.searchBox = args.searchValue;
							$scope.loadMore();
							// do what you want to do
						});

						
						$scope.onClickModel = function(index, id) {
							/*if ($scope.loginUserID == $scope.mlsolutions[index].ownerId) {
								$state.go('modelEdit', {
									solutionId : id
								});
							} else {*/

								$scope.updateViewCount = function() {
									$scope.solutionId = id;
									apiService
											.updateViewCount($scope.solutionId)
											.then(
													function(response) {
														$scope.status = response.status;
														$scope.detail = response.data.response_detail;
														$rootScope.load = true;
														$state.go('marketSolutions', {solutionId : id, parentUrl: 'marketplace'});
														
													},
													function(error) {
														$scope.status = 'Unable to load data: '
																+ error.data.error;
														console.log("Error: "+error.data);
														$rootScope.load = true;
														$state.go('marketSolutions', {solutionId : id, parentUrl: 'marketplace'});
														
													});

								}
								
								
								//Don't call update view count if user not logged in
								if($scope.loginUserID){
									$scope.updateViewCount();
								}else{
									$state.go('marketSolutions', {solutionId : id, parentUrl: 'marketplace'});
								}
								
								//$state.go('marketSolutions', {solutionId : id});
							//}
						}
						
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
						
					}

				}).config([ '$compileProvider', function($compileProvider) {
			$compileProvider.debugInfoEnabled(false);
		} ]).filter('unique', function() {
   // we will return a function which will take in a collection
   // and a keyname
   return function(collection, keyname) {
      // we define our output and keys array;
      var output = [], 
          keys = [];
      
      // we utilize angular's foreach function
      // this takes in our original collection and an iterator function
      angular.forEach(collection, function(item) {
          // we check to see whether our object exists
          var key = item[keyname];
          // if it's not already part of our keys array
          if(keys.indexOf(key) === -1) {
              // add it to our keys array
              keys.push(key); 
              // push this item to our final output array
              output.push(item);
          }
      });
      // return our array which should be devoid of
      // any duplicates
      return output;
   };
});