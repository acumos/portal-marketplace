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

package org.acumos.portal.be.service;

import java.util.List;

import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLUserNotifPref;
import org.acumos.portal.be.transport.NotificationRequestObject; 


public interface NotificationService {
	/**
	 * Creates the notification in DB
	 * 
	 * @param body
	 *            MLPNotification
	 * @return MLNotification
	 */
	MLNotification createNotification(MLPNotification body);

	/**
	 * Fetch all the notifications from the DB
	 * 
	 * @return List of paginated MLNotification
	 */
	List<MLNotification> getNotifications();

	/**
	 * Get the List of notifications for a user
	 * 
	 * @param userId
	 * @param restPageRequest
	 * @return List of notifications for user
	 */
	List<MLPUserNotification> getUserNotifications(String userId, RestPageRequest restPageRequest);

	/**
	 * Add the generated notification to the user
	 * 
	 * @param notificationId
	 * @param userId
	 */
	void addNotificationUser(String notificationId, String userId);

	/**
	 * Delete the notification for the user
	 * 
	 * @param notificationId
	 * @param userId
	 */
	void dropNotificationUser(String notificationId, String userId);

	/**
	 * Mark the notification as viewed for the user
	 * 
	 * @param notificationId
	 * @param userId
	 */
	void setNotificationUserViewed(String notificationId, String userId);

	/**
	 * @param notificationId
	 */
	void deleteNotification(String notificationId);

	/**
	 * Get Count of all the notifications
	 * 
	 * @return Number of notifications
	 */
	int getNotificationCount();

	/**
	 * Generate a notification for the user
	 * 
	 * @param notification
	 * @param userId
	 */
	void generateNotification(MLPNotification notification, String userId);

	/**
	 * Get the notification preferences for the user
	 * 
	 * @param userId
	 * @return List of notification preferences for the user
	 */
	List<MLUserNotifPref> getUserNotifPrefByUserId(String userId);

	/**
	 * Create a notification preference for the user
	 * 
	 * @param mlpUserNotifPref
	 * @return Notification preference 
	 */
	MLUserNotifPref createUserNotificationPreference(MLUserNotifPref mlpUserNotifPref);

	/**
	 * Update the notification preference
	 * 
	 * @param mlpUserNotifPref
	 */
	void updateUserNotificationPreference(MLUserNotifPref mlpUserNotifPref);

	/**
	 * Send the notification to the user according to the notification preference
	 * 
	 * @param notificationRequest
	 * @throws AcumosServiceException
	 */
	void sendUserNotification(NotificationRequestObject notificationRequest) throws AcumosServiceException;

}
