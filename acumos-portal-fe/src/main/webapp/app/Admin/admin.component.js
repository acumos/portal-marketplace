	/*
===============LICENSE_START=======================================================
Acumos  Apache-2.0
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

angular.module('admin').filter('abs', function () {
    return function (num) { return Math.abs(num); }
})
    .component('admin', {
        templateUrl: './app/Admin/admin.template.html',
        controller: function ($scope, apiService, fileUploadService, $mdDialog, $http, $timeout, $location, $anchorScroll, $uibModal, $rootScope, $state, $filter, browserStorageService) {
            componentHandler.upgradeAllRegistered();
            //Editor Module configuration
            $scope.modulesConfig = {
                toolbar: [
                    ['bold', 'italic', 'underline'],        // toggled buttons
                    ['link']
                ]
            };
            //Sorting
            $rootScope.setLoader = false;

            $scope.orderByField = 'username'; $scope.reverseSort = false;
            $scope.orderByFieldFed = 'created'; $scope.reverseSortFederation = true;
            $scope.showAllModelsTable = false;
            $scope.verify = true;
            $scope.changeOrderfor = 0;
            $scope.validImageFile = false;
            $scope.backupStatus=false;

            $scope.changeOrderValue = 0
            //Bulk Action
            $scope.bulkAction = [{ 'name': 'Active User', 'value': 'active' }, { 'name': 'Inactive User', 'value': 'inactive' }]
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
            //Verify check for url in add peer popup
            $scope.verifyUrl = function (apiUrlPop) {
                $scope.verify = false;
                //$scope.verified = !$scope.verified ;
                var url = "api/gateway/ping/" + $scope.editPeerID;
                console.clear(); console.log(url);
				/*var json={"request_body": {
			    	"apiUrl": apiUrlPop,
	  				}}*/
                $http({
                    method: "GET",
                    url: url
                }).then(function mySuccess(response) {
                    console.log("Ping,", response);
                    if (response.data.status_code == 400) {
                        $scope.verified = false;
                        $scope.errorMessage = response.data.error_code + ": " + response.data.response_detail;
                    }
                    else {
                        $scope.verified = true;
                        $scope.successMessage = response.data.response_detail;
                    }
                }, function myError(response) {
                    console.log("Error response", response);
                    $scope.verified = false;
                    $scope.errorMessage = response.data.error_code + ": " + response.data.response_detail;
                });
            }
            $scope.accessType =
                [{
                    'name': 'Full Access',
                    'value': 'FL'
                }, {
                    'name': 'Partial Access',
                    'value': 'RF'
                }];
            $scope.AccessValue = "FL";
            //Browse catelog when category and toolkitype selected
            $scope.browseForCatTool = function () {
                $scope.showSolutionTable = true;
                //allModelsTable && (allSubs == 'true') && showAllModelsTable
                $scope.showAllModelsTable = true;
                $scope.allModelsTable = true;

                //Code for selector
                var jsonFormate = '', cat = '', toolKit = '';
                if ($scope.categoryValue) {
                    cat = '"modelTypeCode":"' + $scope.categoryValue + '"'
                }
                if ($scope.toolKitTypeValue) {
                    toolKit = '"toolkitTypeCode":"' + $scope.toolKitTypeValue
                }
                if (cat && toolKit) { var catToolkit = '{' + cat + ',' + toolKit + '"}'; }
                else if (cat && !toolKit) var catToolkit = '{' + cat + '}';
                else if (!cat && toolKit) var catToolkit = '{' + toolKit + '"}';

                console.clear(); console.log(catToolkit);
                var json = {
                    "request_body": {
                        "peerId": $scope.peerIdForSubsList,
                        //"subId": $scope.subId,
                        "selector": catToolkit,
                        "ownerId": userId,
                        "scopeType": $scope.AccessValue || "FL",
                        "refreshInterval": freqChangeValue,
                        "accessType": "PB"
                    }
                }
                console.log(json); console.log($scope.publicSolList);
                $http({
                    method: "POST",
                    url: "/api/gateway/solutions",
                    data: json
                }).then(function mySuccess(response) {
                    $scope.mlsolutionCatTool = response.data.response_body;
                    if ($scope.publicSolList == "null") { $scope.noSolution = true; }
                }, function myError(response) {
                    $scope.noSolution = true;
                    console.log("Error response", response);
                });
            }
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
            $scope.menuName = 'Monitoring'; $scope.allSelected = true;
            $scope.userDetail = JSON.parse(browserStorageService
                .getUserDetail());
            if ($scope.userDetail != undefined) {
                var userName = $scope.userDetail[0], userId = $scope.userDetail[1];
            }

            $scope.checkAdmin = function () {
                if (browserStorageService.isAdmin() == 'true') {

                } else {
                    $state.go('404Error');
                }
            }
            $scope.checkAdmin();

            $scope.showDocUrl = false;
            apiService.getDashboardUrl().then(function (response) {
                $scope.dashboardUrl = response.data.response_body;
            });
            //API for get Roles
            function getRole() {
                apiService
                    .getRoleCount()     //.getAllRole() //.getRoleCount()
                    .then(
                        function (response) {
                            $scope.roles = response.data.response_body;
                            $scope.rolesLength = $scope.roles.length;
                        },
                        function (error) { });
            }
            getRole();

            $scope.getAllCatalogs = function(){

				var reqObject = {
						"request_body": {
							"fieldToDirectionMap": {"created":"ASC"},
					        "page": 0,
					        "size": 1000
						  }
					};

				$scope.catalogIdsList = [];
				$scope.showRoleLoader = true;
				apiService.getCatalogs(reqObject)
				.then(
					function successCallback(response) {
						var resp = response.data.response_body.content;
						$scope.allCatalogList = {};
						$scope.showRoleLoader = false;
						for(var i=0; i<resp.length; i++ ){
							if( resp[i].accessTypeCode == 'RS') {
								if(!resp[i].origin){
									if($scope.allCatalogList['My Catalogs'] === undefined ) {
										$scope.allCatalogList['My Catalogs'] = [];
									}
									/*else {
										$scope.catalogIdsList.push(resp[i].catalogId);
									}*/
									$scope.catalogIdsList.push(resp[i].catalogId);
									$scope.allCatalogList['My Catalogs'].push(resp[i]);
								} else {
									if($scope.allCatalogList[resp[i].origin] === undefined ) {
										$scope.allCatalogList[resp[i].origin] = [];
									} else {				
										$scope.catalogIdsList.push(resp[i].catalogId);
									}
									$scope.allCatalogList[resp[i].origin].push(resp[i]);
								}
							}
						}
						$scope.catList = angular.copy($scope.allCatalogList);
					});
			}

            $scope.getAllCatalogs();

            //API for get Roles
            function getAllRole() {
            	$scope.showRoleLoader = true;
                apiService
                    .getAllRole()
                    .then(
                        function (response) {
                            $scope.allRoles = response.data.response_body;
                        	$scope.showRoleLoader = false;
                        });
            }


            getAllRole();
            //API for user count
            function userDetailsFetch() {
                apiService
                    .getAllUserCount()
                    .then(
                        function (response) {
                            $scope.user = response.data.response_body.length;
                            $scope.userDetails = response.data.response_body;
                            $scope.alluserDetails = response.data.response_body;
                            $scope.alluserDetails = ($scope.alluserDetails).filter(function (userObj) {
                                return userObj.active == "true";
                            });
                            detailsUser = $scope.userDetails;
                        },
                        function (error) { console.log(error); });
            }
            userDetailsFetch();
            //API for Peer count
            getAllPeer();
            function getAllPeer() {
                var obj = { "fieldToDirectionMap": {}, "page": 0, "size": 0 };
                apiService
                    .getPeers(obj)
                    .then(
                        function (response) {
                            $scope.isSelfTrue = false;
                            $scope.activeCount = 0;
                            $scope.peer2 = response.data.response_body.content;
                            angular.forEach($scope.peer2, function (value, key) {
                                if (value.statusCode == "AC") {
                                    $scope.activeCount = $scope.activeCount + 1;
                                    value["StatusName"] = "Active";
                                } else if (value.statusCode == "IN") {
                                    value["StatusName"] = "Inactive";
                                }

                            });
                            $scope.peer = $scope.peer2;
                            $scope.getPeerSubscriptionCounts();
                        },
                        function (error) { console.log(error); });
            }
            //Close popup
            $scope.closePoup = function () {
                $mdDialog.hide();
                $scope.value = undefined; $scope.roleValue = '';
                $scope.signupForm.$setPristine();
                $scope.signupForm.$setUntouched();
                $scope.signupForm.$rollbackViewValue();
                angular.element('#emailValue').val('');
                $scope.carouselSlide = {};
                $scope.eventCarousel = {};
                $scope.storyCarousel = {}
                $scope.carouselForm.$setPristine();
                $scope.carouselForm.$setUntouched();

                $scope.getPeerSubscriptionCounts();

                $scope.eventForm.$setPristine();
                $scope.eventForm.$setUntouched();

                $scope.storyForm.$setPristine();
                $scope.storyForm.$setUntouched();

                $scope.topSCLength = 0;
                $scope.eventSCLength = 0;
                $scope.successSCLength = 0;

                $scope.itsEdit = false;
                delete $scope.keyval;
                delete $scope.deleteKey;
                fetchPeer(); fetchCat(); $scope.hidePeer = false; $scope.data = '';

            }
            //Open popup
            $scope.showPopup = function (ev) {
                $mdDialog.show({
                    contentElement: '#myDialog',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }


            $scope.selectAllCatalogs = {"checked":false};
            
            $scope.addNewRole = function (role) {

                $scope.selectedCatalogList = [];
                $scope.selectAllCatalogs.checked = false;
                $scope.allCatalogList = angular.copy($scope.catList);
                if(role){
                      $scope.roleName = role.name;

                     apiService.getCatalogsOfRole(role.roleId)
                     .then(
                         function (response) {
                            var role = response.data.response_body;
                            $scope.roleSelectedCatalogList = role;
                            $scope.checkAllCatalogs = [];
                            if(role && role.catalogIds){
                                   angular.forEach($scope.allCatalogList,function (value, key) {
                                	   $scope.checkAllCatalogs.push(value);
                                          for (var j = 0; j < $scope.allCatalogList[key].length; j++) {
                                                 if((role.catalogIds).indexOf($scope.allCatalogList[key][j].catalogId) > -1){
                                                       $scope.allCatalogList[key][j].checked = true;
                                                       $scope.selectedCatalogList.push($scope.allCatalogList[key][j].catalogId);
                                                }

                                          }
                                   });
                                   if($scope.roleSelectedCatalogList.catalogIds.length == $scope.checkAllCatalogs[0].length){
                                	   $scope.selectAllCatalogs = {"checked":true};
                                   } else {
                                	   $scope.selectAllCatalogs = {"checked":false};
                                   }
                            }
                            $mdDialog.show({
                                 templateUrl: '../app/Admin/create-role.html',
                                 parent: angular.element(document.body),
                                 scope: $scope.$new(),
                                 clickOutsideToClose: true
                             });
                        });


                    $scope.roleId = role.roleId;
              } else {
                    $scope.roleName = '';
                    $scope.roleId = '';
                    $scope.selectedCatalogList = [];
                    $mdDialog.show({
                        templateUrl: '../app/Admin/create-role.html',
                        parent: angular.element(document.body),
                        scope: $scope.$new(),
                        clickOutsideToClose: true
                    });
              }
 
            }

            //Open popup Add Peer
            $scope.showPopupPeer = function (ev) {
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
            $scope.showDeletePopup = function (ev, peerId) {
                $scope.peerId = peerId;
                $mdDialog.show({
                    contentElement: '#deleteConfirmPopup',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });

            }
            $scope.showAddSlidesPopup = function (ev) {
                $mdDialog.show({
                    contentElement: '#addSlides',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }

            $scope.showOrderSlidesPopup = function (ev, changeKey) {
                $scope.changeOrderfor = changeKey;
                $mdDialog.show({
                    contentElement: '#changeorder',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }

            $scope.showOrderEventSlidesPopup = function (ev, changeKey) {
                $scope.changeOrderfor = changeKey;
                $mdDialog.show({
                    contentElement: '#changeEventOrder',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }

            $scope.confirmDeleteTopCarousel = function (ev, deleteKey) {
                $scope.deleteKey = deleteKey;
                $mdDialog.show({
                    contentElement: '#deleteTopCarousel',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }

            $scope.confirmDeleteEventCarousel = function (ev, deleteKey) {
                $scope.deleteKey = deleteKey;
                $mdDialog.show({
                    contentElement: '#deleteEventCarousel',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }

            $scope.confirmDeleteStoryCarousel = function (ev, deleteKey) {
                $scope.deleteKey = deleteKey;
                $mdDialog.show({
                    contentElement: '#deleteStoryCarousel',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }
            $scope.showOrderStorySlidesPopup = function (ev, changeKey) {
                $scope.changeOrderfor = changeKey;
                $mdDialog.show({
                    contentElement: '#changeStoryOrder',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }

            $scope.showEventSlidesPopup = function (ev) {
                $mdDialog.show({
                    contentElement: '#addEventSlides',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }

            $scope.showStorySlidesPopup = function (ev) {
                $mdDialog.show({
                    contentElement: '#addStorySlides',
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
            $scope.submitFucn = function () {
                if ($scope.signupForm.$invalid || !$scope.roleValue) { return; }
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
                        "userNewRoleList": $scope.roleValue
                    }
                };

                apiService.addUser(obj/*, $scope.roleValue*/)
                    .then(function (response) {
                        if (response.data.error_code == 500) {
                            $scope.value = null;
                            $scope.data = null;
                            $scope.roleValue = '';
                            $scope.signupForm.fname.$touched = false; $scope.signupForm.lname.$touched = false; $scope.signupForm.uname.$touched = false;
                            $scope.signupForm.email.$touched = false; $scope.signupForm.pwd.$touched = false; $scope.signupForm.cpwd.$touched = false;
                            $scope.signupForm.pwd.$dirty = false;

                            $mdDialog.hide();
                            $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                            $anchorScroll();
                            $scope.msg = "User already exist.";
                            $scope.icon = 'report_problem';
                            $scope.styleclass = 'c-error';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                            return;
                        }
                        userDetailsFetch();
                        getRole();
                        $scope.value = null; $scope.roleValue = '';
                        $scope.signupForm.fname.$touched = false; $scope.signupForm.lname.$touched = false; $scope.signupForm.uname.$touched = false;
                        $scope.signupForm.email.$touched = false; $scope.signupForm.pwd.$touched = false; $scope.signupForm.cpwd.$touched = false;
                        $scope.signupForm.pwd.$dirty = false;
                        $scope.closePoup();
                        $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                        $anchorScroll();
                        $scope.msg = "User Created successfully.";
                        $scope.icon = '';
                        $scope.styleclass = 'c-success';
                        $scope.showAlertMessage = true;
                        $timeout(function () {
                            $scope.showAlertMessage = false;
                        }, 5000);
                        // success
                    },
                        function (response) {
                            console.log('Error :' + response);// optional
                            // failed
                        });
            };

            //Dynamic form implementation
            apiService.getSiteConfig("site_config")
                .then(
                    function (response) {
                        $scope.siteConfig = angular.fromJson(response.data.response_body.configValue);
                    },
                    function (error) {
                        console.log(error);
                    });

            apiService.getTermsConditions()
                .then(
                    function (response) {
                        $scope.termcondition1 = (response.data.response_body);
                        $scope.termcondition2 = angular.copy($scope.termcondition1);
                    },
                    function (error) {
                        console.log(error);
                    });

            //for site content:
            apiService.getContactInfo()
                .then(
                    function (response) {
                        $scope.contactInfo = (response.data.response_body);
                        $scope.contactInfo1 = angular.copy($scope.contactInfo);
                    },
                    function (error) {
                        console.log(error);
                    });

            // for post information
            $scope.TopostContactupdate = function () {
                $scope.siteContenttInfo = unescape(encodeURIComponent($scope.contactInfo));

                var arrayCheck = [];
                for (var i = 0; i < $scope.siteContenttInfo.length; i++) {
                    arrayCheck.push($scope.siteContenttInfo.charCodeAt(i));
                }

                var toSendrequest = {
                    "request_body": {
                        "contentKey": "global.footer.contactInfo",
                        "contentValue": arrayCheck,
                        "mimeType": "application/json"
                    }
                };

                apiService.updateContactInfo(toSendrequest)
                    .then(
                        function (response) {
                            $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                            $anchorScroll();
                            $scope.msg = "Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);

                            $scope.contactInfo1 = angular.copy($scope.contactInfo);
                        },
                        function (error) {
                            console.log(error);
                        });
            }

            // for post Term condition Information
            $scope.TopostConditionupdate = function () {
                $scope.siteConditionInfo = unescape(encodeURIComponent($scope.termcondition1));

                var arrayConditionCheck = [];
                for (var i = 0; i < $scope.siteConditionInfo.length; i++) {
                    arrayConditionCheck.push($scope.siteConditionInfo.charCodeAt(i));
                }

                var toSendConditionrequest = {
                    "request_body": {
                        "contentKey": "global.termsConditions",
                        "contentValue": arrayConditionCheck,
                        "mimeType": "application/json"
                    }
                };

                apiService.updateTermsConditions(toSendConditionrequest)
                    .then(
                        function (response) {
                            $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                            $anchorScroll();
                            $scope.msg = "Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);

                            $scope.termcondition2 = angular.copy($scope.termcondition1);
                        },
                        function (error) {
                            console.log(error);
                        });

            }

            $scope.submitForm = function () {
                angular
                    .forEach(
                        $scope.siteConfig.fields,
                        function (value, key) {
                            if ($scope.siteConfig.fields[key].type == 'file') {
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
                $rootScope.coBrandingImage = ""
                var uploadUrl = "api/users/updateUserImage/"
                    + userId;
                var reqObj = {
                    "request_body": {
                        "configKey": "site_config",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService
                    .updateSiteConfig("site_config", reqObj)
                    .then(
                        function (response) {
                            //$scope.siteConfig = angular.fromJson(response.data.response_body.configValue);
                            angular
                                .forEach(
                                    $scope.siteConfig.fields,
                                    function (value, key) {
                                        if ($scope.siteConfig.fields[key].name == 'siteInstanceName') {
                                            $rootScope.siteInstanceName = $scope.siteConfig.fields[key].data;
                                        } if ($scope.siteConfig.fields[key].name == 'Headerlogo') {
                                            $rootScope.headerImage = $scope.siteConfig.fields[key].data.base64;
                                        } if ($scope.siteConfig.fields[key].name == 'coBrandingLogo' && $scope.siteConfig.fields[key].data) {
                                            $rootScope.coBrandingImage = $scope.siteConfig.fields[key].data.base64;
                                        } if ($scope.siteConfig.fields[key].name == 'Footerlogo') {
                                            $rootScope.footerImage = $scope.siteConfig.fields[key].data.base64;
                                        } if ($scope.siteConfig.fields[key].name == 'enableOnBoarding') {
                                            if ($scope.siteConfig.fields[key].data.name == 'Enabled') {
                                                $rootScope.enableOnBoarding = true;
                                            } else {
                                                $rootScope.enableOnBoarding = false;
                                            }
                                        } if ($scope.siteConfig.fields[key].name == 'EnableDCAE') {
                                            if ($scope.siteConfig.fields[key].data.name == 'Enabled') {
                                                $rootScope.enableDCAE = true;
                                            } else {
                                                $rootScope.enableDCAE = false;
                                            }
                                        } if ($scope.siteConfig.fields[key].name == 'Choose Background color' && $scope.siteConfig.fields[key].data && $rootScope.coBrandingImage) {
                                            $rootScope.coBrandingBg = $scope.siteConfig.fields[key].data;
                                        } if ($scope.siteConfig.fields[key].name == 'Add tooltip to logo' && $scope.siteConfig.fields[key].data && $rootScope.coBrandingImage) {
                                            $rootScope.logoToolTip = $scope.siteConfig.fields[key].data;
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
                            // alert("Updated successfully.");
                            $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                            $anchorScroll();
                            $scope.msg = "Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        },
                        function (error) {
                            console.log(error);
                        });
                //$log.debug($scope.entity);
            }

            $scope.reset = function(){
            myForm.reset();
            }
            $scope.removeFile = function (fileLabel) {
                angular
                    .forEach(
                        $scope.siteConfig.fields,
                        function (value, key) {
                            if ($scope.siteConfig.fields[key].label == fileLabel) {
                                $scope.siteConfig.fields[key].data = "";
                            }
                        });
            }

            $scope.peerChange = function (peer) {
                $scope.editPeer = peer;
            }
            //Peer Popup
            function fetchPeer() {
                var dataObj = { "fieldToDirectionMap": {}, "page": 0, "size": 0 };
                apiService
                    .getPeers(dataObj)
                    .then(
                        function (response) {
                            $scope.peerList = response.data.response_body.content;
                        },
                        function (error) {
                            console.log('Error :' + error);
                        }
                    );
            }
            fetchPeer();
            //Category Popup
            function fetchCat() {

                apiService
                    .getModelTypes()
                    .then(
                        function (response) {

                            $scope.category = response.data.response_body;
                        },
                        function (error) { console.log('Error :' + error); });
            }
            fetchCat();
            //Category Popup
            function fetchToolKitType() {
                apiService
                    .getToolkitTypes()
                    .then(
                        function (response) {
                            console.log(response.data.response_body);
                            $scope.toolKitType = response.data.response_body;
                        },
                        function (error) { console.log('Error :' + error); });
            }
            fetchToolKitType();
            //Category Json
            var arr = [];
            $scope.categorySelect = function (cat) {
                var checkDup = false, dupKey = 0; $scope.string = '';
                angular.forEach(arr, function (value, key) {
                    if (cat.typeCode == value.typeCode) {
                        checkDup = true;
                        dupKey = key;
                    }
                });
                if (checkDup) { arr.splice(dupKey, 1); } else arr.push({ typeName: cat.typeName, typeCode: cat.typeCode });
                angular.forEach(arr, function (value, key) {
                    if (key == 0) { $scope.string = $scope.string + '"{' }
                    $scope.string = $scope.string + '\\"' + value.typeCode + '\\":\\"' + value.typeName + '\\",';
                });
                $scope.queryParam = $scope.string.slice(0, -1);
                $scope.queryParam = $scope.queryParam + '}"';
            }

            //Access type JSON 
            $scope.accessTypeJson = function () {
                $scope.accessTypeJson = [{
                    "access_type_cd": "PR",
                    "access_type_name": "Private"
                }, {
                    "access_type_cd": "OR",
                    "access_type_name": "Company"
                }, {
                    "access_type_cd": "PB",
                    "access_type_name": "Public"
                }]
            }
            $scope.accessTypeJson();
            //Add peer
            $scope.addEditPeer = '';
            $scope.addPeer = function () {
                if ($scope.itsEdit == true) { $scope.updatePeer('detail'); return }
                var peerDetails = {
                    "request_body": {
                        "self": false,
                        "apiUrl": $scope.apiUrlPop,
                        "contact1": $scope.emailIdPop,
                        "description": $scope.descriptionPop,
                        "name": $scope.peerNamePop,
                        "subjectName": $scope.subNamePop,
                        "webUrl": $scope.apiUrlPop,
                        "validationStatusCode": "PS",
                        "statusCode": "IN"
                        //"selector": $scope.queryParam/*"{\"CL\":\"Classification\",\"DT\":\"Data Transform\"}"*/
                    }
                };
                apiService.insertPeers(peerDetails).then(
                    function (response) {
                        if (response.data.error_code == 400) {

                            getAllPeer();
                            $scope.category; fetchCat();
                            $scope.data = ''; $scope.hidePeer = false; $scope.queryParam = '';
                            $scope.closePoup();
                            $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                            $anchorScroll();
                            $scope.msg = "Peer Already Exists.";
                            $scope.icon = 'report_problem';
                            $scope.styleclass = 'c-error';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                            // success
                        } else {
                            getAllPeer();
                            $scope.category; fetchCat();
                            $scope.data = ''; $scope.hidePeer = false; $scope.queryParam = '';
                            $scope.closePoup();
                            $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                            $anchorScroll();
                            $scope.msg = "Peer Created successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                            // success
                        }

                    },
                    function (error) {
                        // handle error 
                    })

            }
            //Edit PEER
            $scope.itsEdit = false;
            $scope.editPeer = function (peerDetail) {
                $scope.verified = null; $scope.errorMessage = "";
                $scope.verify = true;
                $scope.isSelfTrue = peerDetail.self;
                $scope.itsEdit = true; $scope.peerStatus = peerDetail.statusCode;
                $scope.editPeerID = peerDetail.peerId;
                $scope.peerNamePop = peerDetail.name; $scope.subNamePop = peerDetail.subjectName; $scope.emailIdPop = peerDetail.contact1;
                $scope.apiUrlPop = peerDetail.apiUrl; $scope.webUrlPop = peerDetail.apiUrl; $scope.descriptionPop = peerDetail.description;
                $scope.prevSubNamePop = peerDetail.subjectName; $scope.prevApiUrlPop = peerDetail.apiUrl;

                $scope.showPopupPeer();
            }
            $scope.checkUrlChange = function () {
                if ($scope.prevSubNamePop != $scope.subNamePop || $scope.prevApiUrlPop != $scope.apiUrlPop) {
                    $scope.verified = null; $scope.errorMessage = "";
                    $scope.verify = true;
                }
            }

            $scope.isSelfTrue = false;
            $scope.isSelfTrueFun = function (peerDetail, isSelf) {
                /*if ( isSelf == true){
                    $scope.isSelf = true;
                }else{
                    $scope.isSelf = false;
                }*/

                $scope.isSelfTrue = true;
                $scope.isSelf = isSelf;
                $scope.peerStatus = peerDetail.statusCode;
                $scope.editPeerID = peerDetail.peerId;
                $scope.peerNamePop = peerDetail.name; $scope.subNamePop = peerDetail.subjectName; $scope.emailIdPop = peerDetail.contact1;
                $scope.apiUrlPop = peerDetail.apiUrl; $scope.webUrlPop = peerDetail.apiUrl; $scope.descriptionPop = peerDetail.description;
                $scope.updatePeer();
            }


            $scope.updatePeer = function (val) {
                if (val == 'detail') {
                    var peerDetails = {
                        "request_body": {
                            "self": $scope.isSelfTrue,
                            "apiUrl": $scope.apiUrlPop,
                            "contact1": $scope.emailIdPop,
                            "description": $scope.descriptionPop,
                            "name": $scope.peerNamePop,
                            "subjectName": $scope.subNamePop,
                            "webUrl": $scope.apiUrlPop,
                            "peerId": $scope.editPeerID,
                            "validationStatusCode": "PS",
                            "statusCode": $scope.peerStatus
                            //"selector": $scope.queryParam/*"{\"CL\":\"Classification\",\"DT\":\"Data Transform\"}"*/
                        }
                    }
                }
                else if ($scope.isSelfTrue == true) {
                    var peerDetails = {
                        "request_body": {
                            "self": $scope.isSelf,
                            "apiUrl": $scope.apiUrlPop,
                            "contact1": $scope.emailIdPop,
                            "description": $scope.descriptionPop,
                            "name": $scope.peerNamePop,
                            "subjectName": $scope.subNamePop,
                            "webUrl": $scope.apiUrlPop,
                            "peerId": $scope.editPeerID,
                            "validationStatusCode": "PS",
                            "statusCode": $scope.peerStatus
                        }
                    }
                }
                else {
                    if (val.statusCode == "AC") {
                        val.statusCode = "IN"
                    }
                    else {
                        val.statusCode = "AC";
                    }
                    $scope.editPeerID = val.peerId;
                    var peerDetails = {
                        "request_body": {
                            "self": val.self,
                            "apiUrl": val.apiUrl,
                            "contact1": val.contact1,
                            "description": val.description,
                            "name": val.name,
                            "subjectName": val.subjectName,
                            "webUrl": val.webUrl,
                            "peerId": val.peerId,
                            "validationStatusCode": "PS",
                            "statusCode": val.statusCode
                            //"selector": $scope.queryParam/*"{\"CL\":\"Classification\",\"DT\":\"Data Transform\"}"*/
                        }
                    }
                }
                apiService.editPeer($scope.editPeerID, peerDetails).then(
                    function (response) {
                        $scope.isSelfTrue = false;
                        $scope.peer = ''; getAllPeer();
                        //$scope.category;fetchCat();
                        $scope.data = ''; $scope.hidePeer = false; $scope.queryParam = '';
                        $scope.closePoup();
                        $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                        $anchorScroll();
                        $scope.msg = "Peer Updated successfully.";
                        $scope.icon = '';
                        $scope.styleclass = 'c-success';
                        $scope.showAlertMessage = true;
                        $timeout(function () {
                            $scope.showAlertMessage = false;
                        }, 5000);
                        // success
                    },
                    function (error) {
                        // handle error 
                    })
            }
            //Chane cat and tool
            $scope.catChange = function (val1, val2) {
                $scope.showAllModelsTable = false;
                $scope.catValue = ''; $scope.toolType = ''; $scope.noData = false;
                angular.forEach($scope.category, function (value, key) {
                    if (value.typeCode == val1)
                        $scope.catValue = value.typeName;
                });
                angular.forEach($scope.toolKitType, function (value, key) {
                    if (value.typeCode == val2)
                        $scope.toolType = value.typeName;
                });
            }
            //All select federation
            $scope.allSelect = function () {
                $scope.categoryValue = ''; $scope.toolKitTypeValue = ''; $scope.modelIDValue = '';
            }
            $scope.browseClear = function (val) {
                if (val == 'browse') {
                    $scope.showAllModelsTable = true;
                    $scope.allModelsTable = true;
                }
                else if (val == "clear") {
                    $scope.showAllModelsTable = false;
                    $scope.allModelsTable = false;
                }
            }
            //Serch using model id
            $scope.modelEdit = function () {
                $scope.showAllModelsTable = false;
                $scope.addedToSubs = false;

                apiService.getSolutionPicture($scope.modelIDValue)
                    .then(
                        function (response) {
                            if (response.data.response_body)
                                $scope.imgURLdefault = "data:image/jpeg;base64," + response.data.response_body;
                            else
                                $scope.imgURLdefault = "images/default-model.png";
                        },
                        function (error) {
                            $scope.status = 'Unable to load picture data: '
                                + error.data.error;
                        });

                var url = "api/gateway/" + $scope.modelIDValue + "/solution/" + $scope.peerIdForSubsList;
                $http({
                    method: "GET",
                    url: url
                }).then(function mySuccess(response) {
                    $scope.solutionDetail = response.data.response_body;
                    if ($scope.solutionDetail == 'null') { $scope.noData = true; } else { $scope.noData = false; }
                }, function myError(response) {
                    $scope.noData = true;
                    console.log("Error response", response);
                });
            }
            $scope.mdPrimaryClass = false;
            //Subscription popup
            //Open popup Add Peer
            $scope.showPopupPeeR1 = function (ev, val) {
                $scope.catalogList(val.peerId);
                $scope.addedAllToSubs = false;
                $scope.subscripDetails1 = false; $scope.mdPrimaryClass = false; $scope.modelIDValue = '';
                $scope.categoryValue = ''; $scope.arrDetails = ''; $scope.allSubs = 'false'; $scope.allModelsTable = false;
                $scope.showAllModelsTable = false;
                $scope.toolKitTypeValue = ''; $scope.solutionDetail = '';
                $scope.peerIdForSubsList = val.peerId;
                $scope.peerDetailList = val;
                var url = 'api/admin/peer/subcriptions/' + val.peerId;
                $http.post(url).success(function (response) {
                    fetchToolKitType();
                    $scope.subId = '';
                    if (response.response_body.length > 0) {
                        $scope.subId = response.response_body[0].subId;
                    }
                    $scope.arrSub = [];
                    angular.forEach(response.response_body, function (value, key) {
                        var catTool = value.selector;
                        var catTool = catTool.split(",");

                        if (catTool.length > 1) {
                            angular.forEach($scope.category, function (value, key) {
                                var serch = value.code;
                                var serchValue = catTool[0].search(serch);
                                if (serchValue > 0) $scope.categoryForSubId = value;
                            });
                            angular.forEach($scope.toolKitType, function (value, key) {
                                var serch = value.code;
                                var serchValue = catTool[1].search(serch);
                                if (serchValue > 0) $scope.toolKitForSubId = value;
                            });
                        } else {
                            $scope.toolKitForSubId = ''; $scope.categoryForSubId = '';
                            if (catTool[0].search('modelTypeCode') > 0) {
                                angular.forEach($scope.category, function (value, key) {
                                    var serch = value.code;
                                    var serchValue = catTool[0].search(serch);
                                    if (serchValue > 0) $scope.categoryForSubId = value;
                                });
                            }
                            else if (catTool[0].search('toolKitTypeCode') > 0) {
                                angular.forEach($scope.toolKitType, function (value, key) {
                                    var serch = value.code;
                                    var serchValue = catTool[0].search(serch);
                                    if (serchValue > 0) $scope.toolKitForSubId = value;
                                });
                            }
                        }

                        $scope.frequency;
                        $scope.frequencySelected = [];
                        /*angular.forEach($scope.frequency, function(value1, key1) {*/
                        if (value.refreshInterval == 3600) {
                            $scope.frequencySelected[0] = '1';
                        } else if (value.refreshInterval == 86400) {
                            $scope.frequencySelected[1] = '24';
                        } else if (value.refreshInterval == 2592000) {
                            $scope.frequencySelected[2] = '720';
                        } else if (value.refreshInterval == 0) {
                            $scope.frequencySelected[3] = '0';
                        }
                        /*});*/
                        // $scope.frequencySelected = 24;

                        /* convert time freq of update*/


                        $scope.arrSub.push({
                            "subId": value.subId,
                            "updatedOn": value.modified,
                            "createdOn": value.created,
                            "frequencySelected": $scope.frequencySelected,
                            "catalogName":value.catalogName
                        })
                    });

                    $scope.arrDetails = $scope.arrSub;
                });


                $mdDialog.show({
                    contentElement: '#subscriptionPopUp',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }
            // frequency change from add subscription
            var freqChangeValue = '';
            $scope.freqChange = function (freqOfUpdatePass) {
                freqChangeValue = freqOfUpdatePass * 60 * 60;
            }
            //Add to subscription
            $scope.addedToSubs = false;
            $scope.addToSubs = function () {
                var check = false;
                var jsonFormate = '', cat = '', toolKit = '';
                if (!$scope.categoryValue && !$scope.toolKitTypeValue) {
                    //changed since code was getting fetched in modelType earlier
                    /*$scope.categoryValue = $scope.solutionDetail.modelType;
                    $scope.toolKitTypeValue = $scope.solutionDetail.tookitType;*/

                    $scope.categoryValue = $scope.solutionDetail.modelTypeCode;
                    $scope.toolKitTypeValue = $scope.solutionDetail.toolkitTypeCode;
                    check = true;
                }

                // new code
                if ($scope.categoryValue) {
                    cat = '"modelTypeCode":"' + $scope.categoryValue + '"'
                }
                if ($scope.toolKitTypeValue) {
                    toolKit = '"toolkitTypeCode":"' + $scope.toolKitTypeValue
                }
                if (cat && toolKit) { var catToolkit = '{' + cat + ',' + toolKit + '"}'; }
                else if (cat && !toolKit) var catToolkit = '{' + cat + '}';
                else if (!cat && toolKit) var catToolkit = '{' + toolKit + '"}';

                console.clear(); console.log(catToolkit);
                var json = {
                    "request_body": {
                        "peerId": $scope.peerIdForSubsList,
                        //"subId": $scope.subId,
                        "selector": catToolkit,
                        "userId": userId,
                        "scopeType": $scope.AccessValue || "FL",
                        "refreshInterval": freqChangeValue,
                        "accessType": "PB"
                    }
                }
                if (check) { $scope.categoryValue = ''; $scope.toolKitTypeValue = ''; }
                console.clear(); console.log("json>> ", json);
                var url = "api/admin/peer/subcription/create";
                $http({
                    method: "POST",
                    url: url,
                    data: json
                }).then(function mySuccess(response) {
                    console.log("success>> ", response);
                    if (response.data.response_detail == "Success") {
                        fetchToolKitType();
                        $scope.addedToSubs = true;

                    }
                }, function myError(response) {
                    console.log("success>> ", response);
                });
            }


            //update frequency based on peer id subscription starts
            var reqobj1;
            $scope.toUpdatePeer = function(freq,SUBID){

            	$scope.freqChange(freq);

            	$rootScope.setLoader = true;
                 	apiService.getSubsDetails(SUBID)
                 	 .then(
              			 function(response) {

              				$scope.changedCatalogId=response.data.response_body.selector;
              				 reqobj1 = {
                        			"request_body":{
                        				"peerId" : $scope.peerIdForSubsList,
                        				"refreshInterval":freqChangeValue,
                        				"frequencySelected": freq,
                        				"selector": $scope.changedCatalogId,
                        				"subId":SUBID,
                        				"userId":userId
                        			}
                        	}
              				$scope.CallUpdateSubscrption(reqobj1);
              			 },
              			 function (error) {
                             console.log(error);
                         }); 	 
            }

            $scope.CallUpdateSubscrption = function(reqobj1)  {
            	apiService.updateSubscription(reqobj1).then(
    					function(response) {
    						$rootScope.setLoader = false;
                            $location.hash('subscontainer');  // id of a container on the top of the page - where to scroll (top)
                            $anchorScroll();
                            $scope.msg = "Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showSuccessMessage = true;                         
                            $timeout(function () {
                                $scope.showSuccessMessage = false;
                            }, 5000);
                        },
                        function (error) {
                        	$rootScope.setLoader = false;
                            console.log(error);
                        });
    					}

          //update frequency based on peer id subscription  ends



            //delete subscription
            $scope.deleteSub = function (subId, index) {
                //deleteSubscription
                apiService
                    .deleteSubscription(subId)
                    .then(
                        function (response) {
                            $scope.arrDetails.splice(index, 1);
                        },
                        function (error) { console.log('Error :' + error); });
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
            $scope.confirmDelete = function (peerVal) {
                $scope.confirmMsg = "Do you want to delete this peer ?";
                $scope.warningMsg = "Delete Confirmation";
                $scope.selectedPeer = peerVal;
                $mdDialog.show({
                    contentElement: '#confirmPopupDeletePeer',
                    parent: angular.element(document.body),
                    targetEvent: this,
                    clickOutsideToClose: true
                });

            }

            $scope.deletePeerFunc = function () {
                $scope.selectedPeer;
                var peerDetails = {
                    "request_body": {
                        "self": $scope.selectedPeer.self,
                        "apiUrl": $scope.selectedPeer.apiUrl,
                        "contact1": $scope.selectedPeer.contact1,
                        "description": $scope.selectedPeer.description,
                        "name": $scope.selectedPeer.name,
                        "subjectName": $scope.selectedPeer.subjectName + "_" + $scope.selectedPeer.peerId,
                        "webUrl": $scope.selectedPeer.webUrl,
                        "peerId": $scope.selectedPeer.peerId,
                        "validationStatusCode": "PS",
                        "statusCode": "RN"
                    }
                }

                apiService.deactivatePeer($scope.selectedPeer.peerId, peerDetails).then(
                    function (response) {
                        getAllPeer();
                        fetchPeer();
                        $scope.closePoup();
                        $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                        $anchorScroll();
                        $scope.msg = "Peer deleted successfully.";
                        $scope.icon = '';
                        $scope.styleclass = 'c-success';
                        $scope.showAlertMessage = true;
                        $timeout(function () {
                            $scope.showAlertMessage = false;
                        }, 5000);

                    },
                    function (error) {
                        console.log('Error :' + error);
                    })

                /*apiService
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
                            function(error){console.log('Error :' +error);});*/
            }

            $scope.closePopup = function(){
            	$mdDialog.hide();
            }

            //Add Role
            $scope.addRole = function (roleName) {
                var roleDetails = {
                    "request_body": {
                        "name": roleName,
                        "permissionList": ['OB','DS','MP'],
                        "catalogIds": $scope.selectedCatalogList
                    }
                }
                $scope.closePopup();
                if(!$scope.roleId){
                    apiService
                    .urlCreateRole(roleDetails)
                    .then(
                        function (response) {
                        	getAllRole();
                        	$scope.selectedCatalogList = [];
                            $scope.msg = "Role created successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
                } else {
                    apiService
                    .updateUserRole($scope.roleId,roleDetails)
                    .then(
                        function (response) {
                        	getAllRole();
                        	$scope.selectedCatalogList = [];
                            $scope.msg = "Role updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
                }

            }

            $scope.updateRole = function (roleName) {
                var roleDetails = {
                    "request_body": {
                        "name": roleName,
                        "permissionList": ['OB','DS','MP'],
                        "catalogIds": $scope.selectedCatalogList
                    }
                }
                apiService
                    .urlCreateRole(roleDetails)
                    .then(
                        function (response) {
                        	$scope.closePopup();
                        	getAllRole();
                        	$scope.selectedCatalogList = [];
                            $scope.msg = "Role created successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
            }

			$scope.confirmDeleteRole = function(role){

                 $scope.roleId = role.roleId;
                 $mdDialog.show({
                     contentElement: '#deleteRole',
                     parent:  angular.element(document.body),
                     clickOutsideToClose: true
                 }).then(function(flag){

                 });
             }

			$scope.deleteRole = function() {

			  $scope.closePopup();
              return apiService
                  .deleteRole($scope.roleId)
                  .then(
                      function (response) {
                    	  getAllRole();
                          $scope.msg = response.data.response_detail;
                          $scope.showAlertMessage = true;
                          if(response.data.error_code == '100'){
                        	  $scope.icon = '';
                        	  $scope.styleclass = 'c-success';
                          }
                          else{
                        	  $scope.icon = 'info_outline';
                        	  $scope.styleclass = 'c-info';
                          }
                          $timeout(function () {
                              $scope.showAlertMessage = false;
                          }, 3000);
                      },
                 );

			}
            //Change role by admin
            $scope.roleArr = []; var roleMap = []; var roleFin = [];
            $scope.roleCheckbox = function (selectBox, obj) {

                if (selectBox == true) {
                    $scope.roleArr.push(obj.userId);
                } else if (selectBox == false) {
                    $scope.roleArr = jQuery.grep($scope.roleArr, function (value) {
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
            $scope.addRoleFuncConfirm = function (ev) {
                if ($scope.functionCall == 'updateValue') {
                    if (!$scope.roleIdSelected || !$scope.roleIdSelected.length) {
                        $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                        $anchorScroll();
                        $scope.msg = "Please select role.";
                        $scope.icon = '';
                        $scope.styleclass = 'c-success';
                        $scope.showAlertMessage = true;
                        $timeout(function () {
                            $scope.showAlertMessage = false;
                        }, 5000);
                        return;
                    }
                    $scope.confirmMsg = "Do you want to update user's Role ?";
                    $scope.warningMsg = "Change Role";
                } else if ($scope.functionCall == 'deleteValue') {
                   $scope.confirmMsg = "Do you want to " + $scope.activeYN.slice(0, $scope.activeYN.length-1) + "ate users ?";
                    $scope.warningMsg = $scope.activeYN[0].toUpperCase() + $scope.activeYN.slice(1) + " Confirmation";
                }
                $mdDialog.show({
                    contentElement: '#confirmPopup',
                    parent: angular.element(document.body),
                    targetEvent: ev,
                    clickOutsideToClose: true
                });
            }
            $scope.addRoleFunc = function (val) {
                if (val == 'update') {
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
                        "request_body": json
                    };
                    apiService
                        .updateUserRoleNew(json)
                        .then(function (response) {
                            userDetailsFetch();
                            getRole();
                            $scope.roleIdSelected = [];
                            $scope.roleArr = [];
                            $scope.closePoup();
                            $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                            $anchorScroll();
                            $scope.msg = "Role Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        },
                            function (response) {
                                console.log('Error :' + response);// optional
                                // failed
                            });
                }
                else if (val == 'delete') { $scope.deleteUser(); }
            }
            //Back to subscription list
            $scope.backTo = function () {
                $scope.subscripDetails1 = false;
                $scope.solutionDetail = false;
                $scope.addedAllToSubs = false;
                //$scope.peerDetailList = val;
                $scope.arrDetails = '';
                var url = 'api/admin/peer/subcriptions/' + $scope.peerDetailList.peerId;
                $http.post(url).success(function (response) {
                    $scope.subId = '';
                    if (response.response_body.length > 0) {

                        $scope.subId = response.response_body[0].subId;
                    }


                    $scope.arrSub = [];
                    angular.forEach(response.response_body, function (value, key) {
                        var catTool = value.selector;
                        var catTool = catTool.split(",");
                        if (catTool.length > 1) {
                            angular.forEach($scope.category, function (value, key) {
                                var serch = value.typeCode;
                                var serchValue = catTool[0].search(serch);
                                if (serchValue > 0) $scope.categoryForSubId = value;
                            });
                            angular.forEach($scope.toolKitType, function (value, key) {
                                var serch = value.typeCode;
                                var serchValue = catTool[1].search(serch);
                                if (serchValue > 0) $scope.toolKitForSubId = value;
                            });
                        } else {
                            $scope.toolKitForSubId = ''; $scope.categoryForSubId = '';
                            if (catTool[0].search('modelTypeCode') > 0) {
                                angular.forEach($scope.category, function (value, key) {
                                    var serch = value.typeCode;
                                    var serchValue = catTool[0].search(serch);
                                    if (serchValue > 0) $scope.categoryForSubId = value;
                                });
                            }
                            else if (catTool[0].search('toolkitTypeCode') > 0) {
                                angular.forEach($scope.toolKitType, function (value, key) {
                                    var serch = value.typeCode;
                                    var serchValue = catTool[0].search(serch);
                                    if (serchValue > 0) $scope.toolKitForSubId = value;
                                });
                            }
                        }

                        $scope.frequency;
                        $scope.frequencySelected = [];
                        /*angular.forEach($scope.frequency, function(value1, key1) {*/
                        if (value.refreshInterval == 3600) {
                            $scope.frequencySelected[0] = '1';
                        } else if (value.refreshInterval == 86400) {
                            $scope.frequencySelected[1] = '24';
                        } else if (value.refreshInterval == 2592000) {
                            $scope.frequencySelected[2] = '720';
                        } else if (value.refreshInterval == 0) {
                            $scope.frequencySelected[3] = '0';
                        }

                        $scope.arrSub.push({
                            "subId": value.subId,
                            //"toolKitType": $scope.toolKitForSubId.toolkitName,
                            //"modelType": $scope.categoryForSubId.typeName,
                            "updatedOn": value.modified,
                            "createdOn": value.created,
                            "frequencySelected": $scope.frequencySelected,
                             "catalogName":value.catalogName
                        })
                    });

                    $scope.arrDetails =  $scope.arrSub;

                });
            }
            //Delete user
            $scope.deleteUser = function () {
                var obj = {
                    "request_body": {
                        "bulkUpdate": $scope.activeYN,
                        "userIdList": $scope.roleArr
                    }
                }
                apiService.deleteUser(obj)
                    .then(function (response) {
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
                        $timeout(function () {
                            $scope.showAlertMessage = false;
                        }, 5000);
                    },
                        function (response) {
                            console.log('Error :' + response);// optional
                            // failed
                        });
            };
            //Filter on bases on role
            var detailsUser = '';

            $scope.userFilter = function (role) {
                if (role == 'all') { $scope.userDetails = $scope.alluserDetails; return; }
                var temp = [];

                angular
                    .forEach(
                        detailsUser,
                        function (value, key) {
                            var roleList = value.userAssignedRolesList;
                            if (roleList.length) {
                                angular.forEach(
                                    roleList,
                                    function (roleAssigned, key) {
                                        if (roleAssigned.roleId == role.roleId) {
                                            temp.push(value);
                                        }
                                    })
                            }
                        });
                $scope.userDetails = temp;
            }
            //Add Class
            $scope.selected = 0;
            $scope.select = function (index) {
                $scope.selected = index;
            };

            $scope.$watch('filtered', function (val) {
                if (val && val.length == 0 && ($scope.searchUserDetails || $scope.searchPeerDetails) && ($scope.searchUserDetails.firstName || $scope.searchPeerDetails.name)) {
                    $scope.hideLabel = true;
                } else {
                    $scope.hideLabel = false;
                }
            });
            //Get all request
            function getAllRequest() {
                var dataPass = {
                    "fieldToDirectionMap": {},
                    "page": 0,
                    "size": 0
                };
                $http({
                    method: "POST",
                    url: "/api/admin/requests",
                    data: dataPass
                }).then(function mySuccess(response) {
                    $scope.subscription = response.data.response_body.requestList;
                }, function myError(response) {

                });
            }
            getAllRequest();
            //Approve/Delete request for federation
            $scope.appDelRequest = function (val1, val2) {
                var appDeny = '';
                if (val2 == "approve") { appDeny = 'approve' }
                else if (val2 == "deny") { appDeny = 'deny' }
                var data = {
                    "action": appDeny,
                    "requestId": val1.requestId,
                };
                $http({
                    method: "PUT",
                    url: "/api/admin/request/update",
                    data: data
                }).then(function mySuccess(response) {
                    getAllRequest();
                }, function myError(response) {

                });
            }

            /*
             * Have Added this function for the count as for now. This is not good for the performance.
             * Please remove and ask BE team for an api to return the counts
             *
             * Have removed the subscription count -
             */
            /*$scope.countSubscriptions = function(){
                $scope.subscriptionCount = [];
                $scope.peer;
                if($scope.peer){
                    angular.forEach($scope.peer, function(value, key) {
                        value.peerId;
                        var counyUrl = 'api/admin/peer/subcriptions/' +  value.peerId;
                        $http.post(counyUrl).success(function(response){
                          //$scope.subscriptionCount  = response.response_body.length;
                          $scope.subscriptionCount.push(
                                           {
                                                "peerId" : value.peerId,
                                                "subscriptionLength" : response.response_body.length,
                                           }
                                  )

                        }).error(function(error){

                        });
                      });
                }
            }*/

            $scope.getPeerSubscriptionCounts = function () {
                var body = $scope.peer.map(function (peer) {
                    return peer.peerId;
                });
                var json = { "request_body": body };
                var subCountUrl = 'api/admin/peer/subcriptions/counts';
                $http.post(subCountUrl, json)
                    .success(function (response) {
                        $scope.subscriptionCounts = response.response_body;
                    }).error(function (error) {
                        $scope.subscriptionCounts = {};
                        $scope.peer.forEach(function (peer) {
                            $scope.subscriptionCounts[peer.peerId] = -1;
                        });
                        console.error("Error fetching subscription counts");
                    });
            }

            /*get all solutions start*/
            $scope.loadAllSolutions = function () {

                var dataObj = {
                    "request_body": {
                        "active": true,
                        "pageRequest": {
                            "page": 0,
                            "size": 1000
                        }
                    }
                }
                console.log(angular.toJson(dataObj));
                apiService.insertSolutionDetail(dataObj).then(
                    function (response) {

                        $scope.publicSolList = [];
                        $scope.mlsolutions = response.data.response_body.content;
                        $scope.mlsolutionsSize = 0;

                        if ($scope.mlsolutions) {
                            for (var counter = 0; counter < $scope.mlsolutions.length; counter++) {
                                if ($scope.mlsolutions[counter].modelType) {
                                    angular.forEach($scope.category, function (value, key) {
                                        return ($scope.mlsolutions[counter].modelType == value.typeCode) ? $scope.mlsolutions[counter].modelTypeName = value.typeName : '';
                                    });
                                }

                                if ($scope.mlsolutions[counter].tookitType) {
                                    angular.forEach($scope.toolKitType, function (value, key) {
                                        return ($scope.mlsolutions[counter].tookitType == value.typeCode) ? $scope.mlsolutions[counter].tookitTypeName = value.typeName : '';
                                    });
                                }
                            }
                        }

                        angular.forEach($scope.mlsolutions, function (value1, key1) {
                            if (value1.accessType == "PB") {
                                $scope.mlsolutionsSize = $scope.mlsolutionsSize + 1;
                                $scope.publicSolList.push(
                                    {
                                        "accessType": "PB",
                                        "ownerId": userId,
                                        /*"peerId" : $scope.peerIdForSubsList,*/
                                        "scopeType": "FL",
                                        "tookitType": value1.tookitType,
                                        "modelType": value1.modelType,
                                        "refreshInterval": freqChangeValue

                                    }
                                )
                            }
                        });
                        console.log(" $scope.publicSolList >>>>>>>>>>> ", $scope.publicSolList)
                    },
                    function (error) {
                        $scope.status = 'Unable to load data: '
                            + error.data.error;
                        console.log($scope.status);
                    });
            }

            $scope.loadAllSolutions();

            /*Add all models start*/
            $scope.addAllSolutions = function () {
            	 if(typeof $scope.CatalogSendDeatils == "undefined"){
                	  $location.hash('subscontainer');  // id of a container on the top of the page - where to scroll (top)
                      $anchorScroll();
                      $scope.msg = "Please select any catalog";
                      $scope.icon = 'info_outline';
                      $scope.styleclass = 'c-error';
                      $scope.showSuccessMessage = true;
                      $timeout(function () {
                          $scope.showSuccessMessage = false;
                      }, 5000);
                 } else {
	                var reqAddObj = {
	                    "request_body": {
	                        "userId": userId,
	                        "refreshInterval": freqChangeValue,
	                        "selector": "{\"catalogId\":\"" + $scope.CatalogSendDeatils.catalogId + "\"}",
	                        "catalogName": $scope.CatalogSendDeatils.name
	                    }
	                };

	                angular.element(document.body).css('cursor', 'progress');
	                angular.element('#addSubscriptionBtn').css('cursor', 'progress');
	                apiService.insertAddAllSolutions($scope.peerIdForSubsList, reqAddObj).then(
	                    function (response) {
	                        fetchToolKitType();
	                        angular.element(document.body).css('cursor', 'default');
	                        angular.element('#addSubscriptionBtn').css('cursor', 'default');
	                        if (response.data.response_detail == "Success") {
	                            $scope.addedAllToSubs = true;
	                        } else if (response.data.response_detail == "Create PeerSubscription Failed: Catalog name conflict") {
	                        	$location.hash('subscontainer');  // id of a container on the top of the page - where to scroll (top)
	                            $anchorScroll();
	                            $scope.msg = "Local catalog shares name with remote catalog, resolve conflict and try again";
	                            $scope.icon = 'info_outline';
	                            $scope.styleclass = 'c-error';
	                            $scope.showSuccessMessage = true;
	                            $timeout(function () {
	                                $scope.showSuccessMessage = false;
	                            }, 5000);
	                        }

	                    },
	                    function (error) {
	                        angular.element(document.body).css('cursor', 'default');
	                        angular.element('#addSubscriptionBtn').css('cursor', 'default');
	                        $scope.status = 'Unable to load data: '
	                            + error.data.error;
	                        console.log($scope.status);
	                    });
	            }
            }

            /*End add all models*/

            /*fetch number of subscriptions per peer.
             * Currently invoking api from fe
             * Need counts from BE since performance reduces.
             * */

            /*var url = 'api/admin/peer/subcriptions/' +  val.peerId;
            $http.post(url).success(function(response){*/

            /*get solutions ends*/


            //Validation code
            //$scope.stepIndex =0;
            $scope.addStep = false;
            $scope.assignOnBoardingActivate = false;
            $scope.assignFedratedActivate = false;
            $scope.assignLocalActivate = false;
            $scope.assignPublicActivate = false;
            var onBoardingStep = {
                "step": [
                    { "stepName": "Create Micro-service", "class": "create-docker" },
                    { "stepName": "Package", "class": "create-docker" },
                    { "stepName": "Dockerize", "class": "create-docker" },
                    { "stepName": "Create TOSCA", "class": "create-tosca" },
                    { "stepName": "Security Scan", "class": "create-security" },
                    { "stepName": "Add to Repository", "class": "add-repository" },

                ]
            };
            var localFlowStep = {
                "step": [
                    { "stepName": "Model Documentation", "class": "create-model-documentation" },


                ]
            };
            $scope.optionalLocalFlowStep = {
                "step": [
                    { "stepName": "Security Scan", "class": "create-security-gray", "active": "true" },
                    { "stepName": "License Check", "class": "create-licence-check", "active": "true" },
                    { "stepName": "Text Check", "class": "create-text-check", "active": "true" }
                ]
            };
            var publicFlowStep = {
                "step": [
                    { "stepName": "Model Documentation", "class": "create-model-documentation" },

                ]
            };
            $scope.optionalPublicFlowStep = {
                "step": [
                    { "stepName": "Security Scan", "class": "create-security-gray", "active": "true" },
                    { "stepName": "License Check", "class": "create-licence-check", "active": "true" },
                    { "stepName": "Text Check", "class": "create-text-check", "active": "true" }
                    //{ "stepName" : "Manual Text Check", "class" : "create-manual-text-check", "active" : "true"}
                ]
            };
            var fedratedStep = {
                "step": [
                    { "stepName": "Model Documentation", "class": "create-model-documentation" },
                    { "stepName": "Security Scan", "class": "create-security-gray" },
                    { "stepName": "License Check", "class": "create-licence-check" },
                    { "stepName": "Text Check", "class": "create-text-check" },

                ]
            };

            $scope.showPrerenderedDialog = function (ev, dialogId, workFlow) {
                $scope.workFlow = workFlow;
                if (workFlow == "On-boarding Work flow") { $scope.workFlowStep = onBoardingStep; }
                else if (workFlow == "Publishing to Local Work Flow") { $scope.workFlowStep = localFlowStep; $scope.optionalWorkFlowStep = $scope.optionalLocalFlowStep; }
                else if (workFlow == "Publishing to Public Work Flow") { $scope.workFlowStep = publicFlowStep; $scope.optionalWorkFlowStep = $scope.optionalPublicFlowStep; }
                else if (workFlow == "Import Federated Model Work Flow") { $scope.workFlowStep = fedratedStep; }
                $mdDialog.show({
                    contentElement: '#' + dialogId,
                    parent: angular.element(document.body),
                    targetEvent: this,
                    clickOutsideToClose: true


                });
            };

            $scope.getValidationWorkflow = function (flowConfigKey) {
                $scope.activeworkFlowStep = [];
                if (flowConfigKey == "local_validation_workflow") { $scope.optionalWorkFlowStep = $scope.optionalLocalFlowStep; }
                else if (flowConfigKey == "public_validation_workflow") { $scope.optionalWorkFlowStep = $scope.optionalPublicFlowStep; }
                apiService
                    .getSiteConfig(flowConfigKey)
                    .then(
                        function (response) {
                            $scope.ignoreWorkFlow = angular.fromJson(response.data.response_body.configValue);
                            angular
                                .forEach(
                                    $scope.optionalWorkFlowStep.step,
                                    function (optionalValue, optionalKey) {
                                        angular
                                            .forEach(
                                                $scope.ignoreWorkFlow.ignore_list,
                                                function (ignoreValue, key) {
                                                    if (optionalValue.stepName == ignoreValue) {
                                                        optionalValue.active = "false";
                                                    }
                                                });
                                    });
                            angular
                                .forEach(
                                    $scope.optionalWorkFlowStep.step,
                                    function (optionalValue, optionalKey) {
                                        if (optionalValue.active == "true") {
                                            $scope.activeworkFlowStep.push(optionalValue);
                                        }
                                    });
                            if (flowConfigKey == "local_validation_workflow") {
                                $scope.optionalLocalFlowStep = $scope.optionalWorkFlowStep;
                                $scope.activeLocalFlowStep = $scope.activeworkFlowStep;
                                $scope.getValidationWorkflow("public_validation_workflow");
                            } else if (flowConfigKey == "public_validation_workflow") {
                                $scope.optionalPublicFlowStep = $scope.optionalWorkFlowStep;
                                $scope.activePublicFlowStep = $scope.activeworkFlowStep;
                            }
                        },
                        function (error) {
                            console.log(error);
                        });
            }

            $scope.getValidationWorkflow("local_validation_workflow");

            $scope.addValidationStep = function (validStep, validKey) {
                $scope.editStep = validStep;
                $scope.activeworkFlowStep = [];
                if ($scope.optionalWorkFlowStep.step[validKey].active == "true") {
                    $scope.ignoreWorkFlow.ignore_list.push(validStep);
                    $scope.optionalWorkFlowStep.step[validKey].active = "false";
                    $scope.added = "false";
                } else {
                    var index = $scope.ignoreWorkFlow.ignore_list.indexOf(validStep);
                    $scope.ignoreWorkFlow.ignore_list.splice(index, 1);
                    $scope.optionalWorkFlowStep.step[validKey].active = "true";
                    $scope.added = "true";
                }
                angular
                    .forEach(
                        $scope.optionalWorkFlowStep.step,
                        function (optionalValue, optionalKey) {
                            if (optionalValue.active == "true") {
                                $scope.activeworkFlowStep.push(optionalValue);
                            }
                        });
                if ($scope.workFlow == "On-boarding Work flow") { $scope.assignOnBoardingActivate = true; }
                else if ($scope.workFlow == "Publishing to Local Work Flow") { $scope.assignLocalActivate = true; $scope.activeLocalFlowStep = $scope.activeworkFlowStep; }
                else if ($scope.workFlow == "Publishing to Public Work Flow") { $scope.assignPublicActivate = true; $scope.activePublicFlowStep = $scope.activeworkFlowStep; }
                else if ($scope.workFlow == "Import Federated Model Work Flow") { $scope.assignFedratedActivate = true; }
                $timeout(function () {
                    $scope.added = "";
                }, 5000);
            };

            $scope.assignWorkFlow = function (flow) {
                var configKey = "";
                if (flow == "Publishing to Local Work Flow") { configKey = "local_validation_workflow"; }
                else if (flow == "Publishing to Public Work Flow") { configKey = "public_validation_workflow"; }
                $scope.assignLocalActivate = false;
                $scope.assignPublicActivate = false;
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
                    .then(function (response) {
                        $scope.assignAlert = true;
                        $timeout(function () {
                            $scope.assignAlert = false;
                        }, 5000);
                        console.log("response");
                    });
            };

            $scope.closeValidationPopup = function () {
                $mdDialog.hide();
            };
            // Upload Image


            $scope.enableclose=false;

            $scope.uploadLogoImg = function () {
            	if($scope.logoImage) {
                    var data = $scope.fileData.split('base64,').pop();
                    var ext = $scope.logoImage.name.split('.')[1];
                    var toSend = {
                        "request_body": {
                            "contentValue": data,
                            "mimeType": "image/" + ((ext == "jpg") ? "jpeg" : ext)
                        }
                    };

                    apiService.uploadCobrandLogo(toSend)
                        .then(
                            function (response) {
                            	$scope.preview = $scope.fileData;
                            	$scope.enableclose=true;
                            	$rootScope.coBrandingImage = $scope.fileData;
                                $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                                $anchorScroll();
                                $scope.msg = "The Co-Branding Logo is updated successfully.";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.showAlertMessage = true;
                                $timeout(function () {
                                    $scope.showAlertMessage = false;
                                }, 5000);

                            },
                            function (error) {
                                $scope.serverResponse = 'An error has occurred';
                            })
            	}
            }

            $scope.deleteLogoImg = function () {
            	apiService.deleteCobrandLogo()
                .then(
                    function (response) {

                    	$scope.enableclose=false;
                    	$scope.showDialogue=false;
                    	$scope.preview='images/img-list-item.png';
                    	$rootScope.coBrandingImage = '';
                        $location.hash('myDialog');  // id of a container on the top of the page - where to scroll (top)
                        $anchorScroll();
                        $scope.msg = "The Co-Branding Logo is deleted successfully.";
                        $scope.icon = '';
                        $scope.styleclass = 'c-success';
                        $scope.showAlertMessage = true;
                        $timeout(function () {
                            $scope.showAlertMessage = false;
                        }, 5000);
                        $scope.logoImage={};
                        $scope.validImageFile = false;
                    },
                    function (error) {
                        $scope.serverResponse = 'An error has occurred';
                        console.error(error);
                    })


               $mdDialog.hide();
            }

            //GET Co-Branding Logo

            $scope.getCobrandingLogo = function() {
         	   apiService.getCobrandLogo()
 				 .then(
 						 function(response) {
 							if(response.status==204){
 								$scope.preview='images/img-list-item.png'
 								$scope.upload=true;
 							    $scope.showDialogue=false;
 							    $scope.enableclose=false;
 							}else{
 							$scope.preview="/api/site/content/global/coBrandLogo";
 							$scope.enableclose=true;
 							//$scope.name=window.localStorage.getItem('cobrandingLogoName');
 							$scope.upload=false;
 							$scope.showDialogue=true;
 							}

 						 });
             }
            $scope.getCobrandingLogo();

            $scope.showDeleteDialog = function() {
				$mdDialog.show({
					templateUrl : '../app/Admin/image-logo-delete.template.html',
					clickOutsideToClose : true,
					locals: { parent: $scope},
					controller : function DialogController($scope, parent) {
						$scope.parent = parent;
						$scope.closePoup = function(){
							$mdDialog.hide();
						}
					}
				});
			}

            //Search Data
            $scope.searchData = function (searchValue) {
                var rolename = "";
                angular.forEach(searchValue.userAssignedRolesList, function (userAssignedRolesList) {
                    rolename = userAssignedRolesList.name;
                });

                return (angular.lowercase(searchValue.username).indexOf(angular.lowercase($scope.search) || '') !== -1 ||
                    angular.lowercase(searchValue.emailId).indexOf(angular.lowercase($scope.search) || '') !== -1 ||
                    angular.lowercase(searchValue.status).indexOf(angular.lowercase($scope.search) || '') !== -1 ||
                    angular.lowercase(rolename).indexOf(angular.lowercase($scope.search) || '') !== -1);
            };

            //Get Top Carousel For home screen
            $scope.getCarouselConfig = function () {
                apiService
                    .getSiteConfig("carousel_config")
                    .then(
                        function (response) {
                            if (response.data.response_body != null) {
                                $scope.carouselConfig = angular.fromJson(response.data.response_body.configValue);
                            }
                        });
            }
            $scope.getCarouselConfig();
            $scope.carousel_Info_Aling = 'right';
            $scope.carousel_Text_Aling = 'right';
            $scope.bgColor = ['#0366d6', '#10A6B5', '#8529f5', '#7B132A', '#D5305A', '#F49419'];

            //Select background color
            $scope.selectBgColor = function (color) {
                $scope.carousel_bgColor = color;
            }

            $scope.scCharLimit = 140;
            $scope.headlineCharLimit = 60;

            $scope.topSC = "";
            $scope.updateTopSC = function (text) {
                $scope.topSC = text.trim();
            }

            $scope.eventSC = "";
            $scope.updateEventSC = function (text) {
                $scope.eventSC = text.trim();
            }

            $scope.successSC = "";
            $scope.updateSuccessSC = function (text) {
                $scope.successSC = text.trim();
            }

            $scope.isWithinCharLimit = function (string, charLimit) {
                return (string) ? string.length <= charLimit : true;
            }
            $scope.charsLeft = function (string, charLimit) {
                return charLimit - (string ? string.length : 0);
            }

            $scope.isSlideValid = function (slide, type) {
                var valid = true && slide;
                valid = valid && slide.name && slide.name.length > 0;
                if (["top", "event"].includes(type)) {
                    valid = valid && slide.headline && slide.headline.length > 0;
                    valid = valid && $scope.isWithinCharLimit(slide.headline, $scope.headlineCharLimit);
                    valid = valid && (!slide.graphicImgEnabled || $scope.carouselSlide.infoGraphic || $scope.carouselSlide.hasInfoGraphic);
                    valid = valid && slide.textAling;
                    valid = valid && (!slide.supportingContent || $scope.isWithinCharLimit((type == "top") ? $scope.topSC : $scope.eventSC, $scope.scCharLimit));
                } else if (type == "story") {
                    valid = valid && slide.authorName && slide.authorName.length > 0;
                    valid = valid && $scope.isWithinCharLimit(slide.authorName, $scope.headlineCharLimit);
                    valid = valid && slide.supportingContent && $scope.isWithinCharLimit($scope.successSC, $scope.scCharLimit);
                }

                return (valid != undefined) && valid;
            }

            $scope.addCarouselSlide = function () {
                var validImages = true;
                if ((!$scope.carouselSlide.backGround
                    || !$scope.validateImageSize($scope.carouselSlide.backGround, 2560, 524))
                    && (!$scope.carouselSlide.infoGraphic
                        || !$scope.validateImageSize($scope.carouselSlide.infoGraphic, 494, 867))) {
                    //create json
                    var slide = {};
                    var links = {};
                    var carousel = {};

                    if (typeof $scope.carouselConfig === "undefined") {
                        var keys = [];
                        var carousel = {};
                        var uniqueKeys = [];
                    } else {
                        var keys = Object.keys($scope.carouselConfig);
                        var carousel = $scope.carouselConfig;
                        var uniqueKeys = Object.values($scope.carouselConfig).map(function (item) {
                            return item.uniqueKey;
                        });
                    }
                    if (keys.length == undefined || keys.length == 0) {
                        var keyIndex = 0;
                        var uniqueIndex = 0;
                    } else {
                        var keyIndex = parseInt(keys[keys.length - 1]) + 1;
                        var uniqueIndex = uniqueKeys.sort(function (a, b) { return b - a })[0];
                    }
                    //return;
                    var slide_name = $scope.carouselSlide.name;
                    var slide_headline = $scope.carouselSlide.headline;
                    var slide_supportingContent = $scope.carouselSlide.supportingContent;
                    var slide_tagName = $scope.carouselSlide.tagName;
                    slide['name'] = slide_name;
                    slide['headline'] = slide_headline;
                    slide['supportingContent'] = slide_supportingContent;
                    slide['textAling'] = $scope.carouselSlide.textAling;
                    slide['bgColor'] = $scope.carousel_bgColor;

                    slide['graphicImgEnabled'] = $scope.carouselSlide.graphicImgEnabled;

                    if ($scope.itsEdit) {
                        slide['number'] = $scope.keyval;
                        slide['slideEnabled'] = $scope.carouselSlide.slideEnabled;
                        var keyIndex = $scope.keyval;
                        slide['uniqueKey'] = $scope.carouselSlide.uniqueKey;
                    } else {
                        slide['slideEnabled'] = "true";
                        slide['number'] = keyIndex + 1;
                        slide['uniqueKey'] = uniqueIndex + 1;
                    }
                    slide['tagName'] = slide_tagName;
                    slide['bgImageUrl'] = $scope.carouselBGFileName;
                    slide['InfoImageUrl'] = $scope.carouselInfoFileName;
                    slide['bgImgKey'] = "carousel.top." + slide['uniqueKey'] + ".bgImg";
                    slide['infoImgKey'] = "carousel.top." + slide['uniqueKey'] + ".infoImg";
                    slide['hasInfoGraphic'] = $scope.carouselSlide.hasInfoGraphic || ($scope.carouselSlide.infoGraphic != null);

                    if ($scope.carouselSlide.links) {
                        links['enableLink'] = $scope.carouselSlide.links.enableLink;
                        links['primary'] = {};
                        if (!angular.isUndefined($scope.carouselSlide.links.primary)) {
                            links['primary']['label'] = $scope.carouselSlide.links.primary.label;
                            links['primary']['address'] = $scope.carouselSlide.links.primary.address;
                            if ($scope.carouselSlide.links.primary.address == 'other') {
                                links['primary']['url'] = $scope.carouselSlide.links.primary.url;
                            }
                        }
                        links['secondary'] = {};
                        if (!angular.isUndefined($scope.carouselSlide.links.secondary)) {
                            links['secondary']['label'] = $scope.carouselSlide.links.secondary.label;
                            links['secondary']['address'] = $scope.carouselSlide.links.secondary.address;
                            if ($scope.carouselSlide.links.secondary.address == 'other') {
                                links['secondary']['url'] = $scope.carouselSlide.links.secondary.url;
                            }
                        }
                    } else
                        links['enableLink'] = false;

                    slide['links'] = links;
                    carousel[keyIndex] = slide;
                    var carouselConfigStr = JSON.stringify(carousel);

                    var convertedString = carouselConfigStr.replace(/"/g, '\"');

                    var reqObj = {
                        "request_body": {
                            "configKey": "carousel_config",
                            "configValue": convertedString,
                            "userId": userId
                        }
                    };
                    //if carouselConfig is not present in DB create a record else update the record

                    //typeof thing === "undefined"
                    if (typeof $scope.carouselConfig === "undefined") {
                        apiService
                            .createSiteConfig(reqObj)
                            .then(
                                function (response) {
                                    $scope.getCarouselConfig();
                                    $scope.msg = "Carousel Updated successfully.";
                                    $scope.icon = '';
                                    $scope.styleclass = 'c-success';
                                    $scope.closePoup();
                                    $scope.showAlertMessage = true;
                                    $scope.itsEdit = false;
                                    delete $scope.keyval;
                                    $timeout(function () {
                                        $scope.showAlertMessage = false;
                                    }, 5000);
                                });
                    } else {
                        apiService
                            .updateSiteConfig("carousel_config", reqObj)
                            .then(
                                function (response) {
                                    $scope.msg = "Carousel Updated successfully.";
                                    $scope.icon = '';
                                    $scope.styleclass = 'c-success';
                                    $scope.closePoup();
                                    $scope.showAlertMessage = true;
                                    $scope.itsEdit = false;
                                    delete $scope.keyval;
                                    $timeout(function () {
                                        $scope.showAlertMessage = false;
                                    }, 5000);
                                });
                    }

                    if ($scope.carouselSlide.backGround) {
                        // Save top background
                        $scope.saveCarouselPicture(slide['bgImgKey'],
                            $scope.carouselSlide.backGround);
                    }
                    if ($scope.carouselSlide.infoGraphic) {
                        // Save top infographic
                        $scope.saveCarouselPicture(slide['infoImgKey'],
                            $scope.carouselSlide.infoGraphic);
                    }
                }
            }

            $scope.deleteCarouselSlide = function () {
                //delete $scope.carouselConfig[key];
                var key = $scope.deleteKey;
                $scope.deleteCarouselPicture($scope.carouselConfig[key]['bgImgKey']);
                $scope.deleteCarouselPicture($scope.carouselConfig[key]['infoImgKey']);
                var updatedCarouselConfig = [];
                for (var i = 0; i < Object.keys($scope.carouselConfig).length; i++) {
                    updatedCarouselConfig[i] = $scope.carouselConfig[i];
                }

                updatedCarouselConfig.splice(key, 1);
                var toMap = {};
                for (var i = 0; i < updatedCarouselConfig.length; i++) {
                    toMap[i] = updatedCarouselConfig[i];
                }
                $scope.carouselConfig = toMap;

                var carouselConfigStr = JSON.stringify($scope.carouselConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "carousel_config",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("carousel_config", reqObj)
                    .then(
                        function (response) {
                            $scope.closePoup();
                            $scope.getCarouselConfig();
                            $scope.msg = "Carousel Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
            }

            $scope.editCarouselSlide = function (key, val) {
                $scope.itsEdit = true;
                $scope.carouselSlide = val;
                $scope.carouselBGFileName = val['bgImageUrl'];
                $scope.carouselInfoFileName = val['InfoImageUrl'];
                if ($scope.topSC == "") {
                    $scope.keyval = key;
                    $scope.showAddSlidesPopup();
                }
                else {
                    $scope.topSC = val['supportingContent'].replace(/<(?:.|\n)*?>/gm, '');
                    $scope.keyval = key;
                    $scope.showAddSlidesPopup();
                }
            }
            $scope.order = {};
            $scope.changeCarouselSlideOrder = function (ev) {

                $scope.changeOrderfor = parseInt($scope.changeOrderfor, 10);
                $scope.order.changeOrderValue = parseInt($scope.order.changeOrderValue, 10);
                if (isNaN($scope.order.changeOrderValue) || $scope.order.changeOrderValue > Object.keys($scope.carouselConfig).length - 1 || $scope.order.changeOrderValue < 0) {
                    $scope.topSlideOrderError = true;
                    return;
                }

                $scope.topSlideOrderError = false;
                var updatedCarouselConfig = [];
                for (var i = 0; i < Object.keys($scope.carouselConfig).length; i++) {
                    updatedCarouselConfig[i] = $scope.carouselConfig[i];
                }

                var tempvalue = updatedCarouselConfig.splice($scope.changeOrderfor, 1);
                updatedCarouselConfig.splice($scope.order.changeOrderValue, 0, tempvalue[0]);

                var toMap = {};
                for (var i = 0; i < updatedCarouselConfig.length; i++) {
                    toMap[i] = updatedCarouselConfig[i];
                }
                $scope.carouselConfig = toMap;

                var carouselConfigStr = JSON.stringify($scope.carouselConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "carousel_config",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("carousel_config", reqObj)
                    .then(
                        function (response) {
                            $scope.getCarouselConfig();
                            $scope.msg = "Carousel Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.closePoup();
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
            }



            $scope.alingInfoImage = function (alingment) {
                $scope.carousel_Info_Aling = alingment;
            }

            $scope.alingText = function (alingment) {
                $scope.carousel_Text_Aling = alingment;
            }

            $scope.carouselCheckedList = [];
            $scope.toggleCarouselCheckedList = function (key) {

                var index = $scope.carouselCheckedList.indexOf(key);
                if (index == -1) {
                    $scope.carouselCheckedList.push(key);
                } else {
                    $scope.carouselCheckedList.splice(index, 1);
                }
            }

            $scope.changeCarouselSlides = function () {
                var singleSlideFlag = false;
                if ($scope.carouselCheckedList.length == 1) { singleSlideFlag = true; }
                for (var i = 0; i < $scope.carouselCheckedList.length; i++) {
                    $scope.carouselConfig[$scope.carouselCheckedList[i]]['slideEnabled'] = $scope.changeAction;
                }
                for (var i = 0; i < Object.keys($scope.carouselConfig).length; i++) {
                    if ($scope.carouselConfig[i].slideEnabled == 'true') { singleSlideFlag = false; break; }
                    else if (i == Object.keys($scope.carouselConfig).length - 1) {
                        $scope.carouselConfig[0].slideEnabled = 'true';
                        $scope.showWarningMessage = false;
                        $scope.warning = "By default, one slide should be enable.";
                        $scope.icon = '';
                        $scope.styleclass = 'c-success';
                        $scope.changeAction = "Enable/Disable Slides";
                        $scope.showWarningMessage = true;
                        singleSlideFlag = true;
                        $timeout(function () {
                            $scope.showWarningMessage = false;
                        }, 5000);
                    }
                }
                var carouselConfigStr = JSON.stringify($scope.carouselConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "carousel_config",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("carousel_config", reqObj)
                    .then(
                        function (response) {
                            $scope.getCarouselConfig();
                            $scope.changeAction = "Enable/Disable Slides";

                            if ($scope.carouselCheckedList.length >= 1) {
                                if (!singleSlideFlag) {
                                    $scope.msg = "Carousel Updated successfully.";
                                    $scope.icon = '';
                                    $scope.styleclass = 'c-success';
                                    $scope.showAlertMessage = true;
                                    $timeout(function () {
                                        $scope.showAlertMessage = false;
                                    }, 5000);
                                }
                            }
                            else {
                                $scope.showWarningMessage = false;
                                $scope.warning = "Please Select any Slides";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeAction = "Enable/Disable Slides";
                                $scope.showWarningMessage = true;

                                $timeout(function () {
                                    $scope.showWarningMessage = false;
                                }, 5000);
                            }
                            //}
                        });

            }

            //IMage Validation
            $scope.imageSizeError = false;
            $scope.validateImageSize = function (file, height, width) {
                if (file.height > height || file.width > width) {
                    $scope.imageSizeError = true;
                    $scope.msg = "Image size should be less than " + height + "px X " + width + "px.";
                    $scope.icon = 'info_outline';
                    $scope.styleclass = "c-error";
                    $scope.invalidImage = true;
                    $timeout(function () {
                        $scope.invalidImage = false;
                    }, 5000);
                    return true;
                }
                $scope.imageSizeError = false;
                return false;
            };

            $scope.saveCarouselPicture = function (key, picture) {
                var file = picture;
                var fileName = file.name;
                var validFormats = ['jpg', 'jpeg', 'png', 'gif'];
                var ext = fileName.split('.').pop();

                if (validFormats.indexOf(ext) == -1) {
                    $scope.error = true;
                } else {
                    $scope.error = false;

                    var reader = new FileReader();
                    reader.onload = function (event) {
                        var data = reader.result.split('base64,').pop();

                        var toSend = {
                            "request_body": {
                                "contentKey": key,
                                "contentValue": data,
                                "mimeType": "image/" + ((ext == "jpg") ? "jpeg" : ext)
                            }
                        };

                        apiService.uploadCarouselPicture(toSend)
                            .then(
                                function (response) {
                                    $scope.showSuccessinfoImage = true;
                                },
                                function (error) {
                                    $scope.serverResponse = 'An error has occurred';
                                    console.error("Error,", error);
                                })

                    };
                    reader.readAsDataURL(file);
                }
            }

            $scope.deleteCarouselPicture = function (key) {
                apiService.deleteCarouselPicture(key)
                    .then(
                        function (response) {
                            $scope.showSuccessinfoImage = true;
                        },
                        function (error) {
                            $scope.serverResponse = 'An error has occurred: ' + error;
                        });
            };


            //Get Event Carousel For home screen
            $scope.getEventCarousel = function () {
                apiService
                    .getSiteConfig("event_carousel")
                    .then(
                        function (response) {
                            if (response.data.response_body != null) {
                                $scope.eventConfig = angular.fromJson(response.data.response_body.configValue);
                            }
                        });
            }
            $scope.getEventCarousel();

            $scope.addEventSlide = function () {

                if ((!$scope.eventCarousel.backGround
                    || !$scope.validateImageSize($scope.eventCarousel.backGround, 2560, 524))
                    && (!$scope.eventCarousel.infoGraphic
                        || !$scope.validateImageSize($scope.eventCarousel.infoGraphic, 372, 260))) {
                    //create json
                    var slide = {};
                    var links = {};
                    var carousel = {};


                    if (typeof $scope.eventConfig === "undefined") {
                        var keys = [];
                        var carousel = {};
                        carousel['enabled'] = "true";
                        var uniqueKeys = [];
                    } else {
                        var keys = Object.keys($scope.eventConfig);
                        var enabledIndex = keys.indexOf("enabled");
                        keys.splice(enabledIndex, 1);
                        var carousel = $scope.eventConfig;
                        var uniqueKeys = Object.values($scope.eventConfig).map(function (item) {
                            return item.uniqueKey;
                        });
                    }

                    if (keys.length == undefined || keys.length == 0) {
                        var keyIndex = 0;
                        var uniqueIndex = 0;
                    } else {
                        var keyIndex = parseInt(keys[keys.length - 1]) + 1;
                        var uniqueIndex = uniqueKeys.sort(function (a, b) { return b - a })[0];
                    }
                    var slide_name = $scope.eventCarousel.name;
                    var slide_headline = $scope.eventCarousel.headline;
                    var slide_supportingContent = $scope.eventCarousel.supportingContent;
                    slide['name'] = slide_name;
                    slide['headline'] = slide_headline;
                    slide['supportingContent'] = slide_supportingContent;
                    slide['infoImageAling'] = $scope.carousel_Info_Aling;
                    slide['textAling'] = $scope.carousel_Text_Aling;

                    slide['graphicImgEnabled'] = $scope.eventCarousel.graphicImg;
                    slide['slideEnabled'] = "true";

                    if ($scope.itsEdit) {
                        slide['number'] = $scope.keyval;
                        var keyIndex = $scope.keyval;
                        slide['uniqueKey'] = $scope.eventCarousel.uniqueKey;
                    } else {
                        slide['number'] = keyIndex + 1;
                        slide['uniqueKey'] = uniqueIndex + 1;
                    }

                    slide['bgImageUrl'] = $scope.eventBGFileName;
                    slide['InfoImageUrl'] = $scope.eventInfoFileName;
                    slide['bgImgKey'] = "carousel.event." + slide['uniqueKey'] + ".bgImg";
                    slide['infoImgKey'] = "carousel.event." + slide['uniqueKey'] + ".infoImg";
                    slide['hasInfoGraphic'] = $scope.eventCarousel.hasInfoGraphic || ($scope.eventCarousel.infoGraphic != null);


                    carousel[keyIndex] = slide;
                    var carouselConfigStr = JSON.stringify(carousel);

                    var convertedString = carouselConfigStr.replace(/"/g, '\"');

                    var reqObj = {
                        "request_body": {
                            "configKey": "event_carousel",
                            "configValue": convertedString,
                            "userId": userId
                        }
                    };
                    //if carouselConfig is not present in DB create a record else update the record

                    //typeof thing === "undefined"
                    if (typeof $scope.eventConfig === "undefined") {
                        apiService
                            .createSiteConfig(reqObj)
                            .then(
                                function (response) {
                                    $scope.getEventCarousel();
                                    $scope.msg = "Carousel Updated successfully.";
                                    $scope.icon = '';
                                    $scope.styleclass = 'c-success';
                                    $scope.closePoup();
                                    $scope.showAlertMessage = true;
                                    $scope.itsEdit = false;
                                    delete $scope.keyval;
                                    $timeout(function () {
                                        $scope.showAlertMessage = false;
                                    }, 5000);
                                });
                    } else {
                        apiService
                            .updateSiteConfig("event_carousel", reqObj)
                            .then(
                                function (response) {
                                    $scope.msg = "Carousel Updated successfully.";
                                    $scope.icon = '';
                                    $scope.styleclass = 'c-success';
                                    $scope.getEventCarousel();
                                    $scope.closePoup();
                                    $scope.showAlertMessage = true;
                                    $scope.itsEdit = false;
                                    delete $scope.keyval;
                                    $timeout(function () {
                                        $scope.showAlertMessage = false;
                                    }, 5000);
                                });
                    }

                    if ($scope.eventCarousel.backGround) {
                        // Save event background
                        $scope.saveCarouselPicture(slide['bgImgKey'],
                            $scope.eventCarousel.backGround);
                    }
                    if ($scope.eventCarousel.infoGraphic) {
                        // Save event infographic
                        $scope.saveCarouselPicture(slide['infoImgKey'],
                            $scope.eventCarousel.infoGraphic);
                    }
                }
            }


            $scope.editEventSlide = function (key, val) {
                $scope.itsEdit = true;
                $scope.eventCarousel = val;
                $scope.eventBGFileName = val['bgImageUrl'];
                $scope.eventInfoFileName = val['InfoImageUrl'];

                $scope.event_Text_Aling = val['textAling'];
                $scope.event_Info_Aling = val['infoImageAling'];
                if ($scope.eventSC == "") {
                    $scope.keyval = key;
                    $scope.showEventSlidesPopup();
                }
                else {
                    $scope.eventSC = val['supportingContent'].replace(/<(?:.|\n)*?>/gm, '');
                    $scope.keyval = key;
                    $scope.showEventSlidesPopup();
                }
            }


            $scope.deleteEventSlide = function () {

                var key = $scope.deleteKey;
                $scope.deleteCarouselPicture($scope.eventConfig[key]['bgImgKey']);
                $scope.deleteCarouselPicture($scope.eventConfig[key]['infoImgKey']);
                delete $scope.eventConfig[key];

                var carouselConfigStr = JSON.stringify($scope.eventConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "event_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("event_carousel", reqObj)
                    .then(
                        function (response) {
                            $scope.closePoup();
                            $scope.getEventCarousel();
                            $scope.msg = "Carousel Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
            }

            $scope.changeEventSlideOrder = function (ev) {

                var slideEnabled = $scope.eventConfig['enabled'];
                delete $scope.eventConfig['enabled'];

                $scope.changeOrderfor = parseInt($scope.changeOrderfor, 10);
                $scope.order.changeOrderValue = parseInt($scope.order.changeOrderValue, 10);
                if (isNaN($scope.order.changeOrderValue) || $scope.order.changeOrderValue > Object.keys($scope.eventConfig).length - 1 || $scope.order.changeOrderValue < 0) {
                    $scope.eventSlideOrderError = true;
                    return;
                }
                $scope.eventSlideOrderError = false;
                var updatedCarouselConfig = [];
                for (var i = 0; i < Object.keys($scope.eventConfig).length; i++) {
                    updatedCarouselConfig[i] = $scope.eventConfig[i];
                }

                var tempvalue = updatedCarouselConfig.splice($scope.changeOrderfor, 1);
                updatedCarouselConfig.splice($scope.order.changeOrderValue, 0, tempvalue[0]);

                var toMap = {};
                for (var i = 0; i < updatedCarouselConfig.length; i++) {
                    toMap[i] = updatedCarouselConfig[i];
                }
                $scope.eventConfig = toMap;
                $scope.eventConfig['enabled'] = slideEnabled;


                var carouselConfigStr = JSON.stringify($scope.eventConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "event_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("event_carousel", reqObj)
                    .then(
                        function (response) {
                            $scope.getEventCarousel();
                            $scope.msg = "Carousel Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $scope.closePoup();
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
            }

            $scope.eventCheckedList = [];
            $scope.toggleEventCheckedList = function (key) {

                var index = $scope.eventCheckedList.indexOf(key);
                if (index == -1) {
                    $scope.eventCheckedList.push(key);
                } else {
                    $scope.eventCheckedList.splice(index, 1);
                }
            }

            $scope.changeEventSlides = function () {
                for (var i = 0; i < $scope.eventCheckedList.length; i++) {
                    var key = $scope.eventCheckedList[i];
                    $scope.eventConfig[key].slideEnabled = $scope.changeEventAction;
                }
                //	                       $scope.eventConfig.enabled = !$scope.eventConfig.enabled;
                var carouselConfigStr = JSON.stringify($scope.eventConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "event_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("event_carousel", reqObj)
                    .then(
                        function (response) {
                            if ($scope.eventCheckedList.length >= 1) {
                                $scope.getEventCarousel();
                                $scope.msg = "Carousel Updated successfully.";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeEventAction = "Enable/Disable Slides";
                                $scope.showAlertMessage = true;
                                $timeout(function () {
                                    $scope.showAlertMessage = false;
                                }, 5000);
                            }
                            else {
                                $scope.showWarningMessage = false;
                                $scope.warning = "Please Select any Slides";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeEventAction = "Enable/Disable Slides";
                                $scope.showWarningMessage = true;

                                $timeout(function () {
                                    $scope.showWarningMessage = false;
                                }, 5000);
                            }
                        });

            }

            $scope.disableEventCarousel = function () {
                var carouselConfigStr = JSON.stringify($scope.eventConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "event_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("event_carousel", reqObj)
                    .then(
                        function (response) {
                            if ($scope.eventCheckedList.length >= 1) {
                                $scope.getEventCarousel();
                                $scope.msg = "Carousel Updated successfully.";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeEventAction = "Enable/Disable Slides";
                                $scope.showAlertMessage = true;
                                $timeout(function () {
                                    $scope.showAlertMessage = false;
                                }, 5000);
                            }
                            else {
                                $scope.showWarningMessage = false;
                                $scope.warning = "Please Select any Slides";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeEventAction = "Enable/Disable Slides";
                                $scope.showWarningMessage = true;

                                $timeout(function () {
                                    $scope.showWarningMessage = false;
                                }, 5000);
                            }
                        });
            }

            //Get Story Carousel For home screen
            $scope.getStoryCarousel = function () {
                apiService
                    .getSiteConfig("story_carousel")
                    .then(
                        function (response) {
                            if (response.data.response_body != null) {
                                $scope.storyConfig = angular.fromJson(response.data.response_body.configValue);
                            }
                        });
            }
            $scope.getStoryCarousel();

            $scope.addStorySlide = function () {
                //create json
                var slide = {};
                var links = {};
                var carousel = {};


                if (typeof $scope.storyConfig === "undefined") {
                    var keys = [];
                    var carousel = {};
                    carousel['enabled'] = "true";
                } else {
                    var keys = Object.keys($scope.storyConfig);
                    var enabledIndex = keys.indexOf("enabled");
                    keys.splice(enabledIndex, 1);
                    var carousel = $scope.storyConfig;
                }

                if (keys.length == undefined || keys.length == 0)
                    var keyIndex = 0;
                else
                    var keyIndex = parseInt(keys[keys.length - 1]) + 1;
                //return;
                var slide_name = $scope.successCarousel.name;
                var slide_authorName = $scope.successCarousel.authorName;
                var slide_supportingContent = $scope.successCarousel.supportingContent.trim();
                slide['name'] = slide_name;
                slide['authorName'] = slide_authorName;
                slide['supportingContent'] = slide_supportingContent.trim();
                //							slide['infoImageAling']= $scope.carousel_Info_Aling;
                //							slide['textAling']= $scope.carousel_Text_Aling;

                //							slide['graphicImgEnabled'] =  $scope.successCarousel.graphicImg;
                slide['slideEnabled'] = "true";

                if ($scope.itsEdit) {
                    slide['number'] = $scope.keyval;
                    var keyIndex = $scope.keyval;
                } else {
                    slide['number'] = keyIndex + 1;
                }

                slide['bgImageUrl'] = $scope.successBGFileName;
                slide['InfoImageUrl'] = $scope.successInfoFileName;

                carousel[keyIndex] = slide;
                var carouselConfigStr = JSON.stringify(carousel);

                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "story_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                //if carouselConfig is not present in DB create a record else update the record

                //typeof thing === "undefined"
                if (typeof $scope.storyConfig === "undefined") {
                    apiService
                        .createSiteConfig(reqObj)
                        .then(
                            function (response) {
                                $scope.getStoryCarousel();
                                $scope.msg = "Carousel Updated successfully.";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.closePoup();
                                $scope.showAlertMessage = true;
                                $scope.itsEdit = false;
                                delete $scope.keyval;
                                $timeout(function () {
                                    $scope.showAlertMessage = false;
                                }, 5000);
                            });
                } else {
                    apiService
                        .updateSiteConfig("story_carousel", reqObj)
                        .then(
                            function (response) {
                                $scope.msg = "Carousel Updated successfully.";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.getStoryCarousel();
                                $scope.closePoup();
                                $scope.showAlertMessage = true;
                                $scope.itsEdit = false;
                                delete $scope.keyval;
                                $timeout(function () {
                                    $scope.showAlertMessage = false;
                                }, 5000);
                            });
                }
            }


            $scope.changeStorySlideOrder = function (ev) {

                var slideEnabled = $scope.storyConfig['enabled'];
                delete $scope.storyConfig['enabled'];

                $scope.changeOrderfor = parseInt($scope.changeOrderfor, 10);
                $scope.order.changeOrderValue = parseInt($scope.order.changeOrderValue, 10);
                if (isNaN($scope.order.changeOrderValue) || $scope.order.changeOrderValue > Object.keys($scope.storyConfig).length - 1 || $scope.order.changeOrderValue < 0) {
                    $scope.storySlideOrderError = true;
                    return;
                }
                $scope.storySlideOrderError = false;
                var updatedCarouselConfig = [];
                for (var i = 0; i < Object.keys($scope.storyConfig).length; i++) {
                    updatedCarouselConfig[i] = $scope.storyConfig[i];
                }

                var tempvalue = updatedCarouselConfig.splice($scope.changeOrderfor, 1);
                updatedCarouselConfig.splice($scope.order.changeOrderValue, 0, tempvalue[0]);

                var toMap = {};
                for (var i = 0; i < updatedCarouselConfig.length; i++) {
                    toMap[i] = updatedCarouselConfig[i];
                }
                $scope.storyConfig = toMap;
                $scope.storyConfig['enabled'] = slideEnabled;


                var carouselConfigStr = JSON.stringify($scope.storyConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "story_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("story_carousel", reqObj)
                    .then(
                        function (response) {
                            $scope.getEventCarousel();
                            $scope.msg = "Carousel Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.closePoup();
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
            }

            $scope.editStorySlide = function (key, val) {
                $scope.itsEdit = true;
                $scope.successCarousel = val;
                //	                 	   $scope.successBGFileName = val['bgImageUrl'];
                //	                 	   $scope.successInfoFileName = val['InfoImageUrl'];
                //
                //	                 	   $scope.event_Text_Aling = val['textAling'];
                //	                 	   $scope.event_Info_Aling = val['infoImageAling'];

                $scope.successSC = val['supportingContent'].replace(/<(?:.|\n)*?>/gm, '');

                $scope.keyval = key;
                $scope.showStorySlidesPopup();
            }


            $scope.deleteStorySlide = function () {

                var key = $scope.deleteKey;
                delete $scope.storyConfig[key];

                var carouselConfigStr = JSON.stringify($scope.storyConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "story_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("story_carousel", reqObj)
                    .then(
                        function (response) {
                            $scope.closePoup();
                            $scope.getStoryCarousel();
                            $scope.msg = "Carousel Updated successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 5000);
                        });
            }

            $scope.storyCheckedList = [];
            $scope.toggleStoryCheckedList = function (key) {

                var index = $scope.storyCheckedList.indexOf(key);
                if (index == -1) {
                    $scope.storyCheckedList.push(key);
                } else {
                    $scope.storyCheckedList.splice(index, 1);
                }
            }

            $scope.changeStorySlides = function () {
                for (var i = 0; i < $scope.storyCheckedList.length; i++) {
                    var key = $scope.storyCheckedList[i];
                    $scope.storyConfig[key].slideEnabled = $scope.changeStoryAction;
                }
                //	                 	   $scope.storyConfig.enabled = !$scope.storyConfig.enabled;

                var carouselConfigStr = JSON.stringify($scope.storyConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "story_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("story_carousel", reqObj)
                    .then(
                        function (response) {
                            if ($scope.storyCheckedList.length >= 1) {
                                $scope.getStoryCarousel();
                                $scope.msg = "Carousel Updated successfully.";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeStoryAction = "Enable/Disable Slides";
                                $scope.showAlertMessage = true;
                                $timeout(function () {
                                    $scope.showAlertMessage = false;
                                }, 5000);
                            }
                            else {
                                $scope.showWarningMessage = false;
                                $scope.warning = "Please Select any Slides";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeStoryAction = "Enable/Disable Slides";
                                $scope.showWarningMessage = true;

                                $timeout(function () {
                                    $scope.showWarningMessage = false;
                                }, 5000);
                            }
                        });

            }

            $scope.disableStoryCarousel = function () {
                var carouselConfigStr = JSON.stringify($scope.storyConfig);
                var convertedString = carouselConfigStr.replace(/"/g, '\"');

                var reqObj = {
                    "request_body": {
                        "configKey": "story_carousel",
                        "configValue": convertedString,
                        "userId": userId
                    }
                };
                apiService.updateSiteConfig("story_carousel", reqObj)
                    .then(
                        function (response) {
                            if ($scope.storyCheckedList.length >= 1) {
                                $scope.getStoryCarousel();
                                $scope.msg = "Carousel Updated successfully.";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeStoryAction = "Enable/Disable Slides";
                                $scope.showAlertMessage = true;
                                $timeout(function () {
                                    $scope.showAlertMessage = false;
                                }, 5000);
                            }
                            else {
                                $scope.showWarningMessage = false;
                                $scope.warning = "Please Select any Slides";
                                $scope.icon = '';
                                $scope.styleclass = 'c-success';
                                $scope.changeStoryAction = "Enable/Disable Slides";
                                $scope.showWarningMessage = true;

                                $timeout(function () {
                                    $scope.showWarningMessage = false;
                                }, 5000);
                            }
                        });
            }

            /* IOT changes start */
            $scope.loadAllTags = function (query) {
                $scope.getTags = {
                    "request_body": {
                        "page": 0
                    }
                }
                return apiService
                    .getAllTag($scope.getTags)
                    .then(
                        function (response) {
                            $scope.status = response.data.response_detail;
                            $scope.allTags = response.data.response_body.tags;
                            $scope.allTags.splice(0, 0, "");

                        },
                        function (error) {
                            $scope.status = error.data.error;
                        });
            }
            $scope.loadAllTags();

            /* IOT changes end*/

            /* Catalog changes */
            $scope.catalogList = function (peerID) {
            	$scope.listCatalog = "";
            	$scope.catalogError = false;
                apiService
                    .gatewayListCatalog(peerID)
                    .then(
                        function successCallback(response) {
                            $scope.listCatalog = response.data.response_body;
                            $scope.CatalogValue = "";

                            //$scope.allCatalogList = [];

                            if($scope.listCatalog.length < 1){
                            	$scope.catalogError = true;
                            }
                        },
                        function errorCallback(error) {
                        	$scope.CatalogValue = "";
                           // $scope.allCatalogList = [];
                            $scope.listCatalog = "";
                            $scope.catalogError = true;
                        });
            }

            $scope.getCatalogDetails = function (CatalogId) {
              //  $scope.allCatalogList = [];
                $scope.selectedCatalogId = CatalogId;

                //Filtering now on List of catalogs with selected CatalogId

              if($scope.listCatalog.length>0){
                Object.values($scope.listCatalog).forEach(function(value, index) {
                    if (value.catalogId == $scope.selectedCatalogId) {
                        $scope.CatalogSendDeatils = value;

                    }
                });
            }
                var reqObj = {
                		"peerId": $scope.peerIdForSubsList,
                		"selector": "{\"catalogId\":\"" + CatalogId + "\"}"
                	};
             // Removed Functionality   since it is not working
               /* apiService
                    .getPeerSolutionsByCatalog(reqObj)
                    .then(
                        function successCallback(response) {
                            $scope.allCatalogList = response.data.response_body;
                        },
                        function errorCallback(response) {
                        });*/
            }

            /* Catalog changes */


            /************Maintained Backup Logs Methods***********/
            //call all get methods when the left hand side tab is clicked and not on page load, to decrease the wait time of overall page load
            //will call only when needed
            $scope.getMaintainedBackupLogs = function(){
            	$scope.getAllSnapshot();
				$scope.getAllArchives();

            }

            $scope.orderSnapshots='backupName';
            $scope.reverseSortbackup = true;
            $scope.disableSnapshotButton = true;
            $scope.disableArchiveButton = true;

            $scope.getAllSnapshot = function () {
                $scope.snapshots = [];
                $scope.showContentLoader = true;
                apiService.getAllSnapshot()
                    .then(
                        function (response) {
                              $scope.showContentLoader = false;
                              //snapshots with snapshots clubbed in same repo.
                              $scope.allRepoSnapshots =  response.data.response_body.elasticsearchSnapshots;

                            //snapshots with snapshots separate.
                              var allRepo =  response.data.response_body.elasticsearchSnapshots;
                              for(var i=0; i<allRepo.length;i++){
                                    if(allRepo[i].snapshots.length > 0){
                                        angular.forEach(allRepo[i].snapshots, function (value, key) {
                                        	  var snapshot = {};
                                        	  snapshot['repositoryName'] = allRepo[i].repositoryName;
                                              snapshot['backupName'] = value.snapShotId;
                                              snapshot['createdDate'] = value.startTime;
                                              $scope.snapshots.push(snapshot);
                                        });
                                    }
                              }
                              $scope.loadBackups(0);
                        },
                        function (error) {
                              $scope.showContentLoader = false;
                              console.log(error);
                        });
            };

            $scope.loadBackups = function (pageNumber) {
            	$scope.selectedPage = pageNumber;
				if($scope.snapshots){
					$scope.totalPages = Math.ceil($scope.snapshots.length / $scope.requestResultSize);
					$scope.allSnapshots = ($scope.snapshots).slice($scope.requestResultSize*pageNumber,
            			($scope.requestResultSize*pageNumber)+$scope.requestResultSize);
				}
            }



            $scope.checkedSnapshot = false;

            $scope.checkAllSnapshot = function(selected){
            	$scope.selectAllSnapshotStatus = true;
        		for (var i = 0; i < $scope.allRepoSnapshots.length; i++) {
           	        $scope.allRepoSnapshots[i].checked = selected;
           	        $scope.disableSnapshotButton=!selected;
        		}
            };

            $scope.removeSelectAllSnapshot = function(selected){
      		   $scope.selectAllSnapshotStatus = false;
      		   $scope.disableSnapshotButton = ($scope.allRepoSnapshots.filter(snapshot =>snapshot.checked).length) == 0;
            };
            
            //pagination
            $scope.pageNumber = 0;
			$scope.totalPages = 0;
			$scope.requestResultSize = 10;
			$scope.setPageStart = 0;
            $scope.selectedPage = 0;
            $scope.setStartCount = function(val){
                  if(val == "preBunch"){$scope.setPageStart = $scope.setPageStart-5}
                  if(val == "nextBunch"){$scope.setPageStart = $scope.setPageStart+5}
                  if(val == "pre"){
                        if($scope.selectedPage == $scope.setPageStart)
                        {
                        	$scope.setPageStart = $scope.setPageStart - 1;
                        	$scope.selectedPage = $scope.selectedPage - 1;
                        }
                        else
                        	$scope.selectedPage = $scope.selectedPage - 1;
                        }
                  if(val == "next"){
                        if($scope.selectedPage == $scope.setPageStart + 4)
                              {
                              	$scope.setPageStart = $scope.setPageStart + 1;
                              	$scope.selectedPage = $scope.selectedPage + 1;
                              }
                        else
                        	$scope.selectedPage = $scope.selectedPage + 1;
                        }
            }
            $scope.filterChange = function(pagination, size) {
            	$scope.requestResultSize = size;
            	$scope.loadBackups(0);
				$scope.loadArchives(0);
            }

			/********************Archive methods*************/
            //$scope.orderArchives='backupName';
            $scope.reverseSortarchive = true;

            $scope.getAllArchives = function () {
            	$scope.allArchives = [];
            	$scope.archives = [];
                $scope.showContentLoader = true;
                apiService.getAllArchives()
                    .then(
                        function (response) {
                        	$scope.showContentLoader = false;
                        	//archives with snapshots clubbed in same repo.
                        	$scope.allRepoArchives = response.data.response_body.archiveInfo;

                        	//archives with snapshot separate 
                        	var allArchives = response.data.response_body.archiveInfo;
                            for(var i = 0; i < allArchives.length; i++){
                                  if(allArchives[i].snapshots.length > 0){
                                      angular.forEach(allArchives[i].snapshots, function (value, key) {
                                      	  var archives = {};
                                      	  archives['repositoryName'] = allArchives[i].repositoryName;
                                      	  archives['backupName'] = value.snapShotId;
                                      	  archives['createdDate'] = value.startTime;
                                          $scope.archives.push(archives);
                                      });
                                  }
                            }

                        	$scope.loadArchives(0);
                        },
                        function (error) {
                             console.log(error);
                             $scope.showContentLoader = false;
                        });
            };

            $scope.loadArchives = function (pageNumber) {
            	$scope.selectedPage = pageNumber;
				if($scope.archives){
					$scope.totalPages = Math.ceil($scope.archives.length / $scope.requestResultSize);
					$scope.allArchives = ($scope.archives).slice($scope.requestResultSize*pageNumber, 
            			($scope.requestResultSize*pageNumber)+$scope.requestResultSize);
				}
            }
            
            $scope.checkAllArchives = function(selected){
            	$scope.selectAllArchiveStatus = true;
        		for (var i = 0; i < $scope.allRepoArchives.length; i++) {
           	        $scope.allRepoArchives[i].checked = selected;
           	        $scope.disableArchiveButton=!selected;
        		}
            };

            $scope.removeSelectAllArchives = function(){
            	$scope.selectAllArchiveStatus = false;
      			$scope.disableArchiveButton = ($scope.allRepoArchives.filter(archive =>archive.checked).length) == 0;
            };

			$scope.confirmCreateRestoreArchive = function (action, repositoryName, selectAll) {
            	$scope.archiveRepoName = [];
                $scope.archiveAction = action;
                if(selectAll == true){
                	for(var i = 0; i < repositoryName.length; i++){
                		if (repositoryName[i].checked){
                			if(repositoryName[i].repositoryName){
                				$scope.archiveRepoName.push(repositoryName[i].repositoryName);
                			}else{
                				$scope.archiveRepoName.push(repositoryName[i].backUpName);
                			}
                		}

                	}
                	$scope.removeSelectAllArchives();

                }else{
                	$scope.archiveRepoName.push(repositoryName);
                }


                $mdDialog.show({
                    contentElement: '#archiveRestoreModal',
                    parent: angular.element(document.body),
                    clickOutsideToClose: true
                });
            }
            $scope.createRestoreArchive = function(action, repositoryName){
				$scope.closePoup();
                var reqBody = {
                    "request_body": {
                    	"action": action,
                        "repositoryName": repositoryName
                    }
                }
                return apiService
                    .createRestoreArchive(reqBody)
                    .then(
                        function (response) {
                        	$scope.checkedArchive = false;
                        	$scope.checkedSnapshot = false;
                            $scope.responseMessage = response;
                            $location.hash('myDialog');
                            $anchorScroll();
                            $scope.msg = response.data.response_body.msg;
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                            	$scope.showAlertMessage = false;
                            }, 3000);
                            $scope.getMaintainedBackupLogs();
                        },
                        function (error) {
                            $location.hash('myDialog');
                            $anchorScroll();
                            $scope.msg = error.data.response_detail;
                            $scope.icon = 'info_outline';
                            $scope.styleclass = 'c-error';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                            	$scope.showAlertMessage = false;
                            }, 3000);
                            $scope.getMaintainedBackupLogs();
                        });
            };


            $scope.deleteSnapshot = function(){
                var reqBody = {
                    "request_body": {
                    	"action": action,
                        "repositoryName": repositoryName
                    }
                }
                return apiService
                    .deleteSnapshot(reqBody)
                    .then(
                        function (response) {
                        	$scope.responseMessage = response;
                            $location.hash('myDialog');
                            $anchorScroll();
                            $scope.msg = "Repository deleted successfully.";
                            $scope.icon = '';
                            $scope.styleclass = 'c-success';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                            	$scope.showAlertMessage = true;
                            }, 3000);
                            $scope.getMaintainedBackupLogs();


                        },
                        function (error) {
                            var error = error.data.message;
                            $location.hash('myDialog');
                            $anchorScroll();
                            $scope.msg = "Error Occurred while deleting repository.";
                            $scope.icon = 'info_outline';
                            $scope.styleclass = 'c-error';
                            $scope.showAlertMessage = true;
                            $timeout(function () {
                                $scope.showAlertMessage = false;
                            }, 3000);
                            $scope.fetchAllRepositories();
                        });

            }



            /******** Repository Methods **************/

            $scope.createFirstRepo = false;
            $scope.createBackup = function(ev) {
            	var _isRepoAvailable = $scope.isRepoAvailable;
            	var _createFirstRepo = $scope.createFirstRepo;
                var parentScope = $scope;
                $scope.parentDialog = $mdDialog.show({
                 locals: {isRepoAvailable: _isRepoAvailable, createFirstRepo: _createFirstRepo, parentScope:parentScope},
                 templateUrl: '../app/Admin/create-backup.template.html',
                 parent: angular.element(document.body),
                 targetEvent: ev,
                 clickOutsideToClose:true,
                 controller : function DialogController( $scope, parentScope, isRepoAvailable, createFirstRepo) {
                	 $scope.parent = parentScope;
                	 $scope.isRepoAvailable = isRepoAvailable;
                	 $scope.createFirstRepo = createFirstRepo;
                	 $scope.selectAllIndice = false;
                	 $scope.repositoryStatus=false;
						$scope.closePoup = function(){
							$mdDialog.hide();
	                    };

						$scope.fetchAllRepositories = function () {
			                $scope.allRepositories = [];
			                $scope.showContentLoader = true;
			                apiService.getAllRepositories()
			                    .then(
			                        function (response) {
			                            if(response.data.response_body.repositories){
			                                $scope.allRepositories =  response.data.response_body.repositories;
			                                $scope.isRepoAvailable = true;
			                            }else{
			                                $scope.isRepoAvailable = false;
			                            }
			                            $scope.showContentLoader = false;
			                        },
			                        function (error) {
			                        	console.log(error);
			                        	$scope.showContentLoader = false;
			                        	$scope.isRepoAvailable = false;
			                        });
			            };
			            $scope.fetchAllRepositories();

			            $scope.fetchAllIndices = function () {
			                $scope.allIndices = [];
			                $scope.showContentLoader = true;
			                apiService.getAllIndices()
			                    .then(
			                        function (response) {
			                            if(response.data.response_body.indices){
			                                angular.forEach(response.data.response_body.indices, function (value, key) {
			                                	$scope.allIndices.push({
			                                				"name": value,
			                                				"checked": false
			                                				});
			                                });

			                                $scope.isRepoAvailable = true;
			                            }else{
			                                $scope.isRepoAvailable = false;
			                            }
			                            $scope.showContentLoader = false;
			                        },
			                        function (error) {
			                        	console.log(error);
			                        	$scope.showContentLoader = false;
			                        });
			            };
			            $scope.fetchAllIndices();

			            $scope.selectAllIndices = function(selected){
	                		for (var i = 0; i < $scope.allIndices.length; i++) {
	                   	        $scope.allIndices[i].checked = !selected;
	                		}
                        };

                        $scope.removeSelectAll = function(){
                 		   $scope.selectAllStatus = false;
                 		   var selectedCount = 0;
                 		   angular.forEach($scope.allIndices, function (value, key) {
                 			   if (value.checked == true) {
                 				  selectedCount++
                 			   }
                 		   });
                 		   return selectedCount;
                        };

                        $scope.createRepository = function(repositoryName){

                        		$scope.selectRepository = true;
                        		var reqBody = {
                                "request_body": {
                                	"nodeTimeout": '1m',
                                    "repositoryName": repositoryName
                                }
                        		}
                            return apiService
                                .createRepositories(reqBody)
                                .then(
                                    function (response) {
                                    	$scope.repositoryStatus=true;
                                    	$scope.msg = "Repository created successfully.";
                                        $scope.icon = '';
                                        $scope.styleclass = true;
                                       $timeout(function () {
                                            $scope.repositoryStatus = false;
                                        }, 3000);
                                        //createRepo.reset();
                                        $scope.fetchAllRepositories();
                                      },

                                    function (error) {
                                    	$scope.repositoryStatus=true;
                                        var error = error.data;
                                        $location.hash('backupLogs'); 
                                        $anchorScroll();
                                        $scope.msg = "Error Occurred while creating repository.";
                                        $scope.icon = 'info_outline';
                                        $scope.styleclass = false;
                                        $timeout(function () {
                                            $scope.repositoryStatus = false;
                                        }, 3000);
                                        //createRepo.reset();
                                        $scope.fetchAllRepositories();
                                    });


                        };
                        $scope.elements = [];


                        $scope.createBackup = function(){
                        	$scope.selectedRepoName = $scope.selectRepo;
                        	$scope.allIndices;
                        	$scope.reqBodyIndice = [];

                        	//check which all indices are checked
                        	angular.forEach($scope.allIndices, function (value, key) {
                                if (value.checked == true) {
                                	$scope.reqBodyIndice.push(value.name);
                                }
                            });

                            var reqBody = {
                            		  "request_body": {
                            			    "createSnapshots": [
                            			      {
                            			        "indices": $scope.reqBodyIndice,
                            			        "repositoryName": $scope.selectedRepoName,
                            			        "snapshotName" : $scope.backupName
                            			      }
                            			    ],
                            			    "nodeTimeout": 1
                            			  }
                            			};
                            return apiService
                                .createSnapshot(reqBody)
                                .then(
                                    function (response) {
                                    	$scope.closePoup();
                                    	$scope.parent.backupStatus=true;
                                    	$scope.selectAllIndices(true);
                                    	$scope.parent.msg = "Created Backup successfully.";
                                        $scope.parent.icon = '';
                                        $scope.parent.styleclass = true;
                                        $timeout(function () {
                                            $scope.parent.backupStatus = false;
                                        }, 3000);
                                        backupForm.reset();
                                        $scope.selectRepo=" ";
                                        $scope.parent.getAllSnapshot();
                                        $scope.fetchAllRepositories();
                                    },
                                    function (error) {
                                    	$scope.closePoup();
                                    	$scope.parent.backupStatus=true;
                                        var error = error.data;
                                        $location.hash('myDialog');
                                        $anchorScroll();
                                        $scope.parent.msg = "Error Occurred while creating backup.";
                                        $scope.parent.icon = 'info_outline';
                                        $scope.parent.styleclass = false;

                                        $timeout(function () {
                                        	$scope.parent.backupStatus = false;
                                        }, 3000);
                                        backupForm.reset();
                                        $scope.selectRepo=" ";
                                        $scope.parent.getAllSnapshot();
                                        $scope.fetchAllRepositories();
	
                                    });
                        };

                        //Delete Indices
						$scope.confirmDeleteIndice = function(indiceName){

                            $scope.indiceName = indiceName;
                            $mdDialog.show({
                                contentElement: '#deleteIndex',
                                parent:  angular.element(document.body),
                                targetEvent: ev,
                                clickOutsideToClose: true
                            }).then(function(flag){
                            	if(flag){
                            		$scope.deleteIndices();
                            	} else {
                            		$scope.parent.createBackup();
                            	}
                            });
                        }

                        $scope.deleteIndices = function(indiceName){
                        	var deleteIndiceName = indiceName;
                        	var reqBody = {
                          		  "request_body": {
                          			        "indices": [$scope.indiceName]
                          			  }
                          			};
                          return apiService
                              .deleteIndices(reqBody)
                              .then(
                                  function (response) {
                                     $location.hash('backupLogs');
                                      $anchorScroll();
                                      $scope.parent.msg = response.data.response_body.message;
                                      $scope.parent.icon = '';
                                      $scope.parent.styleclass = 'c-success';
                                      $scope.parent.showAlertMessage = true;
                                      $timeout(function () {
                                          $scope.parent.showAlertMessage = false;
                                      }, 3000);
                                  },
                                  function (error) {
                                      $scope.status = error.data;
                                      console.log($scope.status);
                                      $location.hash('backupLogs');
                                      $anchorScroll();
                                      $scope.parent.msg = 'Error Occurred while deleting Indices.';
                                      $scope.parent.icon = 'info_outline';
                                      $scope.parent.styleclass = 'c-error';
                                      $scope.parent.showAlertMessage = true;
                                      $timeout(function () {
                                          $scope.parent.showAlertMessage = false;
                                      }, 3000);
                                      $scope.fetchAllIndices();

                                  });
                        }


					}
                })

            };

			$scope.closeIndice = function(flag){
				$mdDialog.hide(flag);
			}


            /************Maintained Backup Logs Methods***********/

			/*** Role management**/


			$scope.selectedCatalogList = [];
			$scope.toggleCatalogSelection = function(catalogid){

				var index = $scope.selectedCatalogList.indexOf(catalogid);
			    if (index > -1) {
			      $scope.selectedCatalogList.splice(index, 1);
			    } else {
			      $scope.selectedCatalogList.push(catalogid);
			    }

				if($scope.catalogIdsList.length == $scope.selectedCatalogList.length){
					$scope.selectAllCatalogs = {"checked":true};
                } else {
                	$scope.selectAllCatalogs = {"checked":false};
                }


			}

			 $scope.checkAllRoles = function(selected){
				 var allcatalogs = Object.keys($scope.allCatalogList);
				 $scope.selectedCatalogTempList = []
				 if(selected){
     	        	for (var i = 0; i < allcatalogs.length ; i++) {
	        		     for (var j = 0; j < $scope.allCatalogList[allcatalogs[i]].length; j++) {
	           	          $scope.allCatalogList[allcatalogs[i]][j].checked = selected;
	           	          $scope.selectedCatalogTempList.push(
	           	              $scope.allCatalogList[allcatalogs[i]][j].catalogId);  
	        		     }
	        		    $scope.selectedCatalogList = angular.copy($scope.selectedCatalogTempList);

	        	
     	            }
     	        }
    	        else {
    	        	$scope.selectedCatalogList = [];
    	        }
				 for (var i = 0; i < allcatalogs.length ; i++) {
	        		for (var j = 0; j < $scope.allCatalogList[allcatalogs[i]].length; j++) {
	           	        $scope.allCatalogList[allcatalogs[i]][j].checked = selected;
	        		}
	        	}
			 }

			// Deployment Automation starts
			 $scope.submitDeployment = function(){
				 var deployementObject = {};
				 deployementObject['jsr'] = $scope.jenkinsServer;
				 deployementObject['jjb'] = $scope.jenkinsJob;
				 deployementObject['param'] = $scope.jenkinsName;
				 //deployementObject['paramValue'] = $scope.jenkinsValue;
				 deployementObject['jlog'] = $scope.jenkinsLogin;
				 deployementObject['jst'] = $scope.jenkinsToken;

				 var deployementObjectStrTemp = JSON.stringify(deployementObject);
				 var deployementObjectStr = deployementObjectStrTemp.replace(/"/g, '\"');

				 var reqObj = {
	                        "request_body": {
	                            "configKey": "deployment_jenkins_config",
	                            "configValue": deployementObjectStr,
	                            "userId": userId
	                        }
	                    };
				 console.log("reqObj: ", reqObj);

				 if (typeof $scope.deployment_jenkins_config === "undefined") {
                     apiService
                         .createSiteConfig(reqObj)
                         .then(
                             function (response) {
                                 $scope.msg = "Deployment Automation Updated successfully.";
                                 $scope.icon = '';
                                 $scope.styleclass = 'c-success';
                                 $scope.showAlertMessage = true;
                                 $timeout(function () {
                                     $scope.showAlertMessage = false;
                                 }, 5000);
                             });
                 } else {
                     apiService
                         .updateSiteConfig("deployment_jenkins_config", reqObj)
                         .then(
                             function (response) {
                                 $scope.msg = "Deployment Automation Updated successfully.";
                                 $scope.icon = '';
                                 $scope.styleclass = 'c-success';
                                 $scope.showAlertMessage = true;
                                 $timeout(function () {
                                     $scope.showAlertMessage = false;
                                 }, 5000);
                             });
                 }
			 };
			 // Deployment Automation ends

        }
    })
    .service('fileUploadService', function ($http, $q) {

        this.uploadFileToUrl = function (file, uploadUrl) {
            // FormData, object of key/value pair for form fields and values
            var fileFormData = new FormData();
            fileFormData.append('file', file);

            var deffered = $q.defer();
            $http.post(uploadUrl, fileFormData, {
                transformRequest: angular.identity,
                headers: {
                    'Content-Type': undefined
                }

            }).success(function (response) {
                deffered.resolve(response);

            }).error(function (response) {
                deffered.reject(response);
            });

            return deffered.promise;
        }
    });


//for search solution : addtosubs
//for category and toolkittype : addedAllToSubs
