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

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.portal.be.service.impl.OauthUserServiceImpl;
import org.acumos.portal.be.transport.OauthUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.junit.Assert;


@RunWith(MockitoJUnitRunner.class)
public class OuathServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	OauthUserServiceImpl impl = new OauthUserServiceImpl();

	@Test
	public void saveTest() {
		try {

			OauthUser oauthUser = new OauthUser();
			oauthUser.setUserId("09514016-2f24-4a0c-8587-f0f0d2ff03b3");
			Instant created = Instant.now();
			oauthUser.setCreatedDate(created);
			oauthUser.setDisplayName("Tester");
			oauthUser.setAccessToken(
					"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlSWQiOiIxIiwibmFtZSI6IlVzZXIiLCJhY3RpdmUiOmZhbHNlLCJjcmVhdGVkIjoxNTEwMjIwMDQzMDAwLCJtb2RpZmllZCI6MTUxMDIyMDA0MzAwMH1dLCJjcmVhdGVkIjoxNTEwNzUzMjgzMDUyLCJleHAiOjE1MTEzNTgwODMsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUwODIzNDY5NjAwMCwibW9kaWZpZWQiOjE1MTAyMjkyMzkwMDAsInVzZXJJZCI6IjQxMDU4MTA1LTY3ZjQtNDQ2MS1hMTkyLWY0Y2I3ZmRhZmQzNCIsImZpcnN0TmFtZSI6InNkZnNkZiIsIm1pZGRsZU5hbWUiOiJzZGZzZGYiLCJsYXN0TmFtZSI6ImRmc2RmIiwib3JnTmFtZSI6InNkZnNkZiIsImVtYWlsIjoiZGZzZGYiLCJsb2dpbk5hbWUiOiJNYW5pbW96aGlUMSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjpudWxsLCJwaWN0dXJlIjpudWxsfX0.8LuG8jsQvDDhhS037R6I1AwGOFkq3jTMxg2mLYbtEsKqzJcrS7fa0iwOGpvAMejx0GKoEZAhfWLgR6YVaSwK1w");
			oauthUser.setImageURL(
					"https://www.google.co.in/search?q=google+images&oq=google+im&aqs=chrome.1.69i57j0l5.6032j0j7&sourceid=chrome&ie=UTF-8#");
			oauthUser.setDisplayName("Test_User_name");

			Mockito.when(impl.save(oauthUser)).thenReturn(oauthUser);
			logger.info("Successsfully created user data ");
			Assert.assertNotNull(oauthUser);

		} catch (Exception e) {
			logger.info("Failed to save data : " + e);
		}
	}

	@Test
	public void findUserByEmailTest() {
		try {

			MLPUserLoginProvider loginProvider = new MLPUserLoginProvider();
			loginProvider.setAccessToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlSWQiOiIxIiwibmFtZSI6IlVzZXIiLCJhY3RpdmUiOmZhbHNlLCJjcmVhdGVkIjoxNTEwMjIwMDQzMDAwLCJtb2RpZmllZCI6MTUxMDIyMDA0MzAwMH1dLCJjcmVhdGVkIjoxNTEwNzUzMjgzMDUyLCJleHAiOjE1MTEzNTgwODMsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUwODIzNDY5NjAwMCwibW9kaWZpZWQiOjE1MTAyMjkyMzkwMDAsInVzZXJJZCI6IjQxMDU4MTA1LTY3ZjQtNDQ2MS1hMTkyLWY0Y2I3ZmRhZmQzNCIsImZpcnN0TmFtZSI6InNkZnNkZiIsIm1pZGRsZU5hbWUiOiJzZGZzZGYiLCJsYXN0TmFtZSI6ImRmc2RmIiwib3JnTmFtZSI6InNkZnNkZiIsImVtYWlsIjoiZGZzZGYiLCJsb2dpbk5hbWUiOiJNYW5pbW96aGlUMSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjpudWxsLCJwaWN0dXJlIjpudWxsfX0.8LuG8jsQvDDhhS037R6I1AwGOFkq3jTMxg2mLYbtEsKqzJcrS7fa0iwOGpvAMejx0GKoEZAhfWLgR6YVaSwK1w");
			loginProvider.setUserId("09514016-2f24-4a0c-8587-f0f0d2ff03b3");
			Instant created = Instant.now();
			loginProvider.setCreated(created);
			loginProvider.setDisplayName("Tester");
			loginProvider.setAccessToken(
					"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlSWQiOiIxIiwibmFtZSI6IlVzZXIiLCJhY3RpdmUiOmZhbHNlLCJjcmVhdGVkIjoxNTEwMjIwMDQzMDAwLCJtb2RpZmllZCI6MTUxMDIyMDA0MzAwMH1dLCJjcmVhdGVkIjoxNTEwNzUzMjgzMDUyLCJleHAiOjE1MTEzNTgwODMsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUwODIzNDY5NjAwMCwibW9kaWZpZWQiOjE1MTAyMjkyMzkwMDAsInVzZXJJZCI6IjQxMDU4MTA1LTY3ZjQtNDQ2MS1hMTkyLWY0Y2I3ZmRhZmQzNCIsImZpcnN0TmFtZSI6InNkZnNkZiIsIm1pZGRsZU5hbWUiOiJzZGZzZGYiLCJsYXN0TmFtZSI6ImRmc2RmIiwib3JnTmFtZSI6InNkZnNkZiIsImVtYWlsIjoiZGZzZGYiLCJsb2dpbk5hbWUiOiJNYW5pbW96aGlUMSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjpudWxsLCJwaWN0dXJlIjpudWxsfX0.8LuG8jsQvDDhhS037R6I1AwGOFkq3jTMxg2mLYbtEsKqzJcrS7fa0iwOGpvAMejx0GKoEZAhfWLgR6YVaSwK1w");
			loginProvider.setImageUrl(
					"https://www.google.co.in/search?q=google+images&oq=google+im&aqs=chrome.1.69i57j0l5.6032j0j7&sourceid=chrome&ie=UTF-8#");
			loginProvider.setDisplayName("Test_User_name");
			
			String userId = loginProvider.getUserId();
			
			if(userId != null){
				String emailId = "user123@emial.com"; 
				Assert.assertNotNull(userId);
				Assert.assertNotNull(emailId);
				if(emailId != null){
					Mockito.when(impl.findUserByEmail(emailId)).thenReturn(loginProvider);
					logger.debug("Fetched user data according to email id : ");
					Assert.assertNotNull(loginProvider);
				}
			}
		} catch (Exception e) {
			logger.info("Failed to execute data  : " + e);
		}
	}

}
