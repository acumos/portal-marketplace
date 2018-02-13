'use strict';

angular.module('admin')
	.component('admin',{
		templateUrl:'./app/Admin/admin.template.html',
		controller:function($scope, apiService, fileUploadService, $mdDialog, $http, $timeout, $location, $anchorScroll,  $uibModal, $rootScope, $state){
			componentHandler.upgradeAllRegistered();
			//Sorting
				$scope.orderByField = 'username';$scope.reverseSort = false;
				$scope.orderByFieldFed = 'name'; $scope.reverseSortFederation = false;
			//Bulk Action
			$scope.bulkAction = [{'name':'Active User','value':'active'},{'name':'Inactive User','value':'inactive'},{'name':'Delete','value':'delete'}]
			//Frequency of update
			$scope.frequency = 
				[{
					'name': 'Hourly',
					'value': '1'
				}, {
					'name': 'Daily',
					'value': '24'
				}, {
					'name': 'Monthly',
					'value': '720'
				}, {
					'name': 'Update on demand',
					'value': '0'
				}]
			
			//Hard coded (delete it)
			/*$scope.subscription = [{
			       "requestId": "REQID 12345680",
			       "requestedDetails": "Requested Details",
			       "requestType": "Model Download",
			       "sender": "Techm10",
			       "date": 1518252201559,
			       "action": null,
			       "status": 'pending'
			     }]*/
			$scope.activeRequest = 5;
			//Hard coded (delete it)
			$scope.freqOfUpdate = 'hr';
			$scope.menuName = 'Monitoring';    $scope.allSelected = true;
			$scope.userDetail = JSON.parse(localStorage
					.getItem("userDetail"));
			if($scope.userDetail != undefined){
				var userName = $scope.userDetail[0],userId = $scope.userDetail[1];
			}
		
			$scope.checkAdmin = function(){
				if(localStorage.getItem("userRole") == 'Admin' || localStorage.getItem("userRole") == 'admin'){
					
				}else{
					$state.go('404Error');
				}
			}
			$scope.checkAdmin();
			
			$scope.showDocUrl = false;
			  apiService.getDashboardUrl().then( function(response){
					$scope.dashboardUrl = response.data.response_body;
				});
			//API for get Roles
			function getRole(){
				apiService
				.getRoleCount()     //.getAllRole() //.getRoleCount()
				.then(
						function(response) {
							$scope.roles = response.data.response_body;
							$scope.rolesLength = $scope.roles.length;
						},
						function(error) {});
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
						$scope.alluserDetails = ($scope.alluserDetails).filter(function(userObj) {
							  return userObj.active == "true";
						});
						detailsUser = $scope.userDetails;
					},
					function(error) {console.log(error);});
			}
			userDetailsFetch();
			//API for Peer count
			getAllPeer();
			function getAllPeer(){
			var obj = {"fieldToDirectionMap": {},"page": 0,"size": 0};
			apiService
			.getPeers(obj)
			.then(
					function(response) {
						$scope.activeCount = 0;
						$scope.peer = response.data.response_body.content;
						angular.forEach($scope.peer, function(value, key) {
                            if(value.self == true){
                            	$scope.activeCount = $scope.activeCount+1;
                            }
                          });
						$scope.countSubscriptions();
					},
					function(error) {console.log(error);});
			}
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
        	  $scope.peerForm.$setUntouched();
              $scope.peerForm.$setPristine();
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
                        $scope.icon = '';
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
                          		/*var fileObject  = {
                          			   'lastModified'     : $scope.siteConfig.fields[key].data.lastModified,
                          			   'lastModifiedDate' : $scope.siteConfig.fields[key].data.lastModifiedDate,
                          			   'name'             : $scope.siteConfig.fields[key].data.name,
                          			   'size'             : $scope.siteConfig.fields[key].data.size,
                          			   'type'             : $scope.siteConfig.fields[key].data.type
                          			};*/ 
                        	 
                          		 
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
                                            }if($scope.siteConfig.fields[key].label == 'Headerlogo'){
                                                $rootScope.headerImage = $scope.siteConfig.fields[key].data.base64;
                                            }if($scope.siteConfig.fields[key].label == 'coBrandingLogo'){
                                                $rootScope.coBrandingImage = $scope.siteConfig.fields[key].data.base64;
                                            }if($scope.siteConfig.fields[key].label == 'Footerlogo'){   
                                                $rootScope.footerImage = $scope.siteConfig.fields[key].data.base64;
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
                    //Category Popup
            	      function fetchToolKitType(){
                          apiService
                          .getToolkitTypes()
                          .then(
                                  function(response) {console.log(response.data.response_body);
                                      $scope.toolKitType = response.data.response_body;
                                  },
                                  function(error) {console.log('Error :' +error);});
                      }
                      fetchToolKitType();
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
                          angular.forEach(arr, function(value, key) {
                              if(key == 0){$scope.string = $scope.string + '"{'}
                              $scope.string = $scope.string + '\\"' + value.typeCode + '\\":\\"' + value.typeName + '\\",';
                        });
                          $scope.queryParam = $scope.string.slice(0, -1);
                          $scope.queryParam= $scope.queryParam + '}"';
                      }
                      //Add peer
                      $scope.addEditPeer = '';
                      $scope.addPeer = function(){
                    	  if($scope.itsEdit == true){$scope.updatePeer('detail');return}
                    	 var peerDetails = {"request_body": {				
				                    				  	"apiUrl": $scope.apiUrlPop,
				                    				    "contact1": $scope.emailIdPop,
				                    				    "description": $scope.descriptionPop,
				                    				    "name": $scope.peerNamePop,
				                    				    "subjectName": $scope.subNamePop,
				                    				    "webUrl": $scope.apiUrlPop,
				                    				    "validationStatusCode": "PS",
				                    				    "statusCode": "AC"
				                    				    //"selector": $scope.queryParam/*"{\"CL\":\"Classification\",\"DT\":\"Data Transform\"}"*/
				                    		}};
                    	apiService.insertPeers(peerDetails).then(
                  	    		function(response){
                  	    			if(response.data.error_code == 400){

                      	    			getAllPeer();
                      	    			$scope.category;fetchCat();
                      	    			$scope.data = '';$scope.hidePeer = false;$scope.queryParam='';
                            	    	$scope.closePoup();
                            	    	$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                        $anchorScroll(); 
                                        $scope.msg = "Peer Already Exists."; 
                                        $scope.icon = 'report_problem';
                                        $scope.styleclass = 'c-error';
                                        $scope.showAlertMessage = true;
                                        $timeout(function() {
                                        	$scope.showAlertMessage = false;
                                        }, 5000);
                            	            // success
                  	    			}else{
                  	    				getAllPeer();
                      	    			$scope.category;fetchCat();
                      	    			$scope.data = '';$scope.hidePeer = false;$scope.queryParam='';
                            	    	$scope.closePoup();
                            	    	$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                        $anchorScroll(); 
                                        $scope.msg = "Peer Created successfully."; 
                                        $scope.icon = '';
                                        $scope.styleclass = 'c-success';
                                        $scope.showAlertMessage = true;
                                        $timeout(function() {
                                        	$scope.showAlertMessage = false;
                                        }, 5000);
                            	            // success
                  	    			}
                  	    			
                        	    }, 
                  	    		function(error){
                  	    			// handle error 
                  	    	})
                      
                    }
                      //Edit PEER
                      $scope.itsEdit = false;
                      $scope.editPeer = function(peerDetail){
                    	  $scope.itsEdit = true;$scope.peerStatus = peerDetail.self;
                    	  $scope.editPeerID = peerDetail.peerId;
                    	  $scope.peerNamePop = peerDetail.name;$scope.subNamePop = peerDetail.subjectName;$scope.emailIdPop = peerDetail.contact1;
                    	  $scope.apiUrlPop = peerDetail.apiUrl;$scope.webUrlPop = peerDetail.apiUrl;$scope.descriptionPop = peerDetail.description;
                    	  $scope.showPopupPeer();
                      }
                      $scope.updatePeer = function(val){
                    	  if(val == 'detail'){
                    		  var peerDetails = {"request_body": {	
                    			 "self" : $scope.peerStatus,
                				"apiUrl": $scope.apiUrlPop,
              				    "contact1": $scope.emailIdPop,
              				    "description": $scope.descriptionPop,
              				    "name": $scope.peerNamePop,
              				    "subjectName": $scope.subNamePop,
              				    "webUrl": $scope.apiUrlPop,
              				    "peerId" : $scope.editPeerID,
              				    "validationStatusCode": "PS",
              				    "statusCode": "AC"
              				    //"selector": $scope.queryParam/*"{\"CL\":\"Classification\",\"DT\":\"Data Transform\"}"*/
              		}}
                    	  }else {
                    		  if(val.self == true)(val.self = false);else val.self = true;
                    		  $scope.editPeerID = val.peerId;
                    		  var peerDetails = {"request_body": {	
                    			"self" : val.self,
              				  	"apiUrl": val.apiUrl,
            				    "contact1": val.contact1,
            				    "description": val.description,
            				    "name": val.name,
            				    "subjectName": val.subjectName,
            				    "webUrl": val.webUrl,
            				    "peerId" : val.peerId,
            				    "validationStatusCode": "PS",
              				    "statusCode": "AC"
            				    //"selector": $scope.queryParam/*"{\"CL\":\"Classification\",\"DT\":\"Data Transform\"}"*/
            		}}
                  	  }
                      	  apiService.editPeer($scope.editPeerID,peerDetails).then(
                      	    		function(response){
                      	    			$scope.peer='';getAllPeer();
                      	    			//$scope.category;fetchCat();
                      	    			$scope.data = '';$scope.hidePeer = false;$scope.queryParam='';
                            	    	$scope.closePoup();
                            	    	$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                        $anchorScroll(); 
                                        $scope.msg = "Peer Updated successfully."; 
                                        $scope.icon = '';
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
                    //Chane cat and tool
                      $scope.catChange = function(val1,val2){
                    	  $scope.catValue = '';$scope.toolType = '';$scope.noData = false;
                    	  angular.forEach($scope.category, function(value, key) {
                    		  if(value.typeCode == val1)
                    			  $scope.catValue = value.typeName;
                    	  });
    					  angular.forEach($scope.toolKitType, function(value, key) {
    						  if(value.typeCode == val2)
    							  $scope.toolType  = value.typeName;
    					  });
                      }
                      //All select federation
                      $scope.allSelect = function(){
                    	  $scope.categoryValue='';$scope.toolKitTypeValue='';$scope.modelIDValue='';
                      }
                      //Serch using model id
                      $scope.modelEdit = function(){
                    	  $scope.addedToSubs = false;
                    	  var getSolutionImagesReq = {
									method : 'GET',
									url : '/site/api-manual/Solution/solutionImages/'+$scope.modelIDValue
							};
                     	 $http(getSolutionImagesReq)
								.success(
										function(data, status, headers,
												config) {
											if(data.response_body.length > 0)
												$scope.imgURLdefault = "/site/binaries/content/gallery/acumoscms/solution/" + $scope.modelIDValue + "/" + data.response_body[0];
											else
												$scope.imgURLdefault = "images/default-model.png";
										}).error(
												function(data, status, headers,
														config) {
													return "No Contents Available"
												});
                    	  apiService
                    	  .getSolutionDetail($scope.modelIDValue)
                    	  .then(
                    	  		function(response) {
                    	  			$scope.solutionDetail = response.data.response_body;
                    	  			if($scope.solutionDetail == 'null'){$scope.noData = false;}else {$scope.noData = true;}
                    	  		},
                    	  		function(error) {
                    	  			
                    	  		});
                      }
                      $scope.mdPrimaryClass=false;
                      //Subscription popup
                    //Open popup Add Peer
                      $scope.showPopupPeeR1 = function(ev,val){
                	  $scope.subscripDetails1 = false;$scope.mdPrimaryClass=false;$scope.modelIDValue='';
                	  $scope.categoryValue = '';$scope.arrDetails='';$scope.allSubs = 'false';
            		  $scope.toolKitTypeValue = '';$scope.solutionDetail = '';
                	  $scope.peerIdForSubsList = val.peerId;
                	  $scope.peerDetailList = val;
                	  var url = 'api/admin/peer/subcriptions/' +  val.peerId;
                	  $http.post(url).success(function(response){
                		  $scope.subId = '';
                		  $scope.subId = response.response_body[0].subId;
                		  
                		  $scope.arrSub = [];
                		  angular.forEach(response.response_body, function(value, key) {
                			  var catTool = value.selector;
            				  var catTool = catTool.split(",");
            				  
            				  if(catTool.length > 1){
            					  angular.forEach($scope.category, function(value, key) {
            						  var serch = value.typeCode ;
            						  var serchValue = catTool[0].search(serch);
            						  if(serchValue > 0)$scope.categoryForSubId = value;
            						});
            					  angular.forEach($scope.toolKitType, function(value, key) {
            						  var serch = value.typeCode ;
            						  var serchValue = catTool[1].search(serch);
            						  if(serchValue > 0)$scope.toolKitForSubId = value;
            						});
            				  }else {
            					  $scope.toolKitForSubId ='';$scope.categoryForSubId = '';
            					  if(catTool[0].search('modelTypeCode') > 0){
            						  angular.forEach($scope.category, function(value, key) {
                						  var serch = value.typeCode ;
                						  var serchValue = catTool[0].search(serch);
                						  if(serchValue > 0)$scope.categoryForSubId = value;
                						});
            					  }
            					  else if(catTool[0].search('toolKitTypeCode') > 0){
            						  angular.forEach($scope.toolKitType, function(value, key) {
                						  var serch = value.typeCode ;
                						  var serchValue = catTool[0].search(serch);
                						  if(serchValue > 0)$scope.toolKitForSubId = value;
                						});
            					  }
            				  }
            				  
            				  $scope.frequency;
            				  $scope.frequencySelected = [];
            				  /*angular.forEach($scope.frequency, function(value1, key1) {*/
            					  if(value.refreshInterval == 3600){
                					 $scope.frequencySelected[0] =  '1';
                				  }else if(value.refreshInterval == 86400){
                					  $scope.frequencySelected[1] = '24';
                				  }else if(value.refreshInterval == 2592000){ 
                					  $scope.frequencySelected[2] = '720';
                				  }else if(value.refreshInterval == 24){
                					  $scope.frequencySelected[3] = '0';
                				  }
        						/*});*/
            				 // $scope.frequencySelected = 24;
            				  
            				  /* convert time freq of update*/
            				  
            				  
            				  $scope.arrSub.push({
            					  "subId" : value.subId,
            					  "toolKitType" : $scope.toolKitForSubId.typeName,
            					  "modelType" : $scope.categoryForSubId.typeName,
            					  "updatedOn" : value.modified,
            					  "createdOn" : value.created,
            					  "frequencySelected" : $scope.frequencySelected
            				  })
                		  });

        				 $scope.arrDetails = $scope.arrSub;
                	  });
                  
                	  
              	  $mdDialog.show({
              		  contentElement: '#subscriptionPopUp',
              		  parent: angular.element(document.body),
              		  targetEvent: ev,
              		  clickOutsideToClose: true
              	  });}
                      // frequency change from add subscription
                      var freqChangeValue = '';
                      $scope.freqChange = function(freqOfUpdatePass){
                    	  freqChangeValue = freqOfUpdatePass * 60 * 60;
                    	  }
                      //Add to subscription
                      $scope.addedToSubs = false;
                      $scope.addToSubs = function(){
                    	  var check = false;
                    	  var jsonFormate = '',cat='',toolKit='';
                    	  if(!$scope.categoryValue && !$scope.toolKitTypeValue){
                    		  $scope.categoryValue = $scope.solutionDetail.modelType;
                    		  $scope.toolKitTypeValue = $scope.solutionDetail.tookitType;
                    		  check = true;
                    	  }
                    	  if($scope.categoryValue){
                    		  cat = '"modelTypeCode\\":\\"' +$scope.categoryValue + '\\"'
                    	  }
                    	  if($scope.toolKitTypeValue){
                    		  toolKit = '"toolKitTypeCode\\":\\"' +$scope.toolKitTypeValue 
                    	  }
                    	  if(cat&&toolKit)var catToolkit = '"{\\' + cat + ',\\' + toolKit + '\\"}"';
                    	  else if(cat&&!toolKit)var catToolkit = '"{\\' + cat +'}"';
                    	  else if(!cat&&toolKit)var catToolkit = '"{\\' + toolKit + '\\"}"';
                    	  
                    	  console.clear();console.log($scope);
                    	  var json={"request_body": {
                    			    	"peerId": $scope.peerIdForSubsList,
                    			    	//"subId": $scope.subId,
                    			    	"selector" : catToolkit,
                    			    	"ownerId" : userId,
                    			    	"scopeType": "FL",
                    			    	"refreshInterval": freqChangeValue || '24',
                    			    	"accessType": "PB"
                    	  				}}
                    	  if(check){$scope.categoryValue='';$scope.toolKitTypeValue='';}
                    	  var url = "api/admin/peer/subcription/create";
                          $http({
                              method : "POST",
                              url : url,
                              data : json
                          }).then(function mySuccess(response) {
                              if(response.data.response_detail ==  "Success"){
                            	  //$scope.closePoup();
                            	  $scope.addedToSubs = true;
                      	    	/*$location.hash('myDialog');  
                                  $anchorScroll(); 
                                  $scope.msg = "Successfully added to subscription list."; 
                                  $scope.icon = 'report_problem';
                                  $scope.styleclass = 'c-success';
                                  $scope.showAlertMessage = true;
                                  $timeout(function() {
                                  	$scope.showAlertMessage = false;
                                  }, 5000);*/
                              }
                          }, function myError(response) {
                        	 
                          });
                      }
                      //delete subscription
                      $scope.deleteSub = function(subId,index){
                    	  //deleteSubscription
                    	  apiService
                   	      .deleteSubscription(subId)
                   	      .then(
                   	    		function(response){
                   	    			$scope.arrDetails.splice(index,1);
                   	    		},
                   	    		function(error){console.log('Error :' +error);});
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
                    	
	                      $scope.confirmMsg = "Do you want to delete this peer ?";
	                	  $scope.warningMsg = "Delete Confirmation";
	                	  $scope.selectedPeer = peerId;
	                	  $mdDialog.show({
	                  		  contentElement: '#confirmPopupDeletePeer',
	                  		  parent: angular.element(document.body),
	                  		  targetEvent: this,
	                  		  clickOutsideToClose: true
	                  	  });
                	  
                      }
                	  
                      $scope.deletePeerFunc = function () {

                  		apiService
                   	      .deletePeer($scope.selectedPeer)
                   	      .then(
                   	    		function(response){

                   	    			getAllPeer();
                   	    			fetchPeer();
                   	    			$scope.closePoup();
                        	    	$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                    $anchorScroll(); 
                                    $scope.msg = "Peer deleted successfully."; 
                                    $scope.icon = '';
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
                    	                                    $scope.icon = '';
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
    	        	                                    $scope.icon = '';
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
                                        		  $scope.confirmMsg = "Do you want to Active/Inactive users ?";
                                        		  $scope.warningMsg = "Active/Inactive Confirmation";
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
                                          //Back to subscription list
                                          $scope.backTo = function(){
                                        	  $scope.subscripDetails1 = false;
                                        	  $scope.solutionDetail = false;
                                        	  //$scope.peerDetailList = val;
                                        	  $scope.arrDetails = '';
                                        	  var url = 'api/admin/peer/subcriptions/' +  $scope.peerDetailList.peerId;
                                        	  $http.post(url).success(function(response){
                                        		  $scope.subId = '';
                                        		  $scope.subId = response.response_body[0].subId;
                                        		  
                                        		  var arrSub = [];
                                        		  angular.forEach(response.response_body, function(value, key) {
                                        			  var catTool = value.selector;
                                    				  var catTool = catTool.split(",");
                                    				  if(catTool.length > 1){
                                    					  angular.forEach($scope.category, function(value, key) {
                                    						  var serch = value.typeCode ;
                                    						  var serchValue = catTool[0].search(serch);
                                    						  if(serchValue > 0)$scope.categoryForSubId = value;
                                    						});
                                    					  angular.forEach($scope.toolKitType, function(value, key) {
                                    						  var serch = value.typeCode ;
                                    						  var serchValue = catTool[1].search(serch);
                                    						  if(serchValue > 0)$scope.toolKitForSubId = value;
                                    						});
                                    				  }else {
                                    					  $scope.toolKitForSubId ='';$scope.categoryForSubId = '';
                                    					  if(catTool[0].search('modelTypeCode') > 0){
                                    						  angular.forEach($scope.category, function(value, key) {
                                        						  var serch = value.typeCode ;
                                        						  var serchValue = catTool[0].search(serch);
                                        						  if(serchValue > 0)$scope.categoryForSubId = value;
                                        						});
                                    					  }
                                    					  else if(catTool[0].search('toolKitTypeCode') > 0){
                                    						  angular.forEach($scope.toolKitType, function(value, key) {
                                        						  var serch = value.typeCode ;
                                        						  var serchValue = catTool[0].search(serch);
                                        						  if(serchValue > 0)$scope.toolKitForSubId = value;
                                        						});
                                    					  }
                                    				  }
                                    				  
                                    				  $scope.frequency;
                                    				  $scope.frequencySelected = [];
                                    				  /*angular.forEach($scope.frequency, function(value1, key1) {*/
                                    					  if(value.refreshInterval == 3600){
                                        					 $scope.frequencySelected[0] =  '1';
                                        				  }else if(value.refreshInterval == 86400){
                                        					  $scope.frequencySelected[1] = '24';
                                        				  }else if(value.refreshInterval == 2592000){ 
                                        					  $scope.frequencySelected[2] = '720';
                                        				  }else if(value.refreshInterval == 24){
                                        					  $scope.frequencySelected[3] = '0';
                                        				  }
                                    					  
                                    				  arrSub.push({
                                    					  "subId" : value.subId,
                                    					  "toolKitType" : $scope.toolKitForSubId.toolkitName,
                                    					  "modelType" : $scope.categoryForSubId.typeName,
                                    					  "updatedOn" : value.modified,
                                    					  "createdOn" : value.created,
                                    					  "frequencySelected" : $scope.frequencySelected
                                    				  })
                                        		  });

                                				 $scope.arrDetails = arrSub;
                                			  
                                        	  });
                                          }
                                          //Delete user 
                                          $scope.deleteUser = function(){
                                        	  var obj = {
                                        			  "request_body": {
                                        				  	"bulkUpdate": $scope.activeYN,
                                        				    "userIdList": $scope.roleArr
                                        				  }
                                        				}
                                        	  apiService.deleteUser(obj)
                                      	    .then(function(response) {
                                      	    	$scope.activeYN = '';
                                      	    	userDetailsFetch();
                                      	    	$scope.roleArr = [];
                                      	    	$scope.closePoup();
                                  				$location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
        	                                    $anchorScroll(); 
        	                                    $scope.msg = "User updated successfully."; 
        	                                    $scope.icon = '';
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
                                          //Get all request
                                          function getAllRequest(){
		                                        var dataPass = {
		                                        		  "fieldToDirectionMap": {},
		                                        		  "page": 0,
		                                        		  "size": 0
		                                        		};
		                                          $http({
												        method : "POST",
												        url : "/api/admin/requests",
												        data : dataPass
												    }).then(function mySuccess(response) {
												    	$scope.subscription = response.data.response_body.requestList;
												    }, function myError(response) {
												    	
												    });
                                          }
                                          getAllRequest();
                                          //Approve/Delete request for federation
                                          $scope.appDelRequest = function(val1,val2){
                                        	  var appDeny = '';
                                        	  if(val2 == "approve"){appDeny = 'approve'}
                                        	  else if(val2 == "deny"){appDeny = 'deny'}
                                        	  var data = {
                                        			  	"action": appDeny,
                                        			    "requestId": val1.requestId,
                                        	  };
                                        	  $http({
											        method : "PUT",
											        url : "/api/admin/request/update",
											        data : data
											    }).then(function mySuccess(response) {
											    	getAllRequest();
											    }, function myError(response) {
											    	
											    });
                                          }
                                          
                                          /*
                                           * Have Added this function for the count as for now. This is not good for the performance. 
                                           * Please remove and ask BE team for an api to return the counts
                                           */
                                          $scope.countSubscriptions = function(){
                                        	  $scope.peer;
                                        	  if($scope.peer){
                                        		  angular.forEach($scope.peer, function(value, key) {
                            						  value.peerId;
                            						  var counyUrl = 'api/admin/peer/subcriptions/' +  value.peerId;
                            						  $http.post(counyUrl).success(function(response){
                            							$scope.subscriptionCount  = response.response_body.length;
                            						  }).error(function(error){
                            							 
                            						  });
                            						});
                                        	  }
                                          }
                                          
                                          
                                          /*get all solutions start*/
                                          $scope.loadAllSolutions = function(){
                                        	 
                    							var dataObj = {
                    								"request_body" : {
                    									"active" : true,
                    									"pageRequest" : {
                    										"page" : 0,
                    										"size" : 1000
                    									}
                    								}
                    							}
                    							console.log(angular.toJson(dataObj));
                    							apiService.insertSolutionDetail(dataObj).then(
                      									function(response) {
                      										
                      										$scope.publicSolList = [];
                      										$scope.mlsolutions = response.data.response_body.content;
                      										$scope.mlsolutionsSize = 0;
                      										
                      										/*if(!$scope.category){
                      											 fetchCat(); 
                      										}
                      										
                      										if(!$scope.toolKitType){
                      											fetchToolKitType();
                      										}*/
                      										
                      										angular.forEach($scope.mlsolutions,function(value1, key1) {
	        		 	    		                        	 if(value1.accessType == "PB" ){
	        		 	    		                        		$scope.mlsolutionsSize = $scope.mlsolutionsSize +1;
                      										$scope.publicSolList.push(
   		 	    		                        				 {
	        		 	    		              					  "accessType" : "PB",
	        		 	    		              					  "ownerId" : userId,
	        		 	    		              					  /*"peerId" : $scope.peerIdForSubsList,*/
	        		 	    		              					  "scopeType" : "FL",
	        		 	    		              					"tookitType" :value1.tookitType,
	        		 	    		              					"modelType": value1.modelType
   		 	    		                        				 }
                      										) 
                      										}
                      										});
                      										console.log(" $scope.publicSolList >>>>>>>>>>> ", $scope.publicSolList)
                      									},
                      									function(error) {
                      										$scope.status = 'Unable to load data: '
                      												+ error.data.error;
                      										console.log($scope.status);
                      									});
                                          }
                                          
                                          $scope.loadAllSolutions();
                                          
                                          /*Add all models start*/
                                          $scope.addAllSolutions = function(){
                  							var addAllSolObj =  $scope.publicSolList;
                                        	  var reqAddObj = {
                                        			  "request_body": 
                                        				  addAllSolObj
                                        			  }
                                        	  
                  							console.log(angular.toJson(reqAddObj));
                  							apiService.insertAddAllSolutions($scope.peerIdForSubsList, freqChangeValue, reqAddObj).then(
                    									function(response) {
                    										
                    										if(response.data.response_detail ==  "Success"){
                    			                            	  $scope.addedAllToSubs = true;
                    			                              }
                    										
                    									},
                    									function(error) {
                    										$scope.status = 'Unable to load data: '
                    												+ error.data.error;
                    										console.log($scope.status);
                    									});
                                        }
                                          
                                          /*End add all models*/
                                        
                                  		/*get solutions ends*/
                                          
										  
										   //Validation code
                                          $scope.addStep = false;
                                          var onBoardingStep = {
                      		  					"step": [
                      		  						{ "stepName" : "Create Micro-service", "class" : "create-docker"},
                      		  						{ "stepName" : "Package", "class" : "create-docker"},
                      		  						{ "stepName" : "Dockerize", "class" : "create-docker"},
                      		  						{ "stepName" : "Create TOSCA", "class" : "create-tosca"},
                      		  						{ "stepName" : "Security Scan", "class" : "create-security"},
                      		  						{ "stepName" : "Add to Repository", "class" : "add-repository"},
                      		  						
                      		               	  ]};
                                          var localFlowStep ={"step": [
                      		  						{ "stepName" : "Model Documentation", "class" : "create-solution"},
                      		  						
                      		  						
                      		               	  ]};
                                          $scope.optionalLocalFlowStep ={"step": [
                		  						{ "stepName" : "License Check", "class" : "create-solution", "active" : "true"},
                		  						{ "stepName" : "Security Scan", "class" : "create-security", "active" : "true"},
                		  						{ "stepName" : "Text Check", "class" : "create-docker", "active" : "true"},
                		  					]};
                                          var publicFlowStep ={"step": [
                      		  						{ "stepName" : "Model Documentation", "class" : "create-solution"},
                      		  								  						
                      		               	  ]};
                                          $scope.optionalPublicFlowStep ={"step": [
              		  						{ "stepName" : "License Check", "class" : "create-solution", "active" : "true"},
              		  						{ "stepName" : "Security Scan", "class" : "create-security", "active" : "true"},
              		  						{ "stepName" : "Text Check", "class" : "create-docker", "active" : "true"},
              		  						{ "stepName" : "Manual Text Check", "class" : "create-docker", "active" : "true"}
              		  					]};
                                          var fedratedStep = {"step": [
                      		  						{ "stepName" : "Model Documentation", "class" : "create-solution"},
                      		  						{ "stepName" : "Security Scan", "class" : "create-security"},
                      		  						{ "stepName" : "License Check", "class" : "create-docker"},
                      		  						{ "stepName" : "Text Check", "class" : "create-docker"},
                      		  					
                      		               	  ]
                                          };
                                          $scope.showPrerenderedDialog = function(ev, dialogId, workFlow) {
                                        	  $scope.workFlow = workFlow;
                                        	  if (workFlow == "On-boarding Work flow"){$scope.workFlowStep = onBoardingStep;}
                                        	  else if (workFlow == "Publishing to Local Work Flow"){$scope.workFlowStep = localFlowStep; }
                                        	  else if (workFlow == "Publishing to Public Work Flow"){$scope.workFlowStep = publicFlowStep;}
                                        	  else if (workFlow == "Import Federated Model Work Flow"){$scope.workFlowStep = fedratedStep;}
                                        	    $mdDialog.show({
                                        	      contentElement: '#'+ dialogId,
                                        	      parent: angular.element(document.body),
                                        	      targetEvent: this,
                                        	      clickOutsideToClose: true
                                        	      
                                        	     
                                        	    });
                                        	  };
                                        	  
                                        	  
                                        	  $scope.getValidationWorkflow = function(flowConfigKey){
                                        		$scope.removeTC = false;
                                        		if (flowConfigKey == "local_validation_workflow"){ $scope.optionalWorkFlowStep = $scope.optionalLocalFlowStep;}
                                          	  	else if (flowConfigKey == "public_validation_workflow"){$scope.optionalWorkFlowStep = $scope.optionalPublicFlowStep;}
                                        		apiService
                      		 	    			.getSiteConfig(flowConfigKey)
                      		 	    			.then(
                      		 	    					function(response) {
                      		 	    						$scope.ignoreWorkFlow = angular.fromJson(response.data.response_body.configValue);
                      		 	    						angular
                      		 	    							.forEach(
                      		 	    								$scope.optionalWorkFlowStep.step,
                      		 	    									function(optionalValue,optionalKey){
                      		 	    										angular
				                      		 	    		                  .forEach(
				                      		 	    		                          $scope.ignoreWorkFlow.ignore_list,
				                      		 	    		                          function( ignoreValue, key) {
				                      		 	    		                        	 if(optionalValue.stepName == ignoreValue ){
				                      		 	    		                        		optionalValue.active = false;
				                      		 	    		                        	 }
				                      		 	    		                          });
                      		 	    						});
                      		 	    						if (flowConfigKey == "local_validation_workflow"){ $scope.optionalLocalFlowStep = $scope.optionalWorkFlowStep; }
                                                      	  	else if (flowConfigKey == "public_validation_workflow"){$scope.optionalPublicFlowStep = $scope.optionalWorkFlowStep;}
                      		 	    					},
                      		 	    					function(error) {console.log(error);
                      		 	    			});
                                        	  }
                                        	  
                                        	  $scope.getValidationWorkflow("local_validation_workflow");
                                        	  
                                        	  $scope.addValidationStep = function(validStep, validKey){
                                        		  $scope.editStep = validStep;
                                        		  if($scope.optionalWorkFlowStep.step[validKey].active){
                                        			  $scope.ignoreWorkFlow.ignore_list.push(validStep);
                                        			  $scope.optionalWorkFlowStep.step[validKey].active = false;
                                        			  $scope.added = false;
                                        		  }else{
                                        			  var index = $scope.ignoreWorkFlow.ignore_list.indexOf(validStep);
                                        			  $scope.ignoreWorkFlow.ignore_list.splice(index, 1);
                                        			  $scope.optionalWorkFlowStep.step[validKey].active = true;
                                        			  $scope.added = true;
                                        		  }
                                        	  };	
                                                  
                                              $scope.assignWorkFlow = function(flow){
                                            	  var configKey = "";
                                            	  if(flow == "Publishing to Local Work Flow" ){configKey = "local_validation_workflow";}
                                            	  else if(flow == "Publishing to Public Work Flow" ){configKey = "public_validation_workflow";}
                                            	  var data = angular.copy($scope.ignoreWorkFlow);
                                                  var strworkFlowConfig = JSON.stringify(data);
                                                  var convertedString = strworkFlowConfig.replace(/"/g, '\"');
                                            	  var reqObj = {
                                            			  "request_body": {
                                            				    "configKey": configKey,
                                            				    "configValue": convertedString,
                                            				    "userId": userId
                                            				  }
                                            				 
                                            				}
                                            	  apiService
                                                  .updateSiteConfig(configKey, reqObj)
                                                  .then(function(response) {
                                        		  console.log("response");
                                                  });
                                              };
                                              
                                              $scope.closeValidationPopup = function(){
                                            	  $mdDialog.hide();  
                                              };
            	    
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
							
							var size = element[0].files[0].size;
		                	if(size >= 2000){
		    	            	scope.imageError = true;
		    	            	modelSetter(scope, "");
		    	            	element.val("");
		    	            	return true;
		    	            }
		    	            scope.imageError = false;
		    	            modelSetter(scope, element[0].files[0]);
		    	            //return true;
						});
					});
				}
			}
		});
