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
package org.acumos.be.test.transport;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.junit.Assert;
import org.junit.Test;

public class MLRoleFunctionTest {

	@Test	
	public void testRoleFunctionParameter(){
		
		String roleFunctionId = "443f3";
		String roleId = "5435sf";
		String name1 ="abc";
		boolean active = true;
		Instant created1 = Instant.now();
		Instant modified1 = Instant.now();
		List<String> permissionList = new ArrayList<String>();
		permissionList.add("a");
		permissionList.add("b");
		int roleCount = 10;
		MLRole mlRole = new  MLRole();
		mlRole.setActive(active);
		mlRole.setModified(modified1);
		mlRole.setCreated(created1);
		mlRole.setName(name1);
		mlRole.setPermissionList(permissionList);
		mlRole.setRoleCount(roleCount);
		mlRole.setRoleId(roleId);
		String name = "abc";
		Instant created = Instant.now();
		Instant modified = Instant.now();
		
		MLRoleFunction mlRoleFunction = new MLRoleFunction();
		mlRoleFunction.setCreated(created);
		mlRoleFunction.setMlRole(mlRole);
		mlRoleFunction.setModified(modified);
		mlRoleFunction.setName(name);
		mlRoleFunction.setRoleFunctionId(roleFunctionId);
	
		Assert.assertEquals(mlRole, mlRoleFunction.getMlRole());
		Assert.assertEquals(modified, mlRoleFunction.getModified());
		Assert.assertEquals(created, mlRoleFunction.getCreated());
		Assert.assertEquals(name, mlRoleFunction.getName());
		Assert.assertEquals(roleFunctionId, mlRoleFunction.getRoleFunctionId());

	}
	
}
