/*
===============LICENSE_START=======================================================
Acumos  Apache-2.0
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

app.factory('authenticationInterceptor', function ( $q, $state, $rootScope, $injector ) {
	var accessError = false;
  return {
    request: function (config) {
      config.headers = config.headers;
      if (sessionStorage.getItem('auth_token')) {
        config.headers.Authorization = 'Bearer ' + sessionStorage.getItem('auth_token');
      }
      if(sessionStorage.getItem('provider'))
          config.headers.provider = sessionStorage.getItem('provider');
      return config;
    },
    response: function (response) {
      return response || $q.when(response);
    },
    responseError: function (response){
    	
    	if (response.status === 401 && accessError == false/* && response.config.url != 'api/admin/config/site_config'*/) {
            //session token expired or unauthorized access
    		accessError = true;
    		sessionStorage.removeItem('authToken');
    		sessionStorage.clear();
    		localStorage.clear();
    		//alert("Please sign in to application.");
    		modalService = $injector.get('$mdDialog'); 
    		modalService.show({
    			 templateUrl: '../app/header/sign-in-promt-modal-box.html',
    			 clickOutsideToClose: true,
    			 controller : function DialogController($scope ) {
    				 $scope.closeDialog = function() {
    					 modalService.hide();
    					 $rootScope.showAdvancedLogin();
    			    	 $state.go('home');
    		     } }
    			});

    		return $q.reject(response);
          }else{
        	  return $q.reject(response);
          }
    	
    }
  };
});