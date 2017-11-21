    
 app.factory('productService', function($http,$rootScope) {
	 var test ={};
	 return {
		 	setData: function(param) {
	           this.test = param;
	         //  $rootScope.$broadcast("signIn");
		 	},
		 	getData : function(){
		 		return test;
		 	}
	        
	 }
});