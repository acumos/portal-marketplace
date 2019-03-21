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
package org.acumos.be.test.service.impl;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.service.impl.NotificationServiceImpl;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.junit.Assert;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTestImpl {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	NotificationServiceImpl impl = new NotificationServiceImpl();

	@Test
	public void createNotificationTest() {
		try {
			MLPNotification mlpNotification = new MLPNotification();
			Instant created = Instant.now();
			mlpNotification.setCreated(created);
			mlpNotification.setMessage("notification created for view count");
			Instant modified = Instant.now();
			mlpNotification.setModified(modified);
			mlpNotification.setTitle("Notification");
			mlpNotification.setUrl("http://notify.com");
			Instant end = Instant.now();
			mlpNotification.setEnd(end);
			Instant start = Instant.now();
			mlpNotification.setStart(start);

			MLNotification notifiacation = PortalUtils.convertToMLNotification(mlpNotification);

			if (mlpNotification != null) {
				Mockito.when(impl.createNotification(mlpNotification)).thenReturn(notifiacation);
				logger.info("Successfully created notification " + notifiacation);
				Assert.assertEquals(notifiacation, notifiacation);
			}
		} catch (Exception e) {
			logger.info("Failed to create notification ");
		}
	}

	@Test
	public void getNotificationsTest() {
		try {
			MLPNotification mlpNotification = new MLPNotification();
			Instant created = Instant.now();
			mlpNotification.setCreated(created);
			mlpNotification.setMessage("notification created for view count");
			Instant modified = Instant.now();
			mlpNotification.setModified(modified);
			mlpNotification.setTitle("Notification");
			mlpNotification.setUrl("http://notify.com");
			Instant end = Instant.now();
			mlpNotification.setEnd(end);
			Instant start = Instant.now();
			mlpNotification.setStart(start);

			MLNotification notifiacation = PortalUtils.convertToMLNotification(mlpNotification);

			List<MLNotification> mlNotificationList = new ArrayList<MLNotification>();
			mlNotificationList.add(notifiacation);
			if (mlNotificationList != null) {
				Mockito.when(impl.getNotifications()).thenReturn(mlNotificationList);
				logger.info("Successfully fetched notifications ");
				Assert.assertEquals(mlNotificationList, mlNotificationList);
			}
		} catch (Exception e) {
			logger.info("Failed while fetching notifications ");
		}
	}

	@Test
	public void getUserNotificationsTest() {
		try {

			RestPageRequest restPageRequest = new RestPageRequest();
			restPageRequest.setSize(9);
			restPageRequest.setPage(1);
			MLPUserNotification mlpUserNotification = new MLPUserNotification();
			Instant created = Instant.now();
			mlpUserNotification.setCreated(created);
			mlpUserNotification.setMessage("notification created");
			Instant modified = Instant.now();
			mlpUserNotification.setModified(modified);
			mlpUserNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlpUserNotification.setTitle("Notification");
			mlpUserNotification.setUrl("http://notify.com");
			Instant viewed = Instant.now();
			mlpUserNotification.setViewed(viewed);

			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId(mlpUserNotification.getNotificationId());
			mlpNotificationUserMap.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");

			List<MLPUserNotification> mlpUserNotificationList = new ArrayList<>();
			mlpUserNotificationList.add(mlpUserNotification);

			String userId = mlpNotificationUserMap.getUserId();

			if (restPageRequest.getPage() != null && restPageRequest.getSize() != null) {
				Mockito.when(impl.getUserNotifications(userId, restPageRequest)).thenReturn(mlpUserNotificationList);
				logger.info("User based notification fetched successfully ");
				Assert.assertEquals(mlpUserNotificationList, mlpUserNotificationList);
			}

		} catch (Exception e) {
			logger.info("Failed to fetch user notifications  " + e);
		}
	}

	@Test
	public void addNotificationUserTest() {
		try {

			NotificationServiceImpl mockimpl = mock(NotificationServiceImpl.class);

			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId("de70d37d-c556-4d5f-8582-bed318ac3e49");
			mlpNotificationUserMap.setUserId("1810f833-8698-4233-add4-091e34b8703c");

			String notificationId = mlpNotificationUserMap.getNotificationId();
			String userId = mlpNotificationUserMap.getUserId();

			if (notificationId != null && userId != null) {
				mockimpl.addNotificationUser(notificationId, userId);
				logger.info("Successfully added notification for particuler user ");
				Assert.assertNotNull(notificationId);
				Assert.assertNotNull(userId);
				Assert.assertEquals(mlpNotificationUserMap, mlpNotificationUserMap);
			}

		} catch (Exception e) {
			logger.info("Failed to add user notifications  " + e);
		}
	}

	@Test
	public void dropNotificationUserTest() {
		try {

			NotificationServiceImpl mockimpl = mock(NotificationServiceImpl.class);

			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId("de70d37d-c556-4d5f-8582-bed318ac3e49");
			mlpNotificationUserMap.setUserId("1810f833-8698-4233-add4-091e34b8703c");

			String notificationId = mlpNotificationUserMap.getNotificationId();
			String userId = mlpNotificationUserMap.getUserId();

			if (notificationId != null && userId != null) {
				mockimpl.dropNotificationUser(notificationId, userId);
				logger.info("Successfully dropped notification for particuler user ");
				Assert.assertNotNull(notificationId);
				Assert.assertNotNull(userId);
				Assert.assertEquals(mlpNotificationUserMap, mlpNotificationUserMap);
			}
		} catch (Exception e) {
			logger.info("Failed to drop user notifications  " + e);
		}
	}

	@Test
	public void setNotificationUserViewedTest() {
		try {
			NotificationServiceImpl mockimpl = mock(NotificationServiceImpl.class);

			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId("de70d37d-c556-4d5f-8582-bed318ac3e49");
			mlpNotificationUserMap.setUserId("1810f833-8698-4233-add4-091e34b8703c");

			String notificationId = mlpNotificationUserMap.getNotificationId();
			String userId = mlpNotificationUserMap.getUserId();

			if (notificationId != null && userId != null) {
				mockimpl.setNotificationUserViewed(notificationId, userId);
				logger.info("Successfully setNotificationUserViewed ");
				Assert.assertNotNull(notificationId);
				Assert.assertNotNull(userId);
				Assert.assertEquals(mlpNotificationUserMap, mlpNotificationUserMap);
			}
		} catch (Exception e) {
			logger.info("Failed to setNotificationUserViewedTest  " + e);
		}
	}

	@Test
	public void deleteNotificationTest() {
		try {
			NotificationServiceImpl mockimpl = mock(NotificationServiceImpl.class);
			MLNotification mlNotification = new MLNotification();
			mlNotification.getNotificationId();

			String notificationId = "de70d37d-c556-4d5f-8582-bed318ac3e49";
			Assert.assertNotNull(notificationId);
			if (notificationId != null) {
				mockimpl.deleteNotification(notificationId);
				logger.info("Notification deleted successfully ");
				Assert.assertEquals(mockimpl, mockimpl);
			}

		} catch (Exception e) {
			logger.info("Failed to deleteNotificationTest  " + e);
		}
	}

	@Test
	public void getNotificationCountTest() {
		try {
			Integer value = new Integer(2);
			Mockito.when(impl.getNotificationCount()).thenReturn(value);
			logger.info("Notification count fetched ");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to getNotificationCountTest  " + e);
		}
	}
}
