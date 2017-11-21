'use strict';

/*
 * angular .module('AcumosApp', ['ui.router'])
 */
app.config(function($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, ngQuillConfigProvider, $authProvider/*, socialProvider*/){
	//$locationProvider.html5Mode(true);
	ngQuillConfigProvider.set();
	$urlRouterProvider.otherwise('/404Error');
	$httpProvider.interceptors.push('authenticationInterceptor');
	$stateProvider
		 .state('404Error', {
	         url: '/404Error',
	         //template:'<h1>404 Error</h1>',
	         templateUrl: '/app/error-page/error-404.template.html',
	         //component: '404Error'
	     })
		.state('home', {
			url: '/home',
			component: 'marketHome'
		})
		.state('admin', {
			url: '/admin',
			component: 'admin'
		})
		.state('userDetail', {
			url: '/userDetail',
			component: 'userDetail'
		})
		.state('forgotPswd', {
			url: '/forgotPswd',
			component: 'forgotPswd'
		})
		.state('marketPlace', {
			url: '/marketPlace',
			component: 'marketPlace',
			
		})
		.state('manageModule', {
			url: '/manageModule',
			component: 'manageModule',
			
		})
		.state('designStudio', {
			url: '/designStudio',
			component: 'designStudio'
		})
		// marketSolutions
		.state('marketSolutions', {
			url: '/marketSolutions?solutionId',
			component: 'modelDetails',
			params: {
				solutionId: null,
				parentUrl: null
				}
			//template: '<model-details></model-details>'
				//templateUrl: './app/model-details/model-details.template.html' 
         })
          .state('modelEdit', {
			url: '/modelEdit',
			component: 'modelEdit',
			params: {solutionId: null}
         })
		.state('modularResource', {
			url: '/modelerResource',
			component: 'modelResource'
		})
		.state('resetPswd', {
			url: '/resetPswd',
			component: 'resetPswd'
		})
		.state('adminConfig', {
			url: '/adminConfig',
			component: 'adminConfig'
		})
		.state('peerConfig', {
			url: '/peerConfig',
			component: 'peerConfig'
		})
		.state('notificationModule', {
			url: '/notificationModule',
			component: 'notificationModule'
		})
		.state('attGlobalLogin', {
			url: '/attGlobalLogin',
			component: 'attGlobalLogon'
		})
		.state('qanda', {
			url: '/qanda',
			component: 'qanda'
		})
		.state('termsCondition', {
			url: '/termsCondition',
			component: 'termsCondition'
		})
		;
	
		$authProvider.facebook({
	      clientId: '657854390977827'
	    });

	    $authProvider.google({
	    	 // clientId: '268793520077-crpk6sspb8rob0gs33cgh3hjdfah4iv6.apps.googleusercontent.com',
	         // clientId: '719388764800-sm4ddoku49cl88dl47h0s031968lvd6l.apps.googleusercontent.com',
	          //vlcH1U3F0KGuycEesB0et37K
	          //clientId:'327439217798-e05g1c5ive01i867ikc1lrvcc4tjcsk0.apps.googleusercontent.com',
	    	  clientId:'805276053124-mohnn1eltto7phq51mko2runo7aa04n6.apps.googleusercontent.com',
	          authorizationEndpoint: 'https://accounts.google.com/o/oauth2/auth',
	    	  url: 'http://localhost:8083/oauth/login',
	          responseType: 'token',
	          //default:'code',
	          //redirectUri: window.location.origin,//'http://localhost:8085', //http://localhost:8085/callback //url given in google api configuration. this should match 
	          requiredUrlParams: ['scope'],
	          optionalUrlParams: ['display'],
	          scope: ['profile', 'email'],
	          scopePrefix: 'openid',
	          scopeDelimiter: ' ',
	          popupOptions: { width: 500, height: 600 },
	          //optionalUrlParams: ['access_type'],
	          accessType: 'offline'
	        });
	    $authProvider.github({
	          /*clientId: '5a1c39acf1e13a80b5e1',
	          redirectUri: '/manageModule' //http://localhost:8085/callback
	           */
	    		clientId:'be9d253aac96d4c9627a',
	    		redirectUri: window.location.origin, //http://localhost:8085/callback
	          //authorizationEndpoint:'https://api.github.com',
	          scope: ['user:email'],
	          url:'http://localhost:8085',
	          optionalUrlParams: ['scope'],
	          responseType: 'token'
	    	});
	    
	    /*socialProvider.setGoogleKey("719388764800-sm4ddoku49cl88dl47h0s031968lvd6l.apps.googleusercontent.com");*/
	    
}).run(function($rootScope, $state, $location) {
    $rootScope.$state = $state;
    $rootScope.$on("$locationChangeStart", function(event, next, current) { 
        /*if($location.path() === "/home"){
        	$rootScope.toggleSideNavBar = false;
        } else {
        	$rootScope.toggleSideNavBar = true;
        }*/
    	 	$rootScope.sidebarHeader = false;
    	 	angular.element('#fixed-header-drawer-exp1').val('');
    	 	angular.element('#fixed-header-drawer-exp').val('');

    	 	//$scope.sidebarHeader = false;
		   if (localStorage.getItem("userDetail")) {
				console.log("Yes");
				$rootScope.sidebarHeader = true;
				//$scope.sidebarHeader = true;
			}
		   else{
			   console.log("No");
			   $rootScope.sidebarHeader = false;
			   //$scope.sidebarHeader = false;
		   }
        
    });
})
