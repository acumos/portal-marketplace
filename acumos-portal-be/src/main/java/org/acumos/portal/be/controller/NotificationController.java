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

package org.acumos.portal.be.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLUserNotifPref;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class NotificationController extends AbstractController {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(NotificationController.class);

	@Autowired
	private NotificationService notificationService;

	/**
	 * 
	 */
	public NotificationController() {

	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param mlpNotification
	 *            Notification
	 * @param response
	 *            HttpServletResponse
	 * @return Notification
	 */
	@ApiOperation(value = "Create notification", response = MLSolution.class)
	@RequestMapping(value = { APINames.CREATE_NOTIFICATION }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLNotification> createNotification(HttpServletRequest request,
			@RequestBody JsonRequest<MLPNotification> mlpNotification, HttpServletResponse response) {
		JsonResponse<MLNotification> data = new JsonResponse<>();
		try {
			if (mlpNotification.getBody() != null) {
				MLNotification mlNotification = notificationService.createNotification(mlpNotification.getBody());
				data.setResponseBody(mlNotification);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Notification created Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "Notification created Successfully :  ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while createNotification");
				log.error(EELFLoggerDelegate.errorLogger, "Error Occurred createNotification :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while createNotification");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createNotification :", e);
		}
		return data;
	}

	/**
	 * @return List of notifications
	 */
	@ApiOperation(value = "Gets a list of Paginated Notifications for Market Place Catalog.", response = MLNotification.class, responseContainer = "List")
	@RequestMapping(value = { APINames.NOTIFICATIONS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLNotification>> getNotifications() {
		JsonResponse<List<MLNotification>> data = new JsonResponse<>();
		try {
			List<MLNotification> mlNotificationList = notificationService.getNotifications();
			if (mlNotificationList != null) {
				data.setResponseBody(mlNotificationList);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Notifications fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getNotifications: size is {} ", mlNotificationList.size());
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error Occurred while getNotifications");
				log.debug(EELFLoggerDelegate.debugLogger, "Error Occurred while getNotifications ");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception Occurred while getNotifications");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getNotifications", e);
		}
		return data;
	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param userId
	 *            userId
	 * @param restPageReq
	 *            rest page request
	 * @param response
	 *            HttpServletResponse
	 * @return List of notifications
	 */
	@ApiOperation(value = "Gets a list of Paginated Notifications for Market Place Catalog.", response = MLNotification.class, responseContainer = "List")
	@RequestMapping(value = { APINames.MAIL_NOTIFICATIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPUserNotification>> getUserNotifications(HttpServletRequest request,
			@PathVariable("userId") String userId, @RequestBody JsonRequest<RestPageRequest> restPageReq,
			HttpServletResponse response) {
		JsonResponse<List<MLPUserNotification>> data = new JsonResponse<>();
		try {
			List<MLPUserNotification> mlNotificationList = notificationService.getUserNotifications(userId,
					restPageReq.getBody());
			if (mlNotificationList != null) {
				data.setResponseBody(mlNotificationList);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Notifications fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getUserNotifications: size is {} ",
						mlNotificationList.size());
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("No notifications exist for user : " + userId);
				log.debug(EELFLoggerDelegate.debugLogger, "No notifications exist for user : " + userId);
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception Occurred while getUserNotifications");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getUserNotifications", e);
		}
		return data;
	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param notificationId
	 *            notification ID
	 * @param userId
	 *            user Id
	 * @param response
	 *            HttpServletResponse
	 * @return List of notification
	 */
	@ApiOperation(value = "Add notification for user", response = MLNotification.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.ADD_USER_NOTIFICATIONS }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLNotification>> addNotificationUser(HttpServletRequest request,
			@PathVariable("notificationId") String notificationId, @PathVariable("userId") String userId,
			HttpServletResponse response) {
		JsonResponse<List<MLNotification>> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(notificationId) && !PortalUtils.isEmptyOrNullString(userId)) {
				notificationService.addNotificationUser(notificationId, userId);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Notifications fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "addNotificationUser: size is {} ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error Occurred while addNotificationUser");
				log.debug(EELFLoggerDelegate.debugLogger, "Error Occurred while addNotificationUser ");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception Occurred while addNotificationUser");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while addNotificationUser", e);
		}
		return data;
	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param notificationId
	 *            notification ID
	 * @param userId
	 *            user ID
	 * @param response
	 *            HttpServletResponse
	 * @return List of notification
	 */
	@ApiOperation(value = "Drop notification for user", response = MLNotification.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.DROP_USER_NOTIFICATIONS }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLNotification>> dropNotificationUser(HttpServletRequest request,
			@PathVariable("notificationId") String notificationId, @PathVariable("userId") String userId,
			HttpServletResponse response) {
		JsonResponse<List<MLNotification>> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(notificationId) && !PortalUtils.isEmptyOrNullString(userId)) {
				notificationService.dropNotificationUser(notificationId, userId);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Notifications droped Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "dropNotificationUser: size is {} ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error Occurred while dropNotificationUser");
				log.debug(EELFLoggerDelegate.debugLogger, "Error Occurred while dropNotificationUser ");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception Occurred while dropNotificationUser");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while dropNotificationUser", e);
		}
		return data;
	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param notificationId
	 *            notification ID
	 * @param userId
	 *            user ID
	 * @param response
	 *            HttpServletResponse
	 * @return List of notification
	 */
	@ApiOperation(value = "Notification viewed by user", response = MLNotification.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.VIEW_USER_NOTIFICATIONS }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLNotification>> setNotificationUserViewed(HttpServletRequest request,
			@PathVariable("notificationId") String notificationId, @PathVariable("userId") String userId,
			HttpServletResponse response) {
		JsonResponse<List<MLNotification>> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(notificationId) && !PortalUtils.isEmptyOrNullString(userId)) {
				notificationService.setNotificationUserViewed(notificationId, userId);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Set Notifications viewed by user Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "setNotificationUserViewed: size is {} ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error Occurred while setNotificationUserViewed");
				log.debug(EELFLoggerDelegate.debugLogger, "Error Occurred while setNotificationUserViewed ");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception Occurred while setNotificationUserViewed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while setNotificationUserViewed", e);
		}
		return data;
	}

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param notificationId
	 *            notification ID
	 * @param response
	 *            HttpServletResponse
	 * @return List of notification
	 */
	@ApiOperation(value = "Delete notification", response = MLNotification.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.DELETE_NOTIFICATIONS }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLNotification>> deleteNotification(HttpServletRequest request,
			@PathVariable("notificationId") String notificationId, HttpServletResponse response) {
		JsonResponse<List<MLNotification>> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(notificationId)) {
				notificationService.deleteNotification(notificationId);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Notifications droped Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "deleteNotification: size is {} ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error Occurred while deleteNotification");
				log.debug(EELFLoggerDelegate.debugLogger, "Error Occurred while deleteNotification ");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception Occurred while deleteNotification");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while deleteNotification", e);
		}
		return data;
	}

	/**
	 * 
	 * @return Notification count
	 */
	@ApiOperation(value = "Gets Notifications count for Market Place Catalog.", response = MLNotification.class, responseContainer = "List")
	@RequestMapping(value = { APINames.NOTIFICATIONS_COUNT }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLNotification> getNotificationCount() {
		MLNotification notification = new MLNotification();
		JsonResponse<MLNotification> data = new JsonResponse<>();
		try {
			int count = notificationService.getNotificationCount();
			notification.setCount(count);
			data.setResponseBody(notification);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Notification count fetched Successfully");
			log.debug(EELFLoggerDelegate.debugLogger, "getNotificationCount: size is {} ", count);
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception Occurred while getNotificationCount");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getNotificationCount", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Gets a list of user notification preferences by userId", response = MLPUserNotifPref.class, responseContainer = "List")
	   @RequestMapping(value = { APINames.USER_NOTIFICATION_PREF }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	   @ResponseBody
	   public JsonResponse<List<MLUserNotifPref>> getUserNotifPrefByUserId(HttpServletRequest request,
	           @PathVariable("userId") String userId, HttpServletResponse response) {
	       JsonResponse<List<MLUserNotifPref>> data = new JsonResponse<>();
	       try {
	           List<MLUserNotifPref> mlUserNotifPrefList = notificationService.getUserNotifPrefByUserId(userId);
	           if (mlUserNotifPrefList != null) {
	               data.setResponseBody(mlUserNotifPrefList);
	               data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
	               data.setResponseDetail("Notification preferences fetched Successfully");
	               log.debug(EELFLoggerDelegate.debugLogger, "getUserNotificationPreferences: size is {} ",
	                                   mlUserNotifPrefList.size());
	          } else {
	               data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
	               data.setResponseDetail("No notification preferences for : " + userId);
	               log.debug(EELFLoggerDelegate.debugLogger, "No notification preferences : " + userId);
	           }
	       } catch (Exception e) {
	           data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
	           data.setResponseDetail("Exception Occurred while getUserNotificationPreferences");
	           log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getUserNotificationPreferences", e);
	       }
	       return data;
	   }
	   
	   @ApiOperation(value = "Create notification Preference", response = MLPUserNotifPref.class)
	   @RequestMapping(value = { APINames.CREATE_NOTIFICATION_PREFERENCES }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	   @ResponseBody
	   public JsonResponse<MLUserNotifPref> createUserNotificationPreference(HttpServletRequest request,
	           @RequestBody JsonRequest<MLUserNotifPref> mlNotificationPref, HttpServletResponse response) {
	       JsonResponse<MLUserNotifPref> data = new JsonResponse<>();
	       try {
	           if (mlNotificationPref.getBody() != null) {
	                   MLUserNotifPref mlNotificationPrefObj = notificationService.createUserNotificationPreference(mlNotificationPref.getBody());
	               if (mlNotificationPrefObj != null) {
	                   data.setResponseBody(mlNotificationPrefObj);
	                   data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
	                   data.setResponseDetail("Notification Preference created Successfully");
	                   log.debug(EELFLoggerDelegate.debugLogger, "Notification Preference created Successfully :  ");
	               } else {
	                   data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
	                   data.setResponseDetail("Error occured while createUserNotificationPreference");
	                   log.error(EELFLoggerDelegate.errorLogger, "Error Occurred createUserNotificationPreference :");
	               }
	           } else {
	               data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
	               data.setResponseDetail("Error occured while createUserNotificationPreference");
	               log.error(EELFLoggerDelegate.errorLogger, "Error Occurred createUserNotificationPreference :");
	           }
	       } catch (Exception e) {
	           data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
	           data.setResponseDetail("Exception occured while createNotification");
	           log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createUserNotificationPreference :", e);
	       }
	       return data;
	   }
	   
	   @ApiOperation(value = "Update Notification Preference", response = MLPUserNotifPref.class)
	   @RequestMapping(value = { APINames.UPDATE_NOTIFICATION_PREFERENCES }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	   @ResponseBody
	   public JsonResponse<MLUserNotifPref> updateUserNotificationPreference(HttpServletRequest request,
			@RequestBody JsonRequest<MLUserNotifPref> mlNotificationPref, HttpServletResponse response) {
		JsonResponse<MLUserNotifPref> data = new JsonResponse<>();
		try {
			if (mlNotificationPref.getBody() != null && mlNotificationPref.getBody().getUserNotifPrefId() != null) {
				notificationService.updateUserNotificationPreference(mlNotificationPref.getBody());
				data.setResponseBody(mlNotificationPref.getBody());
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Notification Preference Update Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "Notification Preference Update Successfully :  ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while updateUserNotificationPreference");
				log.error(EELFLoggerDelegate.errorLogger, "Error Occurred updateUserNotificationPreference :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while updateUserNotificationPreference");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateUserNotificationPreference :", e);
		}
		return data;
	}

	   /**
		 * 
		 * @param request
		 *            HttpServletRequest
		 * @param userId
		 *            userId
		 * @param restPageReq
		 *            rest page request
		 * @param response
		 *            HttpServletResponse
		 * @return List of notifications
		 */
		@ApiOperation(value = "Sending mail after successful Onboarding")
		@RequestMapping(value = { APINames.USER_NOTIFICATIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
		@ResponseBody
		public JsonResponse<MLUserNotifPref> sendMailNotification(HttpServletRequest request,
				@PathVariable("user") User user, @PathVariable("subject") String subject, @PathVariable("template") String template,@RequestBody JsonRequest<MLUserNotifPref> mlpNotification, HttpServletResponse response) {
			JsonResponse<MLUserNotifPref> data = new JsonResponse<>();
			try {
				if (mlpNotification.getBody() != null) {
					notificationService.sendMailNotification(user, subject, template);
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					log.debug(EELFLoggerDelegate.debugLogger, "Mail Send Successfully :  ");
				} else {
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
					log.error(EELFLoggerDelegate.errorLogger, "Error Occurred while Sending Mail :");
				}
			} catch (Exception e) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
				data.setResponseDetail("Exception occured while sending mail");
				log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail :", e);
			}
			return data;
		}
}
