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

app
		.factory(
				'authenticationInterceptor',
				function($q, $state, $rootScope, $injector,
						browserStorageService) {
					$rootScope.accessError = false;
					return {
						request : function(config) {
							config.headers = config.headers;
							if (browserStorageService.getAuthToken()) {
								config.headers.Authorization = 'Bearer '
										+ browserStorageService.getAuthToken();
							}
							if (sessionStorage.getItem('provider'))
								config.headers.provider = sessionStorage
										.getItem('provider');
							var tz = jstz.determine().name();
							tz = encodeURIComponent(tz);
							config.headers.UserTimeZone = tz;
							var requestId = uuid();
							config.headers["Request-ID"] = requestId;
							// In case if other component needs with different
							// name. this should be made consisent
							config.headers["X-ACUMOS-Request-Id"] = requestId;

							return config;
						},
						response : function(response) {
							return response || $q.when(response);
						},
						responseError : function(response) {

							if (response.status === 401
									&& $rootScope.accessError == false/*
																		 * &&
																		 * response.config.url !=
																		 * 'api/admin/config/site_config'
																		 */) {
								// session token expired or unauthorized access
								$rootScope.accessError = true;
								modalService = $injector.get('$mdDialog');
								modalService
										.show({
											templateUrl : '../app/header/sign-in-promt-modal-box.html',
											clickOutsideToClose : true,
											controller : function DialogController(
													$scope) {
												$scope.closeDialog = function() {
													modalService.hide();
													$rootScope
															.$broadcast("MyLogOutEvent");
												}
											}
										});

								return $q.reject(response);

							} else if (response.status === 403) {
								$state.go('403Error');
								return $q.reject(response);
							}

							else if (response.status == -1
									&& (response.config.url && (response.config.url)
											.split(".").pop() == "html")) {
								modalService = $injector.get('$mdDialog');
								if ($rootScope.showNetworkError) {
									modalService
											.show({
												template : '<div id="myDialog"><md-dialog aria-label="Network Error" class="sign-in-promt-modal-box"><md-toolbar><div class="md-toolbar-tools"><h2>No Internet</h2><span flex></span><md-button class="md-icon-button" ng-click="closeDialog()"><i class="material-icons">close</i></md-button></div></md-toolbar><md-dialog-content><div class="md-dialog-content"><p>Connection failure error. Please check your network and try again.</p></div></md-dialog-content><md-dialog-actions><div class="dialog-footer-container1"><span></span><md-button type="submit" class="mdl-button mdl-js-button btn-primary" title="Confirm" ng-click="closeDialog()">OK</md-button></div></md-dialog-actions></md-dialog></div>',
												clickOutsideToClose : false,
												controller : function DialogController(
														$scope) {
													$scope.closeDialog = function() {
														modalService.hide();
													}
												}
											});
									$rootScope.showNetworkError = false;
								}
								return $q.reject(response);
							} else {
								return $q.reject(response);
							}

						}
					};

					function uuid() {
						var id = '', i;
						for (i = 0; i < 36; i++) {
							if (i === 14) {
								id += '4';
							} else if (i === 19) {
								id += '89ab'.charAt(Math.random() * 4);
							} else if (i === 8 || i === 13 || i === 18
									|| i === 23) {
								id += '-';
							} else {
								id += '0123456789abcdef'
										.charAt(Math.random() * 16);
							}
						}
						return id;
					}

				});