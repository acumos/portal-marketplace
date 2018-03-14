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