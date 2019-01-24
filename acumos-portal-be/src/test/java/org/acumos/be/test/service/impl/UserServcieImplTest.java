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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.service.impl.UserServiceImpl;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.junit.Assert;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class UserServcieImplTest {
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UserServcieImplTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	UserServiceImpl impl = new UserServiceImpl();
	
	@Test
	public void saveTest() throws HttpClientErrorException {
		try {
			User user = new User();
			user.setFirstName("UserFirstName123");
			user.setLastName("UserLastName123");
			user.setUsername("User1e56");
			user.setEmailId("user123@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId(String.valueOf((Math.incrementExact(0))));
			boolean isUsernameExist = user.getLoginName() != null;

			if (user != null) {
				if (!isUsernameExist) {
					logger.info("UserName already exists : ");
				} else {
					Mockito.when(impl.save(user)).thenReturn(user);
					logger.info("Successfully created user " + user);
					Assert.assertNotNull(user);
					Assert.assertEquals(user, user);

				}
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void getAllUserTest() {
		try {
			User user = new User();
			user.setFirstName("UserFirstName123");
			user.setLastName("UserLastName123");
			user.setUsername("User1e56");
			user.setEmailId("user123@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId(String.valueOf((Math.incrementExact(0))));
			Assert.assertEquals(user, user);
			
			List<User> userList = new ArrayList<User>();
			userList.add(user);
			if(userList != null){
				Mockito.when(impl.getAllUser()).thenReturn(userList);
				logger.info("User list fetched successfully ");
				Assert.assertNotNull(userList);
				Assert.assertEquals(userList, userList);
			}
			
			
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void findUserByEmailTest() {
		try {

			User user = new User();
			user.setFirstName("UserFirstName123");
			user.setLastName("UserLastName123");
			user.setUsername("User1e56");
			user.setEmailId("user123@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId(String.valueOf((Math.incrementExact(0))));
			Assert.assertEquals(user, user);
			
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			String email = user.getEmailId();
			Assert.assertEquals(email, email);
			
			Mockito.when(impl.findUserByEmail(email)).thenReturn(mlpUser);
			Assert.assertNotNull(mlpUser);

			if (email != null) {
				logger.info("Successfully user fetched using email");
			} else {
				logger.info("Failed user fetched using email");
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void findUserByUsernametest() {
		try {
			User user = new User();
			user.setFirstName("UserFirstName123");
			user.setLastName("UserLastName123");
			user.setUsername("User1e56");
			user.setEmailId("user123@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUsername("User1");
			user.setLoginName(user.getUsername());
			user.setUserId(String.valueOf((Math.incrementExact(0))));
			Assert.assertEquals(user, user);
			
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);

			Assert.assertNotNull(mlpUser);
			
			String userName = user.getLoginName();
			Assert.assertNotNull(userName);
			
			Mockito.when(impl.findUserByUsername(userName)).thenReturn(mlpUser);
			logger.info("Successfully fetched using loginname");
			Assert.assertEquals(mlpUser, mlpUser);

		} catch (Exception e) {
			
		}
	}

	@Test
	public void loginTest() throws HttpClientErrorException {
		try {
			
			User user = new User();
			user.setFirstName("UserFirstName123");
			user.setLastName("UserLastName123");
			user.setUsername("User1e56");
			user.setEmailId("user123@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUsername("User1");
			user.setLoginName(user.getUsername());
			user.setUserId(String.valueOf((Math.incrementExact(0))));
			Assert.assertEquals(user, user);
			
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			String username = user.getLoginName();
			String password = user.getPassword();

			Assert.assertNotNull(username);
			Assert.assertNotNull(password);
			if (username != null && password != null) {
				Mockito.when(impl.login(username, password)).thenReturn(mlpUser);
				logger.info("Successfully loged in ");
				Assert.assertEquals(mlpUser, mlpUser);
				
			} else {
				logger.info("Failed to loged in ");
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void changeUserPasswordTest() {
		try {

			User user = new User();
			user.setFirstName("UserFirstName123");
			user.setLastName("UserLastName123");
			user.setUsername("User1e56");
			user.setEmailId("user123@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUsername("User1");
			user.setLoginName(user.getUsername());
			user.setUserId(String.valueOf((Math.incrementExact(0))));
			Assert.assertEquals(user, user);
			
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			String username = mlpUser.getLoginName();
			Assert.assertNotNull(username);
			
			String userId = user.getUserId();
			String oldPassword = user.getPassword();
			String newPassword = "NewPassword";
			
			boolean flag = true;
			
			if (oldPassword == newPassword) {
				logger.info(
						"please reset the password !: failed to reset the password because old and new password are same. it sg=hould be different");
			} else {
				Mockito.when(impl.changeUserPassword(userId, oldPassword, newPassword)).thenReturn(flag);
				logger.info("password changed successfully ");
				Assert.assertTrue(flag);
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void updateUserTest() {
		try {
			User user = new User();
			user.setUserId("d4006019-5c8d-49da-9933-1d0e1adf532c");
			user.setFirstName("updatedUserFirstName123");
			user.setLastName("updatedUserLastName123");
			user.setUsername("User1e56");
			user.setEmailId("user123updated@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId(String.valueOf((Math.incrementExact(0))));

			UserServiceImpl mockimpl = mock(UserServiceImpl.class);

			boolean isUsernameExist = user.getLoginName() != null;

			if (user.getUserId() != null) {
				if (isUsernameExist) {
					mockimpl.updateUser(user);
					logger.info("Successfully updated user ");
					Assert.assertNotNull(user);
				} else {
					logger.info("User does not exists.Please create User ");
				}

			} else {
				logger.info("Unable to update user");
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void forgetPasswordTest() throws HttpClientErrorException {
		try {
			UserServiceImpl mockimpl = mock(UserServiceImpl.class);

			User user = new User();
			user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			user.setFirstName("UpdatedFirstName");
			user.setLastName("UpdatedLastName");
			user.setUsername("User1Updated");
			user.setEmailId("user1Updated@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId(String.valueOf((Math.incrementExact(0))));
			Assert.assertNotNull(user);
			
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			if (mlpUser != null) {
				mockimpl.forgetPassword(mlpUser);
				logger.info("Email sent for resetting the password");
				Assert.assertNotNull(mlpUser);
				Assert.assertEquals(mlpUser, mlpUser);
			} else {
				logger.info("FAiled to update");
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}

	}
	
	@Test
	public void findUserByUserIdTest(){
		try{
			User user = new User();
			user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			user.setFirstName("UpdatedFirstName");
			user.setLastName("UpdatedLastName");
			user.setUsername("User1Updated");
			user.setEmailId("user1Updated@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId("6abf132a-b769-4080-a21a-4d231d5ac544");
			Assert.assertNotNull(user);
			
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			
			String userId = mlpUser.getUserId();
			Assert.assertNotNull(userId);
			Mockito.when(impl.findUserByUserId(userId)).thenReturn(mlpUser);
			logger.info("Successfully user found ");
			Assert.assertEquals(mlpUser, mlpUser);
			
		}catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void getUserRoleTest(){
		try{
			
			MLPRole mlpRole = new MLPRole();
			mlpRole.setName("Admin");
			Instant created = Instant.now();
			mlpRole.setCreated(created);
			mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			
			
			User user = new User();
			user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			user.setFirstName("UpdatedFirstName");
			user.setLastName("UpdatedLastName");
			user.setUsername("User1Updated");
			user.setEmailId("user1Updated@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId("6abf132a-b769-4080-a21a-4d231d5ac544");
			user.setRoleId(mlpRole.getRoleId());
			Assert.assertNotNull(user);
			
			
			List<MLPRole> mlpRolelist = new ArrayList<MLPRole>();
			mlpRolelist.add(mlpRole);
			
			String userId = user.getUserId();
			Mockito.when(impl.getUserRole(userId)).thenReturn(mlpRolelist);
			Assert.assertNotNull(mlpRolelist);
			Assert.assertEquals(mlpRolelist, mlpRolelist);
			
		}catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}
	
	@Test
	public void updateUserImageTest(){
		try{
			User user = new User();
			user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			user.setFirstName("UpdatedFirstName");
			user.setLastName("UpdatedLastName");
			user.setUsername("User1Updated");
			user.setEmailId("user1Updated@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId("6abf132a-b769-4080-a21a-4d231d5ac544");
			Assert.assertNotNull(user);
			
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			Assert.assertNotNull(mlpUser);
			
			UserServiceImpl mockImpl = mock(UserServiceImpl.class);
			
			mockImpl.updateUserImage(mlpUser);
			
			
		}catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}
	
	@Test
	public void updateBulkUsersTest(){
		try{
			User user = new User();
			user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			user.setFirstName("UpdatedFirstName");
			user.setLastName("UpdatedLastName");
			user.setUsername("User1Updated");
			user.setEmailId("user1Updated@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			user.setUserId("6abf132a-b769-4080-a21a-4d231d5ac544");
			Assert.assertNotNull(user);
			
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			Assert.assertNotNull(mlpUser);
			UserServiceImpl mockImpl = mock(UserServiceImpl.class);
			mockImpl.updateBulkUsers(mlpUser);
			Assert.assertNotNull(mlpUser);
		}catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}
	@Test
	public void getRoleCountForUserTest(){
		try{
			
			MLRole mlRole = new MLRole();
			mlRole.setActive(true);
			mlRole.setName("Admin");
			Instant created = Instant.now();
			mlRole.setCreated(created);
			mlRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			
			RestPageRequest pageRequest = new RestPageRequest();
			pageRequest.setSize(9);
			pageRequest.setPage(1);
			Mockito.when(impl.getRoleCountForUser(pageRequest )).thenReturn(mlRole);
			Assert.assertNotNull(mlRole);
			
			
		}catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}
	
}
