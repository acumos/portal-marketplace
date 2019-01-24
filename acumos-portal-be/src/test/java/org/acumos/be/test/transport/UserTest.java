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

import org.acumos.cds.domain.MLPRole;
import org.acumos.portal.be.transport.User;
import org.junit.Assert;
import org.junit.Test;

public class UserTest {

	@Test	
	public void testOauthUserParameter(){
		
		String firstName = "abc";
		String lastName = "xyz";
		String emailId = "zbc@techm.com";
		String username = "zbc";
		String password = "f24sfs";
		String active = "Y";
		Instant lastLogin = Instant.now();
		Instant created = Instant.now();
		Instant modified = Instant.now();
		String userId = "83d5259f-48b7-4fe1-9fd6-d1166f8f3691";
		String loginName = "afaf";
		String orgName = "gdggege";
		byte[] picture = new byte[1];
		String jwttoken = "fsfss535gf44";
		String role = "admin";
		String roleId = "sf3trgf";
		String updatedRole = "feft4";
		String updatedRoleId = "g3t3teg";
		List<String> userIdList = new ArrayList<String>();
		userIdList.add(loginName);
		List<String> userNewRoleList = new ArrayList<String>();
		userNewRoleList.add(roleId);
		List<MLPRole> userAssignedRolesList=new ArrayList<>();

		
		User user= new User();
		user.setActive(active);
		user.setCreated(created);
		user.setEmailId(emailId);
		user.setFirstName(firstName);
		user.setJwttoken(jwttoken);
		user.setLastLogin(lastLogin);
		user.setLastName(lastName);
		user.setLoginName(loginName);
		user.setModified(modified);
		user.setOrgName(orgName);
		user.setPassword(password);
		user.setPicture(picture);
		user.setRole(role);
		user.setRoleId(roleId);
		user.setUpdatedRoleId(updatedRoleId);
		user.setUpdatedRole(updatedRole);
		user.setUserId(userId);
		user.setUserIdList(userIdList);
		user.setUsername(username);
		user.setUserNewRoleList(userNewRoleList);
		user.setUserAssignedRolesList(userAssignedRolesList);
		
		Assert.assertEquals(active, user.getActive());
		Assert.assertEquals(created, user.getCreated());
		Assert.assertEquals(emailId, user.getEmailId());
		Assert.assertEquals(firstName, user.getFirstName());
		Assert.assertEquals(jwttoken, user.getJwttoken());
		Assert.assertEquals(lastName, user.getLastName());
		Assert.assertEquals(loginName, user.getLoginName());
		Assert.assertEquals(modified, user.getModified());
		Assert.assertEquals(orgName, user.getOrgName());
		Assert.assertEquals(password, user.getPassword());
		Assert.assertEquals(picture, user.getPicture());
		Assert.assertEquals(role, user.getRole());
		Assert.assertEquals(roleId, user.getRoleId());
		Assert.assertEquals(updatedRoleId, user.getUpdatedRoleId());
		Assert.assertEquals(updatedRole, user.getUpdatedRole());
		Assert.assertEquals(userId, user.getUserId());
		Assert.assertEquals(userIdList, user.getUserIdList());
		Assert.assertEquals(username, user.getUsername());
		Assert.assertEquals(userNewRoleList, user.getUserNewRoleList());
        Assert.assertNotNull(user.getUserAssignedRolesList());
	}
}
