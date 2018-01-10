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

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.UserServiceException;
import org.acumos.portal.be.controller.UserServiceController;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.PasswordDTO;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceControllerTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UserServiceControllerTest.class);
    final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();


	@InjectMocks
	UserServiceController userServiceController ;
	
	@Mock
	private UserService userService;
	
	@Test
	public void createUserTest() {
		try {
			User user = getUser();
			JsonRequest<User> userReq = new JsonRequest<User>();
			userReq.setBody(user);
			userReq.getBody();

			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(userReq);
			MLPUser mlpUser = getMLPUser();
			Mockito.when(userService.findUserByEmail(userReq.getBody().getEmailId())).thenReturn(mlpUser);
			value =userServiceController.createUser(request, userReq, response);
			logger.info("successfully  created user ");
			Assert.assertNotNull(value);
		} catch (Exception | UserServiceException e) {
			
			logger.debug("Error while creating user profile ", e);
		}
	}

	@Test
	public void updateUserTest() {
		try {
			User user = getUser();
			JsonRequest<User> userReq = new JsonRequest<User>();
			userReq.setBody(user);
			userReq.getBody();
			MLPUser mlpUser = getMLPUser();
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(userReq);
			Mockito.when(userService.findUserByEmail(userReq.getBody().getEmailId())).thenReturn(mlpUser);
			Mockito.when(userService.findUserByUsername(userReq.getBody().getUsername())).thenReturn(mlpUser);
			Assert.assertNotNull(mlpUser);
			value = userServiceController.updateUser(request, userReq, response);
			logger.info("successfully  updated  user details");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			
			logger.debug("Error while updating user profile ", e);
		}
	}

	@Test
	public void forgetPasswordTest() {
		try {
			User user = getUser();
			Assert.assertNotNull(user);
			JsonRequest<User> userReq = new JsonRequest<User>();
			userReq.setBody(user);
			userReq.getBody();
			Assert.assertNotNull(userReq);
			MLPUser mlpUser = getMLPUser();
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(userReq);
			Mockito.when(userService.findUserByEmail(userReq.getBody().getEmailId())).thenReturn(mlpUser);
			Assert.assertNotNull(mlpUser);
			value = userServiceController.forgetPassword(request, userReq, response);
			logger.info("forgetPasswordTest");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			
			logger.debug("Error while changing the password ", e);
		}
	}

	@Test
	public void changeUserPasswordTest() { 
		try {
			User user = getUser();

			JsonRequest<User> userReq = new JsonRequest<User>();
			userReq.setBody(user);
			userReq.getBody();
			Assert.assertNotNull(userReq);
			PasswordDTO passwordDTO = new PasswordDTO();
			passwordDTO.setNewPassword("newPassword");
			passwordDTO.setOldPassword("oldpassword");
			passwordDTO.setUserId(user.getUserId());

			JsonResponse<Object> valuepass = new JsonResponse<>();
			valuepass.setResponseBody(passwordDTO);
			boolean flag= true;
			Mockito.when(userService.changeUserPassword(passwordDTO.getUserId(), passwordDTO.getOldPassword(), passwordDTO.getNewPassword())).thenReturn(flag);
			valuepass = userServiceController.changeUserPassword(request, passwordDTO, response);
			logger.info("Successfully changed user profile password");
			Assert.assertNotNull(valuepass);
		} catch (Exception e) {
			
			logger.debug("Error while changeUserPasswordTest ", e);
		}
	}

	@Test
	public void getUserAccountDetailsTest() {
		try {

			User user = getUser();
			Assert.assertNotNull(user);
			JsonRequest<User> userReq = new JsonRequest<User>();
			userReq.setBody(user);
			userReq.getBody();
			Assert.assertNotNull(userReq);
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			Assert.assertNotNull(mlpUser);
			JsonResponse<MLPUser> value = new JsonResponse<>();
			value.setResponseBody(mlpUser);

			Mockito.when(userService.findUserByUserId(userReq.getBody().getUserId())).thenReturn(mlpUser);
			value= userServiceController.getUserAccountDetails(userReq);
			logger.info("Successfully fectched user details ");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			
			logger.debug("Error while getUserAccountDetailsTest : ", e);
		}
	}

	@Test
	public void getAllUsersTest() {
		try {
			User user = getUser();
			Assert.assertNotNull(user);
			JsonResponse<List<User>> userList = new JsonResponse<List<User>>();
			List<User> responseBody = new ArrayList<User>();
			responseBody.add(user);
			userList.setResponseBody(responseBody);
			userList.getResponseBody();
			Mockito.when(userService.getAllUser()).thenReturn(responseBody);
			Assert.assertNotNull(responseBody);
			userList = userServiceController.getAllUsers(request, response);
			logger.info("Successfully fectched list of user details ");
			Assert.assertNotNull(userList);
		} catch (Exception e) {
			logger.debug("Error while getAllUsersTest : ", e);
		}

	}

	@Test
	public void getUserRoleTest() {
		try {
			MLPRole mlRole = new MLPRole();
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			
			User user = getUser();

			String userId = user.getUserId();
			
			Assert.assertEquals(userId, user.getUserId());
			
			List<MLPRole> mlprolelist = new ArrayList<MLPRole>();
			mlprolelist.add(mlRole);
			Assert.assertNotNull(mlprolelist);
			JsonResponse<List<MLPRole>> responseBody = new JsonResponse<>();
			responseBody.setResponseBody(mlprolelist);
			Mockito.when( userService.getUserRole(userId)).thenReturn(mlprolelist);
			responseBody = userServiceController.getUserRole(userId, request, response);
			logger.info("Successfully fectched list of user details according user roles : ",
					responseBody.getResponseBody().toString());
			Assert.assertNotNull(responseBody);
		} catch (Exception e) {
			logger.debug("Error while getUserRoleTest : ", e);
		}
	}
	
	private User getUser(){
		User user = new User();
		user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		user.setFirstName("UserFirstName");
		user.setLastName("UserLastName");
		user.setUsername("User1");
		user.setEmailId("user1@emial.com");
		user.setActive("Y");
		user.setPassword("password");
		return user;
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
