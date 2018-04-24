/*
===============LICENSE_START=======================================================
Acumos
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