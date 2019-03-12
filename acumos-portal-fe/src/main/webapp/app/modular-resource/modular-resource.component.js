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

angular.module('modelResource')

.service('syncGetService', function($http, $q) {

					this.waitForResponse = function(req) {
						var deffered = $q.defer();
						$http(req).success(function(data) {
							deffered.resolve(data);

						}).error(function(response) {
							deffered.reject(response);
						});

						return deffered.promise;
					}
				})
		.run(
				function($rootScope, $location, $http, syncGetService) {
					
					$rootScope
							.$on(
									"$locationChangeStart",
									function(event, next, current) {
										// On refresh if enableOnBoarding is undefined then fetch the site_config and populate it again
										// The modeler resource page will display for a second and then it will redirect to 404.
										// TODO: instead of redirecting to 404 there should be Access denied page that tells the user that this resource is not accessible.
										if ($rootScope.enableOnBoarding === undefined) {
											var promise = syncGetService
													.waitForResponse({
														url : "api/admin/config/site_config",
														method : "GET"
													});
											promise
													.then(function(response) {
														var siteConfig = angular
																.fromJson(response.response_body.configValue);
														if (siteConfig !== undefined) {
															angular
																	.forEach(
																			siteConfig.fields,
																			function(
																					value,
																					key) {
																				if (siteConfig.fields[key].label == 'enableOnBoarding') {
																					if (siteConfig.fields[key].data.name == 'Enabled') {
																						$rootScope.enableOnBoarding = true;
																					} else {
																						$rootScope.enableOnBoarding = false;
																						if ($location
																								.path() === "/modelerResource")
																							$location
																									.path("/404Error");
																					}
																				}
																			});
														}
													});
										} else if ($location.path() === "/modelerResource"
												&& !$rootScope.enableOnBoarding) {
											$location.path("/404Error");
										}
									})
				})
		
	.component('modelResource',{

		//template:"<div class=''>{{ content }}</div>",
		//template:"<button ng-click='authenticate(google)'>Sign in with Google</button>",
		templateUrl:'./app/modular-resource/modular-resource.template.html',
		controller:function($scope,$location,apiService,$http, modelUploadService, $interval, $anchorScroll, $state, $rootScope, $stateParams, $timeout, browserStorageService, $mdDialog){
			$scope.Math = window.Math;
			$scope.onap = false;	
			if($stateParams.ONAP != undefined && $stateParams.ONAP=='true')
			$scope.onap = true;
			$scope.keepModelName = false;
			$scope.model = {};
			$scope.disableOnboardingButton = false;
			
			$scope.disableRefreshButton = true;
			//alert(sessionStorage.getItem("userDetail"));
			$rootScope.progressBar = 0;
			
			$scope.activeViewModel = false;
			if(browserStorageService.getUserDetail()){
				$scope.userLoggedIn = true;
			}else $scope.userLoggedIn = false;
			$scope.loadToolkitType = function() {
				apiService
						.getToolkitTypes()
						.then(
								function(response) {
									$scope.alltoolkitType = response.data.response_body;
									componentHandler.upgradeAllRegistered();
								},
								function(error) {
									console.log("Error in loading");
								});
			}
			$scope.loadToolkitType();
			$scope.expCont = function() {
			    var x = document.getElementById("expContTxt");
			    var y = document.getElementById("expCont");
			    if (x.style.display === "none") {
			        x.style.display = "block";
			        y.style.display = "none";
			    } else {
			        x.style.display = "none";
			        y.style.display = "block";
			    }
			  }
			//added this code to adjust the height of popup
			$scope.openDrop = function(close){
				if($scope.onap == false) {
					if(close == false){
						angular.element('.onboardingwebContent').css({'height':angular.element('.onboardingwebContent').height()+180 } ); 
					}
					if(close == true){
						angular.element('.onboardingwebContent').css({'height':angular.element('.onboardingwebContent').height()-90 } ); 
					}
				}
			}
			$scope.fileSubmit = false;
			$scope.uploadingFile = false;
			$scope.resetProgress = function(){
				$rootScope.progressBar = 0;
				$scope.file = undefined;
				$scope.filename = undefined;
				$scope.modelUploadErrorMsg = undefined;
				$scope.modelUploadError = false;
			}
			$scope.fileUpload = function(){
				//$scope.uploadModel = false;
				$scope.modelUploadError = false;
				var file = $scope.file;
				var userId = JSON.parse(browserStorageService.getUserDetail());
				
				var uploadUrl = "api/model/upload/" + userId[1];
				var promise = modelUploadService.uploadFileToUrl(
						file, uploadUrl);
				$scope.tempfilename = file.name;
				$scope.uploadingFile = true;
				promise
				.then(
						function(response) {
							$scope.modelUploadError = false;
							$scope.fileSubmit = true;
							$scope.filename = $scope.tempfilename;
							$rootScope.progressBar = 100;
							chkCount();
							$scope.uploadModel = false;
							$scope.uploadingFile = false;
							angular.element('.input-div').addClass('disabledClick');
						},
						function(error) {
								$scope.modelUploadError = true;
								$scope.modelUploadErrorMsg = error;
								$scope.filename = '';
								$rootScope.progressBar = 0;
								$scope.uploadModel = false;
								chkCount();
								$scope.uploadingFile = false;
						});
			}
			
			$scope.closePoup = function(){
				if ($scope.uploadingFile && $rootScope.progressBar < 100){
					modelUploadService.cancelUpload("Upload cancelled by user");
				}
				$scope.uploadModel = !$scope.uploadModel;
				$scope.file = false;
				$scope.filename = "";
				$scope.file = "";
				$scope.fileSubmit = false;
	           	angular.element('#file').val('');
	           	$scope.modelUploadError = false;
	           	angular.element('.input-div').removeClass('disabledClick');
	        }
			
			$scope.getOnboardingCLIUrls = function() {
                apiService
                .getCLIPushUrl()
                .then(
                        function(response) {
                            $scope.cliPushUrl = response.data.data;
                        },
                        function(error) {
                            $scope.cliPushUrl = 'Push URL unavailable';
                        });

                apiService
                .getCLIAuthUrl()
                .then(
                        function(response) {
                           $scope.cliAuthUrl = response.data.data;
                        },
                        function(error) {
                            $scope.cliAuthUrl = 'Auth URL unavailable';
                        });
            }
			
			$scope.getTensorflowContent = function(modelName){
				apiService
				.getModelerResourcesContent(modelName)
				.then(
						function(response) {
							$scope.tensorflow = response.data.description;
						},
						function(error) {
							$scope.tensorflow = 'No Contents Available';
						});
			}
			
			$scope.getH2OContent = function(modelName){
				apiService
				.getModelerResourcesContent(modelName)
				.then(
						function(response) {
							$scope.h2o = response.data.description;
						},
						function(error) {
							$scope.h2o = 'No Contents Available';
						});
			}
			
			$scope.getRCloudContent = function(modelName){
				apiService
				.getModelerResourcesContent(modelName)
				.then(
						function(response) {
							$scope.RCloud = response.data.description;
						},
						function(error) {
							$scope.RCloud = 'No Contents Available';
						});
			}
			
			$scope.getRContent = function(modelName){
				apiService
				.getModelerResourcesContent(modelName)
				.then(
						function(response) {
							$scope.R = response.data.description;
						},
						function(error) {
							$scope.R = 'No Contents Available';
						});
			}
			
			$scope.getJavaContent = function(modelName){
				apiService
				.getModelerResourcesContent(modelName)
				.then(
						function(response) {
							$scope.java = response.data.description;
						},
						function(error) {
							$scope.java = 'No Contents Available';
						});
			}
			
			$scope.getonboardingOverview = function() {
				apiService.getOnboardingOverview()
					.success(
							function(response) {
                            	var overview = JSON.parse(atob(response.response_body.contentValue));
								$scope.onboard_overview = overview.description;
							})
					.error(
							function(error) {
								return "No Contents Available";
							});
			}
			
			$scope.userId = JSON.parse(browserStorageService.getUserDetail());
			$scope.completedSteps = [];
			$scope.errorCS = ''; $scope.errorCT = ''; $scope.errorDO = ''; $scope.errorAA = ''; $scope.errorDI = '';
			$scope.errorCC = '';
			
			$scope.showValidationStatus = function(){
				var counter = 1;

				apiService
				.getMessagingStatus($scope.userId[1], $scope.trackId ).then(
						function(reponse) {
							var data = reponse.data.response_body;
							$rootScope.trackId = $scope.trackId;
							$scope.stepfailed = false;
							$scope.allSuccess = false;
							var width = 0;
							for(var i=0 ; i< data.length; i++){
								var stepName = data[i].name;
								var statusCode =  data[i].statusCode;
								var stepCode = data[i].stepCode;

								if($scope.onap == false){
									switch(stepName){
										case 'CreateSolution': var counter = 0; ( statusCode == 'FA' ) ?  $scope.errorCS = data[i].result : $scope.errorCS = ''; break;
										case 'AddArtifact' :   
											if(counter > 3){
												$scope.clearNotificationInterval(); return;
											}	
											var counter = 2;
											( statusCode == 'FA' ) ?  $scope.errorAA = data[i].result : $scope.errorAA = ''; break;
										case 'CreateTOSCA' :  var counter = 4; ( statusCode == 'FA' ) ?  $scope.errorCT = data[i].result : $scope.errorCT = ''; break;	                        
										case 'Dockerize' :  var counter = 6; ( statusCode == 'FA' ) ?  $scope.errorDO = data[i].result : $scope.errorDO = ''; break;
										case 'AddDockerImage' :  var counter = 8; ( statusCode == 'FA' ) ?  $scope.errorDI = data[i].result : $scope.errorDI = ''; break;							
									}
									var onboardingComponent = '.onboarding-web';									
									$rootScope.$broadcast('updateNotifications');
								} else {
									switch(stepName){
										case 'CheckCompatibility': var counter = 2; ( statusCode == 'FA' ) ?  $scope.errorCC = data[i].result : $scope.errorCC = ''; break;
										/*case 'CreateTOSCA' :  var counter = 6; ( statusCode == 'FA' ) ?  $scope.errorCT = data[i].result : $scope.errorCT = ''; break;*/
										case 'Dockerize' :  var counter = 4; ( statusCode == 'FA' ) ?  $scope.errorDO = data[i].result : $scope.errorDO = ''; break;
										case 'AddDockerImage' :  var counter = 6; ( statusCode == 'FA' ) ?  $scope.errorDI = data[i].result : $scope.errorDI = ''; break;
										case 'AddArtifact' :  
											/*if(counter > 5){
												$scope.clearNotificationInterval(); return;
											}*/
											var counter = 8; ( statusCode == 'FA' ) ?  $scope.errorAA = data[i].result : $scope.errorAA = ''; break;
										default : var counter = -1;
									}

									var onboardingComponent = '#onap-onboarding';
									$rootScope.$broadcast('updateNotifications');
								} 
								
								if (counter != -1) {
									angular.element(angular.element(onboardingComponent + ' li div')[counter]).removeClass('completed incomplet active');
									if(statusCode == 'FA'){
										angular.element(angular.element(onboardingComponent + ' li div')[counter]).addClass('incomplet');
										angular.element(angular.element(onboardingComponent + ' li')[counter+1]).removeClass('green completed');
										$scope.stepfailed = true;
									}else if(statusCode == 'ST'){
										angular.element(angular.element(onboardingComponent + ' li div')[counter]).addClass('active');
										angular.element(angular.element(onboardingComponent + ' li')[counter+1]).addClass('progress-status green')
										
									}else if(statusCode == 'SU'){
										angular.element(angular.element(onboardingComponent + ' li div')[counter]).addClass('completed');
										angular.element(angular.element(onboardingComponent + ' li')[counter+1]).addClass('green completed');
										$scope.completedSteps[stepName] = stepName;

										if( ( ( (counter === 8 && $scope.onap == false ) || (counter === 8 && $scope.onap == true) ) ) && $scope.stepfailed == false ) {
											counter = counter + 2;
											angular.element(angular.element(onboardingComponent + ' li div')[counter]).addClass('completed');
											angular.element(angular.element(onboardingComponent + ' li')[counter+1]).addClass('green completed');
											$scope.errorVM = '';
											$scope.completedSteps['ViewModel'] = 'ViewModel';
											$scope.allSuccess = true;
											
										}
										
										if($scope.completedSteps.indexOf(stepName) == -1 && $scope.stepfailed == false){
											width = width+15;
											angular.element('.progress .progress-bar').css({ "width" : width+'%'});
											angular.element('.onboardingwebContent').css({ "height" :'100%'});
										}
									}
								}
								if( $rootScope.trackId != false && ( $scope.allSuccess != true && $scope.stepfailed != true ) ) {
									$scope.disableOnboardingButton = true;
								}
								
								if( $scope.allSuccess || $scope.stepfailed ){
									$scope.clearNotificationInterval();
									$scope.disableRefreshButton = false;
								}
							}
							

						},
						function(error) {
							
					});
				
			} 
			
			$scope.showErrorDetailsPopup = function(){
				 $mdDialog.show({
	        		  contentElement: '#onboardinErrorDetails',
	        		  parent: angular.element(document.body),
	        		  clickOutsideToClose: true
	        	  });
			}
			$scope.handleDismiss = function(){
				 $mdDialog.cancel();
			}
			
			$scope.clearNotificationInterval = function(){
				$scope.disableOnboardingButton = false;
				$scope.file = '';
				$interval.cancel($scope.clearInterval);
			}
			
			$scope.clearExistingNotifications = function(){
                $scope.disableRefreshButton = true;
                $scope.fileSubmit = false;
                $rootScope.trackId = false;
                angular.element(angular.element('li div')).removeClass('completed incomplet active');
                angular.element(angular.element('li')).removeClass('green completed');
                angular.element('.progress .progress-bar').css({ "width" : '0%'});
                $scope.errorCS = ''; $scope.errorCT = ''; $scope.errorDO = ''; $scope.errorAA = ''; $scope.errorDI = '';
                $scope.errorCC = '';
                angular.element('.input-div').removeClass('disabledClick');
             }

			$scope.$watchGroup(['toolkitNameValue','install','file','fileSubmit'], function(newValues, oldValues) {
				if(newValues[0].length>=1 && newValues[1] && newValues[2] && newValues[3]){
					$scope.clearExistingNotifications();
				}
			});
			
			$scope.addToCatalog = function(){
				
				$scope.statusReult = [];
				$scope.disableOnboardingButton = true;
				
				if($scope.onap == false){
					
					if($scope.disableOnboardingButton == true ){
						$scope.clearExistingNotifications();
						angular.element('.input-div').addClass('disabledClick');
					}
					
					$scope.addToReqObj = {
							  "request_body": {
								    /*"version": $scope.toolkitNameValue,
								    "name": $scope.user.name,*/
								  }
								};
					apiService
					.postAddToCatalog($scope.userId[1], $scope.addToReqObj)
					.then(
							function(response) {
								$location.hash('webonboarding');  // id of a container on the top of the page - where to scroll (top)
		                        $anchorScroll(); 
		                        $scope.msg = "Onboarding process has started and it will take 30 seconds to reflect the change in status."; 
		                        $scope.icon = '';
		                        $scope.styleclass = 'c-warning';
		                        $scope.showAlertMessage = true;
		                        $timeout(function() {
		                        	$scope.showAlertMessage = false;
		                        }, 8000);
		                        
								$scope.trackId = response.data.response_detail;
								$scope.clearInterval = $interval(function(){
									$scope.showValidationStatus();
								}, 25000);
								
							},
							function(error) {
					});
				} else {
				    
					
					apiService
					.addToCatalogONAP($stateParams.solutionId,$stateParams.revisionId,$scope.userId[1], $scope.model.modelName)
					.then(
							function(response) {
								$scope.trackId = response.data.response_detail;
								$location.hash('onap-onboarding');  // id of a container on the top of the page - where to scroll (top)
		                        $anchorScroll(); 
		                        $scope.msg = "Onboarding process has started and it will take 30 seconds to reflect the change in status."; 
		                        $scope.icon = '';
		                        $scope.styleclass = 'c-warning';
		                        $scope.showAlertMessage = true;
		                        $timeout(function() {
		                        	$scope.showAlertMessage = false;
		                        }, 8000);

								$scope.clearInterval = $interval(function(){
									$scope.showValidationStatus();
								}, 25000);
								
							},
							function(error) {
					});
				}

			}
			
			if( $rootScope.trackId && $rootScope.trackId != false && $scope.onap == false){
				$scope.trackId = $rootScope.trackId;
				$scope.showValidationStatus();
			}
			
			$scope.viewModel = function(){
				$state.go('manageModule');
			}
			//cHECK FOR the count of success
			$scope.statusCount = 0;
			function chkCount(){
				var count = 0;
				if($scope.toolkitNameValue)count++;
				if($scope.install)count++;
				if($scope.file && $scope.fileSubmit && $scope.modelUploadError == false)count++;
				$scope.statusCount = count;
				/*if(count === 4){
					$scope.activeViewModel = true;
				}*/
			}
			 
			$scope.$watch('toolkitNameValue', function() {$scope.file=null; chkCount();});
			$scope.$watch('install', function() {chkCount();});
			$scope.$watch('file', function() {chkCount(); $scope.filename = $scope.file.name;});
			
			/*if a popup is open other should close*/
			$scope.closeOtherPopovers = function(variableName, variableValue){
		    	$scope.variableName = variableName;
		    	$scope.variableValue = variableValue;
		    	if($scope.variableName == 'uploadModel' && $scope.variableValue == true){
		    		$scope.selToolKitT = $scope.addModel = false;
		    	}else if( $scope.variableName == 'selToolKitT' && $scope.variableValue == true ){
		    		$scope.uploadModel = $scope.addModel = false;
		    	}else if( $scope.variableName == 'addModel' && $scope.variableValue == true ){
		    		$scope.uploadModel = $scope.selToolKitT = false;
		    	}
		    };

		    $scope.changeOnapSolutionName = function(setDefaultName){
				if(setDefaultName == true){
					// TODO : This is quick fix. Need to convert to query parameter or passed as body parameter
					$scope.model.modelName = 'null';
				}
		    }
		    
			}
});