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
 
package org.acumos.be.test.security.jwt;

import java.time.Instant;

import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtTokenUtilTest {

	private static Logger logger = LoggerFactory.getLogger(JwtTokenUtilTest.class);

	@Mock
	JwtTokenUtilTest test;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	

	@Test
	public void generateToken() {
		JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
		String jwtToken = null;
		try {
			MLPUser userDetails = new MLPUser();
			userDetails.setFirstName("John");
			userDetails.setLastName("Poll");
			userDetails.setLoginName("john");
			userDetails.setLoginHash("!Acumos@73825");
			userDetails.setEmail("john@techmahindra.com");
			userDetails.setUserId("9d1f8220-3aba-4371-8c1a-abc229930652");
			userDetails.setActive(true);
			userDetails.setCreated(Instant.now());
			userDetails.setModified(Instant.now());

			jwtToken = jwtTokenUtil.generateToken(userDetails, null);
			logger.info("JWT Token :    " + jwtToken);

			Boolean isTolenVallid = jwtTokenUtil.validateToken(jwtToken, userDetails);
			logger.info("isTolenVallid :    " + isTolenVallid);

			Boolean isTokenExpired = jwtTokenUtil.isTokenExpired(jwtToken);
			logger.info("isTokenExpired :    " + isTokenExpired);

			String userName = jwtTokenUtil.getUsernameFromToken(jwtToken);
			logger.info("userName from token :    " + userName);
			
			Assert.assertNotNull(userName);
			Assert.assertNotNull(jwtToken);
			Assert.assertNotNull(isTolenVallid);
			Assert.assertNotNull(isTokenExpired);
			

		} catch (Exception e) {
			logger.info("Exception occured while execute testCase  :generateToken " + e);
		}

	}
}
