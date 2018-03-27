angular.module('AcumosApp')
    .service('apiService', ['$http', function ($http, $window) {

        var urlModelTypes = '/api/filter/modeltype';
        var urlAllUserCount = '/api/users/userDetails';
        var urlAllRole = '/api/roles';
        var urlToolkitTypes = '/api/filter/toolkitType';  
        var urlSolutions = '/api/solutions';
        var urlPortalSolutions = '/api/portal/solutions';
        var urlPublishSolution = '/api/publish';
       	var urlSignUp = 'api/users/register';
        var urlSocialSignUp = 'api/oauth/login/register';
        var urlSignIn = 'api/auth/login';//'api/auth/jwtToken';
        var urlJwtAuth = 'api/auth/jwtToken';
        var urlSocialSignIn = 'api/oauth/login';
        var urlCasSignIn = 'api/cas/serviceValidate';
        var urlChangePass = 'api/users/changePassword'
        var urlPutSolutions = '/api/solutions';
        var urlPutPublishSolution = '/api/publish';
        var urlAddTag=  'api/addTag';
        var urlDeleteTag = 'api/dropTag';
        var urlGetAllTag = 'api/tags';
        var urlGetAllPeers = 'api/admin/paginatedPeers';
        var urlPeers = 'api/admin/peers';
        var urlUserDetails = 'api/users/userDetails';
        var urlAllModels = 'api/models';
        
        var urlPostGlobalLogin = '/globalLogin';
        var updateUser = '/api/users/updateUser';
        var updateUserRole = '/api/roles/updateRole';
        var updateUserRoleNew = '/api/roles/user/change';
        var urlShareWithTeam = 'api/solution/userAccess';
        var urlUserRoles = 'api/users/userRole';
        var urlGetAllUsers = 'api/users/userDetails';
        
        var urlUpdateViewCount = 'api/solution/updateViewCount';
        var urlAuthAndDeployToAzure='/azure/authAndpushimage';//'http://localhost:9081/azure/authAndpushimage';
        var urlqandAUrl = 'api/users/qAUrl';
        var docUrl = 'api/users/docs';
        var dashboardUrl = 'api/admin/dashboard';
        var urlmodelFileUpload = 'api/model/upload';
        var urlTermsCondition = '/site/api-manual/Solution/global/termsCondition';
        var urlDownloadArtifact = '/api/downloads';
        var urlModelerResourcesContent = '/site/api-manual/Solution/global/modelerresource';
        var urladdToCatalog = 'api/webBasedOnBoarding/addToCatalog';
        var urlCreateRating = 'api/solution/createRating';
        var urlUpdateRating = 'api/solution/updateRating';
        var getRelatedMySolutions = 'api/getRelatedMySolutions';
        var urlCreateFavorite = 'api/solution/createFavorite';
        var urlDeleteFavorite = 'api/solution/deleteFavorite';
        var urlFavoriteSolution = 'api/solution/getFavoriteSolutions';
        var urlNotification = 'api/notifications';
        var urlSolutionImages = '/site/api-manual/Solution/solutionImages';
        var urlValidationStatus = 'api/validation';
        var urlCreateRole = 'api/createRole';
        var urlSiteConfig = 'api/admin/config';
        var urlCreateTags = 'api/tags/create'
        var urlSolutionDescription = '/site/api-manual/Solution/description';
        var urlAddRole =  'api/roles/add';
        var urlAddPeer = 'api/admin/peer/subcription/create';
        /*var urldeleteUser = '/api/users/user/updateUser';*////user/deleteUser
        var urldeleteUser = '/api/users/user/deleteUser';
        var rolesCount = '/api/roles/count';
        var urlSolutionsCount = "api/solutions/count";
        var urlSearchSolution = "api/searchSolutions";
        var urlGetVersion	= "api/admin/version";
		 var urlComment = "api/comments";
        var urlThread = "api/thread";
        var urlUserProfileImage = "api/users/userProfileImage";
        var urlUserAccountDetails = "api/users/userAccountDetails";
        var urlDeleteSub = 'api/admin/peer/subcription/delete/';
        var urlGetActiveUsers = "api/users/activeUserDetails";
        var urlMessagingStatus = "api/webBasedOnBoarding/messagingStatus";
        var urladdAllSolutionsAdmin = "api/admin/peer/sub/create";
        var urlMessageStatusWithTrackingId = 'api/webBasedOnBoarding/messagingStatus/search/';
        var urlForONAP = 'api/webBasedOnBoarding/convertToOnap';
        var urlCasEnable = 'api/cas/enabled';
        	
        /**************** ALL GET ******************/
    	this.getAllActiveUser = function (activeStatus) {
            return $http.get(urlGetActiveUsers + '/' + activeStatus);
        };
        
        this.getRoleCount = function () {
            return $http.get(rolesCount);
        };
        
        this.getSolutionDescription = function(publicOrOrg, solutionId, revisionId){
        	return $http.get(urlSolutionDescription + '/' +publicOrOrg  + '/'+solutionId + '/' +revisionId)
        };
        
        this.getSolutionImage = function(solutionId){
        	return $http.get(urlSolutionImages + '/'+solutionId)
        }
        
        this.getModelTypes = function () {
            return $http.get(urlModelTypes);
        };
        
        this.getAllUserCount = function () {
            return $http.get(urlAllUserCount);
        };
        
        this.getAllRole = function () {
            return $http.get(urlAllRole);
        };
        
        this.getToolkitTypes = function () {
            return $http.get(urlToolkitTypes);
        };
        
        this.getSolutionDetail = function (solutionId) {
            return $http.get(urlSolutions + '/' + solutionId);
        };
        
        this.getPeer = function(peerId){
        	return $http.get(urlPeers + '/' + peerId);
        };
        
        this.getShareWithTeam = function (solutionId) {
            return $http.get(urlShareWithTeam + '/' + solutionId);
        };
        
        this.getAllUsers = function(){
        	return $http.get(urlUserDetails);
        };
        
        this.getUserRole = function(userId){
        	return $http.get(urlUserRoles + '/' + userId);
        };
        
        this.getAllUsersLists = function () {
            return $http.get(urlGetAllUsers);
        };
        
        this.getQandAUrl = function () {
            return $http.get(urlqandAUrl);
        };
        
        this.getDocUrl = function () {
            return $http.get(docUrl);
        };
        
        this.getDashboardUrl = function () {
            return $http.get(dashboardUrl);
        };
        
        this.downloadPopupValue = function (solutionId, revisionId){
        	return $http.get(urlSolutions + '/' + solutionId + '/revisions/' + revisionId);
        };
        
        this.download = function (solutionId, artifactId, revisionId){
        	return $http.get(urlDownloadArtifact + '/' + solutionId + '?artifactId=' + artifactId + '&revisionId=' + revisionId);
        };
        
        this.getTermsCondition = function (){
        	return $http.get(urlTermsCondition);
        };
        
        this.getModelerResourcesContent = function (modelName){
        	return $http.get(urlModelerResourcesContent + '/'+ modelName);
        };
        
        
        this.getFavoriteSolutions = function (userId){
        	return $http.get(urlFavoriteSolution + '/'+ userId);
        };
        
        this.getNotification = function(userId, reqObj){
        	return $http.post(urlNotification + '/' + userId, reqObj);
        };
        
        this.getModelValidationStatus = function(solutionId, revisionId){
        	return $http.get(urlValidationStatus + '/'+ solutionId + '/' + revisionId);
        }
        
        this.getSiteConfig = function(configKey){
        	return $http.get(urlSiteConfig + '/' + configKey);
        };
        
        this.updateUserRole = function(userDetails){
        	return $http.post(updateUserRole, userDetails);
        }
        
        this.updateUserRoleNew = function(userDetails){
        	return $http.put(updateUserRoleNew, userDetails);
        }
        
        this.getSolutionsCount = function(userID){
        	return $http.get(urlSolutionsCount + '/' + userID);
        }
        
        this.getVersion = function(){
        	return $http.get(urlGetVersion);
        }
		
		this.getUserProfileImage = function(userID){
            return $http.get(urlUserProfileImage + '/' + userID);
        }
        
		this.casSignIn = function (ticketId) {
	        return $http.get(urlCasSignIn + '?ticket=' + ticketId + '&service=' + window.location.origin);
	    }
		 
        this.getCasEnable = function () {
            return $http.get(urlCasEnable);
        }
		 
                
        /**************** ALL PUT ******************/
        this.updateSolutions = function(solution){
        	return $http.put(urlSolutions + '/' + solution.request_body.solutionId, solution);
        }
        
        this.updatePublishSolution = function(solutionId, data){
        	return $http.put(urlPublishSolution + '/' + solutionId + '?' + data);
        }
        
        this.updateAddTag = function(solutionid, tag){
        	return $http.put(urlAddTag + '/' + solutionid + '/tag/' + tag);
        }
        
        this.getAllTag = function(tagList){
        	return $http.put(urlGetAllTag, tagList);
        }
        
        this.editPeer = function(peerId, peer){
        	if(!peerId)
        		return $http.post(urlPeers, peer);
        	else
        		return $http.put(urlPeers + '/' + peerId, peer);
        }
        
        this.deactivatePeer = function(peerId, peer){
    		return $http.put(urlPeers + '/' + peerId, peer);
        }
        
        this.updateViewCount = function(solutionId){
        	return $http.put(urlUpdateViewCount +'/'+ solutionId);
        }
        
        this.updateUserPass = function(userDetails){
        	return $http.put(urlChangePass, userDetails);
        }
        
        this.updateRatingSolution = function(reqObj){
        	return $http.put(urlUpdateRating, reqObj);
        };
        
        this.markReadNotifications = function (notificationId, userId) {
            return $http.put(urlNotification + '/view/'  + notificationId + '/user/' + userId);
        };
        
        this.updateSiteConfig = function(configKey, reqObj){
        	return $http.put(urlSiteConfig + '/' + configKey, reqObj);
        };
        
		 this.updateComment = function(dataObj){
        	return $http.put(urlComment + '/update', dataObj);
        };
		
        /**************** ALL POST ******************/
        this.postGlobalUserDetails = function(){
        	return $http.put(urlPostGlobalLogin);
        }
        
        this.updateUser = function(){
        	return $http.put(updateUser);
        }
        
        this.insertSignUp = function (userDetails) {
            return $http.post(urlSignUp, userDetails);
        };
        
        this.insertSocialSignUp = function (oauthDetails) {
            return $http.post(urlSocialSignUp, oauthDetails);
        };
        
        this.insertSignIn = function (userDetails) {
            return $http.post(urlSignIn, userDetails);
        };
        
        this.getJwtAuth = function (userDetails) {
            return $http.post(urlJwtAuth, userDetails);
        };
        
        this.insertSocialSignIn = function (oauthDetails) {
            return $http.post(urlSocialSignIn, oauthDetails);
        };
        
        this.insertSolutionDetail = function (solutionDetails) {
            return $http.post(urlPortalSolutions, solutionDetails);
        };
        
        this.getPeers = function (dataObj) {
        	return $http.post(urlGetAllPeers, dataObj);
        };
        
        this.insertPeers = function (dataObj) {
            return $http.post(urlPeers, dataObj);
        };
        
        this.insertShareWithTeam = function (solutionId, userId) {
            return $http.post(urlShareWithTeam + '/' + solutionId + '/add/' + userId);
        };
        
        this.authenticateAnddeployToAzure = function (authDeployObject) {
            return $http.post(urlAuthAndDeployToAzure,authDeployObject);
        };
        
        this.getAllModels = function (reqObj, userId){
        	return $http.post(urlAllModels + '/' + userId, reqObj);
        };
        
        this.insertmodelFileUpload = function (userId, file){
        	return $http.post(urlmodelFileUpload + '/' + userId);
        };
        
        this.postAddToCatalog = function(userId, reqObj){
        	return $http.post(urladdToCatalog+ '/' + userId, reqObj);
        };
        
        this.createRatingSolution = function(reqObj){
        	return $http.post(urlCreateRating, reqObj);
        };
        

        this.insertMultipleShare = function(solutionId, reqSharedWith){
        	return $http.post(urlShareWithTeam + '/' + solutionId + '/add', reqSharedWith);
        }

        this.relatedSolutions = function (relatedSolutions) {
            return $http.post(getRelatedMySolutions, relatedSolutions);
        };
        
        this.createFavorite = function (dataObj) {
            return $http.post(urlCreateFavorite, dataObj);
        };
        
        this.urlCreateRole = function (dataObj) {
            return $http.post(urlCreateRole, dataObj);
        };
        
        this.createTags = function (dataObj) {
            return $http.post(urlCreateTags, dataObj);
        };

        this.createComment = function(dataObj){
        	return $http.post(urlComment + '/create', dataObj);
        };
        
        this.createThread = function(dataObj){
        	return $http.post(urlThread + '/create', dataObj);
        };
        
        this.deleteComment = function(threadId, commentId){
        	return $http.post(urlComment + '/delete/' + threadId + '/comment/' + commentId);
        };
        
        this.getComment = function(solutionId, revisionId, dataObj){
        	return $http.post(urlThread + '/' + solutionId + '/' + revisionId + '/comments', dataObj);
        };
        
        this.getUserAccountDetails = function(userID){
        	return $http.post(urlUserAccountDetails, userID);
        }
        
        this.insertAddAllSolutions = function(peeriId, addSolObj){
        	return $http.post(urladdAllSolutionsAdmin + '/' + peeriId , addSolObj);
        }
        
        
        /**************** ALL DELETE ******************/
        this.deleteTag = function(solutionid, tag){
        	return $http.delete(urlDeleteTag + '/' + solutionid + '/tag/' + tag);
        }
        
        this.deletePeer = function(peerId){
        	return $http.delete(urlPeers + '/' + peerId);
        }
        
        this.deleteShareWithTeam = function (solutionId, userId) {
            return $http.delete(urlShareWithTeam + '/' + solutionId + '/delete/' + userId);
        };
        
        this.deleteNotifications = function (notificationId, userId) {
            return $http.delete(urlNotification + '/drop/'  + notificationId + '/user/' + userId);
        };
        
        this.deleteFavorite = function (dataObj) {
            return $http.post(urlDeleteFavorite, dataObj);
        };
        
        this.deleteSubscription = function (dataObj) {
            return $http.delete(urlDeleteSub +'/'+ dataObj);
        };
        /**************** Google API ******************/
        this.getGoogleUserProfile = function(accessToken) {
        	return $http.get("https://www.googleapis.com/plus/v1/people/me",{
        	       "Authorization" : "Bearer " + accessToken
            });
        };
        
        this.getGoogleTokenInfo = function(accessToken) {
        	return $http.get("https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken);
        };
        

        /**************** Github API ******************/
        this.getGithubAccessToken = function(code) {
        	return $http.get("https://cors-anywhere.herokuapp.com/https://github.com/login/oauth/access_token?client_id=1587275085f20e9a68bc&client_secret=3323c7ba07c0328bb838296d1a3cab95c0c3f33a&code="+code, {
        	    headers: {'Access-Control-Allow-Origin': '*',
        	    		'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept'
        	    	
        	    	},
        	    	mode: 'no-cors',
        	    	cache: 'default' 
        	    }
        	);
        };
        
        this.getGithubUserProfile = function(accessToken) {
        	return $http.get("https://api.github.com/user?" + accessToken);
        };
        
        this.addUser = function(reqObj/*, role*/){
        	return $http.post(urlAddRole /*+ '/' + role*/, reqObj);
        };
        
        this.addPeer = function(reqObj){
        	return $http.post(urlAddPeer, reqObj);
        };
        
        this.deleteUser = function(reqObj){
        	return $http.post(urldeleteUser, reqObj);
        };
        
        this.getSearchSolution = function(search){
            return $http.get(urlSearchSolution + '?search=' + search);
        };
        
        this.getMessagingStatus = function(userId, trackingId){
            return $http.post(urlMessagingStatus + '/' + userId + '/' + trackingId);
        };
        
        this.getMessagingStatusBySolutionId = function(solutionId, revisionId){
        	return $http.get(urlMessageStatusWithTrackingId + '/' + solutionId + '/' +revisionId);
        };
        
        this.addToCatalogONAP = function(solutionId, revisionId, userId, modelName){
        	return $http.post(urlForONAP+ '/' + solutionId + '/' + revisionId + '/' + userId + '/' + modelName);
        };
        
    }]);