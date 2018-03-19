'use strict';
angular
		.module('modelDetails', [ 'jkAngularRatingStars' ])
		.component(
				'modelDetails',
				{

					templateUrl : './app/model-details/md-model-details.template.html',
					controller : function($scope, $location, $http, $rootScope,
							$stateParams, $sessionStorage, $localStorage,
							$mdDialog, $state, $window, apiService, $anchorScroll, $timeout, $document, $sce) {
						/* 
						 * if ($stateParams == null) { $localStorage.solutionId =
						 * $scope.solutionId; } else { $localStorage.solutionId =
						 * $stateParams.solutionId; }
						 */
						// $state.reload();
						/*
						 * if($rootScope.load == true){
						 * $window.location.reload(); $rootScope.load = false; }
						 */
						//API for rating the model start
						$scope.showPBDescription = false;
						$scope.showORDescription = true;
						
						$scope.clearForm = function(){
							deploy.reset();
							deployCloud.brokerlink.value = "";
							$scope.vmName = "";
						}
						
						var user= JSON.parse(localStorage.getItem("userDetail"));
						$scope.userDetailsLogged = user;
						
						$scope.showAlertMessage = false;
						
						$scope.getAllRatings = function(){
							var dataObjRating = {
							  "request_body": {
								    "page": 0,
								    "size": 0
									}
							};

							var req = {
								    method: 'POST',
								    url: 'api/solution/getRating/'+ $stateParams.solutionId,
								    data : dataObjRating
								};
							$http(req)
							.success(function(data, status, headers,config) {
								 
								$scope.allUserRatings = data.response_body.content;
								$scope.ratingCount1 = 0;
								$scope.ratingCount2 = 0;
								$scope.ratingCount3 = 0;
								$scope.ratingCount4 = 0;
								$scope.ratingCount5 = 0;
								$scope.totalRatingsCount = 0;
								
								angular.forEach($scope.allUserRatings, function(value, key) {									
                    				if(value.rating == 1){
                    					$scope.ratingCount1 = $scope.ratingCount1+1
                    				}else if(value.rating == 2){
                    					$scope.ratingCount2 = $scope.ratingCount2+1
                    				} else if(value.rating == 3){
                    					$scope.ratingCount3 = $scope.ratingCount3+1
                    				}else if(value.rating == 4){
                    					$scope.ratingCount4 = $scope.ratingCount4+1
                    				}else if(value.rating == 5){
                    					$scope.ratingCount5 = $scope.ratingCount5+1
                    				}
                    			});
								
								$scope.totalRatingsCount = $scope.ratingCount1 + $scope.ratingCount2 + $scope.ratingCount3 + $scope.ratingCount4 + $scope.ratingCount5;
							}).error(function(data, status, headers, config) {
								
							});
						}
													
						$scope.getAllRatings();
						
						$scope.getAverageRatings = function(){
							$stateParams.solutionId
							
									var req = {

										    method: 'GET',
										    url: 'api/solution/avgRating/'+ $stateParams.solutionId,
										};
									$http(req)
									.success(function(data, status, headers,config) {
										 
										$scope.averageRatings = data.response_body;
										
									}).error(function(data, status, headers, config) {
										console.warn("Error: ",data);
									});
						}
						$scope.getAverageRatings();
						
						
						$scope.revisionId;
						
						$scope.onItemRating = function(rating){
							
							var dataObjRating = {
								"request_body" : {
									"solutionId" : $stateParams.solutionId,
									"rating" : rating,
									"userId" : user[1],
									"textReview": $scope.ratingReview
								},
								  "request_from" : "string",
								  "request_id" : "string"
							}
							
							/*apiService
							.createRatingSolution(dataObjRating)
							.then(
									function(response) {
										console.log("Rating response : " + angular.toJson(response))
										$scope.createRating = response.data.response_body.content;
									},
									function(error) {
										console.log(error);
									});*/
							
							 
							if(!$scope.mlSolutionGetRating.content || $scope.mlSolutionGetRating.content[0].rating == 0)
								{
								//create a new rating for the model. User rates the solution first time.
									apiService
									.createRatingSolution(dataObjRating)
									.then(
											function(response) {
												console.log("Rating response : " + angular.toJson(response));
												/************* if else commented since ratings doens't responsd with correct status at offshore **********/
												if(response.status == 200 || response.status == 201){
													$scope.getSolutionratings();
													$location.hash('md-model-detail-template');  // id of a container on the top of the page - where to scroll (top)
													$anchorScroll(); 							// used to scroll to the id 
													$scope.msg = "You've rated this model successfully. "; 
													$scope.icon = 'report_problem';
													$scope.styleclass = 'c-success';
													$scope.showAlertMessage = true;
													$timeout(function() {
														$scope.showAlertMessage = false;
													}, 2500);
												}else{
													$scope.getSolutionratings();
													$location.hash('md-model-detail-template');
													$anchorScroll();
													$scope.msg = "Unexpected Error occurred.";
													$scope.icon = 'report_problem';
													$scope.styleclass = 'c-error';
													$scope.showAlertMessage = true;
													$timeout(function() {
														$scope.showAlertMessage = false;
													}, 2500);
												}
												$mdDialog.hide();
												$scope.getAllRatings();
											},
											function(error) {
												$mdDialog.hide();
												console.log(error);
											});
								
								}
							else{
								// update rating if user has already rated the solution								
								apiService.updateRatingSolution(dataObjRating)
								.then(
										function(response) {
											console.log("Rating response : " + angular.toJson(response));
											/************* if else commented since ratings doens't responsd with correct status at offshore **********/
											if(response.status == 200 || response.status == 201){
												$scope.getSolutionratings();
												$location.hash('md-model-detail-template');
												$anchorScroll();
												$scope.msg = "Rating Updated successfully.";
												$scope.icon = 'report_problem';
												$scope.styleclass = 'c-success';
												$scope.showAlertMessage = true;
												$timeout(function() {
													$scope.showAlertMessage = false;
												}, 2500);
											}else{
												$scope.getSolutionratings();
												$location.hash('md-model-detail-template');
												$anchorScroll();
												$scope.msg = "Unexpected Error occurred.";
												$scope.icon = 'report_problem';
												$scope.styleclass = 'c-error';
												$scope.showAlertMessage = true;
												$timeout(function() {
													$scope.showAlertMessage = false;
												}, 2500);
												
											}
											$mdDialog.hide();
											$scope.getSolutionratings();
											
										},
										function(error) {
											console.log(error);
											$mdDialog.hide();
										});
								
							}
							
			               }
						
						$scope.getSolutionratings=function(){
							
							if(user){
								var url = '/api/solutions/ratings/'+$stateParams.solutionId+'/user/'+user[1];
							var dataObj = {
									"request_body": {
										    "fieldToDirectionMap": {},
										    "page": 0,
										    "size": 20
										  },
										  "request_from": "string",
										  "request_id": "string"
										}
								
							$http({
								method : 'POST',
								url : url,
								data : dataObj
							}).success(function(data, status, headers,config) {
									$scope.mlSolutionGetRating = data.response_body;
									$scope.ratingReview = $scope.mlSolutionGetRating.textReview;
								 
								}).error(
									function(data, status, headers,config) {
										alert("Error: "+status);
										console.log(status);
									});
							}
						}; 
						$scope.getSolutionratings();
						
						
					  //API for rating the model End
						
						//get User image.
						var pathArray = location.href.split( '/' );
						var protocol = pathArray[0];
						var host = pathArray[2];
						var baseURL = protocol + '//' + host;
						$scope.showAltImage = true;
						$scope.getUserImage = function (userId){
							if(userId != ""){
							var req = {
								    method: 'Get',
								    url: '/api/users/userProfileImage/' + userId
								};
							$http(req)
							.success(function(data, status, headers,config) {
								if(data.status){
								    $scope.userImage = data.response_body;
								    $scope.showAltImage = false;
								}
							}).error(function(data, status, headers, config) {
								data.response_body
							});
							}
						}
					
						$scope.goToRelatedSolutions = function(solutionId){
							$state.go('marketSolutions', {solutionId : solutionId, parentUrl: 'marketSolutions'});
						}
						
						console.log("model: "+$rootScope.urlPath)
						
						if($stateParams.parentUrl){
							$rootScope.routeParentUrl = $stateParams.parentUrl;
							$window.sessionStorage.setItem("SavedString",$rootScope.routeParentUrl);
						}else{
							/*$rootScope.routeParentUrl;*/
						}
						$rootScope.routeParentUrl = $window.sessionStorage.getItem("SavedString");
						$rootScope.routeParentUrl;
						
						 
						if ($rootScope.routeParentUrl == 'mymodel') {
							$rootScope.parentActive = 'mymodel'
						} else if($rootScope.routeParentUrl == 'marketplace') {
							$rootScope.parentActive = 'marketplace'
						}

						componentHandler.upgradeAllRegistered();
						
						$rootScope.urlPath = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);
						//alert($rootScope.urlPath)
						
						if($rootScope.urlPath == 'manageModule'){
							$scope.parentUrl = false
						}else{
							$scope.parentUrl = true
						}
						
						$scope.loginUserID = "";
						if (localStorage.getItem("userDetail")) {
							$scope.loginUserID = JSON.parse(localStorage
									.getItem("userDetail"))[1];
						}

						if ($stateParams.solutionId == ''
								|| $stateParams.solutionId == null) {
							console.log("model details solnId is null")
						} else {
							localStorage.setItem("solutionId",
									$stateParams.solutionId);
						}

						if (localStorage.getItem("solutionId")) {
							console.log("local storage solutionId",
									localStorage.solutionId);
							$scope.solutionId = localStorage
									.getItem("solutionId");

						}
						
						// $scope.solutionId = $stateParams.solutionId
						var modelType = '';
						$scope.apiUrl;
						if ($stateParams.solutionId == null) {
							$scope.apiUrl = '/api/solutions/'
									+ $scope.solutionId;
						} else {
							$scope.apiUrl = '/api/solutions/'
									+ $stateParams.solutionId
						}
						
						$http({
							method : 'GET',
							url : $scope.apiUrl,
						})
								.success(
										function(data, status, headers, config) {
											 
											if( !user || data.response_body.ownerId == user[1] ){
												$scope.cantRate = true;
											}
											else {
												$scope.cantRate = false;
											}
											$scope.tags = data.response_body.solutionTagList;
											$scope.modelOwnerId = data.response_body.ownerId;
											modelType = data.response_body.modelType;
											$scope.solution = data.response_body;
											$scope.firstRate = data.response_body.solutionRating;
											$scope.ratingCount = data.response_body.ratingCount;
											console.log($scope.solution);
											$scope.getUserImage($scope.modelOwnerId);
											relatedSoltion();
											$scope.disableEdit();
											if (data.response_body.revisions) {
												var length = data.response_body.revisions.length;
												
												var counter = 0;
												//**adding list of versions
												$scope.versionList = [];
												while(counter < length){
													($scope.versionList).push(data.response_body.revisions[counter]);
													counter++;
												}
												
												($scope.versionList).sort(function(a, b) {
													  // sort version according to created date
													  const genreA = a.created;
													  const genreB = b.created;

													  let comparison = 0;
													  if (genreA < genreB) {
													    comparison = 1;
													  } else if (genreA > genreB) {
													    comparison = -1;
													  }
													  return comparison; }
												);
												
												$scope.versionId = $scope.versionList[0].version;
												$scope.revisionId = $scope.versionList[0].revisionId;
												$scope.getComment();
												$scope.getArtifacts();
												$scope.getSolPublicDesc();
												$scope.getSolCompanyDesc();
												if(!$scope.solutionPublicDesc){
													$scope.solutionPublicDesc = $scope.solutionCompanyDesc;
												}
												 
												$scope.getPublicSolutionDocuments($scope.solution.accessType);
											}

											if (JSON.parse(localStorage
													.getItem("userDetail"))) {
												$scope.userDetails = JSON
														.parse(localStorage
																.getItem("userDetail"));
												$scope.userName = $scope.userDetails[0];
												$scope.loginUserID = $scope.userDetails[1];
											}

											if (JSON.parse(localStorage
													.getItem("userDetail"))
													&& $scope.solution.ownerId == $scope.loginUserID) {
												$scope.isUser = true
											} else {
												$scope.isUser = false
											}
											$stateParams.solutionId = $scope.solution.solutionId
											if (data.response_body.revisions != null) {
												$scope.revisionId = $scope.versionList[0].revisionId;
												donwloadPopupValue();
											}
											
											//trying for signatures-can be replaced by reading the .proto file and displaying the contents
											var qs = querystring.parse();
											var urlBase = baseURL + '/dsce/';
							                var options = Object.assign({
							                	base:"dsce/dsce/",
							                    //base: urlBase,
							                	//base: 'http://localhost:8088/dsce/',
							                    protobuf: 'artifact/fetchProtoBufJSON'
							                }, qs);
							                
							                function build_url(verb, params) {
							                    return options.base + verb + '?' + Object.keys(params).map(function(k) {
							                        return k + '=' + encodeURIComponent(params[k]);
							                    }).join('&');
							                }
											
							                var url= build_url(options.protobuf, {
					                            userId: $scope.solution.ownerId,
					                            solutionId :  $scope.solution.solutionId,
					                            version : $scope.versionId
					                        });
					                        $http.get(url).success(function(proto){
					                        	console.log(proto);
					                        	
					                        	var i=0; var j=0; var messageJson = [];
					                        	var operations = new Object(); var messages = new Object();var operationName = null; var messagesName = [];
					                        	$scope.protoDisplay = proto;
					                        	
					                        	angular.forEach(proto.protobuf_json.service.listOfOperations, function(value, key) {
					                        		messagesName= [];
					                        		angular.forEach(value.listOfInputMessages,function(value1,key1){
					                        			messagesName["input"]=value1.inputMessageName;
					                        			angular.forEach(proto.protobuf_json.listOfMessages, function(value2, key2) {
					                        				messageJson=[];
					                        				if(value1.inputMessageName === value2.messageName){
						                            			angular.forEach(value2.messageargumentList, function(value3, key3) {  
						                            				messageJson.push(value3.rule+' '+value3.type+' '+value3.name+' = '+value3.tag); 
						                            			});
						                            			messages[value2.messageName]= messageJson;
						                            			
					                        				} 
					                        			});
					                        		});
					                        		
						                        	angular.forEach(value.listOfOutputMessages,function(value1,key1){
						                        		messagesName["output"]= value1.outPutMessageName;
						                        		angular.forEach(proto.protobuf_json.listOfMessages, function(value2, key2) {
						                        			messageJson=[];
							                            	if(value1.outPutMessageName === value2.messageName){
							                            		angular.forEach(value2.messageargumentList, function(value3, key3) {   
							                            			messageJson.push(value3.rule+' '+value3.type+' '+value3.name+' = '+value3.tag); 
							                            		});
							                            		messages[value2.messageName] = messageJson;
							                            	}
							                            });
						                        	});
						                        	operationName = value.operationType+" "+value.operatioName;	
			                                        operations[operationName] = messagesName;
			                                    });
					                        	
					                        	$scope.modelName = proto.protobuf_json.service.name;
					                        	$scope.operationDisplay = operations;
					                        	$scope.messageDisplay = messages;
					                        	
					                        });
											var solutionName = $scope.solution.name;
											//comments only for summit demo
											if(solutionName.indexOf('Predictor') > -1){
												var origComments = {
														"Sam Kimberly": {
															"time": "9:38am",
															"message": "I found this particular model to be very flexible—I’ve applied it to several different usage prediction problems to estimate resource utilization for planning purposes"
														},
														"Danielle Potarski": {
															"time": "2:13pm",
															"message": "I like the fact that the model trains itself as it goes."
														},
														"Wayne O’Keefe": {
															"time": "4:45pm",
															"message": "What technique is used to detect seasonality in the data? Or does it assume a fixed cycle period?"
														}	
													};
													$scope.CommentName = origComments;
													
											}else if(solutionName.indexOf('Video') > -1){
												var origComments = {
														"Lenore Cassals": {
															"time": "9:23am",
															"message": "Very nice! Can I use this to monitor a video feed looking for specific images?",
															"reply": {
																"Jim Smith": {
																	"time": "1:32pm",
																	"message": "Yes! You could pipeline this into a second model that recognizes whatever you are looking for. My emotion classifier model works in this fashion." 
																}		
															}
														},
														"Stuart Arbiter": {
															"time": "4:21pm",
															"message": "I’m going to train this with my own collection of domain-specific images for a factory inspection/training task." 
														}
													};
													$scope.CommentName = origComments;
													
											}else if(solutionName.indexOf('Threat') > -1){
												var origComments = {
														"Serena Pleake": {
															"time": "9:38am",
															"message": "I’m the Chief Security Officer for my company, and we deployed this pretty easily. It’s being used in all our data centers to monitor intrusions and other anomalies."
														},
														"Lane Toomey": {
															"time": "11:14am",
															"message": "Do you have to be an R expert to use this?",
															"reply": {
																"Jim Smith": {
																	"time": "3:33pm",
																	"message": "Not really—since it creates a fully self-contained microservice, you don’t need to know anything about the implementation, if you don’t want to look under the hood."
																},
																"Laura Toomey": {
																	"time": "5:12pm",
																	"message": "What about retraining?"
																}
															}
														}
													};
													$scope.CommentName = origComments;
													
											}else if(solutionName.indexOf('Emotion') > -1){
												var origComments = {
														"Bryan Jones": {
															"time": "9:38am",
															"message": "Is this showing the emotions of the image itself, or the evoked emotions of someone seeing the image?",
															"reply": {
																"Jim Smith": {
																	"time": "9:55am",
																	"message": "The latter. It works by creating a feature vector over the space of recognized image classes, and then pipelining that into an emotion classifier."
																}
															}
														},
														"Charles Stoddard": {
															"time": "12:23pm",
															"message": "I’m planning to retrain this model using images captured from movies and television, and then use it to predict genres of films." 
														}
													};
													$scope.CommentName = origComments;
													
											}else if(solutionName.indexOf('Face') > -1){
												var origComments = {
														"Bryan Jones": {
															"time": "9:38am",
															"message": "I found this model useful in my application where I had to capture and publish security cam records without compromising the privacy of people captured on camera."
														},
														"Charles Stoddard": {
															"time": "12:38pm",
															"message": "Mapping sites use this kind of thing when showing images of people, yes? Can we also recognize other features we want to blur, such as license plates",
															"reply": {
																"Jim Smith": {
																	"time": "3:34pm",
																	"message": "Yes, though our current model doesn’t do that. You’re welcome to train and sub in a license-plate detector; then adding the blurring step should accomplish what you are after."
																}
															}
														},
														"Albert Davis": {
															"time": "4:02pm",
															"message": "If I want to use my own face-detection module (better (?) than what OpenCV provides), how much work would it be to substitute that? I notice the current OpenCV-based detector has some trouble when faces are small or not facing the camera.",
															"reply": {
																"Jim Smith": {
																	"time": "5:22pm",
																	"message": "See the answer above to the person who asked about recognizing license plates"
																}
															}
														}
															
													};
													$scope.CommentName = origComments;
													
											}else if(solutionName.indexOf('Defect') > -1){
												var origComments = {
														"Ginny Vogel": {
															"time": "9:38am",
															"message": "This model is great! Worked right out of the box for me. I’m going to recommend it to the other user groups in my company."
														},
														"Tim Fowler": {
															"time": "1:21pm",
															"message": "Look at the other models by the same author—they are all very well documented and work flawlessly."
														}
													};
													$scope.CommentName = origComments;
													
											}else if(solutionName.indexOf('Chat') > -1){
												var origComments = {
														"Toni Melville": {
															"time": "9:38am",
															"message": "I have a chat system that is a little different from what is described here. How easy would it be to use this model in my own system?",
															"reply": {
																"Laura Dempsey": {
																	"time": "10:28am",
																	"message": "You would need to write an adapter, but it should be possible."
																}
															}
														},
														"Steve Slocum": {
															"time": "3:12pm",
															"message": "Is there a way to automate testing of the ChatBot? Seems like a difficult problem." 
														}
													};
													$scope.CommentName = origComments;
													
											}else if(solutionName.indexOf('Entellio') > -1){
												var origComments = {
														"Dee James": {
															"time": "9:38am",
															"message": "This uses a Long-Short Hybrid model, yes? How does it perform on retrieval tasks?"
														},
														"Louis Wilson": {
															"time": "12:02pm",
															"message": "I love word2vec! I’ve used it many times, and I’m interested to see how you’ve use it here."
														}
													};
													$scope.CommentName = origComments;
													
											}else if(solutionName.indexOf('Sentiment') > -1){
												var origComments = {
														"Jill Stemple": {
															"time": "9:38am",
															"message": "I am planning to use this model, but trained on our large corpus of customer survey verbatims (from Retail and online interactions). We have over 3 million records / month. Will things break when we train?",
															"reply": {
																"Colin Alphonso": {
																	"time": "10:12am",
																	"message": "Should still work—may take a while. If you train on fancy hardware (GPU-optimized) it won’t take too long."
																}
															}
														},
														"Bethany Teller": {
															"time": "12:15pm",
															"message": "Thanks! Wish me luck.",
															"reply": {
																"Colin Alphonso": {
																	"time": "1:24pm",
																	"message": "Good luck! "
																}
															}
														},
														"Quentin Timony": {
															"time": "3:38pm",
															"message": "Why do you list matplotlib among the prerequisites? The microservice doesn’t actually do any plotting, right?",
															"reply": {
																"Colin Alphonso": {
																	"time": "5:43pm",
																	"message": "That’s true—we don’t. But you will likely want to use it to display the output."
																}
															}
														}
													};
													$scope.CommentName = origComments;
													
											}
											
										})
										
								.error(function(data, status, headers, config) {
									// called asynchronously if an error occurs
									// or server returns response with an error
									// status.
									console.log(status);
								});

						// };
						
						$scope.totalCommentCount = 0;
						$scope.postComment = function() {
							if (localStorage.getItem("userDetail")) {
								$scope.loginUserID = JSON.parse(localStorage
										.getItem("userDetail"))[1];
								$scope.userFullName = $scope.userDetails[0];
							}
							
							var threadObj = {
									  "request_body": {
										    "revisionId": $scope.revisionId,
										    "solutionId": $scope.solutionId
										    
										  }
										 
										};
							/*var commentObj = {
									"author": $scope.loginUserID,
									"name": $scope.userFullName,
									"url": $scope.solutionId,
									"text": $scope.comment
							}*/
								apiService.createThread(threadObj).then(function(response) {
									console.log(response);
									var commentObj = {
											  "request_body": {
												    "text": $scope.comment,
												    "threadId": response.data.response_body.threadId,
												    "url": $scope.solutionId,
												    "userId": $scope.loginUserID
												  },
												};
									apiService.createComment(commentObj).then(function(response) {
										$scope.getComment();
										$scope.comment = '';
									});
								});
							/*var url = "/cmnt/api/comments?threadUrl="+ $scope.solutionId +"&threadTitle=";
							$http({
								method : 'POST',
								url : url,
								data : commentObj
							}).success(function(data, status, headers,config) {
								$scope.getComment();
								$scope.comment = '';
							});*/
						}
						
						$scope.newcomment = {};
						$scope.postReply = function(key, comment){
							if (localStorage.getItem("userDetail")) {
								$scope.loginUserID = JSON.parse(localStorage
										.getItem("userDetail"))[1];
								$scope.userFullName = $scope.userDetails[0];
							}
							
								var commentObj = {
										  "request_body": {
											    "text": $scope.newcomment[key].text,
											    "threadId": comment.threadId,
											    "parentId": comment.commentId,
											    "url": $scope.solutionId,
											    "userId": $scope.loginUserID
											  },
											};
								apiService.createComment(commentObj).then(function(response) {
									$scope.getComment();
									$scope.comment = '';
								});
						
							/*var replyCommentObj = {
									"author": $scope.loginUserID,
									"name": $scope.userFullName,
									"url": $scope.solutionId,
									"text": $scope.newcomment[key].text,
									"parentId": parentId
							}
							var url = "/cmnt/api/comments?threadUrl="+ $scope.solutionId +"&threadTitle=";
							$http({
								method : 'POST',
								url : url,
								data : replyCommentObj
							}).success(function(data, status, headers,config) {
								$scope.getComment();
								
							})*/
							$scope.newcomment = {};
						}
						
						
						$scope.getComment = function() {
							if (localStorage.getItem("userDetail")) {
								$scope.loginUserID = JSON.parse(localStorage
										.getItem("userDetail"))[1];
							}
							var reqObj = {
									  "request_body": {
										    "page": 0,
										    "size": 9
										  },
										};
							apiService.getComment($scope.solutionId, $scope.revisionId, reqObj).then(function(response) {
								//$scope.totalCommentCount = response.data.response_body.content.CommentsCount;
								$scope.commentList = [];
								angular.forEach(response.data.response_body.content,function(value,key) {
									if(response.data.response_body.content[key].parentId == null){
										$scope.commentList.push({
											creationDate : response.data.response_body.content[key].creationDate,
											text : response.data.response_body.content[key].text,
											commentId : response.data.response_body.content[key].commentId,
											threadId : response.data.response_body.content[key].threadId
										});
						
										var userObject = {
												  "request_body": {
													  		    "userId": value.userId
												  					}};
										apiService.getUserAccountDetails(userObject).then(function(userDetail){
											console.log(userDetail);
												$scope.commentList[key].name = userDetail.data.response_body.loginName
									    });
										apiService.getUserProfileImage(value.userId).then(function(userImage){
											console.log(userImage);
												$scope.commentList[key].image = userImage.data.response_body;
										});
									}
								});
								console.log(response);
								
							});
							
							/*var url = "/cmnt/api/comments?threadUrl="+ $scope.solutionId +"&threadTitle=";
							$http({
								method : 'GET',
								url : url
							}).success(function(data, status, headers,config) {
								$scope.totalCommentCount = data.totalCommentCount;
								$scope.commentList = data.comments;
							})*/
						};
						
						/*$scope.getReply = function(){
							angular.forEach($scope.commentList,function(commentValue,commentKey) {
								if($scope.commentList[commentKey].commentId == response.data.response_body.content[key].parentId){
									$scope.commentList[commentKey].replies.push({
										creationDate : response.data.response_body.content[key].creationDate,
										text : response.data.response_body.content[key].text,
										name : "",
										
									});
					
									var userObject = {
											  "request_body": {
												  		    "userId": value.userId
											  					}};
									apiService.getUserAccountDetails(userObject).then(function(userDetail){
										console.log(userDetail);
											$scope.commentList[key].name = userDetail.data.response_body.loginName,
											$scope.commentList[key].image = userDetail.data.response_body.picture
										
										
									});
									}
								});
							console.log($scope.commentList);
						};*/
						
						$scope.editComment = function(comment) {
							var commentObj = {
									  "request_body": {
										    "text": comment.text,
										    "commentId": comment.commentId,
										    "threadId": comment.threadId,
										    "url": $scope.solutionId,
										    "userId": $scope.loginUserID
										  },
										};
							apiService.updateComment(commentObj).then(function(response) {
								console.log(response.data);
								$scope.getComment();
							});
							/*var reqObj = {"author":comment.author,
											"email":comment.emailHash,
											"url":$stateParams.solutionId,
											"text":comment.text, 
											"name":comment.name
											};
							
							var url = "/cmnt/api/comments/"+ comment.id;
							$http({
								method : 'PUT',
								data : reqObj, 
								url : url
							}).success(function(data, status, headers,config) {
								console.log(data);
							})*/
						};
						
						$scope.deleteComment = function(comment) {
							apiService.deleteComment(comment.threadId,comment.commentId).then(function(response) {
								console.log(response.data);
								$scope.getComment();
							});
						    /*var url = "/cmnt/api/comments/"+ commentId;
							$http({
								method : 'Delete',
								url : url
							}).success(function(data, status, headers,config) {
								console.log(data);
							})*/
						}
						$scope.getSolPublicDesc = function(){
							var req = {
									method : 'GET',
									url : '/site/api-manual/Solution/description/public/' + $scope.solutionId + '/' + $scope.revisionId,
							};
							$http(req)
							.success(
									function(data, status, headers,
											config) {
										$scope.solutionPublicDesc = data.description;
									}).error(
											function(data, status, headers,
													config) {
									});
						}
						
						$scope.getSolCompanyDesc = function(){
							var req = {
									method : 'GET',
									url : '/site/api-manual/Solution/description/org/' + $scope.solutionId + '/' + $scope.revisionId,
							};
							$http(req)
							.success(
									function(data, status, headers,
											config) {
										$scope.solutionCompanyDesc = data.description;
									}).error(
											function(data, status, headers,
													config) {
									});
						}
						$scope.getSolPublicDesc();
						$scope.getSolCompanyDesc();
						if(!$scope.solutionPublicDesc){
							$scope.solutionPublicDesc = $scope.solutionCompanyDesc;
						}
						var session = sessionStorage.getItem("SessionName")
						if (session) {
							console.log(session);
						}
						// console.log($location.path().split('/')[2]);
						var sol_id = $location.path().split('/')[2]
						// $scope.getSolution(sol_id);

						// Initialize the default start rating
						$('#input-2').rating({
							step : 1,
							size : 'xxs',
							starCaptionClasses : {
								1 : 'text-danger',
								2 : 'text-warning',
								3 : 'text-info',
								4 : 'text-primary',
								5 : 'text-success'
							}
						});

						// alert
						$scope.publishalert = '';

						// show solution download popup based on User logedin
						// session
						$scope.solutionDownloadWindow = function() {
							var session = sessionStorage.getItem("SessionName")
							if (session === "undefined") {

								console.log("Not signin");
								$('#myModal').modal('show');
							} else {
								console.log("Singedin");
								$('#mydownloadModal').modal('show');

							}

						}

						// Publish the specific solutio to Market place by
						// specific version

						$scope.solutionsDownload = function(version) {

							console.log(version);
							$http({
								method : 'GET',
								url : '/api/download/' + version,
							}).success(function(data, status, headers, config) {
								console.log(data);
							}).error(function(data, status, headers, config) {
								// called asynchronously if an error
								// occurs
								// or server returns response with an
								// error status.
								console.log(status);
							});

						}

						// Publish the solution to Market place if both solution
						// author and loged in user same
						$scope.publishtoMarket = function(pub_value) {

							var userId = sessionStorage.getItem("SessionName")

							if (userId === $scope.solution.ownerId) {

								// console.log("I am the Owner");
								// console.log(pub_value);

								if ($scope.solution.accessType == 'PR') {

									var data = $.param({
										visibility : pub_value
									});

									/*
									 * $http( { method : 'PUT', url :
									 * '/api/publish/' +
									 * $scope.solution.solutionId + '?' + data, })
									 * .success(
									 */
									apiService.putPublishSolution
											.then(
													function(response) {
														console.log(data);
														$scope.publishalert = {
															type : 'success',
															msg : 'Well done! You successfully publish solution to Market Place.'
														};
														// setTimeout(function($scope){
														// $scope.publishalert='';console.log(publishalert);
														// }, 3000);
														setTimeout(
																function() {
																	$scope
																			.$apply(function() {
																				$scope.publishalert = '';
																			});
																}, 3000);
													}, function(error) {
														// called asynchronously
														// if an error occurs
														// or server returns
														// response with an
														// error status.
														console.log(error);
													});
								} else {

									$scope.publishalert = {
										type : 'danger',
										msg : 'This solution already published in the Market place'
									};
									// setTimeout(function($scope){
									// $scope.publishalert='';console.log(publishalert);
									// }, 3000);
									setTimeout(function() {
										$scope.$apply(function() {
											$scope.publishalert = '';
										});
									}, 3000);

								}
								// alert("The solution aleady published");
							} else {
								alert("Please sign in as owner  to publish solution");
							}

						}

						// /
						$scope.isReadonly = false; // default test value
						$scope.changeOnHover = false; // default test value
						$scope.maxValue = 5; // default test value
						$scope.ratingValue = 2; // default test value
						
						angular.element('.md-version-ddl1').hide();
						$document.on('click', function(){
							if(angular.element('.md-version-ddl1')){
								angular.element('.md-version-ddl1').hide();
							}
						});
						
						$scope.showVersion = function() {
							if(angular.element('.md-version-ddl1').css('display') == 'none'){
								angular.element('.md-version-ddl1').show();
							} else {
								angular.element('.md-version-ddl1').hide();
							}	
						}
						
						$scope.loadVersionDetails = function(solutionId, revisionId, versionId){
							 
							$scope.solution.solutionId = solutionId; 
							$scope.revisionId = revisionId;
							$scope.versionId = versionId;
							angular.element('.md-version-ddl1').hide();
							donwloadPopupValue();
							$scope.getSolPublicDesc();
							$scope.getSolCompanyDesc();
							if(!$scope.solutionPublicDesc){
								$scope.solutionPublicDesc = $scope.solutionCompanyDesc;
							}
							 
							$scope.getPublicSolutionDocuments($scope.solution.accessType);
							$scope.getArtifacts();
						}
										
						
						/***************** get solution descriptions ***********************/
						
						$scope.getSolCompanyDesc = function() {
							var req = {
								method : 'GET',
								url : '/site/api-manual/Solution/description/org/'
										+ $scope.solutionId + "/" + $scope.revisionId,
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												//$scope.solutionCompanyDesc = data.description;
												$scope.solutionCompanyDesc1 = $sce.trustAsHtml(data.description);
												/*if($scope.solutionCompanyDesc){
													$scope.solutionCompanyDescStatus = true;
												}*/
											}).error(
											function(data, status, headers,
													config) {
											});
						}
						$scope.getSolCompanyDesc();

						$scope.getSolPublicDesc = function() {
						
							var req = {
								method : 'GET',
								url : '/site/api-manual/Solution/description/public/'
										+ $scope.solutionId + "/" + $scope.revisionId,
							};
							$http(req)
									.success(
											function(data, status, headers,
													config) {
												//$scope.solutionPublicDesc = data.description;
												$scope.solutionPublicDesc1 = $sce.trustAsHtml(data.description);
												/*if($scope.solutionPublicDesc){
													$scope.solutionPublicDescStatus = true;
												}*/
											}).error(
											function(data, status, headers,
													config) {
											});
						}
						$scope.getSolPublicDesc();
						
						$scope.getSolutionImages = function(){
	                       	 var getSolutionImagesReq = {
										method : 'GET',
										url : '/site/api-manual/Solution/solutionImages/'+$scope.solutionId
								};

	                       	 $http(getSolutionImagesReq)
									.success(
											function(data, status, headers,
													config) {
												if(data.response_body.length > 0) {
													$scope.showSolutionImage = true;
													$scope.solutionImageName = data[0];
													$scope.imgURLdefault = "/site/binaries/content/gallery/acumoscms/solution/" + $scope.solutionId + "/" + data.response_body[0];

													$scope.previewImage = $scope.imgURLdefault;
												
												}
											}).error(
													function(data, status, headers,
															config) {
														$scope.showSolutionImage = false;
													});
							}
							$scope.getSolutionImages();
							
							/**********************************END*****************************/
						
						
						// Value for Download Popup
						function donwloadPopupValue() {
							/*
							 * $http({ method : 'GET', url :
							 * 'api/solutions/'+$scope.solution.solutionId+'/revisions/'+$scope.revisionId,
							 * }).success(
							 */
							apiService
									.downloadPopupValue(
											$scope.solution.solutionId,
											$scope.revisionId)
									.then(
											function(response) {
												$scope.downloadData = response.data.response_body;
												
												// $state.reload();
												// console.log(data);
											}, function(error) {
												// called asynchronously if an
												// error occurs
												// or server returns response
												// with an error status.
												console.log(error);
											});
						}
						$scope.download = function(artifactId) {
							
							$scope.loginUserID = "";
							if (localStorage.getItem("userDetail")) {
								$scope.loginUserID = JSON.parse(localStorage
										.getItem("userDetail"))[1];
							}

							var url = 'api/downloads/'+$scope.solution.solutionId+'?artifactId='+artifactId+'&revisionId='+$scope.revisionId+'&userId='+$scope.loginUserID;
							// var url = '
							// http://localhost:8083/downloads/101?artifactId=201&revisionId=1001';
							
							 $http({ method : 'GET', url : url, responseType : "arraybuffer", })
							 .success(function(data, status, headers, config) { 
								 headers = headers(); 
								 var anchor = angular.element('<a/>'); //FOR IE 
								 if(navigator.appVersion.toString().indexOf('.NET') > 0) 
									 window.navigator.msSaveBlob(new Blob([data], { type: headers['content-type'] }), headers['x-filename']); 
								 else { //FOR Chrome and Forefox 
									 anchor.css({display: 'none'}); // Make sure it's not visible
									  angular.element(document.body).append(anchor); 
									  // Attach to document 
									  anchor.attr({ href: window.URL.createObjectURL(new Blob([data], {type: headers['content-type'] })), target: '_blank', download: headers['x-filename']
									  })[0].click(); 
									  anchor.remove(); 
								 }
							  $rootScope.showPrerenderedDialog("", '#downloadConfirm'); 
							  $mdDialog.hide();
							  })
							  .error(function(data, status, headers, config) {
								  $rootScope.showPrerenderedDialog("", '#downloadConfirm');
								  console.log(status); $mdDialog.hide(); });
							 
						}
						
						$scope.versionDownload = function(artifactId) {
							
							$scope.loginUserID = "";
							if (localStorage.getItem("userDetail")) {
								$scope.loginUserID = JSON.parse(localStorage
										.getItem("userDetail"))[1];
							}

							var url = 'api/downloads/'+$scope.solution.solutionId+'?artifactId='+artifactId+'&revisionId='+$scope.revisionId+'&userId='+$scope.loginUserID;
							
							 $http({ method : 'GET', url : url, responseType : "arraybuffer", })
							 .success(function(data, status, headers, config) { 
								 headers = headers(); 
								 var anchor = angular.element('<a/>'); // FOR
																		// IE
								 if(navigator.appVersion.toString().indexOf('.NET') > 0) 
									 window.navigator.msSaveBlob(new Blob([data], { type: headers['content-type'] }), headers['x-filename']); 
								 else { // FOR Chrome and Forefox
									 anchor.css({display: 'none'}); // Make sure
																	// it's not
																	// visible
									  angular.element(document.body).append(anchor); 
									  // Attach to document
									  anchor.attr({ href: window.URL.createObjectURL(new Blob([data], {type: headers['content-type'] })), target: '_blank', download: headers['x-filename']
									  })[0].click(); 
									  anchor.remove(); 
								 }
							  $rootScope.showPrerenderedDialog("", '#downloadConfirm'); 
							  //$mdDialog.hide();
							  })
							  .error(function(data, status, headers, config) {
								  $rootScope.showPrerenderedDialog("", '#downloadConfirm');
								  console.log(status);
								 // $mdDialog.hide(); 
								  });
							 
						}
						/** ****** Export/Deploy to Azure starts *** */
						
						$scope.getArtifacts = function() {

							$http(
									{
										method : 'GET',
										url : '/api/solutions/'
												+ $scope.solutionId
												+ '/revisions/'
												+ $scope.revisionId
									})
									.then(
											function successCallback(response) {
												$scope.artifactDownload = response.data.response_body;
												for (var x = 0; x < response.data.response_body.length; x++) {
													if(response.data.response_body[x].artifactTypeCode == "DI"){
														$scope.artifactId = response.data.response_body[x].artifactId;
														$scope.artifactType = response.data.response_body[x].artifactTypeCode;
														$scope.artifactDesc = response.data.response_body[x].description;
														$scope.artifactName = response.data.response_body[x].name;
														$scope.artifactVersion = response.data.response_body[x].version;
														$scope.artifactUri = response.data.response_body[x].uri;
													}
												}

											},
											function errorCallback(response) {
												alert("Error: "
														+ response.status
														+ "Detail: "
														+ response.data.response_detail);
											});

						}
						
						$scope.authenticateAnddeployToAzure = function() {
							var imageTagUri = '';
							if ($scope.artifactType != null
									&& $scope.artifactType == 'DI') {
								imageTagUri = $scope.artifactUri;
							}
							console.log($scope.brokerURL + $scope.positionM1 + $scope.positionM2 + $scope.positionM3 + $scope.positionM4);
							console.log($scope.fieldM1 + $scope.fieldM2 + $scope.fieldM3 + $scope.fieldM4);
							if($scope.solution.tookitType != "CP") {
                                var reqObject = '';
                                if($scope.exportTo == 'microsoft'){
                                      var url = '/azure/singleImageAzureDeployment';
                                      reqObject = {
                                                  /*'request_body' : {*/
                                                        'acrName': $scope.acrName,
                                                        'client': $scope.applicationId,
                                                        'key': $scope.secretKey,
                                                        'rgName': $scope.resourceGroup,
                                                        'solutionId': $scope.solution.solutionId,
                                                        'solutionRevisionId': $scope.revisionId,
                                                        'storageAccount': $scope.storageAccount,
                                                        'subscriptionKey':  $scope.subscriptionKey,
                                                        'tenant': $scope.tenantId,
                                                        //'userId':  $scope.loginUserId[1],
                                                        'imagetag': imageTagUri
                                                  /*}*/
                                      }
                                }
                                else if($scope.exportTo == 'ripple'){
                                      var url =  '/openstack/singleImageOpenstackDeployment';
                                      reqObject ={
                          'vmName': $scope.vmName,
                          'solutionId': $scope.solution.solutionId,
                          'solutionRevisionId': $scope.revisionId,
                          //'userId':  $scope.loginUserId[1],
                          'imagetag': imageTagUri
                                      }
                                }
                                $http({
                                      method : 'POST',
                                      url : url,
                                      data: reqObject
                                      
                                }).then(function(response) {
                                            //alert("Deployment Started Successfully")
                                	$mdDialog.hide();
                                	$location.hash('md-model-detail-template');  // id of a container on the top of the page - where to scroll (top)
									$anchorScroll(); 							// used to scroll to the id 
									$scope.msg = "Deployment Started Successfully. "; 
									$scope.icon = '';
									$scope.styleclass = 'c-success';
									$scope.showAlertMessage = true;
									$timeout(function() {
										$scope.showAlertMessage = false;
									}, 2000);
                                      },
                                      function(error) {
                                            console.warn("Error occured")

                                      });
                                
                          } else {
                                var reqObject = '';
                                if($scope.exportTo == 'microsoft'){
                                      var url = '/azure/compositeSolutionAzureDeployment';
                                      reqObject = {
                                                  /*'request_body' : {*/
                                                        'acrName': $scope.acrName,
                                                        'client': $scope.applicationId,
                                                        'key': $scope.secretKey,
                                                        'rgName': $scope.resourceGroup,
                                                        'solutionId': $scope.solution.solutionId,
                                                        'solutionRevisionId': $scope.revisionId,
                                                        'storageAccount': $scope.storageAccount,
                                                        'subscriptionKey':  $scope.subscriptionKey,
                                                        'tenant': $scope.tenantId,
                                                        //'userId':  $scope.loginUserId[1],
                                                  /*}*/
                                      }
                                }
                                else if($scope.exportTo == 'ripple'){
                                      var url = "/openstack/compositeSolutionOpenstackDeployment";
                                      reqObject ={
                          'vmName': $scope.vmName,
                          'solutionId': $scope.solution.solutionId,
                          'solutionRevisionId': $scope.revisionId,
                          //'userId':  $scope.loginUserId[1],
                          'imagetag': imageTagUri
                                      }
                                }
                                $http({
                                      method : 'POST',
                                      url : url,
                                      data: reqObject
                                }).then(function(response) {
                                      //alert("Deployment Started Successfully")
                                	$mdDialog.hide();
                                	$location.hash('md-model-detail-template');  // id of a container on the top of the page - where to scroll (top)
									$anchorScroll(); 							// used to scroll to the id 
									$scope.msg = "Deployment Started Successfully. "; 
									$scope.icon = '';
									$scope.styleclass = 'c-success';
									$scope.showAlertMessage = true;
									$timeout(function() {
										$scope.showAlertMessage = false;
									}, 2000);
                                },
                                function(error) {
                                      console.warn("Error occured")

                                });
                          }
						}
						//Default values
						$scope.positionM1 = "mime_type";$scope.positionM3 = "image_binary";$scope.positionM2 = 1;$scope.positionM4 = 2;
						$scope.fieldM1 = "mime_type";$scope.fieldM3 = "image_binary";$scope.fieldM4 = "image_binary";$scope.fieldM2 = "mime_type";
						//Deploy to Broker
						$scope.deployCloudVal = function(){
							var obj1 =  '{'+ $scope.positionM1 + ':' + $scope.positionM2+','+ $scope.positionM3+":" +$scope.positionM4 +'}';
							var obj2 =  '{'+ $scope.fieldM1 + ':' + $scope.fieldM2+','+ $scope.fieldM3+":" +$scope.fieldM4 +'}';
							var obj3 = '{"url":"'+$scope.brokerURL+'"}';
							var reqObj = {
										  urlAttribute:obj3,
                                          jsonPosition:obj1,
                                          jsonMapping : obj2,
                                          'acrName': $scope.acrName,
                                          'client': $scope.applicationId,
                                          'key': $scope.secretKey,
                                          'rgName': $scope.resourceGroup,
                                          'solutionId': $scope.solution.solutionId,
                                          'solutionRevisionId': $scope.revisionId,
                                          'storageAccount': $scope.storageAccount,
                                          'subscriptionKey':  $scope.subscriptionKey,
                                          'tenant': $scope.tenantId,
									};
							var req = {
									 method: 'POST',
									 url: '/azure/compositeSolutionAzureDeployment',
									 //data: { test: reqObj }
									 data: reqObj
									}

									$http(req).then(
										function(data){
											console.log(angular.toJson(data))
											
										},
										function(data){
											 
											});
							
						}
						
						$scope.handleDismiss = function() {
							console.info("in handle dismiss");
							$mdDialog.cancel();

							// $ctrl.$dismiss();
						};

						$scope.updateRating = function(rating) {
							 
							if ($scope.loginUserID != ""
									&& $scope.loginUserID != $scope.solution.ownerId) {
								$scope.solution.solutionRating = rating;
								var ratingDetails = {
									"request_body" : {

										"rating" : rating,
										"solutionId" : $scope.solution.solutionId,
										"userId" : $scope.loginUserID
									},
									"request_from" : "string",
									"request_id" : "string"
								}
								/*if ($scope.solution.solutionRating < 1
										&& $scope.solution.solutionRating > 5) {*/
									apiService.createRatingSolution(
											ratingDetails).then(
											function(response) {

											}, function(error) {
												alert(error.response_detail);
											});
								/*} else {
									apiService.updateRatingSolution(
											ratingDetails).then(
											function(response) {

											}, function(error) {
												alert(error.response_detail);
											});
								}*/

							}
						}

						$scope.imgURLnull = "images/vmpredict2.png";
						$scope.imgURLcommercial = "images/commercial_pixelate.jpg";
						$scope.imgURLemotion ="images/emotion_classifier.png";
						$scope.imgURLthreat ="images/threat_analytics.png";
						$scope.imgURLvideo ="images/video_analytics.png";
						$scope.imgURLChat = "images/ChatBot.png";
						$scope.imgURLSensitive = "images/Sensitive.png";
						$scope.imgURLdefault ="images/default-model.png";
						
						//$scope.getUserImage($scope.loginUserID);
						
						$scope.getSolutionImages = function(){
	                       	 var getSolutionImagesReq = {
										method : 'GET',
										url : '/site/api-manual/Solution/solutionImages/'+$scope.solutionId
								};
	                       	 $http(getSolutionImagesReq)
									.success(
											function(data, status, headers,
													config) {
												if(data.response_body.length > 0)
													$scope.imgURLdefault = "/site/binaries/content/gallery/acumoscms/solution/" + $scope.solutionId + "/" + data.response_body[0];
												else
													$scope.imgURLdefault = "images/default-model.png";
											}).error(
													function(data, status, headers,
															config) {
														return "No Contents Available"
													});
							}
							$scope.getSolutionImages();
						
							$scope.getPublicSolutionDocuments = function(type){
								var accessType = 'public';
								if( type == 'OR' ){
									accessType = 'org';
								}
								
		                       	 var getSolutionDocumentsReq = {
											method : 'GET',
											url : '/site/api-manual/Solution/solutionAssets/'+$scope.solutionId + "/" + $scope.revisionId + "?path="+accessType
									};
		                       	 $http(getSolutionDocumentsReq)
										.success(
												function(data, status, headers,
														config) {
													 
													$scope.supportingDocs = [];
													console.log(" Get Asset File name : " + data.response_body);
													$scope.supportingDocs = data.response_body;
												}).error(
														function(data, status, headers,
																config) {
															$scope.supportingDocs = [];
															return "No Contents Available"
														});
								}
							
								//$scope.getPublicSolutionDocuments();
								
								$scope.onClickModel = function(id, ownerId){
									/*if($scope.loginUserID == ownerId){
										$state.go('marketSolutions',{'solutionId':id})
										//window.location.href = "#/modelEdit/" + id;
									}else{
										window.location.href = "#/marketSolutions/" + id;
									}*/
									$scope.updateViewCount = function() {
										$scope.solutionId = id;
										apiService
												.updateViewCount($scope.solutionId)
												.then(
														function(response) {
															$scope.status = response.status;
															$scope.detail = response.data.response_detail;
															$state.go('marketSolutions', {solutionId : id, parentUrl:'mymodel' });
														},
														function(error) {
															$scope.status = 'Unable to load data: '
																	+ error.data.error;
															console.log("Error: "+error.data);
															$state.go('marketSolutions', {solutionId : id, parentUrl:'mymodel'});
														});

									}
									$scope.updateViewCount();
								}
								
								$scope.showAll = function(){
									$state.go('marketPlace');
									$rootScope.relatedModelType = modelType;
									$rootScope.parentActive = 'marketplace'
								};

						
					/*	$scope.getCommentsJson = function(solutionName){
							console.log(solutionName);
							switch(solutionName){
							case "text analyzer":
								var comments = []; var commentsName = [];
								commentsName[0] = "Sam Kimberly";
								comments[0] = "I found this particular model to be very flexible—I’ve applied it to several different usage prediction problems to estimate resource utilization for planning purposes.";
								commentsName[1] = "Danielle Potarski";
								comments[1] = "I like the fact that the model trains itself as it goes.";
								commentsName[2] = "Wayne O’Keefe";
								comments[2] = "What technique is used to detect seasonality in the data? Or does it assume a fixed cycle period?";
								$scope.CommentName = comments;
								console.log($scope.CommentName);
								$scope.comment = comments;
								$scope.firstComment = "I found this particular model to be very flexible—I’ve applied it to several different usage prediction problems to estimate resource utilization for planning purposes.";
								$scope.second
							} 
						}*/
						
						//API call for related solution
						
							function relatedSoltion(){
								var dataObj = {
										"request_body" : {
											"modelType" : $scope.solution.modelType,
											"page" : 0,
											"size" : 6
										},
										"request_from" : "string",
										"request_id" : "string"
									}
								
								apiService
									.relatedSolutions(dataObj)
									.then(
											function(response) {
												
												$scope.relatedSolutions = response.data.response_body.content;
												if( ($scope.relatedSolutions).length ){
													angular.forEach($scope.relatedSolutions, function(solution, key) {
														if (solution.solutionId == $scope.solution.solutionId) {
															($scope.relatedSolutions).splice(key, 1);
														}
													});
												}
											},
											function(error) {
												console.log(error);
											});
							}
							
							$scope.scrolltoId = function(id){
								$location.hash(id);
								$anchorScroll();
							}
							
							$scope.disableEdit = function(){
								$scope.editModel = true;
								if($scope.solution.active == false){$scope.editModel = true;}
								else{
									if($scope.solution.ownerId != $scope.loginUserID){
										if($scope.solution.ownerListForSol != null){
											angular.forEach($scope.solution.ownerListForSol, function(owner, key) {
												if ($scope.loginUserID == owner.userId) {
													$scope.editModel = false;
												}
											
											});
											
										}else{ 
											$scope.editModel = true;
										} 
									}else{
										$scope.editModel = false;
									}
								}
							}
					}

				});
angular.module('angular-star-rating', []).directive('angularStarRating',
		angularStarRating);

function angularStarRating() {
	var directive = {

		restrict : 'EA',
		scope : {
			'value' : '=value',
			'active' : '=active',
			'max' : '=max',
			'hover' : '=hover',
			'isReadonly' : '=isReadonly'
		},
		link : linkFunc,
		template : '<span ng-class="{isReadonly: isReadonly}">'
				+ '<i ng-class="renderObj" '
				+ 'ng-repeat="renderObj in renderAry" '
				+ 'ng-click="!active||setValue($index)" '
				+ 'ng-mouseenter="!active||changeValue($index, changeOnHover )" >'
				+ '</i>' + '</span>',
		replace : true
	};
	return directive;
}

function linkFunc(scope, element, attrs, ctrl) {
	if (scope.max === undefined)
		scope.max = 5; // default
	console.log(scope.test);

	function renderValue() {
		scope.renderAry = [];
		for (var i = 0; i < scope.max; i++) {
			if (i < scope.value) {
				scope.renderAry.push({
					'fa fa-star fa-2x' : true
				});
			} else {
				scope.renderAry.push({
					'fa fa-star-o fa-2x' : true
				});
			}
		}
	}

	scope.setValue = function(index) {
		if (!scope.isReadonly && scope.isReadonly !== undefined) {
			scope.value = index + 1;
		}
	};

	scope.changeValue = function(index) {
		if (scope.hover) {
			scope.setValue(index);
		} else {
			// !scope.changeOnhover && scope.changeOnhover != undefined
		}
	};

	scope.$watch('value', function(newValue, oldValue) {
		if (newValue) {
			renderValue();
		}
	});
	scope.$watch('max', function(newValue, oldValue) {
		if (newValue) {
			renderValue();
		}
	});
}