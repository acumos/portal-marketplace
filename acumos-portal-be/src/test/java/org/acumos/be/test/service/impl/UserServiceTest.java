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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.common.ConfigConstants;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.impl.UserServiceImpl;
import org.acumos.portal.be.transport.User;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.acumos.portal.be.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		ConfigConstants.cdms_client_url + "=http://localhost:8000/ccds",
		ConfigConstants.cdms_client_username + "=ccds_test", ConfigConstants.cdms_client_password + "=ccds_test",
		ConfigConstants.portal_feature_verifyAccount + "=true",
		ConfigConstants.portal_feature_verifyToken_Exp_Time + "=24" })
public class UserServiceTest {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

	private Date date = new Date();

	private final String userObj = "{" + "  \"created\": 1536464516651," + "  \"modified\": 1536464516651,"
			+ "  \"userId\": \"c50e75c6-85c1-4b0f-8617-cc1035a3d430\"," + "  \"firstName\": \"Test\","
			+ "  \"middleName\": null," + "  \"lastName\": \"User\"," + "  \"orgName\": null,"
			+ "  \"email\": \"test@gmail.com\"," + "  \"loginName\": \"test\"," + "  \"loginHash\": null,"
			+ "  \"loginPassExpire\": null," + "  \"authToken\": null," + "  \"active\": true,"
			+ "  \"lastLogin\": null," + "  \"loginFailCount\": null," + "  \"loginFailDate\": null,"
			+ "  \"picture\": null," + "  \"apiToken\": null," + "  \"verifyTokenHash\": null,"
			+ "  \"verifyExpiration\": " + date.getTime() + 150 + "," + "  \"tags\": []" + "}";

	private final String restUserResponse = "{\"content\":[" + userObj
			+ "],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"sort\":null,\"numberOfElements\":1,\"first\":true,\"size\":100,\"number\":0}";

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	@Autowired
	private UserServiceImpl userService;

	@Test
	public void createNewUserTest() {

		stubFor(post(urlEqualTo("/ccds/user")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(userObj)));

		User user = new User();
		user.setFirstName("Test");
		user.setLastName("User");
		user.setEmailId("test@gmail.com");
		user.setLoginName("test");
		user.setPassword("test@1234");

		User saveduser = userService.save(user);
		assertNotNull(saveduser);
		assertEquals(saveduser.getUserId(), "c50e75c6-85c1-4b0f-8617-cc1035a3d430");
	}

	@Test
	public void verifyTokenTest() {

		stubFor(post(urlEqualTo("/ccds/user/verify")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(userObj)));

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		boolean verified = false;
		try {
			verified = userService.verifyUser("test", "1234-5678-1234-1234567");
		} catch (AcumosServiceException e) {
			Assert.fail();
		}
		Assert.assertTrue(verified);
	}

	@Test
	public void verifyTokenFailTest() {

		stubFor(post(urlEqualTo("/ccds/user/verify")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		try {
			boolean verified = userService.verifyUser("test", "1234-5678-1234-1234567");
		} catch (AcumosServiceException e) {

			assertEquals(e.getMessage(), "Token Validation Failed");
		}
	}

	@Test
	public void regenerateVerifyTokenTest() {
		stubFor(get(urlEqualTo("/ccds/user/search?loginName=test&_j=a&page=0&size=10"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(restUserResponse)));

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		boolean verified = false;
		try {
			// Will return boolean true to indicate that token has been regenerated and sent
			// to the email address of user
			verified = userService.regenerateVerifyToken("test");
		} catch (AcumosServiceException e) {
			Assert.fail();
		}
		Assert.assertTrue(verified);
	}

	@Test
	public void regenerateVerifyTokenFailTest() {
		stubFor(get(urlEqualTo("/ccds/user/search?loginName=test&_j=a&page=0&size=10"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(restUserResponse)));

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430")).willReturn(
				aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK).withStatus(HttpStatus.SC_BAD_REQUEST)));

		try {
			// Will return boolean true to indicate that token has been regenerated and sent
			// to the email address of user
			userService.regenerateVerifyToken("test");
		} catch (AcumosServiceException e) {
			assertEquals(e.getMessage(), "Token Regeneration Failed");
		}
	}

	@Test
	public void verifyApiTokenTest() {
		stubFor(post(urlEqualTo("/ccds/user/loginapi")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(userObj)));

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		MLPUser verifiedUser = null;
		try {
			verifiedUser = userService.verifyApiToken("test", "1234-5678-1234-1234567");
		} catch (AcumosServiceException e) {
			Assert.fail();
		}
		Assert.assertNotNull(verifiedUser);
	}

	@Test
	public void verifyApiTokenFailTest() {

		stubFor(post(urlEqualTo("/ccds/user/loginapi")).willReturn(
				aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK).withStatus(HttpStatus.SC_BAD_REQUEST)));
		try {
			userService.verifyApiToken("test", "1234-5678-1234-1234567");
		} catch (AcumosServiceException e) {
			assertEquals(e.getMessage(), "Token Validation Failed");
		}
	}

	@Test
	public void refreshApiTokenTest() {

		stubFor(get(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(userObj)));

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		try {
			userService.refreshApiToken("c50e75c6-85c1-4b0f-8617-cc1035a3d430");
		} catch (AcumosServiceException e) {
			Assert.fail();
		}
	}

	@Test
	public void refreshApiTokenFailTest() {

		stubFor(get(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		try {
			userService.refreshApiToken("c50e75c6-85c1-4b0f-8617-cc1035a3d430");
		} catch (AcumosServiceException e) {
			assertEquals(e.getMessage(), "Api token Refresh Failed");
		}
	}

	@Test
	public void getAllUsersTest() {

		stubFor(get(urlEqualTo("/ccds/user?page=0&size=1000"))
				.willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody(restUserResponse).withStatus(HttpStatus.SC_OK)));

		stubFor(get(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430/role")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[" + "  {" + "    \"created\": 1536311738000," + "    \"modified\": 1536311738000,"
						+ "    \"roleId\": \"040e5576-1d99-48ee-820c-898297e2a34a\","
						+ "    \"name\": \"MLP System User\"," + "    \"active\": true" + "  }" + "]")));

		List<User> users = userService.getAllUser();
		assertNotNull(users);
		assertEquals(1, users.size());
	}

	@Test
	public void findUserByEmailTest() {

		stubFor(get(urlEqualTo("/ccds/user/search?email=test@gmail.com&_j=a&page=0&size=10"))
				.willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody(restUserResponse).withStatus(HttpStatus.SC_OK)));

		MLPUser users = userService.findUserByEmail("test@gmail.com");
		assertNotNull(users);
		assertEquals("c50e75c6-85c1-4b0f-8617-cc1035a3d430", users.getUserId());
	}

	@Test
	public void changePasswordTest() {

		stubFor(get(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(userObj)));

		/// ccds/user/login
		stubFor(post(urlEqualTo("/ccds/user/login")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(userObj)));

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430/chgpw")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		boolean passwordChanged = false;
		try {
			passwordChanged = userService.changeUserPassword("c50e75c6-85c1-4b0f-8617-cc1035a3d430", "test1234",
					"test3459");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(passwordChanged);
	}

	@Test
	public void forgotPasswordTest() {

		MLPUser user = new MLPUser();
		user.setFirstName("Test");
		user.setLastName("User");
		user.setEmail("test@gmail.com");
		user.setLoginName("test");
		user.setUserId("c50e75c6-85c1-4b0f-8617-cc1035a3d430");

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		// Forget password service does not return any thing. So cannot apply any checks
		// other than throwing exception
		try {
			userService.forgetPassword(user);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void generatePasswordTest() {

		MLPUser user = new MLPUser();
		user.setFirstName("Test");
		user.setLastName("User");
		user.setEmail("test@gmail.com");
		user.setLoginName("test");
		user.setUserId("c50e75c6-85c1-4b0f-8617-cc1035a3d430");

		stubFor(put(urlEqualTo("/ccds/user/c50e75c6-85c1-4b0f-8617-cc1035a3d430")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		// Generate password service does not return any thing. So cannot apply any
		// checks other than throwing exception
		try {
			userService.generatePassword(user);
		} catch (Exception e) {
			Assert.fail();
		}
	}
}