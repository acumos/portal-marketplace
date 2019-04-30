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
		.module('marketHome',['ui.bootstrap'])
		.component(
				'marketHome',
				{
					templateUrl : './app/marketplace-home/home.template.html',
					controller : function($scope, $rootScope, apiService, $window, $state, $http, $mdDialog, waitservice, browserStorageService) {
							  /*if(localStorage.getItem("homeRefresh") == 'Yes'){
								  localStorage.setItem("homeRefresh",'No');
								  $state.go("home");
								  location.reload();
							  };*/
							  $scope.myInterval = 3000;
							  $scope.noWrapSlides = false;
							  $scope.active = 0;
							  $scope.imgURLCL = "images/alarm.png";
							  $scope.imgURLRG = "images/image-classifier.png";
							  $scope.imgURLDT = "images/anomaly.png";
							  $scope.imgURLPR = "images/topology.png";
							  $scope.imgURLnull = "images/vmpredict2.png";
							  $scope.imageUrls = {};
							  $scope.mlsolution = [];
							  $scope.homeSolutions = [];
							  //$scope.loginUserID = "";
							  /*changes for demo - images shown according to the solution name*/
							  /*$scope.imgURLcommercial = "images/commercial_pixelate.jpg";
								$scope.imgURLemotion ="images/emotion_classifier.png";
								$scope.imgURLthreat ="images/threat_analytics.png";
								$scope.imgURLvideo ="images/video_analytics.png";
								$scope.imgURLdefault ="images/default-model.png";
								$scope.imgURLChat = "images/ChatBot.png";
								$scope.imgURLSensitive = "images/Sensitive.png";*/
								
							  //$scope.successStories = [];
							  $scope.banner = [];
							  var slides = $scope.slides = [];
							  var currIndex = 0;
							  var accessTypeFilter = ["PB"];
							  if (JSON.parse(browserStorageService.getUserDetail())) {accessTypeFilter = ["OR", "PB"];}
							  var dataObj = {
                                      "request_body" : {
                                          "sortBy" : "MR",
                                          "active": true,
                                          "accessTypeCodes": accessTypeFilter ,
                                          "pageRequest" : {
                                              "fieldToDirectionMap": { "modified" : "DESC" },
                                              "page" : 0,
                                              "size" : 8
                                          }
                                      }
                                  }							  							
							  
							  /* IOT Changes start */	
							 
							  $scope.$on("loadCaurosel",function(event,opt) {
								  	 $scope.loginUserID = opt.userId;
									 $scope.loadTopCarousel();									
								 });
							  
							  if (JSON.parse(browserStorageService.getUserDetail()))
								  {
								  $scope.loginUserID = JSON.parse(browserStorageService
											.getUserDetail())[1];
								  
								  }
							  else
							  {
								  $scope.loginUserID = "";
								  
							  }
							  $scope.loadTopCarousel = function() {													 
								 apiService
									.getcaurosalDetails($scope.loginUserID)
									.then(								 									
										function(data, status, headers, config) {
											if(data.data.response_body != null) {												
												var carouselConfig = data.data.response_body;												
												$scope.banner.slides = [];
												var index = 0;
												angular.forEach(carouselConfig, function (value,  vobj) {												      												     
												   var infoGraphicsSrc = "";
												   
												   var slideObj = value[Object.keys(value)[0]];													  
												      if(slideObj.slideEnabled === true || slideObj.slideEnabled === "true"){
													      var bannerSt = '<div class='; 
										                    if(slideObj.textAling == 'right'){
										                    	bannerSt = bannerSt + '"home-screen-lfimg" ';
										                    } else {
										                    	bannerSt = bannerSt + '"home-screen2" ';
										                    }
													                   
										                    bannerSt = bannerSt + 'style="background-image:url(/api/site/content/carouselImages/' + slideObj.bgImgKey + ') !important;background-color : '+ slideObj.bgColor +'">' + 
														    '<div class="slide-content">' +
														        '<div class="slide-text">' +
														            '<h4>' + slideObj.headline + '</h4>' +
														            '<p ng-if="slideObj.supportingContent">' + slideObj.supportingContent  +'</p>';
								                             if (slideObj.links.enableLink){
								                            	 if (!angular.isUndefined(slideObj.links.primary) && !angular.isUndefined(slideObj.links.primary) && !angular.isUndefined(slideObj.links.primary.address)){
							                                         if ((slideObj.links.primary.address).indexOf("modelerResource") != -1) {
							                                        	 if($rootScope.enableOnBoarding) 
							                                        		 bannerSt = bannerSt + '<md-button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" ' + 'ng-click=\"ctrl.go(\'' + slideObj.links.primary.address + '\');" >' + slideObj.links.primary.label + '</md-button>';
							                                         } else {
							                                        	 if(slideObj.links.primary.address == 'other') {
							                                        		 	bannerSt = bannerSt + '<md-button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" ' + 'href=\"' + slideObj.links.primary.url + '" target="_blank">' + slideObj.links.primary.label + '</md-button>';
						                                        		 } else {
						                                        			 bannerSt = bannerSt + '<md-button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" ' + 'ng-click=\"ctrl.go(\'' + slideObj.links.primary.address + '\');" >' + slideObj.links.primary.label + '</md-button>';
						                                        		 }
							                                         }
									                             }
						                                    
								                            	 if (!angular.isUndefined(slideObj.links.secondary) && !angular.isUndefined(slideObj.links.secondary) && !angular.isUndefined(slideObj.links.secondary.address)){
									                            	 if(slideObj.links.secondary.address) {
								                                         if ((slideObj.links.secondary.address).indexOf("modelerResource") != -1) {
								                                    		 if($rootScope.enableOnBoarding)
								                                    			 bannerSt = bannerSt + '<md-button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" ' + 'ng-click=\"ctrl.go(\'' + slideObj.links.secondary.address  + '\');" >' + slideObj.links.secondary.label + '</md-button>';
								                                         } else {
								                                        	 if(slideObj.links.secondary.address == 'other') {
								                                        		 	bannerSt = bannerSt + '<md-button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" ' + 'href=\"' + slideObj.links.secondary.url + '" target="_blank">' + slideObj.links.secondary.label + '</md-button>';
							                                        		 } else {
							                                        			 bannerSt = bannerSt + '<md-button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" ' + 'ng-click=\"ctrl.go(\'' + slideObj.links.secondary.address + '\');" >' + slideObj.links.secondary.label + '</md-button>';
							                                        		 }
								                                         }
							                                          }
								                            	 }													                                 
								                             }
	
								                             bannerSt = bannerSt + '</div> <div class="image-container" style="margin: 0 0 0 17px; float: right; width: 35%;"><img ng-show="' + slideObj.graphicImgEnabled + '" src="/api/site/content/carouselImages/' + slideObj.infoImgKey + '" alt="" title="" '
								                             	+ 'style="position: relative; top: 50%; left: 50%; transform: translate(-50%, -50%); width: unset; max-width: 100%;"/></div> </div> <div class="mountain"></div> </div>';
								                             $scope.banner.slides[index] = bannerSt;
								                             index++;
												      }
												});
											} else {
												//default slides if no config is present
												$scope.banner.slides = ['<div class="home-screen"><div class="slide-content"><div class="slide-text"><h4>We are Moving to a Future where AI is at the Center of Software.</h4><!-- <h6>the marketplace, the eco-system and the design studio</h6> -->    <p><span>{{$root.siteInstanceName}}</span> is the open-source framework for data scientists to build that future. <!-- <a href="#">Learn More >></a> --></p>    <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" alt="ADD YOUR MODEL NOW"  title=" ADD YOUR MODEL NOW" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'modelerResource\');" ng-if=\"$root.enableOnBoarding\">  ADD YOUR MODEL NOW    </button><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" alt="EXPLORE MARKETPLACE"  title=" EXPLORE MARKETPLACE" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'marketPlace\');"> <a style="color:#FFF" >EXPLORE MARKETPLACE</a></button></div><div class="image-container"><img src="images/banner_ml_graphics.png" alt="" title=""/></div></div><div class="mountain"></div></div>'];
											}
											//If all slides are disabled then show default slide
											if($scope.banner.slides.length == 0){
												$scope.banner.slides = ['<div class="home-screen"><div class="slide-content"><div class="slide-text"><h4>We are Moving to a Future where AI is at the Center of Software.</h4><!-- <h6>the marketplace, the eco-system and the design studio</h6> -->    <p><span>{{$root.siteInstanceName}}</span> is the open-source framework for data scientists to build that future. <!-- <a href="#">Learn More >></a> --></p>    <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" alt="ADD YOUR MODEL NOW"  title=" ADD YOUR MODEL NOW" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'modelerResource\');" ng-if=\"$root.enableOnBoarding\">  ADD YOUR MODEL NOW    </button><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" alt="EXPLORE MARKETPLACE"  title=" EXPLORE MARKETPLACE" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'marketPlace\');"> <a style="color:#FFF" >EXPLORE MARKETPLACE</a></button></div><div class="image-container"><img src="images/banner_ml_graphics.png" alt="" title=""/></div></div><div class="mountain"></div></div>'];
											}
										},
										function(error) {
											console.log(error);
								});
							  }
							  $scope.loadTopCarousel();	
						/* IOT Changes end */			
							  
							  
							  $scope.event = {'slides':[]};
							  apiService
								.getSiteConfig("event_carousel")
								.then(
										function(response) {
											if(response.data.response_body != null) {
												$scope.eventConfig = angular.fromJson(response.data.response_body.configValue);
												//alert (angular.toJson(carouselConfig[0]));
												
												var index = 0;
												angular.forEach($scope.eventConfig, function (value, key) {
													if(value['slideEnabled'] === true || value['slideEnabled'] === 'true'){
														var storyhtml = '<div class="hackathon-container">' + 
															'<div class="hackathon-image">' + 
															'<img src="/api/site/content/carouselImages/' + value['bgImgKey'] +'" onerror="this.style.display=\'none\'"/>' + 
															'</div>' +
															'<div class="hackathon-text">' + 
															'<h3>' + value["headline"] + '</h3>' + 
															'<p ng-if=" value[\'supportingContent\']">' + value["supportingContent"] + '</p>' +
															'</div>' + 
															'</div>';
														$scope.event.slides[index] = storyhtml;
														index++;
													}
													
												});
											}
										});
							  
							  $scope.successStories = {'slides':[]};
							  apiService
								.getSiteConfig("story_carousel")
								.then(
										function(response) {
											if(response.data.response_body != null) {
												$scope.storyConfig = angular.fromJson(response.data.response_body.configValue);
												var index = 0;
												setTimeout(function() {
												angular.forEach($scope.storyConfig, function (value, key) {
													if(value['slideEnabled'] === true || value['slideEnabled'] === 'true' && key != 'enabled'){
														var storyhtml = '<div class="mdl-cell mdl-cell--6-col mdl-cell--8-col-tablet mdl-cell--4-col-phone disinherit">' + 
//															'<div class="success-icon" ng-hide="'+!value["bgImageUrl"]+'" >' + 
//															'<img src="/site/binaries/content/gallery/acumoscms/global/story_carousel_bg/' + value["bgImageUrl"] +'"/>' + 
//															'</div>' +
															'<div class="success-description" ng-show="'+value['supportingContent']+'">' + 
															value["supportingContent"]  +
															'<p><b>' + value["authorName"]  +
															'</b></p></div></div>';
														$scope.successStories.slides[index] = storyhtml;
														$scope.$apply();
							                             index++;
													}
													
												});
											},0);
											}
										});
										
							  $scope.onCarouselBeforeChange = function(){
								  $scope.active = true;
								  //console.log("onCarouselBeforeChange()");
								  //console.log($scope.active);
							  }
							  angular.element($window).bind('orientationchange', function () {
								  getNoOfSolutionTile();
								  $rootScope.$broadcast('oreientationChange');
							  });
							  
							  function getNoOfSolutionTile(){
								  var innerWidth = $window.innerWidth;
								  if(innerWidth < 480){
									  $scope.noOfSolutiontile = 1;
								  }else if(innerWidth > 480 && innerWidth < 767){
									  $scope.noOfSolutiontile = 2;
								  }else{
									  $scope.noOfSolutiontile = 4;
								  }
							  }
							  getNoOfSolutionTile();						  
							  if(sessionStorage.getItem("provider") == "globaluid" ){
									 apiService.postGlobalUserDetails()
									 .then(function successCallback(response) {
		                                  console.log(response);
		                                  
		
		                            }, function errorCallback(response) {
		                                  console.log("Error: ", response);
		                                  $ctrl.userPassInvalid = true;
		                            });
		                         };
		                         
		                         /*$scope.go = function go( path ) {
		                        	 $state.go(path);
		                         };*/
		                         
		                         if ($scope.loginUserID != undefined && $scope.loginUserID != null && $scope.loginUserID != "") {
		                        	 apiService
										.insertSolutionDetail(dataObj)
										.then(
												function(response) {
													$scope.homeSolutions.slides = response.data.response_body.content;

													angular.forEach($scope.homeSolutions.slides,function( value, key) {
														if(value.solutionRatingAvg != null || value.solutionRatingAvg != undefined)
														{
															var starPercentage = (value.solutionRatingAvg / 5) * 100;
															const starPercentageRounded = ($window.Math.round(starPercentage / 10) * 10);	
															$scope.startRatingWidth =   starPercentageRounded + "%"  	;
															
														}
														 value.solutionRatingAvg = $scope.startRatingWidth;
													});
												},
												function(error) {
													$scope.status = 'Unable to load data: '
															+ error.data.error;
													
												});
		                         } else {
		                        	 apiService
										.insertPublicSolutionDetail(dataObj)
										.then(
												function(response) {
													$scope.homeSolutions.slides = response.data.response_body.content;

													angular.forEach($scope.homeSolutions.slides,function( value, key) {
														if(value.solutionRatingAvg != null || value.solutionRatingAvg != undefined)
														{
															var starPercentage = (value.solutionRatingAvg / 5) * 100;
															const starPercentageRounded = ($window.Math.round(starPercentage / 10) * 10);	
															$scope.startRatingWidth =   starPercentageRounded + "%"  	;
															
														}
														 value.solutionRatingAvg = $scope.startRatingWidth;
													});
												},
												function(error) {
													$scope.status = 'Unable to load data: '
															+ error.data.error;
													
												});
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
		 													$state.go('marketSolutions', {solutionId : id});
		 												},
		 												function(error) {
		 													$scope.status = 'Unable to load data: '
		 															+ error.data.error;
		 													console.log("Error: "+error.data);
		 													$state.go('marketSolutions', {solutionId : id});
		 												});
		 							};
		 							$scope.updateViewCount();
		 						}

		                         $scope.getCmsHomeScreenDiscoverContentTeamUP = function(name){
		                        	 var req = {
		 									method : 'GET',
		 									url : '/site/api-manual/Solution/solDescription?path=global/home-screen/discover-acumos&name='+name,
		 							};
		                        	 $http(req)
										.success(
												function(data, status, headers,
														config) {
													$scope.teamup = data.description;
												}).error(
														function(data, status, headers,
																config) {
															return "No Contents Available"
														});
								}
		                         
		                         $scope.getCmsHomeScreenDiscoverContentMarketPlace = function(name){
		                        	 var req = {
		 									method : 'GET',
		 									url : '/site/api-manual/Solution/solDescription?path=global/home-screen/discover-acumos&name='+name,
		 							};
		                        	 $http(req)
										.success(
												function(data, status, headers,
														config) {
													$scope.marketplace = data.description;
												}).error(
														function(data, status, headers,
																config) {
															return "No Contents Available"
														});
								}
		                         
		                         $scope.getCmsHomeScreenDiscoverContentDesignStudio = function(name){
		                        	 var req = {
		 									method : 'GET',
		 									url : '/site/api-manual/Solution/solDescription?path=global/home-screen/discover-acumos&name='+name,
		 							};
		                        	 $http(req)
										.success(
												function(data, status, headers,
														config) {
													$scope.designstudion = data.description;
												}).error(
														function(data, status, headers,
																config) {
															return "No Contents Available"
														});
								}
		                         
		                         $scope.getCmsHomeScreenDiscoverContentSDNONAP = function(name){
		                        	 var req = {
		 									method : 'GET',
		 									url : '/site/api-manual/Solution/solDescription?path=global/home-screen/discover-acumos&name='+name,
		 							};
		                        	 $http(req)
										.success(
												function(data, status, headers,
														config) {
													$scope.sdnonap = data.description;
												}).error(
														function(data, status, headers,
																config) {
															return "No Contents Available"
														});
								}
		                         
		                         $scope.getCmsHomeScreenDiscoverContentOnboard = function(name){
		                        	 var req = {
		 									method : 'GET',
		 									url : '/site/api-manual/Solution/solDescription?path=global/home-screen/discover-acumos&name='+name,
		 							};
		                        	 $http(req)
										.success(
												function(data, status, headers,
														config) {
													$scope.onboard = data.description;
												}).error(
														function(data, status, headers,
																config) {
															return "No Contents Available"
														});
								}
		                         
		                         $scope.getCmsHomeScreenSuccessStoriesexec = function(){

		                        	 		                        	 var req = {
		                        	 		 									method : 'GET',
		                        	 		 									url : '/site/api-manual/Solution/solDescription?path=global/home-screen/success-stories&name=attexec',
		                        	 		 							};
		                        	 		                        	 $http(req)
		                        	 										.success(
		                        	 												function(data, status, headers,
		                        	 														config) {
		                        	 													$scope.successStoryattexec = data.description;
		                        	 												}).error(
		                        	 														function(data, status, headers,
		                        	 																config) {
		                        	 															return "No Contents Available"
		                        	 														});
		                        	 								}
		                        	 		                         
		                        	 		                         $scope.getCmsHomeScreenSuccessStoriesSitemanager = function(){
		                        	 		                        	 var req = {
		                        	 		 									method : 'GET',
		                        	 		 									url : '/site/api-manual/Solution/solDescription?path=global/home-screen/success-stories&name=attsitemanager',
		                        	 		 							};
		                        	 		                        	 $http(req)
		                        	 										.success(
		                        	 												function(data, status, headers,
		                        	 														config) {
		                        	 													$scope.successStorysitemgr = data.description;
		                        	 												}).error(
		                        	 														function(data, status, headers,
		                        	 																config) {
		                        	 															return "No Contents Available"
		                        	 														});
		                        	 								}
		                        	 		                         
		                        	 		                         $scope.getCmsHomeScreenHackathon = function(){
		                        	 		                        	 var req = {
		                        	 		 									method : 'GET',
		                        	 		 									url : '/site/api-manual/Solution/solDescription?path=global/home-screen&name=hackathon',
		                        	 		 							};
		                        	 		                        	 $http(req)
		                        	 										.success(
		                        	 												function(data, status, headers,
		                        	 														config) {
		                        	 													$scope.hackathon = data.description;
		                        	 												}).error(
		                        	 														function(data, status, headers,
		                        	 																config) {
		                        	 															return "No Contents Available"
		                        	 														});
		                        	 								}
		                        	 		                         
		                        	 		                         
		                        	 		                         /*Check if LF id is disabld*/
		                        	 		                        $rootScope.$on('isLFAccDisabledEvent', function (event, data) {
		                        	 		                         if(data.active == "false"){
		                        	 		                        	$mdDialog.show({
		                        	 					                      contentElement: '#myLfDisabled',
		                        	 					                      parent: angular.element(document.body),
		                        	 					                      clickOutsideToClose: true
		                        	 					                    });
		                        	 		                         }
		                        	 		                       });
		                        	 		                        
		                        	 		                       $scope.closePoup = function(){
		                        	 			                	  $mdDialog.hide();
		                        	 			                  };
		                        	 		                       
		                        	 		                       $scope.navigateTo = function(){
		                        	 		                    	   if($rootScope.enableOnBoarding) 
		                        	 		                    		  $state.go("modularResource");
		                        	 		                    	   else return false;
		                        	 		                       };
		 						
								},
								
							
							  /*$scope.slides = [
							    {
							      image: './images/Screen Shot 2017-07-26 at 4.01.08 PM.png'
							    },
							    {
							      image: './images/Screen Shot 2017-07-26 at 4.04.24 PM.png'
							    },
							    {
							      image: './images/Screen Shot 2017-07-26 at 4.04.57 PM.png'
							    }
							  ];*/
							
					//}
					
				}).service('waitservice', function($http, $q) {

					this.waitForResponse = function(req) {
						var deffered = $q.defer();
						$http(req).success(function(data) {
							$q.resolve(data);

						}).error(function(response) {
							$q.reject(response);
						});

						return deffered.promise;
					}
				});

angular.module('marketHome').controller('homeCarouselController', ['$scope', '$state', function ($scope, $state) {
     
}]);


angular.module('marketHome')
.filter('to_trusted', ['$sce', function($sce){
    return function(text) {
        return $sce.trustAsHtml(text);
    };
}]);