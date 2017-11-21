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
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.UserRoleController;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.transport.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;



@RunWith(MockitoJUnitRunner.class)
public class UserRoleControllerTest {
	private static Logger logger = LoggerFactory.getLogger(UserRoleControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Mock
	UserRoleController userRoleController = new UserRoleController();
	
	
	/*
	 TODO Update User role, list of user roles
	 */

	@Test
	public void getRolesListTest() {
		try {
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");

			List<MLRole> mlRoleList = new ArrayList<MLRole>();
			mlRoleList.add(mlRole);

			JsonResponse<List<MLRole>> value = new JsonResponse<>();
			value.setResponseBody(mlRoleList);
			Mockito.when(userRoleController.getRolesList()).thenReturn(value);
			logger.info("Get Role List fetched successfully : ", mlRoleList);

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching getRolesListTest ", e);
		}

	}

	@Test
	public void getRoleDetailsTest() {
		try {
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Date created = new Date();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");

			String roleId = mlRole.getRoleId();

			JsonResponse<MLRole> value = new JsonResponse<>();
			value.setResponseBody(mlRole);

			Mockito.when(userRoleController.getRoleDetails(request, roleId, response)).thenReturn(value);
			logger.info("Get Role List fetched successfully : ", mlRole);

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching getRoleDetailsTest ", e);
		}
	}

	@Test
	public void postRoleTest() {
		try {
			MLPRole mlpRole = new MLPRole();
			mlpRole.setName("Admin");
			Date created = new Date();
			mlpRole.setCreated(created);
			mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");

			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");

			String roleId = mlpRole.getRoleId();

			JsonResponse<MLPRole> value = new JsonResponse<>();
			value.setResponseBody(mlpRole);

			JsonRequest<MLRole> role = new JsonRequest<>();
			role.setBody(mlRole);

			Mockito.when(userRoleController.postRole(request, role, response)).thenReturn(value);
			logger.info("Successfully created Role: ", mlRole);

		} catch (Exception e) {
			e.printStackTrace();
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

			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			String roleId = mlpRole.getRoleId();

			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlpRole);

			JsonRequest<MLPRole> role = new JsonRequest<>();
			role.setBody(mlpRole);

			Mockito.when(userRoleController.updateRole(role)).thenReturn(value);
			logger.info("Successfully updated Role: ", role);

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching updateRoleTest ", e);
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

			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			String roleId = mlpRole.getRoleId();

			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlpRole);

			JsonRequest<MLRole> role = new JsonRequest<>();
			role.setBody(mlRole);

			Mockito.when(userRoleController.deleteRole(role)).thenReturn(value);
			logger.info("Successfully deleted Role: ", role);

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching deleteRoleTest ", e);
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

			MLRoleFunction mlRoleFunction = new MLRoleFunction();
			mlRoleFunction.setCreated(created);
			mlRoleFunction.setMlRole(mlRole);
			mlRoleFunction.setName("Role NAme");
			mlRoleFunction.setRoleFunctionId("123");

			MLRoleFunction updateMlRoleFunction = new MLRoleFunction();
			updateMlRoleFunction.setCreated(created);
			updateMlRoleFunction.setMlRole(mlRole);
			updateMlRoleFunction.setName("updated Role NAme");
			updateMlRoleFunction.setRoleFunctionId("123");

			JsonRequest<MLRoleFunction> roleFunction = new JsonRequest<>();
			roleFunction.setBody(mlRoleFunction);
			JsonResponse<MLRoleFunction> value = new JsonResponse<>();
			value.setResponseBody(mlRoleFunction);

			Mockito.when(userRoleController.getRoleFunction(roleFunction)).thenReturn(value);
			logger.info("Successfully fetched all the role function details ");

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching getRoleFunctionTest ", e);
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

			MLPRoleFunction mlpRoleFunction = new MLPRoleFunction();
			mlpRoleFunction.setCreated(created);
			mlpRoleFunction.setName("Role NAme");
			mlpRoleFunction.setRoleFunctionId("123");

			MLRoleFunction updateMlRoleFunction = new MLRoleFunction();
			updateMlRoleFunction.setCreated(created);
			updateMlRoleFunction.setMlRole(mlRole);
			updateMlRoleFunction.setName("updated Role NAme");
			updateMlRoleFunction.setRoleFunctionId("123");

			JsonResponse<MLPRoleFunction> value = new JsonResponse<>();
			value.setResponseBody(mlpRoleFunction);

			JsonRequest<MLPRoleFunction> mlpRoleFunctionReq = new JsonRequest<>();
			mlpRoleFunctionReq.setBody(mlpRoleFunction);

			Mockito.when(userRoleController.createRoleFunction(mlpRoleFunctionReq)).thenReturn(value);
			logger.info("Successfully created the role function details ");

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching createRoleFunctionTest ", e);
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

			MLPRoleFunction mlpRoleFunction = new MLPRoleFunction();
			mlpRoleFunction.setCreated(created);
			mlpRoleFunction.setName("Role NAme");
			mlpRoleFunction.setRoleFunctionId("123");

			MLPRoleFunction updateMlRoleFunction = new MLPRoleFunction();
			updateMlRoleFunction.setCreated(created);
			updateMlRoleFunction.setName("updated Role NAme");
			updateMlRoleFunction.setRoleFunctionId(mlpRoleFunction.getRoleFunctionId());

			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(updateMlRoleFunction);

			JsonRequest<MLPRoleFunction> mlpRoleFunctionReq = new JsonRequest<>();
			mlpRoleFunctionReq.setBody(updateMlRoleFunction);

			Mockito.when(userRoleController.updateRoleFunction(mlpRoleFunctionReq)).thenReturn(value);
			logger.info("Successfully updated the role function details ");

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching updateRoleFunctionTest ", e);
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

			MLRoleFunction mlRoleFunction = new MLRoleFunction();
			mlRoleFunction.setCreated(created);
			mlRoleFunction.setName("Role NAme");
			mlRoleFunction.setRoleFunctionId("123");

			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlRoleFunction);

			JsonRequest<MLRoleFunction> mlRoleFunctionReq = new JsonRequest<>();
			mlRoleFunctionReq.setBody(mlRoleFunction);

			Mockito.when(userRoleController.deleteRoleFunction(mlRoleFunctionReq)).thenReturn(value);
			logger.info("Successfully deleted the role function details ");

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching deleteRoleFunction ", e);
		}

	}

	@Test
	public void addUserRoleTest() {
		try {
			MLPRole mlpRole = new MLPRole();
			mlpRole.setName("Admin");
			Date created = new Date();
			mlpRole.setCreated(created);
			mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");

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

			String roleId = mlpRole.getRoleId();

			JsonRequest<User> userreq = new JsonRequest<>();
			userreq.setBody(user);

			JsonResponse<MLPRole> value = new JsonResponse<>();
			value.setResponseBody(mlpRole);
			Mockito.when(userRoleController.addUserRole(request, userreq, response)).thenReturn(value);
			logger.info("Successfully added user role");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Eception while fetching addUserRoleTest ", e);
		}
	}



}
