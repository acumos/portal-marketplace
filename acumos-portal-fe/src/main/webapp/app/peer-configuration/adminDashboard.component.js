'use strict';

angular
        .module('adminConfig', ['ui.bootstrap'])
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
			  })
			  .service('sharedProperties', function () {
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
                'adminConfig',
                {
                    templateUrl : '/app/peer-configuration/admin-dashboard.template.html',
                    controller : function($scope, $uibModal, sharedProperties, apiService) {
                        
                        var dataObj = {
                                "fieldToDirectionMap": {},
                                "page": 0,
                                "size": 5
                        };
                        apiService
                                    .getPeers(dataObj)
                                    .then(
                                            function(response){
                                                $scope.peers = response.data.response_body.content;
                                                sharedProperties.setProperty($scope.peers);
                                            },
                                            function(error){
                                            	console.log(error);
                                            }
                                    );
                        
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
                    }
                });


angular.module('adminConfig').controller('ModalInstanceCtrl', ['$scope', '$uibModalInstance', 'apiService', 'sharedProperties', function($scope, $uibModalInstance, apiService, sharedProperties) {
	
	
	  $scope.edPeer = function (peerId) {
	
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
	  
		  apiService
  	      .editPeer(peerId, peerDetails)
  	      .then(
  	    		function(response){
    				console.log(response.data.response_body);
    				sharedProperties.getProperty().splice(0, 0, response.data.response_body);
    				$scope.peerId = response.data.response_body.peerId;
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
