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

app.component('marketFooter',{
	templateUrl : '/app/footer/md-footer.template.html',
	controller : function(apiService, $scope) { 
		
		$scope.loadCategory = function() {
			apiService
					.getVersion()
					.then(
							function(response) {
								$scope.responseData = response.data;
								if($scope.responseData.status == 200){
									$scope.versionNumber = $scope.responseData.data	
								}else{
									$scope.versionNumber = "Version number not fetched."
								}
							},
							function(error) {
								console.warn($scope.status);
								$scope.versionNumber = "Error: Version number not fetched."
							});
		}
		$scope.loadCategory();
		
		apiService.getDocUrl().then( function(response){
			$scope.docUrl = response.data.response_body;
		});
		
	},

});