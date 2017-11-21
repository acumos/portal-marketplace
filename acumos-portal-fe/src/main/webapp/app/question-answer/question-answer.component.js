'use strict';

angular
        .module('qanda', ['ui.bootstrap'])
        .component(
                'qanda',
                {
                	templateUrl : '/app/question-answer/question-answer.template.html',
                    controller : function($scope, $sce, apiService) {
                    	apiService.getQandAUrl().then( function(response){
                			if(sessionStorage.getItem("auth_token")!='')
                				$scope.qandAUrl = $sce.trustAsResourceUrl(response.data.response_body + "/signin?provider=acumos&access_token=" + sessionStorage.getItem("auth_token"));
                		});
                    }
                });