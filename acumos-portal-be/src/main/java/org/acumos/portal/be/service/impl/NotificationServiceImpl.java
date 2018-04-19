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

package org.acumos.portal.be.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUserNotifPref;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLUserNotifPref;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(NotificationServiceImpl.class);
 
	@Autowired
	private Environment env;

	private ICommonDataServiceRestClient getClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(env.getProperty("cdms.client.url"),
				env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"));
		return client;
	}

	/*
	 * No
	 */
	public NotificationServiceImpl() {

	}

	@Override
	public MLNotification createNotification(MLPNotification mlpNotification) {
		log.debug(EELFLoggerDelegate.debugLogger, "createNotification`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLNotification mlNotification = PortalUtils.convertToMLNotification(dataServiceRestClient.createNotification(mlpNotification));
		return mlNotification;
	}

	@Override
	public List<MLNotification> getNotifications() {
		log.debug(EELFLoggerDelegate.debugLogger, "getNotifications`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient(); 
		RestPageResponse<MLPNotification> mlpSolutionsPaged = null;
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(0);
		mlpSolutionsPaged = dataServiceRestClient.getNotifications(pageRequest);
		List<MLPNotification> mlpNotificationList = mlpSolutionsPaged.getContent();
		List<MLNotification> mlNotificationList = new ArrayList<>();
		if (mlpNotificationList != null) {
			for (MLPNotification mlpNotification : mlpNotificationList) {
				MLNotification mlNotification = PortalUtils.convertToMLNotification(mlpNotification);
				mlNotificationList.add(mlNotification);
			}
		}
		return mlNotificationList;
	}

	@Override
	public List<MLPUserNotification> getUserNotifications(String userId, RestPageRequest restPageRequest) {
		log.debug(EELFLoggerDelegate.debugLogger, "getUserNotifications`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient(); 
		RestPageResponse<MLPUserNotification> mlpNotificationList = dataServiceRestClient.getUserNotifications(userId,restPageRequest);	
		return mlpNotificationList.getContent();
	}

	@Override
	public void addNotificationUser(String notificationId, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "addNotificationUser`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.addUserToNotification(notificationId,userId);
	}

	@Override 
	public void dropNotificationUser(String notificationId, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "dropNotificationUser`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.dropUserFromNotification(notificationId,userId);
		dataServiceRestClient.deleteNotification(notificationId);
	}

	@Override 
	public void setNotificationUserViewed(String notificationId, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "dropNotificationUser`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.setUserViewedNotification(notificationId,userId);
	}
	
	@Override
	public void deleteNotification(String notificationId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteNotification`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteNotification(notificationId);
	}

	@Override
	public int getNotificationCount() {
		log.debug(EELFLoggerDelegate.debugLogger, "getNotificationCount");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Long count = dataServiceRestClient.getNotificationCount();
		return count.intValue();
	}
	
	@Override
	public void generateNotification(MLPNotification notification, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "generateNotification");
		try {
			if (notification != null) {
				Calendar cal = Calendar.getInstance();
				Date startDate = cal.getTime();
				cal.add(Calendar.YEAR, 1);
				Date endDate = cal.getTime();

				notification.setStart(startDate);
				notification.setEnd(endDate);
				notification.setCreated(startDate);
				MLNotification mlNotification = createNotification(notification);
				if (mlNotification.getNotificationId() != null && userId != null) {
					addNotificationUser(mlNotification.getNotificationId(), userId);
				}
			} else {
				log.error(EELFLoggerDelegate.errorLogger,
						"Notification message can not be null: generateNotification()");
			}
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getNotifications", e);
		}
	}
	
	@Override
	public List<MLUserNotifPref> getUserNotifPrefByUserId(String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getUserNotificationPreferences`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPUserNotifPref> mlpNotificationList = dataServiceRestClient.getUserNotificationPreferences(userId);
		List<MLUserNotifPref> mlNotificationList = new ArrayList<>(mlpNotificationList.size());
		for (MLPUserNotifPref mlpUserNotifPref : mlpNotificationList) {
			mlNotificationList.add(PortalUtils.convertToMLUserNotifPref(mlpUserNotifPref));
		}
		return mlNotificationList;
	}

	@Override
	public MLUserNotifPref createUserNotificationPreference(MLUserNotifPref mlUserNotifPref) {
		log.debug(EELFLoggerDelegate.debugLogger, "createUserNotificationPreference`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPUserNotifPref mlpUserNotifPref = PortalUtils.convertToMLPUserNotifPref(mlUserNotifPref);
		mlpUserNotifPref = dataServiceRestClient.createUserNotificationPreference(mlpUserNotifPref);
		mlUserNotifPref = PortalUtils.convertToMLUserNotifPref(mlpUserNotifPref);
		return mlUserNotifPref;
	}

}
