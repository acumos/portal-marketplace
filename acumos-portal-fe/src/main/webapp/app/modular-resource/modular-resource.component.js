/*
===============LICENSE_START=======================================================
Acumos
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

	.service('modelUploadService', function($http, $q) {

			this.uploadFileToUrl = function(file, uploadUrl) {
				// FormData, object of key/value pair for form fields and values
				var fileFormData = new FormData();
				fileFormData.append('file', file);

				var deffered = $q.defer();
				$http.post(uploadUrl, fileFormData, {
					transformRequest : angular.identity,
					headers : {
						'Content-Type' : undefined
					},uploadEventHandlers: {
				        progress: function (e) {
			                  if (e.lengthComputable) {
			                     $rootScope.progressBar = (e.loaded / e.total) * 100;
			                     $rootScope.progressCounter = $rootScope.progressBar;
			                  }
			        }
			    }

				}).success(function(response) {
					deffered.resolve(response);

				}).error(function(response) {
					deffered.reject(response);
				});

				return deffered.promise;
			}
		})
	.component('modelResource',{

		//template:"<div class=''>{{ content }}</div>",
		//template:"<button ng-click='authenticate(google)'>Sign in with Google</button>",
		templateUrl:'./app/modular-resource/modular-resource.template.html',
		controller:function($scope,$location,apiService,$http, modelUploadService, $interval, $anchorScroll, $state, $rootScope, $stateParams, $timeout){
			
			$scope.onap = false;
			if($stateParams.ONAP != undefined && $stateParams.ONAP=='true')
			$scope.onap = true;
			$scope.keepModelName = false;
			$scope.model = {};
			$scope.disableOnboardingButton = false;
			//alert(localStorage.getItem("userDetail"));
			$rootScope.progressBar = 0;
			
			$scope.activeViewModel = false;
			if(localStorage.getItem("userDetail")){
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
			$scope.fileUpload = function(){
				//$scope.uploadModel = false;
				$scope.modelUploadError = false;
				var file = $scope.file;
				var userId = JSON.parse(localStorage.getItem("userDetail"));
				
				var uploadUrl = "api/model/upload/" + userId[1];
				var promise = modelUploadService.uploadFileToUrl(
						file, uploadUrl);
				$scope.tempfilename = file.name;
				$scope.filename = '';
				promise
				.then(
						function(response) {
							$scope.modelUploadError = false;
							$scope.fileSubmit = true;
							$scope.filename = $scope.tempfilename;
							$rootScope.progressBar = 0;
							chkCount();
							$scope.uploadModel = false;
						},
						function(error) {
							if(error.status == 400){
								$scope.modelUploadError = true;
								$scope.modelUploadErrorMsg = error.message;
								$rootScope.progressBar = 0;
								$scope.uploadModel = false;
								chkCount();
								
							}else{
								$scope.modelUploadError = true;
								$rootScope.progressBar = 0;
								//$scope.serverResponse = 'An error has occurred';
								$scope.modelUploadErrorMsg = 'An error has occurred';
								$scope.uploadModel = false;
								chkCount();
							}
						});
			}
			
			$scope.closePoup = function(){
				$scope.uploadModel = !$scope.uploadModel;
				$scope.file = false;
				$scope.filename = "";
				$scope.file = "";
	           	angular.element('#file').val('');
	        }
			
			$scope.getscikitLearnContent = function(modelName){
				apiService
				.getModelerResourcesContent(modelName)
				.then(
						function(response) {
							$scope.scikitlearn = response.data.description;
						},
						function(error) {
							$scope.scikitlearn = 'No Contents Available';
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
			
			
			$scope.getCmsH2OWebContents = function(){
           	 var req = {
						method : 'GET',
						url : '/site/api-manual/Solution/solDescription?path=global/web-model-resource&name=h2o',
				};
           	 $http(req)
					.success(
							function(data, status, headers,
									config) {
								$scope.webh2o = data.description;
							}).error(
									function(data, status, headers,
											config) {
										return "No Contents Available"
									});
			}
			
			$scope.getonboardingOverview = function(){
	           	 var req = {
							method : 'GET',
							url : '/site/api-manual/Solution/solDescription?path=global/onboarding-model-screen&name=overview',
					};
	           	 $http(req)
						.success(
								function(data, status, headers,
										config) {
									$scope.onboard_overview = data.description;
								}).error(
										function(data, status, headers,
												config) {
											return "No Contents Available"
										});
				}
			
			$scope.getCmsRWebContents = function(){
	           	 var req = {
							method : 'GET',
							url : '/site/api-manual/Solution/solDescription?path=global/web-model-resource&name=r',
					};
	           	 $http(req)
						.success(
								function(data, status, headers,
										config) {
									$scope.webr = data.description;
								}).error(
										function(data, status, headers,
												config) {
											return "No Contents Available"
										});
				}
			
			$scope.getCmsScikitLearnWebContents = function(){
	           	 var req = {
							method : 'GET',
							url : '/site/api-manual/Solution/solDescription?path=global/web-model-resource&name=scikit-learn',
					};
	           	 $http(req)
						.success(
								function(data, status, headers,
										config) {
									$scope.webscikitlearn = data.description;
								}).error(
										function(data, status, headers,
												config) {
											return "No Contents Available"
										});
				}
			
			$scope.getCmsTensorflowWebContents = function(){
	           	 var req = {
							method : 'GET',
							url : '/site/api-manual/Solution/solDescription?path=global/web-model-resource&name=tensor-flow',
					};
	           	 $http(req)
						.success(
								function(data, status, headers,
										config) {
									$scope.webtensorflow = data.description;
								}).error(
										function(data, status, headers,
												config) {
											return "No Contents Available"
										});
				}
			
			$scope.getCmsJavaWebContents = function(){
	           	 var req = {
							method : 'GET',
							url : '/site/api-manual/Solution/solDescription?path=global/web-model-resource&name=java',
					};
	           	 $http(req)
						.success(
								function(data, status, headers,
										config) {
									$scope.webjava = data.description;
								}).error(
										function(data, status, headers,
												config) {
											return "No Contents Available"
										});
				}
			
			$scope.userId = JSON.parse(localStorage.getItem("userDetail"));
			$scope.completedSteps = [];
			$scope.errorCM = ''; $scope.errorPA = ''; $scope.errorDO = ''; $scope.errorAR = ''; $scope.errorVM = '';
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
										case 'CreateMicroservice': var counter = 0; ( statusCode == 'FA' ) ?  $scope.errorCM = data[i].result : $scope.errorCM = ''; break;
										case 'Dockerize' :  var counter = 2; ( statusCode == 'FA' ) ?  $scope.errorDO = data[i].result : $scope.errorDO = ''; break;
										case 'AddToRepository' :  var counter = 4; ( statusCode == 'FA' ) ?  $scope.errorAR = data[i].result : $scope.errorAR = ''; break;
										case 'CreateTOSCA' :  var counter = 6; ( statusCode == 'FA' ) ?  $scope.errorCT = data[i].result : $scope.errorCT = ''; break;
									}
									var onboardingComponent = '.onboarding-web';
								} else {
									switch(stepName){
										case 'CheckCompatibility': var counter = 2; ( statusCode == 'FA' ) ?  $scope.errorCC = data[i].result : $scope.errorCC = ''; break;
										case 'CreateMicroservice': var counter = 4; ( statusCode == 'FA' ) ?  $scope.errorCM = data[i].result : $scope.errorCM = ''; break;
										case 'Dockerize': var counter = 4; ( statusCode == 'FA' ) ?  $scope.errorCM = data[i].result : $scope.errorCM = ''; break;
										case 'CreateTOSCA' :  var counter = 6; ( statusCode == 'FA' ) ?  $scope.errorCT = data[i].result : $scope.errorCT = ''; break;
										case 'AddToRepository' :  var counter = 8; ( statusCode == 'FA' ) ?  $scope.errorAR = data[i].result : $scope.errorAR = ''; break;
										default : var counter = -1;
									}

									var onboardingComponent = '#onap-onboarding';
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

										if( ( ( (counter === 6 && $scope.onap == false ) || (counter === 8 && $scope.onap == true) ) ) && $scope.stepfailed == false ) {
											counter = counter + 2;
											angular.element(angular.element(onboardingComponent + ' li div')[counter]).addClass('completed');
											angular.element(angular.element(onboardingComponent + ' li')[counter+1]).addClass('green completed');
											$scope.errorVM = '';
											$scope.completedSteps['ViewModel'] = 'ViewModel';
											$scope.allSuccess = true;
										}
										
										if($scope.completedSteps.indexOf(stepName) == -1 && $scope.stepfailed == false){
											width = width+20;
											angular.element('.progress .progress-bar').css({ "width" : width+'%'});
											angular.element('.onboardingwebContent').css({ "height" :'100%'});
										}
									}
								}
							}
							
							if( $rootScope.trackId != false && ( $scope.allSuccess != true && $scope.stepfailed != true ) ) {
								$scope.disableOnboardingButton = true;
							}
							
							if( $scope.allSuccess || $scope.stepfailed ){
								$scope.disableOnboardingButton = false;
								$scope.file = '';
								$interval.cancel($scope.clearInterval);
							}
						},
						function(error) {
							
					});
				
			} 
			
			$scope.addToCatalog = function(){
				
				$scope.statusReult = [];
				$scope.disableOnboardingButton = true;
				if($scope.onap == false){
					
					if($scope.disableOnboardingButton == true ){
						$rootScope.trackId = false;
						angular.element(angular.element('li div')).removeClass('completed incomplet active');
				    	angular.element(angular.element('li')).removeClass('green completed');
				    	angular.element('.progress .progress-bar').css({ "width" : '0%'});
					}
					
					$scope.addToReqObj = {
							  "request_body": {
								    "version": $scope.toolkitNameValue,
								    "name": $scope.user.name,
								  }
								};
					apiService
					.postAddToCatalog($scope.userId[1], $scope.addToReqObj)
					.then(
							function(response) {
								$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
		                        $anchorScroll(); 
		                        $scope.msg = "Onboarding process has started and it will take 30 seconds to reflect the change in status."; 
		                        $scope.icon = '';
		                        $scope.styleclass = 'c-warning';
		                        $scope.showAlertMessage = true;
		                        $timeout(function() {
		                        	$scope.showAlertMessage = false;
		                        }, 3000);
		                        
								$scope.trackId = response.data.response_detail;
								$scope.clearInterval = $interval(function(){
									$scope.showValidationStatus();
								}, 25000);
								
							},
							function(error) {
					});
				} else {
				    
					if($scope.keepModelName == true){
						// TODO : This is quick fix. Need to convert to query parameter or passed as body parameter
						$scope.model.modelName = 'null';
					}
						
					apiService
					.addToCatalogONAP($stateParams.solutionId,$stateParams.revisionId,$scope.userId[1], $scope.model.modelName)
					.then(
							function(response) {
								$scope.trackId = response.data.response_detail;
								$location.hash('onapAlert');  // id of a container on the top of the page - where to scroll (top)
		                        $anchorScroll(); 
		                        $scope.msg = "Onboarding process has started and it will take 30 seconds to reflect the change in status."; 
		                        $scope.icon = '';
		                        $scope.styleclass = 'c-warning';
		                        $scope.showAlertMessage = true;
		                        $timeout(function() {
		                        	$scope.showAlertMessage = false;
		                        }, 5000);

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
				if($scope.user){if(/*$scope.user.pass && $scope.user.name &&*/ $scope.popupAddSubmit)count++;}
				$scope.statusCount = count;
				/*if(count === 4){
					$scope.activeViewModel = true;
				}*/
			}
			 
			$scope.$watch('toolkitNameValue', function() {$scope.file=null; chkCount();});
			$scope.$watch('install', function() {chkCount();});
			$scope.$watch('file', function() {chkCount(); $scope.filename = $scope.file.name;});
			$scope.$watch('user', function() {chkCount();});
			$scope.$watch('popupAddSubmit', function() {chkCount();});
			
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
		    
			}
});