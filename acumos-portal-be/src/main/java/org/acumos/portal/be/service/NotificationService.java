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
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLUserNotifPref;
import org.acumos.portal.be.transport.User; 

public interface NotificationService {

	MLNotification createNotification(MLPNotification body);

	List<MLNotification> getNotifications();

	List<MLPUserNotification> getUserNotifications(String userId, RestPageRequest restPageRequest);

	void addNotificationUser(String notificationId, String userId);

	void dropNotificationUser(String notificationId, String userId);

	void setNotificationUserViewed(String notificationId, String userId);
	
	void deleteNotification(String notificationId);

	int getNotificationCount();

	void generateNotification(MLPNotification notification, String userId);
	
    List<MLUserNotifPref> getUserNotifPrefByUserId(String userId);
    
    MLUserNotifPref createUserNotificationPreference(MLUserNotifPref mlpUserNotifPref);
       
    void updateUserNotificationPreference(MLUserNotifPref mlpUserNotifPref);

    void sendMailNotification(User user,String subject ,String template);
}
