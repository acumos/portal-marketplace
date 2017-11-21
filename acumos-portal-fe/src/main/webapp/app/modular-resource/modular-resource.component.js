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
		controller:function($scope,$location,apiService,$http, modelUploadService, $interval){
			//alert(localStorage.getItem("userDetail"));
		
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
				$scope.uploadModel = false;
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
							chkCount();
						},
						function() {
							$scope.serverResponse = 'An error has occurred';
						});
			}
			
			$scope.closePoup = function(){
				$scope.uploadModel = !$scope.uploadModel;
				$scope.file = false;
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
				$scope.addToReqObj = {
						  "request_body": {
							    "version": $scope.toolkitNameValue,
							    "name": $scope.user.name,
							  }
							}
				
				apiService
				.postAddToCatalog($scope.userId[1], $scope.addToReqObj)
				.then(
						function(response) {
							alert("Onboarding process started. It may take some time to onboard your solution. \nPlease check notification to know the status of your solution.")
							$scope.catalogResponse = response.data;
							
							var counter = 1;
							$interval(function() {
								angular.element(angular.element('.onboarding-web li div')[counter-3]).removeClass('active');				
								angular.element(angular.element('.onboarding-web li div')[counter-1]).addClass('active');
								angular.element(angular.element('.onboarding-web li')[counter]).addClass('progress-status green')
								angular.element(angular.element('.onboarding-web li')[counter-2]).addClass('completed');
								counter = counter + 2;
							}, 5000, 6);
						},
						function(error) {
							$scope.catalorError = error.data;
						});
				
				/*apiService
				.getModelerResourcesContent(addToReqObj)
				.then(
						function(response) {
							$scope.catalogResponse = response.data;
						},
						function(error) {
							$scope.catalorError = error.data;
						});*/
				
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
			}
			 
			$scope.$watch('toolkitNameValue', function() {$scope.file=null; chkCount();});
			$scope.$watch('install', function() {chkCount();});
			$scope.$watch('file', function() {chkCount();});
			$scope.$watch('user', function() {chkCount();});
			$scope.$watch('popupAddSubmit', function() {chkCount();});
		}
});
