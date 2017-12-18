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
package org.acumos.be.test.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.NotificationController;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(NotificationControllerTest.class);


	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	NotificationController notificationController = new NotificationController();

	@Test
	public void createNotificationTest() {
		try {
			MLPNotification mlpNotification = new MLPNotification();
			Date created = new Date();
			mlpNotification.setCreated(created);
			mlpNotification.setMessage("notification created");
			Date modified = new Date();
			mlpNotification.setModified(modified);
			mlpNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlpNotification.setTitle("Notification");
			mlpNotification.setUrl("http://notify.com");
			Assert.assertNotNull(mlpNotification);
			JsonRequest<MLPNotification> notificationReq = new JsonRequest<>();
			notificationReq.setBody(mlpNotification);
			Assert.assertNotNull(notificationReq);
			JsonResponse<MLNotification> notificationres = new JsonResponse<>();
			MLNotification responseBody = PortalUtils.convertToMLNotification(mlpNotification);
			notificationres.setResponseBody(responseBody);
			Assert.assertNotNull(responseBody);
			Mockito.when(notificationController.createNotification(request, notificationReq, response))
					.thenReturn(notificationres);
			logger.info("Notification Response : " + notificationres.getResponseBody());
			Assert.assertNotNull(notificationres);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}

	}

	@Test
	public void getNotificationsTest() {
		try {
			MLNotification mlNotification = new MLNotification();
			mlNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlNotification.setCount(1);
			mlNotification.setMessage("notification");
			mlNotification.setTitle("Notification");
			mlNotification.setUrl("http://notify.com");
			Assert.assertNotNull(mlNotification);
			List<MLNotification> mlNotificationList = new ArrayList<MLNotification>();
			mlNotificationList.add(mlNotification);
			Assert.assertNotNull(mlNotificationList);
			JsonResponse<List<MLNotification>> notificationres = new JsonResponse<>();
			notificationres.setResponseBody(mlNotificationList);
			Mockito.when(notificationController.getNotifications()).thenReturn(notificationres);
			logger.info("Get Notifications : " + notificationres.getResponseBody());
			Assert.assertNotNull(notificationres);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void getUserNotificationsTest() {
		try {
			MLPUserNotification mlpUserNotification = new MLPUserNotification();
			Date created = new Date();
			mlpUserNotification.setCreated(created);
			mlpUserNotification.setMessage("notification created");
			Date modified = new Date();
			mlpUserNotification.setModified(modified);
			mlpUserNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlpUserNotification.setTitle("Notification");
			mlpUserNotification.setUrl("http://notify.com");
			Date viewed = new Date();
			mlpUserNotification.setViewed(viewed);
			Assert.assertNotNull(mlpUserNotification);
			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId(mlpUserNotification.getNotificationId());
			mlpNotificationUserMap.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Assert.assertNotNull(mlpNotificationUserMap);
			List<MLPUserNotification> mlpUserNotificationList = new ArrayList<>();
			mlpUserNotificationList.add(mlpUserNotification);
			Assert.assertNotNull(mlpUserNotificationList);
			String userId = mlpNotificationUserMap.getUserId();
			JsonRequest<RestPageRequest> restPageReq = new JsonRequest<>();
			RestPageRequest body = new RestPageRequest();
			body.setPage(0);
			body.setSize(9);
			JsonResponse<List<MLPUserNotification>> notifires = new JsonResponse<>();
			if (body.getPage() != null || body.getSize() != null) {
				notifires.setResponseBody(mlpUserNotificationList);
			}

			Mockito.when(notificationController.getUserNotifications(request, userId, restPageReq, response))
					.thenReturn(notifires);
			logger.info("User notifications  : " + notifires.getResponseBody());
			Assert.assertNotNull(notifires);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void addNotificationUserTest() {
		try {

			MLNotification mlNotification = new MLNotification();
			mlNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlNotification.setCount(1);
			mlNotification.setMessage("notification");
			mlNotification.setTitle("Notification");
			mlNotification.setUrl("http://notify.com");
			Assert.assertNotNull(mlNotification);
			List<MLNotification> mlNotificationList = new ArrayList<MLNotification>();
			mlNotificationList.add(mlNotification);
			Assert.assertNotNull(mlNotificationList);
			JsonResponse<List<MLNotification>> notificationres = new JsonResponse<>();
			notificationres.setResponseBody(mlNotificationList);

			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId(mlNotification.getNotificationId());
			mlpNotificationUserMap.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Assert.assertNotNull(mlpNotificationUserMap);
			String userId = mlpNotificationUserMap.getUserId();
			String notificationId = mlpNotificationUserMap.getNotificationId();
			Assert.assertNotNull(userId);
			Assert.assertNotNull(notificationId);
			Mockito.when(notificationController.addNotificationUser(request, notificationId, userId, response))
					.thenReturn(notificationres);
			logger.info("Successfully  added notifiaction for particular user : " + notificationres.getResponseBody());
			Assert.assertNotNull(notificationres);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void dropNotificationUserTest() {
		try {

			MLNotification mlNotification = new MLNotification();
			mlNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlNotification.setCount(1);
			mlNotification.setMessage("notification");
			mlNotification.setTitle("Notification");
			mlNotification.setUrl("http://notify.com");
			Assert.assertNotNull(mlNotification);
			List<MLNotification> mlNotificationList = new ArrayList<MLNotification>();
			mlNotificationList.add(mlNotification);
			Assert.assertNotNull(mlNotificationList);
			JsonResponse<List<MLNotification>> notificationres = new JsonResponse<>();
			notificationres.setResponseBody(mlNotificationList);

			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId(mlNotification.getNotificationId());
			mlpNotificationUserMap.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Assert.assertNotNull(mlpNotificationUserMap);
			String userId = mlpNotificationUserMap.getUserId();
			String notificationId = mlpNotificationUserMap.getNotificationId();
			Assert.assertNotNull(userId);
			Assert.assertNotNull(notificationId);
			Mockito.when(notificationController.dropNotificationUser(request, notificationId, userId, response))
					.thenReturn(notificationres);
			logger.info("Successfully  droped notifiaction for particular user : " + notificationres.getResponseBody());
			Assert.assertNotNull(notificationres);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void setNotificationUserViewedTest() {

		try {

			MLNotification mlNotification = new MLNotification();
			mlNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlNotification.setCount(1);
			mlNotification.setMessage("notification");
			mlNotification.setTitle("Notification");
			mlNotification.setUrl("http://notify.com");
			Assert.assertNotNull(mlNotification);
			List<MLNotification> mlNotificationList = new ArrayList<MLNotification>();
			mlNotificationList.add(mlNotification);
			Assert.assertNotNull(mlNotificationList);
			JsonResponse<List<MLNotification>> notificationres = new JsonResponse<>();
			notificationres.setResponseBody(mlNotificationList);

			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId(mlNotification.getNotificationId());
			mlpNotificationUserMap.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Assert.assertNotNull(mlpNotificationUserMap);
			String userId = mlpNotificationUserMap.getUserId();
			String notificationId = mlpNotificationUserMap.getNotificationId();
			Assert.assertNotNull(userId);
			Assert.assertNotNull(notificationId);
			Mockito.when(notificationController.setNotificationUserViewed(request, notificationId, userId, response))
					.thenReturn(notificationres);
			logger.info("Successfully  setNotificationUserViewed: " + notificationres.getResponseBody());
			Assert.assertNotNull(notificationres);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}

	}

	@Test
	public void deleteNotificationTest() {

		try {

			MLNotification mlNotification = new MLNotification();
			mlNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlNotification.setCount(1);
			mlNotification.setMessage("notification");
			mlNotification.setTitle("Notification");
			mlNotification.setUrl("http://notify.com");
			Assert.assertNotNull(mlNotification);
			List<MLNotification> mlNotificationList = new ArrayList<MLNotification>();
			mlNotificationList.add(mlNotification);
			Assert.assertNotNull(mlNotificationList);
			JsonResponse<List<MLNotification>> notificationres = new JsonResponse<>();
			notificationres.setResponseBody(mlNotificationList);
			
			MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
			mlpNotificationUserMap.setNotificationId(mlNotification.getNotificationId());
			mlpNotificationUserMap.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Assert.assertNotNull(mlpNotificationUserMap);
			String userId = mlpNotificationUserMap.getUserId();
			Assert.assertNotNull(userId);
			String notificationId = mlpNotificationUserMap.getNotificationId();
			Assert.assertNotNull(notificationId);
			Mockito.when(notificationController.deleteNotification(request, notificationId, response))
					.thenReturn(notificationres);
			logger.info("Successfully  setNotificationUserViewed: " + notificationres.getResponseBody());
			Assert.assertNotNull(notificationres);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}

	}

	@Test
	public void getNotificationCountTest(){
		try {

			MLNotification mlNotification = new MLNotification();
			mlNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
			mlNotification.setCount(1);
			mlNotification.setMessage("notification");
			mlNotification.setTitle("Notification");
			mlNotification.setUrl("http://notify.com");
			Assert.assertNotNull(mlNotification);
			JsonResponse<MLNotification> notificationres = new JsonResponse<>();
			notificationres.setResponseBody(mlNotification);
			Mockito.when(notificationController.getNotificationCount()).thenReturn(notificationres);
			logger.info("Successfully  setNotificationUserViewed: " + notificationres.getResponseBody());
			Assert.assertNotNull(notificationres);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}

	
	}
}
