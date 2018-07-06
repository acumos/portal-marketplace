/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
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

/*
 * angular .module('AcumosApp', ['ui.router'])
 */
app.config(function($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, ngQuillConfigProvider, $authProvider/*, socialProvider*/){
	//$locationProvider.html5Mode(true);
	ngQuillConfigProvider.set();
	$urlRouterProvider.when('','/home');
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
			params: {solutionId: null,
				deployStatus:null}
         })
		.state('modularResource', {
			url: '/modelerResource?ONAP=?solutionId=?revisionId=?',
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
		.state('termsCondition', {
			url: '/termsCondition',
			component: 'termsCondition'
		})
		;
	
		$authProvider.facebook({
			 clientId: '1013632248810824',
	    	  authorizationEndpoint: 'https://www.facebook.com/dialog/oauth',
	    	  responseType: 'token',
	    	  redirectUri: window.location.origin,
	          requiredUrlParams: ['scope'],
	          optionalUrlParams: ['display'],
	          scope: ['profile', 'email'],
	          scopePrefix: 'openid',
	          scopeDelimiter: ' ',
	          popupOptions: { width: 500, height: 600 },
	          //optionalUrlParams: ['access_type'],
	          accessType: 'offline'

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
	    	clientId:'1587275085f20e9a68bc',
    		redirectUri: window.location.origin, //http://localhost:8085/callback
          authorizationEndpoint:'https://github.com/login/oauth/authorize',
          scope: ['user'],
          url:'http://localhost:8085',
          requiredUrlParams: ['user'],
          optionalUrlParams: ['scope'],
          popupOptions: { width: 500, height: 600 },
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
    		/*This is added for resolving mdl search related issues */
    		componentHandler.upgradeAllRegistered();
    		
    		//console.log('app.config.js->$rootScope.sidebarHeader: ' + $rootScope.sidebarHeader)
    	 	$rootScope.sidebarHeader = false;

    	 	//console.log('#######app.config.js->$rootScope.sidebarHeader: ' + $rootScope.sidebarHeader)
    	 	//$scope.sidebarHeader = false;
		   if (sessionStorage.getItem("userDetail")) {
				//console.log("Yes");
				$rootScope.sidebarHeader = true;
				//$scope.sidebarHeader = true;
			}
		   else{
			   //console.log("No");
			   $rootScope.sidebarHeader = false;
			   //$scope.sidebarHeader = false;
		   }
		    angular.element('.mdl-textfield').removeClass('is-focused');
			angular.element('.mdl-textfield').removeClass('is-upgraded');
			angular.element('.mdl-textfield').removeClass('is-dirty');
			angular.element('.mdl-textfield__expandable-holder input').val('');
			//$rootScope.valueToSearch = '';
        
    });
})