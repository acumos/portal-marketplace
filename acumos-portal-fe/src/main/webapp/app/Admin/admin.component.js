'use strict';

angular.module('admin')
	.component('admin',{
		templateUrl:'./app/Admin/admin.template.html',
		controller:function($scope, apiService, fileUploadService, $mdDialog, $http, $timeout, $location, $anchorScroll,  $uibModal, $rootScope, $state){
			componentHandler.upgradeAllRegistered();
			$scope.menuName = 'Monitoring';    $scope.allSelected = true;
			$scope.userDetail = JSON.parse(localStorage
					.getItem("userDetail"));
			var userName = $scope.userDetail[0],userId = $scope.userDetail[1];
		
			$scope.checkAdmin = function(){
				if(localStorage.getItem("userRole") == 'Admin' || localStorage.getItem("userRole") == 'admin'){
					
				}else{
					$state.go('404Error');
				}
			}
			$scope.checkAdmin();
			
			//API for get Roles
			function getRole(){
				apiService
				.getRoleCount()     //.getAllRole() //.getRoleCount()
				.then(
						function(response) {
							$scope.roles = response.data.response_body;
							$scope.rolesLength = $scope.roles.length;
						},
						function(error) {console.log(error);});
			}
			getRole();
			//API for user count
			function userDetailsFetch(){
			apiService
			.getAllUserCount()
			.then(
					function(response) {
						$scope.user = response.data.response_body.length;
						$scope.userDetails = response.data.response_body;
						$scope.alluserDetails = response.data.response_body;
						detailsUser = $scope.userDetails;
					},
					function(error) {console.log(error);});
			}
			userDetailsFetch();
			//API for Peer count
			var obj = {"fieldToDirectionMap": {},"page": 0,"size": 0};
			apiService
			.getPeers(obj)
			.then(
					function(response) {console.log(response.data.response_body.content);
						$scope.peer = response.data.response_body.content;
					},
					function(error) {console.log(error);});
			//Close popup
			$scope.closePoup = function(){
          	  $mdDialog.hide();
          	 $scope.value = undefined;$scope.roleValue='';
          	 $scope.signupForm.$setPristine();
             $scope.signupForm.$setUntouched();
             $scope.signupForm.$rollbackViewValue();
             angular.element('#emailValue').val('');
             $scope.roleName = '';perValue();
             permissionList = [];$scope.perList = '';
             fetchPeer();fetchCat();$scope.hidePeer = false;$scope.data='';
            }
			//Open popup 
            $scope.showPopup = function(ev){
          	  $mdDialog.show({
          		  contentElement: '#myDialog',
          		  parent: angular.element(document.body),
          		  targetEvent: ev,
          		  clickOutsideToClose: true
          	  });
            }
            $scope.addNewRole = function(ev){
        	  $mdDialog.show({
        		  contentElement: '#addRole',
        		  parent: angular.element(document.body),
        		  targetEvent: ev,
        		  clickOutsideToClose: true
        	  });
          }
          //Open popup Add Peer
            $scope.showPopupPeer = function(ev){
        	  $mdDialog.show({
        		  contentElement: '#addPeer',
        		  parent: angular.element(document.body),
        		  targetEvent: ev,
        		  clickOutsideToClose: true
        	  });
          }
            
          //Open popup Delete confirmation
            $scope.showDeletePopup = function(ev, peerId){
            	$scope.peerId = peerId;
        	  $mdDialog.show({
        		  contentElement: '#deleteConfirmPopup',
        		  parent: angular.element(document.body),
        		  targetEvent: ev,
        		  clickOutsideToClose: true
        	  });
        	  
          }
              	$scope.ok = function () {
            	  $mdDialog.hide($scope.confirmDelete($scope.peerId));
            	};
            
            //User sign up by admin
            $scope.value = [];
            $scope.submitFucn = function(){
            	if($scope.signupForm.$invalid || !$scope.roleValue){return;}
            	//var url = 'api/roles/add/' + $scope.roleValue;
            	var user = $scope.value;
            	var obj = {
            			  "request_body": {
            				    "emailId": user.email,
            				    "firstName": user.fname,
            				    "lastName": user.lname,
            				    "loginName": user.username,
            				    "password": user.password,
            				    "username": user.username,
            				    "userNewRoleList":$scope.roleValue
            				  }
            				};
            	debugger;
            	    apiService.addUser( obj/*, $scope.roleValue*/ )
            	    .then(function(response) {
            	    	if(response.data.error_code == 500){
            	    		alert("User already exist");
            	    		return;
            	    	}
            	    	userDetailsFetch();
            	    	getRole();
            	    	$scope.value = null;$scope.roleValue = '';
                    	$scope.signupForm.fname.$touched = false;$scope.signupForm.lname.$touched = false;$scope.signupForm.uname.$touched = false;
                    	$scope.signupForm.email.$touched = false;$scope.signupForm.pwd.$touched = false;$scope.signupForm.cpwd.$touched = false;
                    	$scope.signupForm.pwd.$dirty = false;
            	    	$scope.closePoup();
            	    	$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                        $anchorScroll(); 
                        $scope.msg = "User Created successfully."; 
                        $scope.icon = 'report_problem';
                        $scope.styleclass = 'c-success';
                        $scope.showAlertMessage = true;
                        $timeout(function() {
                        	$scope.showAlertMessage = false;
                        }, 5000);
            	            // success
            	    }, 
            	    function(response) { console.log('Error :' +response);// optional
            	            // failed
            	    });
            };
            
            //Dynamic form implementation
            apiService
			.getSiteConfig("site_config")
			.then(
					function(response) {
						$scope.siteConfig = angular.fromJson(response.data.response_body.configValue);
						
					},
					function(error) {console.log(error);
			});
            
            /*$scope.entity = {
            	      name : "Course", 
            	      fields :
            	        [
            	          {type: "text", name: "firstname", label: "Name" , required: true, data:""},
            	          {type: "radio", name: "color_id", label: "Colors" , options:[{id: 1, name: "orange"},{id: 2, name: "pink"},{id: 3, name: "gray"},{id: 4, name: "cyan"}], required: true, data:""},
            	          {type: "email", name: "emailUser", label: "Email" , required: true, data:""},
            	          {type: "text", name: "city", label: "City" , required: true, data:""},
            	          {type: "password", name: "pass", label: "Password" , min: 6, max:20, required: true, data:""},
            	          {type: "select", name: "teacher_id", label: "Teacher" , options:[{name: "Mark"},{name: "Claire"},{name: "Daniel"},{name: "Gary"}], required: true, data:""},
            	          {type: "checkbox", name: "car_id", label: "Cars" , options:[{id: 1, name: "bmw"},{id: 2, name: "audi"},{id: 3, name: "porche"},{id: 4, name: "jaguar"}], required: true, data:""}
            	        ]
            	      };*/

            	      $scope.submitForm = function(){  
						angular
                          .forEach(
                                  $scope.siteConfig.fields,
                                  function( value, key) {
                                  	if($scope.siteConfig.fields[key].type == 'file'){
                                  		var fileObject  = {
                                  			   'lastModified'     : $scope.siteConfig.fields[key].data.lastModified,
                                  			   'lastModifiedDate' : $scope.siteConfig.fields[key].data.lastModifiedDate,
                                  			   'name'             : $scope.siteConfig.fields[key].data.name,
                                  			   'size'             : $scope.siteConfig.fields[key].data.size,
                                  			   'type'             : $scope.siteConfig.fields[key].data.type
                                  			}; 
                                  		$scope.siteConfig.fields[key].data = fileObject;
                                  	}
                                     /* fileUploadService.uploadFileToUrl(
                                      		$rootScope.headerImage, uploadUrl).then(
          											function(response) {
          												console.log(response);
          												
          											},
          											function() {
          												//$scope.serverResponse = 'An error has occurred';
          											});*/
                                      
                                  });
                         var data = angular.copy($scope.siteConfig);
                          var strSiteConfig = JSON.stringify(data);
                          var convertedString = strSiteConfig.replace(/"/g, '\"');
						  var file = $scope.userImage;
                          var uploadUrl = "api/users/updateUserImage/"
								+ userId;
                          var reqObj = {
                                  "request_body": {
                                      "configKey": "site_config",
                                      "configValue":convertedString,
                                      "userId": userId
                                    }
                                    };
                          apiService
                            .updateSiteConfig("site_config", reqObj)
                            .then(
                                    function(response) {
                                        //$scope.siteConfig = angular.fromJson(response.data.response_body.configValue);
                                        angular
                                        .forEach(
                                                $scope.siteConfig.fields,
                                                function( value, key) {
                                                   if($scope.siteConfig.fields[key].label == 'siteInstanceName'){
                                                        $rootScope.siteInstanceName = $scope.siteConfig.fields[key].data;
                                                    }if($scope.siteConfig.fields[key].name == 'headerLogo'){
                                                        $rootScope.headerImage = $scope.siteConfig.fields[key].data;
                                                    }if($scope.siteConfig.fields[key].name == 'footerLogo'){
                                                        $rootScope.footerImage = $scope.siteConfig.fields[key].data;
													} if($scope.siteConfig.fields[key].label == 'EnableOnboarding'){
					                                    if($scope.siteConfig.fields[key].data.name == 'Enabled'){
					                                    	$rootScope.enableOnBoarding = true;
					                                    } else {
					                                    	$rootScope.enableOnBoarding = false;
					                                    }
					                                }
                                                    
													/*fileUploadService.uploadFileToUrl(
                                                    		$rootScope.headerImage, uploadUrl).then(
                        											function(response) {
                        												console.log(response);
                        												
                        											},
                        											function() {
                        												//$scope.serverResponse = 'An error has occurred';
                        											});*/
                                                });
                                        alert("Updated successfully.");
                                    },
                                    function(error) {console.log(error);
                            });
                        //$log.debug($scope.entity);
                      }
            	      $scope.peerChange = function(peer){
            	    	  $scope.editPeer = peer;
            	      }
            	      //Peer Popup
            	      function fetchPeer(){
            	    	  var dataObj = {"fieldToDirectionMap": {},"page": 0,"size": 0};
                          apiService
                              .getPeers(dataObj)
                              .then(
                                      function(response){
                                    	  $scope.peerList = response.data.response_body.content;
                                      },
                                      function(error){
                                    	  console.log('Error :' +error);
                                      }
                              );
            	      }    
            	      fetchPeer();
                      //Category Popup
            	      function fetchCat(){
                          apiService
                          .getModelTypes()
                          .then(
                                  function(response) {
                                      $scope.category = response.data.response_body;
                                  },
                                  function(error) {console.log('Error :' +error);});
                      }
                      fetchCat();
                      //Category Json
                      var arr = [];
                      $scope.categorySelect = function(cat){
                    	  var checkDup = false,dupKey=0;$scope.string='';
                          angular.forEach(arr, function(value, key) {
                              if(cat.typeCode == value.typeCode){
                                  checkDup = true;
                                  dupKey = key;
                              }
                            });
                          if(checkDup){arr.splice(dupKey, 1);}else arr.push({typeName : cat.typeName,typeCode : cat.typeCode});
                          console.log(arr);
                          angular.forEach(arr, function(value, key) {
                              if(key == 0){$scope.string = $scope.string + '"{'}
                              $scope.string = $scope.string + '\\"' + value.typeCode + '\\":\\"' + value.typeName + '\\",';
                        });
                          $scope.queryParam = $scope.string.slice(0, -1);
                          $scope.queryParam= $scope.queryParam + '}"';
                          console.log($scope.queryParam);
                      }
                      //Add peer
                      $scope.addPeer = function(){console.log($scope.editPeer);
                    	  var peerDetails = {
				                    			  "request_body": {
				
				                    				    "peerId": $scope.editPeer.peerId,
				                    					"maxArtifactSize": 0,
				                    				    "refreshInterval": 0,
				                    				    "selector": $scope.queryParam/*"{\"CL\":\"Classification\",\"DT\":\"Data Transform\"}"*/
				                    				  }
                    	  
                    						};
                    	  
                    	apiService.addPeer(peerDetails)
                  	    .then(
                  	    		function(response){
                  	    			$scope.category;fetchCat();
                  	    			$scope.data = '';$scope.hidePeer = false;$scope.queryParam='';
                        	    	$scope.closePoup();
                        	    	$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                    $anchorScroll(); 
                                    $scope.msg = "Peer Created successfully."; 
                                    $scope.icon = 'report_problem';
                                    $scope.styleclass = 'c-success';
                                    $scope.showAlertMessage = true;
                                    $timeout(function() {
                                    	$scope.showAlertMessage = false;
                                    }, 5000);
                        	            // success
                        	    }, 
                  	    		function(error){
                  	    			// handle error 
                  	    	})
                      }
            	    //Delete Peer
                      /*$scope.deletePeer = function(peerId, idx) {
                    	  idx= '';
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
                        };*/
                      $scope.confirmDelete = function (peerId) {
                    	                		                   		
                  		apiService
                   	      .deletePeer(peerId)
                   	      .then(
                   	    		function(response){
                   	    			fetchPeer();
                        	    	//$scope.closePoup();
                        	    	$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                    $anchorScroll(); 
                                    $scope.msg = "Peer Deleted successfully."; 
                                    $scope.icon = 'report_problem';
                                    $scope.styleclass = 'c-success';
                                    $scope.showAlertMessage = true;
                                    $timeout(function() {
                                    	$scope.showAlertMessage = false;
                                    }, 5000);
                                    
                        	            // success
                        	    },
                   	    		function(error){console.log('Error :' +error);});
                    }
                    //Permission list
                      function perValue(){
                          $scope.permissionList = [{permission:'Design Studio',value:'DS'},{permission:'Market Place',value:'MP'},{permission:'On Boarding',value:'OB'}]
                      }
                      perValue();


                    //PErmission checkbox
                                          var permissionList = [];
                                          $scope.permissionValue = function(val){
                                        	  var checkDup = false,dupKey=0;$scope.perString='';$scope.perList='';
                                        	  angular.forEach(permissionList, function(value, key) {
                                        		  if(val.value == value.value){
                                        			  checkDup = true;
                                        			  dupKey = key;
                                        			  
                                            	  }
                                        		});
                                        	  if(checkDup){permissionList.splice(dupKey, 1);}else permissionList.push({value : val.value});
                                        	  angular.forEach(permissionList, function(value, key) {
                                        		  $scope.perString = $scope.perString + '"' + value.value + '",' ;
                                        	});
                                        	  $scope.perList = $scope.perString.slice(0, -1);
                                          }
                                        //Add Role
                                          $scope.addRole = function(){
                                        	  var roleDetails = {
                    					                    		  "request_body": {
                    					                    			    "name": $scope.roleName,
                    					                    			    "permissionList": [
                    					                    			            $scope.perList
                    					                    			    ]
                    					                    			  }
                                        			}
                                        	  apiService
                                  				.urlCreateRole(roleDetails)
                                  					.then(
                    	              					function(response) {
                    	              						getRole();
                    	              						permissionList = [];
                    	              						$scope.permissionList;
                                                            perValue();
                    	                   	    			$scope.closePoup();
                    	                   	    			$scope.roleName='';
                    	                        	    	$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                    	                                    $anchorScroll(); 
                    	                                    $scope.msg = "Role created successfully."; 
                    	                                    $scope.icon = 'report_problem';
                    	                                    $scope.styleclass = 'c-success';
                    	                                    $scope.showAlertMessage = true;
                    	                                    $timeout(function() {
                    	                                    	$scope.showAlertMessage = false;
                    	                                    }, 5000);
                    	                                },
                    	              					function(error) {console.log(error);
                    	              			});
                                          }
                                          //Change role by admin
                                          $scope.roleArr = [];var roleMap = [];var roleFin = [];
                                          $scope.roleCheckbox = function(selectBox,obj){

                                        	 if(selectBox == true){
                                        		 $scope.roleArr.push(obj.userId);
                                        	 }else if(selectBox == false){
                                        		 $scope.roleArr = jQuery.grep($scope.roleArr, function(value) {
                                        		   return value != obj.userId;
                                        		 });
                                        	 }
                                        	 console.log($scope.roleArr);
                                          }
                                          /*$scope.changeRoleSelectBox = function(roleId,obj){
                                        	  var dupInMap = false,keyVal = null;
                                        	  angular.forEach(roleMap, function(value, key) {
                                                  if(value.userId == obj.userId){
                                                	  keyVal= key;dupInMap = true;
                                                  }
                                                });
                                        	  if(dupInMap){roleMap[keyVal].updatedRoleId = roleId;}
                                        	  else if(!dupInMap){
                                        		  roleMap.push({'userId':obj.userId,"updatedRoleId":roleId,"roleId":obj.roleId,"username":obj.username})
                                        	  }
                                        	//console.log(roleMap);
                                          }*/
                                          $scope.addRoleFuncConfirm = function(ev){
                                        	  if($scope.functionCall == 'updateValue'){
                                        		  if(!$scope.roleIdSelected){
                                            		  $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
    	        	                                    $anchorScroll(); 
    	        	                                    $scope.msg = "Please select role."; 
    	        	                                    $scope.icon = 'report_problem';
    	        	                                    $scope.styleclass = 'c-success';
    	        	                                    $scope.showAlertMessage = true;
    	        	                                    $timeout(function() {
    	        	                                    	$scope.showAlertMessage = false;
    	        	                                    }, 5000);
    	        	                                    return;                                        	  
                                            	  }
                                        		  $scope.confirmMsg = "Do you want to update user's Role ?";
                                        		  $scope.warningMsg = "Change Role";
                                        	  }else if($scope.functionCall == 'deleteValue'){
                                        		  $scope.confirmMsg = "Do you want to delete users ?";
                                        		  $scope.warningMsg = "Delete Confirmation";
                                        	  }
                                        	  $mdDialog.show({
                                          		  contentElement: '#confirmPopup',
                                          		  parent: angular.element(document.body),
                                          		  targetEvent: ev,
                                          		  clickOutsideToClose: true
                                          	  });
                                          }
                                          $scope.addRoleFunc = function(val){
	                                          if(val == 'update'){
	                                        	  var json = [];
	                                        	  /*angular.forEach($scope.roleArr, function(value, key) {
	                                        		  json.push({                                          		    
	                                          		    "roleId": '',
	                                          		    "updatedRoleId": $scope.roleIdSelected,
	                                          		    "userId": value
	                                          		   });
	                                        	  });*/
	                                        	  json = {
	                                        			  	"userNewRoleList": $scope.roleIdSelected,
	                                        			  	"userIdList": $scope.roleArr
	                                        	  		}
	                                        	 
	                                        	  json = {
	                                        		  "request_body": json};
	                                        	  console.log(angular.toJson(json));
	                                        	 apiService
	                                  			.updateUserRoleNew(json)
	                                  			.then(function(response) {
	                                  				userDetailsFetch();
	                                  				getRole();
	                                  				$scope.roleIdSelected = [];
	                                  				$scope.roleArr = [];
	                                  				$scope.closePoup();
	                                  				$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
	        	                                    $anchorScroll(); 
	        	                                    $scope.msg = "Role Updated successfully."; 
	        	                                    $scope.icon = 'report_problem';
	        	                                    $scope.styleclass = 'c-success';
	        	                                    $scope.showAlertMessage = true;
	        	                                    $timeout(function() {
	        	                                    	$scope.showAlertMessage = false;
	        	                                    }, 5000);
	                                  			}, 
	                                      	    function(response) {console.log('Error :' +response);// optional
	                                      	            // failed
	                                      	    });
	                                         }
	                                          else if(val == 'delete'){ $scope.deleteUser();}
	                                      }
                                          //Delete user 
                                          $scope.deleteUser = function(){
                                        	  var obj = {
                                        			  "request_body": {
                                        				    "userIdList": $scope.roleArr
                                        				  }
                                        				}
                                        	  console.log(angular.toJson(obj));
                                        	  apiService.deleteUser(obj)
                                      	    .then(function(response) {
                                      	    	userDetailsFetch();
                                      	    	$scope.roleArr = [];
                                      	    	$scope.closePoup();
                                  				$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
        	                                    $anchorScroll(); 
        	                                    $scope.msg = "User Deleted successfully."; 
        	                                    $scope.icon = 'report_problem';
        	                                    $scope.styleclass = 'c-success';
        	                                    $scope.showAlertMessage = true;
        	                                    $timeout(function() {
        	                                    	$scope.showAlertMessage = false;
        	                                    }, 5000);
                                      	    }, 
                                      	    function(response) { console.log('Error :' +response);// optional
                                      	            // failed
                                      	    });
                                          };
                                          //Filter on bases on role
                                          var detailsUser = '';
                                          
                                          $scope.userFilter = function(role){
                                        	  debugger;
                                        	  if(role == 'all'){ $scope.userDetails = $scope.alluserDetails; return;}
                                        	  var temp=[];
                                          
	                                          angular
	                                          .forEach(
	                                        		  detailsUser,
	                                                  function( value, key) {
	                                        			  var roleList = value.userAssignedRolesList;
	                                        			  if(roleList.length){
	                                        				  angular.forEach(
	                                                    		  roleList,
	                                                              function( roleAssigned, key) {
	                                                                  if(roleAssigned.roleId == role.roleId){
	                                                                	  temp.push(value) ;
	                                                                  }
	                                                    		  })
	                                        			  }
	                                                  });
	                                          $scope.userDetails = temp;
                                          }
                                          //Add Class
                                          $scope.selected = 0;
                                          $scope.select= function(index) {
                                             $scope.selected = index;  
                                          };
                                          
                                          $scope.$watch( 'filtered', function ( val ) {
                                        	 if(val && val.length == 0 && ($scope.searchUserDetails || $scope.searchPeerDetails) && ( $scope.searchUserDetails.firstName || $scope.searchPeerDetails.name )){
                                         		 	$scope.hideLabel = true;
                                         	 }else {
                                         		 	$scope.hideLabel = false;
                                         	 }
                                           });
            	    
		}
}).directive(
		'uploadImageModel',
		function($parse) {
			return {
				restrict : 'A', // the directive can be used as an
								// attribute only

				/*
				 * link is a function that defines functionality of
				 * directive scope: scope associated with the element
				 * element: element on which this directive used attrs:
				 * key value pair of element attributes
				 */
				link : function(scope, element, attrs) {
					var model = $parse(attrs.uploadImageModel), modelSetter = model.assign; // define
																							// a
																							// setter
																							// for
																							// demoFileModel

					// Bind change event on the element
					element.bind('change', function() {
						// Call apply on scope, it checks for value
						// changes and reflect them on UI
						scope.$apply(function() {
							// set the model value
							modelSetter(scope, element[0].files[0]);
						});
					});
				}
			}
		});
