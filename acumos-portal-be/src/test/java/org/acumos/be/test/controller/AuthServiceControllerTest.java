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

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.MalformedException;
import org.acumos.portal.be.controller.AuthServiceController;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.AbstractResponseObject;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class AuthServiceControllerTest {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AuthServiceControllerTest.class);

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	AuthServiceController authServiceController;
	
	@Mock
	UserService userService;
	
	@Mock
	JwtTokenUtil jwtTokenUtil;
	
	@Mock
	Environment env;
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Test
	public void login(){
		
		User user1 = new User();
		user1.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		user1.setFirstName("UserFirstName");
		user1.setLastName("UserLastName");
		user1.setUsername("User1");
		user1.setEmailId("user1@emial.com");
		user1.setActive("Y");
		user1.setPassword("password");
		MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user1);
		String username = user1.getUsername();
		String password = user1.getPassword();
		Mockito.when(userService.login(username, password)).thenReturn(mlpUser);
//		userService.login(username, password);
		String emailId = user1.getEmailId();
		Mockito.when(userService.findUserByEmail(emailId)).thenReturn(mlpUser);
//		userService.findUserByEmail(emailId );
		JsonRequest<User> user = new JsonRequest<>();
		user.setBody(user1);
		AbstractResponseObject responseObject  = authServiceController.login(request, user , response);
		Assert.assertNotNull(responseObject);
		user.getBody().setEmailId("");
		user.getBody().setUsername("");
		responseObject  = authServiceController.login(request, user , response);
		Assert.assertNotNull(responseObject);
		user.getBody().setUsername("");
		responseObject  = authServiceController.login(request, user , response);
		Assert.assertNotNull(responseObject);
		user1.setActive("N");
		mlpUser = PortalUtils.convertToMLPUserForUpdate(user1);
		Mockito.when(userService.login(username, password)).thenReturn(mlpUser);
		Mockito.when(userService.findUserByEmail(emailId)).thenReturn(mlpUser);
		user = new JsonRequest<>();
		user.setBody(user1);
		responseObject  = authServiceController.login(request, user , response);
		Assert.assertNotNull(responseObject);
		
		mlpUser.setActive(false);
		mlpUser.setEmail(null);
		mlpUser.setLoginPassExpire(new Date());
		responseObject  = authServiceController.login(request, user , response);
	}
	
	@Test
	public void logoutTest(){
		User user1 = new User();
		user1.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		user1.setFirstName("UserFirstName");
		user1.setLastName("UserLastName");
		user1.setUsername("User1");
		user1.setEmailId("user1@emial.com");
		user1.setActive("Y");
		user1.setPassword("password");
		JsonRequest<User> user = new JsonRequest<>();
		user.setBody(user1);
		JsonResponse<Object> responseObject  = authServiceController.logout(request, user , response);
		Assert.assertNull(responseObject);
	}
	
	@Test
	public void jwtLogin(){
		User user1 = new User();
		user1.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		user1.setFirstName("UserFirstName");
		user1.setLastName("UserLastName");
		user1.setUsername("User1");
		user1.setEmailId("user1@emial.com");
		user1.setActive("Y");
		user1.setPassword("password");
		user1.setJwtToken("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		String username = user1.getUsername();
		String password = user1.getPassword();
		
		MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user1);
		mlpUser.setAuthToken("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		Assert.assertNotNull(mlpUser);
		
		JsonRequest<User> user = new JsonRequest<>();
		user.setBody(user1);
		
		MLPRole mlpRole = new MLPRole();
		mlpRole.setName("Admin");
		Date created = new Date();
		mlpRole.setCreated(created);
		mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
		Assert.assertNotNull(mlpRole);
		List<MLPRole> mlpRoles = new ArrayList<>();
		mlpRoles.add(mlpRole);
		String generatedToken = user1.getJwttoken();
		Mockito.when(userService.login(username, password)).thenReturn(mlpUser);
		Mockito.when(userService.getUserRole(mlpUser.getUserId())).thenReturn(mlpRoles);
		Mockito.when(jwtTokenUtil.generateToken(mlpUser, null)).thenReturn(generatedToken);
//		userService.login(username, password);
//		userService.getUserRole(mlpUser.getUserId());
		jwtTokenUtil.generateToken(mlpUser, null);
		AbstractResponseObject abstractobject = authServiceController.jwtLogin(request, user, response, null);
		Assert.assertNotNull(abstractobject);
		
		user.getBody().setUsername(null);
		authServiceController.jwtLogin(request, user, response, null);
		
		mlpUser.setActive(false);
		authServiceController.jwtLogin(request, user, response, null);
	}
	
	@Test
	public void validateToken() throws MalformedException{
		
		User user1 = new User();
		user1.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		user1.setFirstName("UserFirstName");
		user1.setLastName("UserLastName");
		user1.setUsername("User1");
		user1.setEmailId("user1@emial.com");
		user1.setActive("Y");
		user1.setPassword("password");
		//user1.setJwttoken("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		String provider="google";
		JsonRequest<User> userObj = new JsonRequest<>();
		userObj.setBody(user1);
		//userObj.getBody().setJwtToken(user1.getJwttoken());;
		JsonResponse<Object> jsonObj = authServiceController.validateToken(request, response, userObj , provider);
		Assert.assertNotNull(jsonObj);
		
		jsonObj = authServiceController.validateToken(request, response, userObj , null);
		Assert.assertNotNull(jsonObj);
	}
}
