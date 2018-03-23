app.factory('authenticationInterceptor', function ($q, $state, $rootScope) {
	var accessError = false;
  return {
    request: function (config) {
      config.headers = config.headers;
      if (localStorage.getItem('auth_token')) {
        config.headers.Authorization = 'Bearer ' + localStorage.getItem('auth_token');
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
    		alert("Please sign in to application.");
    		$rootScope.showAdvancedLogin();
    		$state.go('home');
    		return $q.reject(response);
          }else{
        	  return $q.reject(response);
          }
    	
    }
  };
});