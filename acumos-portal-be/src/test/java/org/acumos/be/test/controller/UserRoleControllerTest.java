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
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.UserServiceException;
import org.acumos.portal.be.controller.UserRoleController;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.service.impl.UserRoleServiceImpl;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
@RunWith(MockitoJUnitRunner.class)
public class UserRoleControllerTest {
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UserRoleControllerTest.class);


	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@InjectMocks
	UserRoleController userRoleController;
	
	@Mock
	UserRoleService userRoleService;
	
	@Mock
	UserService userService;
	
	@Mock
	UserRoleServiceImpl userRoleImpl;
	
	
	@Test
	public void getRolesListTest() throws UserServiceException {
		try {
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			List<MLRole> mlRoleList = new ArrayList<MLRole>();
			mlRoleList.add(mlRole);
			Assert.assertNotNull(mlRoleList);
			JsonResponse<List<MLRole>> roleList = new JsonResponse<>();
			roleList.setResponseBody(mlRoleList);
			Mockito.when(userRoleService.getAllRoles()).thenReturn(mlRoleList);
			//userRoleService.getAllRoles();
			JsonResponse<List<MLRole>> data = userRoleController.getRolesList();
			Assert.assertNotNull(data);
			Assert.assertEquals("Roles fetched Successfully", data.getResponseDetail());
			Mockito.when(userRoleService.getAllRoles()).thenReturn(null);
			//userRoleService.getAllRoles();
			data = userRoleController.getRolesList();
			Assert.assertNotNull(data);
			Assert.assertEquals("Error Occurred while getRolesList()", data.getResponseDetail());
			
		} catch (Exception e) {
			
			logger.info("Eception while fetching getRolesListTest ", e);
		}

	}

	@Test
	public void getRoleDetailsTest() throws UserServiceException {
		try {
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			String roleId = mlRole.getRoleId();
			Assert.assertEquals(roleId, mlRole.getRoleId());
			JsonResponse<MLRole> mlRoler = new JsonResponse<>();
			mlRoler.setResponseBody(mlRole);
			Mockito.when(userRoleService.getRole(roleId)).thenReturn(mlRole);
			mlRoler = userRoleController.getRoleDetails(request, roleId, response);
			Assert.assertNotNull(mlRoler);
			Assert.assertEquals("Role fetched Successfully", mlRoler.getResponseDetail());
			Mockito.when(userRoleService.getRole(roleId)).thenReturn(null);
			mlRoler = userRoleController.getRoleDetails(request, roleId, response);
			Assert.assertNotNull(mlRoler);
			Assert.assertEquals("Error Occurred while getRoleDetails()", mlRoler.getResponseDetail());

		} catch (Exception e) {
			logger.info("Eception while fetching getRoleDetailsTest ", e);
		}
	}

	@Test
	public void postRoleTest() throws UserServiceException {
		try {
			MLPRole mlpRole = new MLPRole();
			mlpRole.setName("Admin");
			Date created = new Date();
			mlpRole.setCreated(created);
			mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlpRole);
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			mlRole.setCreated(created);

			mlRole.setRoleId(mlpRole.getRoleId());
			Assert.assertNotNull(mlRole);
			String roleId = mlpRole.getRoleId();
			Assert.assertEquals(roleId, mlpRole.getRoleId());
			JsonResponse<MLPRole> value = new JsonResponse<>();
			value.setResponseBody(mlpRole);	
			List<String> permissionList = new ArrayList<>();
			permissionList.add("Admin");
			JsonRequest<MLRole> role = new JsonRequest<>();
			role.setBody(mlRole);
			role.getBody().setPermissionList(permissionList);;
			
			MLPRoleFunction roleFunction = new MLPRoleFunction();
			roleFunction.setRoleId(mlpRole.getRoleId());
			roleFunction.setName("Admin");
			
						
			userRoleService.createRole(mlRole);
			value = userRoleController.postRole(request, role, response);
			Assert.assertNotNull(value);
			Assert.assertEquals("Error occured while creating role", value.getResponseDetail());
			
			Mockito.when(userRoleService.createRole(mlRole)).thenReturn(mlpRole);
			Mockito.when(userRoleService.createRoleFunction(roleFunction)).thenReturn(roleFunction);
			value = userRoleController.postRole(request, role, response);
			Assert.assertNotNull(value);
			Assert.assertEquals("Role created Successfully", value.getResponseDetail());
			logger.info("Successfully created Role: ", mlRole);
			
			Mockito.when(userRoleService.createRole(mlRole)).thenReturn(mlpRole);
			Mockito.when(userRoleService.createRoleFunction(roleFunction)).thenReturn(null);
			value = userRoleController.postRole(request, role, response);
			Assert.assertNotNull(value);
			Assert.assertEquals("Error Occurred while postRole()", value.getResponseDetail());
			
		} catch (Exception e) {
			
			logger.info("Eception while fetching postRoleTest ", e);
		}
	}

	@Test
	public void updateRoleTest() {
		try {
			MLPRole mlpRole = new MLPRole();
			mlpRole.setName("Admin");
			Date created = new Date();
			mlpRole.setCreated(created);
			mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlpRole);
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			String roleId = mlpRole.getRoleId();
			Assert.assertEquals(roleId, mlRole.getRoleId());
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlpRole);

			JsonRequest<MLPRole> role = new JsonRequest<>();
			role.setBody(mlpRole);
			
			UserRoleService userRoleService = Mockito.mock(UserRoleService.class);
			Mockito.doNothing().when(userRoleService).updateRole(role);
			value = userRoleController.updateRole(role);
			
			Assert.assertNotNull(value);
			Assert.assertEquals("Role updated Successfuly", value.getResponseDetail());
		} catch (Exception e) {
			logger.info("Eception while fetching updateRoleTest ", e);
		} catch (UserServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteRoleTest() {
		try {
			MLPRole mlpRole = new MLPRole();
			mlpRole.setName("Admin");
			Date created = new Date();
			mlpRole.setCreated(created);
			mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlpRole);
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			String roleId = mlpRole.getRoleId();
			Assert.assertEquals(roleId, mlRole.getRoleId());
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlpRole);
			Assert.assertNotNull(value);
			JsonRequest<MLRole> role = new JsonRequest<>();
			role.setBody(mlRole);
			Assert.assertNotNull(role);
			
			
			UserRoleService userRoleService = Mockito.mock(UserRoleService.class);
			Mockito.doNothing().when(userRoleService).deleteRole(roleId);
			value = userRoleController.deleteRole(role);
			
			Assert.assertNotNull(value);
			Assert.assertEquals("Role deleted Successfuly", value.getResponseDetail());
					
		} catch (Exception e) {
			
			logger.info("Eception while fetching deleteRoleTest ", e);
		} catch (UserServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getRoleFunctionTest() {
		try {

			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			MLRoleFunction mlRoleFunction = new MLRoleFunction();
			mlRoleFunction.setCreated(created);
			mlRoleFunction.setMlRole(mlRole);
			mlRoleFunction.setName("Role NAme");
			mlRoleFunction.setRoleFunctionId("123");
			Assert.assertNotNull(mlRoleFunction);
			JsonRequest<MLRoleFunction> roleFunction = new JsonRequest<>();
			roleFunction.setBody(mlRoleFunction);
			Assert.assertNotNull(roleFunction);
			JsonResponse<MLRoleFunction> value = new JsonResponse<>();
			value.setResponseBody(mlRoleFunction);
			String roleFunctionId = mlRoleFunction.getRoleFunctionId();
			String roleId = mlRole.getRoleId();
			Mockito.when(userRoleService.getRoleFunction(roleId, roleFunctionId)).thenReturn(mlRoleFunction);
			value = userRoleController.getRoleFunction(roleFunction);
			Assert.assertNotNull(value);
			Assert.assertEquals("Role fetched Successfully", value.getResponseDetail());
			
			Mockito.when(userRoleService.getRoleFunction(roleId, roleFunctionId)).thenReturn(mlRoleFunction);
			value = userRoleController.getRoleFunction(roleFunction);
			Assert.assertNotNull(value);
			Assert.assertEquals("Role fetched Successfully", value.getResponseDetail());

		} catch (Exception e) {
			logger.info("Eception while fetching getRoleFunctionTest ", e);
		} catch (UserServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void createRoleFunctionTest() {
		try {
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			MLPRoleFunction mlpRoleFunction = new MLPRoleFunction();
			mlpRoleFunction.setCreated(created);
			mlpRoleFunction.setName("Role NAme");
			mlpRoleFunction.setRoleFunctionId("123");
			Assert.assertNotNull(mlpRoleFunction);
			JsonResponse<MLPRoleFunction> value = new JsonResponse<>();
			value.setResponseBody(mlpRoleFunction);
			JsonRequest<MLPRoleFunction> mlpRoleFunctionReq = new JsonRequest<>();
			mlpRoleFunctionReq.setBody(mlpRoleFunction);
			Assert.assertNotNull(mlpRoleFunctionReq);
			userRoleService.createRoleFunction(mlpRoleFunction);
			userRoleController.createRoleFunction(mlpRoleFunctionReq);
			logger.info("Successfully created the role function details ");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Eception while fetching createRoleFunctionTest ", e);
		} catch (UserServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void updateRoleFunctionTest() {
		try {
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			MLPRoleFunction mlpRoleFunction = new MLPRoleFunction();
			mlpRoleFunction.setCreated(created);
			mlpRoleFunction.setName("Role NAme");
			mlpRoleFunction.setRoleFunctionId("123");
			Assert.assertNotNull(mlpRoleFunction);
			MLPRoleFunction updateMlRoleFunction = new MLPRoleFunction();
			updateMlRoleFunction.setCreated(created);
			updateMlRoleFunction.setName("updated Role NAme");
			updateMlRoleFunction.setRoleFunctionId(mlpRoleFunction.getRoleFunctionId());
			Assert.assertNotNull(updateMlRoleFunction);
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(updateMlRoleFunction);

			JsonRequest<MLPRoleFunction> mlpRoleFunctionReq = new JsonRequest<>();
			mlpRoleFunctionReq.setBody(updateMlRoleFunction);
			Assert.assertNotNull(mlpRoleFunctionReq);
			userRoleService.updateRoleFunction(mlpRoleFunctionReq);
			userRoleController.updateRoleFunction(mlpRoleFunctionReq);
			logger.info("Successfully updated the role function details ");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Eception while fetching updateRoleFunctionTest ", e);
		} catch (UserServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void deleteRoleFunction() {

		try {
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlRole);
			MLRoleFunction mlRoleFunction = new MLRoleFunction();
			mlRoleFunction.setCreated(created);
			mlRoleFunction.setName("Role NAme");
			mlRoleFunction.setRoleFunctionId("123");
			mlRoleFunction.setMlRole(mlRole);
			Assert.assertNotNull(mlRoleFunction);
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlRoleFunction);

			JsonRequest<MLRoleFunction> mlRoleFunctionReq = new JsonRequest<>();
			mlRoleFunctionReq.setBody(mlRoleFunction);
			Assert.assertNotNull(mlRoleFunctionReq);
			String roleId =mlRole.getRoleId();
			String roleFunctionId = mlRoleFunction.getRoleFunctionId();
			
			UserRoleService userRoleService = Mockito.mock(UserRoleService.class);
			Mockito.doNothing().when(userRoleService).deleteRoleFunction(roleId, roleFunctionId);
			JsonResponse<Object>  data = userRoleController.deleteRoleFunction(mlRoleFunctionReq);
			Assert.assertNotNull(data);
			Assert.assertEquals("RoleFunction deleted Successfuly", data.getResponseDetail());
			
			mlRoleFunction.getMlRole().setRoleId(null);
			Mockito.doNothing().when(userRoleService).deleteRoleFunction(roleId, roleFunctionId);
			data = userRoleController.deleteRoleFunction(mlRoleFunctionReq);
			Assert.assertNotNull(data);
			Assert.assertEquals("Exception Occurred while deleteRoleFunction()", data.getResponseDetail());

			
			logger.info("Successfully deleted the role function details ");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			
			logger.info("Eception while fetching deleteRoleFunction ", e);
		} catch (UserServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void updateUserRoleTest() {
		MLPRole mlpRole = new MLPRole();
		mlpRole.setName("Admin");
		Date created = new Date();
		mlpRole.setCreated(created);
		mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
		Assert.assertNotNull(mlpRole);
		User user = new User();
		user.setActive("Y");
		user.setFirstName("test-first-name");
		user.setJwttoken(
				"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOm51bGwsImNyZWF0ZWQiOjE1MTAxMzgyMzY4NjcsImV4cCI6MTUxMDc0MzAzNiwibWxwdXNlciI6eyJjcmVhdGVkIjoxNTA4MjM0Njk2MDAwLCJtb2RpZmllZCI6MTUwOTk2MDg5NTAwMCwidXNlcklkIjoiNDEwNTgxMDUtNjdmNC00NDYxLWExOTItZjRjYjdmZGFmZDM0IiwiZmlyc3ROYW1lIjoiTWFuaW1vemhpVDEiLCJtaWRkbGVOYW1lIjpudWxsLCJsYXN0TmFtZSI6IlQyIiwib3JnTmFtZSI6bnVsbCwiZW1haWwiOiJNYW5pbW96aGlUMUBnbWFpLmNvbSIsImxvZ2luTmFtZSI6Ik1hbmltb3poaVQxIiwibG9naW5IYXNoIjpudWxsLCJsb2dpblBhc3NFeHBpcmUiOm51bGwsImF1dGhUb2tlbiI6bnVsbCwiYWN0aXZlIjp0cnVlLCJsYXN0TG9naW4iOm51bGwsInBpY3R1cmUiOm51bGx9fQ.bLzIAFVUIPMVE_WD0-BvMupFyHyy90mw_je1PmnvP34swv1ZUW_SL7DBoKeSGnIf_zhtDp8V8d3Q3pAiWMjLyA");
		user.setEmailId("testemail@test.com");
		user.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
		user.setLoginName("test-User-Name");
		user.setRole(mlpRole.getName());
		user.setRoleId(mlpRole.getRoleId());
		Assert.assertNotNull(user);
		String roleId = mlpRole.getRoleId();
		Assert.assertEquals(roleId, user.getRoleId());
		List<User> users = new ArrayList<>();
		users.add(user);
		JsonRequest<List<User>> userreq = new JsonRequest<>();
		userreq.setBody(users);
		try {
			for (User u : userreq.getBody()) {
				UserRoleService userRoleService = Mockito.mock(UserRoleService.class);
				Mockito.doNothing().when(userRoleService).updateUserRole(u);
			}
			JsonResponse<MLPRole> data = userRoleController.updateUserRole(userreq);
			Assert.assertNotNull(data);
			Assert.assertEquals("Role updated Successfully", data.getResponseDetail());
			logger.info("updateUserRoleTest : Role updated Successfully");
		}  catch (Exception e) {
			logger.info("Eception while fetching addUserRoleTest ", e);
		}
			
	}
	
	@Test
	public void getRolesForUserTest() {
		try {
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			MLRole mlRole = createMLRole();
			Assert.assertNotNull(mlRole);
			List<MLRole> mlRoleList = new ArrayList<MLRole>();
			mlRoleList.add(mlRole);
			Assert.assertNotNull(mlRoleList);
			Mockito.when(userRoleService.getRolesForUser(userId)).thenReturn(mlRoleList);
			JsonResponse<List<MLRole>> mlRoles = userRoleController.getRolesForUser(request, userId, response);
			Assert.assertNotNull(mlRoles);
			Assert.assertEquals("Roles for user fetched Successfully", mlRoles.getResponseDetail());
			Mockito.when(userRoleService.getRolesForUser(userId)).thenReturn(null);
			mlRoles = userRoleController.getRolesForUser(request, userId, response);
			Assert.assertNotNull(mlRoles);
			Assert.assertEquals("Error Occurred while getRolesList()", mlRoles.getResponseDetail());
		} catch (Exception e) {
			logger.info("Error Occurred while getRolesForUserTest()", e);
		}
	}
	
	@Test
	public void changeUserRolesTest() {
		JsonResponse<MLPRole> data = new JsonResponse<>();
		try {
			JsonRequest<User> jsonRequest = new JsonRequest<>();
			User user = createUser();
			jsonRequest.setBody(user);
			if(jsonRequest.getBody() != null){
				UserRoleService userRoleService = Mockito.mock(UserRoleService.class);
				Mockito.doNothing().when(userRoleService).updateUserRoles(jsonRequest.getBody());
				data = userRoleController.changeUserRoles(jsonRequest);
				Assert.assertNotNull(data);
				Assert.assertEquals("Role updated Successfully", data.getResponseDetail());
			}
		} catch (Exception e) {
			logger.info("Error Occurred while changeUserRolesTest()", e);
		}
	}
	
	@Test
	public void getRoleUsersCountTest() {
		JsonResponse<List<MLRole>> data = new JsonResponse<List<MLRole>>();
		
		try {	
			MLRole mlRole = createMLRole();
			List<MLRole> rolesCountMap = new ArrayList<>();
			rolesCountMap.add(mlRole);
			Mockito.when(userRoleService.getRoleUsersCount()).thenReturn(rolesCountMap);
			data = userRoleController.getRoleUsersCount();			
			//MLRole role = new MLRole();
			//role.setRoleIdUserCount(rolesCountMap);
			Assert.assertNotNull(data);
			Assert.assertEquals("Role count fetched Successfully", data.getResponseDetail());
			
		} catch (Exception e) {
			logger.info("Error Occurred while getRoleUsersCountTest()", e);
		}
	}
	private MLRole createMLRole(){
		MLRole mlRole = new MLRole();
		mlRole.setActive(true);
		mlRole.setName("Admin");
		Date created = new Date();
		mlRole.setCreated(created);
		mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
		
		return mlRole;
	}
	private User createUser(){
		User user = new User();
		user.setActive("Y");
		user.setFirstName("test-first-name");
		user.setJwttoken(
				"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOm51bGwsImNyZWF0ZWQiOjE1MTAxMzgyMzY4NjcsImV4cCI6MTUxMDc0MzAzNiwibWxwdXNlciI6eyJjcmVhdGVkIjoxNTA4MjM0Njk2MDAwLCJtb2RpZmllZCI6MTUwOTk2MDg5NTAwMCwidXNlcklkIjoiNDEwNTgxMDUtNjdmNC00NDYxLWExOTItZjRjYjdmZGFmZDM0IiwiZmlyc3ROYW1lIjoiTWFuaW1vemhpVDEiLCJtaWRkbGVOYW1lIjpudWxsLCJsYXN0TmFtZSI6IlQyIiwib3JnTmFtZSI6bnVsbCwiZW1haWwiOiJNYW5pbW96aGlUMUBnbWFpLmNvbSIsImxvZ2luTmFtZSI6Ik1hbmltb3poaVQxIiwibG9naW5IYXNoIjpudWxsLCJsb2dpblBhc3NFeHBpcmUiOm51bGwsImF1dGhUb2tlbiI6bnVsbCwiYWN0aXZlIjp0cnVlLCJsYXN0TG9naW4iOm51bGwsInBpY3R1cmUiOm51bGx9fQ.bLzIAFVUIPMVE_WD0-BvMupFyHyy90mw_je1PmnvP34swv1ZUW_SL7DBoKeSGnIf_zhtDp8V8d3Q3pAiWMjLyA");
		user.setEmailId("testemail@test.com");
		user.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
		user.setLoginName("test-User-Name");
		user.setRole("Admin");
		user.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
		return user;
	}
}
