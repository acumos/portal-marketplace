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

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.lang.invoke.MethodHandles;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.common.exception.UserServiceException;
import org.acumos.portal.be.controller.OauthUserServiceController;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.AbstractResponseObject;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.transport.UserMasterObject;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(MockitoJUnitRunner.class)
public class OauthUserServiceControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	private MockMvc mockMvc;
	
	@InjectMocks
	private OauthUserServiceController oauthServiceController;
	
	@Mock
	private UserService userService;
	
	@Mock
	private JwtTokenUtil jwtTokenUtil;
	
	@Mock
	private UserRoleService userRoleService;
	
	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(oauthServiceController).build();

	}
	
	@Test
	public void createUserTest() {
		UserMasterObject userMasterObject = new UserMasterObject();
		Instant created = Instant.now();
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
			MLRole role = new MLRole();
			role.setName("MLP System User");
			MLPRole mlpRole = getMLPRole();
			User user=PortalUtils.convertUserMasterIntoMLPUser(userMasterObject);
			Mockito.when(userService.save(user))
					.thenReturn(user);
			Assert.assertNotNull(user);
			oauthServiceController.createUser(request, userMasterObject, response);
			Mockito.when(userRoleService.createRole(role))
			.thenReturn(mlpRole);
			Assert.assertNotNull(mlpRole);
			oauthServiceController.createUser(request, userMasterObject, response);
			
			logger.debug("Successfully created user ", userMasterObject);
			Assert.assertNotNull(userMasterObject);
		} catch (Exception | UserServiceException e) {
			logger.debug("Exception Occurred while createUser()", e);
		}
	}

	@Test
	public void loginTest() {
		User user = new User();
		Instant created = Instant.now();
		try {
			MLPUser mlpUser = getMLPUser();
			user.setUserId("09514016-2f24-4a0c-8587-f0f0d2ff03b3");
			user.setActive("Y");
			user.setCreated(created);
			user.setEmailId("testEmail1@att.com");
			user.setFirstName("Test_First_name");
			user.setUsername("Test_User_name");
			//user.setJwtToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlSWQiOiIxIiwibmFtZSI6IlVzZXIiLCJhY3RpdmUiOmZhbHNlLCJjcmVhdGVkIjoxNTEwMjIwMDQzMDAwLCJtb2RpZmllZCI6MTUxMDIyMDA0MzAwMH1dLCJjcmVhdGVkIjoxNTEwNzUzMjgzMDUyLCJleHAiOjE1MTEzNTgwODMsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUwODIzNDY5NjAwMCwibW9kaWZpZWQiOjE1MTAyMjkyMzkwMDAsInVzZXJJZCI6IjQxMDU4MTA1LTY3ZjQtNDQ2MS1hMTkyLWY0Y2I3ZmRhZmQzNCIsImZpcnN0TmFtZSI6InNkZnNkZiIsIm1pZGRsZU5hbWUiOiJzZGZzZGYiLCJsYXN0TmFtZSI6ImRmc2RmIiwib3JnTmFtZSI6InNkZnNkZiIsImVtYWlsIjoiZGZzZGYiLCJsb2dpbk5hbWUiOiJNYW5pbW96aGlUMSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjpudWxsLCJwaWN0dXJlIjpudWxsfX0.8LuG8jsQvDDhhS037R6I1AwGOFkq3jTMxg2mLYbtEsKqzJcrS7fa0iwOGpvAMejx0GKoEZAhfWLgR6YVaSwK1w");
			
			String jwtToken = user.getJwtToken();
//			Assert.assertNotNull(jwtToken);
//			Assert.assertEquals(jwtToken, user.getJwtToken());
//			AbstractResponseObject value = new AbstractResponseObject();
//			
//			value.setJwtToken(jwtToken);
			
			Mockito.when(userService.findUserByEmail(user.getEmailId())).thenReturn(mlpUser);
			Assert.assertNotNull(mlpUser);
			AbstractResponseObject value = oauthServiceController.login(request, user, response);
			logger.info("Successfully loged in");
			Assert.assertNotNull(value);
			
			String tokenValue="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlSWQiOiIxIiwibmFtZSI6IlVzZXIiLCJhY3RpdmUiOmZhbHNlLCJjcmVhdGVkIjoxNTEwMjIwMDQzMDAwLCJtb2RpZmllZCI6MTUxMDIyMDA0MzAwMH1dLCJjcmVhdGVkIjoxNTEwNzUzMjgzMDUyLCJleHAiOjE1MTEzNTgwODMsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUwODIzNDY5NjAwMCwibW9kaWZpZWQiOjE1MTAyMjkyMzkwMDAsInVzZXJJZCI6IjQxMDU4MTA1LTY3ZjQtNDQ2MS1hMTkyLWY0Y2I3ZmRhZmQzNCIsImZpcnN0TmFtZSI6InNkZnNkZiIsIm1pZGRsZU5hbWUiOiJzZGZzZGYiLCJsYXN0TmFtZSI6ImRmc2RmIiwib3JnTmFtZSI6InNkZnNkZiIsImVtYWlsIjoiZGZzZGYiLCJsb2dpbk5hbWUiOiJNYW5pbW96aGlUMSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjpudWxsLCJwaWN0dXJlIjpudWxsfX0.8LuG8jsQvDDhhS037R6I1AwGOFkq3jTMxg2mLYbtEsKqzJcrS7fa0iwOGpvAMejx0GKoEZAhfWLgR6YVaSwK1w";
			Mockito.when(jwtTokenUtil.generateToken(mlpUser, null)).thenReturn(tokenValue);
			Assert.assertNotNull(tokenValue);
			
			mlpUser.setAuthToken(tokenValue);
			Mockito.when(userService.findUserByEmail(user.getEmailId())).thenReturn(mlpUser);
			Assert.assertNotNull(mlpUser);
		    value = oauthServiceController.login(request, user, response);
			logger.info("Successfully loged in");
			Assert.assertNotNull(value);
			
			
			Mockito.when(jwtTokenUtil.generateToken(mlpUser, null)).thenReturn(tokenValue);
			Assert.assertNotNull(tokenValue);
			
			Mockito.when(jwtTokenUtil.isTokenExpired(tokenValue)).thenReturn(true);
			value = oauthServiceController.login(request, user, response);
			logger.info("Successfully loged in");
			Assert.assertNotNull(value);
			
			Mockito.when(jwtTokenUtil.isTokenExpired(tokenValue)).thenReturn(false);
			value = oauthServiceController.login(request, user, response);
			logger.info("Successfully loged in");
			Assert.assertNotNull(value);
			
			Mockito.when(jwtTokenUtil.validateToken(tokenValue, mlpUser)).thenReturn(false);
			value = oauthServiceController.login(request, user, response);
			logger.info("Successfully loged in");
			Assert.assertNotNull(value);
			
			Mockito.when(jwtTokenUtil.validateToken(tokenValue, mlpUser)).thenReturn(true);
			value = oauthServiceController.login(request, user, response);
			logger.info("Successfully loged in");
			Assert.assertNotNull(value);
			
		} catch (Exception e) {
			logger.error("Exception Occurred while loginTest()", e);
		}
	}
	
	@Test
	public void getUsernameFromAuthTest() {
		String token = "mockJwtToken";
		String authorization = "Bearer " + token;
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.addHeader("Authorization", authorization);
		String usernameIn = "testUser";
		
		Mockito.when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(usernameIn);
		String usernameOut = oauthServiceController.getUsernameFromAuth(mockRequest, response);
		Assert.assertNotNull(usernameOut);
		Assert.assertEquals(usernameIn, usernameOut);
	}
	
	private MLPRole getMLPRole(){
		MLPRole mlpRole = new MLPRole();
		mlpRole.setName("Admin");
		Instant created = Instant.now();
		mlpRole.setCreated(created);
		mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
		return mlpRole;
	}
	
	private MLPUser getMLPUser(){
		MLPUser mlpUser = new MLPUser();
		mlpUser.setActive(true);
		mlpUser.setFirstName("test-first-name");			
		mlpUser.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
		mlpUser.setLoginName("test-User-Name");
		return mlpUser;
	}

}
