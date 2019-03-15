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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.transport.AuthorTransport;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.Author;
import org.acumos.portal.be.transport.MLComment;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLPublishRequest;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionDownload;
import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.MLTask;
import org.acumos.portal.be.transport.MLUserNotifPref;
import org.acumos.portal.be.transport.OauthUser;
import org.acumos.portal.be.transport.RevisionDescription;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.transport.UserMasterObject;
import org.joda.time.DateTime;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;

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
		if (mlpUser.getVerifyExpiration() != null) {
			user.setStatus("Pending");
		} else {
			if (mlpUser.isActive()) {
				user.setStatus("Active");
			} else {
				user.setStatus("Inactive");
			}
		}
		user.setLastLogin(mlpUser.getLastLogin());
		user.setUsername(mlpUser.getLoginName());
		user.setCreated(mlpUser.getCreated());
		user.setUserId(mlpUser.getUserId());
		user.setJwttoken(mlpUser.getAuthToken());
		user.setPassword(mlpUser.getLoginHash());
		user.setPicture((mlpUser.getPicture()));
		user.setApiTokenHash(mlpUser.getApiToken());
		// user.setApiToken(mlpUser.getApiToken());
		if (mlpUser.getApiToken() != null) {
			user.setApiToken(mlpUser.getApiToken());
		} else if (PortalUtils.isEmptyOrNullString(mlpUser.getApiToken())) {
			user.setApiToken(null);
		}
		user.setTags(mlpUser.getTags());
		return user;
	}

	public static MLSolution convertToMLSolution(MLPSolution mlpSolution) {

		MLSolution mlSolution = new MLSolution();
		mlSolution.setSolutionId(mlpSolution.getSolutionId());
		mlSolution.setName(mlpSolution.getName());
		mlSolution.setOwnerId(mlpSolution.getUserId());
		mlSolution.setCreated(mlpSolution.getCreated());
		mlSolution.setModified(mlpSolution.getModified());
		mlSolution.setActive(mlpSolution.isActive());
		if (mlpSolution.getMetadata() != null)
			mlSolution.setMetadata(mlpSolution.getMetadata());
		mlSolution.setTookitType(mlpSolution.getToolkitTypeCode());
		mlSolution.setModelType(mlpSolution.getModelTypeCode());
		mlSolution.setDownloadCount(mlpSolution.getDownloadCount().intValue());
		mlSolution.setRatingCount(mlpSolution.getRatingCount().intValue());
		mlSolution.setViewCount(mlpSolution.getViewCount().intValue());
		mlSolution.setSolutionRatingAvg(mlpSolution.getRatingAverageTenths() / 10);
		mlSolution.setLastDownload(mlpSolution.getLastDownload());
		if (mlpSolution.isFeatured() != null) {
			mlSolution.setFeatured(mlpSolution.isFeatured());
		}
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
			mlpSolution.setUserId(mlSolution.getOwnerId());
		}
		if (mlSolution.getCreated() != null) {
			mlpSolution.setCreated(mlSolution.getCreated());
		}
		if (mlSolution.getModified() != null) {
			mlpSolution.setModified(mlSolution.getModified());
		}

		mlpSolution.setActive(mlSolution.isActive());

		if (!PortalUtils.isEmptyOrNullString(mlSolution.getMetadata())) {
			mlpSolution.setMetadata(mlSolution.getMetadata());
		}

		if (!PortalUtils.isEmptyOrNullString(mlSolution.getTookitType())) {
			mlpSolution.setToolkitTypeCode(mlSolution.getTookitType());
		}
		if (!PortalUtils.isEmptyOrNullString(mlSolution.getModelType())) {
			mlpSolution.setModelTypeCode(mlSolution.getModelType());
		}

		return mlpSolution;
	}

	// /**
	// *
	// * @param accessType Access type value
	// * @return
	// * MLPAccessType object for Storing in DB
	// */
	// public static MLPAccessType getMLPAccessType(String accessType) {
	// MLPAccessType mlpAccessType = null;
	//
	// if(!isEmptyOrNullString(accessType)) {
	// mlpAccessType = new MLPAccessType();
	// if(accessType.equals("PB")) {
	//
	// mlpAccessType.setCode("PB");
	// mlpAccessType.setName("Public");
	// } else if(accessType.equals("OR")) {
	// mlpAccessType.setCode("OR");
	// mlpAccessType.setName("Organization");
	// } else if(accessType.equals("PR")) {
	// mlpAccessType.setCode("PR");
	// mlpAccessType.setName("Private");
	// } else {//Default
	// mlpAccessType.setCode("PR");
	// mlpAccessType.setName("Private");
	// }
	// }
	// return mlpAccessType;
	// }

	public static String getEnvProperty(Environment env, String property) throws AcumosServiceException {
		String value = env.getProperty(property);
		if (PortalUtils.isEmptyOrNullString(value)) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
					"Environment not configured: " + property);
		}
		return value;
	}

	public static MLRole convertToMLRole(MLPRole mlpRole) {
		MLRole mlRole = new MLRole();
		mlRole.setRoleId(mlpRole.getRoleId());
		mlRole.setName(mlpRole.getName());
		mlRole.setCreated(mlpRole.getCreated());
		mlRole.setModified(mlpRole.getModified());
		mlRole.setActive(mlpRole.isActive());
		return mlRole;
	}

	public static MLPRole convertToMLPRole(MLRole mlRole) {
		MLPRole mlpRole = new MLPRole();
		mlpRole.setRoleId(mlRole.getRoleId());
		mlpRole.setName(mlRole.getName());
		mlpRole.setCreated(mlRole.getCreated());
		mlpRole.setModified(mlRole.getModified());
		mlpRole.setActive(mlRole.isActive());
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
		// private Byte[] picture;
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
		if (!PortalUtils.isEmptyOrNullString(user.getJwttoken())) {
			mlpUser.setAuthToken(user.getJwttoken());
		}
		// mlpUser.setActive(user.getActive());
		if (user.getActive().equals("Y"))
			mlpUser.setActive(true);
		else if (user.getActive().equals("N"))
			mlpUser.setActive(false);

		if (user.getOrgName() != null) {
			mlpUser.setOrgName(user.getOrgName());
		}
		if (user.getPicture() != null) {
			mlpUser.setPicture(user.getPicture());
		}

		/*
		 * if (user.getApiTokenHash() != null) {
		 * mlpUser.setApiTokenHash(user.getApiTokenHash()); }
		 */
		// commenting null check to delete token
		if (user.getApiToken() != null) {
			mlpUser.setApiToken(user.getApiToken());
		} else if (PortalUtils.isEmptyOrNullString(user.getApiToken())) {
			mlpUser.setApiToken(null);
		}

		if (user.getTags() != null) {
			mlpUser.setTags(user.getTags());
		}
		// Always remove the verification token before updating the User
		mlpUser.setVerifyTokenHash(null);
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
		// Need to fix once we have this getter method
		// oauthUser.setExpireTime(mlpUserLoginProvider.getExpired());
		oauthUser.setCreatedDate(mlpUserLoginProvider.getCreated());
		oauthUser.setModifiedDate(mlpUserLoginProvider.getModified());

		return oauthUser;
	}

	public static User convertUserMasterIntoMLPUser(UserMasterObject userMaster) {
		User user = new User();
		user.setFirstName(userMaster.getFirstName());
		user.setLastName(userMaster.getLastName());
		user.setEmailId(userMaster.getEmailId());
		// user.setActive(userMaster.isActive());
		if (userMaster.isActive() == true)
			user.setActive("Y");
		else if (userMaster.isActive() == false)
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

	public static MLSolutionRating convertToMLSolutionRating(MLPSolutionRating mlpSolutionRating) {
		MLSolutionRating mlSolutionRating = new MLSolutionRating();
		if (!PortalUtils.isEmptyOrNullString(mlpSolutionRating.getSolutionId())) {
			mlSolutionRating.setSolutionId(mlpSolutionRating.getSolutionId());
		}
		if (!PortalUtils.isEmptyOrNullString(mlpSolutionRating.getUserId())) {
			mlSolutionRating.setUserId(mlpSolutionRating.getUserId());
		}
		if (mlpSolutionRating.getRating() != null) {
			mlSolutionRating.setRating(mlpSolutionRating.getRating());
		}
		if (!PortalUtils.isEmptyOrNullString(mlpSolutionRating.getTextReview())) {
			mlSolutionRating.setTextReview(mlpSolutionRating.getTextReview());
		}
		if (mlpSolutionRating.getCreated() != null) {
			mlSolutionRating.setCreated(mlpSolutionRating.getCreated());
		}
		return mlSolutionRating;

	}

	public static MLSolutionFavorite convertToMLSolutionFavorite(MLPSolutionFavorite mlpSolutionFavorite) {
		MLSolutionFavorite mlSolutionFavorite = new MLSolutionFavorite();
		if (!PortalUtils.isEmptyOrNullString(mlpSolutionFavorite.getSolutionId())) {
			mlSolutionFavorite.setSolutionID(mlpSolutionFavorite.getSolutionId());
		}
		if (!PortalUtils.isEmptyOrNullString(mlpSolutionFavorite.getUserId())) {
			mlSolutionFavorite.setUserID(mlpSolutionFavorite.getUserId());
		}

		return mlSolutionFavorite;
	}

	public static MLSolutionDownload convertToMLSolutionDownload(MLPSolutionDownload mlpSolutionDownload) {
		MLSolutionDownload mlSolutionDownload = new MLSolutionDownload();
		if (!PortalUtils.isEmptyOrNullString(mlpSolutionDownload.getSolutionId())) {
			mlSolutionDownload.setSolutionId(mlpSolutionDownload.getSolutionId());
		}
		if (mlpSolutionDownload.getDownloadDate() != null) {
			mlSolutionDownload.setDownloadDate(mlpSolutionDownload.getDownloadDate());
		}
		if (!PortalUtils.isEmptyOrNullString(mlpSolutionDownload.getUserId())) {
			mlSolutionDownload.setUserId(mlpSolutionDownload.getUserId());
		}

		return mlSolutionDownload;

	}

	// public static MLPSolutionValidation
	// convertMLPSolutionValidation(MLModelValidationStatus
	// mlModelValidationStatus) {
	// MLPSolutionValidation mlpSolutionValidation = new
	// MLPSolutionValidation();
	// mlpSolutionValidation.setSolutionId(mlModelValidationStatus.getSolutionId());
	// mlpSolutionValidation.setRevisionId(mlModelValidationStatus.getRevisionId());
	// mlpSolutionValidation.setTaskId(mlModelValidationStatus.getTaskId());
	// String valStatus = mlModelValidationStatus.getStatus();
	// if(!isEmptyOrNullString(valStatus)) {
	// if(valStatus.equalsIgnoreCase("Success") ||
	// valStatus.equalsIgnoreCase("Pass") || valStatus.equalsIgnoreCase("PS")) {
	// mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.PS.toString());
	// } else if(valStatus.equalsIgnoreCase("In Progress") ||
	// valStatus.equalsIgnoreCase("IP") || valStatus.contains("Pending")) {
	// mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.IP.toString());
	// } else if(valStatus.equalsIgnoreCase("Failed") ||
	// valStatus.equalsIgnoreCase("FA")) {
	// mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.FA.toString());
	// } else {
	// mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.NV.toString());
	// }
	// } else {
	// mlpSolutionValidation.setValidationStatusCode(ValidationStatusCode.SB.toString());
	// }
	// mlpSolutionValidation.setValidationTypeCode(ValidationTypeCode.SS.toString());
	// if(!isEmptyList(mlModelValidationStatus.getArtifactValidationStatus())) {
	// mlpSolutionValidation.setDetail(JsonUtils.serializer().toString(mlModelValidationStatus.getArtifactValidationStatus()));
	// }
	//
	// log.debug(EELFLoggerDelegate.debugLogger, "convertMLPSolutionValidation
	// ={}", JsonUtils.serializer().toPrettyString(mlpSolutionValidation));
	// return mlpSolutionValidation;
	// }

	public static MLPSiteConfig convertMLSiteConfigToMLPSiteConfig(MLPSiteConfig mlpSiteConfig) {
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

				sb.append(SanitizeUtils.sanitize(line) + "\n");
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

	public static MLStepResult convertToMLStepResult(MLPTask task, MLPTaskStepResult mlpStepResult) {

		MLStepResult mlStepResult = new MLStepResult();
		mlStepResult.setTaskId(task.getTaskId());
		mlStepResult.setUserId(task.getUserId());
		mlStepResult.setTrackingId(task.getTrackingId());
		mlStepResult.setSolutionId(task.getSolutionId());
		mlStepResult.setRevisionId(task.getRevisionId());
		mlStepResult.setStepResultId(mlpStepResult.getStepResultId());
		mlStepResult.setStatusCode(mlpStepResult.getStatusCode());
		mlStepResult.setName(mlpStepResult.getName());
		mlStepResult.setResult(mlpStepResult.getResult());

		return mlStepResult;

	}

	public static MLStepResult convertToMLStepResults(MLPTaskStepResult mlPTaskStepResult) {
		MLStepResult mlStepResult = new MLStepResult();
		mlStepResult.setTaskId(mlPTaskStepResult.getTaskId());
		mlStepResult.setStatusCode(mlPTaskStepResult.getStatusCode());
		mlStepResult.setResult(mlPTaskStepResult.getResult());
		mlStepResult.setName(mlPTaskStepResult.getName());
		mlStepResult.setStepResultId(mlPTaskStepResult.getStepResultId());
		mlStepResult.setStartDate(
				mlPTaskStepResult.getStartDate() != null ? Date.from(mlPTaskStepResult.getStartDate()) : null);
		mlStepResult.setEndDate(
				Date.from(mlPTaskStepResult.getEndDate()) != null ? Date.from(mlPTaskStepResult.getEndDate()) : null);

		return mlStepResult;
	}

	public static MLTask convertToMLTask(MLPTask task) {
		MLTask mlTask = new MLTask();
		mlTask.setCreatedtDate(task.getCreated() != null ? Date.from(task.getCreated()) : null);
		mlTask.setModifiedDate(task.getModified() != null ? Date.from(task.getModified()) : null);
		mlTask.setTaskCode(task.getTaskCode());
		mlTask.setSolutionId(task.getSolutionId());
		mlTask.setRevisionId(task.getRevisionId());
		mlTask.setStatusCode(task.getStatusCode());
		mlTask.setName(task.getName());
		mlTask.setUserId(task.getUserId());
		mlTask.setTrackingId(task.getTrackingId());
		mlTask.setTaskId(task.getTaskId());

		return mlTask;
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

	public static RestPageResponse<MLSolutionRating> convertToMLSolutionRatingRestPageResponse(
			List<MLSolutionRating> mlSolutionRatingList, RestPageResponse<MLPSolutionRating> mlpSolutionRating) {
		RestPageResponse<MLSolutionRating> mlSolutionRating = new RestPageResponse<MLSolutionRating>(
				mlSolutionRatingList,
				PageRequest.of(mlpSolutionRating.getNumber(), mlpSolutionRating.getSize(), mlpSolutionRating.getSort()),
				mlpSolutionRating.getTotalElements());
		return mlSolutionRating;
	}

	// public static MLSolutionWeb convertToMLSolutionWeb(MLPSolutionWeb
	// mlpSolutionweb) {
	//
	// MLSolutionWeb mlSolutionWeb = new MLSolutionWeb();
	// mlSolutionWeb.setSolutionId(mlpSolutionweb.getSolutionId());
	// mlSolutionWeb.setViewCount(mlpSolutionweb.getViewCount());
	// mlSolutionWeb.setDownloadCount(mlpSolutionweb.getDownloadCount());
	// mlSolutionWeb.setLastDownload(mlpSolutionweb.getLastDownload());
	// mlSolutionWeb.setRatingCount(mlpSolutionweb.getRatingCount());
	// mlSolutionWeb.setRatingAverageTenths(mlpSolutionweb.getRatingAverageTenths());
	// mlSolutionWeb.setFeatured(mlpSolutionweb.isFeatured());
	//
	// return mlSolutionWeb;
	// }

	public static MLComment convertToMLComment(MLPComment mlpComment, String userTimeZone) {

		DateUtils dateUtils = new DateUtils();
		MLComment mlComment = new MLComment();
		mlComment.setCommentId(mlpComment.getCommentId());
		mlComment.setParentId(mlpComment.getParentId());
		mlComment.setText(mlpComment.getText());
		mlComment.setThreadId(mlpComment.getThreadId());
		mlComment.setUserId(mlpComment.getUserId());
		mlComment.setCreated(mlpComment.getCreated());
		mlComment.setModified(mlpComment.getModified());
		if (userTimeZone != null)
			mlComment.setStringDate(
					dateUtils.formatCommentTime(new DateTime(mlComment.getModified().toEpochMilli()), userTimeZone));

		return mlComment;
	}

	public static RevisionDescription convertToRevisionDescription(MLPRevisionDescription mlpDescription) {
		RevisionDescription revisiondescription = new RevisionDescription();
		revisiondescription.setRevisionId(mlpDescription.getRevisionId());
		revisiondescription.setAccessTypeCode(mlpDescription.getAccessTypeCode());
		revisiondescription.setDescription(mlpDescription.getDescription());
		return revisiondescription;
	}

	public static List<Author> convertToAuthor(AuthorTransport[] authorTransport) {
		List<Author> authorList = new ArrayList<>();

		for (AuthorTransport authorT : authorTransport) {
			Author portalAuthor = new Author(authorT.getName(), authorT.getContact());
			authorList.add(portalAuthor);
		}
		return authorList;
	}

	public static MLPublishRequest convertToMLPublishRequest(MLPPublishRequest publishRequest) {
		MLPublishRequest mlPublishRequest = new MLPublishRequest();
		mlPublishRequest.setPublishRequestId(publishRequest.getRequestId());
		mlPublishRequest.setCreationDate(publishRequest.getCreated());
		mlPublishRequest.setLastModifiedDate(publishRequest.getModified());
		mlPublishRequest.setRequestUserId(publishRequest.getRequestUserId());
		mlPublishRequest.setSolutionId(publishRequest.getSolutionId());
		mlPublishRequest.setRevisionId(publishRequest.getRevisionId());
		if (!PortalUtils.isEmptyOrNullString(publishRequest.getStatusCode())) {
			mlPublishRequest.setRequestStatusCode(publishRequest.getStatusCode());
		}
		if (!PortalUtils.isEmptyOrNullString(publishRequest.getReviewUserId())) {
			mlPublishRequest.setApproverId(publishRequest.getReviewUserId());
		}
		if (!PortalUtils.isEmptyOrNullString(publishRequest.getComment())) {
			mlPublishRequest.setComment(publishRequest.getComment());
		}
		return mlPublishRequest;
	}
}
