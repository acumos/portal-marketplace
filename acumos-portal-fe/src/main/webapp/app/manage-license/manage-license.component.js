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

angular
.module('AcumosApp').component(
                'manageLicense',
                {
                    templateUrl : '/app/manage-license/manage-license.template.html',
                    controller : function($scope, $mdDialog, apiService) {
                    	
             		   $scope.allTemplates = [];
            		   $scope.getAllLicenseTemplates = function(){
            			   apiService.getAllLicenseProfile()
                           .then(
                                   function(response) {                   	  
                                       if(response.data.response_body.length) {
                                    	  $scope.allTemplates = response.data.response_body;
                                    	  $scope.selectedLicense = -1;
                                       } 
                            });
            		   }
            		      
            		   $scope.getAllLicenseTemplates();
            		   
		           		$scope.getRTUUrl = function(){
		
		       			 apiService.getDockerProperty('portal.feature.rtueditor.url')
		       		        .then(function(response){ 
		       		     	  $rootScope.rtuUrl = $sce.trustAsResourceUrl(response.data.response_body + '?mode=iframe');
		
		       		        });
		           		}  
		           		$scope.getRTUUrl();
           		
                    	$scope.launchRTU = function(){
                    		$mdDialog.show({
	    						controller: function DialogController($scope, $mdDialog) {
	    							$scope.closeDialog = function() {
	    								$mdDialog.hide();
	    							};
	    						},
	    						templateUrl:'./app/manage-license/rtu-editor-dialog.template.html',
	    						parent: angular.element(document.body),
	    						targetEvent: event,
	    						clickOutsideToClose:false
	    					});
                    	}
                    	
                    	$scope.createNewLicenseProfileTemplate = function(){
                    		$mdDialog.show({
	    						controller: function DialogController($scope, $mdDialog) {
	    							$scope.closeDialog = function() {
	    								$mdDialog.hide();
	    							};
	    						},
	    						templateUrl:'./app/modular-resource/license-profile-editor-dialog.template.html',
	    						parent: angular.element(document.body),
	    						targetEvent: event,
	    						clickOutsideToClose:false
	    					});
                    	}
                    	
                    	var winMsgHandler = function(event) {
        					// message listener
        					if (event.data.key === 'output') {
        						var licenseText = JSON.stringify(event.data.value);	
        						$scope.licenseOption = 'selectLicProfile';
        						$scope.createLicenseFile(licenseText);
        						$mdDialog.hide();
        					} else if (event.data.key === 'action') {
        						if (event.data.value === 'cancel') {
        							$mdDialog.hide();
        						}
        					} else if (event.data.key === 'init_iframe') {
        						// if licenseProfileEditorInitMsg then send me
        						var iframe = document.getElementById('iframe-license-profile-editor');

        						if (selLicProfileTplMsg && iframe) {
        							// send message to License Profile Editor iframe
        							iframe.contentWindow.postMessage(selLicProfileTplMsg, '*');
        						}
        					}
        				},
        				bindEvent = function(element, eventName, eventHandler) {
        					if (element.addEventListener) {
        						element.addEventListener(eventName, eventHandler, false);
        					} else if (element.attachEvent) {
        						element.attachEvent('on' + eventName, eventHandler);
        					}
        				},
        				unbindEvent = function(element, eventName, eventHandler) {
        					if (element.removeEventListener) {
        						element.removeEventListener(eventName, eventHandler, false);
        					} else if (element.detachEvent) {
        						element.detachEvent('on' + eventName, eventHandler);
        					}
        				};
        				
        				if (window.licProfEdMsgHandlerRef) {
        					unbindEvent(window, 'message', window.licProfEdMsgHandlerRef);
        				}
        				bindEvent(window, 'message', winMsgHandler);
        				window.licProfEdMsgHandlerRef = winMsgHandler;
                    }
                });
