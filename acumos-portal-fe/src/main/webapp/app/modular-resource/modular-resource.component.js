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
		controller:function($scope,$location,apiService,$http, modelUploadService, $interval, $anchorScroll, $state, $rootScope){
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
				if(close == false){
					angular.element('.onboardingwebContent').css({'height':angular.element('.onboardingwebContent').height()+180 } ); 
				}
				if(close == true){
					angular.element('.onboardingwebContent').css({'height':angular.element('.onboardingwebContent').height()-90 } ); 
				}
			}
			$scope.fileSubmit = false;
			$scope.fileUpload = function(){
				//$scope.uploadModel = false;
				var file = $scope.file;
				var userId = JSON.parse(localStorage.getItem("userDetail"));
				/*var fd = file;*/
				/*var fd = new FormData();
				fd.append('file', file);
				
				console.log(file);*/
				
				/*apiService
				.insertmodelFileUpload(userId, file)
				.then(
						function(response) {
							console.log(response.data.response_body);
						},
						function(error) {
							console.log("Error in uploading file");
						});*/
				
				
				/*,userId:userId[1]*/
				var uploadUrl = "api/model/upload/" + userId[1];
				var promise = modelUploadService.uploadFileToUrl(
						file, uploadUrl);
				$scope.tempfilename = file.name;
				$scope.filename = '';
				promise
				.then(
						function(response) {
							$scope.fileSubmit = true;
							$scope.filename = $scope.tempfilename;
							$rootScope.progressBar = 0;
							chkCount();
							$scope.uploadModel = false;
						},
						function() {
							$rootScope.progressBar = 0;
							$scope.serverResponse = 'An error has occurred';
							$scope.uploadModel = false;
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
			
			
			
			$scope.addToCatalog = function(){
				/*$scope.addToReqObj = {
						  "request_body": {
							    "accessType": "PR",
							    "activeType": "Y",
							    "modelToolkitType": $scope.toolkitNameValue,
							    "name": $scope.user.name,
							  }
							}*/
				
				$scope.userId = JSON.parse(localStorage.getItem("userDetail"));
				$scope.statusReult = [];
				$scope.addToReqObj = {
						  "request_body": {
							    "version": $scope.toolkitNameValue,
							    "name": $scope.user.name,
							  }
							}
				
				$scope.completedSteps = [];
				$scope.errorCM = ''; $scope.errorPA = ''; $scope.errorDO = ''; $scope.errorAR = ''; $scope.errorVM = '';

				$scope.showValidationStatus = function(){
					var counter = 1;
					
					apiService
					.getMessagingStatus($scope.userId[1], $scope.trackId ).then(
							function(reponse) {
								var data = reponse.data.response_body;
								var stepfailed = false;
								var width = 0;
								for(var i=0 ; i< data.length; i++){
									var stepName = data[i].name;
									var statusCode =  data[i].statusCode;
									var stepCode = data[i].stepCode;

									switch(stepName){
										case 'CreateMicroservice': var counter = 0; ( statusCode == 'FA' ) ?  $scope.errorCM = data[i].result : $scope.errorCM = ''; break;
										case 'Dockerize' :  var counter = 2; ( statusCode == 'FA' ) ?  $scope.errorDO = data[i].result : $scope.errorDO = ''; break;
										case 'AddToRepository' :  var counter = 4; ( statusCode == 'FA' ) ?  $scope.errorAR = data[i].result : $scope.errorAR = ''; break;
										case 'CreateTOSCA' :  var counter = 6; ( statusCode == 'FA' ) ?  $scope.errorCT = data[i].result : $scope.errorCT = ''; break;
										case 'ViewModel' :  var counter = 8; ( statusCode == 'FA' ) ?  $scope.errorVM = data[i].result : $scope.errorVM = '';
									}
									angular.element(angular.element('.onboarding-web li div')[counter]).removeClass('completed incomplet active');
									if(statusCode == 'FA'){
										angular.element(angular.element('.onboarding-web li div')[counter]).addClass('incomplet');
										angular.element(angular.element('.onboarding-web li')[counter+1]).removeClass('green completed');
										stepfailed = true;
									}else if(statusCode == 'ST'){
										angular.element(angular.element('.onboarding-web li div')[counter]).addClass('active');
										angular.element(angular.element('.onboarding-web li')[counter+1]).addClass('progress-status green')
										
									} else if(statusCode == 'SU'){
										angular.element(angular.element('.onboarding-web li div')[counter]).addClass('completed');
										angular.element(angular.element('.onboarding-web li')[counter+1]).addClass('green completed');
										$scope.completedSteps[stepName] = stepName;

										if (counter === 6) {
											counter = counter +2;
											angular.element(angular.element('.onboarding-web li div')[counter]).addClass('completed');
											angular.element(angular.element('.onboarding-web li')[counter+1]).addClass('green completed');
											$scope.errorVM = '';
											$scope.completedSteps['ViewModel'] = 'ViewModel';
										}
										
										if($scope.completedSteps.indexOf(stepName) == -1 && stepfailed == false){
											width = width+20;
											angular.element('.progress .progress-bar').css({ "width" : width+'%'});
											angular.element('.onboardingwebContent').css({ "height" :'100%'});
										}
									}
								}
							},
							function(error) {
								
						});
					
						var allStepsCount = Object.keys($scope.completedSteps);
						if($scope.completedSteps && allStepsCount.length == 5 ){
							$interval.cancel($scope.clearInterval);
						}
					
				} 
				
				apiService
				.postAddToCatalog($scope.userId[1], $scope.addToReqObj)
				.then(
						function(response) {
							$scope.trackId = response.data.response_detail;
							//alert("Onboarding process started successfully with track id " + $scope.trackId + ". It may take some time to onboard your solution. \nPlease check notification to know the status of your solution.")
							$scope.catalogResponse = response.data;						
							
							$scope.clearInterval = $interval(function(){
								$scope.showValidationStatus();
							}, 5000);
							
						},
						function(error) {
							$scope.catalorError = error.data;
						});

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
				if($scope.file && $scope.fileSubmit)count++;
				if($scope.user){if(/*$scope.user.pass && $scope.user.name &&*/ $scope.popupAddSubmit)count++;}
				$scope.statusCount = count;
				/*if(count === 4){
					$scope.activeViewModel = true;
				}*/
			}
			 
			$scope.$watch('toolkitNameValue', function() {$scope.file=null; chkCount();});
			$scope.$watch('install', function() {chkCount();});
			$scope.$watch('file', function() {chkCount();});
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