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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.service.impl.UserRoleServiceImpl;
import org.acumos.portal.be.transport.MLRole;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * 
 * @author VT00325492
 *
 */
		
public class UserRoleServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(PublishSolutionServiceImplTest.class);

	@Mock
	Environment env;

	@Mock
	AdminServiceImplTest test;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	private final String url = "http://localhost:8002/ccds/";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	private ICommonDataServiceRestClient cmnDataService;
		
	@Test
	public void getAllRoles(){
		try{
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.getAllRoles();
			
		} catch (Exception e) {
			logger.info("Exception occured while getAllRoles: " + e);			 
		}
	}
	
	@Test
	public void getRolesForUser(){
		try{
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.getRolesForUser(userId);
			
		} catch (Exception e) {
			logger.info("Exception occured while getRolesForUser: " + e);			 
		}
	}
	
	@Test
	public void getRole(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.getRole(roleId);
			
		} catch (Exception e) {
			logger.info("Exception occured while getRolesForUser: " + e);			 
		}
	}
	
	@Test
	public void createRole(){
		try{
			Date date = new Date();
			MLRole role = new MLRole();
			role.setActive(true);
			role.setCreated(date);
			role.setModified(date);
			role.setName("Test");
			/*List<String> permissionList = new ArrayList<String>();
			permissionList.add("Admin");
			role.setPermissionList(permissionList);*/
			
			/*Map<String, Map<String, String>> roleIdUserCount = new HashMap<>();		
			Map<String, String> value = new HashMap<>();
			value.put("", "");
			String key = "";
			roleIdUserCount.put(key, value);
			role.setRoleIdUserCount(roleIdUserCount);*/
			
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.createRole(role);
		
		} catch (Exception e) {
			logger.info("Exception occured while createRole: " + e);			 
		}
	}
	
	@Test
	public void updateRole(){
		try{
			JsonRequest<MLPRole> roleJson = new JsonRequest<>();
			Date date = new Date();
			MLRole role = new MLRole();
			role.setActive(true);
			role.setCreated(date);
			role.setModified(date);
			role.setName("Test");
			/*List<String> permissionList = new ArrayList<String>();
			permissionList.add("Admin");
			role.setPermissionList(permissionList);
			Map<String, Map<String, String>> roleIdUserCount = new HashMap<>();		
			role.setRoleIdUserCount(roleIdUserCount);*/
					
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.updateRole(roleJson);
		
		} catch (Exception e) {
			logger.info("Exception occured while updateRole: " + e);			 
		}
	}
	
	@Test
	public void deleteRole(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.deleteRole(roleId);
		
		} catch (Exception e) {
			logger.info("Exception occured while deleteRole: " + e);			 
		}
	}
	
	@Test
	public void getRoleFunction(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			String roleFunctionId = "7e978f26-7776-4738-a528-3a7f3f2d3c4f";
			
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.getRoleFunction(roleId, roleFunctionId);
		} catch (Exception e) {
			logger.info("Exception occured while getRoleFunction: " + e);			 
		}
	}
	
	@Test
	public void createRoleFunction(){
		try{
			Date date = new Date();
			MLPRoleFunction mlRoleFunction = new MLPRoleFunction();
			mlRoleFunction.setCreated(date);
			mlRoleFunction.setModified(date);
			mlRoleFunction.setName("");
			mlRoleFunction.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.createRoleFunction(mlRoleFunction);
		} catch (Exception e) {
			logger.info("Exception occured while createRoleFunction: " + e);			 
		}
	}
	
	@Test
	public void updateRoleFunction(){
		try{
			Date date = new Date();
			JsonRequest<MLPRoleFunction> mlpRoleFunction = new JsonRequest<>();
			MLPRoleFunction body = new MLPRoleFunction();
			body.setCreated(date);
			body.setModified(date);
			body.setName("Test Function");
			body.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			body.setRoleFunctionId("7e978f26-7776-4738-a528-3a7f3f2d3c4f");		
			mlpRoleFunction.setBody(body);		
			
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.updateRoleFunction(mlpRoleFunction);
		} catch (Exception e) {
			logger.info("Exception occured while updateRoleFunction: " + e);			 
		}
	}
	
	@Test
	public void deleteRoleFunction(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			String roleFunctionId = "7e978f26-7776-4738-a528-3a7f3f2d3c4f";
			
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.deleteRoleFunction(roleId, roleFunctionId);
		} catch (Exception e) {
			logger.info("Exception occured while deleteRoleFunction: " + e);			 
		}
	}
	
	@Test
	public void addUserRole(){
		try{
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.addUserRole(userId, roleId);
		} catch (Exception e) {
			logger.info("Exception occured while addUserRole: " + e);			 
		}
	}
	
	/*@Test
	public void updateUserRole(){ 
		try{
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			String updatedRoleId = "12345678-abcd-90ab-cdef-1234567890ab";
			 
			
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserRoleServiceImpl impl = new UserRoleServiceImpl();
			impl.setEnvironment(env);
			impl.updateUserRole(userId, roleId, updatedRoleId);
		} catch (Exception e) {
			logger.info("Exception occured while updateUserRole: " + e);			 
		}
	}*/
	
	@Test
	public void updateUserRoleMulti(){
		try{
			List<String> userIdList = new ArrayList<>();
			userIdList.add("1810f833-8698-4233-add4-091e34b8703c");
			List<String> roleIdList = new ArrayList<>();
			roleIdList.add("12345678-abcd-90ab-cdef-1234567890ab");
			List<String> updatedRoleIdList = new ArrayList<>();
			updatedRoleIdList.add("12345678-abcd-90ab-cdef-1234567890ab");
		
		} catch (Exception e) {
			logger.info("Exception occured while updateUserRoleMulti: " + e);			 
		}
	}
}
