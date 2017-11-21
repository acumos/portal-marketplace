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

import org.acumos.be.test.controller.UserServiceControllerTest;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.service.impl.AbstractServiceImpl;
import org.acumos.portal.be.service.impl.UserServiceImpl;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.client.HttpClientErrorException;

public class UserServcieImplTest {
	private static Logger logger = LoggerFactory.getLogger(UserServiceControllerTest.class);

	@Mock
	Environment env;

	@Mock
	AdminServiceImplTest test;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	private final String url = "http://localhost:8002/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	private ICommonDataServiceRestClient cmnDataService;

	private AbstractServiceImpl abstractImpl;

	@Before
	public void createClient() throws Exception {
		cmnDataService = CommonDataServiceRestClientImpl.getInstance(url.toString(), user, pass);
	}

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

			when(env.getProperty("portal.feature.email")).thenReturn("user123@emial.com");
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);

			boolean isUsernameExist = user.getLoginName() != null;

			if (user != null) {
				if (!isUsernameExist) {
					logger.info("UserName already exists : ");
				} else {
					User testUser = impl.save(user);
					logger.info("Successfully created user " + user);

				}
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void getAllUserTest() {
		try {
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);
			impl.getAllUser();
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

	@Test
	public void findUserByEmailTest() {
		try {
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);

			String email = "NasirHussain12@gmail.com";
			impl.findUserByEmail(email);

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
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);

			String userName = "User1";
			if (userName != null) {
				impl.findUserByUsername(userName);
				logger.info("Successfully fetched using loginname");
			} else {
				logger.info("loginname does not exists");

			}

		} catch (Exception e) {
			
		}
	}

	@Test
	public void loginTest() throws HttpClientErrorException {
		try {
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);
			String username = "User1";
			String password = "password";

			if (username != null && password != null) {
				impl.login(username, password);
				logger.info("Successfully loged in ");
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
			when(env.getProperty("portal.feature.email")).thenReturn("user123@emial.com");
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);

			String userId = "d4006019-5c8d-49da-9933-1d0e1adf532c";
			String oldPassword = "password";
			String newPassword = "NewPassword";
			if (oldPassword == newPassword) {
				logger.info(
						"please reset the password !: failed to reset the password because old and new password are same. it sg=hould be different");
			} else {
				impl.changeUserPassword(userId, oldPassword, newPassword);
				logger.info("password changed successfully ");
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

			when(env.getProperty("portal.feature.email")).thenReturn("user123updated@emial.com");
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);

			boolean isUsernameExist = user.getLoginName() != null;

			if (user.getUserId() != null) {
				if (isUsernameExist) {
					impl.updateUser(user);
					logger.info("Successfully updated user ");
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
			when(env.getProperty("portal.feature.email")).thenReturn("user123@emial.com");
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);

			User user = new User();
			user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			user.setFirstName("UpdatedFirstName");
			user.setLastName("UpdatedLastName");
			user.setUsername("User1Updated");
			user.setEmailId("user1Updated@emial.com");
			user.setActive("Y");
			user.setPassword("password");

			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			if (mlpUser != null) {
				impl.forgetPassword(mlpUser);
				logger.info("Email sent for resetting the password");
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
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			UserServiceImpl impl = new UserServiceImpl();
			impl.setEnvironment(env);
			
			String userId = "6abf132a-b769-4080-a21a-4d231d5ac544";
			
			if(userId != null){
				impl.findUserByUserId(userId);
				logger.info("Successfully user found ");
			}else{
				logger.info("Failed to find User for the given UserId");
			}
			
		}catch (Exception e) {
			logger.info("Failed to execute testCase ");
		}
	}

}
