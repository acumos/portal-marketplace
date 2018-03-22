'use strict';

angular
		.module('marketHome',['ui.bootstrap'])
		.component(
				'marketHome',
				{
					templateUrl : './app/marketplace-home/home.template.html',
					controller : function($scope, $rootScope, apiService, $window, $state, $http) {
						console.log("market-home")
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
							  
							  /*changes for demo - images shown according to the solution name*/
							  /*$scope.imgURLcommercial = "images/commercial_pixelate.jpg";
								$scope.imgURLemotion ="images/emotion_classifier.png";
								$scope.imgURLthreat ="images/threat_analytics.png";
								$scope.imgURLvideo ="images/video_analytics.png";
								$scope.imgURLdefault ="images/default-model.png";
								$scope.imgURLChat = "images/ChatBot.png";
								$scope.imgURLSensitive = "images/Sensitive.png";*/
								
							  $scope.successStories = [];
							  $scope.banner = [];
							  var slides = $scope.slides = [];
							  var currIndex = 0;
							  var dataObj = {
                                      "request_body" : {
                                          "sortBy" : "MR",
                                          "active": true,
                                          "accessTypeCodes": ["PB", "OR"],
                                          "pageRequest" : {
                                              "fieldToDirectionMap": { "modified" : "DESC" },
                                              "page" : 0,
                                              "size" : 8
                                          }
                                      }
                                  }
							  $scope.successStories.slides = [{story :'I needed to better position my ads in streaming video to improve my results.  The Acumos Design studio allowed me to quickly insert my video stream to a working model and use it ...',
			  												   from : 'AT&T Entertainment Exec'},	
			  												   {story :'I used the Threat Analytics model in Acumos.  I replaced the alerting component with one that let me immediately shut down servers if the threat prediction exceeded my threshold.  So easy to use!',
			  												    from : 'AT&T Site Manager'},
			  												    /*{story :'I used the Threat Analytics model in Acumos.  I replaced the alerting component with one that let me immediately shut down servers if the threat prediction exceeded my threshold.  So easy to use!',
			  												     from : 'AT&T Site Manager'},
			  												    {story :'I used the Threat Analytics model in Acumos.  I replaced the alerting component with one that let me immediately shut down servers if the threat prediction exceeded my threshold.  So easy to use!',
			  												    from : 'AT&T Site Manager'}*/
			  												     ];
							  
							  /*$scope.banner.slides = ['<div class="home-screen"><div class="slide-content"><div class="slide-text"><h4>We are Moving to a Future where AI is at the Center of Software.</h4><!-- <h6>the marketplace, the eco-system and the design studio</h6> -->    <p><span>{{$root.siteInstanceName}}</span> is the open-source framework for data scientists to build that future. <!-- <a href="#">Learn More >></a> --></p>    <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'modelerResource\');" ng-if=\"$root.enableOnBoarding\">  ADD YOUR MODEL NOW    </button><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'marketPlace\');"> <a style="color:#FFF" >EXPLORE MARKETPLACE</a></button></div><div class="image-container"><img src="images/banner_ml_graphics.png" alt="" title=""/></div></div><div class="mountain"></div></div>',
			  					  					  '<div class="home-screen1"><div class="slide-content"><div class="slide-text"><h4>Use the Design Studio to make AI the center of your new software.</h4><p>In <span>{{$root.siteInstanceName}}</span>, every model is a Micro-service, so models can be chained together along with data tools to create custom end-to-end solutions.</p><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'modelerResource\');" ng-if=\"$root.enableOnBoarding\">ADD YOUR MODEL NOW</button><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'marketPlace\');" >EXPLORE MARKETPLACE</button></div></div><div class="mountain"></div></div>',
			  					  					  '<div class="home-screen2"><div class="slide-content"><div class="slide-text"><h4><span>{{$root.siteInstanceName}}</span> is an AI Ecosystem</h4><p>Models are easily uploaded and packaged for the catalog. Model users meet modelers in the marketplace to test-drive, train and engage.</p><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'modelerResource\');" ng-if=\"$root.enableOnBoarding\">ADD YOUR MODEL NOW</button><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'marketPlace\');">EXPLORE MARKETPLACE</button></div></div><div class="mountain"></div></div>',
			  					  					  '<div class="home-screen3"><div class="slide-content"><div class="slide-text"><h4>We Speak Machine Learning</h4><p><span>{{$root.siteInstanceName}}</span> support all major AI toolkits.  Models from diﬀerent sources can be combined to create composite solutions. </p><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'modelerResource\');" ng-if=\"$root.enableOnBoarding\">ADD YOUR MODEL NOW</button><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'marketPlace\');">EXPLORE MARKETPLACE</div></div><div class="mountain"></div></div>'
			  					  ];*/
							  $scope.banner.slides = ['<div class="home-screen"><div class="slide-content"><div class="slide-text"><h4>We are Moving to a Future where AI is at the Center of Software.</h4><!-- <h6>the marketplace, the eco-system and the design studio</h6> -->    <p><span>{{$root.siteInstanceName}}</span> is the open-source framework for data scientists to build that future. <!-- <a href="#">Learn More >></a> --></p>    <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect active" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'modelerResource\');" ng-if=\"$root.enableOnBoarding\">  ADD YOUR MODEL NOW    </button><button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect" ng-mouseover = "ctrl.active= \'true\'" ng-mouseleave = "ctrl.initUI()" ng-click="ctrl.go(\'marketPlace\');"> <a style="color:#FFF" >EXPLORE MARKETPLACE</a></button></div><div class="image-container"><img src="images/banner_ml_graphics.png" alt="" title=""/></div></div><div class="mountain"></div></div>'];
							  
							  $scope.getSolutionImages = function(index, solutionId) {

								  $scope.mlsolution.slides[index]['image'] = "images/default-model.png";
									apiService
											.getSolutionImage(solutionId)
											.then(
													function(response) {
														if (response.data.response_body.length > 0)
															$scope.mlsolution.slides[index]['image'] = "/site/binaries/content/gallery/acumoscms/solution/"
																	+ solutionId
																	+ "/"
																	+ response.data.response_body[0];
													},
													function(data) {
													});
								}
							  // $scope.mlsolution.slides = [
							      
							      //text: ['Nice image','Awesome photograph','That is so cool','I love that'][slides.length % 4],
							      
							  //];
							  //sessionStorage.setItem("provider","");
							 /* $scope.addSlide = function() {
							    var newWidth = slides.length + 1;
							    slides.push({
							      image: './images/carouselImage' + newWidth +'.jpg',
							      //text: ['Nice image','Awesome photograph','That is so cool','I love that'][slides.length % 4],
							      id: currIndex++
							    });
							  };
							  
							  for (var i = 0; i < 3; i++) {
								    $scope.addSlide();
								  }*/
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
		                         
		                         apiService
									.insertSolutionDetail(dataObj)
									.then(
											function(response) {
												$scope.mlsolution.slides = response.data.response_body.content;
												angular
												.forEach(
														$scope.mlsolution.slides,
														function( value, key) {
															$scope.getSolutionImages(key, value.solutionId);
														});
										
											},
											function(error) {
												$scope.status = 'Unable to load data: '
														+ error.data.error;
												console.log($scope.status);
											});
			 						
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

		 							}
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
					
				});

angular.module('marketHome').controller('homeCarouselController', ['$scope', '$state', function ($scope, $state) {
	//$ctrl = this;
	
	console.log("in home carosel");
	 
     
}]);


angular.module('marketHome')
.filter('to_trusted', ['$sce', function($sce){
    return function(text) {
        return $sce.trustAsHtml(text);
    };
}]);
