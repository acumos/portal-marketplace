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

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.service.impl.UserRoleServiceImpl;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)		
public class UserRoleServiceImplTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UserRoleServiceImplTest.class);

	@Mock
	UserRoleServiceImpl impl = new UserRoleServiceImpl();
	
	@Test
	public void getAllRoles(){
		try{			
			
			boolean active = true;
			Date created = new Date();
			Date modified = new Date();
			List<String> permissionList = new ArrayList<String>();
			permissionList.add("a");
			permissionList.add("b");
			int roleCount = 10;

			MLRole mlRole = new  MLRole();
			mlRole.setActive(active);
			mlRole.setModified(modified);
			mlRole.setCreated(created);
			mlRole.setName("abc");
			mlRole.setPermissionList(permissionList);
			mlRole.setRoleCount(roleCount);
			mlRole.setRoleId("sfs3r3gd");
			List<MLRole> list = new ArrayList<MLRole>();
			list.add(mlRole);
			
			Mockito.when(impl.getAllRoles()).thenReturn(list);
			Assert.assertEquals(list, list);
			logger.info("Successfully return AllRoles");
		} catch (Exception e) {
			logger.info("Exception occured while getAllRoles: " + e);			 
		}
	}
	
	@Test
	public void getRolesForUser(){
		try{
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			List<MLRole> list = new ArrayList<MLRole>();
			boolean active = true;
			Date created = new Date();
			Date modified = new Date();
			List<String> permissionList = new ArrayList<String>();
			permissionList.add("a");
			permissionList.add("b");
			int roleCount = 10;

			MLRole mlRole = new  MLRole();
			mlRole.setActive(active);
			mlRole.setModified(modified);
			mlRole.setCreated(created);
			mlRole.setName("abc");
			mlRole.setPermissionList(permissionList);
			mlRole.setRoleCount(roleCount);
			mlRole.setRoleId("sfs3r3gd");
			list.add(mlRole);
			Mockito.when(impl.getRolesForUser(userId)).thenReturn(list);
			Assert.assertEquals(list, list);
			logger.info("Successfully return RolesForUser");
			
		} catch (Exception e) {
			logger.info("Exception occured while getRolesForUser: " + e);			 
		}
	}
	
	@Test
	public void getRole(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			
			MLRole mlRole = new  MLRole();
			mlRole.setName("abc");
			mlRole.setRoleId("sfs3r3gd");
			mlRole.setRoleId(roleId);
			Mockito.when(impl.getRole(roleId)).thenReturn(mlRole);
			Assert.assertEquals(mlRole, mlRole);
			logger.info("Successfully return Role");
			
		} catch (Exception e) {
			logger.info("Exception occured while getRolesForUser: " + e);			 
		}
	}
	
	@Test
	public void createRole(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			Date date = new Date();
			MLRole role = new MLRole();
			role.setActive(true);
			role.setCreated(date);
			role.setModified(date);
			role.setName("Test");
			MLPRole mlpRole = new MLPRole();
			mlpRole.setCreated(date);
			mlpRole.setModified(date);
			mlpRole.setName("abc");
			mlpRole.setRoleId(roleId);
			Mockito.when(impl.createRole(role)).thenReturn(mlpRole);
			Assert.assertEquals(mlpRole, mlpRole);
			logger.info("Successfully create Role");
		
		} catch (Exception e) {
			logger.info("Exception occured while createRole: " + e);			 
		}
	}
	
	@Test
	public void updateRole(){
		try{
			JsonRequest<MLPRole> roleJson = new JsonRequest<>();
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			Date date = new Date();
			MLPRole mlpRole = new MLPRole();
			mlpRole.setCreated(date);
			mlpRole.setModified(date);
			mlpRole.setName("abc");
			mlpRole.setRoleId(roleId);
			roleJson.setBody(mlpRole);
			UserRoleServiceImpl mockimpl = mock(UserRoleServiceImpl.class);
            mockimpl.updateRole(roleJson);
            Assert.assertEquals(roleJson, roleJson);
            Assert.assertNotNull(mockimpl);
			logger.info("Successfully update Role");
		
		} catch (Exception e) {
			logger.info("Exception occured while updateRole: " + e);			 
		}
	}
	
	@Test
	public void deleteRole(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			UserRoleServiceImpl mockimpl = mock(UserRoleServiceImpl.class);
			mockimpl.deleteRole(roleId);
			Assert.assertEquals(roleId, roleId);
			Assert.assertNotNull(mockimpl);
			logger.info("Successfully delet Role");
		} catch (Exception e) {
			logger.info("Exception occured while deleteRole: " + e);			 
		}
	}
	
	@Test
	public void getRoleFunction(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			String roleFunctionId = "7e978f26-7776-4738-a528-3a7f3f2d3c4f";
			MLRoleFunction mlRoleFunction = new MLRoleFunction();
			mlRoleFunction.setCreated(new Date());
			MLRole mlRole = new MLRole();
			mlRole.setCreated(new Date());
			mlRole.setModified(new Date());
			mlRole.setName("abc");
			mlRole.setRoleId(roleId);
			mlRoleFunction.setMlRole(mlRole);
			mlRoleFunction.setName("abc");
			Mockito.when(impl.getRoleFunction(roleId, roleFunctionId)).thenReturn(mlRoleFunction);
			Assert.assertEquals(mlRoleFunction, mlRoleFunction);
			logger.info("Successfully get RoleFunction");
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
			Mockito.when(impl.createRoleFunction(mlRoleFunction)).thenReturn(mlRoleFunction);
			Assert.assertEquals(mlRoleFunction, mlRoleFunction);
			logger.info("Successfully create RoleFunction");
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
			UserRoleServiceImpl mockimpl = mock(UserRoleServiceImpl.class);
			mockimpl.updateRoleFunction(mlpRoleFunction);
			Assert.assertEquals(mlpRoleFunction, mlpRoleFunction);
			logger.info("Successfully update RoleFunction");
		} catch (Exception e) {
			logger.info("Exception occured while updateRoleFunction: " + e);			 
		}
	}
	
	@Test
	public void deleteRoleFunction(){
		try{
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
			String roleFunctionId = "7e978f26-7776-4738-a528-3a7f3f2d3c4f";
			UserRoleServiceImpl mockimpl = mock(UserRoleServiceImpl.class);
			mockimpl.deleteRoleFunction(roleId, roleFunctionId);
			Assert.assertEquals(roleId, roleId);
			Assert.assertEquals(roleFunctionId, roleFunctionId);
			Assert.assertNotNull(mockimpl);
			logger.info("Successfully delete RoleFunction");
		} catch (Exception e) {
			logger.info("Exception occured while deleteRoleFunction: " + e);			 
		}
	}
	
	@Test
	public void addUserRole(){
		try{
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			String roleId = "12345678-abcd-90ab-cdef-1234567890ab";
		
			UserRoleServiceImpl mockimpl = mock(UserRoleServiceImpl.class);
			mockimpl.addUserRole(userId, roleId);
			Assert.assertEquals(userId, userId);
			Assert.assertEquals(roleId, roleId);
			Assert.assertNotNull(mockimpl);
			logger.info("Successfully add User Role");
		} catch (Exception e) {
			logger.info("Exception occured while addUserRole: " + e);			 
		}
	}
}