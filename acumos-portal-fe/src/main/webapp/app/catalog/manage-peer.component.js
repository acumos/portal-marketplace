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
		.module('managePeer', [ 'jkAngularRatingStars' ])
		.component(
				'managePeer',
				{

					templateUrl : './app/catalog/manage-peer.template.html',
					controller : function($scope, $location, $http, $rootScope,
							$stateParams, $sessionStorage, $localStorage,
							$mdDialog, $state, $window, apiService, $anchorScroll, $timeout, $document, $filter, $sce, browserStorageService) {
						
						$scope.catalogName = $stateParams.catalogName;
						$scope.catalogId = $stateParams.catalogId;
						$scope.grantPeersSelected = [];
						$scope.peersSelected = [];
						$scope.selectAllPeer = false; 
						$scope.selectAll = false;
						
						
						$scope.removePeerDialog = function(){
							 
							$mdDialog.show({
								  contentElement: '#removeDialogBox',
								  parent: angular.element(document.body),
								  clickOutsideToClose: true,
								  
							  });
						 };
						
						
						 $scope.closePoup = function(){
							 	$mdDialog.hide();
							 	$('input:checkbox').removeAttr('checked');
							 	$scope.searchPeer = "";
							 	$scope.grantPeersSelected = [];
							 	$scope.peersSelected = [];
						};
						
							$scope.grantAccessDialog = function(){
							 
							$mdDialog.show({
								  contentElement: '#dialogBox',
								  parent: angular.element(document.body)
								 
								  
							  });
						 };
		
						 $scope.accessedPeerIds = [];
						 $scope.getAccessedPeers = function(){
							 $rootScope.setLoader = true;
							 apiService
		                     .getAccessedPeers($scope.catalogId)
		                     .then(
		                         function (response) {
		                        	 $scope.accessedPeers= response.data.response_body;
		                        	 $scope.accessedPeerIds = $scope.accessedPeers.map(function(item){
		                        		 return item.peerId;
		                        	 });
		                        	 $rootScope.setLoader = false;
		                        	 $scope.grantAccess();
		                        	 	  
		                        	 
		                         },
		                         function (error) { console.log(error); });
							
						}
						
						$scope.getAccessedPeers();
						
						$scope.removeSelected = function(selected){
							if(selected.Selected == true)
	            			{
								$scope.peersSelected.push(selected.peerId);
	            			
	            			}else{
	            				$scope.peersSelected.pop(selected.peerId);
	            				
	            			}
							 if($scope.selectAllPeer == true){
			            	   	   $scope.selectAllPeer = false;   
	            	    		   $scope.selectAllPeerStatus = false;
	            	    	 }
				            }
						
						
						$scope.removeAll = function(selected){
							$scope.selectAllPeer = selected;
			            	$scope.selectAllPeerStatus = true;
			            	for (var i = 0; i < $scope.accessedPeers.length; i++) {
			            		$scope.accessedPeers[i].Selected = $scope.selectAllPeer;
			            		if($scope.selectAllPeer){
			            			$scope.peersSelected.push($scope.accessedPeers[i].peerId);
			            		}
			            	}
			            	if($scope.selectAllPeer == false){
			            		$scope.peersSelected=[];
			            		}
			            	};
			            	
			            	
			            	$scope.peerIds=[];
			            	$scope.result = [];
			            	$scope.peers1= [];
			            	
			            	$scope.grantAccess = function(){
			            		$rootScope.setPeerLoader = true;
			            		var obj = { "fieldToDirectionMap": {}, "page": 0, "size": 0 };
			            		 apiService
			                     .getPeers(obj)
			                     .then(
			                         function (response) {
			                        	 $rootScope.setPeerLoader = false;
			                        	 $scope.peers= response.data.response_body.content;
			                        	 for(var i=0;i< $scope.peers.length;i++)
			                        	 {
			                        		 if($scope.peers[i].statusCode == "AC")
			                        			 {
			                        			 $scope.peers1.push($scope.peers[i]);
			                        			 }
			                        		 
			                        		 }
			                        	 $scope.availablePeers = $scope.peers1.filter(x =>!$scope.accessedPeerIds.includes(x.peerId));
			                        });
								
							}
			            	
			            	$scope.grantAccessToPeers = function(){
			            		$rootScope.setPeerLoader = true;
			            		$scope.reqBody = {
			                            "request_body": $scope.grantPeersSelected
			                            }
			            		 apiService.addGrantAccess($scope.catalogId,$scope.reqBody)
			                     .then(
			                         function successCallback(response) {
			                        	 $rootScope.setPeerLoader = false;
			                        	$scope.availablePeers = $scope.availablePeers.filter(x =>!$scope.grantPeersSelected.includes(x.peerId));
			                        	for(var i=0;i<$scope.grantPeersSelected.length;i++){
			                        		$scope.accessedPeerIds.push($scope.grantPeersSelected[i]);
			                        	}
			                        	$scope.accessedPeers = $scope.peers1.filter(x =>$scope.accessedPeerIds.includes(x.peerId));
			                        	for(var i=0;i<$scope.accessedPeers.length;i++){
			                        		$scope.accessedPeers[i].Selected=false;
			                        	}
			                        	$scope.grantPeersSelected = [];
			                        	$mdDialog.hide();
			                        	$location.hash('managePeers');
			                        	$anchorScroll();
			                            $scope.msg = response.data.response_detail;
			                        	$scope.styleclass = 'c-success';
			                            $scope.icon = '';
			                            $scope.showSuccessMessage = true;
			                             $timeout(
			                                 function () {
			                                     $scope.showSuccessMessage = false;
			                                 }, 3000);
			                             $('input:checkbox').removeAttr('checked');
			                        	 
			                         },
			                         function errorCallback(response) {
			                        	 console.log();
			                         });

			            		
			            	}
			            	
			            	$scope.removeIndividualPeer = function(abc)
			            	{
			            		$scope.peersSelected.push(abc.peerId);
			            		$scope.removePeerDialog();
			            		
			            	}
			            	
			            	$scope.removeAccessToPeers = function(){
			            		$rootScope.setPeerLoader = true;
			            		$scope.reqBody1 = {
			                            "request_body": $scope.peersSelected
			                            }
			            		 apiService.removeAccess($scope.catalogId,$scope.reqBody1)
			                     .then(
			                         function successCallback(response) {
			                        	 $rootScope.setPeerLoader = false;
			                        	 	$scope.accessedPeerIds = $scope.accessedPeerIds.filter(x =>!$scope.peersSelected.includes(x));
			                        	 	$scope.availablePeers = $scope.peers1.filter(x =>!$scope.accessedPeerIds.includes(x.peerId));
				                        	$scope.accessedPeers = $scope.peers1.filter(x =>$scope.accessedPeerIds.includes(x.peerId));
				                        	for(var i=0;i<$scope.availablePeers.length;i++){
				                        		$scope.availablePeers[i].Selected=false;
				                        	}
				                        	$scope.peersSelected = [];
				                        	$mdDialog.hide();
				                        	$location.hash('managePeers');
				                        	$anchorScroll();
				                            $scope.msg = response.data.response_detail;
				                        	$scope.styleclass = 'c-success';
				                            $scope.icon = '';
				                            $scope.showSuccessMessage = true;
				                            $timeout(
				                                 function () {
				                                     $scope.showSuccessMessage = false;
				                                 }, 3000);
				                            $('input:checkbox').removeAttr('checked');
				                        	 
			                         },
			                         function errorCallback(response) {
			                        		console.log();
			                         });

			            		
			            	}
			            	
			            	$scope.grantAccessToSelected = function(selected){
			            		if(selected.Selected == true)
		            			{$scope.grantPeersSelected.push(selected.peerId);
		            			
		            			}else{
		            				
		            				$scope.grantPeersSelected.pop(selected.peerId);
		            			}
		            		
				               if($scope.selectAll == true){
				            	   	   $scope.selectAll = false;   
		            	    		   $scope.selectAllStatus = false;
		            	    	 }
				            }
			            	
						
						$scope.grantAccessToAll = function(selected){
			            	$scope.selectAll = selected;
			            	$scope.selectAllStatus = true;
			            	for (var i = 0; i < $scope.availablePeers.length; i++) {
			            		$scope.availablePeers[i].Selected = $scope.selectAll;
			            		if($scope.selectAll){
			            			$scope.grantPeersSelected.push($scope.availablePeers[i].peerId);
			            		}
			            	}
			            	if($scope.selectAll == false){
			            		$scope.grantPeersSelected=[];
			            		}
			            	};
						
						}
				});



