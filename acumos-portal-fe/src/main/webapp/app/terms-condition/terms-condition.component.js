'use strict';

angular
        .module('termsCondition', ['ui.bootstrap'])
        
        .component(
                'termsCondition',
                {
                    templateUrl : '/app/terms-condition/terms-condition.template.html',
                    controller : function($scope, $uibModal, sharedProperties, apiService) {
                        
                        apiService
                                    .getTermsCondition()
                                    .then(
                                            function(response){
                                                $scope.termsCondition = response.data.description;
                                                console.log(response);
                                            },
                                            function(error){
                                            	console.log(error);
                                            }
                                    );
                        
                    }
                });
