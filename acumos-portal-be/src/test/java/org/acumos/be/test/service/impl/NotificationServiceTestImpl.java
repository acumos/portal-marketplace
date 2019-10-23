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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.impl.NotificationServiceImpl;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLUserNotifPref;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTestImpl {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@InjectMocks
	NotificationServiceImpl impl;
	
	private final String url = "http://localhost:8000/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";
	private final String NOTIFICATION_TEMPLATE_URL = "/ccds/notif";
	private final String NOTIFICATION_TEMPLATE_URL1 = "/ccds/notif?page=0&size=0";
	private final String NOTIFICATION_USER_URL="/ccds/notif/user/41058105-67f4-4461-a192-f4cb7fdafd34?page=1&size=9";
	private final String ADD_NOTIFICATION_URL="/ccds/notif/de70d37d-c556-4d5f-8582-bed318ac3e49/user/1810f833-8698-4233-add4-091e34b8703c";
	private final String DELETE_USER_FROM_NOTIFICATION_URL="/ccds/notif/de70d37d-c556-4d5f-8582-bed318ac3e49/user/1810f833-8698-4233-add4-091e34b8703c";
	private final String DELETE_NOTIFICATION_URL="/ccds/notif/de70d37d-c556-4d5f-8582-bed318ac3e49";
	private final String COUNT_NOTIFICATION_URL="/ccds/notif/count";
	private final String NOTIFICATIONPREF_URL="/ccds/notif/notifpref";
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	Environment env;
	
	@Test
	public void createNotificationTest() throws JsonProcessingException {
				MLPNotification mlpNotification = new MLPNotification();
				mlpNotification.setMessage("notification created for view count");
				mlpNotification.setTitle("Notification");
				mlpNotification.setUrl("http://notify.com");
				setCdsProperty();
				
				MLNotification notifiacation = PortalUtils.convertToMLNotification(mlpNotification);
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
					jsonStr = Obj.writeValueAsString(mlpNotification); 
				if (mlpNotification != null) {
					stubFor(post(urlEqualTo(NOTIFICATION_TEMPLATE_URL)).willReturn(
			                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			                .withBody(jsonStr)));
					
					MLNotification mlNotification= impl.createNotification(mlpNotification);
					Assert.assertNotNull(mlNotification);
					Assert.assertEquals(notifiacation.getMessage(), mlNotification.getMessage());
					Assert.assertEquals(notifiacation.getTitle(), mlNotification.getTitle());
					logger.info("Successfully created notification " + notifiacation);
				}
	}

	@Test
	public void getNotificationsTest() throws JsonProcessingException {
				MLPNotification mlpNotification = new MLPNotification();
				mlpNotification.setMessage("notification created for view count");
				mlpNotification.setTitle("Notification");
				mlpNotification.setUrl("http://notify.com");
				setCdsProperty();
				MLNotification notifiacation = PortalUtils.convertToMLNotification(mlpNotification);
	
				List<MLNotification> mlNotificationList = new ArrayList<MLNotification>();
				mlNotificationList.add(notifiacation);
				
				PageRequest pageRequest = PageRequest.of(0, 3);
				int totalElements = 15;
				RestPageResponse<MLNotification> restResponse = new RestPageResponse<>(mlNotificationList, pageRequest, totalElements);
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(restResponse); 
				
				stubFor(get(urlEqualTo(NOTIFICATION_TEMPLATE_URL1)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				
				if (mlNotificationList != null) {
					List<MLNotification> mlLNotificationList=impl.getNotifications();
					logger.info("Successfully fetched notifications ");
					Assert.assertNotNull(mlLNotificationList);
					Assert.assertEquals(mlNotificationList.get(0).getMessage(), mlLNotificationList.get(0).getMessage());
				}
	}

	@Test
	public void getUserNotificationsTest() throws JsonProcessingException {
				RestPageRequest restPageRequest = new RestPageRequest();
				restPageRequest.setSize(9);
				restPageRequest.setPage(1);
				MLPUserNotification mlpUserNotification = new MLPUserNotification();
				
				mlpUserNotification.setMessage("notification created");
				
				mlpUserNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbfc9");
				mlpUserNotification.setTitle("Notification");
				mlpUserNotification.setUrl("http://notify.com");
				MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
				mlpNotificationUserMap.setNotificationId(mlpUserNotification.getNotificationId());
				mlpNotificationUserMap.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
	
				List<MLPUserNotification> mlpUserNotificationList = new ArrayList<>();
				mlpUserNotificationList.add(mlpUserNotification);
	
				String userId = mlpNotificationUserMap.getUserId();
				setCdsProperty();
				PageRequest pageRequest = PageRequest.of(0, 3);
				int totalElements = 15;
				RestPageResponse<MLPUserNotification> restResponse = new RestPageResponse<>(mlpUserNotificationList, pageRequest, totalElements);
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(restResponse); 
				
				stubFor(get(urlEqualTo(NOTIFICATION_USER_URL)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				
				if (restPageRequest.getPage() != null && restPageRequest.getSize() != null) {
					List<MLPUserNotification>  mlpNotificationList=impl.getUserNotifications(userId, restPageRequest);
					logger.info("User based notification fetched successfully ");
					Assert.assertNotNull(mlpNotificationList);
					Assert.assertEquals(mlpUserNotificationList.get(0).getMessage(), mlpNotificationList.get(0).getMessage());
				}
	}

	@Test
	public void addNotificationUserTest() throws JsonProcessingException {
				MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
				mlpNotificationUserMap.setNotificationId("de70d37d-c556-4d5f-8582-bed318ac3e49");
				mlpNotificationUserMap.setUserId("1810f833-8698-4233-add4-091e34b8703c");
	
				String notificationId = mlpNotificationUserMap.getNotificationId();
				String userId = mlpNotificationUserMap.getUserId();
				setCdsProperty();
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(mlpNotificationUserMap);
				stubFor(post(urlEqualTo(ADD_NOTIFICATION_URL)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				if (notificationId != null && userId != null) {
					impl.addNotificationUser(notificationId, userId);
					logger.info("Successfully added notification for particuler user ");
					Assert.assertNotNull(notificationId);
					Assert.assertNotNull(userId);
					Assert.assertEquals(mlpNotificationUserMap, mlpNotificationUserMap);
				}
		}

	@Test
	public void dropNotificationUserTest() throws JsonProcessingException {
				MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
				mlpNotificationUserMap.setNotificationId("de70d37d-c556-4d5f-8582-bed318ac3e49");
				mlpNotificationUserMap.setUserId("1810f833-8698-4233-add4-091e34b8703c");
	
				String notificationId = mlpNotificationUserMap.getNotificationId();
				String userId = mlpNotificationUserMap.getUserId();
				setCdsProperty();
				
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(mlpNotificationUserMap);
				
				WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo(DELETE_USER_FROM_NOTIFICATION_URL))
				        .willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				            .withBody(jsonStr)));
				WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo(DELETE_NOTIFICATION_URL))
				        .willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				            .withBody(jsonStr)));
				if (notificationId != null && userId != null) {
					impl.dropNotificationUser(notificationId, userId);
					logger.info("Successfully dropped notification for particuler user ");
					Assert.assertNotNull(notificationId);
					Assert.assertNotNull(userId);
					Assert.assertEquals(mlpNotificationUserMap, mlpNotificationUserMap);
				}
	}

	@Test
	public void setNotificationUserViewedTest() throws JsonProcessingException {
				MLPNotifUserMap mlpNotificationUserMap = new MLPNotifUserMap();
				mlpNotificationUserMap.setNotificationId("de70d37d-c556-4d5f-8582-bed318ac3e49");
				mlpNotificationUserMap.setUserId("1810f833-8698-4233-add4-091e34b8703c");
	
				String notificationId = mlpNotificationUserMap.getNotificationId();
				String userId = mlpNotificationUserMap.getUserId();
	
				setCdsProperty();
				
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(mlpNotificationUserMap);
				WireMock.stubFor(WireMock.put(WireMock.urlEqualTo(DELETE_USER_FROM_NOTIFICATION_URL))
				        .willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				            .withBody(jsonStr)));
				if (notificationId != null && userId != null) {
					impl.setNotificationUserViewed(notificationId, userId);
					logger.info("Successfully setNotificationUserViewed ");
					Assert.assertNotNull(notificationId);
					Assert.assertNotNull(userId);
					Assert.assertEquals(mlpNotificationUserMap, mlpNotificationUserMap);
				}
	}

	@Test
	public void deleteNotificationTest() throws JsonProcessingException {
				MLNotification mlNotification = new MLNotification();
				mlNotification.getNotificationId();
	
				String notificationId = "de70d37d-c556-4d5f-8582-bed318ac3e49";
				Assert.assertNotNull(notificationId);
				
				setCdsProperty();
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(notificationId);
				
				WireMock.stubFor(WireMock.delete(WireMock.urlEqualTo(DELETE_NOTIFICATION_URL))
				        .willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				            .withBody(jsonStr)));
				
				if (notificationId != null) {
					impl.deleteNotification(notificationId);
					logger.info("Notification deleted successfully ");
				}
	}

	@Test
	public void getNotificationCountTest() throws JsonProcessingException {
				int value=2;
				setCdsProperty();
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(value);
				stubFor(get(urlEqualTo(COUNT_NOTIFICATION_URL)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				int returnValue=impl.getNotificationCount();
				
				logger.info("Notification count fetched ");
				Assert.assertNotNull(returnValue);
				Assert.assertEquals(value, returnValue);
	}
	
	@Test
	public void generateNotificationTest() throws JsonProcessingException {
				MLPNotification mlpNotification = new MLPNotification();
				mlpNotification.setMessage("notification created for view count");
				mlpNotification.setTitle("Notification");
				mlpNotification.setUrl("http://notify.com");
				mlpNotification.setNotificationId("de70d37d-c556-4d5f-8582-bed318ac3e49");
				String userId = "1810f833-8698-4233-add4-091e34b8703c";
				setCdsProperty();
				
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(mlpNotification);
				stubFor(post(urlEqualTo(NOTIFICATION_TEMPLATE_URL)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				
				stubFor(post(urlEqualTo(ADD_NOTIFICATION_URL)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				
				impl.generateNotification(mlpNotification, userId);
				logger.info("Successfully generate notification for user");
				Assert.assertNotNull(userId);
	}
	
	@Test
	public void generateNotificationWhenNotificationIsNullTest() throws JsonProcessingException {
				MLPNotification mlpNotification =null;
				String userId = null;
				setCdsProperty();
				
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(mlpNotification);
				stubFor(post(urlEqualTo(NOTIFICATION_TEMPLATE_URL)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				
				stubFor(post(urlEqualTo(ADD_NOTIFICATION_URL)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				
				impl.generateNotification(mlpNotification, userId);
				logger.info("Failed to generate user notifications");
	}
	
	@Test
	public void createUserNotificationPreferenceTest() throws JsonProcessingException {
				MLUserNotifPref mlUserNotifPref =new MLUserNotifPref(); 
				mlUserNotifPref.setUserId("1810f833-8698-4233-add4-091e34b8703c");
				setCdsProperty();
				
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(mlUserNotifPref);
				stubFor(post(urlEqualTo(NOTIFICATIONPREF_URL)).willReturn(
		                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
		                .withBody(jsonStr)));
				MLUserNotifPref mlpUserNotifPref =impl.createUserNotificationPreference(mlUserNotifPref);
				logger.info("Successfully create notification Preferences for user");
					Assert.assertNotNull(mlpUserNotifPref);
	}
	
	@Test
	public void updateUserNotificationPreferenceTest() throws JsonProcessingException {
				MLUserNotifPref mlUserNotifPref =new MLUserNotifPref(); 
				mlUserNotifPref.setUserId("1810f833-8698-4233-add4-091e34b8703c");
				mlUserNotifPref.setUserNotifPrefId(123L);
				setCdsProperty();
				
				ObjectMapper Obj = new ObjectMapper();
				String jsonStr=null;
				jsonStr = Obj.writeValueAsString(mlUserNotifPref);
				WireMock.stubFor(WireMock.put(WireMock.urlEqualTo("/ccds/notif/notifpref/123"))
				        .willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				            .withBody(jsonStr)));
				impl.updateUserNotificationPreference(mlUserNotifPref);
				logger.info("Successfully update notification Preferences for user");
	}
	
	private void setCdsProperty() {
		when(env.getProperty("cdms.client.url")).thenReturn(url);
		when(env.getProperty("cdms.client.username")).thenReturn(user);
		when(env.getProperty("cdms.client.password")).thenReturn(pass);
	}
}
