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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.CASAuthController;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.transport.UserMasterObject;
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

public class CASAuthControllerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private CASAuthController casAuthController;
	
	@Mock
	private Environment env;
	
	@Mock
	UserService userService;
	
	private HttpServletResponse response = new MockHttpServletResponse();
	private HttpServletRequest request = new MockHttpServletRequest();
	
	@Test
	public void getDocurlTest(){
		JsonResponse<String> responseVO =casAuthController.getDocurl(request, response);
		Assert.assertNotNull(responseVO);
	} 
	//@Test
	public void serviceValidateTest(){
		String ticket="Ticket";
		String service="Service";
		MLPRole mlpRole = new MLPRole();
		mlpRole.setName("Admin");
		Instant created = Instant.now();
		mlpRole.setCreated(created);
		mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
		Assert.assertNotNull(mlpRole);
		List<MLPRole> rolelist=new ArrayList<MLPRole>();
		rolelist.add(mlpRole);
		User user = new User();
		user.setActive("Y");
		user.setFirstName("test-first-name");
		user.setLastName("lname");
		user.setJwttoken(
				"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOm51bGwsImNyZWF0ZWQiOjE1MTAxMzgyMzY4NjcsImV4cCI6MTUxMDc0MzAzNiwibWxwdXNlciI6eyJjcmVhdGVkIjoxNTA4MjM0Njk2MDAwLCJtb2RpZmllZCI6MTUwOTk2MDg5NTAwMCwidXNlcklkIjoiNDEwNTgxMDUtNjdmNC00NDYxLWExOTItZjRjYjdmZGFmZDM0IiwiZmlyc3ROYW1lIjoiTWFuaW1vemhpVDEiLCJtaWRkbGVOYW1lIjpudWxsLCJsYXN0TmFtZSI6IlQyIiwib3JnTmFtZSI6bnVsbCwiZW1haWwiOiJNYW5pbW96aGlUMUBnbWFpLmNvbSIsImxvZ2luTmFtZSI6Ik1hbmltb3poaVQxIiwibG9naW5IYXNoIjpudWxsLCJsb2dpblBhc3NFeHBpcmUiOm51bGwsImF1dGhUb2tlbiI6bnVsbCwiYWN0aXZlIjp0cnVlLCJsYXN0TG9naW4iOm51bGwsInBpY3R1cmUiOm51bGx9fQ.bLzIAFVUIPMVE_WD0-BvMupFyHyy90mw_je1PmnvP34swv1ZUW_SL7DBoKeSGnIf_zhtDp8V8d3Q3pAiWMjLyA");
		user.setEmailId("testemail@test.com");
		user.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
		user.setLoginName("test-User-Name");
		user.setRole(mlpRole.getName());
		user.setRoleId(mlpRole.getRoleId());
		List<String> newRoleList = new ArrayList<>();
		newRoleList.add("Admin");
		MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
		Mockito.when(userService.findUserByEmail(user.getEmailId())).thenReturn(mlpUser);
		Mockito.when(userService.findUserByUsername(user.getLoginName())).thenReturn(mlpUser);
		
		UserMasterObject usermasterobject = new UserMasterObject();
    	usermasterobject.setActive(true);
    	usermasterobject.setFirstName(user.getFirstName());
    	usermasterobject.setLastName(user.getLastName());
    	usermasterobject.setEmailId(user.getEmailId());
    	usermasterobject.setUsername(user.getLoginName());
    	Assert.assertNotNull(usermasterobject);
    	user = PortalUtils.convertUserMasterIntoMLPUser(usermasterobject);
    	Mockito.when(userService.save(user)).thenReturn(user);
    	Mockito.when(userService.getUserRole(user.getUserId())).thenReturn(rolelist);
    	JsonResponse<Object> jsonResponse = new JsonResponse<>();
    	jsonResponse.setStatusCode(200);
		jsonResponse.setResponseDetail("Validation status updated Successfully");
    	jsonResponse.setContent(user);
    	jsonResponse=casAuthController.serviceValidate(response, ticket, service);
    	Assert.assertNotNull(jsonResponse);
	}
}
