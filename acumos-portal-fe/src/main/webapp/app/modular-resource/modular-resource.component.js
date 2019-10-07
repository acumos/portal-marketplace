/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
Modifications Copyright (C) 2019 Nordix Foundation.
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
			$scope.licenseOption = 'Upload';
			
			$scope.dockerBackScreen = true;
			$scope.disableRefreshButton = true;
			$scope.disableDockerRefreshButton = true;
			
			$scope.disableUploadLicense = false;
			$scope.disableUploadDLCheckbox = false;

			if(angular.isDefined($rootScope.isMicroserviceEnabled) == false)
				$rootScope.isMicroserviceEnabled = true;
			$rootScope.progressBar = 0;
			$scope.devEnv = '1';
			$scope.activeViewModel = false;
			if(browserStorageService.getUserDetail()){
				$scope.userLoggedIn = true;
			}else $scope.userLoggedIn = false;

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
			$scope.fileSubmitLicense = false;
			$scope.fileSubmitDocLicense = false;
			
			$scope.resetProgress = function(){
				$rootScope.progressBar = 0;
				$scope.file = undefined;
				$scope.fileSubmit = false;
				$scope.filename = undefined;
				$scope.modelUploadErrorMsg = undefined;
				$scope.modelUploadError = false;				
			}
			// TODO license-profile-editor handlers
			var selLicProfileTplMsg;
			var bindEvent = function(element, eventName, eventHandler) {
					if (element.addEventListener) {
						element.addEventListener(eventName, eventHandler, false);
					} else if (element.attachEvent) {
						element.attachEvent('on' + eventName, eventHandler);
					}
				},
				unbindEvent = function(element, eventName, eventHandler) {
					if (element.removeEventListener) {
						element.removeEventListener(eventName, eventHandler, false);
					} else if (element.detachEvent) {
						element.detachEvent('on' + eventName, eventHandler);
					}
				},
				winMsgHandler = function(event) {
					// message listener
					if (event.data.key === 'output') {
						var licenseText = JSON.stringify(event.data.value);	
						$scope.licenseOption = 'selectLicProfile';
						$scope.createLicenseFile(licenseText);
						$mdDialog.hide();
					} else if (event.data.key === 'action') {
						if (event.data.value === 'cancel') {
							$mdDialog.hide();
						}
					} else if (event.data.key === 'init_iframe') {
						// if licenseProfileEditorInitMsg then send me
						var iframe = document.getElementById('iframe-license-profile-editor');

						if (selLicProfileTplMsg && iframe) {
							// send message to License Profile Editor iframe
							iframe.contentWindow.postMessage(selLicProfileTplMsg, '*');
						}
					}
				},
				showLicenseProfileEditorDialog = function(event) {

					var onCompleteLicProfileTplDialog = function(scope, element, options) {
						var iframe = document.getElementById('iframe-license-profile-editor');

						if (selLicProfileTplMsg && iframe) {
							// send message to License Profile Editor iframe
							iframe.contentWindow.postMessage(selLicProfileTplMsg, '*');
						}
					};

					// open the license profile modal
					$mdDialog.show({
						controller: function DialogController($scope, $mdDialog) {
							$scope.closeDialog = function() {
								$mdDialog.hide();
							};
						},
						templateUrl:'./app/modular-resource/license-profile-editor-dialog.template.html',
						parent: angular.element(document.body),
						targetEvent: event,
						clickOutsideToClose:true,
						onComplete: onCompleteLicProfileTplDialog
					});
				};

			if (window.licProfEdMsgHandlerRef) {
				unbindEvent(window, 'message', window.licProfEdMsgHandlerRef);
			}
			bindEvent(window, 'message', winMsgHandler);
			window.licProfEdMsgHandlerRef = winMsgHandler;

			$scope.createNewLicenseProfileTemplate = function(event, isDockerLicense) {
				selLicProfileTplMsg = undefined;
				$scope.isDockerLicense = isDockerLicense;
				showLicenseProfileEditorDialog(event);
			};
			$scope.allTemplates = [];
			$scope.modifyLicenseProfileTemplate = function(event, isDockerLicense) {
				$scope.isDockerLicense = isDockerLicense;
				var selectedLic = $scope.allTemplates[$scope.selectedLicense];
				var template = JSON.parse(selectedLic.template);
				$scope.createLicenseFile(selectedLic);
				if (selectedLic) {
					try {
						var msgObj = {
							"key": "input",
							"value": template
						};
						selLicProfileTplMsg = msgObj;
					} catch (e) {
						console.error("failed parsing license profile template input", e);
					}
				}
				showLicenseProfileEditorDialog(event);
			};

			$scope.resetLicenseUpload = function(dockerURL){
				$rootScope.progressBar = 0;
				if(dockerURL){
					$scope.licenseDocfile = undefined;
					$scope.fileSubmitDocLicense = false;
					$scope.licenseDockerFilename = undefined
				} else {
					$scope.licensefile = undefined;
					$scope.fileSubmitLicense = false;
					$scope.licenseFilename = undefined;
				}
				$scope.modelLicUploadErrorMsg = undefined;
				$scope.modelLicUploadError = false;
				
			}
			$scope.fileUpload = function(licUploadFlag, isDockerURLLicense){
				
				$scope.licUploadFlag = licUploadFlag;
				var userId = JSON.parse(browserStorageService.getUserDetail());
				
				if(!licUploadFlag){
					var file = $scope.file;
					$scope.modelUploadError = false;
				} else {
					if(isDockerURLLicense) {
						var file = $scope.licenseDocfile;
					} else {
						var file = $scope.licensefile;
					}
					$scope.modelLicUploadError = false;
				}		
				
				var uploadUrl = "api/model/upload/" + userId[1] +"/?licUploadFlag=" + licUploadFlag;
				var promise = modelUploadService.uploadFileToUrl(
						file, uploadUrl);
				
				$scope.uploadingFile = true;
				promise
				.then(
						function(response) {
							$scope.modelUploadError = false;
							
							$rootScope.progressBar = 100;
							if(licUploadFlag){
								
								$scope.modelLicProgressBar = $rootScope.progressBar;
								$rootScope.progressBar = 0;
								if(isDockerURLLicense){
									$scope.fileSubmitDocLicense = true;
								} else {
									$scope.fileSubmitLicense = true;
								}
								
							} else {
								$scope.modelProgressBar = $rootScope.progressBar;
								$rootScope.progressBar = 0;								
								$scope.fileSubmit = true;
								$rootScope.isOnnxOrPFAModel = response.response_body;
							}
														
							$scope.uploadModel = false;
							$scope.uploadingFile = false;
														
						},
						function(error) {
							if(licUploadFlag){
								if(error){
									var errorMsg = [error];
									if(error['response_detail'] &&  (error.response_detail).indexOf('$') != -1) {
											var errorMsg = (error.response_detail).substring(1,(error.response_detail).length-1).split('$.');							
											errorMsg.shift();
									} 
								}

								if(isDockerURLLicense){
									$scope.modelDocLicUploadError = true;
									$scope.modelDocLicUploadErrorMsg = errorMsg;
								} else{
									$scope.modelLicUploadError = true;
									$scope.modelLicUploadErrorMsg = errorMsg;
								}
							} else {
								$scope.modelUploadError = true;
								$scope.modelUploadErrorMsg = error;
							}
							$scope.filename = '';
							$rootScope.progressBar = 0;
							$scope.uploadModel = false;
							$scope.uploadingFile = false;
						});
			}
			
			$scope.closePoup = function(licUploadFlag, dockerURL){
				if ($scope.uploadingFile && $rootScope.progressBar < 100){
					modelUploadService.cancelUpload("Upload cancelled by user");
				}
				
				$scope.uploadModel = !$scope.uploadModel;
				if(licUploadFlag) {
					if(dockerURL){
						$scope.licenseDocfile = "";
						$scope.licenseDockerFilename = "";
						$scope.fileSubmitDocLicense = false;						
						$scope.modelDocLicUploadError = false;
					} else {
						$scope.licenseFilename = "";
						$scope.licensefile = "";
						$scope.fileSubmitLicense = false;
						$scope.modelLicUploadError = false;
					}
				} else {
					$scope.filename = "";
					$scope.file = "";
					$scope.fileSubmit = false;
					$scope.modelUploadError = false;
				}
	           	angular.element('#file').val('');			
	           	
	        }
			
			$scope.getOnboardingCLIUrls = function() {
                apiService
                .getCLIPushUrl()
                .then(
                        function(response) {
                            $scope.cliPushUrl = response.data.data;
                            componentHandler.upgradeAllRegistered();
                        },
                        function(error) {
                            $scope.cliPushUrl = 'Push URL unavailable';
                        });

                apiService
                .getCLIAuthUrl()
                .then(
                        function(response) {
                           $scope.cliAuthUrl = response.data.data;
                           componentHandler.upgradeAllRegistered();
                        },
                        function(error) {
                            $scope.cliAuthUrl = 'Auth URL unavailable';
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
								if(!((data[i].result).includes('OnboardingLog.txt')) || !((data[i].result).includes('MicroserviceGenerationLog.txt'))){} {
									var stepName = data[i].name;
									var statusCode =  data[i].statusCode;
									var stepCode = data[i].stepCode;
	
									if($scope.onap == false){
										switch(stepName){
											case 'CreateSolution': var counter = 0; ( statusCode == 'FA' ) ?  $scope.errorCS = data[i].result : $scope.errorCS = ''; break;
											case 'AddArtifact' :
													var counter = 2;
												( statusCode == 'FA' ) ?  $scope.errorAA = data[i].result : $scope.errorAA = ''; break;
											case 'CreateTOSCA' :  var counter = 4; ( statusCode == 'FA' ) ?  $scope.errorCT = data[i].result : $scope.errorCT = ''; break;	                        
											case 'Dockerize' :  var counter = 6; ( statusCode == 'FA' ) ?  $scope.errorDO = data[i].result : $scope.errorDO = ''; break;
											case 'AddDockerImage' :  var counter = 8; ( statusCode == 'FA' ) ?  $scope.errorDI = data[i].result : $scope.errorDI = ''; break;							
										}
										var onboardingComponent = '.onboarding-web';
										if($rootScope.dockerURIonboarding){
											var onboardingComponent = '.onboarding-docker';
										}
										
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
	
											if( ( ( (counter === 8 && $scope.onap == false ) || (counter === 8 && $scope.onap == true) ) || ($rootScope.dockerURIonboarding && counter == 2) || ($rootScope.isOnnxOrPFAModel == true && counter == 2 ) || ( counter == 4 && !$rootScope.isMicroserviceEnabled ) ) && $scope.stepfailed == false ) {
												if($rootScope.isOnnxOrPFAModel){
													counter = 10;
													width = 85;
												} else if($rootScope.dockerURIonboarding){
													counter = 4;
													width = 85;
												} else if(!$rootScope.isMicroserviceEnabled){
													counter = 10;
													width = 85;
												} else {											
													counter = counter + 2;
												}											
												angular.element(angular.element(onboardingComponent + ' li div')[counter]).addClass('completed');
												angular.element(angular.element(onboardingComponent + ' li')[counter+1]).addClass('green completed');
												$scope.errorVM = '';
												$scope.completedSteps['ViewModel'] = 'ViewModel';
												$scope.allSuccess = true;
											}
											
											if($scope.completedSteps.indexOf(stepName) == -1 && $scope.stepfailed == false){
												width = width+15;
												if($rootScope.dockerURIonboarding){
													angular.element('.docker_onboarding .progress .progress-bar').css({ "width" : width+'%'});	
												} else {
													angular.element('.regular_onboarding .progress .progress-bar').css({ "width" : width+'%'});	
												}
																					
												angular.element('.onboardingwebContent').css({ "height" :'100%'});
											}
										}
									}
									if( $rootScope.trackId != false && ( $scope.allSuccess != true && $scope.stepfailed != true ) ) {
										$scope.disableOnboardingButton = true;
									}
									
									if( $scope.allSuccess || $scope.stepfailed ){
										$scope.clearNotificationInterval();
										
										if($rootScope.dockerURIonboarding){
											$scope.disableDockerRefreshButton = false;
											$rootScope.isMicroserviceEnabled = true;
										} else {
											$scope.disableRefreshButton = false;
										}
										
									}
								}
							
						 }//
						
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
				$scope.licensefile = "";
				$interval.cancel($scope.clearInterval);
			}
			
			$scope.clearExistingNotifications = function(){
                $scope.disableRefreshButton = true;
                $scope.fileSubmit = false;
                $scope.fileSubmitLicense = false;
                $rootScope.trackId = false;
                $scope.filename = '';
                $scope.licenseFilename = '';
                $scope.devEnv = '1';
                angular.element(angular.element('li div')).removeClass('completed incomplet active');
                angular.element(angular.element('li')).removeClass('green completed');
                angular.element('.regular_onboarding .progress .progress-bar').css({ "width" : '0%'});
                $scope.errorCS = ''; $scope.errorCT = ''; $scope.errorDO = ''; $scope.errorAA = ''; $scope.errorDI = '';
                $scope.errorCC = '';
             }
			
			$scope.clearExistingDockerURLNotifications = function(){
                $scope.disableDockerRefreshButton = true;
                $scope.fileSubmitDocLicense = false;
                $scope.disableUploadDLCheckbox = false;
                $rootScope.trackId = false;
                $scope.isDockerLicUploaded = false;
                $scope.licenseDockerFilename = '';
                $scope.licenseDocfile = false;
                angular.element(angular.element('li div')).removeClass('completed incomplet active');
                angular.element(angular.element('li')).removeClass('green completed');
                angular.element('.docker_onboarding .progress .progress-bar').css({ "width" : '0%'});               
                $scope.errorCS = ''; $scope.errorCT = ''; $scope.errorDO = ''; $scope.errorAA = ''; $scope.errorDI = '';
                $scope.errorCC = '';
             }
		
			$scope.addToCatalog = function(dockerUrl){
				
				var	solutionName = $scope.modelName;
				if(!dockerUrl){
					dockerUrl = null;
					$rootScope.dockerURIonboarding = false;
				} else {
					dockerUrl = $scope.dockerURI;
					solutionName = $scope.modelDockerURLName;
					$rootScope.dockerURIonboarding = true;
					$rootScope.isMicroserviceEnabled = false;
					$rootScope.isOnnxOrPFAModel = false;
					$scope.disableUploadDLCheckbox = true;
				}
				
				$scope.statusReult = [];
				$scope.disableOnboardingButton = true;
				
				if($scope.onap == false){
					
					if($scope.disableOnboardingButton == true ){
						$scope.clearExistingNotifications();
					}				
					
					$scope.addToReqObj = { };

					if($rootScope.isOnnxOrPFAModel || dockerUrl) {
						$scope.addToReqObj = { 
						  "request_body": {
							    "name": solutionName,
							    "dockerfileURI" : dockerUrl
							  }
						}
					}
					
					 if($rootScope.isMicroserviceEnabled && $rootScope.isOnnxOrPFAModel == false){
						$scope.addToReqObj = { 
							  "request_body": {
								    "deploymentEnv" : $scope.devEnv
								  }
							}
					}
					
					apiService
					.postAddToCatalog($scope.userId[1], $scope.addToReqObj)
					.then(
							function(response) {
								$location.hash('webonboarding');  // id of a container on the top of the page - where to scroll (top)
		                        $anchorScroll();
		                       
		                        $scope.msg = "On-boarding process has started and it will take 30 seconds to reflect the change in status."; 
		                        $scope.icon = 'info_outline';
		                        $scope.styleclass = 'c-info';
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
		                        $scope.msg = "On-boarding process has started and it will take 30 seconds to reflect the change in status."; 
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
		    	    
		    $scope.dockerURI = '';
		    $scope.$watchGroup(['host', 'port', 'image', 'tag'], function() {
		    	  
		    	  if($scope.host && $scope.image && $scope.port){
		    		  $scope.dockerURI = $scope.host + ':' + $scope.port + '/' + $scope.image;
		    		  if($scope.tag){
		    			  $scope.dockerURI += ':' + $scope.tag;
		    		  }
		    	  }
		    });
		    
		   $scope.resetDockerURLForm = function(){
			   $scope.clearExistingDockerURLNotifications();
		       dockerUrlForm.reset();
		   }
		   	  
		   $scope.backToDocker = function(){
			   $scope.dockerBackScreen = true;
			   $scope.checkingSolution = false;
			   $scope.availableSolution = false;
			   $scope.searchModelName = '';
			   $scope.notavailableSolution = false;
			   $scope.createDockerRef = false;
			   $scope.solutionList = [];
			   $scope.artifactUrl = '';
			   $scope.searchModel = '';	
			   $scope.disableCreateDocker=false;
		   }
		   
		   $scope.checkingSolution = false;
		   $scope.checkModelName = function(modelName, searchType){
			   
			   if(modelName) {		 
				  var request = {
							  "request_body": {
								    "published" : true,
								    "active": true,
								    "nameKeyword" : [modelName],
								    "userId": $scope.userId[1],
								    "pageRequest": {
								      "page" : 0,
								      "size" : 1000
								    }
								  }
								};
				  $scope.checkingSolution=true;
				  $scope.availableSolution=false;
				  $scope.selectedSolutionId = '';
	              apiService.fetchUserSolutions(request)
	               .then(
	                       function(response) {
	                    	   $scope.checkingSolution = false;
	                    	  
	                           if(!response.data.response_body.content.length) {
	                        	   $scope.availableSolution = true;
	                        	   $scope.notavailableSolution = false;	                        	  
	                           } else {
	                        	   $scope.notavailableSolution = true;
	                        	   $scope.availableSolution = false;
	                        	   $scope.solutionList = response.data.response_body.content;
	                        	   document.getElementById("modelList").click();
	                           }
	                });
			   }
			   
			}
		   
		   $scope.$watchGroup(['solutionList', 'searchModel'], function() {
		    	  
		    	  if($scope.solutionList && $scope.searchModel){
		    		  for(var i=0 ; i< $scope.solutionList.length; i++){
		    			  if( $scope.solutionList[i].name == $scope.searchModel ) {
		    				  $scope.selectedSolutionId = $scope.solutionList[i].solutionId;
		    			  }
		    		  }
		    	  } else {
		    		  $scope.selectedSolutionId = '';
		    	  }
		    });
		   
		   $scope.createDockerImageRef = function(isUpdate, modelname){
			   $scope.createDockerRef = true;
			   $scope.addToReqObj = { 
						  "request_body": {
							    "name": modelname,							   
							    "dockerfileURI" : 'DockerModel'
							  }
						};
			               
               $scope.msg = "Generating Artifact Url, Please wait."; 
               $scope.icon = 'info_outline';
               $scope.styleclass = ' c-info';
               $scope.showAlertMessage = true;
               $timeout(function() {
               	$scope.showAlertMessage = false;
               }, 5000);

			   apiService.updateDockerImage($scope.addToReqObj)
               .then(function(response) {
                   $scope.artifactUrl = response.data.response_body;                    	 
               });			   
		   }
		   
		   $scope.copyText = function(id){
			   var copyText = angular.element(id);
			   copyText.select();
			   document.execCommand("copy");
		   }
		   
		   $scope.allTemplates = [];
		   $scope.getAllLicenseTemplates = function(){
			   apiService.getAllLicenseProfile()
               .then(
                       function(response) {                   	  
                           if(response.data.response_body.length) {
                        	  $scope.allTemplates = response.data.response_body;
                        	  $scope.selectedLicense = 0;
                           } 
                });
		   }
		      
		   $scope.getAllLicenseTemplates();
		   
		   $scope.createLicenseFile = function(licenseText) {
			   
			   var request = licenseText; 
				if(licenseText){
					 apiService.createLicenseFile($scope.userId[1], request)
		               .then(function(response){
		            	    $location.hash('webonboarding');
	                        $anchorScroll();
		            	    $scope.msg = "License uploaded successfully. "; 
							$scope.icon = '';
							$scope.styleclass = 'c-success';
							$scope.showAlertMessage = true;
							$timeout(function() {
								$scope.showAlertMessage = false;
							}, 2500);
							
		            	   if($scope.isDockerLicense) {
			            	   $scope.licenseDocfile = true;
			            	   $scope.fileSubmitDocLicense = true;
		            	   } else {
			            	   $scope.licensefile = true;
			            	   $scope.fileSubmitLicense = true;
		            	   }

		             });
				}
		   }
		}
});