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

angular.module('modelDetails').

	component('modelDetails',{

		// template:"<div class=''>{{ content }}</div>",
		// template:"<button ng-click='authenticate(google)'>Sign in with
		// Google</button>",
		templateUrl:'./app/model-details/model-details.template.html',
		controller:function($scope,$location){

 		console.log("modelDetails")
		var session=sessionStorage.getItem("SessionName")
		if(session){
			console.log(session);
		}
		console.log($location.path().split('/')[2]);
    $('#input-2').rating({
        step: 1,
        size:'xxs',
        starCaptionClasses: {1: 'text-danger', 2: 'text-warning', 3: 'text-info', 4: 'text-primary', 5: 'text-success'}
    });

		 

    
	for(var index in $scope.mlsolutions){
		 if ($scope.mlsolutions[index].id ==$location.path().split('/')[2]){

			 console.log("inside");
			 $scope.solution=$scope.mlsolutions[index]
			 break;
		 }
	}


/*
 * $scope.solution={ solution_id:1003, solution_name: "Prediction-supervised KN
 * neighborh ML solution", marketplace_sol_id:"1",
 * create_date_time:"05/20/2017", modified_date_time:"05/20/2017",
 * owner_id:"2001", sol_state:"public", publish_date:"05/20/2017",
 * DESCRIPTION:"The new Machine learning algorithm for Prediction",
 * TOSCA_REFERENCE:"",
 * DOCKER_REFERENCE:"https://hub.docker.com/r/krishna41/nginix/",
 * ML_REFERENCE:"", input_text:"", ouput_text:"", author: "Christine Wright",
 * date: "05/20/2017", isNew: "New", comments: "17", views: "176",
 * avg_rating:5.5, downloads: "8", revision_id:10, UserDefVersion:"1.1",
 * ver_create_date_time:"05/20/2017", sol_ver_status:"", rev_owner_id:10,
 * dnld_date_time:"05/20/2017", user_id:1, status:"success" }
 */
 		$scope.authenticate = function(provider) {
 	  console.log(provider);
    };
    $scope.goto=function(page){

      $scope.currentNavItem = page;
    };


		}

	});
