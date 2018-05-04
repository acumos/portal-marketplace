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
        .module('peerConfig', ['ui.bootstrap', 'infinite-scroll'])
        .factory('modalFactory', function($uibModal) {
			    return {
			      open: function(size, template, params) {
			        return $uibModal.open({
			          animation: true,
			          templateUrl: template ,
			          controller: 'ModalResultInstanceCtrl',
			          size: size,
			          windowClass: 'modal fade peerpopup  in',
			          resolve: {
			            params: function() {
			            	console.log(params.peerId);
			              return params;
			            }
			          }
			        });
			      }
			    };
			  }).service('sharedProperties', function () {
			        var property = {};

			        return {
			            getProperty: function () {
			                return property;
			            },
			            setProperty: function(value) {
			                property = value;
			            }
			        };
			    })
        .component(
                'peerConfig',
                {
                    templateUrl : '/app/peer-configuration/peer-configuration.template.html',
                    controller : function($scope, $uibModal, apiService, sharedProperties) {
                        var $ctrl = this;
                        $scope.page = 0;
                        $scope.size = 8;
                        $scope.peers = [];
                        this.busy = false;
                        $scope.ribbonShow = false;
                        $scope.ribbonMsg = "Peer updated successfully";
                        


                        $scope.loadMore = function() {
                        	if (this.busy) return;
                            this.busy = true;
                        	
                        	var dataObj = {
                                    "fieldToDirectionMap": {},
                                    "page": $scope.page,
                                    "size": $scope.size
                            	};
                            apiService
                                .getPeers(dataObj)
                                .then(
                                        function(response){
                                        	
                                            console.log(response);
                                            
                                            	if($scope.page == 0)
                                            		$scope.peers = response.data.response_body.content;
                                            	else {
                                            		//var respData = response.data.response_body.content;
                                            		for(var i=0; i<response.data.response_body.content.length; i++)
                                            			$scope.peers.push(response.data.response_body.content[i]);
                                            	}
                                            console.log($scope.peers);
                                            console.log($scope.page);
                                            $scope.page = $scope.page + 1;
                                            sharedProperties.setProperty($scope.peers);
                                        },
                                        function(error){
                                        	console.log(error);
                                        }
                                );
                            this.busy = false;
                        }
                        //$scope.loadMore();

                        $scope.open = function() {
                            var modalInstance = $uibModal.open({
                                templateUrl: 'addEditPeerModal.html',
                                controller: 'ModalInstanceCtrl',
                                windowClass: 'modal fade peerpopup  in',
                                resolve: {
                                    params: function() {
                                      return null;
                                    }
                                 }
                            });
                          };

                          $scope.updatePeer = function(peerId, idx) {
                        	  $scope.peerId = peerId;
                        	  $scope.idx = idx;
                      	    			apiService
                                  	    	.getPeer(peerId)
                                  	    	.then(function(data){
                                  	    		console.log("Update Peer Id : " + peerId);
                                  	    		$scope.editPeer = data.data.response_body;
                                  	    		var modalInstance = $uibModal.open({
                                                    templateUrl: 'addEditPeerModal.html',
                                                    controller: 'ModalInstanceCtrl',
                                                    windowClass: 'modal fade peerpopup  in',
                                                    resolve: {
                                                        params: function() {
                                                          return {param : $scope.peerId, idx:$scope.idx};
                                                        }
                                                     }
                                                });
                                  	    		
                                      	    }, 
                                      	    function(error){
                                      	    	// handle error
                                      	    });
                      	    		
                        	  
                            };
                            
                            $scope.deletePeer = function(peerId, idx) {
                            	$scope.idx = idx;
                            	$scope.peerId = peerId;
                                var modalInstance = $uibModal.open({
                                    templateUrl: 'deletePeerModal.html',
                                    controller: 'ModalInstanceCtrl',
                                    windowClass: 'modal fade peerpopup  in',
                                    resolve: {
                                        params: function() {
                                          return {param: $scope.peerId, idx:$scope.idx};
                                        }
                                     }
                                });
                              };

                    }
                });


angular.module('peerConfig').controller('ModalInstanceCtrl', ['$scope', '$uibModalInstance', 'modalFactory', 'apiService', 'params', 'sharedProperties', function($scope, $uibModalInstance, modalFactory, apiService, params, sharedProperties) {

	
	if(params){
		$scope.peerId = params.param;
		$scope.idx = params.idx;
		apiService
	    	.getPeer($scope.peerId)
	    	.then(function(data){
	    		$scope.editPeer = data.data.response_body;
	    	});
	}
	
	console.log(sharedProperties.getProperty());
	//peers = sharedProperties.getProperty();
	//sharedProperties.getProperty().splice(1,1);
	$scope.confirmDelete = function (peerId) {
		apiService
 	      .deletePeer(peerId)
 	      .then(
 	    		function(response){
 	    			console.log("Delete Index : "+$scope.idx)
 	    			sharedProperties.getProperty().splice($scope.idx, 1);
 	    			$uibModalInstance.close('close');
 	    		},
 	    		function(error){
 	    			
 	    		});
	}
	
	apiService
     .getAllUsers()
     .then(
   		function(response){
   			$scope.userDetails = response.data.response_body;
   		},
   		function(error){
   			
   		});
	
	  
	  $scope.edPeer = function (peerId) {
		  
		  var peerDetails = {};
		  if(peerId){
			  peerDetails = {"request_body": 
		  		{
				"peerId": peerId,
			    "apiUrl":$scope.editPeer.apiUrl,
				"contact1UserId":$scope.editPeer.contact1UserId,
				"contact2UserId":$scope.editPeer.contact2UserId,
				"description":$scope.editPeer.description,
				"name":$scope.editPeer.name,
				"subjectName":$scope.editPeer.subjectName,
				"webUrl":$scope.editPeer.webUrl
			  }
		  };
				  } else {
		 peerDetails = {"request_body": 
		  		{
			    "apiUrl":$scope.editPeer.apiUrl,
				"contact1UserId":$scope.editPeer.contact1UserId,
				"contact2UserId":$scope.editPeer.contact2UserId,
				"description":$scope.editPeer.description,
				"name":$scope.editPeer.name,
				"subjectName":$scope.editPeer.subjectName,
				"webUrl":$scope.editPeer.webUrl
			  }
		  };
	  }
		  apiService
  	      .editPeer(peerId, peerDetails)
  	      .then(
  	    		function(response){
  	    			if (!peerId){
  	    				console.log(response.data.response_body);
  	    				sharedProperties.getProperty().splice(0, 0, response.data.response_body);
  	    				$scope.peerId = response.data.response_body.peerId;
  	    			} else {
  	    				sharedProperties.getProperty()[$scope.idx] = peerDetails.request_body;
  	    			}
  	    			apiService
              	    	.getModelTypes()
              	    	.then(function(data){
              	    		modalFactory.open('lg', 'addEditSubscriptionModal.html', {peerId: $scope.peerId});
              	    		$uibModalInstance.close('close');
                  	    }, 
                  	    function(error){
                  	    	// handle error
                  	    });
  	    		}, 
  	    		function(error){
  	    			// handle error 
  	    	})
		  };

	  $scope.cancel = function() {
	    $uibModalInstance.dismiss('cancel');
	  };
	}]);

	angular.module('peerConfig').controller('ModalResultInstanceCtrl', ['$scope', '$uibModalInstance', '$uibModal', 'params', 'modalFactory', 'apiService', function($scope, $uibModalInstance, $uibModal, params, modalFactory, apiService) {

	  console.log("Inside Result Controller" + params.peerId);
	  
	  apiService
  	  .getModelTypes()
  	  .then(function(data){
  		console.log(params.peerId);
  		$scope.categoryList = data.data.response_body;
  		$scope.peerCategory = params.peerId;
	    }, 
	    function(error){
	    	// handle error
	    });
	  
	  $scope.previous = function(peerId){
		  
		  console.log("Previous PeerId : " + params.peerId);
		  apiService
	    	.getPeer(peerId)
	    	.then(function(data){
	    		console.log(data.data.response_body);
	    		$uibModalInstance.close();
	    		var modalInstance = $uibModal.open({
                    templateUrl: 'addEditPeerModal.html',
                    controller: 'ModalInstanceCtrl',
                    windowClass: 'modal fade peerpopup  in',
                    resolve: {
                        params: function() {
                          console.log("Returning Previous PeerId : "+ peerId)
                        	return {param: peerId};
                        }
                     }
                });
  	    		
	    	},
	    	function(error){}
	    	);
	  };


	  $scope.ok = function() {
	    $uibModalInstance.close();
	  };

	  $scope.cancel = function() {
	    $uibModalInstance.dismiss('cancel');
	  };
	}]);