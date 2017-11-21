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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.controller.OauthUserServiceController;
import org.acumos.portal.be.transport.AbstractResponseObject;
import org.acumos.portal.be.transport.ResponseVO;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.transport.UserMasterObject;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class OauthUserServiceControllerTest {

	private static Logger logger = LoggerFactory.getLogger(OauthUserServiceControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	OauthUserServiceController oauthServiceController = new OauthUserServiceController();

	@Test
	public void createUserTest() {
		UserMasterObject userMasterObject = new UserMasterObject();
		Date created = new Date();
		try {
			userMasterObject.setUserId("09514016-2f24-4a0c-8587-f0f0d2ff03b3");
			userMasterObject.setActive(true);
			userMasterObject.setCreated(created);
			userMasterObject.setDisplayName("Tester");
			userMasterObject.setEmailId("testEmail1@att.com");
			userMasterObject.setFirstName("Test_First_name");
			userMasterObject.setAccessToken(
					"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlSWQiOiIxIiwibmFtZSI6IlVzZXIiLCJhY3RpdmUiOmZhbHNlLCJjcmVhdGVkIjoxNTEwMjIwMDQzMDAwLCJtb2RpZmllZCI6MTUxMDIyMDA0MzAwMH1dLCJjcmVhdGVkIjoxNTEwNzUzMjgzMDUyLCJleHAiOjE1MTEzNTgwODMsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUwODIzNDY5NjAwMCwibW9kaWZpZWQiOjE1MTAyMjkyMzkwMDAsInVzZXJJZCI6IjQxMDU4MTA1LTY3ZjQtNDQ2MS1hMTkyLWY0Y2I3ZmRhZmQzNCIsImZpcnN0TmFtZSI6InNkZnNkZiIsIm1pZGRsZU5hbWUiOiJzZGZzZGYiLCJsYXN0TmFtZSI6ImRmc2RmIiwib3JnTmFtZSI6InNkZnNkZiIsImVtYWlsIjoiZGZzZGYiLCJsb2dpbk5hbWUiOiJNYW5pbW96aGlUMSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjpudWxsLCJwaWN0dXJlIjpudWxsfX0.8LuG8jsQvDDhhS037R6I1AwGOFkq3jTMxg2mLYbtEsKqzJcrS7fa0iwOGpvAMejx0GKoEZAhfWLgR6YVaSwK1w");
			userMasterObject.setImageURL(
					"https://www.google.co.in/search?q=google+images&oq=google+im&aqs=chrome.1.69i57j0l5.6032j0j7&sourceid=chrome&ie=UTF-8#");
			userMasterObject.setUsername("Test_User_name");

			Mockito.when(oauthServiceController.createUser(request, userMasterObject, response))
					.thenReturn(userMasterObject);
			logger.debug("Successfully created user ", userMasterObject);
		} catch (Exception e) {
			ResponseVO responseVO = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Failed");
			logger.debug("Exception Occurred while createUser()", e);
		}
	}

	@Test
	public void loginTest() {
		User user = new User();
		Date created = new Date();
		try {
			user.setUserId("09514016-2f24-4a0c-8587-f0f0d2ff03b3");
			user.setActive("Y");
			user.setCreated(created);
			user.setEmailId("testEmail1@att.com");
			user.setFirstName("Test_First_name");
			user.setUsername("Test_User_name");
			user.setJwtToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlSWQiOiIxIiwibmFtZSI6IlVzZXIiLCJhY3RpdmUiOmZhbHNlLCJjcmVhdGVkIjoxNTEwMjIwMDQzMDAwLCJtb2RpZmllZCI6MTUxMDIyMDA0MzAwMH1dLCJjcmVhdGVkIjoxNTEwNzUzMjgzMDUyLCJleHAiOjE1MTEzNTgwODMsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUwODIzNDY5NjAwMCwibW9kaWZpZWQiOjE1MTAyMjkyMzkwMDAsInVzZXJJZCI6IjQxMDU4MTA1LTY3ZjQtNDQ2MS1hMTkyLWY0Y2I3ZmRhZmQzNCIsImZpcnN0TmFtZSI6InNkZnNkZiIsIm1pZGRsZU5hbWUiOiJzZGZzZGYiLCJsYXN0TmFtZSI6ImRmc2RmIiwib3JnTmFtZSI6InNkZnNkZiIsImVtYWlsIjoiZGZzZGYiLCJsb2dpbk5hbWUiOiJNYW5pbW96aGlUMSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjpudWxsLCJwaWN0dXJlIjpudWxsfX0.8LuG8jsQvDDhhS037R6I1AwGOFkq3jTMxg2mLYbtEsKqzJcrS7fa0iwOGpvAMejx0GKoEZAhfWLgR6YVaSwK1w");
			
			String jwtToken = user.getJwtToken();
			
			AbstractResponseObject value = new AbstractResponseObject();
			
			value.setJwtToken(jwtToken);
			
			Mockito.when(oauthServiceController.login(request, user, response)).thenReturn(value);
			logger.info("Successfully loged in");
		} catch (Exception e) {
			ResponseVO responseVO = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Failed");
			logger.error("Exception Occurred while loginTest()", e);
		}
	}

}
