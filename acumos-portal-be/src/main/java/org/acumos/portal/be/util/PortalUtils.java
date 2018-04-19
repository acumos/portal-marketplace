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

package org.acumos.portal.be.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.ValidationTypeCode;
import org.acumos.cds.domain.MLPAccessType;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionValidation;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.portal.be.transport.MLModelValidationStatus;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionDownload;
import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.MLUserNotifPref;
import org.acumos.portal.be.transport.OauthUser;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.transport.UserMasterObject;
import org.apache.commons.lang.ArrayUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PortalUtils {
	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(PortalUtils.class);
	
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	public PortalUtils() {
	}
	
	public static boolean isEmptyOrNullString(String input) {
		boolean isEmpty = false;
		if (null == input || 0 == input.trim().length()) {
			isEmpty = true;
		}
		return isEmpty;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isEmptyList(List input) {
		boolean isEmpty = false;
		if (null == input || 0 == input.size()) {
			isEmpty = true;
		}
		return isEmpty;
	}
	
	public static User convertToMLPuser(MLPUser mlpUser) {
        User user = new User();
        user.setFirstName(mlpUser.getFirstName());
        user.setLastName(mlpUser.getLastName());
        user.setEmailId(mlpUser.getEmail());
        String active = String.valueOf(mlpUser.isActive());
        user.setActive(active);
        user.setLastLogin(mlpUser.getLastLogin());
        user.setUsername(mlpUser.getLoginName());
        user.setCreated(mlpUser.getCreated());
        user.setUserId(mlpUser.getUserId());
        user.setJwttoken(mlpUser.getAuthToken());
        user.setPassword(mlpUser.getLoginHash());
        user.setPicture(ArrayUtils.toPrimitive((mlpUser.getPicture())));
        return user;
    }
	
	public static MLSolution convertToMLSolution(MLPSolution mlpSolution) {
		
		MLSolution mlSolution = new MLSolution();	
		mlSolution.setSolutionId(mlpSolution.getSolutionId());
		mlSolution.setName(mlpSolution.getName());
		mlSolution.setOwnerId(mlpSolution.getOwnerId());
		mlSolution.setCreated(mlpSolution.getCreated());
		mlSolution.setModified(mlpSolution.getModified());
		if(!PortalUtils.isEmptyOrNullString(mlpSolution.getAccessTypeCode()))
			mlSolution.setAccessType(mlpSolution.getAccessTypeCode());
		if(!PortalUtils.isEmptyOrNullString(mlpSolution.getAccessTypeCode()))
			mlSolution.setAccessType(mlpSolution.getAccessTypeCode());
		mlSolution.setActive(mlpSolution.isActive());
		mlSolution.setDescription(mlpSolution.getDescription());
		if(mlpSolution.getMetadata()!=null)
			mlSolution.setMetadata(mlpSolution.getMetadata());
		/*if(mlpSolution.getRevisions() != null && mlpSolution.getRevisions().size() > 0) {
			mlSolution.setRevisions(mlpSolution.getRevisions());
		}*/
		mlSolution.setTookitType(mlpSolution.getToolkitTypeCode());
		mlSolution.setModelType(mlpSolution.getModelTypeCode());
		mlSolution.setValidationStatusCode(mlpSolution.getValidationStatusCode());
		return mlSolution;
	}
	
	public static MLPSolution convertToMLPSolution(MLSolution mlSolution) {
		MLPSolution mlpSolution = new MLPSolution();

		if (!PortalUtils.isEmptyOrNullString(mlSolution.getSolutionId())) {
			mlpSolution.setSolutionId(mlSolution.getSolutionId());
		}
		if (!PortalUtils.isEmptyOrNullString(mlSolution.getName())) {
			mlpSolution.setName(mlSolution.getName());
		}
		if (!PortalUtils.isEmptyOrNullString(mlSolution.getOwnerId())) {
			mlpSolution.setOwnerId(mlSolution.getOwnerId());
		}
		if (mlSolution.getCreated() != null) {
			mlpSolution.setCreated(mlSolution.getCreated());
		}
		if (mlSolution.getModified() != null) {
			mlpSolution.setModified(mlSolution.getModified());
		}
		if (!PortalUtils.isEmptyOrNullString(mlSolution.getAccessType())) {
			mlpSolution.setAccessTypeCode(mlSolution.getAccessType());
		}

		mlpSolution.setActive(mlSolution.isActive());

		if (!PortalUtils.isEmptyOrNullString(mlSolution.getDescription())) {
			mlpSolution.setDescription(mlSolution.getDescription());
		}
		if (!PortalUtils.isEmptyOrNullString(mlSolution.getMetadata())) {
			mlpSolution.setMetadata(mlSolution.getMetadata());
		}
		/*
		 * if(mlpSolution.getRevisions() != null &&
		 * mlSolution.getRevisions().size() > 0) {
		 * mlpSolution.setRevisions(mlSolution.getRevisions()); }
		 */
		if (!PortalUtils.isEmptyOrNullString(mlSolution.getTookitType())) {
			mlpSolution.setToolkitTypeCode(mlSolution.getTookitType());
		}
		if (!PortalUtils.isEmptyOrNullString(mlSolution.getModelType())) {
			mlpSolution.setModelTypeCode(mlSolution.getModelType());
		}
		
		if(!isEmptyList(mlSolution.getSolutionTagList()))
		{
			//mlpSolution.set
		}

		return mlpSolution;
	}
	
	/**
	 * 
	 * @param accessType Access type value
	 * @return
	 * 		MLPAccessType object for Storing in DB
	 */
	public static MLPAccessType getMLPAccessType(String accessType) {
		MLPAccessType mlpAccessType = null;
		
		if(!isEmptyOrNullString(accessType)) {
			mlpAccessType = new MLPAccessType();
			if(accessType.equals("PB")) {
				
				mlpAccessType.setCode("PB");
				mlpAccessType.setName("Public");
			} else if(accessType.equals("OR")) {
				mlpAccessType.setCode("OR");
				mlpAccessType.setName("Organization");
			} else if(accessType.equals("PR")) {
				mlpAccessType.setCode("PR");
				mlpAccessType.setName("Private");
			} else {//Default
				mlpAccessType.setCode("PR");
				mlpAccessType.setName("Private");
			}
		} 
		return mlpAccessType;
	}
	
	public static MLRole convertToMLRole(MLPRole mlpRole) {
		MLRole mlRole = new MLRole();
		mlRole.setRoleId(mlpRole.getRoleId());
		mlRole.setName(mlpRole.getName());
		mlRole.setCreated(mlpRole.getCreated());
		mlRole.setModified(mlpRole.getModified());
		return mlRole;
	}
	
	public static MLPRole convertToMLPRole(MLRole mlRole) {
		MLPRole mlpRole = new MLPRole();
		mlpRole.setRoleId(mlRole.getRoleId());
		mlpRole.setName(mlRole.getName());
		mlpRole.setCreated(mlRole.getCreated());
		mlpRole.setModified(mlRole.getModified());
		return mlpRole;
	}

	public static MLRoleFunction convertToMLRoleFunction(MLPRoleFunction mlpRoleFunction) {
		MLRoleFunction mlRoleFunction = new MLRoleFunction();
		mlRoleFunction.setRoleFunctionId(mlpRoleFunction.getRoleFunctionId());
		mlRoleFunction.setName(mlpRoleFunction.getName());
		mlRoleFunction.setCreated(mlpRoleFunction.getCreated());
		mlRoleFunction.setModified(mlpRoleFunction.getModified());
		return mlRoleFunction;
	}

	public static MLPUser convertToMLPUserForUpdate(User user) {
		MLPUser mlpUser = new MLPUser();
		//private Byte[] picture;
		if (!PortalUtils.isEmptyOrNullString(user.getUserId())) {
			mlpUser.setUserId(user.getUserId());
		}
		if (!PortalUtils.isEmptyOrNullString(user.getFirstName())) {
			mlpUser.setFirstName(user.getFirstName());
		}
		if (!PortalUtils.isEmptyOrNullString(user.getLastName())) {
			mlpUser.setLastName(user.getLastName());
		}
		if (!PortalUtils.isEmptyOrNullString(user.getEmailId())) {
			mlpUser.setEmail(user.getEmailId());
		}
		if (user.getLastLogin() != null) {
			mlpUser.setLastLogin(user.getLastLogin());
		}
		
		if (!PortalUtils.isEmptyOrNullString(user.getUsername())) {
			mlpUser.setLoginName(user.getUsername());
		}
		if(!PortalUtils.isEmptyOrNullString(user.getJwttoken())){
			mlpUser.setAuthToken(user.getJwttoken());
		}
		//mlpUser.setActive(user.getActive());
		if(user.getActive().equals("Y"))
			mlpUser.setActive(true);
		else if(user.getActive().equals("N"))
			mlpUser.setActive(false);
			
		if (user.getOrgName() != null) {
            mlpUser.setOrgName(user.getOrgName());
        }
		if (user.getPicture() != null) {      
			mlpUser.setPicture(ArrayUtils.toObject((user.getPicture())));
			}
		return mlpUser;
	}
	
	public static OauthUser convertToOathUser(MLPUserLoginProvider mlpUserLoginProvider) {
		OauthUser oauthUser = new OauthUser();
		oauthUser.setUserId(mlpUserLoginProvider.getUserId());
		oauthUser.setProviderCd(mlpUserLoginProvider.getProviderCode());
		oauthUser.setProviderUserId(mlpUserLoginProvider.getProviderUserId());
		oauthUser.setRank(mlpUserLoginProvider.getRank());
		oauthUser.setDisplayName(mlpUserLoginProvider.getDisplayName());
		oauthUser.setProfileURL(mlpUserLoginProvider.getProfileUrl());
		oauthUser.setImageURL(mlpUserLoginProvider.getImageUrl());
		oauthUser.setSecret(mlpUserLoginProvider.getSecret());
		oauthUser.setAccessToken(mlpUserLoginProvider.getAccessToken());
		oauthUser.setRefreshToken(mlpUserLoginProvider.getRefreshToken());
		//Need to fix once we have this getter method
		//oauthUser.setExpireTime(mlpUserLoginProvider.getExpired());
		oauthUser.setCreatedDate(mlpUserLoginProvider.getCreated());
		oauthUser.setModifiedDate(mlpUserLoginProvider.getModified());
		
		return oauthUser;
	}
	
	public static User convertUserMasterIntoMLPUser(UserMasterObject userMaster) {
		User user = new User();
		user.setFirstName(userMaster.getFirstName());
		user.setLastName(userMaster.getLastName());
		user.setEmailId(userMaster.getEmailId());
		//user.setActive(userMaster.isActive());
		if(userMaster.isActive()==true)
			user.setActive("Y");
		else if(userMaster.isActive()==false)
			user.setActive("N");
		user.setLastLogin(userMaster.getLastLogin());
		user.setUsername(userMaster.getUsername());
		user.setCreated(userMaster.getCreated());
		user.setUserId(userMaster.getUserId());
		return user;
	}

	public static OauthUser convertUserMasterIntoOauthUser(UserMasterObject userMasterObject) {
		OauthUser oauthUser = new OauthUser();
		oauthUser.setUserId(userMasterObject.getUserId());
		oauthUser.setProviderCd(userMasterObject.getProviderCd());
		oauthUser.setProviderUserId(userMasterObject.getProviderUserId());
		oauthUser.setRank(userMasterObject.getRank());
		oauthUser.setDisplayName(userMasterObject.getDisplayName());
		oauthUser.setProfileURL(userMasterObject.getProfileURL());
		oauthUser.setImageURL(userMasterObject.getImageURL());
		oauthUser.setSecret(userMasterObject.getSecret());
		oauthUser.setAccessToken(userMasterObject.getAccessToken());
		oauthUser.setRefreshToken(userMasterObject.getRefreshToken());
		oauthUser.setExpireTime(userMasterObject.getExpireTime());
		oauthUser.setCreatedDate(userMasterObject.getCreated());
		oauthUser.setModifiedDate(userMasterObject.getModified());
		
		return oauthUser;
	}
	
	public static MLNotification convertToMLNotification(MLPNotification mlpNotification) {
		MLNotification mlNotification = new MLNotification();
		if (!PortalUtils.isEmptyOrNullString(mlpNotification.getNotificationId())) {
			mlNotification.setNotificationId(mlpNotification.getNotificationId());
		}
		if (!PortalUtils.isEmptyOrNullString(mlpNotification.getTitle())) {
			mlNotification.setTitle(mlpNotification.getTitle());
		}
		if (!PortalUtils.isEmptyOrNullString(mlpNotification.getMessage())) {
			mlNotification.setMessage(mlpNotification.getMessage());
		}
		if (!PortalUtils.isEmptyOrNullString(mlpNotification.getUrl())) {
			mlNotification.setUrl(mlpNotification.getUrl());
		}
		if (mlpNotification.getStart() != null) {
			mlNotification.setStart(mlpNotification.getStart());
		}
		if (mlpNotification.getEnd() != null) {
			mlNotification.setEnd(mlpNotification.getEnd());
		}
		return mlNotification;
	}
	
	
	public static MLSolutionRating convertToMLSolutionRating(MLPSolutionRating mlpSolutionRating){
		MLSolutionRating mlSolutionRating = new MLSolutionRating();
		if(!PortalUtils.isEmptyOrNullString(mlpSolutionRating.getSolutionId())){
			mlSolutionRating.setSolutionId(mlpSolutionRating.getSolutionId());
		}
		if(!PortalUtils.isEmptyOrNullString(mlpSolutionRating.getUserId())){
			mlSolutionRating.setUserId(mlpSolutionRating.getUserId());
		}
		if(mlpSolutionRating.getRating() != null){
			mlSolutionRating.setRating(mlpSolutionRating.getRating());
		}
		if(!PortalUtils.isEmptyOrNullString(mlpSolutionRating.getTextReview())){
			mlSolutionRating.setTextReview(mlpSolutionRating.getTextReview());
		}
		if(mlpSolutionRating.getCreated() != null){
			mlSolutionRating.setCreated(mlpSolutionRating.getCreated());
		}
		return mlSolutionRating;
		
	}

	public static MLSolutionFavorite convertToMLSolutionFavorite(MLPSolutionFavorite mlpSolutionFavorite) {
		MLSolutionFavorite mlSolutionFavorite = new MLSolutionFavorite();
		if(!PortalUtils.isEmptyOrNullString(mlpSolutionFavorite.getSolutionId())){
			mlSolutionFavorite.setSolutionID(mlpSolutionFavorite.getSolutionId());
		}
		if(!PortalUtils.isEmptyOrNullString(mlpSolutionFavorite.getUserId())){
			mlSolutionFavorite.setUserID(mlpSolutionFavorite.getUserId());
		}
		
		return mlSolutionFavorite;
	}
	public static MLSolutionDownload convertToMLSolutionDownload(MLPSolutionDownload mlpSolutionDownload) {
		MLSolutionDownload mlSolutionDownload = new MLSolutionDownload();
		if(!PortalUtils.isEmptyOrNullString(mlpSolutionDownload.getSolutionId())){
			mlSolutionDownload.setSolutionId(mlpSolutionDownload.getSolutionId());
		}
		if(mlpSolutionDownload.getDownloadDate() != null){
			mlSolutionDownload.setDownloadDate(mlpSolutionDownload.getDownloadDate());
		}
		if(!PortalUtils.isEmptyOrNullString(mlpSolutionDownload.getUserId())){
			mlSolutionDownload.setUserId(mlpSolutionDownload.getUserId());
		}
		
		return mlSolutionDownload;
		
	}
	
	public static MLPSolutionValidation convertMLPSolutionValidation(MLModelValidationStatus mlModelValidationStatus) {
		MLPSolutionValidation mlpSolutionValidation = new MLPSolutionValidation();
		mlpSolutionValidation.setSolutionId(mlModelValidationStatus.getSolutionId());
		mlpSolutionValidation.setRevisionId(mlModelValidationStatus.getRevisionId());
		mlpSolutionValidation.setTaskId(mlModelValidationStatus.getTaskId());
		String valStatus = mlModelValidationStatus.getStatus();
		if(!isEmptyOrNullString(valStatus)) {
			if(valStatus.equalsIgnoreCase("Success") || valStatus.equalsIgnoreCase("Pass") || valStatus.equalsIgnoreCase("PS")) {
				mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.PS.toString());
			} else if(valStatus.equalsIgnoreCase("In Progress") || valStatus.equalsIgnoreCase("IP") || valStatus.contains("Pending")) {
				mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.IP.toString());
			} else if(valStatus.equalsIgnoreCase("Failed") || valStatus.equalsIgnoreCase("FA")) {
				mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.FA.toString());
			} else {
				mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.NV.toString());
			}
		} else {
			mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.SB.toString());
		}
		mlpSolutionValidation.setValidationTypeCode(ValidationTypeCode.SS.toString());
		if(!isEmptyList(mlModelValidationStatus.getArtifactValidationStatus())) {
			mlpSolutionValidation.setDetail(JsonUtils.serializer().toString(mlModelValidationStatus.getArtifactValidationStatus()));
		}
		
		log.debug(EELFLoggerDelegate.debugLogger, "convertMLPSolutionValidation ={}", JsonUtils.serializer().toPrettyString(mlpSolutionValidation));
		return mlpSolutionValidation;
	}
	
	

	
      public static MLPSiteConfig convertMLSiteConfigToMLPSiteConfig(MLPSiteConfig mlpSiteConfig){
		MLPSiteConfig mlSiteConfig = new MLPSiteConfig();
		mlSiteConfig.setConfigKey(mlpSiteConfig.getConfigKey());
		mlSiteConfig.setConfigValue(JsonUtils.serializer().toString(mlpSiteConfig.getConfigValue()));
		mlSiteConfig.setUserId(mlpSiteConfig.getUserId());
		return mlSiteConfig;
		
	}
      
      public static String convertStreamToString(InputStream is) {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					
				}
			}
			return sb.toString();
      }
	
      public static MLStepResult convertToMLStepResult(MLPStepResult mlpStepResult){
    	  
    	MLStepResult mlStepResult = new MLStepResult();
    	mlStepResult.setArtifactId(mlpStepResult.getArtifactId());
    	mlStepResult.setName(mlpStepResult.getName());
    	mlStepResult.setResult(mlpStepResult.getResult());
    	mlStepResult.setRevisionId(mlpStepResult.getRevisionId());
    	mlStepResult.setSolutionId(mlpStepResult.getSolutionId());
    	mlStepResult.setStatusCode(mlpStepResult.getStatusCode());
    	mlStepResult.setStepCode(mlpStepResult.getStepCode());
    	mlStepResult.setStepResultId(mlpStepResult.getStepResultId());
    	mlStepResult.setTrackingId(mlpStepResult.getTrackingId());
    	mlStepResult.setUserId(mlpStepResult.getUserId());    	
    	
  		return mlStepResult;
  		
  	}
      
    public static String mapToJsonString(Map<String, ?> theMap) {

  		try {
  			return objectMapper.writeValueAsString(theMap);
  		} catch (JsonProcessingException x) {
  			throw new IllegalArgumentException("Failed to convert", x);
  		}
  	}
    
    public static Map<String, Object> jsonStringToMap(String jsonString) {
		Map<String, Object> map = new HashMap<>();

		if (!isEmptyOrNullString(jsonString)) {
			try {
				map = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
				});
			} catch (IOException x) {
				throw new IllegalArgumentException("Argument not a map", x);
			}
		}
		return map;
	}
    
	public static MLUserNotifPref convertToMLUserNotifPref(MLPUserNotifPref mlpUserNotifPref) {
		MLUserNotifPref mlUserNotifPref = new MLUserNotifPref();
		mlUserNotifPref.setUserNotifPrefId(mlpUserNotifPref.getUserNotifPrefId());
		mlUserNotifPref.setUserId(mlpUserNotifPref.getUserId());
		mlUserNotifPref.setNotfDelvMechCode(mlpUserNotifPref.getNotfDelvMechCode());
		mlUserNotifPref.setMsgSeverityCode(mlpUserNotifPref.getMsgSeverityCode());
		return mlUserNotifPref;
	}
	
	public static MLPUserNotifPref convertToMLPUserNotifPref(MLUserNotifPref mlUserNotifPref) {
		MLPUserNotifPref mlpUserNotifPref = new MLPUserNotifPref();
		if (mlUserNotifPref.getUserNotifPrefId() != null) {
			mlpUserNotifPref.setUserNotifPrefId(mlUserNotifPref.getUserNotifPrefId());
		}
		mlpUserNotifPref.setUserId(mlUserNotifPref.getUserId());
		mlpUserNotifPref.setNotfDelvMechCode(mlUserNotifPref.getNotfDelvMechCode());
		mlpUserNotifPref.setMsgSeverityCode(mlUserNotifPref.getMsgSeverityCode());
		return mlpUserNotifPref;
	}

    
}
