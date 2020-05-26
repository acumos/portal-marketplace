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
                    controller : function($scope, $mdDialog, apiService, browserStorageService, $rootScope ,$sce, $timeout ) {
                    	
             		   $scope.allTemplates = [];
            		   $scope.getAllLicenseTemplates = function(){
            			   $rootScope.setLoader = true;
            			   apiService.getAllLicenseProfile()
                           .then(
                                   function(response) {
                                	   $rootScope.setLoader = false;
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
	    						clickOutsideToClose:true
	    					});
                    	}
                    	
                    	$scope.createNewLicenseProfileTemplate = function(isEdit){
                    		$scope.isEdit = isEdit;
                    		
                    		var onCompleteLicProfileTplDialog = function(scope, element, options) {
        						
                        		if(!isEdit){
                        			var iframe = document.getElementById('iframe-license-profile-editor');
            							// send message to License Profile Editor iframe
                        				selLicProfileTplMsg = " ";
            							iframe.contentWindow.postMessage(selLicProfileTplMsg, '*');

                        		}      						
        						
        					};      					
                    		
                    		$mdDialog.show({
	    						controller: function DialogController($scope, $mdDialog) {
	    							$scope.closeDialog = function() {
	    								$mdDialog.hide();
	    							};
	    						},
	    						templateUrl:'./app/modular-resource/license-profile-editor-dialog.template.html',
	    						parent: angular.element(document.body),
	    						targetEvent: event,
	    						clickOutsideToClose:false,
	    						onComplete: onCompleteLicProfileTplDialog
	    					});
                    	}
                    	
                    	$scope.isEdit = false;
                    	var userId = JSON.parse(browserStorageService.getUserDetail())[1];
                    	$scope.createLicenseTemplate  = function(licenseText, templateName){
                    		
                    		var request = {
                    				"request_body":{
                    					"template" : licenseText,
                    					"templateName" : templateName,
                    					"userId" : userId
                    				}
                    		}
                    		
             				if(licenseText){
             					if($scope.isEdit == false){
             						apiService.createLicenseProfileTemplate(request)
             		               .then(function(response){
             		            	    $scope.getAllLicenseTemplates();
             		            	    $scope.msg = "License profile template added successfully."; 
             							$scope.icon = '';
             							$scope.styleclass = 'c-success';
             							$scope.showAlertMessage = true;
             							$timeout(function() {
             								$scope.showAlertMessage = false;
             							}, 6000);

             		             });
             					} else {
             						request.request_body.templateId = $scope.templateId;
             						apiService.updateLicenseProfileTemplate(request)
               		               .then(function(response){
               		            	   	$scope.getAllLicenseTemplates();
               		            	    $scope.msg = "License profile template updated successfully."; 
               							$scope.icon = '';
               							$scope.styleclass = 'c-success';
               							$scope.showAlertMessage = true;
               							$timeout(function() {
               								$scope.showAlertMessage = false;
               							}, 6000);

               		             });
             					}
             				}
                    	}
                    	
                    	var selLicProfileTplMsg = '';
                    	$scope.modifyLicenseProfileTemplate = function(event) {

            					var selectedLic = $scope.allTemplates[$scope.selectedLicense];
            					var template = JSON.parse(selectedLic.template);
            					$scope.templateId =  selectedLic.templateId;
            					if (selectedLic) {
            						try {
            						var msgObj = {
            							"key": "input",
            							"value": template
            						};
            						selLicProfileTplMsg = msgObj;
            						
            						var iframe = document.getElementById('iframe-license-profile-editor');

            						if (selLicProfileTplMsg && iframe) {
            							// send message to License Profile Editor iframe
            							iframe.contentWindow.postMessage(selLicProfileTplMsg, '*');
            						}
            						
            						$scope.createNewLicenseProfileTemplate(true);
            					} catch (e) {
            						console.error("failed parsing license profile template input", e);
            					}
            				}
            			};
            			
                    	
                    	var winMsgHandler = function(event) {
        					// message listener
        					if (event.data.key === 'output') {
        						var licenseText = JSON.stringify(event.data.value);	
        					
        						$scope.createLicenseTemplate(licenseText, event.data.value.keyword);
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
									if($scope.isEdit){
										iframe.contentWindow.postMessage(selLicProfileTplMsg, '*');
									}       							
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

angular
.module('AcumosApp').filter('templateName',function(){
    return function(input)
    {
          var templateName = ((input).templateName);
          if(JSON.parse(input.template).copyright.company){
        	  templateName += " - "  + JSON.parse(input.template).copyright.company;
          }
          return templateName;
    }
});
