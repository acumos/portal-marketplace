/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.portal.be;

 
/**
 *
 * Constants class to list all the REST API endpoints
 */
public class APINames {

	// charset
    public static final String CHARSET = "application/json;charset=utf-8";

    //Auth Service APIs 
    public static final String AUTH = "/auth"; 
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";

    //User Service APIs
    public static final String USERS = "/users";
    public static final String ACCOUNT_SIGNUP = "/register";
    public static final String ACCOUNT_DELETE = "/delete";
    public static final String RESET_PASSWORD = "/resetPassword";
    public static final String CHANGE_PASSWORD = "/changePassword";
    public static final String SAVE_PASSWORD = "/savePassword";
    public static final String UPADATE_USER = "/updateUser";
    public static final String FORGET_PASSWORD = "/forgetPassword"; 
    public static final String USER_ACCOUNT_DETAILS = "/userAccountDetails";
    public static final String USER_DETAILS = "/userDetails";
    public static final String ACTIVE_USER_DETAILS = "/activeUserDetails/{active}";
    public static final String USER_ROLE_DETAILS = "/userRole/{userId}";
    public static final String UPDATE_USER_IMAGE = "/updateUserImage/{userId}";
    public static final String UPADATE_BULK_USER = "/user/updateUser";
    public static final String DELETE_BULK_USER = "/user/deleteUser";

	public static final String USER_PROFILE_PIC = "/userProfileImage/{userId}";
    
    // OAUTH2
    public static final String OAUTH_LOGIN = "oauth/login";
    public static final String OAUTH_IMPLICIT_LOGIN = "oauth/implicit/login";
    public static final String CHECK_API_KEY = "oauth";    
    
    //Solutions APIs for MarketPlace Catalog
    public static final String SOLUTIONS = "/solutions";
    public static final String SOLUTIONS_DETAILS = "/solutions/{solutionId}";
    public static final String PAGINATED_SOLUTIONS = "/paginatedSolutions";
    public static final String SOLUTIONS_UPDATE = "/solutions/{solutionId}";
    
    public static final String SOLUTIONS_REVISIONS = "/solutions/{solutionId}/revisions";
    public static final String SOLUTIONS_REVISIONS_ARTIFACTS = "/solutions/{solutionId}/revisions/{revisionId}";
    
    public static final String ARTIFACT = "/artifacts";
    public static final String ARTIFACT_DETAILS = "/artifacts/{artifactId}";
    
    public static final String FILTER = "/filter";
    public static final String FILTER_MODELTYPE = "/modeltype";
    public static final String FILTER_ACCESSTYPE = "/accesstype";
    public static final String FILTER_MODELTYPE_CODE = "/modeltype/{categoryTypeCode}";
    public static final String FILTER_TOOLKITTYPE = "/toolkitType";
    
    public static final String SOLUTION_USER_ACCESS = "solution/userAccess/{solutionId}";
    public static final String SOLUTION_USER_ACCESS_ADD = "solution/userAccess/{solutionId}/add";
    public static final String SOLUTION_USER_ACCESS_DELETE = "solution/userAccess/{solutionId}/delete/{userId}";
    public static final String UPDATE_VIEW_COUNT = "solution/updateViewCount/{solutionId}";
    public static final String UPDATE_RATING = "solution/updateRating";
    public static final String CREATE_RATING = "solution/createRating";
    public static final String SHARED_MODELS_FOR_USER = "solutions/shared/{userId}";
    public static final String CREATE_FAVORITE = "solution/createFavorite";
    public static final String DELETE_FAVORITE = "solution/deleteFavorite";
    public static final String USER_FAVORITE_SOLUTIONS = "solution/getFavoriteSolutions/{userId}";
    public static final String GET_SOLUTION_RATING = "solution/getRating/{solutionId}";
    public static final String CREATE_TAG = "tags/create";      
    public static final String SOLUTIONS_COUNT = "/solutions/count/{userId}";
    public static final String GET_SOLUTION_RATING_USER = "/solutions/ratings/{solutionId}/user/{userId}";
    public static final String PORTAL_SOLUTIONS = "/portal/solutions";
    public static final String USER_ACCESS_SOLUTIONS = "userAccess/solution/{userId}";
    public static final String GET_AVG_SOLUTION_RATING = "solution/avgRating/{solutionId}";
    
    //Tags APIs for MarketPlace Catalog
    public static final String TAGS = "/tags";
    public static final String ADD_TAG = "/addTag/{solutionId}/tag/{tag}";
    public static final String DROP_TAG = "/dropTag/{solutionId}/tag/{tag}";
    public static final String SEARCH_SOLUTION_TAGS = "/searchSolutionsTags/{tags}";
    public static final String ADD_TO_CATALOG = "/addToCatalog/{userId}";
    //Will be used when search condition is applied
    public static final String SEARCH_SOLUTION = "/searchSolutions";
    
    
    //Will be used when Filters are applied
    public static final String SOLUTIONS_BY_CATEGORY = "/catgeory";
    
    //publish APIs for Catalog Solutions
    public static final String PUBLISH = "/publish/{solutionId}";
    public static final String UNPUBLISH = "/unpublish/{solutionId}";
    
    public static final String MODEL_VALIDATION = "/validation/{solutionId}/{revisionId}";
    public static final String MODEL_VALIDATION_UPDATE = "/validation/{taskId}";
    
    public static final String MANAGE_MY_SOLUTIONS = "/models/{userId}";
    
    //Solutions APIs for MarketPlace Catalog
    public static final String DOWNLOADS = "/downloads";
    public static final String DOWNLOADS_SOLUTIONS = "/downloads/{solutionId}";
    public static final String UPLOAD_USER_MODEL = "/model/upload/{userId}";
     
    public static final String JWTTOKEN = "/jwtToken";
    public static final String JWTTOKENVALIDATION = "/validateToken";
    
    //Role APIs for MarketPlace user
    public static final String ROLES = "/roles";
    public static final String ROLES_DEATAILS = "/roles/{roleId}"; 
    public static final String CREATE_ROLE = "/createRole";
    public static final String UPDATE_ROLE = "/updateRole/{roleId}";
    public static final String DELETE_ROLE = "/deleteRole/{roleId}";
    public static final String UPDATE_ROLES_USER = "/roles/updateRole";
    public static final String USER_ROLES = "/roles/{userId}"; 
    public static final String USER_ROLE_COUNT = "/roleCounts";
    public static final String ROLES_COUNT = "/roles/count";
    public static final String CHANGE_ROLES_USER = "/roles/user/change";
    
    public static final String ROLE_FUNCTION_DETAILS = "/roleFunction/{roleId}/{roleFunctionId}";
    public static final String CREATE_ROLE_FUNCTION = "/createRoleFunction";
    public static final String UPDATE_ROLE_FUNCTION = "/updateRoleFunction";
    public static final String DELETE_ROLE_FUNCTION = "/deleteRoleFunction/{roleId}/{roleFunctionId}";
    
    //Peer APIs for Admin
    public static final String ADMIN = "/admin";
    public static final String PEERS_PAGINATED = "/paginatedPeers";
    public static final String PEERS = "/peers";
    public static final String PEER_DETAILS = "/peers/{peerId}";
    
    //Peer subscription API for Admin
    public static final String PEERSUBSCRIPTION_PAGINATED = "/peer/subcriptions/{peerId}";
    public static final String SUBSCRIPTION_DETAILS = "/peer/subcription/{subId}";
    public static final String SUBSCRIPTION_CREATE = "/peer/subcription/create";
    public static final String SUBSCRIPTION_UPDATE = "/peer/subcription/update";
    public static final String SUBSCRIPTION_DELETE = "/peer/subcription/delete/{subId}";
    public static final String CREATE_SUBSCREPTION = "/peer/sub/create/{peerId}";
    
    //Site Configuration API's for Admin
    
    public static final String GET_SITE_CONFIG = "/config/{configKey}";
    public static final String UPDATE_SITE_CONFIG = "/config/{configKey}";
    public static final String CREATE_SITE_CONFIG = "/config";
    public static final String DELETE_SITE_CONFIG = "/config/{configKey}";
    
    //Request API's for Admin
    public static final String GET_REQUESTS = "/requests";
    public static final String UPDATE_REQUEST = "/request/update";

    public static final String ADD_USER = "/addUser";

    // Notifications
    public static final String CREATE_NOTIFICATION = "/notifications/createNotification";
    public static final String ADD_USER_NOTIFICATIONS = "/notifications/create/{notificationId}/user/{userId}";
    public static final String NOTIFICATIONS = "/notifications/notifications";
    public static final String USER_NOTIFICATIONS = "/notifications/{userId}";
    public static final String MAIL_NOTIFICATIONS = "/notifications/{user}/{subject}/{template}";
    public static final String VIEW_USER_NOTIFICATIONS= "/notifications/view/{notificationId}/user/{userId}";
    public static final String NOTIFICATIONS_COUNT = "/notifications/count";
    public static final String DELETE_NOTIFICATIONS = "/notifications/delete/{notificationId}";
    public static final String DROP_USER_NOTIFICATIONS= "/notifications/drop/{notificationId}/user/{userId}";

	public static final String RELATED_MY_SOLUTIONS = "/getRelatedMySolutions";
	public static final String READ_SIGNATURE_TAB = "/readArtifactSolutions/{artifactId}"; 

    public static final String USER_NOTIFICATION_PREF = "/notifications/pref/byUserId/{userId}"; 
    public static final String CREATE_NOTIFICATION_PREFERENCES = "/notifications/pref/create";
    public static final String UPDATE_NOTIFICATION_PREFERENCES = "/notifications/pref/update";

	//Comments
	   public static final String CREATE_COMMENT = "comments/create";
	   public static final String UPDATE_COMMENT = "comments/update";
	   public static final String DELETE_COMMENT = "comments/delete/{threadId}/{commentId}";
	   public static final String GET_COMMENT = "comments/{threadId}/{commentId}";
	   public static final String GET_COMMENT_SOLUTIONREVISION = "thread/{solutionId}/{revisionId}/comments";
	//Thread
	   public static final String CREATE_THREAD = "thread/create";
	   public static final String UPDATE_THREAD = "thread/update";
	   public static final String DELETE_THREAD = "thread/delete/{threadId}";
	   public static final String GET_THREAD = "thread/{threadId}";
	   public static final String GET_THREADS = "thread";
	   public static final String GET_THREAD_SOLUTIONREVISION = "thread/{solutionId}/{revisionId}";
	   public static final String GET_THREAD_COMMENTS = "thread/{threadId}/comment";
	   public static final String GET_THREADCOUNT = "thread/count";
	   public static final String GET_THREADCOMMENTSCOUNT = "thread/{threadId}/comments/count";
	   
	//HealthCheck
	   
	   public static final String GET_VERSION = "/version";
	   
	   public static final String MESSAGING_STATUS = "/messagingStatus/{userId}/{trackingId}";
	   public static final String CREATE_STEP_RESULT = "/stepResult/create";
	   public static final String UPDATE_STEP_RESULT = "/stepResult/update";
	   public static final String DELETE_STEP_RESULT = "/stepResult/delete/{stepResultId}";
	   public static final String GET_STEP_STATUSES = "/stepStatuses";
	   public static final String GET_STEP_TYPES = "/stepTypes";
	   public static final String SEARCH_STEP_RESULT = "/messagingStatus/search/{solutionId}/{revisionId}";
	   
	   public static final String CONVERT_TO_ONAP = "/convertToOnap/{solutionId}/{revisionId}/{userId}/{modName}";
	   
	   public static final String BROKER = "/broker";
	   
	   public static final String GATEWAY = "/gateway";
	   public static final String PING = "/ping/{peerId}";
	   
	   public static final String CAS = "/cas";
}
